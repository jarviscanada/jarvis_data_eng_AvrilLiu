package ca.jrvs.apps.trading.model.config;

public class MarketDataConfig {

  private String baseUrl;
  private String token;

  public String getBaseUrl() {
    return baseUrl;
  }

  public void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }
}
