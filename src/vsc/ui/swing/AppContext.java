package vsc.ui.swing;

import vsc.exception.VscException;
import vsc.model.Customer;
import vsc.repository.FileRepository;
import vsc.service.*;

import java.time.LocalDate;

/** Wires repositories and services together once for the whole GUI. */
public final class AppContext {
    final CustomerService customers;
    final VehicleService vehicles;
    final MechanicService mechanics;
    final PartService parts;
    final ServiceJobService jobs;
    final BillingService billing;
    final AppointmentService appointments;

    public AppContext() {
        customers = new CustomerService(new FileRepository<>("customers.dat"));
        vehicles = new VehicleService(new FileRepository<>("vehicles.dat"), customers);
        mechanics = new MechanicService(new FileRepository<>("mechanics.dat"));
        parts = new PartService(new FileRepository<>("parts.dat"));
        jobs = new ServiceJobService(new FileRepository<>("services.dat"), vehicles, mechanics, parts);
        billing = new BillingService(new FileRepository<>("bills.dat"), jobs);
        appointments = new AppointmentService(new FileRepository<>("appointments.dat"), vehicles);
    }

    String customerName(String id) {
        try { return customers.getById(id).getName(); } catch (VscException e) { return id; }
    }

    void loadDemo() throws VscException {
        Customer c1 = customers.register("Nimal Perera", "0771234567", "nimal@mail.com");
        Customer c2 = customers.register("Kamani Silva", "0712345678", "");
        vehicles.register("CAB-1234", "Car", "Toyota", "Axio", c1.getId());
        vehicles.register("KV-5678", "Van", "Nissan", "Caravan", c2.getId());
        mechanics.register("Sunil Fernando", "0701112233", "Engine");
        mechanics.register("Ruwan Jayasuriya", "0702223344", "Electrical");
        parts.addPart("Oil filter", 20, 850, 5);
        parts.addPart("Brake pads", 6, 4200, 5);
        parts.addPart("Engine oil 4L", 12, 6500, 4);
        appointments.book("CAB-1234", LocalDate.now().plusDays(1).atTime(10, 0));
    }
}
