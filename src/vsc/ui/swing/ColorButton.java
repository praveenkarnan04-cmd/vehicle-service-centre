package vsc.ui.swing;

import javax.swing.*;
import java.awt.*;

/** Rounded, coloured button with hover and press effects. */
class ColorButton extends JButton {
    private final Color base;

    ColorButton(String text, Color base) {
        super(text);
        this.base = base;
        setContentAreaFilled(false);
        setFocusPainted(false);
        setRolloverEnabled(true);
        setForeground(Color.WHITE);
        setFont(getFont().deriveFont(Font.BOLD, 13f));
        setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color c = getModel().isPressed() ? base.darker() : getModel().isRollover() ? Theme.tint(base, 0.18f) : base;
        g2.setPaint(new GradientPaint(0, 0, Theme.tint(c, 0.12f), 0, getHeight(), c.darker()));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
        g2.dispose();
        super.paintComponent(g);
    }
}
