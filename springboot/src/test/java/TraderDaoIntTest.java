import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import ca.jrvs.apps.trading.Application;
import ca.jrvs.apps.trading.dao.TraderDao;
import ca.jrvs.apps.trading.model.Trader;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class TraderDaoIntTest {

  @Autowired
  private TraderDao traderDao;

  @Before
  public void setup() {
    // If your DB already has dependent rows in account/security_order,
    // this may fail due to FK constraints. In that case, delete child tables first.
    traderDao.deleteAll();
  }

  private Trader newTrader(String first, String last, String email) {
    Trader t = new Trader();
    t.setFirstName(first);
    t.setLastName(last);
    t.setDob(LocalDate.of(1990, 1, 1));
    t.setCountry("CA");
    t.setEmail(email);
    return t;
  }

  @Test
  public void save_create_and_findById() {
    Trader t = newTrader("Test", "User", "test@user.com");
    Trader saved = traderDao.save(t);

    assertNotNull(saved.getId());

    Trader found = traderDao.findById(saved.getId());
    assertNotNull(found);
    assertEquals(saved.getId(), found.getId());
    assertEquals("Test", found.getFirstName());
  }

  @Test
  public void save_update() {
    Trader t = newTrader("A", "B", "a@b.com");
    Trader saved = traderDao.save(t);

    saved.setCountry("US");
    saved.setEmail("updated@b.com");
    Trader updated = traderDao.save(saved);

    assertEquals(saved.getId(), updated.getId());
    assertEquals("US", updated.getCountry());
    assertEquals("updated@b.com", updated.getEmail());
  }

  @Test
  public void existsById_count_findAll() {
    assertEquals(0L, traderDao.count());
    assertFalse(traderDao.existsById(1));

    Trader t1 = traderDao.save(newTrader("T1", "L1", "t1@x.com"));
    Trader t2 = traderDao.save(newTrader("T2", "L2", "t2@x.com"));

    assertEquals(2L, traderDao.count());
    assertTrue(traderDao.existsById(t1.getId()));
    assertTrue(traderDao.existsById(t2.getId()));

    List<Trader> all = traderDao.findAll();
    assertEquals(2, all.size());
  }

  @Test
  public void findAllById_deleteById_deleteAll() {
    Trader t1 = traderDao.save(newTrader("T1", "L1", "t1@x.com"));
    Trader t2 = traderDao.save(newTrader("T2", "L2", "t2@x.com"));
    Trader t3 = traderDao.save(newTrader("T3", "L3", "t3@x.com"));

    List<Trader> some = traderDao.findAllById(Arrays.asList(t1.getId(), t3.getId(), 999999));
    assertEquals(2, some.size());

    traderDao.deleteById(t2.getId());
    assertNull(traderDao.findById(t2.getId()));
    assertEquals(2L, traderDao.count());

    traderDao.deleteAll();
    assertEquals(0L, traderDao.count());
  }
}
