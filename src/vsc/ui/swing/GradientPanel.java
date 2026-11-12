package vsc.ui.swing;

import javax.swing.*;
import java.awt.*;

class GradientPanel extends JPanel {
    private final Color left, right;

    GradientPanel(Color left, Color right) {
        super(new BorderLayout());
        this.left = left;
        this.right = right;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setPaint(new GradientPaint(0, 0, left, getWidth(), 0, right));
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
        super.paintComponent(g);
    }
}
