package vsc.ui.swing;

import vsc.exception.VscException;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/** Main window of the desktop application. */
public final class SwingApp {
    private SwingApp() { }

    public static void launch() {
        SwingUtilities.invokeLater(SwingApp::create);
    }

    private static void create() {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) { }
        AppContext ctx = new AppContext();
        JFrame frame = new JFrame("Vehicle Service Centre Management System");
        JPanel root = buildRoot(ctx);
        Runnable refreshAll = (Runnable) root.getClientProperty("refreshAll");

        JMenuBar bar = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenuItem demo = new JMenuItem("Load demo data");
        demo.addActionListener(e -> {
            try { ctx.loadDemo(); refreshAll.run(); }
            catch (VscException ex) { JOptionPane.showMessageDialog(frame, ex.getMessage(), "Demo data", JOptionPane.WARNING_MESSAGE); }
        });
        JMenuItem refresh = new JMenuItem("Refresh all");
        refresh.addActionListener(e -> refreshAll.run());
        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(e -> System.exit(0));
        file.add(demo); file.add(refresh); file.addSeparator(); file.add(exit);
        bar.add(file);

        frame.setJMenuBar(bar);
        frame.setContentPane(root);
        frame.setSize(1180, 740);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        refreshAll.run();
        frame.setVisible(true);
    }

    /** Builds header + tabs. The returned panel carries its refresh action as client property "refreshAll". */
    static JPanel buildRoot(AppContext ctx) {
        DashboardPanel dashboard = new DashboardPanel(ctx);
        List<BasePanel> panels = new ArrayList<>();
        JTabbedPane tabs = new JTabbedPane();
        tabs.setUI(new ColorTabUI());
        tabs.setOpaque(true);
        tabs.setBackground(Theme.BG);
        tabs.setFont(tabs.getFont().deriveFont(Font.BOLD, 14f));
        tabs.addTab("Dashboard", dashboard);
        String[] names = {"Customers", "Vehicles", "Service Jobs", "Mechanics", "Spare Parts", "Billing", "Appointments"};
        BasePanel[] all = {new CustomersPanel(ctx), new VehiclesPanel(ctx), new JobsPanel(ctx), new MechanicsPanel(ctx),
                new PartsPanel(ctx), new BillingPanel(ctx), new AppointmentsPanel(ctx)};
        for (int i = 0; i < all.length; i++) { tabs.addTab(names[i], all[i]); panels.add(all[i]); }

        Runnable refreshAll = () -> { dashboard.refresh(); panels.forEach(BasePanel::refresh); };
        panels.forEach(p -> p.setRefreshAll(refreshAll));
        tabs.addChangeListener(e -> refreshAll.run());

        GradientPanel header = new GradientPanel(new Color(0x3730A3), new Color(0x06B6D4));
        header.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));
        JLabel title = new JLabel("Vehicle Service Centre Management System");
        title.setForeground(Color.WHITE);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 24f));
        JLabel sub = new JLabel("Customers  •  Vehicles  •  Service jobs  •  Mechanics  •  Spare parts  •  Billing  •  Appointments");
        sub.setForeground(new Color(255, 255, 255, 210));
        JPanel text = new JPanel(new GridLayout(2, 1, 0, 2));
        text.setOpaque(false);
        text.add(title);
        text.add(sub);
        JLabel date = new JLabel(LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy")));
        date.setForeground(Color.WHITE);
        date.setFont(date.getFont().deriveFont(Font.BOLD, 15f));
        header.add(text, BorderLayout.WEST);
        header.add(date, BorderLayout.EAST);

        JPanel root = new JPanel(new BorderLayout(0, 6));
        root.setBackground(Theme.BG);
        root.add(header, BorderLayout.NORTH);
        JPanel body = new JPanel(new BorderLayout());
        body.setOpaque(false);
        body.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 8));
        body.add(tabs, BorderLayout.CENTER);
        root.add(body, BorderLayout.CENTER);
        root.putClientProperty("refreshAll", refreshAll);
        return root;
    }
}
