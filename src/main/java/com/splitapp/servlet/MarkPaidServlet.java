package com.splitapp.servlet;

import java.io.IOException;
import java.sql.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import com.splitapp.db.DBConnection;

@WebServlet("/markPaid")
public class MarkPaidServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int groupId = Integer.parseInt(req.getParameter("groupId"));
        int fromUserId = Integer.parseInt(req.getParameter("fromUserId"));
        int toUserId = Integer.parseInt(req.getParameter("toUserId"));
        double amount = Double.parseDouble(req.getParameter("amount"));

        String sql = "INSERT INTO settlements (group_id, paid_by, paid_to, amount) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, groupId);
            ps.setInt(2, fromUserId);
            ps.setInt(3, toUserId);
            ps.setDouble(4, amount);
            ps.executeUpdate();
            resp.sendRedirect("settleUp?groupId=" + groupId + "&status=paid");
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect("settleUp?groupId=" + groupId + "&error=1");
        }
    }
}