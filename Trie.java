
/**
 * MODEL: Trie Data Structure for fast dictionary operations.
 */
public class Trie {
    private final TrieNode root;

    public Trie() {
        this.root = new TrieNode();
    }

    public TrieNode getRoot() { 
        return root; 
    }

    // Inserts a word in O(L) time where L = word length
    public void insert(String word) {
        if (word == null || word.isEmpty()) return;

        TrieNode current = root;
        String upperWord = word.toUpperCase();

        for (int i = 0; i < upperWord.length(); i++) {
            char ch = upperWord.charAt(i);
            if (ch < 'A' || ch > 'Z') continue;

            TrieNode child = current.getChild(ch);
            if (child == null) {
                child = new TrieNode();
                current.setChild(ch, child);
            }
            current = child;
        }
        current.setEndOfWord(true);
    }

    // Validates a full word in O(L) time
    public boolean isValidWord(String word) {
        TrieNode node = navigateToNode(word);
        return node != null && node.isEndOfWord();
    }

    // Validates a prefix in O(L) time
    public boolean isValidPrefix(String prefix) {
        return navigateToNode(prefix) != null;
    }

    private TrieNode navigateToNode(String str) {
        if (str == null) return null;
        TrieNode current = root;
        String upper = str.toUpperCase();

        for (int i = 0; i < upper.length(); i++) {
            char ch = upper.charAt(i);
            current = current.getChild(ch);
            if (current == null) return null;
        }
        return current;
    }
}