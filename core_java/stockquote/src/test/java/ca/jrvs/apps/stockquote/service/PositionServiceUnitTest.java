package ca.jrvs.apps.stockquote.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.jrvs.apps.stockquote.dao.CrudDao;
import ca.jrvs.apps.stockquote.dao.Position;
import ca.jrvs.apps.stockquote.dao.PositionDao;
import ca.jrvs.apps.stockquote.dao.Quote;
import ca.jrvs.apps.stockquote.dao.QuoteDao;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PositionServiceUnitTest {

  @Mock private CrudDao<Position, String> positionDao;
  @Mock private CrudDao<Quote, String> quoteDao;


  private PositionService service;

  @BeforeEach
  void setup() {
    service = new PositionService(positionDao, quoteDao);
  }


  @Test
  void buy_quoteNotFound_throws_andNoSave() {
    when(quoteDao.findById("AAPL")).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class,
        () -> service.buy("aapl", 1, 10.0));

    verify(positionDao, never()).save(any());
  }

  @Test
  void buy_exceedsVolume_throws_andNoSave() {
    Quote quote = new Quote();
    quote.setTicker("AAPL");
    quote.setVolume(5);

    when(quoteDao.findById("AAPL")).thenReturn(Optional.of(quote));

    assertThrows(IllegalArgumentException.class,
        () -> service.buy("aapl", 6, 10.0));

    verify(positionDao, never()).save(any());
  }

  @Test
  void buy_newPosition_savesAndReturns() {
    Quote quote = new Quote();
    quote.setTicker("AAPL");
    quote.setVolume(100);

    when(quoteDao.findById(eq("AAPL"))).thenReturn(Optional.of(quote));
    when(positionDao.findById(eq("AAPL"))).thenReturn(Optional.empty());

    when(positionDao.save(any(Position.class)))
        .thenAnswer(inv -> inv.getArgument(0));



    Position result = service.buy("aapl", 2, 10.0);

    assertEquals(2, result.getNumOfShares());
    assertEquals(20.0, result.getValuePaid(), 0.000001);

    verify(positionDao, times(1)).save(any(Position.class));
    service.buy("aapl", 2, 10.0);

    verify(quoteDao, times(2)).findById(anyString());

  }

  @Test
  void buy_existingPosition_accumulatesAndSaves() {
    // quote exists with enough volume
    Quote q = new Quote();
    q.setTicker("AAPL");
    q.setVolume(100);
    when(quoteDao.findById("AAPL")).thenReturn(Optional.of(q));

    // existing position in DB
    Position existing = new Position();
    existing.setTicker("AAPL");
    existing.setNumOfShares(3);
    existing.setValuePaid(30.0);
    when(positionDao.findById("AAPL")).thenReturn(Optional.of(existing));

    // save returns the same object (like a real dao)
    when(positionDao.save(any(Position.class))).thenAnswer(inv -> inv.getArgument(0));

    Position result = service.buy("aapl", 2, 10.0);

    assertEquals("AAPL", result.getTicker());
    assertEquals(5, result.getNumOfShares());                 // 3 + 2
    assertEquals(50.0, result.getValuePaid(), 0.000001);      // 30 + 20

    verify(positionDao, times(1)).save(any(Position.class));
  }

}
