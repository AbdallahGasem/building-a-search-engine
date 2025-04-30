package hw2.ir;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * This class represents an inverted index that maps words to their occurrences
 * in documents. It allows building the index, performing Boolean queries, and
 * printing results.
 */
public class Index5 {

    int N = 0; // Number of documents
    public Map<Integer, SourceRecord> sources; // Map of document IDs to their metadata, ana hal3b hna (Gasem)!
    public HashMap<String, DictEntry> index; // The inverted index (word -> posting list)

    public Index5() {
        sources = new HashMap<>();
        index = new HashMap<>();
    }

    /**
     * Builds the inverted index from a list of text files.
     *
     * @param files Array of file paths to be indexed.
     */
    public void buildIndex(String[] files) {    // momken adelo list el source records w yemshy zay ma howa!
        int fid = 0; // Document ID counter
        for (String fileName : files) {
            try (BufferedReader file = new BufferedReader(new FileReader(fileName))) {    // NONEED
                // Add document metadata to the sources map if not already present
                if (!sources.containsKey(fid)) {
                    sources.put(fid, new SourceRecord(fid, fileName, fileName, "notext"));  // RE
                }
                String ln;  // NONEED
                int flen = 0; // Word count for the current file    // NONEED

                // Read each line in the file and index it
                while ((ln = file.readLine()) != null) {
                    flen += indexOneLine(ln, fid);
                }

                // Update the document length in the metadata
                sources.get(fid).length = flen; // NONEED
            } catch (IOException e) {
                System.out.println("File " + fileName + " not found. Skipping...");
            }
            fid++; // Increment document ID
        }
    }

    // new for crawler
    public void buildWebIndex(LinkedList<SourceRecord> records) {    // momken adelo list el source records w yemshy zay ma howa!
        int fid = 0; // Document ID counter, can be removed as i did an incremental counter in crawler for source docs!
        for (SourceRecord record : records) {
            // Add document metadata to the sources map if not already present
            if (!sources.containsKey(fid)) {
                sources.put(fid, record);
            }

            // Read each line in the file and index it, Reading a string from the source record now not from a file!
            String[] lines = record.text.split("\\n");
            for (String line : lines) {
                indexOneLine(line, fid);
            }
        }
        fid++; // Increment document ID
    }

    /**
     * Processes a single line from a document, extracting words and adding them
     * to the index.
     *
     * @param ln The line of text to be processed.
     * @param fid The document ID.
     * @return The number of words processed in the line.
     */
    public int indexOneLine(String ln, int fid) {
        int flen = 0; // Word count for the current line
        String[] words = ln.split("\\W+"); // Split the line into words using non-word characters as delimiters
        flen += words.length;

        // Process each word in the line
        for (String word : words) {
            word = word.toLowerCase(); // Convert word to lowercase
            if (stopWord(word)) { // Skip stop words
                continue;
            }
            word = stemWord(word); // Stem the word

            // Add the word to the index if not already present
            if (!index.containsKey(word)) {
                index.put(word, new DictEntry());
            }

            // Update the posting list for the word
            if (!index.get(word).postingListContains(fid)) {
                index.get(word).doc_freq += 1; // Increment document frequency
                if (index.get(word).pList == null) {
                    index.get(word).pList = new Posting(fid); // Create a new posting list
                    index.get(word).last = index.get(word).pList;
                } else {
                    index.get(word).last.next = new Posting(fid); // Add to the existing posting list
                    index.get(word).last = index.get(word).last.next;
                }
            } else {
                index.get(word).last.dtf += 1; // Increment term frequency in the document
            }
            index.get(word).term_freq += 1; // Increment overall term frequency
        }
        return flen;
    }

