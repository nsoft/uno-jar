package com.needhamsoftware.unojar;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class LibHibernateTest {
  public void test() {
    try {
      Class.forName("org.hsqldb.jdbc.JDBCDriver");
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }

    // initialize database
    try {
      init();

    System.err.println("System Err Success - library class");

      destroy();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private  void init() throws SQLException {
    try (Connection connection = getConnection(); Statement statement = connection.createStatement();) {
      statement.execute("CREATE TABLE Thing (id INT NOT NULL, name VARCHAR(50) NOT NULL, PRIMARY KEY (id))");
      connection.commit();
      statement.executeUpdate("INSERT INTO employee VALUES (1001,'Some Thing')");
      connection.commit();
    }
  }

  private  Connection getConnection() throws SQLException {
    return DriverManager.getConnection("jdbc:hsqldb:mem:things", "testuser", "testpass");
  }

  public void destroy() throws SQLException {
    try (Connection connection = getConnection(); Statement statement = connection.createStatement();) {
      statement.executeUpdate("DROP TABLE Thing");
      connection.commit();
    }
  }
}
