package maingame;

import java.awt.*;

public class Food {
    public int x, y, value;
    public int size = 28;

    public Food(int x, int y, int value) {
        this.x = x;
        this.y = y;
        this.value = value;
    }

    public void draw(Graphics2D g2) {
        // Drop shadow
        g2.setColor(new Color(0, 0, 0, 35));
        g2.fillOval(x + 2, y + 2, size, size);

        // Food seed body
        g2.setColor(new Color(255, 180, 25));
        g2.fillOval(x, y, size, size);

        // Border highlight
        g2.setColor(new Color(230, 120, 0));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawOval(x, y, size, size);

        // Numeric value label
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 15));
        FontMetrics fm = g2.getFontMetrics();
        String str = String.valueOf(value);
        int tx = x + (size - fm.stringWidth(str)) / 2;
        int ty = y + (size - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(str, tx, ty);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, size, size);
    }
}
