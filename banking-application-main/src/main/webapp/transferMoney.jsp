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
        return;
    }

    String accountNo = (String) session.getAttribute("accountNo");
    String fullName = "";
    double balance = 0.0;

    try (Connection con = DatabaseConnection.getConnection();
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
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Transfer Money</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f2f2f2;
            margin: 0;
            padding: 0;
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
        form {
            display: flex;
            flex-direction: column;
        }
        label {
            margin-bottom: 10px;
        }
        input[type="text"], input[type="number"] {
            padding: 10px;
            margin-bottom: 15px;
            border: 1px solid #ccc;
            border-radius: 3px;
            font-size: 14px;
            width: 100%;
        }
        input[type="submit"] {
            padding: 10px;
            background-color: #007bff;
            color: white;
            border: none;
            border-radius: 3px;
            cursor: pointer;
            font-size: 14px;
        }
        input[type="submit"]:hover {
            background-color: #0056b3;
        }
        .success-message {
            color: green;
            text-align: center;
            margin-bottom: 10px;
        }
        .error-message {
            color: red;
            text-align: center;
            margin-bottom: 10px;
        }
        .gif-container {
            text-align: center;
            margin-top: 20px;
        }
    </style>
</head>
<body>
<div class="navbar">
    <a href="customerDashboard.jsp" class="logo"><img src="logo1.png" alt="Logo"></a>
    <a href="logoutcust.jsp" class="logout">Logout</a>
</div>

<div class="container">
    <%-- Display success message if it exists --%>
    <% if (request.getAttribute("successMessage") != null) { %>
        <div class="success-message">
            <p><%= request.getAttribute("successMessage") %></p>
            <img src="success.gif" alt="Success GIF">
            <audio id="successSound" src="success-sound.mp3"></audio>
        </div>
    <% } %>

    <%-- Display error message if present --%>
    <% if (request.getParameter("error") != null) {
        String error = request.getParameter("error");
        if (error.equals("recipientNotFound")) { %>
            <div class="error-message">
                Recipient account not found
            </div>
        <% } else if (error.equals("insufficientFunds")) { %>
            <div class="error-message">
                Insufficient funds for this transfer
            </div>
        <% } else if (error.equals("transactionError")) { %>
            <div class="error-message">
                An error occurred during the transaction. Please try again later.
            </div>
        <% } else if (error.equals("databaseError")) { %>
            <div class="error-message">
                Database error occurred. Please try again later.
            </div>
        <% } %>
    <% } %>

    <div class="form-container">
        <h2>Transfer Money</h2>
        <p>Current Balance: ₹<%= balance %></p>
        <form action="TransferMoneyServlet" method="post">
            <label for="recipientAccountNo">Recipient Account Number</label>
            <input type="text" id="recipientAccountNo" name="recipientAccountNo" required>

            <label for="amount">Amount to Transfer</label>
            <input type="number" id="amount" name="amount" min="1" step="any" required>

            <input type="submit" value="Transfer">
        </form>
    </div>
</div>

<script>
    // Check if success message exists and play sound
    <% if (request.getAttribute("successMessage") != null) { %>
        var successSound = document.getElementById('successSound');
        successSound.play().catch(function(error) {
            console.error('Autoplay was prevented:', error);
        });
    <% } %>
</script>
<script>
    history.forward();
</script>

</body>
</html>
