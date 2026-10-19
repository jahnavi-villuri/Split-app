<%@ page language="java" contentType="text/html; charset=UTF-8" import="java.util.*" %>
<html>
<head>
    <title>Settle Up - SplitApp</title>
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
    <div class="container wide">
        <h2>Settle Up 🤝</h2>
        <p style="text-align:center; color:#8a6f52; margin-top:-8px;">
            Minimum transactions needed to clear all debts in this group
        </p>

        <% if (request.getAttribute("error") != null) { %>
            <div class="error"><%= request.getAttribute("error") %></div>
        <% } %>
        <% if ("1".equals(request.getParameter("error"))) { %>
            <div class="error">Could not record payment. Please try again.</div>
        <% } %>
        <% if ("paid".equals(request.getParameter("status"))) { %>
            <div class="error" style="background:#e3f0e3; color:#3a6b3a; border-left-color:#4f8a5b;">
                ✅ Payment recorded successfully!
            </div>
        <% } %>

        <%
            @SuppressWarnings("unchecked")
            List<Object[]> transactions = (List<Object[]>) request.getAttribute("transactions");
        %>

        <table>
            <tr><th>From</th><th>To</th><th>Amount (₹)</th><th>Action</th></tr>
            <%
                if (transactions != null && !transactions.isEmpty()) {
                    for (Object[] t : transactions) {
            %>
            <tr>
                <td><%= t[1] %></td>
                <td>➡️ <%= t[3] %></td>
                <td>₹<%= t[4] %></td>
                <td>
                    <form action="markPaid" method="post" style="display:inline;">
                        <input type="hidden" name="groupId" value="<%= request.getAttribute("groupId") %>" />
                        <input type="hidden" name="fromUserId" value="<%= t[0] %>" />
                        <input type="hidden" name="toUserId" value="<%= t[2] %>" />
                        <input type="hidden" name="amount" value="<%= t[4] %>" />
                        <input type="submit" value="Mark as Paid" class="btn" style="width:auto; padding:6px 14px; font-size:12px; margin-top:0;" />
                    </form>
                </td>
            </tr>
            <%
                    }
                } else {
            %>
            <tr><td colspan="4" style="text-align:center; color:#4f8a5b;">🎉 Everyone is settled up!</td></tr>
            <%
                }
            %>
        </table>

        <a href="viewBalance?groupId=<%= request.getAttribute("groupId") %>" class="btn" style="display:block; text-align:center; margin-top:20px; text-decoration:none;">
            ← Back to Balances
        </a>
    </div>
</body>
</html>