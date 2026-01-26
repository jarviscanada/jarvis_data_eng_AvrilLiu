package ca.jrvs.apps.trading.config;

import ca.jrvs.apps.trading.model.config.MarketDataConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MarketDataConfigProperties {

  /**
   * Bind properties under "marketdata.eodhd" into a MarketDataConfig object.
   * Example:
   * marketdata:
   *   eodhd:
   *     base-url: ...
   *     token: ...
   */
  @Bean
  @ConfigurationProperties(prefix = "marketdata.eodhd")
  public MarketDataConfig marketDataConfig() {
    return new MarketDataConfig();
  }
}
