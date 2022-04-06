package newbank.server.loan;

import newbank.server.Customer;
import newbank.server.NewBank;

// An object of this class records a loan offer which a customer is willing to lend.
public class AvailableLoan implements Comparable<AvailableLoan> {
  private static long maxAvailableLoanID = 0;
  private final long availableLoanID;
  private final String lenderUserID;
  private double amount;
  private boolean stillAvailable;

  public AvailableLoan(String lenderUserID, double amount) {
    availableLoanID = ++maxAvailableLoanID;
    this.lenderUserID = lenderUserID;
    this.amount = amount;
    stillAvailable = true;
  }
  public AvailableLoan(long availableLoanID, String lenderUserID, double amount, boolean stillAvailable) {
      this.availableLoanID = availableLoanID;
      this.lenderUserID = lenderUserID;
      this.amount = amount;
      this.stillAvailable = stillAvailable;
  }

  public void getTotalNumberOfAvailableLoanFromDatabase() {
    maxAvailableLoanID = 0; // should be got from database
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

  public static void setMaxAvailableLoanID(long maxAvailableLoanID) {
    AvailableLoan.maxAvailableLoanID = maxAvailableLoanID;
  }

  public boolean lend(String borrowerUserID, double loanAmount) {
    if (NewBank.getBank().getCustomer(borrowerUserID) != null && loanAmount > 0 && amount >= loanAmount) {
      amount -= loanAmount;
      if (amount <= 0) {
        stillAvailable = false;
      }
      Loan loan = new Loan(lenderUserID, borrowerUserID, loanAmount * (1 + LoanMarketplace.getInterestPerLoan()));
      NewBank.getLoanMarketplace().addLoan(loan);
      return true;
    }
    return false;
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

  @Override
  public int compareTo(AvailableLoan o) {
    if (availableLoanID == o.availableLoanID) return 0;
    return availableLoanID < o.availableLoanID ? -1 : 1;
  }
}
