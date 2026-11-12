package vsc.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Appointment implements Identifiable {
    private static final long serialVersionUID = 1L;
    public static final int DURATION_MINUTES = 60;
    public static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final String id;
    private final String customerId;
    private final String vehicleRegNo;
    private LocalDateTime start;
    private boolean cancelled;

    public Appointment(String id, String customerId, String vehicleRegNo, LocalDateTime start) {
        this.id = id; this.customerId = customerId; this.vehicleRegNo = vehicleRegNo; this.start = start;
    }

    @Override public String getId() { return id; }
    public String getCustomerId() { return customerId; }
    public String getVehicleRegNo() { return vehicleRegNo; }
    public LocalDateTime getStart() { return start; }
    public void setStart(LocalDateTime s) { this.start = s; }
    public boolean isCancelled() { return cancelled; }
    public void cancel() { this.cancelled = true; }

    @Override
    public String toString() {
        return String.format("%s | %s | vehicle %-10s | customer %s%s", id, start.format(FORMAT),
                vehicleRegNo, customerId, cancelled ? " | CANCELLED" : "");
    }
}
