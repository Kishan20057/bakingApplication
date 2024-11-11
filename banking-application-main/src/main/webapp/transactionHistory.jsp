<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.sql.*, java.util.List, java.util.ArrayList, com.bank.customer.Transaction" %>
<%
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); 
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0); 
%>

<html>
<head>
    <title>Transaction History</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f2f2f2;
            margin: 0;
            padding: 0;
        }
        .container {
            max-width: 800px;
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
            background-color: #007bff;
            color: white;
        }
        .download-button {
            text-align: center;
            margin-top: 20px;
        }
        .download-button a {
            display: inline-block;
            padding: 10px 20px;
            background-color: #007bff;
            color: white;
            text-decoration: none;
            border-radius: 5px;
        }
        .download-button a:hover {
            background-color: #0056b3;
        }
    </style>
</head>
<body>
<div class="container">
    <h2>Transaction History</h2>
    <table>
        <thead>
            <tr>
                <th>Date</th>
                <th>Type</th>
                <th>Amount</th>
                <th>Balance</th>
            </tr>
        </thead>
        <tbody>
            <% 
                
                String accountNo = (String) session.getAttribute("accountNo");
                if (accountNo != null) {
                    List<Transaction> transactions = new ArrayList<>();
                    Connection con = null;
                    PreparedStatement ps = null;
                    ResultSet rs = null;

                    try {
                        // Database connection details
                        String url = "jdbc:mysql://localhost:3306/bank_DBS";
                        String user = "root";
                        String password = "root";

                        // Load MySQL JDBC driver
                        Class.forName("com.mysql.cj.jdbc.Driver");

                        // Establish connection
                        con = DriverManager.getConnection(url, user, password);

                        // Query to fetch transactions
                        String query = "SELECT * FROM transaction WHERE account_no = ? ORDER BY date DESC LIMIT 20";
                        ps = con.prepareStatement(query);
                        ps.setString(1, accountNo);
                        rs = ps.executeQuery();

                        // Process the result set
                        while (rs.next()) {
                            Transaction transaction = new Transaction();
                            transaction.setId(rs.getInt("id"));
                            transaction.setAccountNo(rs.getString("account_no"));
                            transaction.setDate(rs.getTimestamp("date"));
                            transaction.setType(rs.getString("type"));
                            transaction.setAmount(rs.getDouble("amount"));
                            transaction.setBalance(rs.getDouble("balance"));
                            transactions.add(transaction);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        request.setAttribute("errorMessage", "Error fetching transaction history. Please try again later.");
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

                    // Display transactions
                    for (Transaction transaction : transactions) {
            %>
                        <tr>
                            <td><%= transaction.getDate() %></td>
                            <td><%= transaction.getType() %></td>
                            <td><%= transaction.getAmount() %></td>
                            <td><%= transaction.getBalance() %></td>
                        </tr>
            <%
                    }
                } else {
            %>
                <tr>
                    <td colspan="4">No transactions found.</td>
                </tr>
            <%
                }
            %>
        </tbody>
    </table>
</div>
<script>
    history.forward();
</script>

</body>
</html>
