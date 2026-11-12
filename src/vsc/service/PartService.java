package vsc.service;

import vsc.exception.*;
import vsc.model.SparePart;
import vsc.repository.Repository;
import vsc.util.Validator;

import java.util.List;
import java.util.stream.Collectors;

public class PartService {
    private final Repository<SparePart> repo;

    public PartService(Repository<SparePart> repo) { this.repo = repo; }

    public SparePart addPart(String name, int qty, double price, int reorderLevel) throws VscException {
        String n = Validator.requireText(name, "Part name");
        Validator.requireNonNegative(qty, "Quantity");
        Validator.requirePositive(price, "Unit price");
        Validator.requireNonNegative(reorderLevel, "Reorder level");
        SparePart p = new SparePart(repo.nextId("P"), n, qty, price, reorderLevel);
        repo.save(p);
        return p;
    }

    public SparePart restock(String id, int qty) throws VscException {
        Validator.requirePositive(qty, "Restock quantity");
        SparePart p = getById(id);
        p.addStock(qty);
        repo.save(p);
        return p;
    }

    /** Deducts stock; throws InsufficientStockException if not enough. */
    public SparePart consume(String id, int qty) throws VscException {
        Validator.requirePositive(qty, "Quantity");
        SparePart p = getById(id);
        p.removeStock(qty);
        repo.save(p);
        return p;
    }

    public SparePart getById(String id) throws EntityNotFoundException {
        return repo.findById(Validator.safe(id).toUpperCase())
                .orElseThrow(() -> new EntityNotFoundException("Spare part " + id + " not found."));
    }

    public List<SparePart> lowStock() {
        return repo.findAll().stream().filter(SparePart::isLowStock).collect(Collectors.toList());
    }

    public List<SparePart> all() { return repo.findAll(); }
}
