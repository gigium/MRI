/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab_mri;

/**
 *
 * @author luigi
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;


public class CranIndexer {

    /**
     * doc_file index_dir
     * 
     * 
     * @param args
     */
    public static void main(String args []) {
        File index_dir = new File("/home/luigi/NetBeansProjects/LAB_mri/inv_index");
        String doc_file = "/home/luigi/NetBeansProjects/LAB_mri/CRAN/cran.all.1400";
        try {
                SearchEngine se = new SearchEngine(index_dir);
                se.open();
                File inputFile = new File(doc_file);
                BufferedReader reader = new BufferedReader(new FileReader(inputFile));
               
                FieldType ft = new FieldType();
                ft.stored();
                ft.setIndexed(true); //done as default
                ft.setStoreTermVectors(true);
                ft.setStoreTermVectorPositions(true);
                ft.setStoreTermVectorOffsets(true);
                ft.setStoreTermVectorPayloads(true); 
                
                String id = null;
                StringBuilder title = new StringBuilder();
                StringBuilder authors = new StringBuilder();
                StringBuilder affiliation = new StringBuilder();
                StringBuilder abst = new StringBuilder();
                char code = ' ';
                int c = 0;
                while (reader.ready()) {
                    String line = reader.readLine();
                    if (line.startsWith(".I")) {
                        if (id != null) {
                            System.out.println(id + "\t" + abst);

                            Document doc = new Document();
                            doc.add(new StringField("id", id, Field.Store.YES));
                            doc.add(new TextField("title", title.toString(), Field.Store.NO));
                            doc.add(new TextField("authors", authors.toString(), Field.Store.NO));
                            doc.add(new TextField("affiliation", affiliation.toString(), Field.Store.NO));
                            doc.add(new Field("abst", abst.toString(), ft));
                            se.addDocument(doc);

                            c++;
                            title = new StringBuilder();
                            authors = new StringBuilder();
                            affiliation = new StringBuilder();
                            abst = new StringBuilder();
                        }
                        id = line.substring(2).trim();
                        
                    } else if (line.startsWith(".T")) {
                        code = 'T';
                    } else if (line.startsWith(".A")) {
                        code = 'A';
                    } else if (line.startsWith(".B")) {
                        code = 'B';
                    } else if (line.startsWith(".W")) {
                        code = 'W';
                    } else {
                        switch (code) {
                            case 'T':
                                title.append(line).append(" ");
                                break;
                            case 'A':
                                authors.append(line).append(" ");
                                break;
                            case 'B':
                                affiliation.append(line).append(" ");
                                break;
                            case 'W':
                                abst.append(line).append(" ");
                                break;
                            default:
                                break;
                        }
                    }
                }
                reader.close();
                //store last documents
                if (id != null) {
                    System.out.println(id + "\t" + title);
                    //store document
                    //put index code here
                    Document doc = new Document();
                    doc.add(new StringField("id", id, Field.Store.YES));
                    doc.add(new TextField("title", title.toString(), Field.Store.NO));
                    doc.add(new TextField("authors", authors.toString(), Field.Store.NO));
                    doc.add(new TextField("affiliation", affiliation.toString(), Field.Store.NO));
                    doc.add(new TextField("abst", abst.toString(), Field.Store.NO));
                    se.addDocument(doc);
                    c++;
                }
                System.out.println("Total docs: " + c);
 
                se.close();
        } catch (IOException ioex) {
            Logger.getLogger(CranIndexer.class.getName()).log(Level.SEVERE, null, ioex);
        }

    }
    


}
