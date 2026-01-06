package ca.jrvs.apps.trading.dao;

import ca.jrvs.apps.trading.model.Trader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class TraderDao {
  private final JdbcTemplate jdbcTemplate;

  public TraderDao(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  private static final RowMapper<Trader> TRADER_ROW_MAPPER = (rs, rowNum) -> {
    Trader t = new Trader();
    t.setId(rs.getInt("id"));
    t.setFirstName(rs.getString("first_name"));
    t.setLastName(rs.getString("last_name"));
    t.setDob(rs.getDate("dob").toLocalDate());
    t.setCountry(rs.getString("country"));
    t.setEmail(rs.getString("email"));
    return t;
  };

  private static final String FIND_BY_ID =
      "SELECT id, first_name, last_name, dob, country, email FROM trader WHERE id = ?";

  private static final String INSERT_RETURNING_ID =
      "INSERT INTO trader (first_name, last_name, dob, country, email) VALUES (?, ?, ?, ?, ?) RETURNING id";

  private static final String UPDATE_BY_ID =
      "UPDATE trader SET first_name=?, last_name=?, dob=?, country=?, email=? WHERE id=?";

  public Trader findById(Integer id) {
    return jdbcTemplate.query(FIND_BY_ID, TRADER_ROW_MAPPER, id)
        .stream().findFirst().orElse(null);
  }

  public Trader save(Trader trader) {
    if (trader.getId() == null) {
      Integer newId = jdbcTemplate.queryForObject(
          INSERT_RETURNING_ID,
          Integer.class,
          trader.getFirstName(),
          trader.getLastName(),
          java.sql.Date.valueOf(trader.getDob()),
          trader.getCountry(),
          trader.getEmail()
      );
      trader.setId(newId);
      return trader;
    } else {
      jdbcTemplate.update(
          UPDATE_BY_ID,
          trader.getFirstName(),
          trader.getLastName(),
          java.sql.Date.valueOf(trader.getDob()),
          trader.getCountry(),
          trader.getEmail(),
          trader.getId()
      );
      return findById(trader.getId());
    }
  }
}
