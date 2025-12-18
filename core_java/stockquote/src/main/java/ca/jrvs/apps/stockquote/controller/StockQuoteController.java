package ca.jrvs.apps.stockquote.controller;

import ca.jrvs.apps.stockquote.dao.Position;
import ca.jrvs.apps.stockquote.dao.Quote;
import ca.jrvs.apps.stockquote.service.PositionService;
import ca.jrvs.apps.stockquote.service.QuoteService;
import java.util.Locale;
import java.util.Optional;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StockQuoteController {

  private final QuoteService quoteService;
  private final PositionService positionService;

  private static final Logger logger = LoggerFactory.getLogger(StockQuoteController.class);

  public StockQuoteController(QuoteService quoteService, PositionService positionService) {
    this.quoteService = quoteService;
    this.positionService = positionService;
  }

  public void initClient() {
    printHelp();

    try (Scanner scanner = new Scanner(System.in)) {
      while (true) {
        System.out.print("\nstockquote> ");
        String line = scanner.nextLine();

        if (line == null) {
          continue;
        }

        line = line.trim();
        if (line.isEmpty()) {
          continue;
        }
        logger.info("Command received: {}", line);

        String[] tokens = line.split("\\s+");
        String cmd = tokens[0].toLowerCase(Locale.ROOT);

        try {
          if ("exit".equals(cmd) || "quit".equals(cmd)) {
            System.out.println("Bye.");
            break;
          } else if ("help".equals(cmd)) {
            printHelp();
          } else if ("quote".equals(cmd)) {
            handleQuote(tokens);
          } else if ("buy".equals(cmd)) {
            handleBuy(tokens);
          } else if ("portfolio".equals(cmd)) {
            handlePortfolio(tokens);
          } else if ("sell".equals(cmd)) {
            handleSell(tokens);
          } else {
            System.out.println("Unknown command. Type `help`.");
          }
        } catch (IllegalArgumentException e) {
          logger.warn("Invalid input: {}", e.getMessage());
          System.out.println("Invalid input: " + e.getMessage());
        } catch (Exception e) {
          logger.error("Unexpected error", e.getMessage());
          System.out.println("Error: " + e.getMessage());
        }
      }
    }
  }

  private void handleQuote(String[] tokens) {
    if (tokens.length != 2) {
      throw new IllegalArgumentException("Usage: quote <ticker>");
    }

    String ticker = tokens[1].trim().toUpperCase(Locale.ROOT);
    validateTicker(ticker);

    Optional<Quote> opt = quoteService.fetchQuoteDataFromAPI(ticker);
    Quote q = opt.orElseThrow(() ->
        new IllegalArgumentException("No quote found for ticker: " + ticker)
    );

    printQuote(q);
  }

  private void validateTicker(String ticker) {
    if (ticker.isEmpty()) {
      throw new IllegalArgumentException("Ticker is empty");
    }
    if (!ticker.matches("^[A-Z.]{1,10}$")) {
      throw new IllegalArgumentException("Ticker must be 1-10 chars: A-Z or '.'");
    }
  }

  private void handleBuy(String[] tokens) {
    if (tokens.length != 3) {
      throw new IllegalArgumentException("Usage: buy <ticker> <shares>");
    }

    String ticker = tokens[1].trim().toUpperCase(Locale.ROOT);
    validateTicker(ticker);

    int shares;
    try {
      shares = Integer.parseInt(tokens[2]);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Shares must be an integer");
    }
    if (shares <= 0) {
      throw new IllegalArgumentException("Shares must be > 0");
    }

    Quote q = quoteService.fetchQuoteDataFromAPI(ticker)
        .orElseThrow(() -> new IllegalArgumentException("No quote found for ticker: " + ticker));

    Position p = positionService.buy(ticker, shares, q.getPrice());

    System.out.println("Bought " + shares + " shares of " + ticker);
    System.out.println("Position: " + p);
  }

  private void handlePortfolio(String[] tokens) {
    if (tokens.length != 1) {
      throw new IllegalArgumentException("Usage: portfolio");
    }

    Iterable<Position> positions = positionService.getAllPositions();
    boolean hasAny = positions.iterator().hasNext();
    if (!hasAny) {
      System.out.println("No positions.");
      return;
    }

    double totalMarketValue = 0.0;
    double totalCost = 0.0;

    for (Position p : positions) {
      String ticker = p.getTicker().toUpperCase(Locale.ROOT);

      Quote q = quoteService.fetchQuoteDataFromAPI(ticker)
          .orElseThrow(() -> new IllegalArgumentException("No quote found for ticker: " + ticker));

      double price = q.getPrice();
      int shares = p.getNumOfShares();
      double cost = p.getValuePaid();
      double marketValue = price * shares;
      double pnl = marketValue - cost;

      totalMarketValue += marketValue;
      totalCost += cost;

      System.out.println("-----");
      System.out.println("Ticker: " + ticker);
      System.out.println("Shares: " + shares);
      System.out.println("Cost: " + cost);
      System.out.println("Price: " + price);
      System.out.println("Market Value: " + marketValue);
      System.out.println("P/L: " + pnl);
    }

    System.out.println("=====");
    System.out.println("Total Cost: " + totalCost);
    System.out.println("Total Market Value: " + totalMarketValue);
    System.out.println("Total P/L: " + (totalMarketValue - totalCost));
  }

  private void handleSell(String[] tokens) {
    if (tokens.length != 2) {
      throw new IllegalArgumentException("Usage: sell <ticker>");
    }

    String ticker = tokens[1].trim().toUpperCase(Locale.ROOT);
    validateTicker(ticker);

    Quote q = quoteService.fetchQuoteDataFromAPI(ticker)
        .orElseThrow(() -> new IllegalArgumentException("No quote found for ticker: " + ticker));

    double sellPrice = q.getPrice();

    Position sold = positionService.sellAll(ticker, sellPrice);

    double marketValue = sold.getNumOfShares() * sellPrice;
    double pnl = marketValue - sold.getValuePaid();

    System.out.println("Sold ALL shares of " + ticker);
    System.out.println("Shares: " + sold.getNumOfShares());
    System.out.println("Sell Price: " + sellPrice);
    System.out.println("Proceeds: " + marketValue);
    System.out.println("Cost: " + sold.getValuePaid());
    System.out.println("P/L: " + pnl);
  }

  private void printQuote(Quote q) {
    if (q == null) {
      throw new IllegalArgumentException("No quote returned");
    }

    System.out.println("Ticker: " + q.getTicker());
    System.out.println("Open: " + q.getOpen());
    System.out.println("High: " + q.getHigh());
    System.out.println("Low: " + q.getLow());
    System.out.println("Price: " + q.getPrice());
    System.out.println("Volume: " + q.getVolume());
    System.out.println("Latest Trading Day: " + q.getLatestTradingDay());
    System.out.println("Previous Close: " + q.getPreviousClose());
    System.out.println("Change: " + q.getChange());
    System.out.println("Change Percent: " + q.getChangePercent());
  }


  private void printHelp() {
    System.out.println("Commands:");
    System.out.println("  help                  Show commands");
    System.out.println("  quote <ticker>         Show latest quote for ticker");
    System.out.println("  exit                   Quit app");
  }
}
