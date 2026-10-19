<%@ page language="java" contentType="text/html; charset=UTF-8" import="java.sql.*, java.util.*" %>
<%@ page import="com.splitapp.db.DBConnection" %>
<html>
<head>
    <title>Add Expense</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <div class="navbar">
        <span class="brand">💰 SplitApp</span>
        <div>
            <a href="dashboard.jsp">Dashboard</a>
            <a href="login.jsp">Logout</a>
        </div>
    </div>

    <div class="container">
        <h2>Add Expense</h2>
        <%
            int groupId = Integer.parseInt(request.getParameter("groupId"));
        %>
        <form action="addExpense" method="post">
            <input type="hidden" name="groupId" value="<%= groupId %>" />

            <label>Description</label>
            <input type="text" name="description" placeholder="e.g. Dinner at cafe" required />

            <label>Amount (₹)</label>
            <input type="number" name="amount" step="0.01" placeholder="e.g. 900" required />

            <label>Was this spent on everyone in the group?</label>
            <p style="font-size:13px; color:#8a6f52; margin-top:-6px;">
                Uncheck anyone who wasn't included in this expense.
            </p>
            <div class="checkbox-group">
                <%
                    String sql = "SELECT u.user_id, u.name FROM users u " +
                                 "JOIN group_members gm ON u.user_id = gm.user_id " +
                                 "WHERE gm.group_id = ?";
                    try (Connection con = DBConnection.getConnection();
                         PreparedStatement ps = con.prepareStatement(sql)) {
                        ps.setInt(1, groupId);
                        ResultSet rs = ps.executeQuery();
                        while (rs.next()) {
                            int uid = rs.getInt("user_id");
                            String uname = rs.getString("name");
                %>
                <label class="checkbox-item">
                    <input type="checkbox" name="members" value="<%= uid %>" checked /> <%= uname %>
                </label>
                <%
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                %>
                <p class="error">Could not load group members: <%= e.getMessage() %></p>
                <%
                    }
                %>
            </div>

            <input type="submit" value="Add Expense" />
        </form>
    </div>
</body>
</html>