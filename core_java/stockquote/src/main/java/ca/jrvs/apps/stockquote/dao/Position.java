package ca.jrvs.apps.stockquote.dao;

/**
 * Domain model for the position table.
 * This class represents one row in the position table.
 */
public class Position {

  private String ticker; // id (symbol)
  private int numOfShares;
  private double valuePaid; // total amount paid for shares

  public String getTicker() {
    return ticker;
  }

  public void setTicker(String ticker) {
    this.ticker = ticker;
  }

  public int getNumOfShares() {
    return numOfShares;
  }

  public void setNumOfShares(int numOfShares) {
    this.numOfShares = numOfShares;
  }

  public double getValuePaid() {
    return valuePaid;
  }

  public void setValuePaid(double valuePaid) {
    this.valuePaid = valuePaid;
  }
}
