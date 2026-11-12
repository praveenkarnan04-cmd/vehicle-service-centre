package vsc.service;

import vsc.exception.*;
import vsc.model.*;
import vsc.repository.Repository;
import vsc.util.Validator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class BillingService {
    private final Repository<Bill> repo;
    private final ServiceJobService jobs;

    public BillingService(Repository<Bill> repo, ServiceJobService jobs) {
        this.repo = repo;
        this.jobs = jobs;
    }

    public Bill generateBill(String serviceId) throws VscException {
        ServiceRecord r = jobs.getById(serviceId);
        if (r.getStatus() != JobStatus.COMPLETED)
            throw new VscException("Job must be COMPLETED before a bill can be generated.");
        if (repo.findAll().stream().anyMatch(b -> b.getServiceId().equals(r.getId())))
            throw new DuplicateEntityException("A bill already exists for " + r.getId() + ".");
        Bill b = new Bill(repo.nextId("B"), r.getId(), r.getCustomerId(), r.getLabourCharge(), r.getPartsCost());
        repo.save(b);
        writeBillFile(b, r);
        return b;
    }

    public Bill recordPayment(String billId, double amount) throws VscException {
        Bill b = getById(billId);
        b.addPayment(amount);
        repo.save(b);
        return b;
    }

    public Bill getById(String id) throws EntityNotFoundException {
        return repo.findById(Validator.safe(id).toUpperCase())
                .orElseThrow(() -> new EntityNotFoundException("Bill " + id + " not found."));
    }

    public List<Bill> outstanding() {
        return repo.findAll().stream().filter(b -> b.getBalance() > 0.0001).collect(Collectors.toList());
    }

    public List<Bill> all() { return repo.findAll(); }

    /** File handling: writes a printable text bill to ./bills. */
    // OOP: FILE HANDLING - writes a printable bill to bills/<id>.txt with BufferedWriter (try-with-resources).
    private void writeBillFile(Bill b, ServiceRecord r) throws VscException {
        Path path = Paths.get("bills", b.getId() + ".txt");
        try {
            Files.createDirectories(path.getParent());
            try (BufferedWriter w = Files.newBufferedWriter(path)) {
                w.write("========== VEHICLE SERVICE CENTRE - BILL ==========\n");
                w.write(String.format("Bill: %s   Date: %s%n", b.getId(), b.getDate()));
                w.write(String.format("Customer: %s   Vehicle: %s%n", b.getCustomerId(), r.getVehicleRegNo()));
                w.write(String.format("Service: %s (%s)%n", r.getId(), r.getServiceType()));
                w.write("---------------------------------------------------\n");
                w.write(String.format("Labour charge %29s Rs. %9.2f%n", "", b.getLabour()));
                for (UsedPart p : r.getUsedParts())
                    w.write(String.format("%-28s x%-3d  Rs. %9.2f%n", p.getName(), p.getQuantity(), p.getCost()));
                w.write("---------------------------------------------------\n");
                w.write(String.format("TOTAL %37s Rs. %9.2f%n", "", b.calculateCharge()));
            }
        } catch (IOException e) {
            throw new VscException("Bill saved, but writing the bill file failed: " + e.getMessage(), e);
        }
    }
}
