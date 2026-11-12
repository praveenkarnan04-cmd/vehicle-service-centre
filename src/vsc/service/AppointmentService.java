package vsc.service;

import vsc.exception.*;
import vsc.model.Appointment;
import vsc.repository.Repository;
import vsc.util.Validator;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/** Books appointments while preventing overlaps beyond the number of service bays. */
public class AppointmentService {
    public static final int BAYS = 3;
    private static final LocalTime OPEN = LocalTime.of(8, 0);
    private static final LocalTime CLOSE = LocalTime.of(17, 0);

    private final Repository<Appointment> repo;
    private final VehicleService vehicles;

    public AppointmentService(Repository<Appointment> repo, VehicleService vehicles) {
        this.repo = repo;
        this.vehicles = vehicles;
    }

    public Appointment book(String regNo, LocalDateTime start) throws VscException {
        var v = vehicles.getByRegNo(regNo);
        checkSlot(start, v.getRegNo(), null);
        Appointment a = new Appointment(repo.nextId("A"), v.getOwnerId(), v.getRegNo(), start);
        repo.save(a);
        return a;
    }

    public Appointment reschedule(String id, LocalDateTime newStart) throws VscException {
        Appointment a = getById(id);
        if (a.isCancelled()) throw new VscException("Cancelled appointments cannot be changed.");
        checkSlot(newStart, a.getVehicleRegNo(), a.getId());
        a.setStart(newStart);
        repo.save(a);
        return a;
    }

    public Appointment cancel(String id) throws VscException {
        Appointment a = getById(id);
        if (a.isCancelled()) throw new VscException("Appointment is already cancelled.");
        a.cancel();
        repo.save(a);
        return a;
    }

    public int freeBays(LocalDateTime start) { return BAYS - overlapping(start, null).size(); }

    public List<Appointment> upcoming() {
        LocalDateTime now = LocalDateTime.now();
        return repo.findAll().stream()
                .filter(a -> !a.isCancelled() && a.getStart().isAfter(now))
                .sorted(Comparator.comparing(Appointment::getStart))
                .collect(Collectors.toList());
    }

    public List<Appointment> all() {
        return repo.findAll().stream().sorted(Comparator.comparing(Appointment::getStart)).collect(Collectors.toList());
    }

    public Appointment getById(String id) throws EntityNotFoundException {
        return repo.findById(Validator.safe(id).toUpperCase())
                .orElseThrow(() -> new EntityNotFoundException("Appointment " + id + " not found."));
    }

    private void checkSlot(LocalDateTime start, String regNo, String excludeId) throws VscException {
        if (start.isBefore(LocalDateTime.now())) throw new InvalidInputException("Appointment must be in the future.");
        LocalTime t = start.toLocalTime();
        if (t.isBefore(OPEN) || t.plusMinutes(Appointment.DURATION_MINUTES).isAfter(CLOSE))
            throw new InvalidInputException("Open hours are 08:00-17:00 (last slot starts 16:00).");
        List<Appointment> clash = overlapping(start, excludeId);
        if (clash.stream().anyMatch(a -> a.getVehicleRegNo().equals(regNo)))
            throw new AppointmentConflictException("This vehicle already has an overlapping appointment.");
        if (clash.size() >= BAYS)
            throw new AppointmentConflictException("All " + BAYS + " service bays are booked at that time.");
    }

    private List<Appointment> overlapping(LocalDateTime start, String excludeId) {
        return repo.findAll().stream()
                .filter(a -> !a.isCancelled() && !a.getId().equals(excludeId))
                .filter(a -> Math.abs(Duration.between(a.getStart(), start).toMinutes()) < Appointment.DURATION_MINUTES)
                .collect(Collectors.toList());
    }
}
