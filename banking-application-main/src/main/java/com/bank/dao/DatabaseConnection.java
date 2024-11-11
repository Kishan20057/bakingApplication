package com.bank.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/bank_DBS";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    // Get a connection to the database
    public static Connection getConnection() throws SQLException {
        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace(); // Log the driver loading error
            throw new SQLException("MySQL JDBC Driver not found", e);
        }
        // Return the database connection
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}