package hw2.ir;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

// Updated Index5: Indexes crawler results (title + content), not files.

public class Index5 {

    int N = 0; // Number of documents
    public Map<Integer, SourceRecord> sources; // Document ID -> SourceRecord
    public HashMap<String, List<Posting>> index; // Word -> List of Postings

    public Index5() {
        sources = new HashMap<>();
        index = new HashMap<>();
    }

    /**
     * Index one document from the crawler.
     * 
     * @param docId   unique document id
     * @param title   page title
     * @param content plain text of the page
     */
    public void indexDocument(int docId, String title, String content) {
        if (!sources.containsKey(docId)) {
            sources.put(docId, new SourceRecord(docId, title, title, "notext"));
        }
        int flen = indexOneText(content, docId);
        sources.get(docId).length = flen;
    }

    private int indexOneText(String text, int docId) {
        int flen = 0;
        String[] words = text.split("\\W+");

        for (String word : words) {
            if (word.isEmpty())
                continue;
            word = word.toLowerCase();
            if (isStopWord(word))
                continue;
            word = stemWord(word);

            index.computeIfAbsent(word, k -> new ArrayList<>());
            List<Posting> postings = index.get(word);

            Optional<Posting> existing = postings.stream()
                    .filter(p -> p.docId == docId)
                    .findFirst();

            if (existing.isPresent()) {
                existing.get().frequency++;
            } else {
                postings.add(new Posting(docId, 1));
            }

            flen++;
        }

        return flen;
    }

    private boolean isStopWord(String word) {
        String[] stopWords = {
                "a", "an", "and", "are", "as", "at", "be", "by", "for", "from",
                "has", "he", "in", "is", "it", "its", "of", "on", "that", "the",
                "to", "was", "were", "will", "with"
        };
        return Arrays.asList(stopWords).contains(word);
    }

    private String stemWord(String word) {
        Stemmer stemmer = new Stemmer();
        stemmer.addString(word);
        stemmer.stem();
        return stemmer.toString();
    }

    public void printDictionary() {
        for (Map.Entry<String, List<Posting>> entry : index.entrySet()) {
            System.out.print(entry.getKey() + ": ");
            List<Posting> postings = entry.getValue();
            System.out.print("[");
            for (int i = 0; i < postings.size(); i++) {
                System.out.print("(" + postings.get(i).docId + ", " + postings.get(i).frequency + ")");
                if (i != postings.size() - 1)
                    System.out.print(", ");
            }
            System.out.println("]");
        }
    }

    public List<Integer> intersect(List<Posting> pL1, List<Posting> pL2) {
        List<Integer> answer = new ArrayList<>();
        int i = 0, j = 0;
        while (i < pL1.size() && j < pL2.size()) {
            if (pL1.get(i).docId == pL2.get(j).docId) {
                answer.add(pL1.get(i).docId);
                i++;
                j++;
            } else if (pL1.get(i).docId < pL2.get(j).docId) {
                i++;
            } else {
                j++;
            }
        }
        return answer;
    }

    public String findPhrase(String phrase) {
        String[] words = phrase.split("\\W+");
        int len = words.length;

        for (int i = 0; i < len; i++) {
            words[i] = stemWord(words[i].toLowerCase());
        }

        if (!index.containsKey(words[0])) {
            return "No results found for: " + phrase;
        }

        List<Posting> postings = index.get(words[0]);
        int i = 1;

        List<Integer> resultDocs = new ArrayList<>();
        for (Posting p : postings) {
            resultDocs.add(p.docId);
        }

        while (i < len && !resultDocs.isEmpty()) {
            if (!index.containsKey(words[i])) {
                return "No results found for: " + phrase;
            }

            List<Posting> nextPostings = index.get(words[i]);
            List<Integer> nextDocs = new ArrayList<>();
            for (Posting p : nextPostings) {
                nextDocs.add(p.docId);
            }

            resultDocs = intersect(
                    postingsFromIds(resultDocs),
                    postingsFromIds(nextDocs));

            i++;
        }

        if (resultDocs.isEmpty()) {
            return "No results found for: " + phrase;
        }

        StringBuilder sb = new StringBuilder();
        for (Integer docId : resultDocs) {
            sb.append("\t").append(docId).append(" - ").append(sources.get(docId).title).append("\n");
        }

        return sb.toString();
    }

    private List<Posting> postingsFromIds(List<Integer> ids) {
        List<Posting> postings = new ArrayList<>();
        for (Integer id : ids) {
            postings.add(new Posting(id, 1));
        }
        return postings;
    }

    /*
     * @author George G
     */

    /*
     * 
     * this function will be used to take the terms one by one to calc its TF and
     * IDF
     */
    private double compute_TF_IDF(String term, int docId) {
        if (!index.containsKey(term))
            return 0.0;

        // Get term frequency (TF) in the document
        int tf = index.get(term).stream()
                .filter(p -> p.docId == docId)
                .findFirst()
                .map(p -> p.frequency)
                .orElse(0);
        if (tf == 0)
            return 0.0;

        // TF weight: 1 + log10(tf)
        double tfWeight = 1 + Math.log10(tf);

        // IDF: log10(N / df)
        int df = index.get(term).size(); // # of docs
        double idf = Math.log10((double) N / df);

        return tfWeight * idf;
    }

    /**
     * Computes the TF-IDF vector for a document.
     * now each document will have its terms and its TF_IDF values
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
            if (w.isEmpty())
                continue;
            w = w.toLowerCase();
            if (isStopWord(w))
                continue;
            w = stemWord(w);

            // Compute raw term frequency in the query
            int tf = (int) Arrays.stream(words)
                    .filter(W -> W.equalsIgnoreCase(W))
                    .count();
            if (tf == 0)
                continue;

            // Compute TF weight: 1 + log10(tf)
            double tfWeight = 1 + Math.log10(tf);

            // Compute IDF (use the document collection's stats)
            int df = index.getOrDefault(w, Collections.emptyList()).size();
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

        if (docNorm == 0 || queryNorm == 0)
            return 0.0;
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
}
