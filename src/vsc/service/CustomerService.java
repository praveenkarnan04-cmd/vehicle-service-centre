package vsc.service;

import vsc.exception.*;
import vsc.model.Customer;
import vsc.repository.Repository;
import vsc.util.Validator;

import java.util.List;
import java.util.stream.Collectors;

public class CustomerService {
    private final Repository<Customer> repo;

    public CustomerService(Repository<Customer> repo) { this.repo = repo; }

    public Customer register(String name, String phone, String email) throws VscException {
        String n = Validator.requireText(name, "Name");
        String p = Validator.requirePhone(phone);
        String e = Validator.optionalEmail(email);
        if (repo.findAll().stream().anyMatch(c -> c.getPhone().equals(p)))
            throw new DuplicateEntityException("A customer with phone " + p + " already exists.");
        Customer c = new Customer(repo.nextId("C"), n, p, e);
        repo.save(c);
        return c;
    }

    public Customer update(String id, String name, String phone, String email) throws VscException {
        Customer c = getById(id);
        if (name != null && !name.isBlank()) c.setName(name.trim());
        if (phone != null && !phone.isBlank()) c.setPhone(Validator.requirePhone(phone));
        if (email != null && !email.isBlank()) c.setEmail(Validator.optionalEmail(email));
        repo.save(c);
        return c;
    }

    public void save(Customer c) throws VscException { repo.save(c); }

    public Customer getById(String id) throws EntityNotFoundException {
        return repo.findById(Validator.safe(id).toUpperCase())
                .orElseThrow(() -> new EntityNotFoundException("Customer " + id + " not found."));
    }

    public List<Customer> search(String keyword) {
        String k = Validator.safe(keyword).toLowerCase();
        return repo.findAll().stream()
                .filter(c -> c.getName().toLowerCase().contains(k) || c.getPhone().contains(k)
                        || c.getId().toLowerCase().equals(k))
                .collect(Collectors.toList());
    }

    public List<Customer> all() { return repo.findAll(); }
}
