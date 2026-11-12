package vsc.model;

// OOP: INHERITANCE + POLYMORPHISM - overrides getLabourCharge() with a flat price.
public class OilChange extends ServiceType {
    private static final long serialVersionUID = 1L;
    public static final double FLAT_RATE = 2500.00;
    public OilChange() { super("Oil Change"); }
    @Override protected double getLabourCharge() { return FLAT_RATE; }
}
