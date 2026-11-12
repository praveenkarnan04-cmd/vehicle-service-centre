package vsc.ui.swing;

import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;

/** Tabs drawn as coloured rounded pills, one colour per tab. */
class ColorTabUI extends BasicTabbedPaneUI {
    private static Color colorOf(int i) { return Theme.TAB_COLORS[i % Theme.TAB_COLORS.length]; }

    @Override protected Insets getTabInsets(int p, int i) { return new Insets(9, 18, 9, 18); }
    @Override protected Insets getContentBorderInsets(int p) { return new Insets(6, 0, 0, 0); }
    @Override protected void paintContentBorder(Graphics g, int p, int s) { }
    @Override protected void paintTabBorder(Graphics g, int p, int i, int x, int y, int w, int h, boolean sel) { }
    @Override protected void paintFocusIndicator(Graphics g, int p, Rectangle[] r, int i, Rectangle ic, Rectangle tr, boolean sel) { }

    @Override
    protected void paintTabBackground(Graphics g, int p, int i, int x, int y, int w, int h, boolean sel) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color c = colorOf(i);
        g2.clipRect(x, y, w + 1, h);
        g2.setColor(sel ? c : Theme.tint(c, 0.8f));
        g2.fillRoundRect(x + 2, y + 2, w - 4, h + 12, 16, 16);
        g2.dispose();
    }

    @Override
    protected void paintText(Graphics g, int p, Font font, FontMetrics m, int i, String title, Rectangle tr, boolean sel) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setFont(font);
        g2.setColor(sel ? Color.WHITE : colorOf(i).darker());
        g2.drawString(title, tr.x, tr.y + m.getAscent());
        g2.dispose();
    }
}
