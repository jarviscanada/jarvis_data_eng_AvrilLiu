package ca.jrvs.apps.stockquote.dao;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class QuoteDao implements CrudDao<Quote, String> {

  private Connection connection;

  public QuoteDao(Connection connection) {
    this.connection = connection;
  }

  // Upsert Quote by primary key (symbol)
  private static final String UPSERT_SQL =
      "INSERT INTO quote (symbol, open, high, low, price, volume, latest_trading_day, previous_close, change, change_percent, timestamp) "
          +
          "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
          "ON CONFLICT (symbol) DO UPDATE SET " +
          "open = EXCLUDED.open, " +
          "high = EXCLUDED.high, " +
          "low = EXCLUDED.low, " +
          "price = EXCLUDED.price, " +
          "volume = EXCLUDED.volume, " +
          "latest_trading_day = EXCLUDED.latest_trading_day, " +
          "previous_close = EXCLUDED.previous_close, " +
          "change = EXCLUDED.change, " +
          "change_percent = EXCLUDED.change_percent, " +
          "timestamp = EXCLUDED.timestamp";

  // Delete one row by primary key (symbol)
  private static final String DELETE_BY_ID_SQL = "DELETE FROM quote WHERE symbol = ?";

  // Delete all rows
  private static final String DELETE_ALL_SQL = "DELETE FROM quote";

  // Select one row by primary key (symbol)
  private static final String SELECT_BY_ID_SQL =
      "SELECT symbol, open, high, low, price, volume, latest_trading_day, previous_close, change, change_percent, timestamp "
          +
          "FROM quote WHERE symbol = ?";

  // Select all rows
  private static final String SELECT_ALL_SQL =
      "SELECT symbol, open, high, low, price, volume, latest_trading_day, previous_close, change, change_percent, timestamp "
          +
          "FROM quote";

  // Map a ResultSet row to a Quote object
  private Quote mapRowToQuote(java.sql.ResultSet rs) throws java.sql.SQLException {
    Quote q = new Quote();
    q.setTicker(rs.getString("symbol"));
    q.setOpen(rs.getDouble("open"));
    q.setHigh(rs.getDouble("high"));
    q.setLow(rs.getDouble("low"));
    q.setPrice(rs.getDouble("price"));
    q.setVolume(rs.getInt("volume"));
    q.setLatestTradingDay(rs.getDate("latest_trading_day")); // java.sql.Date
    q.setPreviousClose(rs.getDouble("previous_close"));
    q.setChange(rs.getDouble("change"));
    q.setChangePercent(rs.getString("change_percent"));
    q.setTimestamp(rs.getTimestamp("timestamp"));
    return q;
  }

  @Override
  public Quote save(Quote entity) throws IllegalArgumentException {
    if (entity == null) {
      throw new IllegalArgumentException("Quote must not be null");
    }
    if (entity.getTicker() == null || entity.getTicker().isBlank()) {
      throw new IllegalArgumentException("Ticker must not be null or empty");
    }

    try (var ps = connection.prepareStatement(UPSERT_SQL)) {
      ps.setString(1, entity.getTicker());
      ps.setDouble(2, entity.getOpen());
      ps.setDouble(3, entity.getHigh());
      ps.setDouble(4, entity.getLow());
      ps.setDouble(5, entity.getPrice());
      ps.setInt(6, entity.getVolume());
      ps.setDate(7, entity.getLatestTradingDay());
      ps.setDouble(8, entity.getPreviousClose());
      ps.setDouble(9, entity.getChange());
      ps.setString(10, entity.getChangePercent());
      ps.setTimestamp(11, entity.getTimestamp());

      ps.executeUpdate();
      return entity;
    } catch (Exception e) {
      throw new IllegalStateException("Failed to save quote: " + entity.getTicker(), e);
    }
  }


  @Override
  public Optional<Quote> findById(String s) throws IllegalArgumentException {
    if (s == null || s.isBlank()) {
      throw new IllegalArgumentException("Quote must not be null or blank");
    }

    try (var ps = connection.prepareStatement(SELECT_BY_ID_SQL)) {
      ps.setString(1, s);

      try (var rs = ps.executeQuery()) {

        if (!rs.next()) {
          return Optional.empty();
        }

        Quote q = mapRowToQuote(rs);
        return Optional.of(q);
      }
    } catch (Exception e) {
      throw new IllegalStateException("Failed to find quote by symbol: " + s, e);
    }

  }

  @Override
  public Iterable<Quote> findAll() {
    List<Quote> result = new ArrayList<>();
    try (var ps = connection.prepareStatement(SELECT_ALL_SQL);
        var rs = ps.executeQuery()) {

      while (rs.next()) {
        result.add(mapRowToQuote(rs));
      }

      return result;
    } catch (Exception e) {
      throw new IllegalStateException("Failed to find all quotes", e);
    }
  }

  @Override
  public void deleteById(String s) throws IllegalArgumentException {
    if (s == null || s.isBlank()) {
      throw new IllegalArgumentException("Quote must not be null or blank");
    }

    try (var ps = connection.prepareStatement(DELETE_BY_ID_SQL)) {
      ps.setString(1, s);
      ps.executeUpdate();
    } catch (Exception e) {
      throw new IllegalStateException("Failed to delete quote: " + s, e);
    }
  }

  @Override
  public void deleteAll() {
    try (var ps = connection.prepareStatement(DELETE_ALL_SQL)) {
      ps.executeUpdate();
    } catch (Exception e) {
      throw new IllegalStateException("Failed to delete all quotes:", e);
    }
  }
}
