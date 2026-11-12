package vsc.model;

import vsc.exception.InvalidInputException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Bill implements Identifiable, Billable {
    private static final long serialVersionUID = 1L;
    private final String id;
    private final String serviceId;
    private final String customerId;
    private final double labour;
    private final double partsCost;
    private final LocalDate date = LocalDate.now();
    private final List<Payment> payments = new ArrayList<>();

    public Bill(String id, String serviceId, String customerId, double labour, double partsCost) {
        this.id = id; this.serviceId = serviceId; this.customerId = customerId;
        this.labour = labour; this.partsCost = partsCost;
    }

    @Override public double calculateCharge() { return labour + partsCost; }
    public double getPaid() { return payments.stream().mapToDouble(Payment::getAmount).sum(); }
    public double getBalance() { return calculateCharge() - getPaid(); }

    // OOP: ENCAPSULATION - the payments list is private; this is the only way in, and it validates first.
    public void addPayment(double amount) throws InvalidInputException {
        if (amount <= 0) throw new InvalidInputException("Payment must be greater than zero.");
        if (amount > getBalance() + 0.0001)
            throw new InvalidInputException(String.format("Payment exceeds outstanding balance (Rs. %.2f).", getBalance()));
        payments.add(new Payment(amount));
    }

    @Override public String getId() { return id; }
    public String getServiceId() { return serviceId; }
    public String getCustomerId() { return customerId; }
    public double getLabour() { return labour; }
    public double getPartsCost() { return partsCost; }
    public LocalDate getDate() { return date; }
    public List<Payment> getPayments() { return Collections.unmodifiableList(payments); }

    @Override
    public String toString() {
        return String.format("%s | service %s | total Rs. %.2f | paid Rs. %.2f | balance Rs. %.2f",
                id, serviceId, calculateCharge(), getPaid(), getBalance());
    }
}
