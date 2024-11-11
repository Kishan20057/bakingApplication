package com.bank.customer;

import com.bank.dao.DatabaseConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet("/changePassword")
public class ChangePasswordServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            String accountNo = (String) session.getAttribute("accountNo");
            String newPassword = request.getParameter("newPassword");

            if (accountNo == null || newPassword == null || newPassword.isEmpty()) {
                response.sendRedirect("changePassword.jsp?error=invalidInput");
                return;
            }

            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement("UPDATE customer SET password = ? WHERE account_no = ?")) {

                statement.setString(1, newPassword);
                statement.setString(2, accountNo);
                int rowsAffected = statement.executeUpdate();

                if (rowsAffected > 0) {
                    // Password updated successfully
                    response.sendRedirect("customerDashboard.jsp?message=passwordChanged");
                } else {
                    // Account not found or no change made
                    response.sendRedirect("changePassword.jsp?error=updateFailed");
                }

            } catch (SQLException e) {
                e.printStackTrace();
                throw new ServletException("Database connection error", e);
            }
        } else {
            // Session does not exist, redirect to login
            response.sendRedirect("customerLogin.jsp");
        }
    }
}
