import javax.swing.border.AbstractBorder;
import java.awt.*;

public class ShadowBorder extends AbstractBorder {
    private static final int SHADOW_SIZE = 4;
    private static final Color SHADOW_COLOR = new Color(0, 0, 0, 50);

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Dessiner l'ombre
        g2d.setColor(SHADOW_COLOR);
        for (int i = 0; i < SHADOW_SIZE; i++) {
            int alpha = 50 - (i * 10);
            if (alpha > 0) {
                g2d.setColor(new Color(0, 0, 0, alpha));
                g2d.drawRoundRect(x + i, y + i, width - (i * 2), height - (i * 2), 10, 10);
            }
        }

        // Dessiner le fond blanc
        g2d.setColor(Color.WHITE);
        g2d.fillRoundRect(x, y, width - SHADOW_SIZE, height - SHADOW_SIZE, 10, 10);

        // Dessiner la bordure
        g2d.setColor(new Color(229, 231, 235));
        g2d.drawRoundRect(x, y, width - SHADOW_SIZE, height - SHADOW_SIZE, 10, 10);

        g2d.dispose();
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(SHADOW_SIZE, SHADOW_SIZE, SHADOW_SIZE * 2, SHADOW_SIZE * 2);
    }

    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        insets.left = insets.top = SHADOW_SIZE;
        insets.right = insets.bottom = SHADOW_SIZE * 2;
        return insets;
    }
} 