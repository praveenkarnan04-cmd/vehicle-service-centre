package vsc.repository;

import vsc.exception.VscException;
import vsc.model.Identifiable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Generic repository that keeps entities in a map and persists them to a file in ./data. */
// OOP: GENERICS + COLLECTIONS + FILE HANDLING - one class stores any entity in a Map and saves it to a file.
public class FileRepository<T extends Identifiable> implements Repository<T> {
    private final Path file;
    private Map<String, T> store = new LinkedHashMap<>();

    public FileRepository(String filename) {
        this.file = Paths.get("data", filename);
        load();
    }

    @SuppressWarnings("unchecked")
    private void load() {
        if (!Files.exists(file)) return;
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(file))) {
            store = (Map<String, T>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Warning: could not read " + file + " (" + e.getMessage() + "). Starting empty.");
        }
    }

    private void persist() throws VscException {
        try {
            Files.createDirectories(file.getParent());
            try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(file))) {
                out.writeObject(store);
            }
        } catch (IOException e) {
            throw new VscException("Failed to save data to " + file + ": " + e.getMessage(), e);
        }
    }

    @Override
    public void save(T entity) throws VscException {
        store.put(entity.getId(), entity);
        persist();
    }

    @Override public Optional<T> findById(String id) { return Optional.ofNullable(store.get(id)); }
    @Override public List<T> findAll() { return new ArrayList<>(store.values()); }
    @Override public boolean exists(String id) { return store.containsKey(id); }

    @Override
    public void delete(String id) throws VscException {
        store.remove(id);
        persist();
    }

    @Override
    public String nextId(String prefix) {
        int max = 0;
        for (String key : store.keySet()) {
            if (key.startsWith(prefix)) {
                try { max = Math.max(max, Integer.parseInt(key.substring(prefix.length()))); }
                catch (NumberFormatException ignored) { }
            }
        }
        return String.format("%s%03d", prefix, max + 1);
    }
}
