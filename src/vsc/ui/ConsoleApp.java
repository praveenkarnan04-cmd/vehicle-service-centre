package vsc.ui;

import vsc.exception.VscException;
import vsc.model.*;
import vsc.repository.FileRepository;
import vsc.service.*;
import vsc.util.Validator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/** Text-based user interface. All business errors are caught and shown as friendly messages. */
public class ConsoleApp {
    @FunctionalInterface
    private interface Action { void run() throws VscException; }

    private final Scanner in = new Scanner(System.in);
    private final CustomerService customers;
    private final VehicleService vehicles;
    private final MechanicService mechanics;
    private final PartService parts;
    private final ServiceJobService jobs;
    private final BillingService billing;
    private final AppointmentService appointments;

    public ConsoleApp() {
        customers = new CustomerService(new FileRepository<>("customers.dat"));
        vehicles = new VehicleService(new FileRepository<>("vehicles.dat"), customers);
        mechanics = new MechanicService(new FileRepository<>("mechanics.dat"));
        parts = new PartService(new FileRepository<>("parts.dat"));
        jobs = new ServiceJobService(new FileRepository<>("services.dat"), vehicles, mechanics, parts);
        billing = new BillingService(new FileRepository<>("bills.dat"), jobs);
        appointments = new AppointmentService(new FileRepository<>("appointments.dat"), vehicles);
    }

    public void run() {
        System.out.println("\n=== VEHICLE SERVICE CENTRE MANAGEMENT SYSTEM ===");
        warnLowStock();
        try {
            while (true) {
                System.out.println("\nMAIN MENU\n 1. Customers\n 2. Vehicles\n 3. Service jobs\n 4. Mechanics"
                        + "\n 5. Spare parts\n 6. Billing & payments\n 7. Appointments\n 8. Load demo data\n 0. Exit");
                switch (readInt("Choose")) {
                    case 1 -> customerMenu();
                    case 2 -> vehicleMenu();
                    case 3 -> serviceMenu();
                    case 4 -> mechanicMenu();
                    case 5 -> partsMenu();
                    case 6 -> billingMenu();
                    case 7 -> appointmentMenu();
                    case 8 -> safely(this::loadDemo);
                    case 0 -> { System.out.println("Goodbye!"); return; }
                    default -> System.out.println("Invalid option.");
                }
            }
        } catch (NoSuchElementException e) {
            System.out.println("\nInput closed. Goodbye!");
        }
    }

