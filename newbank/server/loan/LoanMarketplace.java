package newbank.server.loan;

import java.util.HashMap;

public class LoanMarketplace {

  private static double interestPerLoan = 0.05;
  private HashMap<Long, AvailableLoan> availableLoans;
  private HashMap<Long, Loan> loans;

  public LoanMarketplace() {
    availableLoans = new HashMap<>();
    loans = new HashMap<>();
  }

  public static double getInterestPerLoan() {
    return interestPerLoan;
  }

  public void putAvailableLoan(AvailableLoan availableLoan) {
    availableLoans.put(availableLoan.getAvailableLoanID(), availableLoan);
  }

  public void putLoan(Loan loan) {
    loans.put(loan.getLoanID(), loan);
  }

}
