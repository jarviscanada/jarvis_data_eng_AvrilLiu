package ca.jrvs.apps.trading.dao;

import ca.jrvs.apps.trading.model.SecurityOrder;
import java.util.ArrayList;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class SecurityOrderDao {

  private final JdbcTemplate jdbcTemplate;

  public SecurityOrderDao(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  private static final RowMapper<SecurityOrder> SECURITY_ORDER_ROW_MAPPER = (rs, rowNum) -> {
    SecurityOrder o = new SecurityOrder();
    o.setId(rs.getInt("id"));
    o.setAccountId(rs.getInt("account_id"));
    o.setStatus(rs.getString("status"));
    o.setTicker(rs.getString("ticker"));
    o.setSize(rs.getInt("size"));

    // nullable columns
    double price = rs.getDouble("price");
    o.setPrice(rs.wasNull() ? null : price);

    o.setNotes(rs.getString("notes")); // getString returns null if sql null
    return o;
  };

  private static final String FIND_BY_ID =
      "SELECT id, account_id, status, ticker, size, price, notes FROM security_order WHERE id = ?";

  private static final String INSERT_RETURNING_ID =
      "INSERT INTO security_order (account_id, status, ticker, size, price, notes) " +
          "VALUES (?, ?, ?, ?, ?, ?) RETURNING id";

  private static final String UPDATE_BY_ID =
      "UPDATE security_order SET account_id=?, status=?, ticker=?, size=?, price=?, notes=? WHERE id=?";

  private static final String EXISTS_BY_ID =
      "SELECT EXISTS (SELECT 1 FROM security_order WHERE id = ?)";

  private static final String FIND_ALL =
      "SELECT id, account_id, status, ticker, size, price, notes FROM security_order ORDER BY id";

  private static final String DELETE_BY_ID =
      "DELETE FROM security_order WHERE id=?";

  private static final String DELETE_ALL =
      "DELETE FROM security_order";

  private static final String COUNT =
      "SELECT COUNT(1) FROM security_order";

  public SecurityOrder findById(Integer id) {
    return jdbcTemplate.query(FIND_BY_ID, SECURITY_ORDER_ROW_MAPPER, id)
        .stream().findFirst().orElse(null);
  }

  public SecurityOrder save(SecurityOrder order) {
    if (order.getId() == null) {
      Integer newId = jdbcTemplate.queryForObject(
          INSERT_RETURNING_ID,
          Integer.class,
          order.getAccountId(),
          order.getStatus(),
          order.getTicker(),
          order.getSize(),
          order.getPrice(), // can be null
          order.getNotes()  // can be null
      );
      order.setId(newId);
      return order;
    } else {
      jdbcTemplate.update(
          UPDATE_BY_ID,
          order.getAccountId(),
          order.getStatus(),
          order.getTicker(),
          order.getSize(),
          order.getPrice(),
          order.getNotes(),
          order.getId()
      );
      return findById(order.getId());
    }
  }

  public boolean existsById(Integer id) {
    Boolean exists = jdbcTemplate.queryForObject(EXISTS_BY_ID, Boolean.class, id);
    return Boolean.TRUE.equals(exists);
  }

  public List<SecurityOrder> findAll() {
    return jdbcTemplate.query(FIND_ALL, SECURITY_ORDER_ROW_MAPPER);
  }

  public List<SecurityOrder> findAllById(Iterable<Integer> ids) {
    List<SecurityOrder> result = new ArrayList<>();
    for (Integer id : ids) {
      SecurityOrder o = findById(id);
      if (o != null) result.add(o);
    }
    return result;
  }

  public void deleteById(Integer id) {
    jdbcTemplate.update(DELETE_BY_ID, id);
  }

  public void deleteAll() {
    jdbcTemplate.update(DELETE_ALL);
  }

  public long count() {
    Long c = jdbcTemplate.queryForObject(COUNT, Long.class);
    return c == null ? 0L : c;
  }
}
