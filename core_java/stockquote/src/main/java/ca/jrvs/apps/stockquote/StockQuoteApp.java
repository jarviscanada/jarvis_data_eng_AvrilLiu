package ca.jrvs.apps.stockquote;

import ca.jrvs.apps.stockquote.controller.StockQuoteController;
import ca.jrvs.apps.stockquote.dao.PositionDao;
import ca.jrvs.apps.stockquote.dao.QuoteDao;
import ca.jrvs.apps.stockquote.dao.QuoteHttpHelper;
import ca.jrvs.apps.stockquote.service.PositionService;
import ca.jrvs.apps.stockquote.service.QuoteFetcher;
import ca.jrvs.apps.stockquote.service.QuoteService;
import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StockQuoteApp {

  private static final Logger logger = LoggerFactory.getLogger(StockQuoteApp.class);

  private static Map<String, String> loadProps(String path) {

    Map<String, String> props = new HashMap<>();

    try (BufferedReader br = new BufferedReader(new FileReader(path))) {
      String line;
      while ((line = br.readLine()) != null) {
        line = line.trim();
        if (line.isEmpty() || line.startsWith("#")) {
          continue;
        }
        String[] tokens = line.split(":", 2);
        if (tokens.length != 2) {
          throw new IllegalArgumentException("Bad property line: " + line);
        }
        props.put(tokens[0].trim(), tokens[1].trim());
      }
    } catch (Exception e) {
      throw new RuntimeException("Failed to load properties from " + path, e);
    }

    return props;
  }

  public static void main(String[] args) {

    // Application entry point
    logger.info("Starting StockQuoteApp");

    try {
      Map<String, String> props = loadProps("src/main/resources/properties.txt");
      logger.info("Loaded properties keys: {}", props.keySet());

      String url = "jdbc:postgresql://"
          + props.get("server") + ":"
          + props.get("port") + "/"
          + props.get("database");

      try (Connection conn = DriverManager.getConnection(
          url,
          props.get("username"),
          props.get("password")
      )) {
        logger.info("Database connection established");

        OkHttpClient httpClient = new OkHttpClient();

        QuoteDao quoteDao = new QuoteDao(conn);
        PositionDao positionDao = new PositionDao(conn);

        QuoteFetcher quoteFetcher = new QuoteHttpHelper(props.get("api-key"), httpClient);
        QuoteService quoteService = new QuoteService(quoteDao, quoteFetcher);

        PositionService positionService = new PositionService(positionDao, quoteDao);

        StockQuoteController controller = new StockQuoteController(quoteService, positionService);
        controller.initClient();
      }
    } catch (Exception e) {
      logger.error("Fatal error when starting StockQuoteApp", e);
    }

    logger.info("StockQuoteApp Stopped");
  }
}
