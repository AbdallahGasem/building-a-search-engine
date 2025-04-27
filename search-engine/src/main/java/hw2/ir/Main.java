package hw2.ir;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

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
        String filesDirectory = "E:\\FCAICU\\3rd-YEAR\\2nd-Term\\IR\\Assignments\\A2\\building-a-search-engine\\search-engine\\src\\test\\java"; // Change to your directory path   // input stuff needs to be edited !
        File file = new File(filesDirectory);

        // Get the list of files in the directory
        String[] fileList = file.list();
        if (fileList == null) {
            System.out.println("No files found in directory: " + filesDirectory);
            return;
        }

        // Sort the file list alphabetically
        fileList = index.sort(fileList);
        index.N = fileList.length; // Set the number of documents

        // Prepend the directory path to each file name
        for (int i = 0; i < fileList.length; i++) {
            fileList[i] = filesDirectory + File.separator + fileList[i];
        }

        // Build and store the index
        index.buildIndex(fileList);     // this needs to be edited
        index.printDictionary(); // Print the dictionary (optional)

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