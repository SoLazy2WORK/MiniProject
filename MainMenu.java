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
        setLayout(null); 

        // Load the background image
        try {
            backgroundImage = ImageIO.read(new File("start.png")); // Read the image file
        } catch (IOException e) {
            e.printStackTrace(); 
        }

        // Create "Start Game" button
        JButton startButton = new JButton("Start Game"); // Create a new button with label "Start Game"
        startButton.setBounds(315, 250, 200, 50); 
        startButton.addActionListener(new ActionListener() { 
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame(); // Call startGame method when button is clicked
            }
        });
        add(startButton); 

        // Create "Exit" button
        JButton exitButton = new JButton("Exit"); // Create a new button with label "Exit"
        exitButton.setBounds(315, 330, 200, 50); 
        exitButton.addActionListener(new ActionListener() { 
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0); // Exit the application when button is clicked
            }
        });
        add(exitButton); // Add button to the panel

        // Create JComboBox for difficulty selection
        String[] difficulties = {"Easy", "Medium", "Hard"}; // Difficulty options
        difficultyComboBox = new JComboBox<>(difficulties); 
        difficultyComboBox.setBounds(315, 300, 200, 30);
        add(difficultyComboBox); // Add combo box to the panel

        // Create label for easy high score
        easyHighScoreLabel = new JLabel("Max score: " + getHighScore("maxscore")); // Get high score and create label
        easyHighScoreLabel.setBounds(10, 10, 300, 30); // Set position and size
        add(easyHighScoreLabel); // Add label to the panel
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); 
        if (backgroundImage != null) { // Check if the background image is loaded
            // Draw the background image scaled to the panel's size
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    private void startGame() {
        // Retrieve selected difficulty from the combo box
        String selectedDifficulty = (String) difficultyComboBox.getSelectedItem(); // Get selected item
        System.out.println("Selected Difficulty: " + selectedDifficulty); // Print selected difficulty for debugging

        // Proceed to start the game with the selected difficulty
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this); // Get the parent frame of the panel
        frame.getContentPane().removeAll(); // Remove all components from the frame (menu)
        TileGame game = new TileGame(selectedDifficulty); // Create a new game instance with the selected difficulty
        frame.add(game); // Add the game panel to the frame
        frame.revalidate(); // Refresh the frame to show the new game panel
        frame.repaint(); // Repaint the frame to update the display
        game.requestFocusInWindow(); // Request focus for the game panel for key events
        new Thread(game).start(); // Start the game loop in a new thread
    }

    private String getHighScore(String difficulty) {
        ScoreManager scoreManager = new ScoreManager(); // Create an instance of ScoreManager to handle high scores
        return scoreManager.getHighScore(difficulty); // Retrieve and return the high score for the specified difficulty
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Tile Game"); // Create a new JFrame with the title "Tile Game"
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Set default close operation to exit the application
        frame.setSize(640, 480); // Set the size of the frame
        frame.setResizable(false); // Disable resizing of the frame
        frame.setLocationRelativeTo(null); // Center the frame on the screen
        frame.add(new MainMenu()); // Add the main menu panel to the frame
        frame.setVisible(true); // Make the frame visible
    }
}