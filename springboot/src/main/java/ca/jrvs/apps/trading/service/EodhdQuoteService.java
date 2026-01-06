package ca.jrvs.apps.trading.service;

import ca.jrvs.apps.trading.dao.EodhdMarketDataDao;
import ca.jrvs.apps.trading.dao.QuoteDao;
import ca.jrvs.apps.trading.model.EodhdQuote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EodhdQuoteService {

  private static final Logger logger = LoggerFactory.getLogger(EodhdQuoteService.class);

  private final EodhdMarketDataDao marketDataDao;

  private final QuoteDao quoteDao;


  public EodhdQuoteService(EodhdMarketDataDao marketDataDao, QuoteDao quoteDao) {
    this.marketDataDao = marketDataDao;
    this.quoteDao = quoteDao;
  }

  /**
   * Get one quote by ticker.
   *
   * @param ticker e.g. "AAPL.US"
   * @return EodhdQuote
   */
  public EodhdQuote getQuote(String ticker) {
    // 1) Validate: service is the "gatekeeper" for external inputs.
    if (ticker == null || ticker.trim().isEmpty()) {
      logger.warn("Invalid ticker received: '{}'", ticker);
      throw new IllegalArgumentException("ticker must not be blank");
    }

    // 2) Normalize: make input consistent for downstream components.
    String normalizedTicker = ticker.trim().toUpperCase();

    // 3) Delegate: DAO handles HTTP and JSON deserialization.
    logger.info("Fetching EODHD quote for ticker={}", normalizedTicker);
    EodhdQuote quote = marketDataDao.findQuoteByTicker(normalizedTicker);

    // 4) Post-check + log: helpful for troubleshooting.
    logger.info("Service received quote: symbol={}, lastTradePrice={}, bid={}, ask={}",
        quote.getSymbol(), quote.getLastTradePrice(), quote.getBidPrice(), quote.getAskPrice());

    return quote;
  }

  public EodhdQuote putQuote(String ticker) {
    if (ticker == null || ticker.trim().isEmpty()) {
      logger.warn("Invalid ticker received: '{}'", ticker);
    }
    // 1. Fetch from external market data provider
    EodhdQuote quote = marketDataDao.findQuoteByTicker(ticker);

    // 2. Persist into database
    int rows = quoteDao.upsert(quote);
    logger.info("Upserted quote for ticker={}, affectedRows={}", ticker, rows);

    // 3. Return latest quote
    return quote;
  }

}
