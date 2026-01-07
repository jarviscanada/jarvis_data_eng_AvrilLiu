package ca.jrvs.apps.trading.service;

import ca.jrvs.apps.trading.Application;
import ca.jrvs.apps.trading.dao.AccountDao;
import ca.jrvs.apps.trading.dao.SecurityOrderDao;
import ca.jrvs.apps.trading.dao.TraderDao;
import ca.jrvs.apps.trading.model.Account;
import ca.jrvs.apps.trading.model.Trader;
import ca.jrvs.apps.trading.view.TraderAccountView;
import java.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;
import org.springframework.jdbc.core.JdbcTemplate;



@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class TraderAccountServiceIntTest {

  private TraderAccountView savedView;

  @Autowired
  private TraderAccountService traderAccountService;

  @Autowired
  private TraderDao traderDao;

  @Autowired
  private AccountDao accountDao;

  @Autowired
  private SecurityOrderDao securityOrderDao;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Before
  public void setup() {
    // Clean tables in child -> parent order to avoid FK violation
    securityOrderDao.deleteAll();
    accountDao.deleteAll();
    traderDao.deleteAll();

    // Create a baseline trader+account for deposit/withdraw/delete tests
    Trader t = new Trader();
    t.setFirstName("Test");
    t.setLastName("User");
    t.setDob(LocalDate.of(1990, 1, 1));
    t.setCountry("CA");
    t.setEmail("test.user@example.com");

    savedView = traderAccountService.createTraderAndAccount(t);
    assertNotNull(savedView);
    assertNotNull(savedView.getTrader());
    assertNotNull(savedView.getAccount());
    assertNotNull(savedView.getTrader().getId());
    assertNotNull(savedView.getAccount().getId());
  }

  @Test
  public void createTraderAndAccount_shouldInitBalanceAndIdMapping() {
    Integer traderId = savedView.getTrader().getId();
    Integer accountId = savedView.getAccount().getId();

    // Assumption: traderId == accountId
    assertEquals(traderId, accountId);

    // Initial balance must be 0
    assertEquals(0.0, savedView.getAccount().getAmount(), 0.000001);

    // Positions should be non-null (empty is fine)
    assertNotNull(savedView.getPositions());
  }

  @Test
  public void deposit_and_withdraw_shouldUpdateBalance() {
    Integer traderId = savedView.getTrader().getId();

    Account afterDeposit = traderAccountService.deposit(traderId, 100.0);
    assertEquals(100.0, afterDeposit.getAmount(), 0.000001);

    Account afterWithdraw = traderAccountService.withdraw(traderId, 30.0);
    assertEquals(70.0, afterWithdraw.getAmount(), 0.000001);

    // Double-check persisted value in DB
    Account persisted = accountDao.findById(traderId);
    assertNotNull(persisted);
    assertEquals(70.0, persisted.getAmount(), 0.000001);
  }

  @Test(expected = IllegalArgumentException.class)
  public void withdraw_shouldThrowWhenInsufficientBalance() {
    Integer traderId = savedView.getTrader().getId();
    traderAccountService.withdraw(traderId, 1.0);
  }

  @Test
  public void deleteTraderById_shouldDeleteWhenNoCashAndNoPosition() {
    Integer traderId = savedView.getTrader().getId();

    // Sanity check before delete (prove data exists)
    Integer beforeTraderCnt = jdbcTemplate.queryForObject(
        "SELECT COUNT(1) FROM trader WHERE id=?",
        Integer.class,
        traderId
    );
    Integer beforeAccountCnt = jdbcTemplate.queryForObject(
        "SELECT COUNT(1) FROM account WHERE id=?",
        Integer.class,
        traderId
    );
    System.out.println("Before delete: traderCnt=" + beforeTraderCnt + ", accountCnt=" + beforeAccountCnt);

    traderAccountService.deleteTraderById(traderId);

    // Strongest proof: direct SQL count must be 0
    Integer afterTraderCnt = jdbcTemplate.queryForObject(
        "SELECT COUNT(1) FROM trader WHERE id=?",
        Integer.class,
        traderId
    );
    Integer afterAccountCnt = jdbcTemplate.queryForObject(
        "SELECT COUNT(1) FROM account WHERE id=?",
        Integer.class,
        traderId
    );
    System.out.println("After delete: traderCnt=" + afterTraderCnt + ", accountCnt=" + afterAccountCnt);

    assertEquals(Integer.valueOf(0), afterTraderCnt);
    assertEquals(Integer.valueOf(0), afterAccountCnt);

    // Secondary check: DAO should also return null
    assertNull(traderDao.findById(traderId));
    assertNull(accountDao.findById(traderId));
  }


}

