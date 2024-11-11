<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="jakarta.servlet.http.*, jakarta.servlet.*, java.io.*, java.sql.*, com.bank.dao.DatabaseConnection" %>
<%
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); 
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0); 
%>


<%
    // Check if user is logged in
    
    if (session == null || session.getAttribute("accountNo") == null) {
        response.sendRedirect("customerLogin.jsp");
        return; // Stop further execution
    }

    String accountNo = (String) session.getAttribute("accountNo");

    // Initialize variables for user details
    String fullName = "";
    double balance = 0.0;

    try (Connection con = DatabaseConnection.getConnection(); // Use the DatabaseConnection utility
         PreparedStatement ps = con.prepareStatement("SELECT full_name, current_balance FROM customer WHERE account_no = ?")) {
        
        ps.setString(1, accountNo);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                fullName = rs.getString("full_name");
                balance = rs.getDouble("current_balance");
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
        request.setAttribute("errorMessage", "Database error occurred while fetching details.");
        request.getRequestDispatcher("error.jsp").forward(request, response);
        return; // Stop further execution
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Customer Dashboard</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f2f2f2;
            margin: 0;
            padding: 0;
            background-size: cover;
            position: relative;
        }
        .overlay {
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background-color: rgba(255, 255, 255, 0.8);
            display: flex;
            flex-direction: column;
        }
        .navbar {
            overflow: hidden;
            background-color: rgba(0, 123, 255, 0.7);
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 14px 20px;
        }
        .navbar .logo img {
            height: 30px;
            width: auto;
        }
        .navbar .logout {
            display: block;
            background-color: black;
            color: white;
            padding: 10px 20px;
            text-decoration: none;
            border-radius: 3px;
        }
        .navbar .logout:hover {
            background-color: #0056b3;
        }
        .container {
            max-width: 900px;
            margin: 20px auto;
            padding: 20px;
            background: url('') no-repeat center center fixed;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
            border-radius: 5px;
        }
        h2 {
            text-align: center;
        }
        .info {
            margin-bottom: 20px;
        }
        .buttons {
            margin-top: 50px;
            text-align: center;
            display: flex;
            flex-wrap: wrap;
            justify-content: center;
        }
        .buttons .button-container {
            display: flex;
            flex-direction: column;
            align-items: center;
            padding: 20px;
            border-radius: 10px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
            width: 250px;
            margin: 10px;
            background-color: rgba(252, 249, 251, 0.92);
        }
        .buttons .button-container img {
            height: 150px;
            width: 200px;
            margin-bottom: 15px;
        }
        .buttons .button-container a {
            background-color: #007bff;
            color: #fff;
            padding: 15px 20px;
            text-decoration: none;
            border-radius: 5px;
            text-align: center;
            width: 100%;
            font-size: 16px;
            margin:10px;
        }
        .buttons .button-container a:hover {
            background-color: #0056b3;
            transform: scale(1.1);
            transition: transform 0.3s;
        }
    </style>
    <script>
    window.history.pushState(null, "", window.location.href);
    window.onpopstate = function() {
        window.history.pushState(null, "", window.location.href);
    };
    </script>
</head>
<body>
<div class="navbar">
    <a href="#" class="logo"><img src="logo1.png" alt="Logo"></a>
    <a href="logoutcust.jsp" class="logout">Logout</a>
</div>

<div class="container">
    <h2>Welcome, <%= fullName %></h2>
    <div class="info">
        <p><strong>Account Number:</strong> <%= accountNo %></p>
        <p><strong>Balance:</strong> ₹<%= balance %></p>
    </div>
    <div class="buttons">
        <div class="button-container">
            <img src="dep.gif" alt="Deposit" height="150" width="200">
            <a href="transaction.jsp">Deposit/Withdraw</a>
        </div>
        <div class="button-container">
            <img src="up.gif" alt="Update Profile" height="150" width="200">
            <a href="profile.jsp">Update Profile Preference</a>
        </div>
        <div class="button-container">
            <img src="transach.gif" alt="Transaction History" height="150" width="200">
            <a href="transactionHistory.jsp">Transaction History</a>
        </div>
        <div class="button-container">
            <img src="trans.gif" alt="Transfer Money" height="150" width="200">
            <a href="transferMoney.jsp">Transfer Money</a>
        </div>
        <div class="button-container">
            <img src="spen.gif" alt="Spend Analyzer" height="150" width="200">
            <a href="spendAnalyzer.jsp">Spend Analyzer</a>
        </div>
        <div class="button-container">
            <img src="del.gif" alt="Delete Account" height="150" width="150">
            <a href="deleteAccount.jsp">Delete Account</a>
        </div>
    </div>
</div>
<script>
    history.forward();
</script>


</body>
</html>
