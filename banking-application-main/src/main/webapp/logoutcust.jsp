<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="jakarta.servlet.http.*,jakarta.servlet.*" %>
<%
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); 
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0); 
%>


<%
if (session == null || session.getAttribute("username") == null) {
	session.invalidate(); 
    
}
    response.sendRedirect("customerLogin.jsp"); // Redirect to the admin login page
%>
<script>
    history.forward();
</script>
