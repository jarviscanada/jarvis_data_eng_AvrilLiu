package ca.jrvs.apps.stockquote.service;

import ca.jrvs.apps.stockquote.dao.Quote;

public interface QuoteFetcher {
  Quote fetchQuoteInfo(String symbol) throws IllegalArgumentException;
}
