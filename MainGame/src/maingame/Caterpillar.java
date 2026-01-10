
package maingame;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Caterpillar {
    int x, y;
    long spawnTime;
    boolean isButterfly = false;

    public Caterpillar(int x, int y) {
        this.x = x;
        this.y = y;
        this.spawnTime = System.currentTimeMillis();
    }

    public void update() {
        long aliveTime = System.currentTimeMillis() - spawnTime;

        
        if (aliveTime > 2000 && !isButterfly) {
            isButterfly = true;
        }
    }

    public boolean shouldDisappear() {
        long aliveTime = System.currentTimeMillis() - spawnTime;

        
        return aliveTime > 3000;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, 30, 30);
    }

    public void draw(Graphics2D g) {
        if (!isButterfly) {
            g.setColor(Color.GREEN);
            g.fillOval(x, y, 30, 30); 
        } else {
            g.setColor(Color.MAGENTA);
            g.fillOval(x, y, 30, 20); 
        }
    }
}
