package com.splitapp.model;

public class Expense {
    private int expenseId;
    private int groupId;
    private int paidBy;
    private String description;
    private double amount;
    private String category;
    private String splitType;

    public Expense() {}

    public Expense(int groupId, int paidBy, String description, double amount, String splitType) {
        this.groupId = groupId;
        this.paidBy = paidBy;
        this.description = description;
        this.amount = amount;
        this.splitType = splitType;
    }

    public int getExpenseId() { return expenseId; }
    public void setExpenseId(int expenseId) { this.expenseId = expenseId; }

    public int getGroupId() { return groupId; }
    public void setGroupId(int groupId) { this.groupId = groupId; }

    public int getPaidBy() { return paidBy; }
    public void setPaidBy(int paidBy) { this.paidBy = paidBy; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getSplitType() { return splitType; }
    public void setSplitType(String splitType) { this.splitType = splitType; }
}