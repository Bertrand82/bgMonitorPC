package bg.monitor.gui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.DoubleUnaryOperator;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.WindowConstants;

/**
 * Trace y = f(t) avec échelle fixe, mise à jour chaque seconde.
 * Axe X: fenêtre glissante de windowSeconds (en secondes, vers le passé).
 * Axe Y: fixe [yMin, yMax].
 */
public class FixedScalePlotPanel extends JPanel {

    private static final class Sample {
        final long epochMillis;
        final double y;
        Sample(long epochMillis, double y) { this.epochMillis = epochMillis; this.y = y; }
    }

    private final Deque<Sample> samples = new ArrayDeque<>();

    private final int windowSeconds;
    private final double yMin;
    private final double yMax;

    // Marges pour axes/labels
    private final int padLeft = 55;
    private final int padRight = 15;
    private final int padTop = 15;
    private final int padBottom = 35;

    public FixedScalePlotPanel(int windowSeconds, double yMin, double yMax) {
        if (windowSeconds <= 1) throw new IllegalArgumentException("windowSeconds doit être > 1");
        if (yMax <= yMin) throw new IllegalArgumentException("yMax doit être > yMin");
        this.windowSeconds = windowSeconds;
        this.yMin = yMin;
        this.yMax = yMax;
        setBackground(Color.WHITE);
        setOpaque(true);
        setPreferredSize(new Dimension(800, 300));
    }

    /** Ajoute un point (timestamp = maintenant). */
    public void addSample(double y) {
        long now = System.currentTimeMillis();
        samples.addLast(new Sample(now, y));
        pruneOld(now);
        repaint();
    }

    /** Enlève les points plus vieux que la fenêtre. */
    private void pruneOld(long nowMillis) {
        long minMillis = nowMillis - windowSeconds * 1000L;
        while (!samples.isEmpty() && samples.peekFirst().epochMillis < minMillis) {
            samples.removeFirst();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        long now = System.currentTimeMillis();
        pruneOld(now);

        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            int plotX = padLeft;
            int plotY = padTop;
            int plotW = Math.max(1, w - padLeft - padRight);
            int plotH = Math.max(1, h - padTop - padBottom);

            // Cadre
            g2.setColor(new Color(245, 245, 245));
            g2.fillRect(plotX, plotY, plotW, plotH);
            g2.setColor(Color.GRAY);
            g2.drawRect(plotX, plotY, plotW, plotH);

            // Grille horizontale (Y fixe)
            g2.setColor(new Color(220, 220, 220));
            int gridLines = 5;
            for (int i = 0; i <= gridLines; i++) {
                int yy = plotY + (int) Math.round(i * (plotH / (double) gridLines));
                g2.drawLine(plotX, yy, plotX + plotW, yy);
            }

            // Labels Y
            g2.setColor(Color.DARK_GRAY);
            for (int i = 0; i <= gridLines; i++) {
                double v = yMax - i * ((yMax - yMin) / gridLines);
                int yy = plotY + (int) Math.round(i * (plotH / (double) gridLines));
                String s = String.format("%.2f", v);
                int sw = g2.getFontMetrics().stringWidth(s);
                g2.drawString(s, plotX - 8 - sw, yy + 5);
            }

            // Axe X: fenêtre [now-windowSeconds, now]
            long xMinMillis = now - windowSeconds * 1000L;
            long xMaxMillis = now;

            // Petits ticks X (toutes les 10s si fenêtre large, sinon toutes les 5s)
            int step = (windowSeconds >= 60) ? 10 : 5;
            g2.setColor(new Color(200, 200, 200));
            for (int s = 0; s <= windowSeconds; s += step) {
                long t = xMaxMillis - s * 1000L;
                int xx = xToPixel(t, xMinMillis, xMaxMillis, plotX, plotW);
                g2.drawLine(xx, plotY, xx, plotY + plotH);

                String label = "-" + s + "s";
                int sw = g2.getFontMetrics().stringWidth(label);
                g2.setColor(Color.DARK_GRAY);
                g2.drawString(label, xx - sw / 2, plotY + plotH + 20);
                g2.setColor(new Color(200, 200, 200));
            }

            // Courbe
            if (samples.size() >= 2) {
                Path2D path = new Path2D.Double();
                boolean first = true;

                for (Sample sm : samples) {
                    int xx = xToPixel(sm.epochMillis, xMinMillis, xMaxMillis, plotX, plotW);
                    int yy = yToPixel(sm.y, yMin, yMax, plotY, plotH);
                    if (first) {
                        path.moveTo(xx, yy);
                        first = false;
                    } else {
                        path.lineTo(xx, yy);
                    }
                }

                g2.setColor(new Color(0, 90, 200));
                g2.setStroke(new BasicStroke(2f));
                g2.draw(path);
            }

            // Titre léger
            g2.setColor(Color.DARK_GRAY);
            g2.drawString("f(t) — fenêtre " + windowSeconds + "s — " + Instant.ofEpochMilli(now),
                    plotX, padTop - 2);

        } finally {
            g2.dispose();
        }
    }

    private static int xToPixel(long tMillis, long xMinMillis, long xMaxMillis, int plotX, int plotW) {
        if (xMaxMillis == xMinMillis) return plotX;
        double u = (tMillis - xMinMillis) / (double) (xMaxMillis - xMinMillis);
        u = Math.max(0, Math.min(1, u));
        return plotX + (int) Math.round(u * plotW);
    }

    private static int yToPixel(double y, double yMin, double yMax, int plotY, int plotH) {
        double v = (y - yMin) / (yMax - yMin);
        v = Math.max(0, Math.min(1, v));
        // y pixel: haut = 0
        return plotY + (int) Math.round((1.0 - v) * plotH);
    }

    // ---- Démo ----
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FixedScalePlotPanel plot = new FixedScalePlotPanel(
                    120,   // 2 minutes affichées
                    -1.5,  // yMin fixe
                    1.5    // yMax fixe
            );

            // Exemple de f(t): sin(t) (t en secondes)
            DoubleUnaryOperator ff = (t) -> Math.sin(t / 5.0);

            Timer timer = new Timer(1000, e -> {
                double t = System.currentTimeMillis() / 1000.0;
                plot.addSample(ff.applyAsDouble(t));
            });
            timer.start();

            JFrame frame = new JFrame("Courbe Swing - échelle fixe");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());
            frame.add(plot, BorderLayout.CENTER);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
