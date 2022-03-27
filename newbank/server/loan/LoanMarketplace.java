package newbank.server.loan;

import newbank.server.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

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
  public boolean offerLoan(String userID, String accountName, double amount) throws InvalidAccountException, InvalidAmountException {
    Customer customer = NewBank.getBank().getCustomer(userID);
    if (customer != null) {
      Account account = customer.getAccount(accountName);
      if (account == null) throw new InvalidAccountException();
      if (amount < 0.01 || account.getAmount() < amount) throw new InvalidAmountException();
      account.updateBalance(-amount);
      AvailableLoan availableLoan = new AvailableLoan(customer.getUserID(), amount);
      addAvailableLoan(availableLoan);
      return true;
    }
    return false;
  }

  // Generate Loan object(s) according to the order of AvailableLoan object(s)
  public boolean processLoanRequest(String borrowerUserID, String receivedAccountName, double amount) throws InvalidAccountException, InvalidAmountException {
    Customer borrower = NewBank.getBank().getCustomer(borrowerUserID);
    if (borrower != null) {
      Account account = borrower.getAccount(receivedAccountName);
      if (account == null) throw new InvalidAccountException();
      if (amount < 0.01) throw new InvalidAmountException();
      double remaining = amount;

      for (int i = 0; i < availableLoans.size(); i++) {
        AvailableLoan firstAvailableLoan = availableLoans.get(i);
        if (!firstAvailableLoan.isStillAvailable()) continue;
        if (remaining <= firstAvailableLoan.getAmount()) {
          firstAvailableLoan.lend(borrower.getUserID(), remaining);
          remaining = 0;
          break;
        } else { // for the first AvailableLoan object cannot fulfil the (remaining) loan amount
          remaining -= firstAvailableLoan.getAmount();
          firstAvailableLoan.lend(borrower.getUserID(), firstAvailableLoan.getAmount());
        }
      }
      account.updateBalance(amount);
      return true;
    }
    return false;
  }

  // After a borrower repay the loan, update the corresponding Loan objects and new AvailableLoan object(s) of the lender(s) is/are generated.
  public boolean repayLoan(String borrowerUserID, String paidAccountName, double amount) throws InvalidAccountException, InvalidAmountException {
    Customer borrower = NewBank.getBank().getCustomer(borrowerUserID);
    if (borrower != null) {
      Account account = borrower.getAccount(paidAccountName);
      if (account == null) throw new InvalidAccountException();
      if (amount < 0.01 || account.getAmount() < amount) throw new InvalidAmountException();

      ArrayList<Loan> borrowedLoans = borrower.getBorrowedLoans();
      double remaining = amount;
      for (int i = 0; i < borrowedLoans.size(); i++) {
        Loan firstBorrowedLoans = borrowedLoans.get(i);
        if (remaining > firstBorrowedLoans.getRemainingAmount()) {
          remaining -= firstBorrowedLoans.getRemainingAmount();
          firstBorrowedLoans.repayLoan(borrower, paidAccountName, firstBorrowedLoans.getRemainingAmount());
        } else {
          firstBorrowedLoans.repayLoan(borrower, paidAccountName, remaining);
          remaining = 0;
          break;
        }
      }
      return true;
    }
    return false;
  }

}
