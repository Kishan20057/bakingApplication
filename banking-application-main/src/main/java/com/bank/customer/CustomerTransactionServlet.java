package com.bank.customer;

import com.bank.dao.DatabaseConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.RequestDispatcher;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/customerTransaction")
public class CustomerTransactionServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Retrieve form data
        String transactionType = request.getParameter("type");
        double amount = Double.parseDouble(request.getParameter("amount"));

        // Get current user's account number from session
        HttpSession session = request.getSession();
        String accountNo = (String) session.getAttribute("accountNo");

        try (Connection con = DatabaseConnection.getConnection()) {
            con.setAutoCommit(false); // Start transaction

            try {
                // Retrieve current balance
                double currentBalance = getCurrentBalance(con, accountNo);

                if ("Deposit".equals(transactionType)) {
                    double newBalance = currentBalance + amount;
                    updateBalance(con, accountNo, newBalance);
                    insertTransaction(con, accountNo, "Deposit", amount, newBalance);
                    request.setAttribute("successMessage", "Deposit of ₹" + amount + " successful!");

                } else if ("Withdraw".equals(transactionType)) {
                    if (currentBalance >= amount) {
                        double newBalance = currentBalance - amount;
                        updateBalance(con, accountNo, newBalance);
                        insertTransaction(con, accountNo, "Withdraw", amount, newBalance);
                        request.setAttribute("successMessage", "Withdrawal of ₹" + amount + " successful!");
                    } else {
                        request.setAttribute("errorMessage", "Insufficient balance for withdrawal.");
                    }
                } else {
                    request.setAttribute("errorMessage", "Invalid transaction type.");
                }

                con.commit(); // Commit transaction

            } catch (SQLException e) {
                con.rollback(); // Rollback transaction on error
                e.printStackTrace();
                request.setAttribute("errorMessage", "Database error occurred. Please try again.");
            }

            // Forward to transaction.jsp to display message
            RequestDispatcher dispatcher = request.getRequestDispatcher("transaction.jsp");
            dispatcher.forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle SQL exceptions here
            request.setAttribute("errorMessage", "Database connection error. Please try again.");
            RequestDispatcher dispatcher = request.getRequestDispatcher("transaction.jsp");
            dispatcher.forward(request, response);
        }
    }

    private double getCurrentBalance(Connection con, String accountNo) throws SQLException {
        String query = "SELECT current_balance FROM customer WHERE account_no=?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, accountNo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("current_balance");
                }
                return 0.0; // Default to 0 if no balance found
            }
        }
    }

    private void updateBalance(Connection con, String accountNo, double newBalance) throws SQLException {
        String query = "UPDATE customer SET current_balance=? WHERE account_no=?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setDouble(1, newBalance);
            ps.setString(2, accountNo);
            ps.executeUpdate();
        }
    }

    private void insertTransaction(Connection con, String accountNo, String type, double amount, double balanceAfterTransaction) throws SQLException {
        String query = "INSERT INTO transaction (account_no, type, amount, balance) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, accountNo);
            ps.setString(2, type);
            ps.setDouble(3, amount);
            ps.setDouble(4, balanceAfterTransaction);
            ps.executeUpdate();
        }
    }
}
