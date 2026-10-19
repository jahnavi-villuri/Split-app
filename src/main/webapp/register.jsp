<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<html>
<head>
    <title>Register - SplitApp</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <div class="container">
        <h2>Create Account 🌻</h2>
        <% if (request.getAttribute("error") != null) { %>
            <div class="error"><%= request.getAttribute("error") %></div>
        <% } %>
        <form action="register" method="post">
            <label>Name</label>
            <input type="text" name="name" required/>
            <label>Email</label>
            <input type="email" name="email" required/>
            <label>Password</label>
            <input type="password" name="password" required/>
            <input type="submit" value="Register"/>
        </form>
        <p class="link-row">Already have an account? <a href="login.jsp">Login</a></p>
    </div>
</body>
</html>