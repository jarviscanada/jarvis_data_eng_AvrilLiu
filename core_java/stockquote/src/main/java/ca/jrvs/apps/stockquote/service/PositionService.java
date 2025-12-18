package ca.jrvs.apps.stockquote.service;

import ca.jrvs.apps.stockquote.dao.CrudDao;
import ca.jrvs.apps.stockquote.dao.Position;
import ca.jrvs.apps.stockquote.dao.Quote;

public class PositionService {

  private final CrudDao<Position, String> positiondao;
  private final CrudDao<Quote, String> quotedao;

  public PositionService(CrudDao<Position, String> positiondao, CrudDao<Quote, String> quotedao) {
    this.positiondao = positiondao;
    this.quotedao = quotedao;
  }

  /**
   * Processes a buy order and updates the database accordingly
   * @param ticker
   * @param numberOfShares
   * @param price
   * @return
   */
  public Position buy(String ticker, int numberOfShares, double price) {
    // validate inputs
    if (ticker == null || ticker.isBlank()) {
      throw new IllegalArgumentException("ticker is null or empty");
    }
    if (numberOfShares <= 0) {
      throw new IllegalArgumentException("numberOfShares must be greater than 0");
    }
    if (price <= 0) {
      throw new IllegalArgumentException("price must be greater than 0");
    }

    // normalize ticker
    String symbol = ticker.trim().toUpperCase();

    // fetch quote from DB (market data must exist)
    Quote quote = quotedao.findById(symbol)
        .orElseThrow(
            () -> new IllegalArgumentException("Ticker " + symbol + " not found in table"));

    // business rule: cannot buy more than available volume
    if (numberOfShares > quote.getVolume()) {
      throw new IllegalArgumentException("Cannot buy more than available volume. volume=" +
          quote.getVolume() + ", requested=" + numberOfShares);
    }

    // fetch existing position (if any)
    Position position = positiondao.findById(symbol).orElse(null);

    // create or update position
    double cost = price * numberOfShares;
    if (position == null) {
      position = new Position();
      position.setTicker(symbol);
      position.setNumOfShares(numberOfShares);
      position.setValuePaid(cost);
    } else {
      position.setNumOfShares(position.getNumOfShares() + numberOfShares);
      position.setValuePaid(position.getValuePaid() + cost);
    }

    // save and return
    positiondao.save(position);
    return position;
  }

  /**
   * Sells all shares of the given ticker symbol
   * @param ticker
   */
  public void sell(String ticker) {
    if (ticker == null || ticker.isBlank()) {
      throw new IllegalArgumentException("ticker is null or empty");
    }

    String symbol = ticker.trim().toUpperCase();
    positiondao.deleteById(symbol);
  }
}
