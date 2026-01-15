package ca.jrvs.apps.stockquote.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {

  public static Connection getConnection() {
    try {
      String host = System.getenv("PG_HOST");
      String port = System.getenv("PG_PORT");
      String db = System.getenv("PG_DB");
      String user = System.getenv("PG_USER");
      String password = System.getenv("PG_PASSWORD");

      String url = String.format(
          "jdbc:postgresql://%s:%s/%s",
          host, port, db
      );

      return DriverManager.getConnection(url, user, password);
    } catch (SQLException e) {
      throw new IllegalStateException("Failed to connect to PostgreSQL", e);
    }
  }
}
