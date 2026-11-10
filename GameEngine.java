import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * CONTROLLER: Handles game logic, scoring, and duplicate checking.
 */
public class GameEngine {
    private final Trie dictionary;
    private final Set<String> claimedWords;
    private int totalScore;

    public GameEngine(Trie dictionary) {
        this.dictionary = dictionary;
        this.claimedWords = new HashSet<>();
        this.totalScore = 0;
    }

    // Calculates points based on word length
    public int calculateScore(String word) {
        int length = word.length();
        if (length < 3) return 0;

        switch (length) {
            case 3:  return 100;
            case 4:  return 200;
            case 5:  return 400;
            case 6:  return 800;
            default: return 1500;
        }
    }

    // Processes submission and returns status response
    public String processSubmission(String word) {
        if (word == null || word.length() < 3) {
            return "ERR_TOO_SHORT";
        }

        String formattedWord = word.toUpperCase();

        if (claimedWords.contains(formattedWord)) {
            return "ERR_DUPLICATE";
        }

        if (!dictionary.isValidWord(formattedWord)) {
            return "ERR_INVALID_WORD";
        }

        int points = calculateScore(formattedWord);
        claimedWords.add(formattedWord);
        totalScore += points;
        return "SUCCESS:" + points;
    }

    public int getTotalScore() { 
        return totalScore; 
    }

    public Set<String> getClaimedWords() { 
        return Collections.unmodifiableSet(claimedWords); 
    }
}