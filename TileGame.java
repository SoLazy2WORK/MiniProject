import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent; 
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random; 
import javax.swing.*; 

public class TileGame extends JPanel implements Runnable {

    private final List<Tile> tiles; // List to hold active tiles
    private final Queue<Character> order; // Queue to hold the order of keys to be pressed
    private int score = 0; // Player's score
    private boolean running = true; 
    private long lastTileTime = 0; // Timestamp for the last tile generation
    private long tileGenerationInterval; 
    private final int tileSize = 150; // Size of each tile
    private final int gap = 5;
    private Image backgroundImage;
    private Image tileImageA;
    private Image tileImageS; 
    private Image tileImageD; 
    private Image tileImageF; 
    private Image tileImageB; 
    private Image bonusImage; 

    // Timer variables
    private int remainingTime = 120; // Remaining time in seconds (2 minutes)
    private boolean timerRunning = false; 

    public TileGame(String difficulty) {
        this.tiles = new ArrayList<>(); // Initialize the list of tiles
        this.order = new LinkedList<>(); // Initialize the queue for key order
        this.setPreferredSize(new Dimension(640, 480)); // Set the preferred size of the panel

        // Load images for the game
        backgroundImage = new ImageIcon("play_screen.png").getImage();
        tileImageA = new ImageIcon("a.png").getImage();
        tileImageS = new ImageIcon("s.png").getImage();
        tileImageD = new ImageIcon("d.png").getImage();
        tileImageF = new ImageIcon("f.png").getImage();
        tileImageB = new ImageIcon("b.png").getImage();
        bonusImage = new ImageIcon("bonus.png").getImage();

        // Set tile generation interval based on difficulty
        switch (difficulty) {
            case "Easy":
                tileGenerationInterval = 2000; // 2 seconds for easy difficulty
                break;
            case "Medium":
                tileGenerationInterval = 1000; // 1 second for medium difficulty
                break;
            case "Hard":
                tileGenerationInterval = 600; // 0.6 seconds for hard difficulty
                break;
            default:
                tileGenerationInterval = 2000; // Default to easy 
        }

        // Key listener to handle key presses
        this.setFocusable(true); // Make the panel focusable
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                char keyChar = e.getKeyChar(); // Get the character of the pressed key
                checkKeyPress(keyChar); // Check if the pressed key matches the expected key
            }
        });

        // Enable double buffering for smoother rendering
        this.setDoubleBuffered(true);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g); // Call the superclass's paintComponent method
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this); // Draw the background image
        // Draw each tile on the panel
        for (Tile tile : tiles) {
            Image tileImage = getTileImage(tile.key); // Get the image for the tile based on its key
            if (tileImage != null) {
                g.drawImage(tileImage, tile.x, tile.y, tileSize, tileSize, this); // Draw the tile
            }
        }
        // Display score and remaining time
        g.setColor(Color.RED);
        g.drawString("Score: " + score, 10, 20); // Display the score
        g.drawString("Time Remaining: " + remainingTime + "s", 10, 40); // Display remaining time
    }

    // Get the corresponding tile image based on the key pressed
    private Image getTileImage(char key) {
        switch (key) {
            case 'a':
                return tileImageA; // Return image for 'A'
            case 's':
                return tileImageS; // Return image for 'S'
            case 'd':
                return tileImageD; // Return image for 'D'
            case 'f':
                return tileImageF; // Return image for 'F'
            case 'b':
                return tileImageB; // Return image for bonus tile 'B'
            default:
                return null; // Return null for unrecognized keys
        }
    }

    // Start the countdown timer
    private void startTimer() {
        timerRunning = true; // Set timer running flag to true
        new Thread(() -> {
            while (remainingTime > 0 && timerRunning) {
                try {
                    Thread.sleep(1000); // Wait for 1 second
                    remainingTime--; // Decrement the remaining time
                    repaint(); // Update the display
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Restore interrupted status
                }
            }
            if (remainingTime <= 0) {
                endGame(); // End the game when time runs out
            }
        }).start(); // Start the timer thread
    }

    // Check if the pressed key matches the expected key
    private void checkKeyPress(char keyChar) {
        if (order.isEmpty()) {
            return; // No tiles to press
        }

        char expectedKey = order.peek(); // Get the expected key from the queue
        if (keyChar == expectedKey) {
            order.poll(); // Remove the expected key from the queue
            for (int i = 0; i < tiles.size(); i++) {
                Tile tile = tiles.get(i);
                if (tile.key == expectedKey) {
                    tiles.remove(i); // Remove the tile from the list
                    score += tile.isBonus ? 5 : 1; // Update score based on tile type
                    repaint(); // Update the display
                    return; // Exit the method
                }
            }
        } else {
            endGame(); // End the game if the wrong key is pressed
        }

        if (order.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All tiles cleared! Your score: " + score); // Show success message
            restartGame(); // Restart the game
        }
    }

    // Add a new tile to the game
    private void addNewTile() {
        Random rand = new Random(); // Create a random number generator
        int column; // Column for the new tile
        char key; // Key associated with the new tile
        boolean isBonus = rand.nextInt(10) < 2; // 20% chance to be a bonus tile
        if (tileGenerationInterval == 600) {
            do {
                column = rand.nextInt(4); // Randomly select a column
                key = getKeyForColumn2(column, isBonus); // Get the key for the column
            } while (isColumnOccupied(column)); // Ensure the column is not occupied

            int x = (getWidth() / 4) * column + gap * column; // Calculate x position
            Tile tile = new Tile(x, -100, tileSize, tileSize, key, isBonus); // Create a new tile
            tiles.add(tile); // Add the tile to the list
            order.add(key); // Add the key to the order queue
        } else {
            do {
                column = rand.nextInt(4); // Randomly select a column
                key = getKeyForColumn(column, isBonus); // Get the key for the column
            } while (isColumnOccupied(column)); // Ensure the column is not occupied

            int x = (getWidth() / 4) * column + gap * column; // Calculate x position
            Tile tile = new Tile(x, -100, tileSize, tileSize, key, isBonus); // Create a new tile
            tiles.add(tile); // Add the tile to the list
            order.add(key); // Add the key to the order queue
        }
    }

    // Get the key associated with a specific column
    private char getKeyForColumn(int column, boolean isBonus) {
        if (isBonus) {
            return 'b'; // Return 'b' for bonus tiles
        }
        switch (column) {
            case 0:
                return 'a'; // Return 'a' for column 0
            case 1:
                return 's'; // Return 's' for column 1
            case 2:
                return 'd'; // Return 'd' for column 2
            case 3:
                return 'f'; // Return 'f' for column 3
            default:
                return ' '; // Return space for unrecognized columns
        }
    }

    // Get the key associated with a specific column for a different generation logic
    private char getKeyForColumn2(int column, boolean isBonus) {
        Random rand = new Random(); // Create a random number generator
        int z = rand.nextInt(4); // Randomly select a key from the available options
        if (isBonus) {
            return 'b'; // Return 'b' for bonus tiles
        }
        switch (z) {
            case 0:
                return 'a'; // Return 'a' for column 0
            case 1:
                return 's'; // Return 's' for column 1
            case 2:
                return 'd'; // Return 'd' for column 2
            case 3:
                return 'f'; // Return 'f' for column 3
            default:
                return ' '; // Return space for unrecognized columns
        }
    }

    // Check if a specific column is occupied by any tile
    private boolean isColumnOccupied(int column) {
        for (Tile tile : tiles) {
            if (tile.x / (getWidth() / 4) == column) {
                return true; // Return true if the column is occupied
            }
        }
        return false; // Return false if the column is not occupied
    }

    // End the game and handle score saving
    private void endGame() {
        running = false; // Stop the game loop
        timerRunning = false; // Stop the timer

        String difficulty = "maxscore"; // Set difficulty for high score management

        // Get the current high score for the selected difficulty
        ScoreManager scoreManager = new ScoreManager();
        String[] highScoreParts = scoreManager.getHighScore(difficulty).split(": ");
        int currentHighScore = highScoreParts.length > 1 ? Integer.parseInt(highScoreParts[1]) : 0;

        // Check if the current score is higher than the current high score
        if (score > currentHighScore) {
            String name = JOptionPane.showInputDialog(this, "New High Score! Your score: " + score + "\nEnter your name:", "New High Score", JOptionPane.QUESTION_MESSAGE);
            if (name != null && !name.trim().isEmpty()) {
                scoreManager.saveScore(difficulty, name, score); // Save score for the selected difficulty
            }
        } else {
            String name = JOptionPane.showInputDialog(this, "Game Over! Your score: " + score + "\nEnter your name:", "Game Over", JOptionPane.QUESTION_MESSAGE);
            if (name != null && !name.trim().isEmpty()) {
                scoreManager.saveScore(difficulty, name, score); // Save score for the selected difficulty
            }
        }

        // Ask the player if they want to restart the game
        int response = JOptionPane.showConfirmDialog(this, "Would you like to restart?", "Game Over", JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            // Return to the main menu
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            frame.getContentPane().removeAll();
            frame.add(new MainMenu()); // Add the MainMenu instance
            frame.revalidate();
            frame.repaint();
        } else {
            System.exit(0); // Exit the application
        }
    }

    // Restart the game
    private void restartGame() {
        running = true; // Set running flag to true
        score = 0; // Reset score
        tiles.clear(); // Clear the list of tiles
        order.clear(); // Clear the order queue
        lastTileTime = System.currentTimeMillis(); // Reset last tile time
        remainingTime = 120; // Reset the timer
        timerRunning = false; // Reset timer running flag
        repaint(); // Update the display
    }

    @Override
    public void run() {
        startTimer(); // Start the timer
        while (running) {
            long currentTime = System.currentTimeMillis(); // Get the current time
            if (currentTime - lastTileTime > tileGenerationInterval) {
                addNewTile(); // Add a new tile if the generation interval has passed
                lastTileTime = currentTime; // Update last tile time
            }

            // Update tile positions and check for game over condition
            for (int i = 0; i < tiles.size(); i++) {
                Tile tile = tiles.get(i);
                tile.y += 5; // Move tile down

                // Check if the tile has reached the bottom of the panel
                if (tile.y > getHeight()) {
                    endGame(); // End the game if a tile reaches the bottom
                    return; // Exit the loop
                }
            }

            repaint(); // Refresh the display

            try {
                Thread.sleep(16); // Sleep for a short duration to control the game loop speed
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore interrupted status
            }
        }
    }
}

class Tile {

    int x, y, width, height; // Position and size of the tile
    char key; // Key associated with the tile
    boolean isBonus; // Flag to indicate if the tile is a bonus tile

    // Constructor for Tile class
    public Tile(int x, int y, int width, int height, char key, boolean isBonus) {
        this.x = x; // Set x position
        this.y = y; // Set y position
        this.width = width; // Set width
        this.height = height; // Set height
        this.key = key; // Set associated key
        this.isBonus = isBonus; // Set bonus flag
    }
}