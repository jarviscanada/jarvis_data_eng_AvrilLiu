package ca.jrvs.apps.stockquote;

import ca.jrvs.apps.stockquote.dto.GlobalQuote;
import ca.jrvs.apps.stockquote.dto.GlobalQuoteResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StockQuoteApp {

  private static final Logger logger = LoggerFactory.getLogger(StockQuoteApp.class);

  public static void main(String[] args) {
    // Application entry point
    logger.info("Starting StockQuoteApp");

    try {
      // Use the first CLI argument as symbol, or default to MSFT
      String symbol = (args.length > 0) ? args[0] : "MSFT";

      //Read API key from environment variable
      String apikey = System.getenv("ALPHAVANTAGE_API_KEY");
      if (apikey == null || apikey.isBlank()) {
        logger.error("Environment variable ALPHAVANTAGE_API_KEY is not set");
        return;
      }

      AlphaVantageClient client = new AlphaVantageClient(apikey);
      String jsonResponse = client.getGlobalQuote(symbol);

      // Create ObjectMapper instance
      ObjectMapper objectMapper = new ObjectMapper();

      // Parse JSON into DTO
      GlobalQuoteResponse quoteResponse = objectMapper.readValue(jsonResponse,
          GlobalQuoteResponse.class);

      GlobalQuote quote = quoteResponse.getGlobalQuote();

      if (quote == null) {
        logger.error("Parsed GlobalQuote is null for symbol={}", symbol);
      } else {
        logger.info("Current quote for {}:", quote.getSymbol());
        logger.info("  Latest trading day : {}", quote.getLatestTradingDay());
        logger.info("  Price              : {}", quote.getPrice());
        logger.info("  Previous close     : {}", quote.getPreviousClose());
        logger.info("  Change             : {}", quote.getChange());
        logger.info("  Change percent     : {}", quote.getChangePercent());
      }


    } catch (IllegalArgumentException e) {
      logger.error("Invalid input: {}", e.getMessage(), e);
    } catch (IOException e) {
      logger.error("IO error when calling Alpha Vantage", e);
    } catch (InterruptedException e) {
      logger.error("Request to Alpha Vantage was interrupted", e);
      Thread.currentThread().interrupt();
    } catch (Exception e) {
      logger.error("Unexpected error in StockQuoteApp", e);
    }

    logger.info("StockQuoteApp Stopped");
  }
}
