
package maingame;


import java.awt.*;

public class Food {
    public int x, y, value;
    public int size = 25;

    public Food(int x, int y, int value) {
        this.x = x;
        this.y = y;
        this.value = value;
    }

    public void draw(Graphics2D g2) {
        g2.setColor(new Color(255, 165, 0)); 
        g2.fillOval(x, y, size, size);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        g2.drawString(String.valueOf(value), x + 8, y + 18);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, size, size);
    }
}
