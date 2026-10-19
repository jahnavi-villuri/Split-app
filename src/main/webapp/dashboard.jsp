<%@ page language="java" contentType="text/html; charset=UTF-8" import="java.util.*, java.sql.*" %>
<%@ page import="com.splitapp.db.DBConnection" %>
<html>
<head>
    <title>Dashboard - SplitApp</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <div class="navbar">
        <span class="brand">💰 SplitApp</span>
        <div><a href="login.jsp">Logout</a></div>
    </div>
    <div class="container wide">
        <h2>Welcome, ${sessionScope.userName} 👋</h2>

        <form action="createGroup" method="post">
            <label>New Group Name</label>
            <input type="text" name="groupName" placeholder="e.g. Goa Trip" required/>
            <input type="submit" value="Create Group"/>
        </form>

        <h2 style="margin-top:32px;">Your Groups</h2>
        <%
            Integer userId = (Integer) session.getAttribute("userId");
            if (userId != null) {
                String sql = "SELECT g.group_id, g.group_name FROM groups_table g " +
                             "JOIN group_members gm ON g.group_id = gm.group_id " +
                             "WHERE gm.user_id = ?";
                try (Connection con = DBConnection.getConnection();
                     PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setInt(1, userId);
                    ResultSet rs = ps.executeQuery();
                    boolean any = false;
        %>
        <table>
            <tr><th>Group Name</th><th>Action</th></tr>
        <%
                    while (rs.next()) {
                        any = true;
        %>
            <tr>
                <td><%= rs.getString("group_name") %></td>
                <td><a href="viewBalance?groupId=<%= rs.getInt("group_id") %>">View Balances</a></td>
            </tr>
        <%
                    }
                    if (!any) {
        %>
            <tr><td colspan="2" style="text-align:center; color:#a1815c;">No groups yet — create one above.</td></tr>
        <%
                    }
        %>
        </table>
        <%
                } catch (Exception e) {
                    e.printStackTrace();
        %>
            <p class="error">Error loading groups: <%= e.getMessage() %></p>
        <%
                }
            }
        %>
    </div>
</body>
</html>