package vsc.ui.swing;

import vsc.exception.VscException;
import vsc.model.Appointment;
import vsc.util.Validator;

import javax.swing.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

class AppointmentsPanel extends BasePanel {
    private static final List<String> TIMES = slots();
    private final AppContext ctx;
    private final JCheckBox upcomingOnly = new JCheckBox("Upcoming only");

    AppointmentsPanel(AppContext ctx) {
        super("ID", "Date & time", "Vehicle", "Customer", "Status");
        this.ctx = ctx;
        button("Book", this::book);
        button("Reschedule", this::reschedule);
        button("Cancel Booking", this::cancel);
        button("Check Availability", this::availability);
        toolbar.add(upcomingOnly);
        upcomingOnly.addActionListener(e -> refresh());
    }

    private static List<String> slots() {
        List<String> t = new ArrayList<>();
        for (int h = 8; h <= 16; h++) t.add(String.format("%02d:00", h));
        return t;
    }

    @Override
    public void refresh() {
        List<Appointment> list = upcomingOnly.isSelected() ? ctx.appointments.upcoming() : ctx.appointments.all();
        fill(list.stream().map(a -> new Object[]{a.getId(), a.getStart().format(Appointment.FORMAT), a.getVehicleRegNo(),
                ctx.customerName(a.getCustomerId()), a.isCancelled() ? "Cancelled" : "Booked"}).toList());
    }

    private void book() throws VscException {
        List<String> vs = ctx.vehicles.all().stream().map(v -> v.getRegNo() + " - " + ctx.customerName(v.getOwnerId())).toList();
        if (vs.isEmpty()) throw new VscException("Register a vehicle first.");
        Form f = new Form();
        var veh = f.choice("Vehicle", vs);
        var date = f.text("Date (yyyy-MM-dd)", LocalDate.now().plusDays(1).toString());
        var time = f.choice("Time", TIMES);
        if (!f.show(this, "Book Appointment")) return;
        Appointment a = ctx.appointments.book(Form.idOf(veh), when(date, time));
        info("Booked " + a.getId() + " for " + a.getStart().format(Appointment.FORMAT));
    }

    private void reschedule() throws VscException {
        Appointment a = ctx.appointments.getById(selectedId("appointment"));
        Form f = new Form();
        var date = f.text("New date (yyyy-MM-dd)", a.getStart().toLocalDate().toString());
        var time = f.choice("New time", TIMES);
        if (!f.show(this, "Reschedule " + a.getId())) return;
        ctx.appointments.reschedule(a.getId(), when(date, time));
    }

    private void cancel() throws VscException {
        String id = selectedId("appointment");
        if (JOptionPane.showConfirmDialog(this, "Cancel appointment " + id + "?", "Confirm",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) ctx.appointments.cancel(id);
    }

    private void availability() throws VscException {
        Form f = new Form();
        var date = f.text("Date (yyyy-MM-dd)", LocalDate.now().plusDays(1).toString());
        var time = f.choice("Time", TIMES);
        if (!f.show(this, "Check Availability")) return;
        LocalDateTime t = when(date, time);
        info(ctx.appointments.freeBays(t) + " of " + vsc.service.AppointmentService.BAYS + " service bays free at "
                + t.format(Appointment.FORMAT));
    }

    private LocalDateTime when(JTextField date, JComboBox<String> time) throws VscException {
        return Validator.parseDateTime(date.getText().trim() + " " + time.getSelectedItem());
    }
}
