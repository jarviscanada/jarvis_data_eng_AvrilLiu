package ca.jrvs.apps.stockquote.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GlobalQuoteResponse {

  @JsonProperty("Global Quote")
  private GlobalQuote globalQuote;

  public GlobalQuote getGlobalQuote() {
    return globalQuote;
  }

  public void setGlobalQuote(GlobalQuote globalQuote) {
    this.globalQuote = globalQuote;
  }

  @Override
  public String toString() {
    return "GlobalQuoteResponse{" +
        "globalQuote=" + globalQuote +
        '}';
  }
}
