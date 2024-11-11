package com.bank.customer;

import java.sql.Timestamp;

public class Transaction {
    private int id;
    private String accountNo;
    private String recipientAccountNo; // Added recipient account number
    private Timestamp date;
    private String type;
    private double amount;
    private double balance;

    // Default Constructor
    public Transaction() {
    }

    // Parameterized Constructor
    public Transaction(int id, String accountNo, String recipientAccountNo, Timestamp date, String type, double amount, double balance) {
        this.id = id;
        this.accountNo = accountNo;
        this.recipientAccountNo = recipientAccountNo;
        this.date = date;
        this.type = type;
        this.amount = amount;
        this.balance = balance;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getRecipientAccountNo() {
        return recipientAccountNo;
    }

    public void setRecipientAccountNo(String recipientAccountNo) {
        this.recipientAccountNo = recipientAccountNo;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    // toString Method
    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", accountNo='" + accountNo + '\'' +
                ", recipientAccountNo='" + recipientAccountNo + '\'' +
                ", date=" + date +
                ", type='" + type + '\'' +
                ", amount=" + amount +
                ", balance=" + balance +
                '}';
    }
}
