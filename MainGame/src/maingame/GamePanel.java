package maingame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class GamePanel extends JPanel implements ActionListener {

    public enum GameState {
        MENU,
        PLAYING,
        GAME_OVER
    }

    public GameState gameState = GameState.MENU;

    // Player position & stats
    private int birdX = 380, birdY = 270;
    private int targetSum = 15, currentSum = 0;
    private int score = 0, level = 1, vomitRights = 3;
    private int timeLeft = 25;
    private long lastTimeCheck;
    private String deathReason = "Game Over!";

    // Animation & pulse
    private float pulseFactor = 0f;
    private boolean pulseUp = true;

    // Game data structures & entities
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

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int code = e.getKeyCode();
                if (code < 256) {
                    keys[code] = true;
                }

                if (gameState == GameState.MENU) {
                    if (code == KeyEvent.VK_SPACE || code == KeyEvent.VK_ENTER) {
                        startGame();
                    }
                } else if (gameState == GameState.PLAYING) {
                    if (code == KeyEvent.VK_SPACE) {
                        vomit();
                    }
                } else if (gameState == GameState.GAME_OVER) {
                    if (code == KeyEvent.VK_R || code == KeyEvent.VK_ENTER) {
                        startGame();
                    } else if (code == KeyEvent.VK_M) {
                        gameState = GameState.MENU;
                        repaint();
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                int code = e.getKeyCode();
                if (code < 256) {
                    keys[code] = false;
                }
            }
        });

        // 60 FPS Game Loop
        gameTimer = new javax.swing.Timer(16, this);
        gameTimer.start();
    }

    public void startGame() {
        score = 0;
        level = 1;
        vomitRights = 3;
        birdX = 380;
        birdY = 270;
        predators.clear();
        setupLevel();
        gameState = GameState.PLAYING;
    }

    public void setupLevel() {
        targetSum = 10 + random.nextInt(15);
        stomach.clear();
        currentSum = 0;
        foods.clear();

        for (int i = 0; i < 7; i++) {
            spawnFood();
        }

        if (level % 3 == 0) {
            caterpillar = new Caterpillar(
                    60 + random.nextInt(680),
                    80 + random.nextInt(440)
            );
        } else {
            caterpillar = null;
        }

        timeLeft = 25;
        lastTimeCheck = System.currentTimeMillis();
    }

    private void spawnFood() {
        foods.add(new Food(
                40 + random.nextInt(720),
                80 + random.nextInt(460),
                random.nextInt(9) + 1
        ));
    }

    private void vomit() {
        if (!stomach.isEmpty() && vomitRights > 0) {
            int popped = stomach.pop();
            currentSum -= popped;
            vomitRights--;
        }
    }

    private void triggerGameOver(String reason) {
        deathReason = reason;
        gameState = GameState.GAME_OVER;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Pulse animation for menus / warnings
        if (pulseUp) {
            pulseFactor += 0.04f;
            if (pulseFactor >= 1.0f) pulseUp = false;
        } else {
            pulseFactor -= 0.04f;
            if (pulseFactor <= 0.0f) pulseUp = true;
        }

        if (gameState == GameState.PLAYING) {
            updatePlayingState();
        }

        repaint();
    }

    private void updatePlayingState() {
        // Timer update
        long now = System.currentTimeMillis();
        if (now - lastTimeCheck >= 1000) {
            timeLeft--;
            lastTimeCheck = now;
            if (timeLeft <= 0) {
                triggerGameOver("Time ran out!");
                return;
            }
        }

        // Movement
        int moveSpeed = 5;
        if (keys[KeyEvent.VK_W] || keys[KeyEvent.VK_UP]) birdY -= moveSpeed;
        if (keys[KeyEvent.VK_S] || keys[KeyEvent.VK_DOWN]) birdY += moveSpeed;
        if (keys[KeyEvent.VK_A] || keys[KeyEvent.VK_LEFT]) birdX -= moveSpeed;
        if (keys[KeyEvent.VK_D] || keys[KeyEvent.VK_RIGHT]) birdX += moveSpeed;

        int birdSize = 42 + Math.min(24, currentSum / 2);
        birdX = Math.max(0, Math.min(getWidth() - birdSize, birdX));
        birdY = Math.max(62, Math.min(getHeight() - birdSize, birdY));

        Rectangle birdRect = new Rectangle(birdX, birdY, birdSize, birdSize);

        // Food collection
        for (int i = foods.size() - 1; i >= 0; i--) {
            if (birdRect.intersects(foods.get(i).getBounds())) {
                if (currentSum < targetSum) {
                    int val = foods.get(i).value;
                    currentSum += val;
                    stomach.push(val);
                    foods.remove(i);
                    spawnFood();

                    // Check exact target match
                    if (currentSum == targetSum) {
                        score += targetSum * 10;
                        level++;
                        if (level % 2 == 0) {
                            vomitRights++;
                        }
                        setupLevel();
                        return;
                    }
                }
            }
        }

        // Predator Spawning & Movement
        if (random.nextInt(100) < 2) {
            int predSpeed = 4 + Math.min(5, level / 2);
            predators.add(new Predator(70 + random.nextInt(480), predSpeed));
        }

        for (int i = predators.size() - 1; i >= 0; i--) {
            Predator p = predators.get(i);
            p.update();
            if (birdRect.intersects(p.getBounds())) {
                triggerGameOver("Caught by a deadly predator!");
                return;
            }
            if (p.x > getWidth() + 100) {
                predators.remove(i);
            }
        }

        // Caterpillar / Butterfly logic
        if (caterpillar != null) {
            caterpillar.update();
            if (caterpillar.shouldDisappear()) {
                caterpillar = null;
            } else if (!caterpillar.isButterfly && birdRect.intersects(caterpillar.getBounds())) {
                score += 500;
                level++;
                setupLevel();
                return;
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        if (gameState == GameState.MENU) {
            drawMenu(g2);
        } else if (gameState == GameState.PLAYING) {
            drawGame(g2);
        } else if (gameState == GameState.GAME_OVER) {
            drawGame(g2);
            drawGameOver(g2);
        }
    }

    private void drawMenu(Graphics2D g2) {
        // Sky gradient background
        GradientPaint sky = new GradientPaint(0, 0, new Color(135, 206, 235), 0, 600, new Color(200, 235, 255));
        g2.setPaint(sky);
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Lush grass at bottom
        g2.setColor(new Color(46, 204, 113));
        g2.fillRect(0, 520, getWidth(), 80);
        g2.setColor(new Color(39, 174, 96));
        g2.fillRect(0, 520, getWidth(), 6);

        // Title Shadow & Text
        g2.setFont(new Font("SansSerif", Font.BOLD, 46));
        g2.setColor(new Color(0, 0, 0, 60));
        g2.drawString("🐥 CHICKY PRECISION", 143, 113);
        g2.setColor(new Color(243, 156, 18));
        g2.drawString("🐥 CHICKY PRECISION", 140, 110);

        // Subtitle
        g2.setFont(new Font("SansSerif", Font.BOLD, 18));
        g2.setColor(new Color(44, 62, 80));
        g2.drawString("Strategic Arcade Math with a LIFO Stomach Mechanic", 175, 148);

        // Instruction Card
        g2.setColor(new Color(255, 255, 255, 220));
        g2.fillRoundRect(120, 180, 560, 270, 20, 20);
        g2.setColor(new Color(189, 195, 199));
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(120, 180, 560, 270, 20, 20);

        g2.setColor(new Color(44, 62, 80));
        g2.setFont(new Font("SansSerif", Font.BOLD, 16));
        g2.drawString("GAMEPLAY & RULES:", 150, 215);

        g2.setFont(new Font("SansSerif", Font.PLAIN, 15));
        g2.drawString("🎯 Goal: Reach the exact TARGET sum by eating numbered seeds.", 150, 250);
        g2.drawString("⚠️ Precision: Do not exceed the target! Overeating turns Chicky RED.", 150, 280);
        g2.drawString("🔄 Vomit (LIFO): Press SPACE to pop() the last eaten seed from stack.", 150, 310);
        g2.drawString("🦊 Danger: Dodge lethal predators! Collision causes instant Game Over.", 150, 340);
        g2.drawString("🐛 Bonus: Catch green caterpillars before they become butterflies (+500).", 150, 370);
        g2.drawString("⌨️ Controls: WASD or Arrow Keys to Move | SPACE to Vomit", 150, 400);

        // Pulsing Start Prompt
        int pulseAlpha = 160 + (int) (95 * pulseFactor);
        g2.setColor(new Color(231, 76, 60, pulseAlpha));
        g2.setFont(new Font("SansSerif", Font.BOLD, 22));
        String startText = "👉 Press SPACE or ENTER to Start 👈";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(startText, (getWidth() - fm.stringWidth(startText)) / 2, 495);
    }

    private void drawGame(Graphics2D g2) {
        // Background
        g2.setColor(new Color(135, 206, 235));
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Decorative ground
        g2.setColor(new Color(110, 210, 120));
        g2.fillRect(0, 570, getWidth(), 30);

        // Draw entities
        for (Food f : foods) {
            f.draw(g2);
        }
        for (Predator p : predators) {
            p.draw(g2);
        }
        if (caterpillar != null) {
            caterpillar.draw(g2);
        }

        // Draw Chick
        drawChick(g2);

        // Draw Overfed warning if currentSum > targetSum
        if (currentSum > targetSum) {
            int alpha = 180 + (int) (75 * pulseFactor);
            g2.setColor(new Color(231, 76, 60, alpha));
            g2.setFont(new Font("SansSerif", Font.BOLD, 18));
            String warning = "⚠️ OVERFED (" + currentSum + "/" + targetSum + ")! PRESS SPACE TO VOMIT!";
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(warning, (getWidth() - fm.stringWidth(warning)) / 2, 85);
        }

        // Draw HUD Header
        drawHUD(g2);
    }

    private void drawChick(Graphics2D g2) {
        int birdSize = 42 + Math.min(24, currentSum / 2);
        boolean overfed = currentSum > targetSum;

        // Shadow
        g2.setColor(new Color(0, 0, 0, 30));
        g2.fillOval(birdX + 2, birdY + 3, birdSize, birdSize);

        // Body Color: Yellow normally, Red when overfed
        Color bodyColor = overfed ? new Color(231, 76, 60) : new Color(241, 196, 15);
        Color borderColor = overfed ? new Color(192, 57, 43) : new Color(212, 172, 13);

        g2.setColor(bodyColor);
        g2.fillOval(birdX, birdY, birdSize, birdSize);
        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(2f));
        g2.drawOval(birdX, birdY, birdSize, birdSize);

        // Wing
        g2.setColor(overfed ? new Color(192, 57, 43) : new Color(243, 156, 18));
        g2.fillOval(birdX + 4, birdY + birdSize / 3, birdSize / 2, birdSize / 3);

        // Eye
        int eyeSize = Math.max(6, birdSize / 5);
        int eyeX = birdX + birdSize - eyeSize - 8;
        int eyeY = birdY + birdSize / 4;
        g2.setColor(Color.WHITE);
        g2.fillOval(eyeX, eyeY, eyeSize, eyeSize);
        g2.setColor(Color.BLACK);
        g2.fillOval(eyeX + 2, eyeY + 1, eyeSize / 2, eyeSize / 2);

        // Cute Orange Beak
        Polygon beak = new Polygon();
        beak.addPoint(birdX + birdSize - 2, birdY + birdSize / 3);
        beak.addPoint(birdX + birdSize + 10, birdY + birdSize / 3 + 5);
        beak.addPoint(birdX + birdSize - 2, birdY + birdSize / 3 + 10);
        g2.setColor(new Color(230, 126, 34));
        g2.fillPolygon(beak);
    }

    private void drawHUD(Graphics2D g2) {
        // HUD Top Banner
        g2.setColor(new Color(44, 62, 80));
        g2.fillRect(0, 0, getWidth(), 55);
        g2.setColor(new Color(52, 73, 94));
        g2.fillRect(0, 52, getWidth(), 3);

        g2.setFont(new Font("SansSerif", Font.BOLD, 15));

        // TARGET
        g2.setColor(new Color(241, 196, 15));
        g2.drawString("TARGET: " + targetSum, 15, 24);

        // STOMACH SUM
        if (currentSum > targetSum) {
            g2.setColor(new Color(231, 76, 60));
        } else if (currentSum == targetSum) {
            g2.setColor(new Color(46, 204, 113));
        } else {
            g2.setColor(Color.WHITE);
        }
        g2.drawString("STOMACH: " + currentSum, 135, 24);

        // VOMIT RIGHTS
        g2.setColor(new Color(52, 152, 219));
        g2.drawString("VOMIT RIGHTS: " + vomitRights, 265, 24);

        // LEVEL & SCORE
        g2.setColor(Color.WHITE);
        g2.drawString("LEVEL: " + level, 440, 24);
        g2.setColor(new Color(46, 204, 113));
        g2.drawString("SCORE: " + score, 535, 24);

        // TIME LEFT
        if (timeLeft <= 10) {
            int pulse = (int) (4 * pulseFactor);
            g2.setFont(new Font("SansSerif", Font.BOLD, 17 + pulse));
            g2.setColor(new Color(231, 76, 60));
        } else {
            g2.setFont(new Font("SansSerif", Font.BOLD, 15));
            g2.setColor(Color.WHITE);
        }
        g2.drawString("TIME: " + timeLeft + "s", 670, 25);

        // LIFO STACK VISUALIZATION BAR
        g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g2.setColor(new Color(189, 195, 199));
        StringBuilder stackStr = new StringBuilder("Stomach [LIFO Stack]: ");
        if (stomach.isEmpty()) {
            stackStr.append("(Empty)");
        } else {
            stackStr.append("[ ");
            for (int i = 0; i < stomach.size(); i++) {
                stackStr.append(stomach.get(i));
                if (i < stomach.size() - 1) stackStr.append(" | ");
            }
            stackStr.append(" ] ➔ TOP");
        }
        g2.drawString(stackStr.toString(), 15, 45);
    }

    private void drawGameOver(Graphics2D g2) {
        // Dark translucent overlay
        g2.setColor(new Color(0, 0, 0, 190));
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Game Over Banner Card
        g2.setColor(new Color(44, 62, 80));
        g2.fillRoundRect(180, 140, 440, 310, 20, 20);
        g2.setColor(new Color(231, 76, 60));
        g2.setStroke(new BasicStroke(3f));
        g2.drawRoundRect(180, 140, 440, 310, 20, 20);

        // Text: GAME OVER
        g2.setFont(new Font("SansSerif", Font.BOLD, 36));
        g2.setColor(new Color(231, 76, 60));
        FontMetrics fm1 = g2.getFontMetrics();
        g2.drawString("GAME OVER", (getWidth() - fm1.stringWidth("GAME OVER")) / 2, 205);

        // Death Reason
        g2.setFont(new Font("SansSerif", Font.PLAIN, 18));
        g2.setColor(Color.WHITE);
        FontMetrics fm2 = g2.getFontMetrics();
        g2.drawString(deathReason, (getWidth() - fm2.stringWidth(deathReason)) / 2, 245);

        // Stats
        g2.setFont(new Font("SansSerif", Font.BOLD, 20));
        g2.setColor(new Color(241, 196, 15));
        String scoreText = "Final Score: " + score;
        FontMetrics fm3 = g2.getFontMetrics();
        g2.drawString(scoreText, (getWidth() - fm3.stringWidth(scoreText)) / 2, 290);

        g2.setFont(new Font("SansSerif", Font.PLAIN, 16));
        g2.setColor(new Color(189, 195, 199));
        String levelText = "Reached Level: " + level;
        FontMetrics fm4 = g2.getFontMetrics();
        g2.drawString(levelText, (getWidth() - fm4.stringWidth(levelText)) / 2, 320);

        // Restart Actions
        int pulseAlpha = 170 + (int) (85 * pulseFactor);
        g2.setColor(new Color(46, 204, 113, pulseAlpha));
        g2.setFont(new Font("SansSerif", Font.BOLD, 18));
        String restartText = "👉 Press 'R' or ENTER to Play Again 👈";
        FontMetrics fm5 = g2.getFontMetrics();
        g2.drawString(restartText, (getWidth() - fm5.stringWidth(restartText)) / 2, 380);

        g2.setFont(new Font("SansSerif", Font.PLAIN, 14));
        g2.setColor(new Color(149, 165, 166));
        String menuText = "Press 'M' for Main Menu";
        FontMetrics fm6 = g2.getFontMetrics();
        g2.drawString(menuText, (getWidth() - fm6.stringWidth(menuText)) / 2, 415);
    }

    // Getters / Setters for programmatic capture and testing
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    public Caterpillar getCaterpillar() { return caterpillar; }
    public void setCaterpillar(Caterpillar caterpillar) { this.caterpillar = caterpillar; }
    public void setBirdPosition(int x, int y) { this.birdX = x; this.birdY = y; }
    public void setDeathReason(String reason) { this.deathReason = reason; }
    public Stack<Integer> getStomach() { return stomach; }
    public void setCurrentSum(int sum) { this.currentSum = sum; }
    public void setTargetSum(int target) { this.targetSum = target; }
    public void setTimeLeft(int time) { this.timeLeft = time; }
}
