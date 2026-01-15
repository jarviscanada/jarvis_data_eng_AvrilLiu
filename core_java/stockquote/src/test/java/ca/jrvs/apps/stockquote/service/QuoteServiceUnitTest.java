package ca.jrvs.apps.stockquote.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.jrvs.apps.stockquote.dao.Quote;
import ca.jrvs.apps.stockquote.dao.QuoteDao;
import ca.jrvs.apps.stockquote.dao.QuoteHttpHelper;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class QuoteServiceUnitTest {

  @Mock
  private ca.jrvs.apps.stockquote.dao.CrudDao<Quote, String> quoteDao;


  @Mock private QuoteFetcher quoteFetcher;


  @InjectMocks
  private QuoteService service;

  @Test
  void etchQuoteDataFromAPI_notFound_returnsEmpty_andNoSave() {
    when(quoteFetcher.fetchQuoteInfo("AAPL"))
        .thenThrow(new IllegalArgumentException("Quote not found"));

    Optional<Quote> result = service.fetchQuoteDataFromAPI("aapl");

    assertTrue(result.isEmpty());
    verify(quoteDao, never()).save(any());
  }


@Test
  void fetchQuoteDataFromAPI_found_savesAndReturns() {
  Quote quote = new Quote();
  quote.setTicker("AAPL");

  when(quoteFetcher.fetchQuoteInfo("AAPL")).thenReturn(quote);
  when(quoteDao.save(any(Quote.class))).thenAnswer(inv -> inv.getArgument(0));

  Optional<Quote> result = service.fetchQuoteDataFromAPI("aapl");

  assertTrue(result.isPresent());
  assertEquals("AAPL", result.get().getTicker());

  verify(quoteDao, times(1)).save(any(Quote.class));
  }

}
