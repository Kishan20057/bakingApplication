package com.bank.admin;

import com.bank.dao.DatabaseConnection;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.UUID;

public class RegisterCustomerServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String fullName = request.getParameter("fullName");
        String address = request.getParameter("address");
        String mobileNo = request.getParameter("mobileNo");
        String emailId = request.getParameter("emailId");
        String accountType = request.getParameter("accountType");
        double initialBalance = Double.parseDouble(request.getParameter("initialBalance"));
        String dob = request.getParameter("dob");
        String idProof = request.getParameter("idProof");

        // Validate mobile number and ID proof
        if (mobileNoExists(mobileNo) || idProofExists(idProof)) {
            request.setAttribute("error", "Mobile number or ID proof already registered.");
            request.getRequestDispatcher("registerCustomer.jsp").forward(request, response);
            return;
        }

        // Validate age
        if (!isAdult(dob)) {
            request.setAttribute("error", "You must be at least 18 years old to register.");
            request.getRequestDispatcher("registerCustomer.jsp").forward(request, response);
            return;
        }

        // Generate account number and temporary password
        String accountNo = generateAccountNo();
        String tempPassword = "TEMP_" + UUID.randomUUID().toString().substring(0, 8);

        try (Connection con = DatabaseConnection.getConnection()) {
            // Insert customer details
            String sql = "INSERT INTO customer (full_name, address, mobile_no, email_id, account_type, current_balance, dob, id_proof, account_no, password) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, fullName);
                ps.setString(2, address);
                ps.setString(3, mobileNo);
                ps.setString(4, emailId);
                ps.setString(5, accountType);
                ps.setDouble(6, initialBalance);
                ps.setDate(7, Date.valueOf(dob));
                ps.setString(8, idProof);
                ps.setString(9, accountNo);
                ps.setString(10, tempPassword);
                ps.executeUpdate();
            }

            // Set attributes for success page
            request.setAttribute("accountNo", accountNo);
            request.setAttribute("tempPassword", tempPassword);

            // Forward to the success page
            RequestDispatcher dispatcher = request.getRequestDispatcher("registrationSuccess.jsp");
            dispatcher.forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("registerCustomer.jsp?error=database_error");
        }
    }

    private boolean mobileNoExists(String mobileNo) {
        String query = "SELECT 1 FROM customer WHERE mobile_no=?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, mobileNo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // Returns true if mobile number exists
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean idProofExists(String idProof) {
        String query = "SELECT 1 FROM customer WHERE id_proof=?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, idProof);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // Returns true if ID proof exists
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isAdult(String dob) {
        LocalDate birthDate = LocalDate.parse(dob);
        LocalDate currentDate = LocalDate.now();
        Period period = Period.between(birthDate, currentDate);
        return period.getYears() >= 18;
    }

    private String generateAccountNo() {
        // Generate a unique 12-digit account number
        return String.format("%012d", new java.util.Random().nextInt(999999999));
    }
}
