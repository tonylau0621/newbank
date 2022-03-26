package newbank.server.loan;

import newbank.server.Customer;
import newbank.server.NewBank;

// first-in-first-lend
public class AvailableLoan {
  private static long totalNumberOfAvailableLoan = 0;
  private final long availableLoanID; // lower value, sooner to be lent
  private final String lenderUserID;
  private double amount;
  private boolean stillAvailable;

  public AvailableLoan(String lenderUserID, double amount) {
    availableLoanID = ++totalNumberOfAvailableLoan;
    this.lenderUserID = lenderUserID;
    this.amount = amount;
    stillAvailable = true;
  }

  public void getTotalNumberOfAvailableLoanFromDatabase() {
    totalNumberOfAvailableLoan = 0; // should be got from database
  }

  public long getAvailableLoanID() {
    return availableLoanID;
  }

  public String getLenderUserID() {
    return lenderUserID;
  }

  public double getAmount() {
    return amount;
  }

  public boolean isStillAvailable() {
    return stillAvailable;
  }

  public synchronized Loan lend(String borrowerUserID, double loanAmount) {
    if (NewBank.getBank().getCustomer(borrowerUserID) != null && loanAmount > 0 && amount >= loanAmount) {
      amount -= loanAmount;
      if (amount <= 0) {
        stillAvailable = false;
      }
      Loan loan = new Loan(lenderUserID, borrowerUserID, loanAmount * (1 + LoanMarketplace.getInterestPerLoan()));
      NewBank.getBank().getCustomer(lenderUserID).addLentLoan(loan);
      NewBank.getBank().getCustomer(borrowerUserID).addBorrowedLoan(loan);
      NewBank.getLoanMarketplace().putLoan(loan);
      return loan;
    }
    return null;
  }

  public boolean transferToAccount(Customer customer, String accountName, double transferAmount) {
    if (customer.getUserID().equals(lenderUserID) && customer.getAccount(accountName) != null && transferAmount > 0 && transferAmount <= amount) {
      customer.getAccount(accountName).updateBalance(transferAmount);
      amount -= transferAmount;
      if (amount <= 0) {
        stillAvailable = false;
      }
      return true;
    }
    return false;
  }


}
