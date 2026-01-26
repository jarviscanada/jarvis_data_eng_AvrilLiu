package ca.jrvs.apps.trading.dao;

import ca.jrvs.apps.trading.Application;
import ca.jrvs.apps.trading.model.Account;
import ca.jrvs.apps.trading.model.Trader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class AccountDaoIntTest {

  @Autowired
  private TraderDao traderDao;

  @Autowired
  private AccountDao accountDao;

  @Before
  public void setup() {
    // If you already have SecurityOrderDao implemented, delete it first (child -> parent).
    // securityOrderDao.deleteAll();
    accountDao.deleteAll();
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

  private Account newAccount(Integer traderId, double amount) {
    Account a = new Account();
    a.setTraderId(traderId);
    a.setAmount(amount);
    return a;
  }

  @Test
  public void save_create_and_findById() {
    Trader savedTrader = traderDao.save(newTrader("T", "1", "t1@x.com"));
    assertNotNull(savedTrader.getId());

    Account a = newAccount(savedTrader.getId(), 1000.0);
    Account saved = accountDao.save(a);

    assertNotNull(saved.getId());

    Account found = accountDao.findById(saved.getId());
    assertNotNull(found);
    assertEquals(saved.getId(), found.getId());
    assertEquals(savedTrader.getId(), found.getTraderId());
    assertEquals(1000.0, found.getAmount(), 0.000001);
  }

  @Test
  public void save_update() {
    Trader savedTrader = traderDao.save(newTrader("T", "2", "t2@x.com"));

    Account saved = accountDao.save(newAccount(savedTrader.getId(), 10.0));
    saved.setAmount(99.5);

    Account updated = accountDao.save(saved);
    assertEquals(saved.getId(), updated.getId());
    assertEquals(99.5, updated.getAmount(), 0.000001);
  }

  @Test
  public void existsById_count_findAll() {
    assertEquals(0L, accountDao.count());

    Trader t1 = traderDao.save(newTrader("A", "A", "a@a.com"));
    Trader t2 = traderDao.save(newTrader("B", "B", "b@b.com"));

    Account a1 = accountDao.save(newAccount(t1.getId(), 1.0));
    Account a2 = accountDao.save(newAccount(t2.getId(), 2.0));

    assertEquals(2L, accountDao.count());
    assertTrue(accountDao.existsById(a1.getId()));
    assertTrue(accountDao.existsById(a2.getId()));

    List<Account> all = accountDao.findAll();
    assertEquals(2, all.size());
  }

  @Test
  public void findAllById_deleteById_deleteAll() {
    Trader t1 = traderDao.save(newTrader("C", "1", "c1@x.com"));
    Trader t2 = traderDao.save(newTrader("C", "2", "c2@x.com"));

    Account a1 = accountDao.save(newAccount(t1.getId(), 11.0));
    Account a2 = accountDao.save(newAccount(t2.getId(), 22.0));

    List<Account> some = accountDao.findAllById(Arrays.asList(a1.getId(), 999999, a2.getId()));
    assertEquals(2, some.size());

    accountDao.deleteById(a1.getId());
    assertNull(accountDao.findById(a1.getId()));
    assertEquals(1L, accountDao.count());

    accountDao.deleteAll();
    assertEquals(0L, accountDao.count());
  }
}
