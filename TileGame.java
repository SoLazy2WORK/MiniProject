
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

    private final List<Tile> tiles;
    private final Queue<Character> order;
    private int score = 0;
    private boolean running = true;
    private long lastTileTime = 0;
    private long tileGenerationInterval; // Adjusted based on difficulty
    private final int tileSize = 150;
    private final int gap = 5;
    private Image backgroundImage;
    private Image tileImageA;
    private Image tileImageS;
    private Image tileImageD;
    private Image tileImageF;
    private Image tileImageB;
    private Image bonusImage;

    // Timer variables
    private int remainingTime = 120; // 2 minutes in seconds
    private boolean timerRunning = false;

    public TileGame(String difficulty) {
        this.tiles = new ArrayList<>();
        this.order = new LinkedList<>();
        this.setPreferredSize(new Dimension(640, 480));

        // Load images
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
                tileGenerationInterval = 2000; // 2 seconds
                break;
            case "Medium":
                tileGenerationInterval = 1000; // 1 second
                break;
            case "Hard":
                tileGenerationInterval = 600; // 0.6 seconds
                break;
            default:
                tileGenerationInterval = 2000; // Default to easy if not recognized
        }

        // Key listener
        this.setFocusable(true);
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                char keyChar = e.getKeyChar();
                checkKeyPress(keyChar);
            }
        });

        // Enable double buffering
        this.setDoubleBuffered(true);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        for (Tile tile : tiles) {
            Image tileImage = getTileImage(tile.key);
            if (tileImage != null) {
                g.drawImage(tileImage, tile.x, tile.y, tileSize, tileSize, this);
            }
        }
        g.setColor(Color.RED);
        g.drawString("Score: " + score, 10, 20);
        g.drawString("Time Remaining: " + remainingTime + "s", 10, 40); // Display remaining time
    }

    private Image getTileImage(char key) {
        switch (key) {
            case 'a':
                return tileImageA;
            case 's':
                return tileImageS;
            case 'd':
                return tileImageD;
            case 'f':
                return tileImageF;
            case 'b':
                return tileImageB;
            default:
                return null; // Return null for unrecognized keys
        }
    }

    private void startTimer() {
        timerRunning = true;
        new Thread(() -> {
            while (remainingTime > 0 && timerRunning) {
                try {
                    Thread.sleep(1000); // Wait for 1 second
                    remainingTime--; // Decrement the timer
                    repaint(); // Update the display
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            if (remainingTime <= 0) {
                endGame(); // End the game when time runs out
            }
        }).start();
    }

    private void checkKeyPress(char keyChar) {
        if (order.isEmpty()) {
            return; // No tiles to press
        }

        char expectedKey = order.peek();
        if (keyChar == expectedKey) {
            order.poll();
            for (int i = 0; i < tiles.size(); i++) {
                Tile tile = tiles.get(i);
                if (tile.key == expectedKey) {
                    tiles.remove(i);
                    score += tile.isBonus ? 5 : 1;
                    repaint();
                    return;
                }
            }
        } else {
            endGame(); // Wrong key pressed
        }

        if (order.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All tiles cleared! Your score: " + score);
            restartGame();
        }
    }

    private void addNewTile() {
        Random rand = new Random();
        int column;
        char key;
        boolean isBonus = rand.nextInt(10) < 2;
        if (tileGenerationInterval == 600) {
            do {
                column = rand.nextInt(4);
                key = getKeyForColumn2(column, isBonus);
            } while (isColumnOccupied(column));

            int x = (getWidth() / 4) * column + gap * column;
            Tile tile = new Tile(x, -100, tileSize, tileSize, key, isBonus);
            tiles.add(tile);
            order.add(key);
        } else {
            do {
                column = rand.nextInt(4);
                key = getKeyForColumn(column, isBonus);
            } while (isColumnOccupied(column));

            int x = (getWidth() / 4) * column + gap * column;
            Tile tile = new Tile(x, -100, tileSize, tileSize, key, isBonus);
            tiles.add(tile);
            order.add(key);
        }
    }

    private char getKeyForColumn(int column, boolean isBonus) {
        if (isBonus) {
            return 'b';
        }
        switch (column) {
            case 0:
                return 'a';
            case 1:
                return 's';
            case 2:
                return 'd';
            case 3:
                return 'f';
            default:
                return ' ';
        }
    }
    private char getKeyForColumn2(int column, boolean isBonus) {
        Random rand = new Random();
        int z=rand.nextInt(4);
        if (isBonus) {
            return 'b';
        }
        switch (z) {
            case 0:
                return 'a';
            case 1:
                return 's';
            case 2:
                return 'd';
            case 3:
                return 'f';
            default:
                return ' ';
        }
    }

    private boolean isColumnOccupied(int column) {
        for (Tile tile : tiles) {
            if (tile.x / (getWidth() / 4) == column) {
                return true;
            }
        }
        return false;
    }

    private void endGame() {
        running = false;
        timerRunning = false; // Stop the timer

        String difficulty = "maxscore";

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

        int response = JOptionPane.showConfirmDialog(this, "Would you like to restart?", "Game Over", JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            // Return to the main menu
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            frame.getContentPane().removeAll();
            frame.add(new MainMenu()); // Add the MainMenu instance
            frame.revalidate();
            frame.repaint();
        } else {
            System.exit(0);
        }
    }

    private void restartGame() {
        running = true;
        score = 0;
        tiles.clear();
        order.clear();
        lastTileTime = System.currentTimeMillis();
        remainingTime = 120; // Reset the timer
        timerRunning = false;
        repaint();
    }

    @Override
    public void run() {
        startTimer(); // Start the timer
        while (running) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastTileTime > tileGenerationInterval) {
                addNewTile();
                lastTileTime = currentTime;
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

            repaint();

            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}

class Tile {

    int x, y, width, height;
    char key;
    boolean isBonus;

    public Tile(int x, int y, int width, int height, char key, boolean isBonus) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.key = key;
        this.isBonus = isBonus;
    }
}
