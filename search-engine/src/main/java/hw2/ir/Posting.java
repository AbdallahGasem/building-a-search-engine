/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hw2.ir;

/**
 *
 * @author ehab
 */
 
/**
 * The Posting class represents a node in an inverted index's posting list.
 * Each Posting object contains information about a document and its term frequency.
 * It also supports linking to the next Posting in the list.
 */
public class Posting {

    public Posting next = null;
    int docId;
    int dtf = 1;

    Posting(int id, int t) {
        docId = id;
        dtf=t;
    }
    
    Posting(int id) {
        docId = id;
    }
}