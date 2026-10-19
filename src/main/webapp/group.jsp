<%@ page language="java" contentType="text/html; charset=UTF-8" import="java.util.*, java.util.Map" %>
<html>
<head>
    <title>Group Balances - SplitApp</title>
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
    <%@ page import="java.sql.*, com.splitapp.db.DBConnection" %>
<%
    int groupIdForInvite = Integer.parseInt(request.getParameter("groupId"));
    String inviteCode = "";
    try (Connection invCon = DBConnection.getConnection();
         PreparedStatement invPs = invCon.prepareStatement("SELECT invite_code FROM groups_table WHERE group_id=?")) {
        invPs.setInt(1, groupIdForInvite);
        ResultSet invRs = invPs.executeQuery();
        if (invRs.next()) inviteCode = invRs.getString("invite_code");
    } catch (Exception e) {
        e.printStackTrace();
    }
    String inviteLink = "http://localhost:8080/Splitapp/join?code=" + inviteCode;
%>
<div class="container wide" style="margin-bottom:0; padding-bottom:16px;">
    <h2 style="font-size:18px;">🔗 Invite Link</h2>
    <div style="display:flex; gap:10px; align-items:center;">
        <input type="text" id="inviteLinkBox" value="<%= inviteLink %>" readonly
               style="flex:1; padding:10px 14px; border:1.5px solid #ecd9bd; border-radius:10px; background:#fffdf9; color:#5a4634; font-size:13px;" />
        <button onclick="copyInviteLink()" class="btn" style="width:auto; padding:10px 20px; margin-top:0;">Copy</button>
    </div>
    <p id="copyMsg" style="color:#4f8a5b; font-size:13px; margin-top:6px; display:none;">✅ Link copied!</p>
</div>

<script>
function copyInviteLink() {
    var box = document.getElementById("inviteLinkBox");
    box.select();
    document.execCommand("copy");
    document.getElementById("copyMsg").style.display = "block";
    setTimeout(function() {
        document.getElementById("copyMsg").style.display = "none";
    }, 2000);
}
</script>
    <div class="container wide">
        <h2>Group Balances 🧾</h2>
        <table>
            <tr><th>Name</th><th>Net Balance (₹)</th></tr>
            <%
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> balances = (List<Map<String, Object>>) request.getAttribute("balances");
                if (balances != null) {
                    for (Map<String, Object> b : balances) {
                        Object balObj = b.get("balance");
                        double bal = (balObj != null) ? ((Number) balObj).doubleValue() : 0.0;
                        String cls = bal >= 0 ? "positive" : "negative";
            %>
            <tr>
                <td><%= b.get("name") %></td>
                <td class="<%= cls %>"><%= bal >= 0 ? "+₹" + bal : "-₹" + Math.abs(bal) %></td>
            </tr>
            <%
                    }
                } else {
            %>
            <tr><td colspan="2" style="text-align:center; color:#a1815c;">No balances yet — add an expense first.</td></tr>
            <%
                }
            %>
        </table>
        <div style="margin-top:24px;">
    <h2 style="font-size:18px;">Add a Member</h2>
    <form action="addMember" method="post" style="display:flex; gap:10px; align-items:flex-end;">
        <input type="hidden" name="groupId" value="<%= request.getParameter("groupId") %>" />
        <div style="flex:1;">
            <label>Member's Email</label>
            <input type="email" name="email" placeholder="friend@example.com" required />
        </div>
        <input type="submit" value="Add" style="margin-top:0; width:auto; padding:10px 24px;" />
    </form>
    <% if (request.getAttribute("memberError") != null) { %>
        <div class="error" style="margin-top:12px;"><%= request.getAttribute("memberError") %></div>
    <% } %>
</div>
        <a href="settleUp?groupId=<%= request.getParameter("groupId") %>" class="btn" style="display:block; text-align:center; margin-top:20px; text-decoration:none;">
    🤝 Settle Up
</a>
<a href="addExpense.jsp?groupId=<%= request.getParameter("groupId") %>" class="btn" style="display:block; text-align:center; margin-top:20px; text-decoration:none;">
    ➕ Add Expense
</a>
    </div>
</body>
</html>