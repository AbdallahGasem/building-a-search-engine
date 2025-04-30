package hw2.ir;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
                    // flen += indexOneLine(ln, fid);
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
            String[] words = record.text.split("\\W+");
            // for (String line : lines) {
            indexOneLine(words, fid);
            // }
            fid++; // Increment document ID
        }
    }

    /**
     * Processes a single line from a document, extracting words and adding them
     * to the index.
     *
     * @param ln The line of text to be processed.
     * @param fid The document ID.
     * @return The number of words processed in the line.
     */
    public int indexOneLine(String[] words, int fid) {
        // String[] words = ln.split("\\W+"); // Split the line into words using non-word characters as delimiters
        int flen = words.length;

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

        // Debugging
        System.out.println("PHRASE LENGTH: " + len);

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
        Map<Integer, Double> scores = new HashMap<>();
        while (posting != null) {
            double similarity = computeCosineSimilarity(posting.docId, phrase);
            scores.put(posting.docId, similarity);
            posting = posting.next;
        }

        // Rank the documents by similarity
        List<Integer> rankedDocs = scores.entrySet().stream()
                .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        for (int docId : rankedDocs) {
            result += "\t" + docId + " - " + sources.get(docId).getURL() + "\n";
        }

        if (result.isEmpty()) {
            return "No results found for: " + phrase;
        }
        return result;
    }

    /*
     * @author George G
     */
    private boolean isStopWord(String word) {
        String[] stopWords = {
            "a", "an", "and", "are", "as", "at", "be", "by", "for", "from",
            "has", "he", "in", "is", "it", "its", "of", "on", "that", "the",
            "to", "was", "were", "will", "with"
        };
        return Arrays.asList(stopWords).contains(word);
    }

    /*
     * 
     * this function will be used to take the terms one by one to calc its TF and
     * IDF
     */
    private double compute_TF_IDF(String term, int docId) {
        if (!index.containsKey(term)) {
            return 0.0;
        }

        // Get term frequency (TF) in the document
        Posting posting = index.get(term).pList;
        int tf = 0;
        while (posting != null) {
            if (posting.docId == docId) {
                tf = posting.dtf; // Get term frequency in the document
                break;
            }
            posting = posting.next;
        }
        if (tf == 0) {
            return 0.0;
        }

        // TF weight: 1 + log10(tf)
        double tfWeight = 1 + Math.log10(tf);

        // IDF: log10(N / df)
        int df = index.get(term).doc_freq; // Document frequency
        double idf = Math.log10((double) N / df);

        return tfWeight * idf;
    }

    /**
     * Computes the TF-IDF vector for a document. now each document will have
     * its terms and its TF_IDF values
     */
    private Map<String, Double> getDocumentVector(int docId) {
        Map<String, Double> docVector = new HashMap<>();
        for (var entry : index.entrySet()) {
            String term = entry.getKey();
            double tfidf = compute_TF_IDF(term, docId);
            if (tfidf > 0) {
                docVector.put(term, tfidf);
            }
        }
        return docVector;
    }

    /**
     * Computes the TF-IDF vector for a query.
     */
    private Map<String, Double> getQueryVector(String query) {
        Map<String, Double> queryVector = new HashMap<>();
        String[] words = query.split("\\W+");

        for (String w : words) {
            if (w.isEmpty()) {
                continue;
            }
            w = w.toLowerCase();
            if (isStopWord(w)) {
                continue;
            }
            w = stemWord(w);

            // Compute raw term frequency in the query
            int tf = (int) Arrays.stream(words)
                    .filter(W -> W.equalsIgnoreCase(W))
                    .count();
            if (tf == 0) {
                continue;
            }

            // Compute TF weight: 1 + log10(tf)
            double tfWeight = 1 + Math.log10(tf);

            // Compute IDF (use the document collection's stats)
            int df = index.getOrDefault(w, new DictEntry()).doc_freq;
            double idf = (df == 0) ? 0 : Math.log10((double) N / df);

            queryVector.put(w, tfWeight * idf);
        }
        return queryVector;
    }

    /*
     * calculating cosine similarity
     * cosine_similarity(d,q)=∥d∥*∥q∥/d⋅q
     */
    /**
     * Computes norm of a vector.
     */
    private double computeNorm(Map<String, Double> vector) {
        double sum = 0.0;
        for (double value : vector.values()) {
            sum += value * value;
        }
        return Math.sqrt(sum);
    }

    /**
     * Computes the dot product of two vectors.
     */
    private double computeDotProduct(Map<String, Double> v1, Map<String, Double> v2) {
        double dotProduct = 0.0;
        for (String term : v1.keySet()) {
            if (v2.containsKey(term)) {
                dotProduct += v1.get(term) * v2.get(term);
            }
        }
        return dotProduct;
    }

    /**
     * Computes cosine similarity between a document and a query.
     */
    private double computeCosineSimilarity(int docId, String query) {
        Map<String, Double> docVector = getDocumentVector(docId);
        Map<String, Double> queryVector = getQueryVector(query);

        double dotProduct = computeDotProduct(docVector, queryVector);
        double docNorm = computeNorm(docVector);
        double queryNorm = computeNorm(queryVector);

        if (docNorm == 0 || queryNorm == 0) {
            return 0.0;
        }
        return dotProduct / (docNorm * queryNorm);
    }

    // ranking
    /**
     * Ranks documents by cosine similarity to a query and returns the top 10.
     */
    public List<Integer> rankDocumentsByQuery(String query) {
        // Map to store docId -> similarity score
        Map<Integer, Double> scores = new HashMap<>();

        for (int docId : sources.keySet()) {
            double similarity = computeCosineSimilarity(docId, query);
            scores.put(docId, similarity);
        }
        // Sort by similarity and return top 10 docIds
        return scores.entrySet().stream()
                .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
                .limit(10)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
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
