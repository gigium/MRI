/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab_mri;
import java.io.Reader;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.standard.ClassicTokenizer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.standard.ClassicFilter;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.wikipedia.WikipediaTokenizer;

public class CustomAnalyzer extends Analyzer{

    @Override
    protected TokenStreamComponents createComponents(String string, Reader reader) 
    {
            CharArraySet stopWords = EnglishAnalyzer.getDefaultStopSet();
            Tokenizer tokenizer = new WikipediaTokenizer(reader);
            TokenStream filter = new ClassicFilter(tokenizer);
            filter = new StandardFilter(filter);
            filter = new StopFilter( filter, stopWords);
            filter = new PorterStemFilter(filter);

            filter = new LowerCaseFilter(filter);
            

            return new TokenStreamComponents(tokenizer, filter);
    }
    
    
}
