package maingame;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class ScreenshotCapture {
    public static void main(String[] args) throws Exception {
        File dir = new File("assets/screenshots");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 1. Capture Main Menu Screen
        GamePanel menuPanel = new GamePanel();
        menuPanel.setSize(800, 600);
        menuPanel.gameState = GamePanel.GameState.MENU;

        BufferedImage menuImg = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
        Graphics2D gMenu = menuImg.createGraphics();
        menuPanel.paint(gMenu);
        gMenu.dispose();
        ImageIO.write(menuImg, "PNG", new File(dir, "main-menu.png"));
        System.out.println("Generated assets/screenshots/main-menu.png");

        // 2. Capture In-Game Action Screen
        GamePanel playPanel = new GamePanel();
        playPanel.setSize(800, 600);
        playPanel.startGame();
        playPanel.setTargetSum(22);
        playPanel.setCurrentSum(13);
        playPanel.setScore(380);
        playPanel.setLevel(2);
        playPanel.setTimeLeft(18);
        playPanel.getStomach().clear();
        playPanel.getStomach().push(6);
        playPanel.getStomach().push(7);

        for (int i = 0; i < 25; i++) {
            playPanel.actionPerformed(null);
        }

        BufferedImage gameImg = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
        Graphics2D gGame = gameImg.createGraphics();
        playPanel.paint(gGame);
        gGame.dispose();
        ImageIO.write(gameImg, "PNG", new File(dir, "gameplay-1.png"));
        System.out.println("Generated assets/screenshots/gameplay-1.png");

        // 3. Capture Gameplay Action (Overfed State)
        GamePanel overfedPanel = new GamePanel();
        overfedPanel.setSize(800, 600);
        overfedPanel.startGame();
        overfedPanel.setTargetSum(18);
        overfedPanel.setCurrentSum(24);
        overfedPanel.setScore(750);
        overfedPanel.setLevel(3);
        overfedPanel.setTimeLeft(9);
        overfedPanel.getStomach().clear();
        overfedPanel.getStomach().push(8);
        overfedPanel.getStomach().push(7);
        overfedPanel.getStomach().push(9);

        for (int i = 0; i < 20; i++) {
            overfedPanel.actionPerformed(null);
        }

        BufferedImage overfedImg = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
        Graphics2D gOverfed = overfedImg.createGraphics();
        overfedPanel.paint(gOverfed);
        gOverfed.dispose();
        ImageIO.write(overfedImg, "PNG", new File(dir, "gameplay-2.png"));
        System.out.println("Generated assets/screenshots/gameplay-2.png");

        // 4. Capture Caterpillar Stage (+500 Bonus Opportunity)
        GamePanel catPanel = new GamePanel();
        catPanel.setSize(800, 600);
        catPanel.startGame();
        catPanel.setLevel(3);
        catPanel.setTargetSum(25);
        catPanel.setCurrentSum(16);
        catPanel.setScore(950);
        catPanel.setBirdPosition(260, 260);
        Caterpillar cat = new Caterpillar(380, 270);
        cat.isButterfly = false;
        catPanel.setCaterpillar(cat);

        BufferedImage catImg = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
        Graphics2D gCat = catImg.createGraphics();
        catPanel.paint(gCat);
        gCat.dispose();
        ImageIO.write(catImg, "PNG", new File(dir, "caterpillar.png"));
        System.out.println("Generated assets/screenshots/caterpillar.png");

        // 5. Capture Butterfly Stage (Transformed & Fleeting)
        GamePanel bflyPanel = new GamePanel();
        bflyPanel.setSize(800, 600);
        bflyPanel.startGame();
        bflyPanel.setLevel(3);
        bflyPanel.setTargetSum(25);
        bflyPanel.setCurrentSum(16);
        bflyPanel.setScore(950);
        bflyPanel.setBirdPosition(260, 260);
        Caterpillar bfly = new Caterpillar(410, 240);
        bfly.isButterfly = true;
        bflyPanel.setCaterpillar(bfly);

        BufferedImage bflyImg = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
        Graphics2D gBfly = bflyImg.createGraphics();
        bflyPanel.paint(gBfly);
        gBfly.dispose();
        ImageIO.write(bflyImg, "PNG", new File(dir, "butterfly.png"));
        System.out.println("Generated assets/screenshots/butterfly.png");

        // 6. Capture Game Over Screen
        GamePanel overPanel = new GamePanel();
        overPanel.setSize(800, 600);
        overPanel.startGame();
        overPanel.setDeathReason("Caught by a deadly predator!");
        overPanel.setScore(1250);
        overPanel.setLevel(4);
        overPanel.gameState = GamePanel.GameState.GAME_OVER;

        BufferedImage overImg = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
        Graphics2D gOver = overImg.createGraphics();
        overPanel.paint(gOver);
        gOver.dispose();
        ImageIO.write(overImg, "PNG", new File(dir, "game-over.png"));
        System.out.println("Generated assets/screenshots/game-over.png");

        System.exit(0);
    }
}
