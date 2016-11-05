/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mri1;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/**
 *
 * @author luigi
 */



public class MRI1 {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     * @throws org.apache.lucene.queryparser.classic.ParseException
     */
    public static void main(String[] args) throws IOException, ParseException 
    {
        
            File dest = new File("dest");
            File source = new File("source");
            FSDirectory fsdir = FSDirectory.open(dest);
            IndexWriterConfig iwc=new IndexWriterConfig(Version.LATEST, new StandardAnalyzer());
            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            try (IndexWriter writer = new IndexWriter(fsdir, iwc))
            {
                File[] listFiles = source.listFiles();
                for (File file:listFiles) 
                {
                    System.out.println(file.getName());
                    if (file.isFile() && file.getName().endsWith(".txt")) 
                    {
                        Document doc=new Document();
                        doc.add(new StringField("path", file.getAbsolutePath(), Field.Store.YES));
                        doc.add(new TextField("content", new FileReader(file)));
                        writer.addDocument(doc);
                    }
                }
            }
            
            DirectoryReader idxReader = DirectoryReader.open(fsdir);
            IndexSearcher searcher = new IndexSearcher(idxReader);
            //Query q=new TermQuery(new Term("content","system"));
            QueryParser parser=new QueryParser("content", new StandardAnalyzer());
            Query q=parser.parse("basic");
            TopDocs topDocs = searcher.search(q, 20);
            ScoreDoc[] sd=topDocs.scoreDocs;
            for (ScoreDoc d:sd) {
                System.out.println(searcher.doc(d.doc).get("path")+"\t"+d.score);
            }
        
    }
    
}
