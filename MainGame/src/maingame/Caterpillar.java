package maingame;

import java.awt.*;

public class Caterpillar {
    public int x, y;
    public long spawnTime;
    public boolean isButterfly = false;

    public Caterpillar(int x, int y) {
        this.x = x;
        this.y = y;
        this.spawnTime = System.currentTimeMillis();
    }

    public void update() {
        long aliveTime = System.currentTimeMillis() - spawnTime;
        if (aliveTime > 2500 && !isButterfly) {
            isButterfly = true;
        }
    }

    public boolean shouldDisappear() {
        long aliveTime = System.currentTimeMillis() - spawnTime;
        return aliveTime > 4500;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, 36, 36);
    }

    public void draw(Graphics2D g) {
        if (!isButterfly) {
            // Caterpillar: Segmented green body
            g.setColor(new Color(46, 204, 113));
            g.fillOval(x, y + 6, 14, 14);
            g.fillOval(x + 9, y + 4, 14, 14);
            g.fillOval(x + 18, y + 6, 16, 16);

            // Head and eye
            g.setColor(new Color(39, 174, 96));
            g.setStroke(new BasicStroke(1.5f));
            g.drawOval(x + 18, y + 6, 16, 16);
            g.setColor(Color.BLACK);
            g.fillOval(x + 28, y + 10, 3, 3);

            // Bonus label
            g.setFont(new Font("SansSerif", Font.BOLD, 10));
            g.setColor(new Color(0, 120, 0));
            g.drawString("+500", x + 4, y);
        } else {
            // Fluttering Butterfly
            g.setColor(new Color(155, 89, 182));
            g.fillOval(x - 2, y, 16, 22);
            g.fillOval(x + 12, y, 16, 22);
            g.setColor(new Color(52, 73, 94));
            g.fillRoundRect(x + 7, y + 3, 7, 18, 4, 4);
        }
    }
}
