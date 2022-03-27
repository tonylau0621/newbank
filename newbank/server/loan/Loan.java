package newbank.server.loan;

import newbank.server.Customer;
import newbank.server.NewBank;

public class Loan implements Comparable<Loan> {
  private static long maxLoanID = 0;
  private final long loanID;
  private final String lenderUserID;
  private final String borrowerUserID;
  private final double loanAmount;
  private double remainingAmount;
  private boolean allRepaid;

  public Loan(String lenderUserID, String borrowerUserID, double loanAmount) {
    loanID = ++maxLoanID;
    this.lenderUserID = lenderUserID;
    this.borrowerUserID = borrowerUserID;
    this.loanAmount = loanAmount;
    remainingAmount = loanAmount;
    allRepaid = false;
  }

  public void getTotalNumberOfLoanFromDatabase() {
    maxLoanID = 0; // should be got from database
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

  // After the borrower repay the loan, update the loan fields and the repaid amount will become a new AvailableLoan object of the lender.
  public boolean repayLoan(Customer customer, String accountName, double repayAmount) {
    if (customer.getUserID().equals(borrowerUserID) && customer.getAccount(accountName) != null && repayAmount > 0 && customer.getAccount(accountName).getAmount() >= repayAmount) {
      double realRepayAmount = repayAmount < remainingAmount ? repayAmount : remainingAmount;
      customer.getAccount(accountName).updateBalance(-realRepayAmount);
      remainingAmount -= realRepayAmount;
      if (remainingAmount <= 0) {
        allRepaid = true;
      }
      AvailableLoan availableLoan = new AvailableLoan(lenderUserID, realRepayAmount);
      NewBank.getBank().getLoanMarketplace().addAvailableLoan(availableLoan);
      return true;
    }
    return false;
  }

  @Override
  public int compareTo(Loan o) {
    if (loanID == o.loanID) return 0;
    return loanID < o.loanID ? -1 : 1;
  }
}
