package vsc.ui.swing;

import vsc.exception.VscException;
import vsc.model.Customer;

import java.util.List;

class VehiclesPanel extends BasePanel {
    private final AppContext ctx;

    VehiclesPanel(AppContext ctx) {
        super("Reg No", "Type", "Brand", "Model", "Owner ID", "Owner");
        this.ctx = ctx;
        button("Register Vehicle", this::register);
        button("Service History", this::history);
    }

    @Override
    public void refresh() {
        fill(ctx.vehicles.all().stream().map(v -> new Object[]{v.getRegNo(), v.getType(), v.getBrand(), v.getModel(),
                v.getOwnerId(), ctx.customerName(v.getOwnerId())}).toList());
    }

    private void register() throws VscException {
        List<String> owners = ctx.customers.all().stream().map(c -> c.getId() + " - " + c.getName()).toList();
        if (owners.isEmpty()) throw new VscException("Register a customer first.");
        Form f = new Form();
        var reg = f.text("Reg no (e.g. CAB-1234)", "");
        var type = f.choice("Type", List.of("Car", "Van", "Motorbike", "Three-wheeler", "SUV", "Lorry", "Bus"));
        var brand = f.text("Brand", "");
        var model = f.text("Model", "");
        var owner = f.choice("Owner", owners);
        if (!f.show(this, "Register Vehicle")) return;
        ctx.vehicles.register(reg.getText(), String.valueOf(type.getSelectedItem()), brand.getText(),
                model.getText(), Form.idOf(owner));
    }

    private void history() throws VscException {
        String reg = selectedId("vehicle");
        showTable("Service history - " + reg, Rows.HISTORY_COLS, ctx.jobs.byVehicle(reg).stream().map(Rows::history).toList());
    }
}
