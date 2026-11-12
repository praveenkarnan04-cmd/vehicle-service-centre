package vsc.ui.swing;

import vsc.exception.VscException;
import vsc.model.Customer;

import javax.swing.*;
import java.util.List;

class CustomersPanel extends BasePanel {
    private final AppContext ctx;
    private final JTextField search = new JTextField(14);

    CustomersPanel(AppContext ctx) {
        super("ID", "Name", "Phone", "Email", "Vehicles");
        this.ctx = ctx;
        button("Add Customer", this::add);
        button("Edit Selected", this::edit);
        button("Service History", this::history);
        toolbar.add(new JLabel("   Search:"));
        toolbar.add(search);
        button("Go", () -> { });
        button("Clear", () -> search.setText(""));
    }

    @Override
    public void refresh() {
        String k = search.getText().trim();
        List<Customer> list = k.isEmpty() ? ctx.customers.all() : ctx.customers.search(k);
        fill(list.stream().map(c -> new Object[]{c.getId(), c.getName(), c.getPhone(), c.getEmail(),
                String.join(", ", c.getVehicleRegNos())}).toList());
    }

    private void add() throws VscException {
        Form f = new Form();
        JTextField n = f.text("Name", ""), p = f.text("Phone", ""), e = f.text("Email (optional)", "");
        if (!f.show(this, "Register Customer")) return;
        Customer c = ctx.customers.register(n.getText(), p.getText(), e.getText());
        info("Registered " + c.getId() + " - " + c.getName());
    }

    private void edit() throws VscException {
        Customer c = ctx.customers.getById(selectedId("customer"));
        Form f = new Form();
        JTextField n = f.text("Name", c.getName()), p = f.text("Phone", c.getPhone()), e = f.text("Email", c.getEmail());
        if (!f.show(this, "Edit " + c.getId())) return;
        ctx.customers.update(c.getId(), n.getText(), p.getText(), e.getText());
    }

    private void history() throws VscException {
        String id = selectedId("customer");
        showTable("Service history - " + ctx.customerName(id), Rows.HISTORY_COLS,
                ctx.jobs.byCustomer(id).stream().map(Rows::history).toList());
    }
}
