package vsc.model;

/** Contract for anything that can produce a monetary charge. */
// OOP: INTERFACE - anything that can produce a charge (ServiceType and Bill both do).
public interface Billable {
    double calculateCharge();
}
