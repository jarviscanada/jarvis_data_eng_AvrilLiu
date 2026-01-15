package ca.jrvs.apps.stockquote.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GlobalQuote {

  @JsonProperty("01. symbol")
  private String symbol;

  @JsonProperty("02. open")
  private String open;

  @JsonProperty("03. high")
  private String high;

  @JsonProperty("04. low")
  private String low;

  @JsonProperty("05. price")
  private String price;

  @JsonProperty("06. volume")
  private String volume;

  @JsonProperty("07. latest trading day")
  private String latestTradingDay;

  @JsonProperty("08. previous close")
  private String previousClose;

  @JsonProperty("09. change")
  private String change;

  @JsonProperty("10. change percent")
  private String changePercent;

  public String getSymbol() {
    return symbol;
  }

  public void setSymbol(String symbol) {
    this.symbol = symbol;
  }

  public String getOpen() {
    return open;
  }

  public void setOpen(String open) {
    this.open = open;
  }

  public String getHigh() {
    return high;
  }

  public void setHigh(String high) {
    this.high = high;
  }

  public String getLow() {
    return low;
  }

  public void setLow(String low) {
    this.low = low;
  }

  public String getPrice() {
    return price;
  }

  public void setPrice(String price) {
    this.price = price;
  }

  public String getVolume() {
    return volume;
  }

  public void setVolume(String volume) {
    this.volume = volume;
  }

  public String getLatestTradingDay() {
    return latestTradingDay;
  }

  public void setLatestTradingDay(String latestTradingDay) {
    this.latestTradingDay = latestTradingDay;
  }

  public String getPreviousClose() {
    return previousClose;
  }

  public void setPreviousClose(String previousClose) {
    this.previousClose = previousClose;
  }

  public String getChange() {
    return change;
  }

  public void setChange(String change) {
    this.change = change;
  }

  public String getChangePercent() {
    return changePercent;
  }

  public void setChangePercent(String changePercent) {
    this.changePercent = changePercent;
  }

  @Override
  public String toString() {
    return "GlobalQuote{" +
        "symbol='" + symbol + '\'' +
        ", price='" + price + '\'' +
        ", change='" + change + '\'' +
        ", changePercent='" + changePercent + '\'' +
        '}';
  }
}
