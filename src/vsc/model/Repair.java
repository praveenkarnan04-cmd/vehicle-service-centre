package vsc.model;

/** Repairs are charged per estimated hour. */
// OOP: INHERITANCE + POLYMORPHISM - same method as OilChange, different result (hours x rate).
public class Repair extends ServiceType {
    private static final long serialVersionUID = 1L;
    public static final double HOURLY_RATE = 3000.00;
    private final double hours;

    public Repair(double hours) {
        super("Repair (" + hours + "h)");
        this.hours = hours;
    }

    public double getHours() { return hours; }
    @Override protected double getLabourCharge() { return hours * HOURLY_RATE; }
}
