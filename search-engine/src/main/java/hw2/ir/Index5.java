package hw2.ir;

import java.util.*;


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
     * @param docId unique document id
     * @param title page title
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
            if (word.isEmpty()) continue;
            word = word.toLowerCase();
            if (isStopWord(word)) continue;
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
                if (i != postings.size() - 1) System.out.print(", ");
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
                    postingsFromIds(nextDocs)
            );

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
}
