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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.queryparser.classic.ParseException;


public class CranSearcher
{

    private static final String[] fields = new String[]{"title", "abst"};

    private static void writeResults(String queryId, List<SearchResult> results, Writer writer) throws IOException {
        int rank = 1;
        for (SearchResult r : results)
        {
            writer.append(queryId).append(" 0 ").append(r.getId()).append(" ").append(String.valueOf(rank)).append(" ").append(String.valueOf(r.getScore())).append(" exp_0\n");
            rank++;
        }
    }

    /**
     * index_dir query_file results_file
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try
        { 
            File index_dir = new File("/home/luigi/NetBeansProjects/LAB_mri/inv_index");
                File query_file = new File("/home/luigi/NetBeansProjects/LAB_mri/CRAN/cran.qry");
                File result_file = new File("/home/luigi/NetBeansProjects/LAB_mri/Result/trec_eval.8.1/pps_result.txt");
                SearchEngine se = new SearchEngine(index_dir);
                se.open();
                BufferedReader reader = new BufferedReader(new FileReader(query_file));
                BufferedWriter writer = new BufferedWriter(new FileWriter(result_file));
                String id = null;
                StringBuilder query = new StringBuilder();
                char code = ' ';
                int c = 1;
                while (reader.ready()) {
                    String line = reader.readLine();
                    if (line.startsWith(".I")) {
                        if (id != null) 
                        {
                             List<SearchResult> search = null;
                            //search
                            for(int i= 0; i< 5;i++)
                            {
                               search = se.search(query.toString(), fields, 1000);
                            }
                            writeResults(id, search, writer);
                            query = new StringBuilder();
                                                
                            System.out.println(" query : " + c);
                            c++;
                        }
                        id=String.valueOf(c);
                    } else if (line.startsWith(".W")) {
                        code = 'W';
                    } else {
                        switch (code) {
                            case 'W':
                                query.append(line).append(" ");
                                break;
                            default:
                                break;
                        }
                    }
                }
                reader.close();
                //store last documents
                if (id != null) 
                {
                             List<SearchResult> search = null;
                            //search
                            for(int i= 0; i< 5;i++)
                            {
                               search = se.search(query.toString(), fields, 1000);
                            }
                       writeResults(id, search, writer);
                }
                System.out.println("Total queries: " + c);
                se.close();
                writer.close();

        } catch (IOException ioex) {
            Logger.getLogger(CranIndexer.class.getName()).log(Level.SEVERE, null, ioex);
        } catch (ParseException ex) {
            Logger.getLogger(CranSearcher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(CranSearcher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
