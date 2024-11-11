package com.bank.customer;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.itextpdf.text.Document;

import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * Servlet implementation class DownloadTransactionsServlet
 */
@WebServlet("/DownloadTransactionsServlet")
public class DownloadTransactionsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accountNo = request.getParameter("account_no");

        List<String> transactions = new ArrayList<>();

        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            // Database connection
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bank_DBS", "root", "root");
            pst = conn.prepareStatement("SELECT * FROM Transactions WHERE account_no = ?");
            pst.setString(1, accountNo);
            rs = pst.executeQuery();

            // Populate transactions list with transaction details
            while (rs.next()) {
                Timestamp timestamp = rs.getTimestamp("transaction_time");
                String type = rs.getString("transaction_type");
                double amount = rs.getDouble("amount");

                String transactionInfo = "Timestamp: " + timestamp + ", Type: " + type + ", Amount: " + amount;
                transactions.add(transactionInfo);
            }

            // Generate PDF document
            response.setContentType("application/pdf");
            response.setHeader("Content-disposition", "attachment; filename=transactions.pdf");

            Document document = new Document();
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            // Add transaction details to PDF
            for (String transaction : transactions) {
                document.add(new Paragraph(transaction));
            }

            document.close();

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error generating PDF: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pst != null) pst.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}