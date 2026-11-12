package vsc.ui.swing;

import vsc.exception.VscException;
import vsc.model.*;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

class BillingPanel extends BasePanel {
    private final AppContext ctx;
    private final JCheckBox outstandingOnly = new JCheckBox("Outstanding only");

    BillingPanel(AppContext ctx) {
        super("Bill ID", "Date", "Service", "Customer", "Total (Rs)", "Paid (Rs)", "Balance (Rs)");
        this.ctx = ctx;
        button("Generate Bill", this::generate);
        button("Record Payment", this::pay);
        button("View Bill", this::view);
        toolbar.add(outstandingOnly);
        outstandingOnly.addActionListener(e -> refresh());
    }

    @Override
    public void refresh() {
        List<Bill> bills = outstandingOnly.isSelected() ? ctx.billing.outstanding() : ctx.billing.all();
        fill(bills.stream().map(b -> new Object[]{b.getId(), b.getDate(), b.getServiceId(),
                ctx.customerName(b.getCustomerId()), Rows.money(b.calculateCharge()), Rows.money(b.getPaid()),
                Rows.money(b.getBalance())}).toList());
    }

    private void generate() throws VscException {
        List<String> ready = ctx.jobs.all().stream()
                .filter(r -> r.getStatus() == JobStatus.COMPLETED)
                .filter(r -> ctx.billing.all().stream().noneMatch(b -> b.getServiceId().equals(r.getId())))
                .map(r -> r.getId() + " - " + r.getVehicleRegNo() + " - " + r.getServiceType().getName()).toList();
        if (ready.isEmpty()) throw new VscException("No completed jobs are waiting for a bill.");
        Form f = new Form();
        var job = f.choice("Completed job", ready);
        if (!f.show(this, "Generate Bill")) return;
        Bill b = ctx.billing.generateBill(Form.idOf(job));
        info(String.format("Bill %s generated. Total: Rs. %s", b.getId(), Rows.money(b.calculateCharge())));
    }

    private void pay() throws VscException {
        Bill b = ctx.billing.getById(selectedId("bill"));
        Form f = new Form();
        var amt = f.text(String.format("Amount (balance Rs. %s)", Rows.money(b.getBalance())), String.valueOf(b.getBalance()));
        if (!f.show(this, "Record payment for " + b.getId())) return;
        Bill updated = ctx.billing.recordPayment(b.getId(), Form.number(amt, "Amount"));
        info(String.format("Payment saved. Remaining balance: Rs. %s", Rows.money(updated.getBalance())));
    }

    private void view() throws VscException {
        String id = selectedId("bill");
        String text;
        try {
            text = Files.readString(Paths.get("bills", id + ".txt"));
        } catch (IOException e) {
            throw new VscException("Bill file for " + id + " could not be read: " + e.getMessage());
        }
        JTextArea area = new JTextArea(text, 14, 52);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        area.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Bill " + id, JOptionPane.PLAIN_MESSAGE);
    }
}
