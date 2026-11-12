package vsc.model;

import java.io.Serializable;

/** Abstract service type; each subclass decides its own labour charge (polymorphism). */
// OOP: ABSTRACTION - says every service has a labour charge, but not how it is calculated.
public abstract class ServiceType implements Billable, Serializable {
    private static final long serialVersionUID = 1L;
    private final String name;

    protected ServiceType(String name) { this.name = name; }

    public String getName() { return name; }
    protected abstract double getLabourCharge();

    @Override public double calculateCharge() { return getLabourCharge(); }

    @Override public String toString() { return name; }
}
