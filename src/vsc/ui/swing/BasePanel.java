package vsc.ui.swing;

import vsc.exception.VscException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/** Abstract tab: a toolbar, a sortable table and shared error handling. Subclasses supply data. */
// OOP: ABSTRACTION + INHERITANCE - every tab extends this; each subclass supplies its own refresh().
abstract class BasePanel extends JPanel implements Refreshable {
    protected final DefaultTableModel model;
    protected final JTable table;
    protected final JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
    private Runnable refreshAll = () -> { };

    protected BasePanel(String... columns) {
        super(new BorderLayout(6, 6));
        setBackground(Theme.BG);
        setBorder(BorderFactory.createEmptyBorder(8, 10, 10, 10));
        toolbar.setOpaque(false);
        model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setAutoCreateRowSorter(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);
        Theme.styleTable(table);
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(Theme.SELECT, 2));
        add(toolbar, BorderLayout.NORTH);
        add(sp, BorderLayout.CENTER);
    }

    void setRefreshAll(Runnable r) { this.refreshAll = r; }

    protected JButton button(String text, ThrowingAction action) {
        JButton b = new ColorButton(text, Theme.colorFor(text));
        b.addActionListener(e -> run(action));
        toolbar.add(b);
        return b;
    }

    protected void fill(List<Object[]> rows) {
        model.setRowCount(0);
        rows.forEach(model::addRow);
    }

    protected String selectedId(String what) throws VscException {
        int r = table.getSelectedRow();
        if (r < 0) throw new VscException("Please select a " + what + " in the table first.");
        return model.getValueAt(table.convertRowIndexToModel(r), 0).toString();
    }

    // OOP: EXCEPTION HANDLING - the one place where every business error becomes a friendly dialog.
    protected void run(ThrowingAction action) {
        try {
            action.run();
            refreshAll.run();
        } catch (VscException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Cannot complete action", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, "Unexpected problem: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    protected void info(String message) {
        JOptionPane.showMessageDialog(this, message, "Done", JOptionPane.INFORMATION_MESSAGE);
    }

    protected void showTable(String title, String[] cols, List<Object[]> rows) {
        DefaultTableModel m = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        rows.forEach(m::addRow);
        JTable t = new JTable(m);
        Theme.styleTable(t);
        JScrollPane sp = new JScrollPane(t);
        sp.setPreferredSize(new Dimension(820, 300));
        JOptionPane.showMessageDialog(this, rows.isEmpty() ? new JLabel("No records found.") : sp, title,
                JOptionPane.PLAIN_MESSAGE);
    }
}
