package com.splitapp.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.splitapp.db.DBConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/viewBalance")
public class ViewBalanceServlet extends HttpServlet {
    @Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
    	int groupId = Integer.parseInt(req.getParameter("groupId"));

    	Integer userId = (Integer) req.getSession().getAttribute("userId");
    	if (userId == null) { resp.sendRedirect("login.jsp"); return; }

    	String memberCheckSql = "SELECT 1 FROM group_members WHERE group_id=? AND user_id=?";
    	try (Connection memberCon = DBConnection.getConnection();
    	     PreparedStatement memberPs = memberCon.prepareStatement(memberCheckSql)) {
    	    memberPs.setInt(1, groupId);
    	    memberPs.setInt(2, userId);
    	    if (!memberPs.executeQuery().next()) {
    	        resp.sendRedirect("dashboard.jsp");
    	        return;
    	    }
    	} catch (Exception e) {
    	    e.printStackTrace();
    	}

        String sql = "SELECT u.user_id, u.name, "
            + "COALESCE(paid.total_paid,0) - COALESCE(owed.total_owed,0) "
            + "+ COALESCE(received.total_received,0) - COALESCE(sent.total_sent,0) AS net_balance "
            + "FROM users u "
            + "LEFT JOIN (SELECT paid_by, SUM(amount) total_paid FROM expenses WHERE group_id=? GROUP BY paid_by) paid ON u.user_id=paid.paid_by "
            + "LEFT JOIN (SELECT es.user_id, SUM(es.share_amount) total_owed FROM expense_splits es JOIN expenses e ON es.expense_id=e.expense_id WHERE e.group_id=? GROUP BY es.user_id) owed ON u.user_id=owed.user_id "
            + "LEFT JOIN (SELECT paid_to, SUM(amount) total_received FROM settlements WHERE group_id=? GROUP BY paid_to) received ON u.user_id=received.paid_to "
            + "LEFT JOIN (SELECT paid_by, SUM(amount) total_sent FROM settlements WHERE group_id=? GROUP BY paid_by) sent ON u.user_id=sent.paid_by "
            + "WHERE u.user_id IN (SELECT user_id FROM group_members WHERE group_id=?)";

        List<Map<String, Object>> balances = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 1; i <= 5; i++) {
				ps.setInt(i, groupId);
			}
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("name", rs.getString("name"));
                row.put("balance", rs.getDouble("net_balance"));
                balances.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        req.setAttribute("balances", balances);
        req.setAttribute("groupId", groupId);
        req.getRequestDispatcher("group.jsp").forward(req, resp);
    }
}