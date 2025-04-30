package hw2.ir;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

/**
 * Main class to test the inverted index implementation. It builds the index
 * from a given directory and allows interactive search queries.
 */
public class Main {

    /**
     * Main method to run the program.
     *
     * @param args Command-line arguments (not used in this program).
     * @throws IOException If an I/O error occurs.
     */
    public static void main(String args[]) throws IOException {
        Index5 index = new Index5(); // Create an instance of the Index5 class

        // crawler comes to play, later make user enter these
        Crawler crawler = new Crawler();
        LinkedList<SourceRecord> sources = crawler.crawl("https://en.wikipedia.org/wiki/List_of_pharaohs", "https://en.wikipedia.org/wiki/Pharaoh");

        // // Build and store the index
        index.buildWebIndex(sources);
        // index.printDictionary(); // Print the dictionary (optional)
        // Perform interactive search queries
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String phrase;
        do {
            System.out.println("Enter search phrase (or press Enter to exit): ");
            phrase = in.readLine(); // Read user input
            if (!phrase.isEmpty()) {
                // Perform the search and print the results
                System.out.println("Boolean Model Result:\n" + index.find_24_01(phrase));
            }
        } while (!phrase.isEmpty()); // Continue until the user presses Enter without typing a phrase
    }
}
