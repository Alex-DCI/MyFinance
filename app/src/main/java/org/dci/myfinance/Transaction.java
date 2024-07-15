package org.dci.myfinance;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class Transaction implements Comparable<Transaction>, Serializable {
    private boolean isIncome;
    private double amount;
    private String description;
    private String category;
    private LocalDateTime dateTime;

    public void setAmount(double amount) {
        this.amount = Math.round(amount * 100.0) / 100.0;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIncome(boolean income) {
        isIncome = income;
    }

    public Transaction(double amount, String category, LocalDateTime dateTime, String description, boolean isIncome) {
        this.category = category;
        this.dateTime = dateTime;
        this.description = description;
        this.isIncome = isIncome;
        this.amount = amount;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return isIncome == that.isIncome && Double.compare(amount, that.amount) == 0 && Objects.equals(description, that.description) && Objects.equals(category, that.category) && Objects.equals(dateTime, that.dateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isIncome, amount, description, category, dateTime);
    }

}
