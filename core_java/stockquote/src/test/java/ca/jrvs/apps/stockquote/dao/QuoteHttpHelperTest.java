package ca.jrvs.apps.stockquote.dao;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class QuoteHttpHelperTest {

  @Test
  void fetchQuoteInfo_shouldReturnQuote() {
    String apiKey = System.getenv("ALPHAVANTAGE_API_KEY");
    assertNotNull(apiKey);
    assertFalse(apiKey.isBlank());

    QuoteHttpHelper helper = new QuoteHttpHelper(apiKey);
    Quote quote = helper.fetchQuoteInfo("MSFT");

    assertNotNull(quote);

    assertNotNull("MSFT", quote.getTicker());
    assertFalse(quote.getTicker().isBlank());
    assertTrue(quote.getPrice() > 0);
    assertTrue(quote.getOpen() > 0);
    assertTrue(quote.getHigh() >= quote.getLow());
    assertTrue(quote.getVolume() > 0);

    assertNotNull(quote.getLatestTradingDay());
    assertTrue(quote.getPreviousClose() > 0);

    assertNotNull(quote.getChangePercent());
    assertNotNull(quote.getTimestamp());
  }
}
