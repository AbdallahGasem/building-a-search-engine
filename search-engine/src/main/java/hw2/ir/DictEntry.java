/*
 * This class represents a dictionary entry in an inverted index.
 * It stores information about a term, such as its document frequency,
 * term frequency, and a linked list of postings (documents where the term appears).
 */
package hw2.ir;

/**
 * Represents a dictionary entry for a term in an inverted index.
 */
public class DictEntry {

    // The number of documents that contain the term.
    public int doc_freq = 0;

    // The total number of times the term appears in the entire collection.
    public int term_freq = 0;

    // The head of the linked list of postings (documents where the term appears).
    Posting pList = null;

    // A reference to the last posting in the linked list (used for efficient appending).
    Posting last = null;

    /**
     * Checks if the posting list contains a specific document ID.
     * 
     * @param i The document ID to search for.
     * @return True if the document ID is found, otherwise false.
     */
    boolean postingListContains(int i) {
        boolean found = false; // Flag to indicate if the document ID is found.
        Posting p = pList; // Start from the head of the posting list.
        while (p != null) { // Traverse the linked list.
            if (p.docId == i) { // If the document ID matches, return true.
                return true;
            }
            p = p.next; // Move to the next posting.
        }
        return found; // Return false if the document ID is not found.
    }

    /**
     * Retrieves the term frequency (dtf) for a specific document ID.
     * 
     * @param i The document ID to search for.
     * @return The term frequency if the document ID is found, otherwise 0.
     */
    int getPosting(int i) {
        int found = 0; // Default value if the document ID is not found.
        Posting p = pList; // Start from the head of the posting list.
        while (p != null) { // Traverse the linked list.
            if (p.docId >= i) { // If the current document ID is greater than or equal to the target ID.
                if (p.docId == i) { // If the document ID matches, return the term frequency.
                    return p.dtf;
                } else { // If the document ID is greater but does not match, return 0.
                    return 0;
                }
            }
            p = p.next; // Move to the next posting.
        }
        return found; // Return 0 if the document ID is not found.
    }

    /**
     * Adds a new posting (document ID) to the posting list.
     * 
     * @param i The document ID to add.
     */
    void addPosting(int i) {
        if (pList == null) { // If the posting list is empty, create the first posting. el head ya3ny
            pList = new Posting(i);
            last = pList; // Update the last pointer to the new posting.
        } else { // If the posting list is not empty, append the new posting to the end.
            last.next = new Posting(i);
            last = last.next; // Update the last pointer to the new posting.
        }
    }

    /**
     * Default constructor for the DictEntry class.
     * Initializes an empty dictionary entry.
     */
    DictEntry() {
        // No initialization needed for now.
    }

    /**
     * Parameterized constructor for the DictEntry class.
     * 
     * @param df The document frequency of the term.
     * @param tf The term frequency of the term.
     */
    DictEntry(int df, int tf) {
        doc_freq = df; // Initialize the document frequency.
        term_freq = tf; // Initialize the term frequency.
    }

    Object stream() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
