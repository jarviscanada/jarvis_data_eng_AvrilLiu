

import ca.jrvs.apps.trading.Application;
import ca.jrvs.apps.trading.dao.AccountDao;
import ca.jrvs.apps.trading.dao.SecurityOrderDao;
import ca.jrvs.apps.trading.dao.TraderDao;
import ca.jrvs.apps.trading.model.Account;
import ca.jrvs.apps.trading.model.SecurityOrder;
import ca.jrvs.apps.trading.model.Trader;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class SecurityOrderDaoIntTest {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Autowired
  private TraderDao traderDao;

  @Autowired
  private AccountDao accountDao;

  @Autowired
  private SecurityOrderDao securityOrderDao;

  @Before
  public void setup() {
    // child -> parent
    securityOrderDao.deleteAll();
    accountDao.deleteAll();
    traderDao.deleteAll();
  }

  private String anyExistingTicker() {
    // pick one ticker that already exists in quote table
    return jdbcTemplate.query("SELECT ticker FROM quote LIMIT 1",
            (rs, rowNum) -> rs.getString("ticker"))
        .stream().findFirst()
        .orElseThrow(() -> new IllegalStateException("quote table is empty. Insert quotes first."));
  }

  private Trader newTrader() {
    Trader t = new Trader();
    t.setFirstName("Test");
    t.setLastName("User");
    t.setDob(LocalDate.of(1990, 1, 1));
    t.setCountry("CA");
    t.setEmail("test@user.com");
    return t;
  }

  private Account newAccount(Integer traderId) {
    Account a = new Account();
    a.setTraderId(traderId);
    a.setAmount(10_000.0);
    return a;
  }

  private SecurityOrder newOrder(Integer accountId, String ticker) {
    SecurityOrder o = new SecurityOrder();
    o.setAccountId(accountId);
    o.setStatus("PENDING");
    o.setTicker(ticker);
    o.setSize(10);
    o.setPrice(null);
    o.setNotes(null);
    return o;
  }

  @Test
  public void save_create_and_findById() {
    Trader t = traderDao.save(newTrader());
    Account a = accountDao.save(newAccount(t.getId()));
    String ticker = anyExistingTicker();

    SecurityOrder o = securityOrderDao.save(newOrder(a.getId(), ticker));
    assertNotNull(o.getId());

    SecurityOrder found = securityOrderDao.findById(o.getId());
    assertNotNull(found);
    assertEquals(a.getId(), found.getAccountId());
    assertEquals("PENDING", found.getStatus());
    assertEquals(ticker, found.getTicker());
    assertEquals(10, found.getSize().intValue());
  }

  @Test
  public void save_update_exists_count_findAll_delete() {
    Trader t = traderDao.save(newTrader());
    Account a = accountDao.save(newAccount(t.getId()));
    String ticker = anyExistingTicker();

    SecurityOrder o1 = securityOrderDao.save(newOrder(a.getId(), ticker));
    SecurityOrder o2 = securityOrderDao.save(newOrder(a.getId(), ticker));

    assertEquals(2L, securityOrderDao.count());
    assertTrue(securityOrderDao.existsById(o1.getId()));

    // update
    o1.setStatus("FILLED");
    o1.setPrice(123.45);
    o1.setNotes("filled in test");
    SecurityOrder updated = securityOrderDao.save(o1);
    assertEquals("FILLED", updated.getStatus());
    assertEquals(123.45, updated.getPrice(), 0.000001);

    List<SecurityOrder> all = securityOrderDao.findAll();
    assertEquals(2, all.size());

    List<SecurityOrder> some = securityOrderDao.findAllById(Arrays.asList(o1.getId(), 999999, o2.getId()));
    assertEquals(2, some.size());

    securityOrderDao.deleteById(o2.getId());
    assertNull(securityOrderDao.findById(o2.getId()));
    assertEquals(1L, securityOrderDao.count());

    securityOrderDao.deleteAll();
    assertEquals(0L, securityOrderDao.count());
  }
}
