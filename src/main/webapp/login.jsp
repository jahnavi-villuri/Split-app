<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<html>
<head>
    <title>Login - SplitApp</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <div class="container">
        <h2>Welcome Back 🍂</h2>
        <% if (request.getAttribute("error") != null) { %>
            <div class="error"><%= request.getAttribute("error") %></div>
        <% } %>
        <form action="login" method="post">
            <label>Email</label>
            <input type="email" name="email" required/>
            <label>Password</label>
            <input type="password" name="password" required/>
            <input type="submit" value="Login"/>
        </form>
        <p class="link-row">New here? <a href="register.jsp">Create an account</a></p>
    </div>
</body>
</html>