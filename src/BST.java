import java.io.PrintStream;
import java.util.Comparator;
import java.io.*;
import java.util.StringTokenizer;

public class BST implements WordCounter {

    private class TreeNode {
        WordFreq item;
        TreeNode left;
        TreeNode right;
        int subtreeSize;

        TreeNode(WordFreq item) {
            this.item = item;
            left = right = null;
            subtreeSize = 1;
        }

    }


    private TreeNode root;
    private StringList stopWords;
    private Comparator<WordFreq> comparator;

    public BST(){
        this.root = null;
        this.stopWords = new StringList();
    }

    private BST(Comparator<WordFreq> comparator){
        this();
        this.comparator = comparator;
    }

    // worst-case complexity: O(N)
    public void insert(String w) {
        root = insertR(root, w.toLowerCase().strip());
    }

    private TreeNode insertR(TreeNode r, String w) {

        // if doesn't exist, create new leaf node with new WordFreq item.
        if (r == null) {
            return new TreeNode(new WordFreq(w));
        }

        if (w.equals(r.item.key())) {
            r.item.increaseFreq();  // if already exists, increase word frequency.
            return r;
        }

        if (r.item.key().compareTo(w) > 0) {
            r.left = insertR(r.left, w);
        } else {
            r.right = insertR(r.right, w);
        }

        update(r); // update subtreesize.
        return r;
    }

    // complexity: O(N)
    public void printAlphabetically(PrintStream stream) {
        if (root == null){
            stream.println("There are no words stored inside.");
            return;
        }

        printAlphabeticallyR(root, stream);
    }

    private void printAlphabeticallyR(TreeNode r, PrintStream stream) {

        if (r == null){return;}
        printAlphabeticallyR(r.left, stream);
        stream.print(r.item + "\n");
        printAlphabeticallyR(r.right, stream);
    }

    // complexity: O(N)
    public double getMeanFrequency() {
        if (root == null) return 0;
        return (double) sumOfFreqR(root) / root.subtreeSize;
    }

    private int sumOfFreqR(TreeNode r) {
        if (r == null) {
            return 0;
        }

        int sum = sumOfFreqR(r.left); // get sum of frequencies of left subtree.
        sum += r.item.getFreq();
        sum += sumOfFreqR(r.right);  // get sum of frequencies of right subtree.
        return sum;
    }

    // complexity: O(N)
    public int getTotalWords() {
        return sumOfFreqR(root);
    }

    private TreeNode rotR(TreeNode r) {
        TreeNode x = r.left;
        r.left = x.right;
        x.right = r;

        // update subtree sizes
        update(r);
        update(x);

        return x;
    }

    private TreeNode rotL(TreeNode r) {
        TreeNode x = r.right;
        r.right = x.left;
        x.left = r;

        // update subtree sizes
        update(r);
        update(x);

        return x;
    }

    // worst-case complexity: O(N)
    public int getFrequency(String w) {
        TreeNode t = getFrequencyR(root, w.toLowerCase());
        return (t == null) ? 0 : t.item.getFreq();
    }

    private TreeNode getFrequencyR(TreeNode r, String w) {
        if (r == null) {
            return null;
        }

        if (r.item.key().equals(w)) {
            return r;
        }

        if (r.item.key().compareTo(w) > 0) {
            return getFrequencyR(r.left, w);
        } else {
            return getFrequencyR(r.right, w);
        }
    }

    // worst-case complexity: O(N)
    public WordFreq getMaximumFrequency(){
        if (root == null){
            return null;
        }

        Queue<TreeNode> q = new Queue<>();
        WordFreq max = root.item;
        q.put(root);

        while(!q.isEmpty()){
            TreeNode t = q.get();
            max = t.item.getFreq() > max.getFreq() ? t.item : max;
            if (t.left != null){q.put(t.left);}
            if (t.right != null){q.put(t.right);}
        }

        return max;
    }

    private void update(TreeNode t){
        if (t == null){return;}
        int leftSize = t.left == null ? 0 : t.left.subtreeSize;
        int rightSize = t.right == null ? 0 : t.right.subtreeSize;
        t.subtreeSize = (leftSize + rightSize) + 1;
    }

    public int getDistinctWords(){
        return (root == null) ? 0 : root.subtreeSize;
    }

    // complexity: O(1)
    public void addStopWord(String w){
        if (w == null){return;}
        stopWords.insert(w.toLowerCase().strip());
    }

