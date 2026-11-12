package vsc.ui.swing;

import vsc.exception.VscException;
import vsc.model.*;

import java.util.List;

class JobsPanel extends BasePanel {
    private final AppContext ctx;

    JobsPanel(AppContext ctx) {
        super("Job ID", "Date", "Vehicle", "Customer", "Service", "Status", "Mechanic", "Complaint", "Labour (Rs)", "Parts (Rs)");
        this.ctx = ctx;
        button("New Job", this::newJob);
        button("Assign Mechanic", this::assign);
        button("Start Job", () -> ctx.jobs.updateStatus(selectedId("job"), JobStatus.IN_PROGRESS));
        button("Complete Job", () -> ctx.jobs.updateStatus(selectedId("job"), JobStatus.COMPLETED));
        button("Add Used Part", this::addPart);
        button("Generate Bill", this::bill);
    }

    @Override
    public void refresh() {
        fill(ctx.jobs.all().stream().map(r -> new Object[]{r.getId(), r.getDate(), r.getVehicleRegNo(),
                ctx.customerName(r.getCustomerId()), r.getServiceType().getName(), r.getStatus(),
                r.getMechanicId() == null ? "-" : r.getMechanicId(), r.getComplaint(),
                Rows.money(r.getLabourCharge()), Rows.money(r.getPartsCost())}).toList());
    }

    private void newJob() throws VscException {
        List<String> vs = ctx.vehicles.all().stream()
                .map(v -> v.getRegNo() + " - " + v.getBrand() + " " + v.getModel()).toList();
        if (vs.isEmpty()) throw new VscException("Register a vehicle first.");
        Form f = new Form();
        var veh = f.choice("Vehicle", vs);
        var complaint = f.text("Customer complaint", "");
        var type = f.choice("Service type", List.of("Oil Change", "Repair", "Full Service"));
        var hours = f.text("Repair hours (Repair only)", "1.0");
        if (!f.show(this, "Create Service Job")) return;
        ServiceType t = switch (type.getSelectedIndex()) {
            case 0 -> new OilChange();
            case 1 -> new Repair(vsc.util.Validator.requirePositive(Form.number(hours, "Hours"), "Hours"));
            default -> new FullService();
        };
        ServiceRecord r = ctx.jobs.create(Form.idOf(veh), complaint.getText(), t);
        info(String.format("Job %s created. Labour charge: Rs. %s", r.getId(), Rows.money(r.getLabourCharge())));
    }

    private void assign() throws VscException {
        String id = selectedId("job");
        List<String> ms = ctx.mechanics.all().stream().map(m -> m.getId() + " - " + m.getName()).toList();
        if (ms.isEmpty()) throw new VscException("Register a mechanic first.");
        Form f = new Form();
        var mech = f.choice("Mechanic", ms);
        if (!f.show(this, "Assign mechanic to " + id)) return;
        ctx.jobs.assignMechanic(id, Form.idOf(mech));
    }

    private void addPart() throws VscException {
        String id = selectedId("job");
        List<String> ps = ctx.parts.all().stream()
                .map(p -> p.getId() + " - " + p.getName() + " (" + p.getQuantity() + " in stock)").toList();
        if (ps.isEmpty()) throw new VscException("Add spare parts first.");
        Form f = new Form();
        var part = f.choice("Part", ps);
        var qty = f.text("Quantity", "1");
        if (!f.show(this, "Record used part for " + id)) return;
        ctx.jobs.addUsedPart(id, Form.idOf(part), Form.whole(qty, "Quantity"));
    }

    private void bill() throws VscException {
        Bill b = ctx.billing.generateBill(selectedId("job"));
        info(String.format("Bill %s generated. Total: Rs. %s", b.getId(), Rows.money(b.calculateCharge())));
    }
}
