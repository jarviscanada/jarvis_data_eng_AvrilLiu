package ca.jrvs.apps.stockquote.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.jrvs.apps.stockquote.util.ConnectionManager;
import java.sql.Connection;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PositionDaoTest {

  private Connection connection;
  private QuoteDao quoteDao;
  private PositionDao positionDao;

  @BeforeEach
  void setUp() {
    ConnectionManager cm = new ConnectionManager();
    connection = cm.getConnection();

    quoteDao = new QuoteDao(connection);
    positionDao = new PositionDao(connection);

    // Clean tables in correct order (child -> parent)
    positionDao.deleteAll();
    quoteDao.deleteAll();
  }

  @AfterEach
  void tearDown() throws Exception {
    if (connection != null && !connection.isClosed()) {
      connection.close();
    }
  }

  @Test
  void saveAndFindById() {
    // 1. Insert quote first (foreign key requirement)
    Quote quote = TestDataUtil.sampleQuote("MSFT");
    quoteDao.save(quote);

    // 2. Insert position
    Position position = new Position();
    position.setTicker("MSFT");
    position.setNumOfShares(10);
    position.setValuePaid(3200.00);

    positionDao.save(position);

    // 3. Find position
    Optional<Position> result = positionDao.findById("MSFT");

    assertTrue(result.isPresent());
    assertEquals(10, result.get().getNumOfShares());
    assertEquals(3200.00, result.get().getValuePaid());
  }

  @Test
  void findAll() {
    Quote quote = TestDataUtil.sampleQuote("AAPL");
    quoteDao.save(quote);

    Position p = new Position();
    p.setTicker("AAPL");
    p.setNumOfShares(5);
    p.setValuePaid(1000.00);

    positionDao.save(p);

    Iterable<Position> positions = positionDao.findAll();

    assertTrue(positions.iterator().hasNext());
  }

  @Test
  void deleteById() {
    Quote quote = TestDataUtil.sampleQuote("GOOG");
    quoteDao.save(quote);

    Position p = new Position();
    p.setTicker("GOOG");
    p.setNumOfShares(3);
    p.setValuePaid(900.00);

    positionDao.save(p);
    positionDao.deleteById("GOOG");

    Optional<Position> result = positionDao.findById("GOOG");
    assertTrue(result.isEmpty());
  }
}