    // complexity: O(N)
    public void removeStopWord(String w){
        if (w == null){return;}
        stopWords.remove(w.toLowerCase().strip());
    }

    public void remove(String w){
        if (w == null){return;}
        root = removeR(root, w.toLowerCase().strip());
    }

    private TreeNode removeR(TreeNode r, String w){
        if (r == null){return null;}

        String s = r.item.key();
        if (s.compareTo(w) > 0){r.left = removeR(r.left, w);}
        if (s.compareTo(w) < 0){r.right = removeR(r.right, w);}

        // If the string given is found, remove node.
        if (s.equals(w)){r = joinLR(r.left, r.right); }

        update(r); // update subtree size
        return r;
    }

    private TreeNode joinLR(TreeNode a, TreeNode b){

        // If there is no right subtree, return left subtree (or null).
        if (b == null){
            return a;
        }

        // If right subtree exists, find the TreeNode with smallest key and rotate to root of subtree.
        b = partR(b, 1);
        b.left = a;
        update(b); // update subtree size

        return b;
    }

    // Puts TreeNode containing k-smallest key to the root of the (sub)tree.
    private TreeNode partR(TreeNode r, int k){

        int t = (r.left == null) ? 0 : r.left.subtreeSize;

        if (k < t+1) {
            assert r.left != null;
            r.left = partR(r.left, k);
            r = rotR(r);
        }

        if (k > t+1){
            assert r.right != null;
            r.right = partR(r.right, k - (t+1));
            r = rotL(r);
        }

        return r;
    }

    // worst-case complexity: O(N)
    public WordFreq search(String w){

        // If word is on root, return root. Saves time, prevents calculation of mean frequency.
        if (root != null && root.item.key().equals(w)){ return root.item; }

        // Search for word recursively.
        TreeNode f = searchR(root, w.toLowerCase().strip());

        // If word was not found, return null and print according message.
        if (f == null){
            System.out.println("The word \"" + w + "\" was not found.");
            return null;}

        // If word was found, calculate mean frequency, if word's frequency is greater than mean frequency rotate to root.
        if (f.item.getFreq() > getMeanFrequency()){
            rotateToRoot(f.item.key());
        }
        return f.item;
    }


    private TreeNode searchR(TreeNode r, String w){

        // If word does not exist, return null.
        if (r == null){return null;}
        String s = r.item.key();

        // If word was found, return the TreeNode containing the WordFreq object.
        if (s.equals(w)){return r;}

        if (s.compareTo(w) > 0){
            return searchR(r.left, w);
        }else{
            return searchR(r.right, w);
        }
    }

    private void insertByFreq(WordFreq wf){
        root = insertByFreqR(root, wf);
    }

    private TreeNode insertByFreqR(TreeNode r, WordFreq wf){
        // we assume that there will never be same wf object

        if (r == null){return new TreeNode(wf);}
        if (comparator.compare(r.item, wf) > 0){
            r.left = insertByFreqR(r.left, wf);
        }
        // if equal frequencies, insert right to preserve relativity. (because we insert by inorder traversing)
        else{
            r.right = insertByFreqR(r.right, wf);
        }
        return r;
    }

    // complexity O(N^2)
    public void printByFrequency(PrintStream stream){
        BST freqBST = new BST(new WordFreqComparator());
        copyToFreqBST(root, freqBST);
        freqBST.printAlphabetically(stream);
    }

    private void copyToFreqBST(TreeNode r, BST freqBST){
        copyToFreqBSTR(root, freqBST);
    }

    private void copyToFreqBSTR(TreeNode r, BST freqBST){
        if (r == null){return;}

        copyToFreqBSTR(r.left, freqBST);
        freqBST.insertByFreq(r.item);
        copyToFreqBSTR(r.right, freqBST);
    }

    // Finds word and rotates it to root.
    private void rotateToRoot(String w){
        root = rotateToRootR(root, w);
    }

    private TreeNode rotateToRootR(TreeNode r, String w){
        assert r != null;   // we only use the method when the element exists.

        if (r.item.key().equals(w)){return r;}
        if (r.item.key().compareTo(w) > 0){
            r.left = rotateToRootR(r.left, w);
            r = rotR(r);
        }
        else{
            r.right = rotateToRootR(r.right, w);
            r = rotL(r);
        }
        return r;
    }

