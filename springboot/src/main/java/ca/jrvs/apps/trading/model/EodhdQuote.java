package ca.jrvs.apps.trading.model;

/**
 * Represents the quote object under:
 *   response.data["AAPL.US"]
 *
 * Field names match EODHD JSON keys so Jackson can map automatically.
 */
public class EodhdQuote {

  private String symbol;

  private double bidPrice;
  private int bidSize;

  private double askPrice;
  private int askSize;

  private double lastTradePrice;
  private long lastTradeTime;

  private long timestamp;

  // --- Getters/Setters ---

  public String getSymbol() {
    return symbol;
  }

  public void setSymbol(String symbol) {
    this.symbol = symbol;
  }

  public double getBidPrice() {
    return bidPrice;
  }

  public void setBidPrice(double bidPrice) {
    this.bidPrice = bidPrice;
  }

  public int getBidSize() {
    return bidSize;
  }

  public void setBidSize(int bidSize) {
    this.bidSize = bidSize;
  }

  public double getAskPrice() {
    return askPrice;
  }

  public void setAskPrice(double askPrice) {
    this.askPrice = askPrice;
  }

  public int getAskSize() {
    return askSize;
  }

  public void setAskSize(int askSize) {
    this.askSize = askSize;
  }

  public double getLastTradePrice() {
    return lastTradePrice;
  }

  public void setLastTradePrice(double lastTradePrice) {
    this.lastTradePrice = lastTradePrice;
  }

  public long getLastTradeTime() {
    return lastTradeTime;
  }

  public void setLastTradeTime(long lastTradeTime) {
    this.lastTradeTime = lastTradeTime;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }
}
