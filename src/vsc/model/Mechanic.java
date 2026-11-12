package vsc.model;

// OOP: INHERITANCE - Mechanic reuses everything in Person and adds specialization.
public class Mechanic extends Person {
    private static final long serialVersionUID = 1L;
    private String specialization;

    public Mechanic(String id, String name, String phone, String specialization) {
        super(id, name, phone, null);
        this.specialization = specialization;
    }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String s) { this.specialization = s; }

    @Override public String getRole() { return "Mechanic"; }

    @Override
    public String toString() { return String.format("%s | %-20s | %-12s | %s", getId(), getName(), getPhone(), specialization); }
}
