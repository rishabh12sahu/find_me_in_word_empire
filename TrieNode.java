/**
 * MODEL: Represents a single character node inside the Trie.
 */
public class TrieNode {
    private final TrieNode[] children;
    private boolean isEndOfWord;

    public TrieNode() {
        this.children = new TrieNode[26]; // 26 letters ('A' through 'Z')
        this.isEndOfWord = false;
    }

    public TrieNode getChild(char ch) {
        int index = ch - 'A';
        if (index < 0 || index >= 26) return null;
        return children[index];
    }

    public void setChild(char ch, TrieNode node) {
        int index = ch - 'A';
        if (index >= 0 && index < 26) {
            children[index] = node;
        }
    }

    public boolean isEndOfWord() { 
        return isEndOfWord; 
    }

    public void setEndOfWord(boolean endOfWord) { 
        this.isEndOfWord = endOfWord; 
    }
}