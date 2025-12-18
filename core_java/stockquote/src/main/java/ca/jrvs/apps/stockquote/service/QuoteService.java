package ca.jrvs.apps.stockquote.service;
import ca.jrvs.apps.stockquote.dao.CrudDao;

import ca.jrvs.apps.stockquote.dao.Quote;
import ca.jrvs.apps.stockquote.dao.QuoteDao;
import java.util.Optional;

public class QuoteService {
  private final CrudDao<Quote, String> dao;
  private final QuoteFetcher quoteFetcher;

  public QuoteService(CrudDao<Quote, String> dao, QuoteFetcher quoteFetcher) {
    this.dao = dao;
    this.quoteFetcher = quoteFetcher;
  }



  public Optional<Quote> fetchQuoteDataFromAPI(String ticker){
    String symbol =normalizeTicker(ticker);
    try{
      Quote quote = quoteFetcher.fetchQuoteInfo(symbol);

      // Make sure the ticker stored is normalized
      quote.setTicker(symbol);

      dao.save(quote);
      return Optional.of(quote);
    }catch (IllegalArgumentException e){
      return Optional.empty();
    }
  }

  private String normalizeTicker(String ticker) {
    if (ticker == null) {
      throw new IllegalArgumentException("Ticker cannot be null");
    }
    String symbol = ticker.trim().toUpperCase();
    if (symbol.isBlank()){
      throw new IllegalArgumentException("Ticker cannot be blank");
    }
    return symbol;
  }
}


