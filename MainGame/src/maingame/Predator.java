
package maingame;


import java.awt.*;

public class Predator {
    public int x, y, speed;
    public int width = 60, height = 40;

    public Predator(int startY, int speed) {
        this.x = -70; 
        this.y = startY;
        this.speed = speed;
    }

    public void update() {
        x += speed;
    }

    public void draw(Graphics2D g2) {
        g2.setColor(new Color(101, 67, 33)); 
        g2.fillRect(x, y, width, height);
        g2.setColor(Color.RED);
        g2.fillOval(x + width - 12, y + 8, 8, 8); 
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}