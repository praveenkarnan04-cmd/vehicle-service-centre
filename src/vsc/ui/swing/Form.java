package vsc.ui.swing;

import vsc.exception.VscException;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/** Small helper that builds a labelled input dialog. */
final class Form {
    private final JPanel panel = new JPanel(new GridBagLayout());
    private int row = 0;

    JTextField text(String label, String initial) {
        JTextField f = new JTextField(initial, 22);
        add(label, f);
        return f;
    }

    JComboBox<String> choice(String label, List<String> options) {
        JComboBox<String> c = new JComboBox<>(options.toArray(new String[0]));
        add(label, c);
        return c;
    }

    private void add(String label, JComponent c) {
        GridBagConstraints g = new GridBagConstraints();
        g.gridy = row++;
        g.insets = new Insets(5, 6, 5, 6);
        g.anchor = GridBagConstraints.WEST;
        g.gridx = 0;
        panel.add(new JLabel(label), g);
        g.gridx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1;
        panel.add(c, g);
    }

    boolean show(Component parent, String title) {
        return JOptionPane.showConfirmDialog(parent, panel, title, JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION;
    }

    /** Options look like "C001 - Nimal"; the id is the part before " - ". */
    static String idOf(JComboBox<String> box) throws VscException {
        Object s = box.getSelectedItem();
        if (s == null) throw new VscException("Nothing selected.");
        return s.toString().split(" - ")[0];
    }

    static double number(JTextField f, String field) throws VscException {
        try { return Double.parseDouble(f.getText().trim()); }
        catch (NumberFormatException e) { throw new VscException(field + " must be a number."); }
    }

    static int whole(JTextField f, String field) throws VscException {
        try { return Integer.parseInt(f.getText().trim()); }
        catch (NumberFormatException e) { throw new VscException(field + " must be a whole number."); }
    }
}