    // worst-case complexity: O(N^2)
    public void load(String filename){
        System.out.println("Loading contents of \"" + filename + "\"");

        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
            String stopWordRegex = "(?i)\\b(" + stopWords.buildForRegex() + ")\\b"; // Build regex in order to remove stopwords.

            try{
                String line = br.readLine();
                while (line != null){
                    loadLine(line, stopWordRegex); // insert correctly-formatted words (non-stopwords) from line inside the tree.
                    line = br.readLine();
                }
                br.close(); // Close input buffer.

            }catch (IOException e){
                System.out.println("File partly loaded. Error while reading line!");
                return;
            }

        }catch (FileNotFoundException e){
            System.out.println("Couldn't load file. Error opening file!");
            return;
        }
    }

    // worst-case complexity: O(N) (dependent on load() )
    private void loadLine(String line, String stopWordRegex){
        if (line.isEmpty()){return;} // if line is empty, return.

        line = filter(line, stopWordRegex); // filters line by specified rules of proffesor and removes stopwords.
        StringTokenizer tokenizer = new StringTokenizer(line); // tokenize line using whitespace as delimiter.

        String token;
        // While line has more words.
        while(tokenizer.hasMoreTokens()) {
            token = tokenizer.nextToken();
            insert(token.toLowerCase());
        }
    }

    // Parses and filters line by the rules below in the specified sequence:
    // 1. Removes any leading and preceding punctuation from "words" separated by whitespace (or start/end of string)
    // 2. Any "word" that contains punctuation in between that is not a SINGLE apostrophe, is removed entirely.
    // 3. Any "word" that contains at least one numeric character is removed entirely.
    // 4. Removes all stopwords from the line.
    private String filter(String line, String stopWordRegex){
        line = line.replaceAll("((?<!\\S)\\p{Punct}+)|(\\p{Punct}+(?!\\S))", ""); // 1
        line = line.replaceAll("\\b[a-zA-Z]+(?:\\u0027[a-zA-Z]*)?+\\p{Punct}[a-zA-Z\\p{Punct}]*[a-zA-Z]\\b", ""); // 2
        line = line.replaceAll("\\b[[a-zA-Z]*[\\p{Punct}]*]*\\d+[^\\s]*\\b", ""); // 3
        line = line.replaceAll(stopWordRegex, ""); // 4

        return line;
    }

    // ! In the examples below, nothing has been escaped (i.e \b should be \\b) !

    // 1. removes all trailing and preceding punctuation ex "$*-some*thing_)*" -> "some*thing"

    // "((?<!\S)\p{Punct}+)|(\p{Punct}+(?!\S))"
    // "(?<!\S)\p{Punct}+" using negative lookbehind, the string to be replaced must not have a word behind and must be punctuation.
    // "|(\p{Punct}+(?!\S)" OR using negative lookahead, the string must not have a word infront and must be punctuation.


    // 2. removes all words that have inbetween punctuation except if it's single apostrophe

    // \b[a-zA-Z]+(?:\u0027[a-zA-Z]*)?+\p{Punct}[a-zA-Z\p{Punct}]*[a-zA-Z]\b
    // "[a-zA-Z]+" must start with word (also word must be preceded by word boundary)
    // "(?:\u0027[a-zA-Z]*)?+" if single apostrophe, then do not match, also don't backtrack otherwise will also match with single apostrophes cause of the next regex part
    // "\p{Punct}" at least one punctuation (now established the apostrophe won't be single)
    // "[a-zA-Z\p{Punct}]*[a-zA-Z]\b" so we can remove any more punctuation/word if exists, must also end with word and be leaded by word boundary.

    // 3. removes any words containing digits

    // "\b[[a-zA-Z]*[\p{Punct}]*]*\d+[^\s]*\b"
    // from the previous removals, it's guaranteed that any "token" will start with alphanumeric character so we match indicate "[a-zA-Z]*"
    // "[\p{Punct}]*" it may be of the form "something'324" so we want to match case of alpha leaded by possible punctuation
    // above contained inside []* because above may repeat in some form, i.e "so'me'324"
    // "\d+[^\s]*" if it contains at least one digit, match up to whitespace so we can remove.
    // "\b" must be preceded and leaded by word boundary

    // 4. removes stopwords ignoring case

    // "(?i)" ignore case
    // "\b" used because we must match exact words (so we need preceding and leading word boundaries)


}











