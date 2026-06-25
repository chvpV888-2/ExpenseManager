package com.example.expensemanager;

public class Expense {
    private String expenseId;
    private String employeeUid;
    private String employeeName;
    private double amount;
    private String date;
    private String category;
    private String description;
    private String receiptUrl;
    private String status;
    private String currentApprover;

    // Required empty constructor for Firebase
    public Expense() {}

    public Expense(String expenseId, String employeeUid, String employeeName, double amount,
                   String date, String category, String description, String receiptUrl,
                   String status, String currentApprover) {
        this.expenseId = expenseId;
        this.employeeUid = employeeUid;
        this.employeeName = employeeName;
        this.amount = amount;
        this.date = date;
        this.category = category;
        this.description = description;
        this.receiptUrl = receiptUrl;
        this.status = status;
        this.currentApprover = currentApprover;
    }

    // Getters and Setters for all fields
    public String getExpenseId() { return expenseId; }
    public double getAmount() { return amount; }
    public String getStatus() { return status; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public String getDate() { return date; }
    public String getEmployeeName() { return employeeName; }
    public String getReceiptUrl() { return receiptUrl; }
    public String getCurrentApprover() { return currentApprover; }

    public void setStatus(String status) { this.status = status; }
    public void setCurrentApprover(String currentApprover) { this.currentApprover = currentApprover; }
}