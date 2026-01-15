package ca.jrvs.apps.stockquote.dao;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PositionDao implements CrudDao<Position, String> {

  private final Connection connection;

  public PositionDao(Connection connection) {
    this.connection = connection;
  }

  private Position mapRowToPosition(java.sql.ResultSet rs) throws java.sql.SQLException {
    Position p = new Position();
    p.setTicker(rs.getString("symbol"));
    p.setNumOfShares(rs.getInt("number_of_shares"));
    p.setValuePaid(rs.getDouble("value_paid"));
    return p;
  }

  // Insert or update a position row
  private static final String UPSERT_SQL =
      "INSERT INTO position (symbol, number_of_shares, value_paid) " +
          "VALUES (?, ?, ?) " +
          "ON CONFLICT (symbol) DO UPDATE SET " +
          "number_of_shares = EXCLUDED.number_of_shares, " +
          "value_paid = EXCLUDED.value_paid";

  // Select one row by symbol
  private static final String SELECT_BY_ID_SQL =
      "SELECT symbol, number_of_shares, value_paid FROM position WHERE symbol = ?";

  // Select all rows
  private static final String SELECT_ALL_SQL =
      "SELECT symbol, number_of_shares, value_paid FROM position";

  // Delete one row by symbol
  private static final String DELETE_BY_ID_SQL =
      "DELETE FROM position WHERE symbol = ?";

  // Delete all rows
  private static final String DELETE_ALL_SQL =
      "DELETE FROM position";

  @Override
  public Position save(Position entity) throws IllegalArgumentException {
    if (entity == null) {
      throw new IllegalArgumentException("Position must not be null");
    }
    if (entity.getTicker() == null || entity.getTicker().isBlank()) {
      throw new IllegalArgumentException("Ticker must not be null or blank");
    }

    try (var ps = connection.prepareStatement(UPSERT_SQL)) {
      ps.setString(1, entity.getTicker());
      ps.setInt(2, entity.getNumOfShares());
      ps.setDouble(3, entity.getValuePaid());

      ps.executeUpdate();
      return entity;
    } catch (Exception e) {
      // Foreign key violation will be caught here if quote(symbol) doesn't exist
      throw new IllegalStateException("Failed to save position: " + entity.getTicker(), e);
    }
  }

  @Override
  public Optional<Position> findById(String s) throws IllegalArgumentException {
    if (s == null || s.isBlank()) {
      throw new IllegalArgumentException("Position must not be null or blank");
    }

    try (var ps = connection.prepareStatement(SELECT_BY_ID_SQL)) {

      ps.setString(1, s);

      try (var rs = ps.executeQuery()) {

        if (!rs.next()) {
          return Optional.empty();
        }

        return Optional.of(mapRowToPosition(rs));
      }
    } catch (Exception e) {
      throw new IllegalStateException("Failed to find position by symbol: " + s, e);
    }
  }

  @Override
  public Iterable<Position> findAll() {
    List<Position> result = new ArrayList<>();

    try (var ps = connection.prepareStatement(SELECT_ALL_SQL);
        var rs = ps.executeQuery()) {

      while (rs.next()) {
        result.add(mapRowToPosition(rs));
      }

      return result;
    } catch (Exception e) {
      throw new IllegalStateException("Failed to find all positions", e);
    }
  }

  @Override
  public void deleteById(String s) throws IllegalArgumentException {
    if (s == null || s.isBlank()) {
      throw new IllegalArgumentException("Position must not be null or blank");
    }

    try (var ps = connection.prepareStatement(DELETE_BY_ID_SQL)) {
      ps.setString(1, s);
      ps.executeUpdate();
    }catch (Exception e) {
      throw new IllegalStateException("Failed to delete position by symbol: " + s, e);
    }
  }


  @Override
  public void deleteAll() {
    try (var ps = connection.prepareStatement(DELETE_ALL_SQL)){
      ps.executeUpdate();
    }catch (Exception e) {
      throw new IllegalStateException("Failed to delete all positions", e);
    }
  }
}
