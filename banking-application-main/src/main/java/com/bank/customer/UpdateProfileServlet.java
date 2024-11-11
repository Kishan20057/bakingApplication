package com.bank.customer;

import com.bank.dao.DatabaseConnection;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;

public class UpdateProfileServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Retrieve form data
        String fullName = request.getParameter("fullName");
        String address = request.getParameter("address");
        String mobileNo = request.getParameter("mobileNo");
        String emailId = request.getParameter("emailId");

        // Get current user's account number from session
        HttpSession session = request.getSession();
        String accountNo = (String) session.getAttribute("accountNo");

        // Basic validation
        if (fullName == null || address == null || mobileNo == null || emailId == null) {
            request.setAttribute("message", "All fields are required.");
            request.setAttribute("messageColor", "red");
            forwardToProfilePage(request, response);
            return;
        }

        // Perform database operations to update profile
        try (Connection con = DatabaseConnection.getConnection()) {
            // Prepare the update query
            String updateQuery = "UPDATE customer SET full_name=?, address=?, mobile_no=?, email_id=? WHERE account_no=?";
            try (PreparedStatement psUpdate = con.prepareStatement(updateQuery)) {
                psUpdate.setString(1, sanitizeInput(fullName));
                psUpdate.setString(2, sanitizeInput(address));
                psUpdate.setString(3, sanitizeInput(mobileNo));
                psUpdate.setString(4, sanitizeInput(emailId));
                psUpdate.setString(5, accountNo);

                int updatedRows = psUpdate.executeUpdate();

                if (updatedRows > 0) {
                    // Update session with new details if successful
                    session.setAttribute("fullName", fullName);
                    session.setAttribute("email", emailId);

                    // Set success message in request attribute
                    request.setAttribute("message", "Profile updated successfully!");
                    request.setAttribute("messageColor", "green");
                } else {
                    // Handle update failure
                    request.setAttribute("message", "Failed to update profile. Please try again.");
                    request.setAttribute("messageColor", "red");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Set error message and forward to profile page
            request.setAttribute("message", "Database error occurred. Please try again.");
            request.setAttribute("messageColor", "red");
        }

        // Forward to profile.jsp to display message
        forwardToProfilePage(request, response);
    }

    // Helper method to sanitize input to prevent SQL injection
    private String sanitizeInput(String input) {
        return input != null ? input.replaceAll("[^\\p{Print}]", "") : "";
    }

    // Helper method to forward to profile.jsp
    private void forwardToProfilePage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("profile.jsp");
        dispatcher.forward(request, response);
    }
}
