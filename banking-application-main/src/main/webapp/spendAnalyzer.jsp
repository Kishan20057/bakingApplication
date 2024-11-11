<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="jakarta.servlet.http.*, jakarta.servlet.*, java.io.*, java.sql.*" %>
<%
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); 
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0); 
%>


<%
    // Check if user is logged in
    if (session == null || session.getAttribute("accountNo") == null) {
        response.sendRedirect("customerLogin.jsp");
        return;
    }

    String accountNo = (String) session.getAttribute("accountNo");
    Connection con = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/bank_DBS", "root", "root");

        // Query to aggregate spending by category
        String query = "SELECT type, SUM(amount) AS total FROM transaction WHERE account_no=? GROUP BY type";
        ps = con.prepareStatement(query);
        ps.setString(1, accountNo);
        rs = ps.executeQuery();
%>
<html>
<head>
    <title>Spend Analyzer</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f2f2f2;
            margin: 0;
            padding: 0;
        }
        .container {
            max-width: 600px;
            margin: 20px auto;
            padding: 20px;
            background-color: #fff;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
            border-radius: 5px;
        }
        h2 {
            text-align: center;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }
        table, th, td {
            border: 1px solid #ccc;
        }
        th, td {
            padding: 10px;
            text-align: left;
        }
        th {
            background-color: #007bff; /* Sky blue header */
            color: white;
        }
    </style>
</head>
<body>
<div class="container">
    <h2>Spend Analyzer</h2>
    <table>
        <thead>
            <tr>
                <th>Category</th>
                <th>Total Amount</th>
            </tr>
        </thead>
        <tbody>
            <% if (rs != null) { %>
                <% if (rs.next()) { %>
                    <% do { %>
                        <tr>
                            <td><%= rs.getString("type") %></td>
                            <td><%= rs.getDouble("total") %></td>
                        </tr>
                    <% } while (rs.next()); %>
                <% } else { %>
                    <tr>
                        <td colspan="2">No transactions found.</td>
                    </tr>
                <% } %>
            <% } else { %>
                <tr>
                    <td colspan="2">Error fetching data. Please try again later.</td>
                </tr>
            <% } %>
        </tbody>
    </table>
</div>
<script>
    history.forward();
</script>

</body>
</html>
<%
    } catch (Exception e) {
        e.printStackTrace();
        request.setAttribute("errorMessage", "An error occurred while fetching data. Please try again later.");
    } finally {
        // Close resources
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (con != null) con.close();
        } catch (SQLException e) {
            e.printStackTrace(); // Log closing errors
        }
    }
%>
