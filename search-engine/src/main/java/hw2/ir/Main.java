package hw2.ir;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

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

        // // NONEED
        // String filesDirectory = "E:\\FCAICU\\3rd-YEAR\\2nd-Term\\IR\\Assignments\\A2\\building-a-search-engine\\search-engine\\src\\test\\java"; // Change to your directory path   // input stuff needs to be edited !
        // File file = new File(filesDirectory);   // NONEED

        // // Get the list of files in the directory // NONEED
        // String[] fileList = file.list();
        // if (fileList == null) {
        //     System.out.println("No files found in directory: " + filesDirectory);
        //     return;
        // }

        // // Sort the file list alphabetically    // NONEED
        // fileList = index.sort(fileList);
        // index.N = fileList.length; // Set the number of documents

        // // Prepend the directory path to each file name // NONEED
        // for (int i = 0; i < fileList.length; i++) {
        //     fileList[i] = filesDirectory + File.separator + fileList[i];
        // }

        // crawler comes to play, later make user enter these
        Crawler crawler = new Crawler();
        // LinkedList<SourceRecord> sources = crawler.crawl("https://en.wikipedia.org/wiki/List_of_pharaohs", "https://en.wikipedia.org/wiki/Pharaoh");

        SourceRecord rec = crawler.scrapper("https://en.wikipedia.org/wiki/Pharaoh", 1);

        System.out.println("\n===================================Program Starting=================================\n");
        
        System.out.println("FID:==============================" + rec.fid);
        System.out.println("URL:==============================" + rec.URL);
        System.out.println(":==============================" + rec.length);
        System.out.println(":==============================" + rec.norm);
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"))) {
            writer.write(rec.text);
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
        // System.out.println("TITLE:==============================" + rec.title);

        System.out.println("\n====================================Program Ending=================================\n");

        // // Build and store the index
        // index.buildWebIndex(sources);     
        // index.printDictionary(); // Print the dictionary (optional)

        // // Perform interactive search queries
        // BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        // String phrase;
        // do {
        //     System.out.println("Enter search phrase (or press Enter to exit): ");
        //     phrase = in.readLine(); // Read user input
        //     if (!phrase.isEmpty()) {
        //         // Perform the search and print the results
        //         System.out.println("Boolean Model Result:\n" + index.find_24_01(phrase));
        //     }
        // } while (!phrase.isEmpty()); // Continue until the user presses Enter without typing a phrase
    }
}