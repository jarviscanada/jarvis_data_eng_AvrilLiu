package ca.jrvs.apps.trading.model;

import java.util.Map;

/**
 * Wrapper for EODHD us-quote-delayed response.
 * The actual quote is stored in data[symbol].
 */
public class EodhdQuoteResponse {

  private Map<String, EodhdQuote> data;

  public Map<String, EodhdQuote> getData() {
    return data;
  }

  public void setData(Map<String, EodhdQuote> data) {
    this.data = data;
  }
}
