package vsc.service;

import vsc.exception.*;
import vsc.model.Mechanic;
import vsc.repository.Repository;
import vsc.util.Validator;

import java.util.List;

public class MechanicService {
    private final Repository<Mechanic> repo;

    public MechanicService(Repository<Mechanic> repo) { this.repo = repo; }

    public Mechanic register(String name, String phone, String specialization) throws VscException {
        Mechanic m = new Mechanic(repo.nextId("M"), Validator.requireText(name, "Name"),
                Validator.requirePhone(phone), Validator.requireText(specialization, "Specialization"));
        repo.save(m);
        return m;
    }

    public Mechanic getById(String id) throws EntityNotFoundException {
        return repo.findById(Validator.safe(id).toUpperCase())
                .orElseThrow(() -> new EntityNotFoundException("Mechanic " + id + " not found."));
    }

    public List<Mechanic> all() { return repo.findAll(); }
}
