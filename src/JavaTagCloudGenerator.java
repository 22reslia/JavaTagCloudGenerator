import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Simple HelloWorld program (clear of Checkstyle and SpotBugs warnings).
 *
 * @author P. Bucci
 */
public final class JavaTagCloudGenerator {

    /**
     * No argument constructor--private to prevent instantiation.
     */
    private JavaTagCloudGenerator() {
        // no code needed here
    }

    /**
     * prints HTML code that isn't dependent on input file to output file.
     *
     * @param sortedKeys
     * @param words
     * @param outputFile
     * @param boringOutput
     * @param inputFile
     * @param numWords
     */
    public static void generateHTML(ConcurrentSkipListSet<String> sortedKeys,
            Map<String, Integer> words, PrintWriter outputFile,
            boolean boringOutput, String inputFile, int numWords) {
        outputFile.println("<!DOCTYPE html>");
        outputFile.println("<html>");
        outputFile
                .println("<meta name=viewport content=\"width=device-width\">");
        outputFile.println("<head>");
        outputFile
                .println("<title> Top 100 words in " + inputFile + " </title>");
        outputFile.println(
                "<link href=\"http://web.cse.ohio-state.edu/software/2231/web-sw2"
                        + "/assignments/projects/tag-cloud-generator/data/tagcloud"
                        + ".css\" rel=\"stylesheet\" type=\"text/css\">");
        outputFile.println(
                "<link href=\"data/tagcloud.css\" rel=\"stylesheet\" type=\"text/css\">");
        outputFile.println("</head>");
        outputFile.println("<body>");
        outputFile.print("<h2>");
        outputFile.println("Top 100 words in " + inputFile + "</h2>");
        outputFile.println("<hr>");
        outputFile.println("<div class=\"cdiv\">");
        outputFile.println("<p class=\"cbox\">");
        generateMapHTML(sortedKeys, words, outputFile, boringOutput, numWords);
        outputFile.println("</p>");
        outputFile.println("</div>");
        outputFile.println("</body>");
        outputFile.println("</html>");
    }

    /**
     *
     * @param sortedKeys
     * @param words
     * @param outputFile
     * @param boringOutput
     * @param numWords
     */
    public static void generateMapHTML(ConcurrentSkipListSet<String> sortedKeys,
            Map<String, Integer> words, PrintWriter outputFile,
            boolean boringOutput, int numWords) {
        for (int i = 0; i < numWords; i++) {
            String first = sortedKeys.pollFirst();
            Map.Entry<String, Integer> tempPair = new SimpleEntry<String, Integer>(
                    first, words.get(first));
            int x = fontSize(words, tempPair.getValue(), numWords);
            outputFile.println("<span style=\"cursor:default\" class=\"f" + x
                    + "\" title=\"count: " + tempPair.getValue() + "\">"
                    + tempPair.getKey() + "</span>");
        }
    }

    /**
     *
     * @param words
     * @param count
     * @param numWords
     * @return fontSize
     */
    public static int fontSize(Map<String, Integer> words, int count,
            int numWords) {
        final int min = 11;
        final int max = 48;
        int a = max - min;
        int b = (count - min) / a;
        int fontSize = min + b;
        return fontSize;
    }

    /**
     * returns a set with every letter contained in a word.
     *
     * @return letters
     */
    public static Set<Character> generateLetters() {
        HashSet<Character> letters = new HashSet<Character>();
        letters.add(',');
        letters.add('.');
        letters.add(' ');
        letters.add('?');
        letters.add('!');
        letters.add(';');
        letters.add(':');
        letters.add('$');
        letters.add('%');
        letters.add('^');
        letters.add('&');
        letters.add('*');
        letters.add('(');
        letters.add(')');
        letters.add('_');
        letters.add('-');
        letters.add('=');
        letters.add('+');
        letters.add('[');
        letters.add(']');
        letters.add('{');
        letters.add('}');
        letters.add('|');
        letters.add('\\');
        letters.add('`');
        letters.add('\"');
        letters.add('>');
        letters.add('<');
        return letters;
    }

