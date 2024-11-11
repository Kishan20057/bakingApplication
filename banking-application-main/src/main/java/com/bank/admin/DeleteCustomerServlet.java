package com.bank.admin;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;

import com.bank.dao.DatabaseConnection;

public class DeleteCustomerServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accountNo = request.getParameter("accountNo");

        if (accountNo == null || accountNo.trim().isEmpty()) {
            response.sendRedirect("deleteCustomer.jsp?error=missingAccountNo");
            return;
        }

        try (Connection con = DatabaseConnection.getConnection()) {
            // Check if the balance is zero
            String checkBalanceQuery = "SELECT current_balance FROM customer WHERE account_no=?";
            try (PreparedStatement checkBalanceStmt = con.prepareStatement(checkBalanceQuery)) {
                checkBalanceStmt.setString(1, accountNo);
                try (ResultSet rs = checkBalanceStmt.executeQuery()) {
                    if (rs.next()) {
                        double balance = rs.getDouble("current_balance");
                        if (balance == 0) {
                            // Delete the customer
                            String deleteQuery = "DELETE FROM customer WHERE account_no=?";
                            try (PreparedStatement deleteStmt = con.prepareStatement(deleteQuery)) {
                                deleteStmt.setString(1, accountNo);
                                int rowsAffected = deleteStmt.executeUpdate();
                                
                                if (rowsAffected > 0) {
                                    response.sendRedirect("adminDashboard.jsp?success=1");
                                } else {
                                    response.sendRedirect("deleteCustomer.jsp?error=deleteFailed");
                                }
                            }
                        } else {
                            response.sendRedirect("deleteCustomer.jsp?error=balance_not_zero");
                        }
                    } else {
                        response.sendRedirect("deleteCustomer.jsp?error=account_not_found");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("deleteCustomer.jsp?error=databaseError");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("deleteCustomer.jsp?error=generalError");
        }
    }
}
