package vsc.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// OOP: INHERITANCE - Customer reuses id, name, phone and email from Person.
public class Customer extends Person {
    private static final long serialVersionUID = 1L;
    private final List<String> vehicleRegNos = new ArrayList<>();

    public Customer(String id, String name, String phone, String email) {
        super(id, name, phone, email);
    }

    public void addVehicle(String regNo) {
        if (!vehicleRegNos.contains(regNo)) vehicleRegNos.add(regNo);
    }

    public List<String> getVehicleRegNos() { return Collections.unmodifiableList(vehicleRegNos); }

    @Override public String getRole() { return "Customer"; }

    @Override
    public String toString() { return super.toString() + " | vehicles: " + vehicleRegNos; }
}
