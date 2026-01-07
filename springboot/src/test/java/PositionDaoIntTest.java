import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import ca.jrvs.apps.trading.Application;
import ca.jrvs.apps.trading.dao.AccountDao;
import ca.jrvs.apps.trading.dao.PositionDao;
import ca.jrvs.apps.trading.dao.SecurityOrderDao;
import ca.jrvs.apps.trading.dao.TraderDao;
import ca.jrvs.apps.trading.model.Account;
import ca.jrvs.apps.trading.model.Position;
import ca.jrvs.apps.trading.model.SecurityOrder;
import ca.jrvs.apps.trading.model.Trader;
import java.time.LocalDate;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class PositionDaoIntTest {

  @Autowired
  private TraderDao traderDao;

  @Autowired
  private AccountDao accountDao;

  @Autowired
  private SecurityOrderDao securityOrderDao;

  @Autowired
  private PositionDao positionDao;

  @Before
  public void setup() {
    securityOrderDao.deleteAll();
    accountDao.deleteAll();
    traderDao.deleteAll();
  }

  private Trader newTrader() {
    Trader t = new Trader();
    t.setFirstName("P");
    t.setLastName("T");
    t.setDob(LocalDate.of(1990, 1, 1));
    t.setCountry("CA");
    t.setEmail("p@t.com");
    return t;
  }

  private Account newAccount(Integer traderId) {
    Account a = new Account();
    a.setTraderId(traderId);
    a.setAmount(10_000.0);
    return a;
  }

  private SecurityOrder newOrder(Integer accountId, String ticker, int size) {
    SecurityOrder o = new SecurityOrder();
    o.setAccountId(accountId);
    o.setStatus(
        "FILLED");
    o.setTicker(ticker);
    o.setSize(size);
    o.setPrice(null);
    o.setNotes(null);
    return o;
  }

  @Test
  public void position_view_read_only() {
    Trader t = traderDao.save(newTrader());
    Account a = accountDao.save(newAccount(t.getId()));

    // IMPORTANT: must exist in quote table
    String ticker = "AAPL.US";

    // create orders so the view has rows
    securityOrderDao.save(newOrder(a.getId(), ticker, 10));
    securityOrderDao.save(newOrder(a.getId(), ticker, 5));

    // read from view
    Position p = positionDao.findByAccountIdAndTicker(a.getId(), ticker);
    assertNotNull(p);
    assertEquals(a.getId(), p.getAccountId());
    assertEquals(ticker, p.getTicker());
    assertEquals(15L, p.getPosition().longValue());

    List<Position> all = positionDao.findAll();
    assertTrue(all.size() >= 1);

    assertTrue(positionDao.existsByAccountIdAndTicker(a.getId(), ticker));

    // write ops should be disabled
    try {
      positionDao.save(p);
      fail("Expected UnsupportedOperationException");
    } catch (UnsupportedOperationException ignored) {
    }
  }
}
