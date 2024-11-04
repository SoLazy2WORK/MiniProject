import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class MainMenu extends JPanel {
    private BufferedImage backgroundImage;
    private JComboBox<String> difficultyComboBox;
    private JLabel easyHighScoreLabel;
    private JLabel mediumHighScoreLabel;
    private JLabel hardHighScoreLabel;

    public MainMenu() {
        setLayout(null); // Disable layout manager

        // Load the background image
        try {
            backgroundImage = ImageIO.read(new File("start.png")); // Adjust the path as necessary
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Create "Start Game" button
        JButton startButton = new JButton("Start Game");
        startButton.setBounds(315, 250, 200, 50); // Set position and size
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame();
            }
        });
        add(startButton); // Add button to panel

        // Create "Exit" button
        JButton exitButton = new JButton("Exit");
        exitButton.setBounds(315, 330, 200, 50); // Set position and size
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        add(exitButton); // Add button to panel

        // Create JComboBox for difficulty selection
        String[] difficulties = {"Easy", "Medium", "Hard"}; // Difficulty options
        difficultyComboBox = new JComboBox<>(difficulties);
        difficultyComboBox.setBounds(315, 300, 200, 30); // Set position and size
        add(difficultyComboBox); // Add combo box to panel

        // Create labels for high scores
        easyHighScoreLabel = new JLabel("Max score: " + getHighScore("maxscore"));
        easyHighScoreLabel.setBounds(10, 10, 300, 30);
        add(easyHighScoreLabel);       
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            // Draw the background image
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    private void startGame() {
        // Retrieve selected difficulty
        String selectedDifficulty = (String) difficultyComboBox.getSelectedItem();
        System.out.println("Selected Difficulty: " + selectedDifficulty); // For demonstration

        // Proceed to start the game with the selected difficulty
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.getContentPane().removeAll(); // Remove the menu
        TileGame game = new TileGame(selectedDifficulty); // Pass the difficulty to the game
        frame.add(game); // Add the game panel
        frame.revalidate(); // Refresh the frame
        frame.repaint(); // Repaint the frame
        game.requestFocusInWindow(); // Request focus for key events
        new Thread(game).start(); // Start the game loop
    }

    private String getHighScore(String difficulty) {
        ScoreManager scoreManager = new ScoreManager();
        return scoreManager.getHighScore(difficulty);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Tile Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(640, 480);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.add(new MainMenu()); // Add the main menu
        frame.setVisible(true);
    }
}