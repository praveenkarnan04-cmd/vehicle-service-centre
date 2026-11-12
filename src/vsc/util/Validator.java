package vsc.util;

import vsc.exception.InvalidInputException;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import vsc.model.Appointment;

/** Central input validation; throws InvalidInputException on bad data. */
public final class Validator {
    private Validator() { }

    public static String safe(String v) { return v == null ? "" : v.trim(); }

    public static String requireText(String v, String field) throws InvalidInputException {
        if (v == null || v.trim().isEmpty()) throw new InvalidInputException(field + " cannot be empty.");
        return v.trim();
    }

    public static String requirePhone(String v) throws InvalidInputException {
        String p = requireText(v, "Phone").replaceAll("[\\s-]", "");
        if (!p.matches("\\+?\\d{9,12}")) throw new InvalidInputException("Phone must be 9-12 digits.");
        return p;
    }

    public static String optionalEmail(String v) throws InvalidInputException {
        if (v == null || v.trim().isEmpty()) return "";
        String e = v.trim();
        if (!e.matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$")) throw new InvalidInputException("Invalid email address.");
        return e;
    }

    public static String requireRegNo(String v) throws InvalidInputException {
        String r = requireText(v, "Registration number").toUpperCase();
        if (!r.matches("[A-Z0-9]{1,3}-\\d{4}")) throw new InvalidInputException("Registration must look like CAB-1234.");
        return r;
    }

    public static double requirePositive(double v, String field) throws InvalidInputException {
        if (v <= 0) throw new InvalidInputException(field + " must be greater than zero.");
        return v;
    }

    public static int requireNonNegative(int v, String field) throws InvalidInputException {
        if (v < 0) throw new InvalidInputException(field + " cannot be negative.");
        return v;
    }

    public static LocalDateTime parseDateTime(String v) throws InvalidInputException {
        try {
            return LocalDateTime.parse(requireText(v, "Date/time"), Appointment.FORMAT);
        } catch (DateTimeParseException e) {
            throw new InvalidInputException("Use the format yyyy-MM-dd HH:mm (e.g. 2026-11-20 10:00).");
        }
    }
}
