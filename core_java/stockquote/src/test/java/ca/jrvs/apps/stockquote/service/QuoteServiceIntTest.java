package ca.jrvs.apps.stockquote.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import ca.jrvs.apps.stockquote.dao.Quote;
import ca.jrvs.apps.stockquote.dao.QuoteDao;
import ca.jrvs.apps.stockquote.util.ConnectionManager;
import java.sql.Connection;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class QuoteServiceIntTest {
  private static Connection connection;

  private QuoteDao quoteDao;
  private QuoteService quoteService;
  private QuoteFetcher quoteFetcher;

  @BeforeAll
  static void setupDB(){
    connection = ConnectionManager.getConnection();
  }

  @BeforeEach
  void setUp() {
    quoteDao = new QuoteDao(connection);
    quoteDao.deleteAll();

    quoteFetcher = Mockito.mock(QuoteFetcher.class);

    quoteService = new QuoteService(quoteDao, quoteFetcher);
  }

  @AfterAll
  static void tearDownDB()throws Exception {
    if (connection != null) {
      connection.close();
    }
  }

  @Test
  void fetchQuoteDataFromAPI_savesAndReturnsQuote() {
    Quote q = new Quote();
    q.setTicker("AAPL");
    q.setOpen(10.0);
    q.setHigh(12.0);
    q.setLow(9.5);
    q.setPrice(10.0);
    q.setVolume(100);
    q.setLatestTradingDay(java.sql.Date.valueOf(LocalDate.now()));
    q.setPreviousClose(9.8);
    q.setChange(0.2);
    q.setChangePercent("2.04%");
    q.setTimestamp(new Timestamp(System.currentTimeMillis()));

    when(quoteFetcher.fetchQuoteInfo("AAPL")).thenReturn(q);

    Optional<Quote> result = quoteService.fetchQuoteDataFromAPI("AAPL");

    assertTrue(result.isPresent());
    assertEquals("AAPL", result.get().getTicker());

    assertTrue(quoteDao.findById("AAPL").isPresent());
  }

  @Test
  void fetchQuoteDataFromAPI_notFound_returnsEmpty_andNoSave() {
    when(quoteFetcher.fetchQuoteInfo("AAPL")).thenThrow(new IllegalArgumentException("not found"));

    Optional<Quote> result = quoteService.fetchQuoteDataFromAPI("AAPL");

    assertTrue(result.isEmpty());
    assertTrue(quoteDao.findById("AAPL").isEmpty());
  }
}
