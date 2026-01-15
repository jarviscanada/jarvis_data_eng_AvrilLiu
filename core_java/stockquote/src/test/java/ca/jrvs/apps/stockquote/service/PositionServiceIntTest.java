package ca.jrvs.apps.stockquote.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.jrvs.apps.stockquote.dao.Position;
import ca.jrvs.apps.stockquote.dao.PositionDao;
import ca.jrvs.apps.stockquote.dao.Quote;
import ca.jrvs.apps.stockquote.dao.QuoteDao;
import ca.jrvs.apps.stockquote.util.ConnectionManager;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import org.junit.jupiter.api.*;

import java.sql.Connection;

public class PositionServiceIntTest {

  private static Connection connection;

  private PositionService positionService;
  private PositionDao positionDao;
  private QuoteDao quoteDao;

  @BeforeAll
  static void setupDB() {
    connection = ConnectionManager.getConnection();
  }

  @BeforeEach
  void setup() {
    positionDao = new PositionDao(connection);
    quoteDao = new QuoteDao(connection);

    // clean tables so tests don't affect each other
    positionDao.deleteAll();
    quoteDao.deleteAll();

    positionService = new PositionService(positionDao, quoteDao);
  }

  @AfterAll
  static void closeDB() throws Exception {
    if (connection != null) {
      connection.close();
    }
  }

  @Test
  void buy_thenPositionPersisted() {
    // 1. prepare quote in DB (market data must exist)
    Quote q = new Quote();
    q.setTicker("AAPL");
    q.setOpen(10.0);
    q.setHigh(12.0);
    q.setLow(9.5);
    q.setPrice(10.0);
    q.setVolume(100);
    q.setLatestTradingDay(Date.valueOf(LocalDate.now()));
    q.setPreviousClose(9.8);
    q.setChange(0.2);
    q.setChangePercent("2.04%");
    q.setTimestamp(new Timestamp(System.currentTimeMillis()));

    quoteDao.save(q);

    // 2. call service
    Position result = positionService.buy("AAPL", 3, 10.0);

    // 3. verify returned object
    assertEquals(3, result.getNumOfShares());
    assertEquals(30.0, result.getValuePaid(), 0.000001);

    // 4. verify data persisted in DB
    Position fromDb = positionDao.findById("AAPL").orElseThrow();
    assertEquals(3, fromDb.getNumOfShares());
    assertEquals(30.0, fromDb.getValuePaid(), 0.000001);
  }

  @Test
  void sell_removesPosition() {
    Quote q = new Quote();
    q.setTicker("AAPL");
    q.setOpen(10.0);
    q.setHigh(12.0);
    q.setLow(9.5);
    q.setPrice(10.0);
    q.setVolume(100);
    q.setLatestTradingDay(java.sql.Date.valueOf(java.time.LocalDate.now()));
    q.setPreviousClose(9.8);
    q.setChange(0.2);
    q.setChangePercent("2.04%");
    q.setTimestamp(new java.sql.Timestamp(System.currentTimeMillis()));
    quoteDao.save(q);

    positionService.buy("AAPL", 2, 10.0);
    positionService.sell("AAPL");

    assertTrue(positionDao.findById("AAPL").isEmpty());
  }


}
