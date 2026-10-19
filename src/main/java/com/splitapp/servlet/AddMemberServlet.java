package com.splitapp.servlet;

import java.io.IOException;
import java.sql.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import com.splitapp.db.DBConnection;

@WebServlet("/addMember")
public class AddMemberServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int groupId = Integer.parseInt(req.getParameter("groupId"));
        String email = req.getParameter("email");

        try (Connection con = DBConnection.getConnection()) {

            // Step 1: find the user_id for this email
            String findUserSql = "SELECT user_id FROM users WHERE email = ?";
            int userId = -1;
            try (PreparedStatement ps = con.prepareStatement(findUserSql)) {
                ps.setString(1, email);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    userId = rs.getInt("user_id");
                }
            }

            if (userId == -1) {
                req.setAttribute("memberError", "No user found with that email. They need to register first.");
                req.getRequestDispatcher("viewBalance?groupId=" + groupId).forward(req, resp);
                return;
            }

            // Step 2: check if already a member
            String checkSql = "SELECT * FROM group_members WHERE group_id = ? AND user_id = ?";
            try (PreparedStatement ps = con.prepareStatement(checkSql)) {
                ps.setInt(1, groupId);
                ps.setInt(2, userId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    req.setAttribute("memberError", "This person is already in the group.");
                    req.getRequestDispatcher("viewBalance?groupId=" + groupId).forward(req, resp);
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
            req.setAttribute("memberError", "Error adding member: " + e.getMessage());
            req.getRequestDispatcher("viewBalance?groupId=" + groupId).forward(req, resp);
        }
    }
}
