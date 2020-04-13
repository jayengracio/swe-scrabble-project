package scrabble.bot;

import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Gaddag {

    private Trie rootTrie = new Trie();

    public Gaddag() {}

    public Gaddag(Set<String> wordList) {
        for (String word : wordList) {
            addWord(word);
        }
    }

    public void addWord(String word) {
        Trie st = rootTrie;

        // Add reverse of word as a path
        for (int i = word.length() - 1; i >= 2; --i) {
            st = st.addArc(word.charAt(i));
        }
        st.addFinalArc(word.charAt(1), word.charAt(0));


        // Add Rev(word[0:l-1])+word[l-1] as path
        st = rootTrie;
        for (int i = word.length() - 2; i >= 0; --i) {
            st = st.addArc(word.charAt(i));
        }
        st = st.addFinalArc('+', word.charAt(word.length()-1));


        // Add rest of the paths
        for (int m = word.length() - 3; m >= 0; --m) {
            final Trie forceSt = st;
            st = rootTrie;

            for (int i = m; i >= 0; --i) {
                st = st.addArc(word.charAt(i));
            }
            st = st.addArc('+');
            st.forceArc(word.charAt(m+1), forceSt);
        }
    }

    public static class Join {
        private String left, middle, right;

        public Join(String middle, String rest) {
            this.middle = middle;
            String[] pieces = rest.split("\\+");

            switch (pieces.length) {
                case 0:
                    this.left = this.right = "";
                    break;
                case 1:
                    this.left = new StringBuilder(pieces[0]).reverse().toString();
                    this.right = "";
                    break;
                case 2:
                    this.left = new StringBuilder(pieces[0]).reverse().toString();
                    this.right = pieces[1];
                    break;
            }
        }

        public Join(String left, String middle, String right) {
            this.left = left;
            this.middle = middle;
            this.right = right;
        }

        public String getLeft() {
            return left;
        }

        public String getMiddle() {
            return middle;
        }

        public String getRight() {
            return right;
        }

        public String getWord() {
            return left + middle + right;
        }

        @Override
        public String toString() {
            return left + '+' + middle + '+' + right;
        }
    }

    public Stream<Join> findWords(String subStr) {
        try {
            String reversed = new StringBuilder(subStr).reverse().toString();
            Trie subTrie = rootTrie.get(reversed);
            return StreamSupport.stream(subTrie.spliterator(), false)
                    .map(s -> new Join(subStr, s));
        } catch (NoSuchElementException e) {
            return Stream.empty();
        }
    }
}
