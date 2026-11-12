package vsc.ui.swing;

import vsc.model.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;

class DashboardPanel extends JPanel implements Refreshable {
    private final AppContext ctx;
    private final StatCard customers = new StatCard("Customers", Theme.BLUE);
    private final StatCard vehicles = new StatCard("Vehicles", Theme.TEAL);
    private final StatCard active = new StatCard("Active jobs", Theme.AMBER);
    private final StatCard low = new StatCard("Low-stock parts", Theme.GREEN);
    private final StatCard outstanding = new StatCard("Outstanding (Rs)", Theme.PURPLE);
    private final StatCard upcoming = new StatCard("Upcoming appointments", Theme.PINK);
    private final JTextArea lowArea = area(), apptArea = area();

    DashboardPanel(AppContext ctx) {
        super(new BorderLayout(12, 12));
        this.ctx = ctx;
        setBackground(Theme.BG);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel cards = new JPanel(new GridLayout(2, 3, 12, 12));
        cards.setOpaque(false);
        for (StatCard c : new StatCard[]{customers, vehicles, active, low, outstanding, upcoming}) cards.add(c);
        cards.setPreferredSize(new Dimension(100, 230));
        JPanel lists = new JPanel(new GridLayout(1, 2, 12, 12));
        lists.setOpaque(false);
        lists.add(titled("Low-stock warnings", lowArea, Theme.RED));
        lists.add(titled("Next appointments", apptArea, Theme.PINK));
        add(cards, BorderLayout.NORTH);
        add(lists, BorderLayout.CENTER);
    }

    private static JTextArea area() {
        JTextArea a = new JTextArea();
        a.setEditable(false);
        a.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        a.setMargin(new Insets(6, 8, 6, 8));
        return a;
    }

    private static JPanel titled(String title, JTextArea a, Color color) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        TitledBorder tb = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(color, 2, true), "  " + title + "  ");
        tb.setTitleColor(color);
        tb.setTitleFont(tb.getTitleFont().deriveFont(Font.BOLD, 14f));
        p.setBorder(tb);
        p.add(new JScrollPane(a), BorderLayout.CENTER);
        return p;
    }

    @Override
    public void refresh() {
        customers.setValue(String.valueOf(ctx.customers.all().size()));
        vehicles.setValue(String.valueOf(ctx.vehicles.all().size()));
        active.setValue(String.valueOf(ctx.jobs.all().stream().filter(r -> r.getStatus() != JobStatus.COMPLETED).count()));
        List<SparePart> lowParts = ctx.parts.lowStock();
        low.setValue(String.valueOf(lowParts.size()));
        low.setColor(lowParts.isEmpty() ? Theme.GREEN : Theme.RED);
        outstanding.setValue(Rows.money(ctx.billing.outstanding().stream().mapToDouble(Bill::getBalance).sum()));
        List<Appointment> next = ctx.appointments.upcoming();
        upcoming.setValue(String.valueOf(next.size()));
        lowArea.setForeground(Theme.RED);
        lowArea.setText(lowParts.isEmpty() ? "All stock levels are fine."
                : String.join("\n", lowParts.stream().map(SparePart::toString).toList()));
        apptArea.setText(next.isEmpty() ? "No upcoming appointments."
                : String.join("\n", next.stream().limit(8).map(Appointment::toString).toList()));
    }
}
