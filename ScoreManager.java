import java.io.*; // Import necessary classes for file I/O
import java.util.HashMap; // Import HashMap for storing high scores
import java.util.Map; // Import Map interface

public class ScoreManager {
    private static final String SCORE_FILE = "highscores.txt"; // File to store high scores
    private Map<String, HighScoreEntry> highScores; // Map to hold high scores for different difficulties

    // Constructor for ScoreManager
    public ScoreManager() {
        highScores = new HashMap<>(); // Initialize the HashMap to store high scores
        loadScores(); // Load existing high scores from file
    }

    // Load scores from file
    private void loadScores() {
        try (BufferedReader reader = new BufferedReader(new FileReader(SCORE_FILE))) { // Use try-with-resources to auto-close reader
            String line; // Variable to hold each line from the file
            // Read each line until the end of the file
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(","); // Split the line by comma
                // Check if the line has exactly three parts
                if (parts.length == 3) {
                    String difficulty = parts[0]; // First part is the difficulty
                    String name = parts[1]; // Second part is the player's name
                    int score = Integer.parseInt(parts[2]); // Third part is the score (converted to int)
                    // Store the high score entry in the map
                    highScores.put(difficulty, new HighScoreEntry(name, score));
                }
            }
        } catch (IOException e) {
            e.printStackTrace(); // Print stack trace if an error occurs while reading the file
        }
    }

    // Save score to file
    public void saveScore(String difficulty, String name, int score) {
        // Get the current high score entry for the specified difficulty
        HighScoreEntry currentHighScore = highScores.get(difficulty);
        // Check if the new score is higher than the current high score
        if (currentHighScore == null || score > currentHighScore.score) {
            // Update the high score entry with the new score
            highScores.put(difficulty, new HighScoreEntry(name, score));
            saveScoresToFile(); // Save updated scores to file
        }
    }

    // Save all high scores to file
    private void saveScoresToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SCORE_FILE))) { // Use try-with-resources to auto-close writer
            // Iterate over each entry in the highScores map
            for (Map.Entry<String, HighScoreEntry> entry : highScores.entrySet()) {
                String difficulty = entry.getKey(); // Get the difficulty from the entry
                HighScoreEntry highScoreEntry = entry.getValue(); // Get the HighScoreEntry object
                // Write the difficulty, name, and score to the file
                writer.write(difficulty + "," + highScoreEntry.name + "," + highScoreEntry.score);
                writer.newLine(); // Write a new line after each entry
            }
        } catch (IOException e) {
            e.printStackTrace(); // Print stack trace if an error occurs while writing to the file
        }
    }

    // Get high score for a specific difficulty
    public String getHighScore(String difficulty) {
        HighScoreEntry entry = highScores.get(difficulty); // Retrieve the high score entry for the specified difficulty
        // Check if the entry exists
        if (entry != null) {
            return entry.name + " : " + entry.score; // Return the player's name and score
        }
        return "No score yet"; // Return message if no score exists for the difficulty
    }

    // Inner class to hold high score entries
    private static class HighScoreEntry {
        String name; // Player's name
        int score; // Player's score

        // Constructor for HighScoreEntry
        HighScoreEntry(String name, int score) {
            this.name = name; // Set the player's name
            this.score = score; // Set the player's score
        }
    }
}