package vsc.ui.swing;

import vsc.exception.VscException;
import vsc.model.SparePart;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

class PartsPanel extends BasePanel {
    private final AppContext ctx;

    PartsPanel(AppContext ctx) {
        super("ID", "Part", "Quantity", "Unit price (Rs)", "Reorder level", "Status");
        this.ctx = ctx;
        button("Add Part", this::add);
        button("Restock", this::restock);
        button("Low-stock Report", () -> showTable("Low-stock warnings",
                new String[]{"ID", "Part", "Quantity", "Reorder level"},
                ctx.parts.lowStock().stream().map(p -> new Object[]{p.getId(), p.getName(), p.getQuantity(), p.getReorderLevel()}).toList()));
    }

    @Override
    public void refresh() {
        fill(ctx.parts.all().stream().map(p -> new Object[]{p.getId(), p.getName(), p.getQuantity(),
                Rows.money(p.getUnitPrice()), p.getReorderLevel(), p.isLowStock() ? "LOW STOCK" : "OK"}).toList());
    }

    private void add() throws VscException {
        Form f = new Form();
        var n = f.text("Part name", ""); var q = f.text("Quantity", "0");
        var pr = f.text("Unit price (Rs)", ""); var ro = f.text("Reorder level", "5");
        if (!f.show(this, "Add Spare Part")) return;
        ctx.parts.addPart(n.getText(), Form.whole(q, "Quantity"), Form.number(pr, "Unit price"), Form.whole(ro, "Reorder level"));
    }

    private void restock() throws VscException {
        String id = selectedId("part");
        Form f = new Form();
        var q = f.text("Quantity to add", "10");
        if (!f.show(this, "Restock " + id)) return;
        SparePart p = ctx.parts.restock(id, Form.whole(q, "Quantity"));
        info(p.getName() + " now has " + p.getQuantity() + " in stock.");
    }
}
