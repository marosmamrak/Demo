package com.marosmamrak.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {
    public static void createUsersTable(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            String createTableSQL = "CREATE TABLE IF NOT EXISTS USERS (" +
                    "USER_ID INT PRIMARY KEY," +
                    "USER_GUID VARCHAR(255)," +
                    "USER_NAME VARCHAR(255)" +
                    ")";
            statement.executeUpdate(createTableSQL);
        } catch (SQLException e) {
            if ("X0Y32".equals(e.getSQLState())) {
                // Table already exists, do nothing
                System.out.println("Table USERS already exists.");
            } else {
                // Other SQLException, rethrow
                throw e;
            }
        }
    }
}
