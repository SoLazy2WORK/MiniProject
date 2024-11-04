import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ScoreManager {
    private static final String SCORE_FILE = "highscores.txt"; // File to store high scores
    private Map<String, HighScoreEntry> highScores;

    public ScoreManager() {
        highScores = new HashMap<>();
        loadScores();
    }

    // Load scores from file
    private void loadScores() {
        try (BufferedReader reader = new BufferedReader(new FileReader(SCORE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String difficulty = parts[0];
                    String name = parts[1];
                    int score = Integer.parseInt(parts[2]);
                    highScores.put(difficulty, new HighScoreEntry(name, score));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Save score to file
    public void saveScore(String difficulty, String name, int score) {
        HighScoreEntry currentHighScore = highScores.get(difficulty);
        if (currentHighScore == null || score > currentHighScore.score) {
            highScores.put(difficulty, new HighScoreEntry(name, score));
            saveScoresToFile();
        }
    }

    // Save all high scores to file
    private void saveScoresToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SCORE_FILE))) {
            for (Map.Entry<String, HighScoreEntry> entry : highScores.entrySet()) {
                String difficulty = entry.getKey();
                HighScoreEntry highScoreEntry = entry.getValue();
                writer.write(difficulty + "," + highScoreEntry.name + "," + highScoreEntry.score);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Get high score for a specific difficulty
    public String getHighScore(String difficulty) {
        HighScoreEntry entry = highScores.get(difficulty);
        if (entry != null) {
            return entry.name + " : " + entry.score;
        }
        return "No score yet";
    }

    // Inner class to hold high score entries
    private static class HighScoreEntry {
        String name;
        int score;

        HighScoreEntry(String name, int score) {
            this.name = name;
            this.score = score;
        }
    }
}