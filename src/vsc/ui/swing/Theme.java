package vsc.ui.swing;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

/** Central colour palette and table styling. */
final class Theme {
    private Theme() { }

    static final Color BG = new Color(0xF3F4F9);
    static final Color DARK = new Color(0x1F2937);
    static final Color INDIGO = new Color(0x4F46E5);
    static final Color BLUE = new Color(0x2563EB);
    static final Color SKY = new Color(0x0EA5E9);
    static final Color TEAL = new Color(0x0D9488);
    static final Color GREEN = new Color(0x16A34A);
    static final Color ORANGE = new Color(0xEA580C);
    static final Color AMBER = new Color(0xF59E0B);
    static final Color RED = new Color(0xDC2626);
    static final Color PURPLE = new Color(0x7C3AED);
    static final Color PINK = new Color(0xDB2777);
    static final Color SELECT = new Color(0xC7D2FE);

    /** One colour per tab: Dashboard, Customers, Vehicles, Jobs, Mechanics, Parts, Billing, Appointments. */
    static final Color[] TAB_COLORS = {INDIGO, BLUE, TEAL, ORANGE, PURPLE, RED, GREEN, PINK};

    static Color tint(Color c, float f) {
        return new Color((int) (c.getRed() + (255 - c.getRed()) * f), (int) (c.getGreen() + (255 - c.getGreen()) * f),
                (int) (c.getBlue() + (255 - c.getBlue()) * f));
    }

    static Color colorFor(String text) {
        String t = text.toLowerCase();
        if (t.contains("cancel") || t.contains("clear")) return RED;
        if (t.contains("complete") || t.contains("generate") || t.contains("add") || t.contains("register")
                || t.contains("book") || t.contains("record") || t.contains("new")) return GREEN;
        if (t.contains("start") || t.contains("restock")) return ORANGE;
        if (t.contains("history") || t.contains("view") || t.contains("report") || t.contains("check")) return TEAL;
        if (t.contains("edit") || t.contains("assign") || t.contains("reschedule")) return BLUE;
        return INDIGO;
    }

    static void styleTable(JTable t) {
        t.setDefaultRenderer(Object.class, new TableRenderer());
        t.setGridColor(new Color(0xE5E7EB));
        t.setShowVerticalLines(false);
        t.setRowHeight(30);
        t.setSelectionBackground(SELECT);
        t.setSelectionForeground(DARK);
        JTableHeader h = t.getTableHeader();
        h.setReorderingAllowed(false);
        h.setPreferredSize(new Dimension(100, 34));
        h.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object v, boolean s, boolean f, int r, int c) {
                super.getTableCellRendererComponent(tbl, v, s, f, r, c);
                setOpaque(true);
                setBackground(INDIGO);
                setForeground(Color.WHITE);
                setFont(getFont().deriveFont(Font.BOLD));
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return this;
            }
        });
    }

    /** Zebra rows, red rows for low stock, and coloured status words. */
    private static final class TableRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
            super.getTableCellRendererComponent(t, v, sel, foc, r, c);
            setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
            int mr = t.convertRowIndexToModel(r);
            boolean low = false;
            for (int i = 0; i < t.getModel().getColumnCount(); i++)
                if ("LOW STOCK".equals(String.valueOf(t.getModel().getValueAt(mr, i)))) { low = true; break; }
            if (sel) setBackground(SELECT);
            else setBackground(low ? new Color(0xFEE2E2) : (r % 2 == 0 ? Color.WHITE : new Color(0xEEF2FF)));
            setForeground(DARK);
            setFont(t.getFont().deriveFont(Font.PLAIN));
            switch (String.valueOf(v)) {
                case "LOW STOCK", "Cancelled" -> { setForeground(RED); setFont(getFont().deriveFont(Font.BOLD)); }
                case "OK", "Booked", "COMPLETED" -> { setForeground(GREEN); setFont(getFont().deriveFont(Font.BOLD)); }
                case "PENDING" -> { setForeground(ORANGE); setFont(getFont().deriveFont(Font.BOLD)); }
                case "IN_PROGRESS" -> { setForeground(BLUE); setFont(getFont().deriveFont(Font.BOLD)); }
                default -> { }
            }
            return this;
        }
    }
}
