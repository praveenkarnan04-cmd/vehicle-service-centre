package vsc.service;

import vsc.exception.*;
import vsc.model.Customer;
import vsc.model.Vehicle;
import vsc.repository.Repository;
import vsc.util.Validator;

import java.util.List;
import java.util.stream.Collectors;

public class VehicleService {
    private final Repository<Vehicle> repo;
    private final CustomerService customers;

    public VehicleService(Repository<Vehicle> repo, CustomerService customers) {
        this.repo = repo;
        this.customers = customers;
    }

    public Vehicle register(String regNo, String type, String brand, String model, String ownerId) throws VscException {
        String reg = Validator.requireRegNo(regNo);
        String t = Validator.requireText(type, "Vehicle type");
        String b = Validator.requireText(brand, "Brand");
        String m = Validator.requireText(model, "Model");
        Customer owner = customers.getById(ownerId);
        if (repo.exists(reg)) throw new DuplicateEntityException("Vehicle " + reg + " is already registered.");
        Vehicle v = new Vehicle(reg, t, b, m, owner.getId());
        repo.save(v);
        owner.addVehicle(reg);
        customers.save(owner);
        return v;
    }

    public Vehicle getByRegNo(String regNo) throws EntityNotFoundException {
        return repo.findById(Validator.safe(regNo).toUpperCase())
                .orElseThrow(() -> new EntityNotFoundException("Vehicle " + regNo + " not found."));
    }

    public List<Vehicle> byOwner(String ownerId) {
        return repo.findAll().stream().filter(v -> v.getOwnerId().equalsIgnoreCase(Validator.safe(ownerId)))
                .collect(Collectors.toList());
    }

    public List<Vehicle> all() { return repo.findAll(); }
}
