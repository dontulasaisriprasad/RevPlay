package com.revplay.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DBUtil {
    private static final Logger logger = LogManager.getLogger(DBUtil.class);
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:orcl";
    private static final String DB_USER = "rev_play";
    private static final String DB_PASSWORD = "revplay123";
    private static Connection connection = null;

    static {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            logger.info("Oracle JDBC Driver loaded successfully");
        } catch (ClassNotFoundException e) {
            logger.error("Oracle JDBC Driver not found", e);
            throw new RuntimeException("Failed to load Oracle JDBC Driver", e);
        }
    }

    private DBUtil() {
        // Private constructor to prevent instantiation
    }

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            logger.debug("Database connection established");
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                logger.debug("Database connection closed");
            } catch (SQLException e) {
                logger.error("Error closing database connection", e);
            }
        }
    }

    public static void rollbackTransaction(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
                logger.debug("Transaction rolled back");
            } catch (SQLException e) {
                logger.error("Error rolling back transaction", e);
            }
        }
    }

    public static void setAutoCommit(Connection conn, boolean autoCommit) {
        if (conn != null) {
            try {
                conn.setAutoCommit(autoCommit);
                logger.debug("AutoCommit set to: " + autoCommit);
            } catch (SQLException e) {
                logger.error("Error setting auto commit", e);
            }
        }
    }

    public static void commitTransaction(Connection conn) {
        if (conn != null) {
            try {
                conn.commit();
                logger.debug("Transaction committed");
            } catch (SQLException e) {
                logger.error("Error committing transaction", e);
                rollbackTransaction(conn);
            }
        }
    }
}