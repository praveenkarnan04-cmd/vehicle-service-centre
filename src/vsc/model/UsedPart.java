package vsc.model;

import java.io.Serializable;

public class UsedPart implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String partId;
    private final String name;
    private final int quantity;
    private final double unitPrice;

    public UsedPart(String partId, String name, int quantity, double unitPrice) {
        this.partId = partId; this.name = name; this.quantity = quantity; this.unitPrice = unitPrice;
    }

    public String getPartId() { return partId; }
    public String getName() { return name; }
    public int getQuantity() { return quantity; }
    public double getUnitPrice() { return unitPrice; }
    public double getCost() { return quantity * unitPrice; }
}
