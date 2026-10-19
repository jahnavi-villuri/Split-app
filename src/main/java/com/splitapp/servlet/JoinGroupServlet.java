package com.splitapp.servlet;

import java.io.IOException;
import java.sql.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import com.splitapp.db.DBConnection;

@WebServlet("/join")
public class JoinGroupServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String inviteCode = req.getParameter("code");

        Integer userId = (Integer) req.getSession().getAttribute("userId");
        if (userId == null) {
            // Not logged in — send to login, then come back here after
            resp.sendRedirect("login.jsp?redirect=join?code=" + inviteCode);
            return;
        }

        try (Connection con = DBConnection.getConnection()) {

            // Step 1: find the group by invite code
            String findGroupSql = "SELECT group_id, group_name FROM groups_table WHERE invite_code = ?";
            int groupId = -1;
            String groupName = "";
            try (PreparedStatement ps = con.prepareStatement(findGroupSql)) {
                ps.setString(1, inviteCode);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    groupId = rs.getInt("group_id");
                    groupName = rs.getString("group_name");
                }
            }

            if (groupId == -1) {
                req.setAttribute("error", "Invalid or expired invite link.");
                req.getRequestDispatcher("dashboard.jsp").forward(req, resp);
                return;
            }

            // Step 2: check if already a member
            String checkSql = "SELECT 1 FROM group_members WHERE group_id=? AND user_id=?";
            try (PreparedStatement ps = con.prepareStatement(checkSql)) {
                ps.setInt(1, groupId);
                ps.setInt(2, userId);
                if (ps.executeQuery().next()) {
                    resp.sendRedirect("viewBalance?groupId=" + groupId); // already a member, just go there
                    return;
                }
            }

            // Step 3: add them
            String insertSql = "INSERT INTO group_members (group_id, user_id) VALUES (?, ?)";
            try (PreparedStatement ps = con.prepareStatement(insertSql)) {
                ps.setInt(1, groupId);
                ps.setInt(2, userId);
                ps.executeUpdate();
            }

            resp.sendRedirect("viewBalance?groupId=" + groupId);

        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Error joining group: " + e.getMessage());
            req.getRequestDispatcher("dashboard.jsp").forward(req, resp);
        }
    }
}