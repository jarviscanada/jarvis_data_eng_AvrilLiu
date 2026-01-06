package ca.jrvs.apps.trading.dao;

import ca.jrvs.apps.trading.model.EodhdQuote;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * DAO for quote table.
 * Responsible for persisting and retrieving quote records.
 */
@Repository
public class QuoteDao {

  private final JdbcTemplate jdbcTemplate;

  public QuoteDao(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  /**
   * Insert or update a quote record.
   *
   * @param quote latest market quote
   * @return number of affected rows
   */
  public int upsert(EodhdQuote quote) {
    String sql =
        "INSERT INTO quote (ticker, last_price, bid_price, bid_size, ask_price, ask_size) " +
            "VALUES (?, ?, ?, ?, ?, ?) " +
            "ON CONFLICT (ticker) DO UPDATE SET " +
            "last_price = EXCLUDED.last_price, " +
            "bid_price = EXCLUDED.bid_price, " +
            "bid_size = EXCLUDED.bid_size, " +
            "ask_price = EXCLUDED.ask_price, " +
            "ask_size = EXCLUDED.ask_size";

    return jdbcTemplate.update(
        sql,
        quote.getSymbol(),
        quote.getLastTradePrice(),
        quote.getBidPrice(),
        quote.getBidSize(),
        quote.getAskPrice(),
        quote.getAskSize()
    );
  }

  /**
   * Find a quote by ticker.
   *
   * @param ticker symbol (e.g. AAPL.US)
   * @return quote object or null if not found
   */
  public EodhdQuote findByTicker(String ticker) {
    String sql =
        "SELECT ticker, last_price, bid_price, bid_size, ask_price, ask_size " +
            "FROM quote WHERE ticker = ?";

    return jdbcTemplate.query(
        sql,
        rs -> {
          if (!rs.next()) {
            return null;
          }
          EodhdQuote q = new EodhdQuote();
          q.setSymbol(rs.getString("ticker"));
          q.setLastTradePrice(rs.getDouble("last_price"));
          q.setBidPrice(rs.getDouble("bid_price"));
          q.setBidSize(rs.getInt("bid_size"));
          q.setAskPrice(rs.getDouble("ask_price"));
          q.setAskSize(rs.getInt("ask_size"));
          return q;
        },
        ticker
    );
  }
}
