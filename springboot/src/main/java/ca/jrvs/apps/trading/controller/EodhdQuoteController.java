package ca.jrvs.apps.trading.controller;

import ca.jrvs.apps.trading.model.EodhdQuote;
import ca.jrvs.apps.trading.service.EodhdQuoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/quote")
public class EodhdQuoteController {

  private static final Logger logger = LoggerFactory.getLogger(EodhdQuoteController.class);

  private final EodhdQuoteService eodhdQuoteService;

  /**
   * Dependency injection:
   * Spring will create and inject EodhdQuoteService automatically.
   */
  public EodhdQuoteController(EodhdQuoteService eodhdQuoteService) {
    this.eodhdQuoteService = eodhdQuoteService;
  }

  /**
   * GET /quote/eodhd/ticker/{ticker}
   *
   * Example:
   *   GET http://localhost:8080/quote/eodhd/ticker/AAPL.US
   *
   * Spring will:
   *  - read {ticker} from URL
   *  - pass it into this method as the "ticker" parameter
   *  - serialize returned EodhdQuote into JSON automatically
   */
  @GetMapping("/eodhd/ticker/{ticker}")
  public EodhdQuote getEodhdQuote(@PathVariable("ticker") String ticker) {
    logger.info("GET quote from EODHD ticker={}", ticker);
    return eodhdQuoteService.getQuote(ticker);
  }

  /**
   * PUT /quote/eodhd/ticker/{ticker}
   *
   * This endpoint fetches latest quote from EODHD and upserts it into DB.
   *
   * Example:
   *   PUT http://localhost:8080/quote/eodhd/ticker/AAPL.US
   */
  @PutMapping("/eodhd/ticker/{ticker}")
  public EodhdQuote putEodhdQuote(@PathVariable("ticker") String ticker) {
    logger.info("PUT quote to DB ticker={}", ticker);
    return eodhdQuoteService.putQuote(ticker);
  }
}
