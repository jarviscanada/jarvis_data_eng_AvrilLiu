package ca.jrvs.apps.trading.dao;

import ca.jrvs.apps.trading.model.Position;
import java.util.ArrayList;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class PositionDao {

  private final JdbcTemplate jdbcTemplate;

  public PositionDao(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  private static final RowMapper<Position> POSITION_ROW_MAPPER = (rs, rowNum) -> {
    Position p = new Position();
    p.setAccountId(rs.getInt("account_id"));
    p.setTicker(rs.getString("ticker"));
    p.setPosition(rs.getLong("position"));
    return p;
  };

  private static final String FIND_BY_ACCOUNT_AND_TICKER =
      "SELECT account_id, ticker, position FROM position WHERE account_id=? AND ticker=?";

  private static final String EXISTS_BY_ACCOUNT_AND_TICKER =
      "SELECT EXISTS (SELECT 1 FROM position WHERE account_id=? AND ticker=?)";

  private static final String FIND_ALL =
      "SELECT account_id, ticker, position FROM position ORDER BY account_id, ticker";

  private static final String COUNT =
      "SELECT COUNT(1) FROM position";

  // read
  public Position findByAccountIdAndTicker(Integer accountId, String ticker) {
    return jdbcTemplate.query(FIND_BY_ACCOUNT_AND_TICKER, POSITION_ROW_MAPPER, accountId, ticker)
        .stream().findFirst().orElse(null);
  }

  public boolean existsByAccountIdAndTicker(Integer accountId, String ticker) {
    Boolean exists = jdbcTemplate.queryForObject(EXISTS_BY_ACCOUNT_AND_TICKER, Boolean.class, accountId, ticker);
    return Boolean.TRUE.equals(exists);
  }

  public List<Position> findAll() {
    return jdbcTemplate.query(FIND_ALL, POSITION_ROW_MAPPER);
  }

  /**
   * Return all positions for a list of account IDs.
   * (Because position doesn't have a single 'id', using account_id is the practical "ID list".)
   */
  public List<Position> findAllByAccountId(Iterable<Integer> accountIds) {
    List<Position> result = new ArrayList<>();
    for (Integer accountId : accountIds) {
      List<Position> positions = jdbcTemplate.query(
          "SELECT account_id, ticker, position FROM position WHERE account_id=? ORDER BY ticker",
          POSITION_ROW_MAPPER,
          accountId
      );
      result.addAll(positions);
    }
    return result;
  }

  public long count() {
    Long c = jdbcTemplate.queryForObject(COUNT, Long.class);
    return c == null ? 0L : c;
  }

  // write ops disabled (view is read-only)
  public Position save(Position position) {
    throw new UnsupportedOperationException("position view is read-only; save is not supported");
  }

  public void deleteById(Integer id) {
    throw new UnsupportedOperationException("position view is read-only; delete is not supported");
  }

  public void deleteAll() {
    throw new UnsupportedOperationException("position view is read-only; delete is not supported");
  }
}
