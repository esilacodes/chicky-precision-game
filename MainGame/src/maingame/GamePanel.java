package maingame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class GamePanel extends JPanel implements ActionListener {

   
    private int birdX = 400, birdY = 250;
    private int targetSum, currentSum = 0;
    private int score = 0, level = 1, vomitRights = 3;
    private boolean isGameOver = false;
    private int timeLeft = 25; 
    private long lastTimeCheck; 
    private int timerFontSize = 18; 

    private Stack<Integer> stomach = new Stack<>();
    private ArrayList<Food> foods = new ArrayList<>();
    private ArrayList<Predator> predators = new ArrayList<>();
    private boolean[] keys = new boolean[256];
    private javax.swing.Timer gameTimer;
    private Random random = new Random();
    private Caterpillar caterpillar = null;

    public GamePanel() {
        setPreferredSize(new Dimension(800, 600));
        setBackground(new Color(135, 206, 235));
        setFocusable(true);
        setupLevel();
        

addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() < 256) {
                    keys[e.getKeyCode()] = true;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() < 256) {
                    keys[e.getKeyCode()] = false;
                }
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    vomit();
                }
            }
        });

        gameTimer = new javax.swing.Timer(20, this);
        gameTimer.start();
    }

    private void setupLevel() {
        targetSum = 10 + random.nextInt(15);
        stomach.clear();
       currentSum = 0;
        foods.clear();
       
        for (int i = 0; i < 6; i++) {
            spawnFood();
        }
        if (level % 3 == 0) {
            caterpillar = new Caterpillar(
                    random.nextInt(750),
                    random.nextInt(550)
            );
        } else {
            caterpillar = null;
        }

        timeLeft = 25;
        lastTimeCheck = System.currentTimeMillis();
    }

    private void spawnFood() {
        foods.add(new Food(random.nextInt(750), 50 + random.nextInt(500), random.nextInt(9) + 1));
    }

    private void vomit() {
        if (!stomach.isEmpty() && vomitRights > 0) {
            currentSum -= stomach.pop();
            vomitRights--;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isGameOver) {
            return;
        }

        if (System.currentTimeMillis() - lastTimeCheck >= 1000) {
            timeLeft--;
            lastTimeCheck = System.currentTimeMillis();

            if (timeLeft <= 10 && timeLeft > 0) {
                timerFontSize = 35;
            }

            if (timeLeft <= 0) {
                isGameOver = true;
            }

            if (timerFontSize > 20) {
                timerFontSize--;
            }

            if (timeLeft <= 0) {
                isGameOver = true; 
            }
        }

        
        if (keys[KeyEvent.VK_W] || keys[KeyEvent.VK_UP]) {
            birdY -= 6;
        }
        if (keys[KeyEvent.VK_S] || keys[KeyEvent.VK_DOWN]) {
            birdY += 6;
        }
        if (keys[KeyEvent.VK_A] || keys[KeyEvent.VK_LEFT]) {
            birdX -= 6;
        }
        if (keys[KeyEvent.VK_D] || keys[KeyEvent.VK_RIGHT]) {
            birdX += 6;
        }

        birdX = Math.max(0, Math.min(750, birdX));
        birdY = Math.max(0, Math.min(550, birdY));

        
        int birdSize = 40 + (currentSum / 2);
        Rectangle birdRect = new Rectangle(birdX, birdY, birdSize, birdSize);

        for (int i = foods.size() - 1; i >= 0; i--) {
            if (birdRect.intersects(foods.get(i).getBounds())) {
                if (currentSum < targetSum) {
                    currentSum += foods.get(i).value;
                    stomach.push(foods.get(i).value);
                    foods.remove(i);
                    spawnFood();
                }
            }
        }

        
        if (random.nextInt(100) < 2) {
            predators.add(new Predator(random.nextInt(550), 5));
        }
        for (int i = predators.size() - 1; i >= 0; i--) {
            Predator p = predators.get(i);
            p.update();
            if (birdRect.intersects(p.getBounds())) {
                isGameOver = true;
            }
            if (p.x > 850) {
                predators.remove(i);
            }
        }

        if (currentSum == targetSum) {
            score += targetSum * 10;
            level++;
            if (level % 2 == 0) {
                vomitRights++;
            }
            setupLevel();
        }


        if (caterpillar != null) {
            caterpillar.update();

            if (caterpillar.shouldDisappear()) {
                caterpillar = null;
            }
        }
        if (caterpillar != null && !caterpillar.isButterfly) {
            if (birdRect.intersects(caterpillar.getBounds())) {
                
                score += 500;
                level++;
                setupLevel();
                return;
            }
        }

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        g2.drawString("TARGET: " + targetSum + "  STOMACK: " + currentSum + "   RIHTING TO VOMİT: " + vomitRights, 20, 25);
        g2.drawString("LEVEL: " + level + "  SKOR: " + score, 600, 25);




g2.setFont(new Font("Arial", Font.BOLD, 16));
g2.setColor(Color.BLACK);
g2.drawString("LEVEL: " + level + "  SKOR: " + score, 600, 25);
g2.setColor(new Color(0, 100, 0)); 
g2.drawString("KUSMA HAKKI: " + vomitRights, 20, 75);


        for (Food f : foods) {
            f.draw(g2);
        }
        for (Predator p : predators) {
            p.draw(g2);
        }

        int size = 40 + (currentSum / 2);
       g2.setColor(currentSum > targetSum ? Color.RED : Color.YELLOW);
       g2.fillOval(birdX, birdY, size, size);

g2.setColor(Color.BLACK);
g2.drawOval(birdX, birdY, size, size);
        
        
        
        
        
        if (isGameOver) {
            g2.setColor(new Color(0, 0, 0, 150));
            g2.fillRect(0, 0, 800, 600);
            g2.setColor(Color.WHITE);
            g2.drawString("GAME OVER ! SKOR: " + score, 330, 300);
        }
        if (caterpillar != null) {
            caterpillar.draw(g2);
        }

        if (timeLeft <= 10) {
            g2.setColor(Color.RED); 
        } else {
            g2.setColor(Color.BLACK); 
        }

        g2.setFont(new Font("Arial", Font.BOLD, timerFontSize)); 
        g2.drawString("TIME: " + timeLeft + "s", 450 - (timerFontSize / 2), 27); 
    }
}
