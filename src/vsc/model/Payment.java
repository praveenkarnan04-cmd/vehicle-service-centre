package vsc.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Payment implements Serializable {
    private static final long serialVersionUID = 1L;
    private final double amount;
    private final LocalDateTime paidAt = LocalDateTime.now();

    public Payment(double amount) { this.amount = amount; }
    public double getAmount() { return amount; }
    public LocalDateTime getPaidAt() { return paidAt; }
}
