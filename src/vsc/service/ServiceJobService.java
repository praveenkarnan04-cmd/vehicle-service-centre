package vsc.service;

import vsc.exception.*;
import vsc.model.*;
import vsc.repository.Repository;
import vsc.util.Validator;

import java.util.List;
import java.util.stream.Collectors;

public class ServiceJobService {
    private final Repository<ServiceRecord> repo;
    private final VehicleService vehicles;
    private final MechanicService mechanics;
    private final PartService parts;

    public ServiceJobService(Repository<ServiceRecord> repo, VehicleService vehicles,
                             MechanicService mechanics, PartService parts) {
        this.repo = repo; this.vehicles = vehicles; this.mechanics = mechanics; this.parts = parts;
    }

    public ServiceRecord create(String regNo, String complaint, ServiceType type) throws VscException {
        Vehicle v = vehicles.getByRegNo(regNo);
        ServiceRecord r = new ServiceRecord(repo.nextId("S"), v.getRegNo(), v.getOwnerId(),
                Validator.requireText(complaint, "Complaint"), type);
        repo.save(r);
        return r;
    }

    public ServiceRecord assignMechanic(String serviceId, String mechanicId) throws VscException {
        ServiceRecord r = getById(serviceId);
        if (r.getStatus() == JobStatus.COMPLETED) throw new VscException("Job is already completed.");
        Mechanic m = mechanics.getById(mechanicId);
        r.setMechanicId(m.getId());
        repo.save(r);
        return r;
    }

    public ServiceRecord updateStatus(String serviceId, JobStatus next) throws VscException {
        ServiceRecord r = getById(serviceId);
        if (r.getStatus() == JobStatus.COMPLETED) throw new VscException("Completed jobs cannot be changed.");
        if (r.getMechanicId() == null) throw new VscException("Assign a mechanic before changing the status.");
        if (next.ordinal() <= r.getStatus().ordinal())
            throw new VscException("Status can only move forward (" + r.getStatus() + " -> " + next + " is invalid).");
        r.setStatus(next);
        repo.save(r);
        return r;
    }

    public ServiceRecord addUsedPart(String serviceId, String partId, int qty) throws VscException {
        ServiceRecord r = getById(serviceId);
        if (r.getStatus() == JobStatus.COMPLETED) throw new VscException("Cannot add parts to a completed job.");
        SparePart p = parts.consume(partId, qty);
        r.addPart(new UsedPart(p.getId(), p.getName(), qty, p.getUnitPrice()));
        repo.save(r);
        return r;
    }

    public ServiceRecord getById(String id) throws EntityNotFoundException {
        return repo.findById(Validator.safe(id).toUpperCase())
                .orElseThrow(() -> new EntityNotFoundException("Service job " + id + " not found."));
    }

    public List<ServiceRecord> byVehicle(String regNo) {
        return repo.findAll().stream().filter(r -> r.getVehicleRegNo().equalsIgnoreCase(Validator.safe(regNo)))
                .collect(Collectors.toList());
    }

    public List<ServiceRecord> byCustomer(String customerId) {
        return repo.findAll().stream().filter(r -> r.getCustomerId().equalsIgnoreCase(Validator.safe(customerId)))
                .collect(Collectors.toList());
    }

    public List<ServiceRecord> byMechanic(String mechanicId) {
        return repo.findAll().stream().filter(r -> Validator.safe(mechanicId).equalsIgnoreCase(r.getMechanicId()))
                .collect(Collectors.toList());
    }

    public List<ServiceRecord> all() { return repo.findAll(); }
}