    /**
     * Checks if a word is a stop word.
     *
     * @param word The word to check.
     * @return True if the word is a stop word, false otherwise.
     */
    private boolean stopWord(String word) {
        // List of common stop words
        String[] stopWords = {"a", "an", "and", "are", "as", "at", "be", "by", "for", "from", "has", "he", "in", "is", "it", "its", "of", "on", "that", "the", "to", "was", "were", "will", "with"};
        for (String stopWord : stopWords) {
            if (word.equals(stopWord)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Stems a word using the Porter Stemming Algorithm.
     *
     * @param word The word to stem.
     * @return The stemmed word.
     */
    private String stemWord(String word) {
        Stemmer stemmer = new Stemmer();
        stemmer.addString(word); // Add the word to the stemmer
        stemmer.stem(); // Perform stemming
        return stemmer.toString(); // Return the stemmed word
    }

    /**
     * Prints the dictionary (inverted index) in a readable format.
     */
    public void printDictionary() {
        for (Map.Entry<String, DictEntry> entry : index.entrySet()) {
            System.out.print(entry.getKey() + ": ");
            printPostingList(entry.getValue().pList); // Print the posting list for the word
        }
    }

    /**
     * Performs an intersection of two posting lists to find common document
     * IDs.
     *
     * @param pL1 First posting list.
     * @param pL2 Second posting list.
     * @return A new posting list containing only the common document IDs.
     */
    public Posting intersect(Posting pL1, Posting pL2) {
        Posting answer = null; // Resulting posting list
        Posting last = null; // Pointer to the last node in the resulting list

        // Traverse both posting lists
        while (pL1 != null && pL2 != null) {
            if (pL1.docId == pL2.docId) { // Common document ID found
                if (answer == null) {
                    answer = new Posting(pL1.docId); // Create the first node in the result
                    last = answer;
                } else {
                    last.next = new Posting(pL1.docId); // Add to the result
                    last = last.next;
                }
                pL1 = pL1.next;
                pL2 = pL2.next;
            } else if (pL1.docId < pL2.docId) { // Move pointer in the first list
                pL1 = pL1.next;
            } else { // Move pointer in the second list
                pL2 = pL2.next;
            }
        }
        return answer;
    }

    /**
     * Prints a posting list in a readable format.
     *
     * @param p The posting list to print.
     */
    public void printPostingList(Posting p) {
        System.out.print("[");
        while (p != null) {
            System.out.print(p.docId); // Print document ID
            if (p.next != null) {
                System.out.print(", "); // Print comma if not the last element
            }
            p = p.next; // Move to the next node
        }
        System.out.println("]");
    }

    /**
     * Finds documents that contain all the words in a given phrase.
     *
     * @param phrase The search phrase.
     * @return A string containing the document IDs and titles of matching
     * documents.
     */
    public String find_24_01(String phrase) {
        String result = "";
        String[] words = phrase.split("\\W+"); // Split the phrase into words
        int len = words.length;

        // Apply stemming to each word in the phrase
        for (int i = 0; i < len; i++) {
            words[i] = stemWord(words[i].toLowerCase()); // Stem the word
        }

        // Check if the first word exists in the index
        if (!index.containsKey(words[0])) {
            return "No results found for: " + phrase;
        }

        // Get the posting list for the first word
        Posting posting = index.get(words[0]).pList;
        int i = 1;

        // Intersect the posting lists of the remaining words
        while (i < len && posting != null) {
            if (!index.containsKey(words[i])) {
                return "No results found for: " + phrase;
            }
            posting = intersect(posting, index.get(words[i]).pList);
            i++;
        }

        // Collect the results
        while (posting != null) {
            result += "\t" + posting.docId + " - " + sources.get(posting.docId).title + sources.get(posting.docId).getURL() + "\n";
            posting = posting.next;
        }

        if (result.isEmpty()) {
            return "No results found for: " + phrase;
        }
        return result;
    }

    /**
     * Sorts an array of words in lexicographical order.
     *
     * @param words The array of words to sort.
     * @return The sorted array of words.
     */
    public String[] sort(String[] words) {
        boolean sorted = false;
        while (!sorted) {
            sorted = true;
            for (int i = 0; i < words.length - 1; i++) {
                if (words[i].compareTo(words[i + 1]) > 0) { // Swap if out of order
                    String temp = words[i];
                    words[i] = words[i + 1];
                    words[i + 1] = temp;
                    sorted = false;
                }
            }
        }
        return words;
    }
}
