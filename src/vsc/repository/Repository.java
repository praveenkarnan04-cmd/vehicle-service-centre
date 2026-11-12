package vsc.repository;

import vsc.exception.VscException;
import vsc.model.Identifiable;

import java.util.List;
import java.util.Optional;

/** Generic repository contract. */
// OOP: INTERFACE + GENERICS - T is a placeholder for Customer, Bill, SparePart...
public interface Repository<T extends Identifiable> {
    void save(T entity) throws VscException;
    Optional<T> findById(String id);
    List<T> findAll();
    boolean exists(String id);
    void delete(String id) throws VscException;
    String nextId(String prefix);
}
