package ca.jrvs.apps.stockquote.dao;

import ca.jrvs.apps.stockquote.util.ConnectionManager;
import org.junit.jupiter.api.*;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class QuoteDaoTest {

  private Connection connection;
  private QuoteDao quoteDao;
  private PositionDao positionDao;
  @BeforeEach
  void setUp() {
    // Create a real DB connection for integration test
    ConnectionManager cm = new ConnectionManager();
    connection = cm.getConnection();
    quoteDao = new QuoteDao(connection);

    positionDao = new PositionDao(connection);

    // Clean table to make tests deterministic
    positionDao.deleteAll();
    quoteDao.deleteAll();
  }

  @AfterEach
  void tearDown() throws Exception {
    // Close the connection created in this test
    if (connection != null && !connection.isClosed()) {
      connection.close();
    }
  }

  @Test
  void saveAndFindById_shouldWork() {
    Quote q = sampleQuote("MSFT");
    quoteDao.save(q);

    Optional<Quote> actual = quoteDao.findById("MSFT");
    assertTrue(actual.isPresent());

    Quote a = actual.get();
    assertEquals("MSFT", a.getTicker());
    assertEquals(q.getPrice(), a.getPrice(), 0.0001);
    assertEquals(q.getVolume(), a.getVolume());
    assertEquals(q.getLatestTradingDay(), a.getLatestTradingDay());
  }

  @Test
  void findAll_shouldReturnAllRows() {
    quoteDao.save(sampleQuote("MSFT"));
    quoteDao.save(sampleQuote("AAPL"));

    int count = 0;
    for (Quote ignored : quoteDao.findAll()) {
      count++;
    }
    assertEquals(2, count);
  }

  @Test
  void deleteById_shouldRemoveRow() {
    quoteDao.save(sampleQuote("MSFT"));

    quoteDao.deleteById("MSFT");

    Optional<Quote> actual = quoteDao.findById("MSFT");
    assertTrue(actual.isEmpty());
  }

  @Test
  void deleteAll_shouldRemoveAllRows() {
    quoteDao.save(sampleQuote("MSFT"));
    quoteDao.save(sampleQuote("AAPL"));

    quoteDao.deleteAll();

    int count = 0;
    for (Quote ignored : quoteDao.findAll()) {
      count++;
    }
    assertEquals(0, count);
  }

  // Create a fixed Quote for deterministic testing (no API calls)
  private Quote sampleQuote(String symbol) {
    Quote q = new Quote();
    q.setTicker(symbol);
    q.setOpen(100.00);
    q.setHigh(110.00);
    q.setLow(90.00);
    q.setPrice(105.00);
    q.setVolume(12345);
    q.setLatestTradingDay(java.sql.Date.valueOf("2025-12-10"));
    q.setPreviousClose(98.00);
    q.setChange(7.00);
    q.setChangePercent("7.0%");
    q.setTimestamp(new Timestamp(System.currentTimeMillis()));
    return q;
  }
}
