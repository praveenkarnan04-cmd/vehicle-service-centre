package vsc.ui.swing;

import javax.swing.*;
import java.awt.*;

/** Colourful rounded dashboard tile. */
class StatCard extends JPanel {
    private final JLabel value = new JLabel("0", SwingConstants.CENTER);
    private Color color;

    StatCard(String caption, Color color) {
        super(new BorderLayout());
        this.color = color;
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(14, 10, 14, 10));
        JLabel cap = new JLabel(caption, SwingConstants.CENTER);
        cap.setForeground(new Color(255, 255, 255, 220));
        cap.setFont(cap.getFont().deriveFont(Font.BOLD, 14f));
        value.setForeground(Color.WHITE);
        value.setFont(value.getFont().deriveFont(Font.BOLD, 36f));
        add(cap, BorderLayout.NORTH);
        add(value, BorderLayout.CENTER);
    }

    void setValue(String v) { value.setText(v); }
    void setColor(Color c) { this.color = c; repaint(); }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setPaint(new GradientPaint(0, 0, Theme.tint(color, 0.15f), getWidth(), getHeight(), color.darker()));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
        g2.dispose();
        super.paintComponent(g);
    }
}
