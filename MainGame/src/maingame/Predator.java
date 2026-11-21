package maingame;

import java.awt.*;

public class Predator {
    public int x, y, speed;
    public int width = 65, height = 38;

    public Predator(int startY, int speed) {
        this.x = -80;
        this.y = startY;
        this.speed = speed;
    }

    public void update() {
        x += speed;
    }

    public void draw(Graphics2D g2) {
        // Shadow
        g2.setColor(new Color(0, 0, 0, 35));
        g2.fillRoundRect(x + 2, y + 3, width, height, 12, 12);

        // Predator Body
        g2.setColor(new Color(139, 69, 19));
        g2.fillRoundRect(x, y, width, height, 12, 12);
        g2.setColor(new Color(90, 40, 10));
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(x, y, width, height, 12, 12);

        // Sharp beak
        Polygon beak = new Polygon();
        beak.addPoint(x + width, y + height / 2 - 8);
        beak.addPoint(x + width + 14, y + height / 2);
        beak.addPoint(x + width, y + height / 2 + 8);
        g2.setColor(new Color(240, 120, 0));
        g2.fillPolygon(beak);

        // Piercing eye
        g2.setColor(Color.RED);
        g2.fillOval(x + width - 18, y + 10, 10, 10);
        g2.setColor(Color.BLACK);
        g2.fillOval(x + width - 14, y + 12, 4, 4);

        // Dorsal spikes
        g2.setColor(new Color(180, 50, 20));
        for (int i = 8; i < width - 18; i += 12) {
            Polygon spike = new Polygon();
            spike.addPoint(x + i, y);
            spike.addPoint(x + i + 5, y - 7);
            spike.addPoint(x + i + 10, y);
            g2.fillPolygon(spike);
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width + 12, height);
    }
}
