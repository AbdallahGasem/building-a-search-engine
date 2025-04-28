package hw2.ir;

import java.io.IOException;
import java.util.LinkedList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Crawler {

    public void main(String[] args) {
        System.out.println("\n===================================Program Starting=================================\n");

        // urlScrapper("https://en.wikipedia.org/wiki/List_of_pharaohs", "https://en.wikipedia.org/wiki/Pharaoh");
        System.out.println("\n====================================Program Ending=================================\n");
    }

    public boolean filter(String URL) {
        // match the URL to assure it starts with "https://en.wikipedia.org/wiki/"
        return URL.matches("^https://en\\.wikipedia\\.org/wiki/.*$");
    }

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
            e.printStackTrace();
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
            e.printStackTrace();
        }

        return validUrls;
    }

    public SourceRecord scrapper(String seedURL, Integer fid) {
        try {

            // 1- navigate to the seedURL and open a connection
            Document doc = Jsoup.connect(seedURL).get();

            // 2- start scrapping the textual data, and for the first scarpe round make a queue for the links
            Element content = doc.getElementById("bodyContent");
            Element title = doc.getElementById("firstHeading");

            String textualContent = title.text();
            String textualTitle = content.text();

            // 3- make return a sourceRecord format for each scrape round
            SourceRecord record = new SourceRecord(fid, seedURL, textualTitle, textualContent);

            return record;

        } catch (IOException e) {
            System.out.println("Scrapper(): Connection Error at Seed: " + seedURL);
            e.printStackTrace();
        }

        return new SourceRecord(Integer.MIN_VALUE, "", "", "");
    }

    public LinkedList<SourceRecord> crawl(String seed1, String seed2) {
        
        // files ID counter
        Integer fid = 0;
        
        // Crawling counter limited to 10 URLs
        Integer cc = 10;
        
        // 1- get all the potential URLs to be scarped
        LinkedList<String> validUrls = urlScrapper(seed1, seed2);
        LinkedList<SourceRecord> records = new LinkedList<>();

        for (String url : validUrls) {
            
            // if crawl limit is reached break 
            if (fid.equals(cc)) {
                break;
            }

            // add the scrapped doc into the source records list
            records.add(scrapper(url, fid));
            fid++;

        }

        return records;

    }

}
