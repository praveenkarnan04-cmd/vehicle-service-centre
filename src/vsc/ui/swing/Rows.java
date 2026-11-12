package vsc.ui.swing;

import vsc.model.ServiceRecord;

final class Rows {
    private Rows() { }

    static final String[] HISTORY_COLS = {"Job", "Date", "Vehicle", "Service", "Status", "Mechanic", "Labour (Rs)", "Parts (Rs)", "Complaint"};

    static String money(double v) { return String.format("%,.2f", v); }

    static Object[] history(ServiceRecord r) {
        return new Object[]{r.getId(), r.getDate(), r.getVehicleRegNo(), r.getServiceType().getName(), r.getStatus(),
                r.getMechanicId() == null ? "-" : r.getMechanicId(), money(r.getLabourCharge()),
                money(r.getPartsCost()), r.getComplaint()};
    }
}
