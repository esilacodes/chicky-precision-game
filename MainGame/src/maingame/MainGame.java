
package maingame;


import javax.swing.JFrame;

public class MainGame {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Chick");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        GamePanel panel = new GamePanel();
        frame.add(panel);
        
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
    }
}
    