    // ---------- menus ----------
    private void customerMenu() {
        while (true) {
            System.out.println("\nCUSTOMERS: 1 Register  2 Update  3 Search  4 Service history  5 List all  0 Back");
            switch (readInt("Choose")) {
                case 1 -> safely(() -> System.out.println("Registered: " + customers.register(
                        ask("Name"), ask("Phone"), ask("Email (optional)"))));
                case 2 -> safely(() -> System.out.println("Updated: " + customers.update(ask("Customer id"),
                        ask("New name (blank = keep)"), ask("New phone (blank = keep)"), ask("New email (blank = keep)"))));
                case 3 -> printAll(customers.search(ask("Name / phone / id")));
                case 4 -> safely(() -> printAll(jobs.byCustomer(customers.getById(ask("Customer id")).getId())));
                case 5 -> printAll(customers.all());
                case 0 -> { return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void vehicleMenu() {
        while (true) {
            System.out.println("\nVEHICLES: 1 Register  2 Find by reg no  3 Vehicles of a customer  4 List all  0 Back");
            switch (readInt("Choose")) {
                case 1 -> safely(() -> System.out.println("Registered: " + vehicles.register(ask("Reg no (e.g. CAB-1234)"),
                        ask("Type (Car/Van/Bike...)"), ask("Brand"), ask("Model"), ask("Owner customer id"))));
                case 2 -> safely(() -> System.out.println(vehicles.getByRegNo(ask("Reg no"))));
                case 3 -> printAll(vehicles.byOwner(ask("Customer id")));
                case 4 -> printAll(vehicles.all());
                case 0 -> { return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void serviceMenu() {
        while (true) {
            System.out.println("\nSERVICE JOBS: 1 Create  2 Assign mechanic  3 Update status  4 Record used part"
                    + "\n  5 Vehicle service history  6 List all  0 Back");
            switch (readInt("Choose")) {
                case 1 -> safely(this::createService);
                case 2 -> safely(() -> System.out.println("Assigned: " + jobs.assignMechanic(ask("Service id"), ask("Mechanic id"))));
                case 3 -> safely(this::updateStatus);
                case 4 -> safely(() -> System.out.println("Part recorded: " + jobs.addUsedPart(
                        ask("Service id"), ask("Part id"), readInt("Quantity"))));
                case 5 -> printAll(jobs.byVehicle(ask("Reg no")));
                case 6 -> printAll(jobs.all());
                case 0 -> { return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void mechanicMenu() {
        while (true) {
            System.out.println("\nMECHANICS: 1 Register  2 View assigned work  3 List all  0 Back");
            switch (readInt("Choose")) {
                case 1 -> safely(() -> System.out.println("Registered: " + mechanics.register(
                        ask("Name"), ask("Phone"), ask("Specialization"))));
                case 2 -> safely(() -> printAll(jobs.byMechanic(mechanics.getById(ask("Mechanic id")).getId())));
                case 3 -> printAll(mechanics.all());
                case 0 -> { return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void partsMenu() {
        while (true) {
            System.out.println("\nSPARE PARTS: 1 Add part  2 Restock  3 List all  4 Low-stock warnings  0 Back");
            switch (readInt("Choose")) {
                case 1 -> safely(() -> System.out.println("Added: " + parts.addPart(ask("Name"),
                        readInt("Quantity"), readDouble("Unit price"), readInt("Reorder level"))));
                case 2 -> safely(() -> System.out.println("Restocked: " + parts.restock(ask("Part id"), readInt("Quantity to add"))));
                case 3 -> printAll(parts.all());
                case 4 -> warnLowStock();
                case 0 -> { return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void billingMenu() {
        while (true) {
            System.out.println("\nBILLING: 1 Generate bill  2 Record payment  3 Outstanding balances  4 View bill  5 All bills  0 Back");
            switch (readInt("Choose")) {
                case 1 -> safely(() -> System.out.println("Bill created (file saved in ./bills): " + billing.generateBill(ask("Service id"))));
                case 2 -> safely(() -> System.out.println("Payment recorded: " + billing.recordPayment(ask("Bill id"), readDouble("Amount"))));
                case 3 -> printAll(billing.outstanding());
                case 4 -> safely(() -> System.out.println(billing.getById(ask("Bill id"))));
                case 5 -> printAll(billing.all());
                case 0 -> { return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void appointmentMenu() {
        while (true) {
            System.out.println("\nAPPOINTMENTS: 1 Book  2 Upcoming  3 Reschedule  4 Cancel  5 Check availability  0 Back");
            switch (readInt("Choose")) {
                case 1 -> safely(() -> System.out.println("Booked: " + appointments.book(ask("Reg no"), askDateTime())));
                case 2 -> printAll(appointments.upcoming());
                case 3 -> safely(() -> System.out.println("Rescheduled: " + appointments.reschedule(ask("Appointment id"), askDateTime())));
                case 4 -> safely(() -> System.out.println("Cancelled: " + appointments.cancel(ask("Appointment id"))));
                case 5 -> safely(() -> System.out.println("Free bays: " + appointments.freeBays(askDateTime())
                        + " of " + AppointmentService.BAYS));
                case 0 -> { return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    // ---------- actions ----------
    private void createService() throws VscException {
        String reg = ask("Vehicle reg no");
        String complaint = ask("Customer complaint");
        System.out.println(" 1 Oil change  2 Repair  3 Full service");
        ServiceType type;
        switch (readInt("Service type")) {
            case 1 -> type = new OilChange();
            case 2 -> type = new Repair(Validator.requirePositive(readDouble("Estimated hours"), "Hours"));
            case 3 -> type = new FullService();
            default -> throw new VscException("Unknown service type.");
        }
        ServiceRecord r = jobs.create(reg, complaint, type);
        System.out.printf("Created: %s (labour Rs. %.2f)%n", r, r.getLabourCharge());
    }

    private void updateStatus() throws VscException {
        String id = ask("Service id");
        System.out.println(" 1 In progress  2 Completed");
        JobStatus next = switch (readInt("New status")) {
            case 1 -> JobStatus.IN_PROGRESS;
            case 2 -> JobStatus.COMPLETED;
            default -> throw new VscException("Unknown status.");
        };
        System.out.println("Updated: " + jobs.updateStatus(id, next));
    }

    private void warnLowStock() {
        List<SparePart> low = parts.lowStock();
        if (low.isEmpty()) System.out.println("Stock levels OK.");
        else { System.out.println("!! LOW STOCK WARNING !!"); low.forEach(p -> System.out.println("  " + p)); }
    }

    private void loadDemo() throws VscException {
        Customer c1 = customers.register("Nimal Perera", "0771234567", "nimal@mail.com");
        Customer c2 = customers.register("Kamani Silva", "0712345678", "");
        vehicles.register("CAB-1234", "Car", "Toyota", "Axio", c1.getId());
        vehicles.register("KV-5678", "Van", "Nissan", "Caravan", c2.getId());
        mechanics.register("Sunil Fernando", "0701112233", "Engine");
        mechanics.register("Ruwan Jayasuriya", "0702223344", "Electrical");
        parts.addPart("Oil filter", 20, 850, 5);
        parts.addPart("Brake pads", 6, 4200, 5);
        parts.addPart("Engine oil 4L", 12, 6500, 4);
        LocalDateTime t = LocalDate.now().plusDays(1).atTime(10, 0);
        appointments.book("CAB-1234", t);
        System.out.println("Demo data loaded (2 customers, 2 vehicles, 2 mechanics, 3 parts, 1 appointment).");
    }

    // ---------- helpers ----------
    private void safely(Action a) {
        try {
            a.run();
        } catch (VscException e) {
            System.out.println("ERROR: " + e.getMessage());
        } catch (RuntimeException e) {
            if (e instanceof NoSuchElementException) throw e;
            System.out.println("Unexpected problem: " + e.getMessage());
        }
    }

    private <T> void printAll(List<T> items) {
        if (items.isEmpty()) System.out.println("(no records)");
        else items.forEach(i -> System.out.println("  " + i));
    }

    private String ask(String label) {
        System.out.print(label + ": ");
        return in.nextLine();
    }

    private LocalDateTime askDateTime() throws VscException {
        return Validator.parseDateTime(ask("Date & time (yyyy-MM-dd HH:mm)"));
    }

    private int readInt(String label) {
        while (true) {
            try { return Integer.parseInt(ask(label).trim()); }
            catch (NumberFormatException e) { System.out.println("Please enter a whole number."); }
        }
    }

    private double readDouble(String label) {
        while (true) {
            try { return Double.parseDouble(ask(label).trim()); }
            catch (NumberFormatException e) { System.out.println("Please enter a number."); }
        }
    }
}
