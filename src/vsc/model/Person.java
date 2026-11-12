package vsc.model;

/** Abstract base class for people in the system (encapsulated fields). */
// OOP: ABSTRACTION - abstract class. It cannot be created with 'new'; every subclass must implement getRole().
public abstract class Person implements Identifiable {
    private static final long serialVersionUID = 1L;
    // OOP: ENCAPSULATION - fields are private; other classes use getters/setters only.
    private final String id;
    private String name;
    private String phone;
    private String email;

    protected Person(String id, String name, String phone, String email) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    public abstract String getRole();

    @Override public String getId() { return id; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public void setName(String name) { this.name = name; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() {
        return String.format("%s | %-20s | %-12s | %s", id, name, phone, email == null ? "" : email);
    }
}
