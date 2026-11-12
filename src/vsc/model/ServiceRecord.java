package vsc.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServiceRecord implements Identifiable {
    private static final long serialVersionUID = 1L;
    private final String id;
    private final String vehicleRegNo;
    private final String customerId;
    private final String complaint;
    private final ServiceType serviceType;
    private final LocalDate date;
    private String mechanicId;
    private JobStatus status = JobStatus.PENDING;
    private final List<UsedPart> usedParts = new ArrayList<>();

    public ServiceRecord(String id, String vehicleRegNo, String customerId, String complaint, ServiceType type) {
        this.id = id; this.vehicleRegNo = vehicleRegNo; this.customerId = customerId;
        this.complaint = complaint; this.serviceType = type; this.date = LocalDate.now();
    }

    public void addPart(UsedPart p) { usedParts.add(p); }
    public double getLabourCharge() { return serviceType.calculateCharge(); }
    public double getPartsCost() { return usedParts.stream().mapToDouble(UsedPart::getCost).sum(); }

    @Override public String getId() { return id; }
    public String getVehicleRegNo() { return vehicleRegNo; }
    public String getCustomerId() { return customerId; }
    public String getComplaint() { return complaint; }
    public ServiceType getServiceType() { return serviceType; }
    public LocalDate getDate() { return date; }
    public String getMechanicId() { return mechanicId; }
    public void setMechanicId(String m) { this.mechanicId = m; }
    public JobStatus getStatus() { return status; }
    public void setStatus(JobStatus s) { this.status = s; }
    public List<UsedPart> getUsedParts() { return Collections.unmodifiableList(usedParts); }

    @Override
    public String toString() {
        return String.format("%s | %s | %-10s | %-16s | %-11s | mech: %-6s | %s",
                id, date, vehicleRegNo, serviceType, status, mechanicId == null ? "-" : mechanicId, complaint);
    }
}
