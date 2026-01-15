package ca.jrvs.apps.stockquote;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlphaVantageClient {

  private static final Logger logger = LoggerFactory.getLogger(AlphaVantageClient.class);
  private static final String BASE_URL = "https://alpha-vantage.p.rapidapi.com/query";
  private static final String GLOBAL_QUOTE_FUNCTION = "GLOBAL_QUOTE";
  private final String apiKey;
  private final HttpClient httpClient;

  public AlphaVantageClient(String apiKey) {
    if (apiKey == null || apiKey.isBlank()) {
      throw new IllegalArgumentException("apiKey cannot be null or empty");
    }
    this.apiKey = apiKey;
    this.httpClient = HttpClient.newHttpClient();
  }

  /**
   * @param symbol
   * @return
   * @throws IOException
   * @throws InterruptedException
   */
  public String getGlobalQuote(String symbol) throws IOException, InterruptedException {
    if (symbol == null || symbol.isBlank()) {
      throw new IllegalArgumentException("symbol cannot be null or empty");
    }
    String uri = BASE_URL
        + "?function=" + GLOBAL_QUOTE_FUNCTION
        + "&symbol=" + symbol
        + "&datatype=json";
    logger.info("Sending request to Alpha Vantage for symbol={}", symbol);

    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(uri))
        .GET()
        .header("X-RapidAPI-Key", apiKey)
        .header("X-RapidAPI-Host", "alpha-vantage.p.rapidapi.com")
        .build();

    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

    int statusCode = response.statusCode();
    logger.info("Received HTTP status code {}", statusCode);

    if (statusCode != 200) {
      logger.error("Unexpected HTTP status code {} for symbol={}. Body={}", statusCode, symbol,
          response.body());
      throw new IOException("Received non-200 response from Alpha Vantage:" + statusCode);
    }

    String body = response.body();
    logger.info("Got response from Alpha Vantage: {}", body);
    return body;
  }
}
