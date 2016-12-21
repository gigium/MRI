/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab_mri;

/**
 * @author luigi
 */


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexReader;
import java.util.AbstractMap.SimpleEntry;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;


public class RocchioExpander  {

	private final float alpha;
	private final float beta;
	private final float gama;
	private final int docsLimit;

	private Analyzer analyzer;
	private final String field;

	public RocchioExpander(Analyzer analyzer, final String field,
			       float alpha, float beta, float gama,
			       int docsLimit)
        {
		this.analyzer = analyzer;
		this.field = field;
		this.alpha = alpha;
		this.beta = beta;
		this.gama = gama;
		this.docsLimit = docsLimit;

	}

 
	public String expand(final String original, final List<SearchResult> relevantDocs)
		throws ParseException, CorruptIndexException,
		       LockObtainFailedException, IOException {
            /*		Directory index = createIndex(relevantDocs);*/		// newQVector = alpha * oldQVector + beta * Sum{i=1..docs}( DocsVector )
		List<Entry<String, Float>> newQVector = getTermScoreList(relevantDocs);
		for (String term : Arrays.asList(original.split("\\s+"))) 
                {
                    float score = alpha * getScore(relevantDocs, term);
                    boolean found = false;
                    for (Entry<String, Float> entry : newQVector) 
                    {
                        if (entry.getKey().equalsIgnoreCase(term)) 
                        {
                            entry.setValue(entry.getValue() + score);
                            found = true;
                            break;
			}
                    }
                    if (!found)
                    {
                        newQVector.add(new SimpleEntry<>(term, score));
                    }
		}
		Collections.sort(newQVector, new ScoreComparator<String>());
		Collections.reverse(newQVector);
		StringBuilder rocchioTerms = new StringBuilder();
		for (int limit = 0; limit < 5; limit++) 
                {
                   rocchioTerms.append(' ').append(newQVector.get(limit).getKey());
		}
                
                
		String rocchioQuery = rocchioTerms.toString();
		System.out.println("old q  :  "+original +"\nnew q : "+rocchioQuery);
                return rocchioQuery;
	}
        
        
        /*
        private Directory createIndex(List<SearchResult> relevantDocs)
        throws CorruptIndexException, LockObtainFailedException, IOException {
        Directory index = new RAMDirectory();
        IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        IndexWriter idxWriter = new IndexWriter(index, config);
        try (IndexReader idxreader = DirectoryReader.open(FSDirectory.open(new File("/home/luigi/NetBeansProjects/LAB_mri/inv_index"))))
        {
        for (int limit = 0; limit < docsLimit && limit < relevantDocs.size(); limit++) {
        idxWriter.addDocument(idxreader.document(Integer.parseInt(relevantDocs.get(limit).getId())));
        }
        }
        idxWriter.close();
        return index;
        }*/
        
        

	private List<Entry<String, Float>> getTermScoreList(List<SearchResult>  results) 
                throws CorruptIndexException, IOException 
        {
		Map<String, Float> termScoreMap = new HashMap<>();
            try (IndexReader idxreader = IndexReader.open(FSDirectory.open(new File("/home/luigi/NetBeansProjects/LAB_mri/inv_index")))) {
                int docsnum = idxreader.numDocs();
  
            for(SearchResult res : results)
            {
               
  
                Terms termVector = idxreader.getTermVector(Integer.parseInt(res.getId())-1, field); // index starts from zero 

                TermsEnum itr = null;
                if(termVector != null)
                {
                    itr =  termVector.iterator(null);
                     BytesRef term = null;

                  while ((term = itr.next()) != null) 
                  {          
                      String termTxt = term.utf8ToString();                              
                      double tf = itr.totalTermFreq(); 
                      double df = idxreader.docFreq(new Term("abst", term) );
                      float idf = (float) Math.log(docsnum/df);
                      float tfidf = (float) (tf * idf);
                      termScoreMap.put(termTxt, beta * tfidf);

                  }
                }
               
            }
              return new ArrayList<Entry<String, Float>>(termScoreMap.entrySet());
            }
              
    }
        
        
    private float getScore (List<SearchResult>  results, String term) throws IOException
    {
        float tfidf = 0;
        try (IndexReader idxreader = IndexReader.open(FSDirectory.open(new File("/home/luigi/NetBeansProjects/LAB_mri/inv_index")))) 
        {
                   
                for(SearchResult res : results)
                {

                    Terms termVector = idxreader.getTermVector(Integer.parseInt(res.getId())-1, "abst");
                    int docsnum = idxreader.numDocs();
                    TermsEnum itr = null;
                    if(termVector != null)
                    {
                        itr =  termVector.iterator(null);

                        BytesRef this_term = null;

                        while ((this_term = itr.next()) != null) 
                        {          
                            String termTxt = this_term.utf8ToString();   
                            if (term.equalsIgnoreCase(termTxt)) 
                            {
                                double tf = itr.totalTermFreq(); 
                                double df = idxreader.docFreq(new Term("abst", term) );
                                float idf = (float) Math.log(docsnum/df);
                                tfidf = (float) (tf * idf);
                                return tfidf;
                            }
                        }
                    }

                }
            
	}
        return tfidf;

    }

    


}