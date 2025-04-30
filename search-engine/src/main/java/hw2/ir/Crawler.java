package hw2.ir;

import java.io.IOException;
import java.util.LinkedList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * The Crawler class is responsible for web crawling and scraping operations. It
 * provides methods to filter URLs, scrape content from web pages, and manage a
 * crawling process to collect data from a set of seed URLs.
 */
public class Crawler {

    public void main(String[] args) {
        System.out.println("\n===================================Program Starting=================================\n");

        scrapper("https://en.wikipedia.org/wiki/List_of_pharaohs", 0);

        System.out.println("\n====================================Program Ending=================================\n");
    }

    /**
     * Filters a given URL to check if it starts with
     * "https://en.wikipedia.org/wiki/".
     *
     * @param URL The URL to be checked.
     * @return {@code true} if the URL starts with
     * "https://en.wikipedia.org/wiki/", {@code false} otherwise.
     */
    public boolean filter(String URL) {
        // match the URL to assure it starts with "https://en.wikipedia.org/wiki/"
        return URL.matches("^https://en\\.wikipedia\\.org/wiki/.*$");
    }

    /**
     * Scrapes URLs from two seed URLs and filters them using `fliter(String
     * URL)` fn
     *
     *
     * This method connects to the provided seed URLs, extracts all hyperlinks
     * within the "bodyContent" section of the HTML, and filters them using the
     * {@code filter} method. The filtered URLs are added to a list along with
     * the seed URLs.
     *
     * @param seed1 The first seed URL to scrape.
     * @param seed2 The second seed URL to scrape.
     * @return A LinkedList containing the seed URLs and the filtered URLs
     * extracted from the "bodyContent" of the seed pages.
     * @throws IOException If a connection error occurs while accessing the seed
     * URLs.
     */
    public LinkedList<String> urlScrapper(String seed1, String seed2) {

        // make a list of filtered links and add the seeds to explore them later
        LinkedList<String> validUrls = new LinkedList<>();
        validUrls.add(seed1);
        validUrls.add(seed2);

        // exploring seed1
        try {
            Document doc1 = Jsoup.connect(seed1).get();
            Element content1 = doc1.getElementById("bodyContent");
            Elements links1 = content1.select("a"); // get all links within the bodyContent to start filtering for crawling

            for (Element lnk : links1) {
                if (filter(lnk.absUrl("href"))) {
                    validUrls.add(lnk.absUrl("href"));
                }
            }

        } catch (IOException e) {
            System.out.println("UrlScrapper(): Connection Error at Seed-1: " + seed1);
        }

        // exploring seed2
        try {

            Document doc2 = Jsoup.connect(seed2).get();
            Element content2 = doc2.getElementById("bodyContent");
            Elements links2 = content2.select("a"); // get all links within the bodyContent to start filtering for crawling

            for (Element lnk : links2) {
                if (filter(lnk.absUrl("href"))) {
                    validUrls.add(lnk.absUrl("href"));
                }
            }

        } catch (IOException e) {
            System.out.println("UrlScrapper(): Connection Error at Seed-2: " + seed2);
        }

        return validUrls;
    }

    /**
     * Scrapes the content of a web page from the given seed URL and returns it
     * as a SourceRecord.
     *
     * @param seedURL The URL of the web page to scrape.
     * @param fid A unique identifier for the source record.
     * @return A SourceRecord containing the scraped data, including the title
     * and textual content of the web page. If an error occurs during scraping,
     * a default SourceRecord with minimal values is returned.
     * @throws IOException If a connection error occurs while accessing the seed
     * URL.
     */
    public SourceRecord scrapper(String seedURL, Integer fid) {
        try {
            System.out.println("ENTERED SCRAPPER");

            // 1- navigate to the seedURL and open a connection
            Document doc = Jsoup.connect(seedURL).get();

            // 2- start scrapping the textual data, and for the first scarpe round make a queue for the links
            Element content = doc.getElementById("bodyContent");
            Elements title = doc.getElementsByTag("h1"); // problem

            // ===================================================================//
            // Element title = doc.getElementById("firstHeading"); // problem
            // String textualContent = title.text();   // problem
            // ===================================================================//
            String textualContent = title.get(0).text();
            String textualTitle = content.text();
            int ln = textualTitle.length();

            // 3- make return a sourceRecord format for each scrape round, set norm to zero for now
            SourceRecord record = new SourceRecord(fid, seedURL, textualTitle, ln, 0.0, textualContent);

            return record;

        } catch (IOException e) {
            System.out.println("Scrapper(): Connection Error at Seed: " + seedURL);
        }

        return new SourceRecord(Integer.MIN_VALUE, "", "", "");
    }

    /**
     * Crawls web pages starting from two seed URLs and collects a limited
     * number of documents.
     *
     * @param seed1 The first seed URL to start crawling from.
     * @param seed2 The second seed URL to start crawling from.
     * @return A LinkedList of SourceRecord objects containing the scraped data
     * from the crawled URLs. The crawling process is limited to a total of 10
     * URLs plus the two seed URLs.
     */
    public LinkedList<SourceRecord> crawl(String seed1, String seed2) {

        // files ID counter
        Integer fid = 0;

        // Crawling counter limited to 10 URLs
        Integer cc = 1;    // 10 links + 2 for seeds

        // 1- get all the potential URLs to be scarped
        LinkedList<String> validUrls = urlScrapper(seed1, seed2);
        LinkedList<SourceRecord> records = new LinkedList<>();

        for (String url : validUrls) {

            // if crawl limit is reached break 
            if (fid.equals(cc)) {
                break;
            }

            // add the scrapped doc into the source records list
            records.add(scrapper(url, fid));    // need to check if the returned record is empty
            fid++;

        }

        return records;
        // return new LinkedList<>();
    }

}
