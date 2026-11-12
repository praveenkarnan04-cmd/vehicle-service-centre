package vsc.model;

import vsc.exception.InsufficientStockException;

public class SparePart implements Identifiable {
    private static final long serialVersionUID = 1L;
    private final String id;
    private final String name;
    private int quantity;
    private double unitPrice;
    private final int reorderLevel;

    public SparePart(String id, String name, int quantity, double unitPrice, int reorderLevel) {
        this.id = id; this.name = name; this.quantity = quantity;
        this.unitPrice = unitPrice; this.reorderLevel = reorderLevel;
    }

    public void addStock(int qty) { quantity += qty; }

    // OOP: EXCEPTION HANDLING - throws a custom checked exception instead of letting stock go negative.
    public void removeStock(int qty) throws InsufficientStockException {
        if (qty > quantity)
            throw new InsufficientStockException("Only " + quantity + " x " + name + " in stock, requested " + qty + ".");
        quantity -= qty;
    }

    public boolean isLowStock() { return quantity <= reorderLevel; }

    @Override public String getId() { return id; }
    public String getName() { return name; }
    public int getQuantity() { return quantity; }
    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double p) { this.unitPrice = p; }
    public int getReorderLevel() { return reorderLevel; }

    @Override
    public String toString() {
        return String.format("%s | %-20s | qty %4d | Rs. %9.2f%s", id, name, quantity, unitPrice,
                isLowStock() ? "  ** LOW STOCK **" : "");
    }
}
