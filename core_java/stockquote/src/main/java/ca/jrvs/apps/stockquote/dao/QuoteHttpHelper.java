package ca.jrvs.apps.stockquote.dao;

import ca.jrvs.apps.stockquote.dto.GlobalQuote;
import ca.jrvs.apps.stockquote.dto.GlobalQuoteResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuoteHttpHelper {
  private static final Logger logger = LoggerFactory.getLogger(QuoteHttpHelper.class);
  private final String apikey;
  private final OkHttpClient client;
  private final ObjectMapper objectMapper;

  public QuoteHttpHelper(String apikey) {
    this.apikey = apikey;
    this.client = new OkHttpClient();
    this.objectMapper = new ObjectMapper();
  }

  public Quote fetchQuoteInfo(String symbol) throws IllegalArgumentException {

    // 1. validate symbol

    if (symbol == null || symbol.isBlank()) {
      throw new IllegalArgumentException("symbol is null or empty");
    }

    // 2. validate api key

    if (apikey == null || apikey.isBlank()) {
      throw new IllegalArgumentException("apikey is null or empty");
    }

    // 3. build request url

    HttpUrl url = HttpUrl.parse("https://alpha-vantage.p.rapidapi.com/query")
        .newBuilder()
        .addQueryParameter("function", "GLOBAL_QUOTE")
        .addQueryParameter("symbol", symbol)
        .addQueryParameter("datatype","json")
        .build();

    Request request = new Request.Builder()
        .url(url)
        .addHeader("X-RapidAPI-Key", apikey)
        .addHeader("X-RapidAPI-Host", "alpha-vantage.p.rapidapi.com")
        .get()
        .build();

    // 4. execute http request

    logger.info("Sending request to Alpha Vantage for symbol={}", symbol);
    try (Response response = client.newCall(request).execute()){
      int statusCode = response.code();
      logger.info("Received HTTP status code {}", statusCode);

      String bodyStr = (response.body() == null) ? "" : response.body().string();

      if (!response.isSuccessful()) {
        logger.error("Unexpected HTTP status code {} for symbol={}. Body={}",
            statusCode, symbol, bodyStr);
        throw new IllegalArgumentException("Received non-200 response from Alpha Vantage: " + statusCode);
      }

      if (bodyStr.isBlank()){
        throw new IllegalArgumentException("Empty response body for symbol: " + symbol);
      }

      // 5. parse json response
      GlobalQuoteResponse dto =objectMapper.readValue(bodyStr, GlobalQuoteResponse.class);
      GlobalQuote globalQuote = (dto ==null) ? null : dto.getGlobalQuote();

      if (globalQuote == null || globalQuote.getSymbol() == null || globalQuote.getSymbol().isBlank()) {
        throw new IllegalArgumentException("Empty response body for symbol: " + symbol);
      }

      Quote quote = new Quote();
      quote.setTicker(globalQuote.getSymbol());
      quote.setPrice(Double.parseDouble(globalQuote.getPrice()));
      quote.setOpen(Double.parseDouble(globalQuote.getOpen()));
      quote.setHigh(Double.parseDouble(globalQuote.getHigh()));
      quote.setLow(Double.parseDouble(globalQuote.getLow()));
      quote.setVolume(Integer.parseInt(globalQuote.getVolume()));

      LocalDate day = LocalDate.parse(globalQuote.getLatestTradingDay());
      quote.setLatestTradingDay(java.sql.Date.valueOf(day));

      quote.setPreviousClose(Double.parseDouble(globalQuote.getPreviousClose()));
      quote.setChange(Double.parseDouble(globalQuote.getChange()));
      quote.setChangePercent(globalQuote.getChangePercent());

      quote.setTimestamp(new Timestamp(System.currentTimeMillis()));
      return quote;
    }catch (IOException e){
      throw new IllegalArgumentException("Error parsing JSON response from Alpha Vantage: " + symbol, e);
    }

    // 6. convert DTO to Quote
    // 7. return Quote

    // temporary
  }

}
