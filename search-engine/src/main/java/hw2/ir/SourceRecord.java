package hw2.ir;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author ehab
 */
public class SourceRecord {

    /**
     * The unique file identifier for the source document.
     */
    public int fid;

    /**
     * The URL of the source document.
     */
    public String URL;

    /**
     * The title of the source document.
     */
    public String title;

    /**
     * The textual content of the source document.
     */
    public String text;

    /**
     * The normalization factor for the source document, typically used in
     * ranking algorithms. Rakez fe el 7war da ya wagdy enta w Goerge!!
     */
    public Double norm;

    /**
     * The length of the source document, often used in text processing.
     */
    public int length;

    /**
     * Retrieves the URL of the source document.
     *
     * @return The URL of the source document.
     */
    public String getURL() {
        return URL;
    }

    /**
     * Constructs a SourceRecord with all fields initialized.
     *
     * @param f The unique file identifier.
     * @param u The URL of the source document.
     * @param tt The title of the source document.
     * @param ln The length of the source document.
     * @param n The normalization factor for the source document.
     * @param tx The textual content of the source document.
     */
    public SourceRecord(int f, String u, String tt, int ln, Double n, String tx) {
        fid = f;
        URL = u;
        title = tt;
        text = tx;
        norm = n;
        length = ln;
    }

    /**
     * Constructs a SourceRecord with only the essential fields initialized. The
     * normalization factor and length are set to default values (0.0 and 0).
     *
     * @param f The unique file identifier.
     * @param u The URL of the source document.
     * @param tt The title of the source document.
     * @param tx The textual content of the source document.
     */
    public SourceRecord(int f, String u, String tt, String tx) {
        fid = f;
        URL = u;
        title = tt;
        text = tx;
        norm = 0.0;
        length = 0;
    }
}
