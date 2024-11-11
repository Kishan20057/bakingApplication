package com.bank.customer;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;

public class TransferMoneyServlet extends HttpServlet {

	private static final String URL = "jdbc:mysql://localhost:3306/bank_DBS";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    // Get a connection to the database
    private Connection getConnection() throws SQLException {
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

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Retrieve form data
        String recipientAccountNo = request.getParameter("recipientAccountNo");
        double amount = Double.parseDouble(request.getParameter("amount"));

        // Get current user's account number from session
        HttpSession session = request.getSession();
        String accountNo = (String) session.getAttribute("accountNo");

        if (recipientAccountNo == null || recipientAccountNo.trim().isEmpty() || amount <= 0) {
            response.sendRedirect("transferMoney.jsp?error=invalidInput");
            return;
        }

        Connection con = null;

        try {
            // Initialize connection
            con = getConnection();

            // Check if the recipient account exists
            if (!accountExists(con, recipientAccountNo)) {
                response.sendRedirect("transferMoney.jsp?error=recipientNotFound");
                return;
            }

            // Begin transaction
            con.setAutoCommit(false);

            // Retrieve and check current balance for the sender
            double senderBalance = getCurrentBalance(con, accountNo);

            // Check if the sender has sufficient balance
            if (senderBalance < amount) {
                response.sendRedirect("transferMoney.jsp?error=insufficientFunds");
                return;
            }

            // Deduct amount from sender's balance
            double newSenderBalance = senderBalance - amount;
            updateCurrentBalance(con, accountNo, newSenderBalance);

            // Retrieve current balance for the recipient
            double recipientBalance = getCurrentBalance(con, recipientAccountNo);

            // Add amount to recipient's balance
            double newRecipientBalance = recipientBalance + amount;
            updateCurrentBalance(con, recipientAccountNo, newRecipientBalance);

            // Insert into transaction table for sender (transfer out)
            insertTransaction(con, accountNo, "transfer out", -amount, newSenderBalance);

            // Insert into transaction table for recipient (transfer in)
            insertTransaction(con, recipientAccountNo, "transfer in", amount, newRecipientBalance);

            // Commit transaction
            con.commit();

            // Set success attribute to true
            request.setAttribute("successMessage", "Transfer of ₹" + amount + " to account " + recipientAccountNo + " was successful!");
            RequestDispatcher dispatcher = request.getRequestDispatcher("transferMoney.jsp");
            dispatcher.forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            response.sendRedirect("transferMoney.jsp?error=databaseError&message=" + e.getMessage());
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Helper method to check if an account exists
    private boolean accountExists(Connection con, String accountNo) throws SQLException {
        String query = "SELECT 1 FROM customer WHERE account_no=?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, accountNo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // Helper method to update current balance
    private void updateCurrentBalance(Connection con, String accountNo, double newBalance) throws SQLException {
        String query = "UPDATE customer SET current_balance = ? WHERE account_no = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setDouble(1, newBalance);
            ps.setString(2, accountNo);
            ps.executeUpdate();
        }
    }

    // Helper method to insert transaction record
    private void insertTransaction(Connection con, String accountNo, String type, double amount, double balanceAfterTransaction) throws SQLException {
        String query = "INSERT INTO transaction (account_no, date, type, amount, balance) VALUES (?, NOW(), ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, accountNo);
            ps.setString(2, type);
            ps.setDouble(3, amount);
            ps.setDouble(4, balanceAfterTransaction);
            ps.executeUpdate();
        }
    }

    // Helper method to get current balance from database
    private double getCurrentBalance(Connection con, String accountNo) throws SQLException {
        String query = "SELECT current_balance FROM customer WHERE account_no = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, accountNo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("current_balance");
                }
            }
        }
        return 0; // Return 0 if balance retrieval fails
    }
}