    /**
     *
     * @param words
     * @param order
     * @return sortedKeys
     */
    public static ConcurrentSkipListSet<String> sortMapKeys(
            Map<String, Integer> words, Comparator<String> order) {

        ConcurrentSkipListSet<String> ordered = new ConcurrentSkipListSet<String>(
                order);
        Set<String> keys = words.keySet();
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()) {
            String tempWord = iterator.next();
            ordered.add(tempWord);
        }
        return ordered;
    }

    /**
     * reads input file and updates map accordingly.
     *
     * @updates termCounts
     *
     * @param termCounts
     * @param inputFile
     */
    public static void updateMapFromFile(HashMap<String, Integer> termCounts,
            BufferedReader inputFile) {
        try {
            while (inputFile.readLine() != null) {
                int pos = 0;
                String line = inputFile.readLine();
                String nextLine = line.toLowerCase();
                int endPos = nextLine.length();
                Set<Character> specialChars = new HashSet<Character>();
                specialChars = generateLetters();
                while (pos < endPos) {
                    String nextWordOrSeparator = nextWordOrSeparator(nextLine,
                            pos, specialChars);
                    pos += nextWordOrSeparator.length();
                    if (!specialChars.contains(nextWordOrSeparator.charAt(0))) {
                        if (!termCounts.containsKey(nextWordOrSeparator)) {
                            termCounts.put(nextWordOrSeparator, 1);
                        } else {
                            termCounts.replace(nextWordOrSeparator,
                                    termCounts.get(nextWordOrSeparator) + 1);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    public static void updateMapNumberOfEntries(
            HashMap<String, Integer> termCounts, int numWords,
            Comparator<String> order) {
        // Temporary map to store the entries
        HashMap<String, Integer> temp = new HashMap<>(termCounts);
        termCounts.clear();

        // Using a priority queue to sort the counts
        PriorityQueue<Map.Entry<String, Integer>> queue = new PriorityQueue<>(
                (a, b) -> b.getValue() - a.getValue());
        queue.addAll(temp.entrySet());

        // Finding the minimum number to be included
        int minNum = -1;
        List<Map.Entry<String, Integer>> entries = new ArrayList<>();
        for (int i = 0; i < numWords && !queue.isEmpty(); i++) {
            Map.Entry<String, Integer> entry = queue.poll();
            entries.add(entry);
            minNum = entry.getValue();
        }

        // Separate map for entries with the minimum number
        HashMap<String, Integer> temp2 = new HashMap<>();
        List<String> temp3 = new ArrayList<>();

        // Adding entries back to termCounts and handling ties
        for (Map.Entry<String, Integer> entry : temp.entrySet()) {
            if (entry.getValue() > minNum) {
                termCounts.put(entry.getKey(), entry.getValue());
            } else if (entry.getValue() == minNum) {
                temp2.put(entry.getKey(), entry.getValue());
                temp3.add(entry.getKey());
            }
        }

        // Sorting the ties based on the provided comparator
        temp3.sort(order);
        for (String key : temp3) {
            if (termCounts.size() < numWords) {
                termCounts.put(key, temp2.get(key));
            }
        }
    }

    /**
     *
     * @param terms
     * @return minMax
     */
    public static int[] minMaxCount(Map<String, Integer> terms) {
        int[] minMax = new int[2];
        minMax[0] = -1;
        minMax[1] = -1;
        Set<String> keys = terms.keySet();
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()) {
            String temp = iterator.next();
            if (minMax[0] == -1 || terms.get(temp) < minMax[0]) {
                minMax[0] = terms.get(temp);
            }
            if (minMax[1] == -1 || terms.get(temp) > minMax[1]) {
                minMax[1] = terms.get(temp);
            }
        }
        return minMax;
    }

    /**
     *
     * @param text
     * @param position
     * @param separators
     * @return nextWordOrSeparator
     */
    public static String nextWordOrSeparator(String text, int position,
            Set<Character> separators) {
        assert text != null : "Violation of: text is not null";
        assert separators != null : "Violation of: separators is not null";
        assert 0 <= position : "Violation of: 0 <= position";
        assert position < text.length() : "Violation of: position < |text|";

        boolean isWord = true;
        String nextWordOrSeparator = "";
        char initialChar = text.charAt(position);
        if (isWord && separators.contains(initialChar)) {
            isWord = false;
        }
        nextWordOrSeparator += text.substring(position, position + 1);
        boolean foundOtherCharType = false;
        if (isWord) {
            while (!foundOtherCharType && position < text.length() - 1) {
                position++;
                if (separators.contains(text.charAt(position))) {
                    foundOtherCharType = true;
                } else {
                    nextWordOrSeparator += text.substring(position,
                            position + 1);
                }
            }
        } else {
            boolean foundSeparator = false;
            while (!foundOtherCharType && position < text.length() - 1) {
                position++;
                if (separators.contains(text.charAt(position))) {
                    nextWordOrSeparator += text.substring(position,
                            position + 1);
                } else {
                    foundOtherCharType = true;
                }
            }
        }
        return nextWordOrSeparator;

    }

    /**
     *
     * @author shivam
     *
     */
    public static class Comparator1L implements Comparator<String> {

        /**
         *
         */
        @Override
        public int compare(String o1, String o2) {
            return o1.toLowerCase().compareTo(o2.toLowerCase());
        }

    }

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments; unused here
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        BufferedReader in = new BufferedReader(
                new InputStreamReader(System.in));

        System.out.println("Enter the name of an input file: ");
        String inputFile = in.readLine();
        System.out.println("Enter the name of an output file: ");
        String outputFile = in.readLine();
        System.out
                .println("How many words do you want to be in the tag storm?");
        int n = in.read();

        PrintWriter outFile = new PrintWriter(
                new BufferedWriter(new FileWriter(outputFile)));
        BufferedReader inFile = new BufferedReader(new FileReader(inputFile));

        Comparator<String> order = new Comparator1L();
        HashMap<String, Integer> terms = new HashMap<String, Integer>();
        updateMapFromFile(terms, inFile);
        updateMapNumberOfEntries(terms, n, order);
        ConcurrentSkipListSet<String> sortedKeys = sortMapKeys(terms, order);
        generateHTML(sortedKeys, terms, outFile, false, inputFile, n);

        in.close();
        outFile.close();
        inFile.close();
    }

}
