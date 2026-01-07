package ca.jrvs.apps.trading.dao;

import ca.jrvs.apps.trading.model.Account;
import java.util.ArrayList;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class AccountDao {

  private final JdbcTemplate jdbcTemplate;

  public AccountDao(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  private static final RowMapper<Account> ACCOUNT_ROW_MAPPER = (rs, rowNum) -> {
    Account account = new Account();
    account.setId(rs.getInt("id"));
    account.setTraderId(rs.getInt("trader_id"));
    account.setAmount(rs.getDouble("amount"));
    return account;
  };

  private static final String FIND_BY_ID =
      "SELECT id, trader_id, amount FROM account WHERE id = ?";

  private static final String INSERT_RETURNING_ID =
      "INSERT INTO account (trader_id, amount) VALUES (?, ?) RETURNING id";

  private static final String UPDATE_BY_ID =
      "UPDATE account SET trader_id=?, amount=? WHERE id=?";

  private static final String EXISTS_BY_ID =
      "SELECT EXISTS (SELECT 1 FROM account WHERE id = ?)";

  private static final String FIND_ALL =
      "SELECT id, trader_id, amount FROM account ORDER BY id";

  private static final String DELETE_BY_ID =
      "DELETE FROM account WHERE id=?";

  private static final String DELETE_ALL =
      "DELETE FROM account";

  private static final String COUNT =
      "SELECT COUNT(1) FROM account";

  public Account findById(Integer id) {
    return jdbcTemplate.query(FIND_BY_ID, ACCOUNT_ROW_MAPPER, id)
        .stream().findFirst().orElse(null);
  }

  public Account save(Account account) {
    if (account.getId() == null) {
      Integer newId = jdbcTemplate.queryForObject(
          INSERT_RETURNING_ID,
          Integer.class,
          account.getTraderId(),
          account.getAmount()
      );
      account.setId(newId);
      return account;
    } else {
      jdbcTemplate.update(
          UPDATE_BY_ID,
          account.getTraderId(),
          account.getAmount(),
          account.getId()
      );
      return findById(account.getId());
    }
  }

  public boolean existsById(Integer id) {
    Boolean exists = jdbcTemplate.queryForObject(EXISTS_BY_ID, Boolean.class, id);
    return Boolean.TRUE.equals(exists);
  }

  public List<Account> findAll() {
    return jdbcTemplate.query(FIND_ALL, ACCOUNT_ROW_MAPPER);
  }

  public List<Account> findAllById(Iterable<Integer> ids) {
    List<Account> result = new ArrayList<>();
    for (Integer id : ids) {
      Account a = findById(id);
      if (a != null) result.add(a);
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
