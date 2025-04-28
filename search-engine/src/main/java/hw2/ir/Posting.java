package hw2.ir;


 // Posting = one document and the frequency of a word inside it.

public class Posting {
    public int docId;
    public int frequency;

    public Posting(int docId, int frequency) {
        this.docId = docId;
        this.frequency = frequency;
    }
}
