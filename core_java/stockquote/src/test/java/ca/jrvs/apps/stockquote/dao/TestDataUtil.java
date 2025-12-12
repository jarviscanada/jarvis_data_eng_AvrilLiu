package ca.jrvs.apps.stockquote.dao;


import java.sql.Date;
import java.sql.Timestamp;

public class TestDataUtil {

  public static Quote sampleQuote(String symbol) {
    Quote q = new Quote();
    q.setTicker(symbol);
    q.setOpen(100);
    q.setHigh(110);
    q.setLow(90);
    q.setPrice(105);
    q.setVolume(1000);
    q.setLatestTradingDay(Date.valueOf("2025-01-01"));
    q.setPreviousClose(98);
    q.setChange(7);
    q.setChangePercent("7%");
    q.setTimestamp(new Timestamp(System.currentTimeMillis()));
    return q;
  }
}
