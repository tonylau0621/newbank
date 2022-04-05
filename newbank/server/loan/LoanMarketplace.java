package newbank.server.loan;

import newbank.server.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class LoanMarketplace {

  private static double interestPerLoan = 0.05;
  private static double loanLimit = 3000.0;
  private ArrayList<AvailableLoan> availableLoans;
  private ArrayList<Loan> loans;

  public LoanMarketplace() {
    availableLoans = new ArrayList<>();
    loans = new ArrayList<>();
  }

  public static double getInterestPerLoan() {
    return interestPerLoan;
  }

  public static double getLoanLimit() {
    return loanLimit;
  }

  public double getTotalAvailableLoanAmount() {
    double total = 0;
    for (int i = 0; i < availableLoans.size(); i++) {
      total += availableLoans.get(i).getAmount();
    }
    return total;
  }

  // Calculate total available loan amount except the borrower's
  public double getTotalAvailableLoanAmount(String borrowerUserID) {
    Customer borrower = NewBank.getBank().getCustomer(borrowerUserID);
    double total = getTotalAvailableLoanAmount();
    if (borrower != null) {
      total -= borrower.getTotalAvailableLoans();
    }
    return total;
  }

  public void addAvailableLoan(AvailableLoan availableLoan) {
    availableLoans.add(availableLoan);
    Collections.sort(availableLoans);
    Customer customer = NewBank.getBank().getCustomer(availableLoan.getLenderUserID());
    customer.addAvailableLoan(availableLoan);
  }

  public void addLoan(Loan loan) {
    loans.add(loan);
    Collections.sort(loans);
    Customer lender = NewBank.getBank().getCustomer(loan.getLenderUserID());
    Customer borrower = NewBank.getBank().getCustomer(loan.getBorrowerUserID());
    lender.addLentLoan(loan);
    borrower.addBorrowedLoan(loan);
  }

  // If a customer is willing to lend an amount, call this method to generate an AvailableLoan object.
  public synchronized boolean offerLoan(String userID, String accountName, double amount) throws InvalidAccountException, InvalidAmountException, InsufficientBalanceException, IOException {
    NewBank bank = NewBank.getBank();
    Customer customer = bank.getCustomer(userID);
    if (customer != null) {
      Account account = customer.getAccount(accountName);
      if (account == null) throw new InvalidAccountException();
      if (amount < 0.01) throw new InvalidAmountException();
      if (account.getAmount() < amount) throw new InsufficientBalanceException();
      account.updateBalance(-amount);
      AvailableLoan availableLoan = new AvailableLoan(customer.getUserID(), amount);
      addAvailableLoan(availableLoan);

      // Update database
      bank.addTransaction(new Transaction(account.getID(), userID+"-a", amount, "Transfer"));
      DataHandler.updateAvailableLoanCSV(availableLoans);
      DataHandler.updateAccountCSV(bank.getCustomers());
      return true;
    }
    return false;
  }

  public synchronized boolean transferLendingAccountToOtherAccount(String userID, String accountName, double amount) throws InvalidAccountException, InvalidAmountException, InsufficientBalanceException, IOException {
    NewBank bank = NewBank.getBank();
    Customer customer = bank.getCustomer(userID);
    if (customer != null) {
      Account account = customer.getAccount(accountName);
      double totalAvailableLoans = customer.getTotalAvailableLoans();
      if (account == null) throw new InvalidAccountException();
      if (amount < 0.01) throw new InvalidAmountException();
      if (totalAvailableLoans < amount) throw new InsufficientBalanceException();

      ArrayList<AvailableLoan> availableLoans = customer.getAvailableLoans();
      double remaining = amount;

      for (int i = availableLoans.size() - 1; i >= 0 ; i--) {
        AvailableLoan lastAvailableLoan = availableLoans.get(i);
        if (!lastAvailableLoan.isStillAvailable()) continue;
        if (remaining <= lastAvailableLoan.getAmount()) {
          lastAvailableLoan.transferToAccount(customer, accountName, remaining);
          remaining = 0;
          break;
        } else { // for the last AvailableLoan object cannot fulfil the (remaining) amount
          remaining -= lastAvailableLoan.getAmount();
          lastAvailableLoan.transferToAccount(customer, accountName, lastAvailableLoan.getAmount());
        }
      }

      // Update database
      bank.addTransaction(new Transaction(userID+"-a", account.getID(), amount, "Transfer"));
      DataHandler.updateAvailableLoanCSV(availableLoans);
      DataHandler.updateAccountCSV(bank.getCustomers());
      return true;
    }
    return false;
  }

  // If a customer borrows money, call this method to generate Loan object(s) according to the order of AvailableLoan object(s)
  public synchronized boolean processLoanRequest(String borrowerUserID, String receivedAccountName, double amount) throws InvalidAccountException, InvalidAmountException, IOException {
    NewBank bank = NewBank.getBank();
    Customer borrower = bank.getCustomer(borrowerUserID);
    double totalAvailableAmount = getTotalAvailableLoanAmount(borrowerUserID);
    if (borrower != null) {
      Account account = borrower.getAccount(receivedAccountName);
      if (account == null) throw new InvalidAccountException();
      if (amount < 0.01 || amount > totalAvailableAmount || amount > 3000) throw new InvalidAmountException();
      double remaining = amount;

      // For the available loans lent by customers, first-in-first-lend
      // The lender of an availableLoan cannot be the borrower of it
      for (int i = 0; i < availableLoans.size(); i++) {
        AvailableLoan firstAvailableLoan = availableLoans.get(i);
        if (!firstAvailableLoan.isStillAvailable() || firstAvailableLoan.getLenderUserID().equals(borrowerUserID)) continue;
        if (remaining <= firstAvailableLoan.getAmount()) {
          firstAvailableLoan.lend(borrower.getUserID(), remaining);
          // Update the lender's transaction (and database)
          bank.addTransaction(new Transaction(firstAvailableLoan.getLenderUserID()+"-a", "99999999-1", remaining, "Lending"));
          remaining = 0;
          break;
        } else { // for the first AvailableLoan object cannot fulfil the (remaining) loan amount
          remaining -= firstAvailableLoan.getAmount();
          // Update the lender's transaction (and database)
          bank.addTransaction(new Transaction(firstAvailableLoan.getLenderUserID()+"-a", "99999999-1", firstAvailableLoan.getAmount(), "Lending"));
          firstAvailableLoan.lend(borrower.getUserID(), firstAvailableLoan.getAmount());
        }
      }
      account.updateBalance(amount);

      // Update the borrower's transaction (and database)
      bank.addTransaction(new Transaction("99999999-1", account.getID(), amount, "Borrowing"));

      // Update database
      DataHandler.updateAvailableLoanCSV(availableLoans);
      DataHandler.updateLoanCSV(loans);
      DataHandler.updateAccountCSV(bank.getCustomers());

      return true;
    }
    return false;
  }

  // After a borrower repay the loan, update the corresponding Loan objects and new AvailableLoan object(s) of the lender(s) is/are generated.
  public synchronized boolean repayLoan(String borrowerUserID, String paidAccountName, double amount) throws InvalidAccountException, InvalidAmountException, InsufficientBalanceException, IOException {
    NewBank bank = NewBank.getBank();
    Customer borrower = bank.getCustomer(borrowerUserID);
    if (borrower != null) {
      Account account = borrower.getAccount(paidAccountName);
      if (account == null) throw new InvalidAccountException();
      if (amount < 0.01) throw new InvalidAmountException();
      if (account.getAmount() < amount) throw new InsufficientBalanceException();

      ArrayList<Loan> borrowedLoans = borrower.getBorrowedLoans();
      double remaining = amount;
      for (int i = 0; i < borrowedLoans.size(); i++) {
        Loan firstBorrowedLoans = borrowedLoans.get(i);
        if (remaining > firstBorrowedLoans.getRemainingAmount()) {
          remaining -= firstBorrowedLoans.getRemainingAmount();
          firstBorrowedLoans.repayLoan(borrower, paidAccountName, firstBorrowedLoans.getRemainingAmount());
          // Update the lender's transaction (and database)
          bank.addTransaction(new Transaction("99999999-1", firstBorrowedLoans.getLenderUserID()+"-a", firstBorrowedLoans.getRemainingAmount(), "Debt Collection"));
        } else {
          firstBorrowedLoans.repayLoan(borrower, paidAccountName, remaining);
          // Update the lender's transaction (and database)
          bank.addTransaction(new Transaction("99999999-1", firstBorrowedLoans.getLenderUserID()+"-a", remaining, "Debt Collection"));
          remaining = 0;
          break;
        }
      }

      // Update the borrower's transaction (and database)
      bank.addTransaction(new Transaction(account.getID(), "99999999-1", amount, "Repayment"));

      // Update database
      DataHandler.updateAvailableLoanCSV(availableLoans);
      DataHandler.updateLoanCSV(loans);
      DataHandler.updateAccountCSV(bank.getCustomers());
      return true;
    }
    return false;
  }

}
