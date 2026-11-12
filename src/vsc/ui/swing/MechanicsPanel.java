package vsc.ui.swing;

import vsc.exception.VscException;
import vsc.model.JobStatus;

class MechanicsPanel extends BasePanel {
    private final AppContext ctx;

    MechanicsPanel(AppContext ctx) {
        super("ID", "Name", "Phone", "Specialization", "Active jobs");
        this.ctx = ctx;
        button("Register Mechanic", this::register);
        button("View Assigned Work", () -> {
            String id = selectedId("mechanic");
            showTable("Work assigned to " + id, Rows.HISTORY_COLS,
                    ctx.jobs.byMechanic(id).stream().map(Rows::history).toList());
        });
    }

    @Override
    public void refresh() {
        fill(ctx.mechanics.all().stream().map(m -> new Object[]{m.getId(), m.getName(), m.getPhone(),
                m.getSpecialization(), ctx.jobs.byMechanic(m.getId()).stream()
                .filter(r -> r.getStatus() != JobStatus.COMPLETED).count()}).toList());
    }

    private void register() throws VscException {
        Form f = new Form();
        var n = f.text("Name", ""); var p = f.text("Phone", ""); var s = f.text("Specialization", "");
        if (!f.show(this, "Register Mechanic")) return;
        ctx.mechanics.register(n.getText(), p.getText(), s.getText());
    }
}
