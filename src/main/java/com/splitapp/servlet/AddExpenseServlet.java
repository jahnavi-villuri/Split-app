package com.splitapp.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import com.splitapp.db.DBConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/addExpense")
public class AddExpenseServlet extends HttpServlet {
    @Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession();
        int paidBy = (Integer) session.getAttribute("userId");
        int groupId = Integer.parseInt(req.getParameter("groupId"));
        String description = req.getParameter("description");
        double amount = Double.parseDouble(req.getParameter("amount"));
        String[] memberIds = req.getParameterValues("members"); // checkboxes from JSP

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false); // transaction: expense + splits must succeed together

            String expenseSql = "INSERT INTO expenses (group_id, paid_by, description, amount, split_type) VALUES (?, ?, ?, ?, 'equal')";
            PreparedStatement ps = con.prepareStatement(expenseSql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, groupId);
            ps.setInt(2, paidBy);
            ps.setString(3, description);
            ps.setDouble(4, amount);
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            int expenseId = 0;
            if (keys.next()) {
				expenseId = keys.getInt(1);
			}

            double share = amount / memberIds.length;
            String splitSql = "INSERT INTO expense_splits (expense_id, user_id, share_amount) VALUES (?, ?, ?)";
            PreparedStatement ps2 = con.prepareStatement(splitSql);
            for (String memberId : memberIds) {
                ps2.setInt(1, expenseId);
                ps2.setInt(2, Integer.parseInt(memberId));
                ps2.setDouble(3, share);
                ps2.addBatch();
            }
            ps2.executeBatch();
            con.commit();

            resp.sendRedirect("group.jsp?groupId=" + groupId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}