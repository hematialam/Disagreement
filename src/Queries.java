import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.SortedSetDocValues;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
public class Queries {
	 public static void main(String[] args) throws IOException, ParseException {
	        // 0. Specify the analyzer for tokenizing text.
	        //    The same analyzer should be used for indexing and searching
	        StandardAnalyzer analyzer = new StandardAnalyzer();

	        // 1. create the index
	        Directory index_sent = new RAMDirectory();
	        IndexWriterConfig config_sent = new IndexWriterConfig(analyzer);
	        IndexWriter w_sent = new IndexWriter(index_sent, config_sent);
	        Directory index_type = new RAMDirectory();
	        IndexWriterConfig config_type = new IndexWriterConfig(analyzer);
	        IndexWriter w_type = new IndexWriter(index_type, config_type);
	        Directory index_name = new RAMDirectory();
	        IndexWriterConfig config_name = new IndexWriterConfig(analyzer);
	        IndexWriter w_name = new IndexWriter(index_name, config_name);
	        
	        
	        FileInputStream fis_sent1 = null;
	        FileInputStream fis_sent2 = null;
	        BufferedReader reader_sent1 = null;
	        BufferedReader reader_sent2 = null;
	        FileInputStream fis_type1 = null;
	        FileInputStream fis_type2 = null;
	        BufferedReader reader_type1 = null;
	        BufferedReader reader_type2 = null;
	        FileInputStream fis_name1 = null;
	        FileInputStream fis_name2 = null;
	        BufferedReader reader_name1 = null;
	        BufferedReader reader_name2 = null;
	        
	        
	        
	        
	        float merged_score = 0;
	        BufferedWriter out=null;
			out = new BufferedWriter(new FileWriter("Search-Output-ACOG.txt"));
			try {
				fis_sent1 = new FileInputStream("ACOG_sentence.txt");
				fis_sent2 = new FileInputStream("Queries_sentence.txt");
				reader_sent1 = new BufferedReader(new InputStreamReader(fis_sent1));
				reader_sent2 = new BufferedReader(new InputStreamReader(fis_sent2));
				fis_type1 = new FileInputStream("ACOG_semantic.txt");
				fis_type2 = new FileInputStream("Queries_semantic.txt");
				reader_type1 = new BufferedReader(new InputStreamReader(fis_type1));
				reader_type2 = new BufferedReader(new InputStreamReader(fis_type2));
				fis_name1 = new FileInputStream("ACOG_PreferredName.txt");
				fis_name2 = new FileInputStream("Queries_preferredName.txt");
				reader_name1 = new BufferedReader(new InputStreamReader(fis_name1));
				reader_name2 = new BufferedReader(new InputStreamReader(fis_name2));
				
				
				String line_index_sent;
				line_index_sent = reader_sent1.readLine();
				String line_index_type;
				line_index_type = reader_type1.readLine();
				String line_index_name;
				line_index_name = reader_name1.readLine();
				while (line_index_sent != null) {
				
					 addDoc(w_sent, line_index_sent, "");
					line_index_sent = reader_sent1.readLine();
					addDoc(w_type, line_index_type, "");
					line_index_type = reader_type1.readLine();
					addDoc(w_name, line_index_name, "");
					line_index_name = reader_name1.readLine();
				
				}
				
	        w_sent.close();
	        w_type.close();
	        w_name.close();
	        
	        
	        String line_query_sent;
	        line_query_sent=reader_sent2.readLine();
	        String line_query_type;
	        line_query_type=reader_type2.readLine();
	        String line_query_name;
	        line_query_name=reader_name2.readLine();
	        
	        while(line_query_sent!=null){
	        	String[] prefered_query = line_query_name.split("//");
	            String[] types_query = line_query_type.split(" ");
	            	
	        // 2. query
	        String querystr_sent = args.length > 0 ? args[0] : "Breast";
	        String querystr_type = args.length > 0 ? args[0] : "Breast";
	        String querystr_name = args.length > 0 ? args[0] : "Breast";

	        // the "title" arg specifies the default field to use
	        // when no field is explicitly specified in the query.
	        Query q_sent = new QueryParser("title", analyzer).parse(QueryParser.escape(line_query_sent));
	        Query q_type = new QueryParser("title", analyzer).parse(QueryParser.escape(line_query_type));
	        Query q_name = new QueryParser("title", analyzer).parse(QueryParser.escape(line_query_name));
	        // 3. search
	        int hitsPerPage = 50;
	        
	        IndexReader reader_sent = DirectoryReader.open(index_sent);
	        IndexSearcher searcher_sent = new IndexSearcher(reader_sent);
	        TopDocs docs_sent = searcher_sent.search(q_sent, hitsPerPage);
	        ScoreDoc[] hits_sent = docs_sent.scoreDocs;
	        
	        IndexReader reader_type = DirectoryReader.open(index_type);
	        IndexSearcher searcher_type = new IndexSearcher(reader_type);
	        TopDocs docs_type = searcher_type.search(q_type, hitsPerPage);
	        ScoreDoc[] hits_type = docs_type.scoreDocs;
	        
	        IndexReader reader_name = DirectoryReader.open(index_name);
	        IndexSearcher searcher_name = new IndexSearcher(reader_name);
	        TopDocs docs_name = searcher_name.search(q_name, hitsPerPage);
	        ScoreDoc[] hits_name = docs_name.scoreDocs;
	        int doc_number[]=new int[hitsPerPage];
	        float doc_score[]=new float[hitsPerPage];
	        
	        
	        for(int i=0;i<hits_sent.length;++i) {
	        	merged_score= hits_sent[i].score/docs_sent.getMaxScore();
	        for (int j=0;j<hits_name.length;j++){
            	if (hits_name[j].doc==hits_sent[i].doc)
            	{
            		merged_score+= (hits_name[j].score/docs_name.getMaxScore());
            		//hits_name[j].score=(hits_name[j].score/docs_name.getMaxScore())+hits_sent[i].score/docs_sent.getMaxScore();
            	}
            }
	        doc_number[i]=hits_sent[i].doc;
	        doc_score[i]=merged_score;
	        }
	        
	        int temp_index=0;
	        float temp_score=0;
	        
	        for (int i = 0; i < hits_sent.length; i++)
	        {
	               for(int j = 1; j < (hits_sent.length-i); j++)
	               {
	                        if(doc_score[j-1] < doc_score[j])
	                        {
	                                    temp_index = doc_number [j - 1];
	                                    doc_number [j - 1]= doc_number [j];
	                                    doc_number [j] = temp_index;
	                                    temp_score = doc_score [j - 1];
	                                    doc_score [j - 1]= doc_score [j];
	                                    doc_score [j] = temp_score;
	                        }
	                }
	        }
	        
	        
	        
	        
	        
	        // 4. display results
	        out.write("Found 10 " + " hits for: "+line_query_sent);
	        out.newLine();
//	        out.write("Found " + hits_sent.length + " hits for: "+line_query_name);
//	        out.newLine();
//	        out.write("Found " + hits_sent.length + " hits for: "+line_query_type);
//	        out.newLine();
	        for(int i=0;i<hits_sent.length;++i) {
	            int docId = hits_sent[i].doc;
	            Document d = searcher_sent.doc(docId);
	            //out.write((i + 1)+ ". " +" "+(hits_sent[i].score/docs_sent.getMaxScore())+ "\t" + d.get("title"));
	            //out.newLine();
	        }
	        
//	        for(int i=0;i<hits_name.length;++i) {
//	            int docId = hits_name[i].doc;
//	            Document d = searcher_name.doc(docId);
//	            out.write((i + 1)+ ". " +" "+hits_name[i].score + "\t" + d.get("title"));
//	            out.newLine();
//	        }
//	        for(int i=0;i<hits_type.length;++i) {
//	            int docId = hits_type[i].doc;
//	            Document d = searcher_type.doc(docId);
//	            out.write((i + 1)+ ". "  +" "+hits_type[i].score+ "\t" + d.get("title"));
//	            out.newLine();
//	        }
	        for(int i=0;i<20;++i) {
	            int docId = doc_number[i];
	            Document d = searcher_sent.doc(docId);
	            String[] prefered = searcher_name.doc(docId).get("title").split("//");
	            String[] types = searcher_type.doc(docId).get("title").split(" ");
	            
	            for (int j = 0; j < (prefered.length-1); j++)
		        {
		               for(int k = 0; k < (prefered_query.length-1); k++)
		               {
		            	   if ( types_query[k].contains("[diap]")||types_query[k].contains("[topp]")||types_query[k].contains("[hlca]")||types_query[k].contains("[lbpr]"))
		            	   {
		            		   
		            		   if (types_query[k].contains(types[j])||types[j].contains(types_query[k]))
		            		   if(prefered_query[k].trim().toLowerCase().contains(prefered[j].trim().toLowerCase())||prefered[j].trim().toLowerCase().contains(prefered_query[k].trim().toLowerCase()))
		            		   {
		            			out.write((i + 1)+ ". " +" "+doc_score[i]+ "\t" + d.get("title")+prefered_query[k]+types_query[k]);
		           	            out.newLine();
		            		   }
		            	   }
		               }
		        }
	            
	        }
	        // reader can only be closed when there
	        // is no need to access the documents any more.
	        reader_sent.close();
	        reader_type.close();
	        reader_name.close();
	        
	        line_query_sent=reader_sent2.readLine();
	        line_query_type=reader_type2.readLine();
	        line_query_name=reader_name2.readLine();
	        }
			out.close();
			}
			catch (FileNotFoundException ex) {
				 Logger.getLogger(BufferedReader.class.getName()).log(Level.SEVERE, null, ex);
				 } catch (IOException ex) {
				 Logger.getLogger(BufferedReader.class.getName()).log(Level.SEVERE, null, ex);
				 } finally {
				 try {
				 reader_sent1.close();
				 reader_sent2.close();
				 fis_sent1.close();
				 fis_sent2.close();

				 reader_name1.close();
				 reader_name2.close();
				 fis_name1.close();
				 fis_name2.close();

				 reader_type1.close();
				 reader_type2.close();
				 fis_type1.close();
				 fis_type2.close();
				 } catch (IOException ex) {
				 Logger.getLogger(BufferedReader.class.getName()).log(Level.SEVERE, null, ex);
				 }
				 }
			}

	    private static void addDoc(IndexWriter w, String title, String isbn) throws IOException {
	        Document doc = new Document();
	        doc.add(new TextField("title", title, Field.Store.YES));

	        // use a string field for isbn because we don't want it tokenized
	        doc.add(new StringField("isbn", isbn, Field.Store.YES));
	        w.addDocument(doc);
	    }
}
