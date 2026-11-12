package vsc.model;

// OOP: INHERITANCE + POLYMORPHISM - a third behaviour behind the same method call.
public class FullService extends ServiceType {
    private static final long serialVersionUID = 1L;
    public static final double FLAT_RATE = 12000.00;
    public FullService() { super("Full Service"); }
    @Override protected double getLabourCharge() { return FLAT_RATE; }
}
