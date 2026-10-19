package com.splitapp.servlet;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import com.splitapp.db.DBConnection;

@WebServlet("/settleUp")
public class SettlementServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int groupId = Integer.parseInt(req.getParameter("groupId"));
        req.setAttribute("groupId", groupId);

        String sql = "SELECT u.user_id, u.name, "
            + "COALESCE(paid.total_paid,0) - COALESCE(owed.total_owed,0) "
            + "+ COALESCE(received.total_received,0) - COALESCE(sent.total_sent,0) AS net_balance "
            + "FROM users u "
            + "LEFT JOIN (SELECT paid_by, SUM(amount) total_paid FROM expenses WHERE group_id=? GROUP BY paid_by) paid ON u.user_id=paid.paid_by "
            + "LEFT JOIN (SELECT es.user_id, SUM(es.share_amount) total_owed FROM expense_splits es JOIN expenses e ON es.expense_id=e.expense_id WHERE e.group_id=? GROUP BY es.user_id) owed ON u.user_id=owed.user_id "
            + "LEFT JOIN (SELECT paid_to, SUM(amount) total_received FROM settlements WHERE group_id=? GROUP BY paid_to) received ON u.user_id=received.paid_to "
            + "LEFT JOIN (SELECT paid_by, SUM(amount) total_sent FROM settlements WHERE group_id=? GROUP BY paid_by) sent ON u.user_id=sent.paid_by "
            + "WHERE u.user_id IN (SELECT user_id FROM group_members WHERE group_id=?)";

        List<Object[]> balances = new ArrayList<>(); // [0]=userId(int), [1]=name(String), [2]=balance(double)

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 1; i <= 5; i++) ps.setInt(i, groupId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int userId = rs.getInt("user_id");
                String name = rs.getString("name");
                double balance = rs.getDouble("net_balance");
                if (Math.abs(balance) > 0.01) {
                    balances.add(new Object[]{userId, name, balance});
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Error computing balances: " + e.getMessage());
            req.getRequestDispatcher("settlement.jsp").forward(req, resp);
            return;
        }

        List<Object[]> transactions = simplifyDebts(balances);
        req.setAttribute("transactions", transactions);
        req.getRequestDispatcher("settlement.jsp").forward(req, resp);
    }

    /**
     * Returns list of Object[]: [0]=fromUserId(int), [1]=fromName(String),
     * [2]=toUserId(int), [3]=toName(String), [4]=amount(String formatted)
     */
    private List<Object[]> simplifyDebts(List<Object[]> balances) {
        List<Object[]> result = new ArrayList<>();

        while (true) {
            int maxCreditorIdx = -1, maxDebtorIdx = -1;
            double maxCredit = 0.01, maxDebt = -0.01;

            for (int i = 0; i < balances.size(); i++) {
                double bal = (double) balances.get(i)[2];
                if (bal > maxCredit) { maxCredit = bal; maxCreditorIdx = i; }
                if (bal < maxDebt) { maxDebt = bal; maxDebtorIdx = i; }
            }

            if (maxCreditorIdx == -1 || maxDebtorIdx == -1) break;

            Object[] creditor = balances.get(maxCreditorIdx);
            Object[] debtor = balances.get(maxDebtorIdx);

            double creditAmt = (double) creditor[2];
            double debtAmt = (double) debtor[2];

            double settleAmount = Math.min(creditAmt, -debtAmt);
            settleAmount = Math.round(settleAmount * 100.0) / 100.0;

            result.add(new Object[]{
                debtor[0], debtor[1],       // fromUserId, fromName
                creditor[0], creditor[1],   // toUserId, toName
                String.format("%.2f", settleAmount)
            });

            creditor[2] = creditAmt - settleAmount;
            debtor[2] = debtAmt + settleAmount;
        }

        return result;
    }
}