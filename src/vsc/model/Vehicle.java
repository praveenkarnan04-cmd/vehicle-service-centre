package vsc.model;

public class Vehicle implements Identifiable {
    private static final long serialVersionUID = 1L;
    private final String regNo;
    private final String type;
    private final String brand;
    private final String model;
    private final String ownerId;

    public Vehicle(String regNo, String type, String brand, String model, String ownerId) {
        this.regNo = regNo; this.type = type; this.brand = brand; this.model = model; this.ownerId = ownerId;
    }

    @Override public String getId() { return regNo; }
    public String getRegNo() { return regNo; }
    public String getType() { return type; }
    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public String getOwnerId() { return ownerId; }

    @Override
    public String toString() {
        return String.format("%-10s | %-8s | %s %s | owner %s", regNo, type, brand, model, ownerId);
    }
}
