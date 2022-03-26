package newbank.server.loan;

import newbank.server.Customer;
import newbank.server.NewBank;

public class Loan {
  private static long totalNumberOfLoan = 0;
  private final long loanID;
  private final String lenderUserID;
  private final String borrowerUserID;
  private final double loanAmount;
  private double remainingAmount;
  private boolean allRepaid;

  public Loan(String lenderUserID, String borrowerUserID, double loanAmount) {
    loanID = ++totalNumberOfLoan;
    this.lenderUserID = lenderUserID;
    this.borrowerUserID = borrowerUserID;
    this.loanAmount = loanAmount;
    remainingAmount = loanAmount;
    allRepaid = false;
  }

  public void getTotalNumberOfLoanFromDatabase() {
    totalNumberOfLoan = 0; // should be got from database
  }

  public long getLoanID() {
    return loanID;
  }

  public String getLenderUserID() {
    return lenderUserID;
  }

  public String getBorrowerUserID() {
    return borrowerUserID;
  }

  public double getLoanAmount() {
    return loanAmount;
  }

  public double getRemainingAmount() {
    return remainingAmount;
  }

  public boolean isAllRepaid() {
    return allRepaid;
  }

  public AvailableLoan repayLoan(Customer customer, String accountName, double repayAmount) {
    if (customer.getUserID().equals(borrowerUserID) && customer.getAccount(accountName) != null && repayAmount > 0 && customer.getAccount(accountName).getAmount() >= repayAmount) {
      double realRepayAmount = repayAmount < remainingAmount ? repayAmount : remainingAmount;
      customer.getAccount(accountName).updateBalance(-realRepayAmount);
      remainingAmount -= realRepayAmount;
      if (remainingAmount <= 0) {
        allRepaid = true;
      }
      AvailableLoan availableLoan = new AvailableLoan(lenderUserID, realRepayAmount);
      NewBank.getBank().getCustomer(lenderUserID).addAvailableLoan(availableLoan);
      return availableLoan;
    }
    return null;
  }


}
