import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

/**
 * VIEW: User Interface built with Java Swing and main execution entry point.
 */
public class WordEmpire extends JFrame {

    private final GameEngine gameEngine;
    private final char[][] board = {
        {'C', 'A', 'T', 'S'},
        {'R', 'E', 'R', 'O'},
        {'D', 'A', 'R', 'T'},
        {'G', 'O', 'D', 'E'}
    };

    private final JButton[][] gridButtons = new JButton[4][4];
    private final List<JButton> selectedButtons = new ArrayList<>();
    private final List<Point> selectedCoordinates = new ArrayList<>();
    private final StringBuilder currentWord = new StringBuilder();

    private JLabel scoreLabel;
    private JLabel currentWordLabel;
    private JLabel statusLabel;
    private JTextArea foundWordsArea;

    public WordEmpire() {
        Trie dictionary = new Trie();
        loadSampleDictionary(dictionary);
        this.gameEngine = new GameEngine(dictionary);

        initializeUI();
    }

    private void loadSampleDictionary(Trie trie) {
        String[] defaultWords = {
            "CAT", "CATS", "CATER", "CARD", "CARE", "DOG", 
            "DART", "OAT", "STAR", "DATE", "CODE", "ROSE", "TOAD", "GOD","READ"
        };
        for (String word : defaultWords) {
            trie.insert(word);
        }
    }

    private void initializeUI() {
        setTitle("Word Empire - Search Game");
        setSize(700, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(12, 12));

        add(buildHeaderPanel(), BorderLayout.NORTH);
        add(buildGridPanel(), BorderLayout.CENTER);
        add(buildSidebarPanel(), BorderLayout.EAST);
        add(buildControlPanel(), BorderLayout.SOUTH);
    }

    private JPanel buildHeaderPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 4, 4));
        panel.setBackground(new Color(245, 247, 250));

        scoreLabel = new JLabel("Score: 0 pts", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        scoreLabel.setForeground(new Color(41, 128, 185));

        currentWordLabel = new JLabel("Current Word: -", SwingConstants.CENTER);
        currentWordLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));

        panel.add(scoreLabel);
        panel.add(currentWordLabel);
        return panel;
    }

    private JPanel buildGridPanel() {
        JPanel gridPanel = new JPanel(new GridLayout(4, 4, 8, 8));
        gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                JButton btn = new JButton(String.valueOf(board[r][c]));
                btn.setFont(new Font("Segoe UI", Font.BOLD, 22));
                btn.setFocusable(false);
                btn.setBackground(Color.WHITE);

                final int row = r;
                final int col = c;
                btn.addActionListener(e -> onTileClicked(btn, row, col));

                gridButtons[r][c] = btn;
                gridPanel.add(btn);
            }
        }
        return gridPanel;
    }

    private JPanel buildSidebarPanel() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBorder(BorderFactory.createTitledBorder("Discovered Words"));

        foundWordsArea = new JTextArea(12, 15);
        foundWordsArea.setEditable(false);
        foundWordsArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        sidebar.add(new JScrollPane(foundWordsArea), BorderLayout.CENTER);
        return sidebar;
    }

    private JPanel buildControlPanel() {
        JPanel controlPanel = new JPanel(new BorderLayout(5, 5));

        JPanel buttonBox = new JPanel();
        JButton submitBtn = new JButton("Submit Word");
        JButton clearBtn = new JButton("Clear Selection");

        submitBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        clearBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        submitBtn.addActionListener(e -> onSubmitWord());
        clearBtn.addActionListener(e -> clearSelection());

        buttonBox.add(submitBtn);
        buttonBox.add(clearBtn);

        statusLabel = new JLabel("Click adjacent letters to form words.", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));

        controlPanel.add(buttonBox, BorderLayout.NORTH);
        controlPanel.add(statusLabel, BorderLayout.SOUTH);
        return controlPanel;
    }

    private void onTileClicked(JButton btn, int row, int col) {
        Point point = new Point(row, col);

        if (selectedCoordinates.contains(point)) {
            statusLabel.setText("⚠️ Tile already selected!");
            return;
        }

        if (!selectedCoordinates.isEmpty()) {
            Point lastPoint = selectedCoordinates.get(selectedCoordinates.size() - 1);
            int rowDiff = Math.abs(lastPoint.x - row);
            int colDiff = Math.abs(lastPoint.y - col);

            if (rowDiff > 1 || colDiff > 1) {
                statusLabel.setText("⚠️ Must select an adjacent tile!");
                return;
            }
        }

        selectedButtons.add(btn);
        selectedCoordinates.add(point);
        btn.setBackground(new Color(174, 214, 241));

        currentWord.append(board[row][col]);
        currentWordLabel.setText("Current Word: " + currentWord);
        statusLabel.setText("Building word...");
    }

    private void onSubmitWord() {
        String word = currentWord.toString();
        if (word.isEmpty()) {
            statusLabel.setText("⚠️ Select letters before submitting.");
            return;
        }

        String result = gameEngine.processSubmission(word);

        if (result.startsWith("SUCCESS:")) {
            int points = Integer.parseInt(result.split(":")[1]);
            scoreLabel.setText("Score: " + gameEngine.getTotalScore() + " pts");
            foundWordsArea.append("• " + word + " (+" + points + ")\n");
            statusLabel.setText("✅ Found '" + word + "' (+" + points + " pts)!");
        } else if (result.equals("ERR_TOO_SHORT")) {
            statusLabel.setText("❌ Words must be at least 3 letters long!");
        } else if (result.equals("ERR_DUPLICATE")) {
            statusLabel.setText("⚠️ Word '" + word + "' already claimed!");
        } else if (result.equals("ERR_INVALID_WORD")) {
            statusLabel.setText("❌ '" + word + "' is not in the dictionary.");
        }

        clearSelection();
    }

    private void clearSelection() {
        for (JButton btn : selectedButtons) {
            btn.setBackground(Color.WHITE);
        }
        selectedButtons.clear();
        selectedCoordinates.clear();
        currentWord.setLength(0);
        currentWordLabel.setText("Current Word: -");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WordEmpire().setVisible(true));
    }
}