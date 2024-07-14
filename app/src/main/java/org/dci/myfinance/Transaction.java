package org.dci.myfinance;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Transaction implements Comparable<Transaction>, Serializable {
    private boolean isIncome;
    private double amount;
    private String description;
    private String category;
    private LocalDateTime dateTime;

    public Transaction(double amount, String category, LocalDateTime dateTime, String description, boolean isIncome) {
        this.category = category;
        this.dateTime = dateTime;
        this.description = description;
        this.isIncome = isIncome;
        this.amount = isIncome ? amount : -amount;
    }

    public double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getDescription() {
        return description;
    }

    public boolean isIncome() {
        return isIncome;
    }

    @Override
    public int compareTo(Transaction o) {
        return dateTime.compareTo(o.dateTime);
    }
}
