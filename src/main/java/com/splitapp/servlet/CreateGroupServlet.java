package com.splitapp.servlet;

import java.io.IOException;
import java.sql.*;
import java.util.UUID;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import com.splitapp.db.DBConnection;

@WebServlet("/createGroup")
public class CreateGroupServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        String groupName = req.getParameter("groupName");

        // Generate a short random invite code, e.g. "a1b2c3d4"
        String inviteCode = UUID.randomUUID().toString().substring(0, 8);

        String sql = "INSERT INTO groups_table (group_name, created_by, invite_code) VALUES (?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, groupName);
            ps.setInt(2, userId);
            ps.setString(3, inviteCode);
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            int groupId = 0;
            if (keys.next()) groupId = keys.getInt(1);

            String memberSql = "INSERT INTO group_members (group_id, user_id) VALUES (?, ?)";
            try (PreparedStatement ps2 = con.prepareStatement(memberSql)) {
                ps2.setInt(1, groupId);
                ps2.setInt(2, userId);
                ps2.executeUpdate();
            }
            resp.sendRedirect("dashboard.jsp");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}