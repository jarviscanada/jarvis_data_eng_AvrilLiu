package ca.jrvs.apps.trading.dao;

import ca.jrvs.apps.trading.model.EodhdQuote;
import ca.jrvs.apps.trading.model.EodhdQuoteResponse;
import ca.jrvs.apps.trading.model.config.MarketDataConfig;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Repository
public class EodhdMarketDataDao {


  private final RestTemplate restTemplate;
  private final MarketDataConfig marketDataConfig;

  public EodhdMarketDataDao(RestTemplate restTemplate, MarketDataConfig marketDataConfig) {
    this.restTemplate = restTemplate;
    this.marketDataConfig = marketDataConfig;
  }

  /**
   * Query a real-time quote from EODHD.
   *
   * @param ticker e.g. "AAPL.US"
   * @return deserialized EodhdQuote object
   */

  public EodhdQuote findQuoteByTicker(String ticker) {
    if (ticker == null || ticker.trim().isEmpty()) {
      throw new IllegalArgumentException("Ticker cannot be null or empty");
    }

    String normalizedTicker = ticker.trim().toUpperCase();

    // Build URL: baseUrl + ?s={ticker}&api_token=...&fmt=json
    String url = UriComponentsBuilder
        .fromHttpUrl(marketDataConfig.getBaseUrl())
        .queryParam("s", normalizedTicker)
        .queryParam("api_token", marketDataConfig.getToken())
        .queryParam("fmt", "json")
        .toUriString();

    try {
      // Deserialize the outer wrapper first
      EodhdQuoteResponse resp = restTemplate.getForObject(url, EodhdQuoteResponse.class);

      // Defensive checks
      if (resp == null || resp.getData() == null) {
        throw new IllegalStateException(
            "EODHD returned empty response wrapper for ticker=" + normalizedTicker);
      }

      EodhdQuote quote = resp.getData().get(normalizedTicker);
      if (quote == null) {
        // Sometimes the key might not match normalization; if needed you can try resp.getData().values().stream().findFirst()
        throw new IllegalStateException(
            "EODHD returned no quote data for ticker=" + normalizedTicker);
      }

      return quote;

    } catch (RestClientException e) {
      // Covers connection issues, timeouts, invalid responses, etc.
      throw new IllegalStateException(
          "Failed to call EODHD API for ticker=" + normalizedTicker + ": " + e.getMessage(), e);
    }
  }

}
