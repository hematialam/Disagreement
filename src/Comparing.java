import java.io.BufferedReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.FileWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;

import java.util.Arrays;

public class Comparing {

	public static double tf(List<String> doc, String term) {
		double result = 0;
		for (String word : doc) {
			if (term.equalsIgnoreCase(word))
				result++;
		}
		// System.out.println(term + " " +result);
		return result / doc.size();
	}

	public static void main(String args[]) {
		// reading file line by line in Java using BufferedReader
		FileInputStream fis_pref1= null;
		FileInputStream fis_pref2 = null;
		FileInputStream fis_sem1= null;
		FileInputStream fis_sem2 = null;
		FileInputStream fis_sent1 = null;
		FileInputStream fis_sent2 = null;
		FileInputStream fis_doc1 = null;
		FileInputStream fis_doc2 = null;
		
		BufferedReader reader_pref1= null;
		BufferedReader reader_pref2 = null;
		BufferedReader reader_sem1= null;
		BufferedReader reader_sem2 = null;
		BufferedReader reader_sent1 = null;
		BufferedReader reader_sent2 = null;
		BufferedReader reader_doc1 = null;
		BufferedReader reader_doc2 = null;
		BufferedWriter out = null;
		
		int t = 0;
		int t2=0;
		double w1 = 0.0;
		double w2 = 0.0;
		int d=0;
		try {
			fis_pref1= new FileInputStream("PreferedName1.txt");
			fis_pref2 = new FileInputStream("PreferedName2.txt");
			fis_sem1= new FileInputStream("semantic1.txt");
			fis_sem2 = new FileInputStream("semantic2.txt");
			fis_sent1 = new FileInputStream("output1.txt");
			fis_sent2 = new FileInputStream("output2.txt");
			fis_doc1 = new FileInputStream("PreferedName1 - Copy.txt");
			fis_doc2 = new FileInputStream("PreferedName2 - Copy.txt");
			reader_pref1= new BufferedReader(new InputStreamReader(fis_pref1));
			reader_pref2 = new BufferedReader(new InputStreamReader(fis_pref2));
			reader_sem1= new BufferedReader(new InputStreamReader(fis_sem1));
			reader_sem2 = new BufferedReader(new InputStreamReader(fis_sem2));
			reader_sent1 = new BufferedReader(new InputStreamReader(fis_sent1));
			reader_sent2 = new BufferedReader(new InputStreamReader(fis_sent2));
			reader_doc1 = new BufferedReader(new InputStreamReader(fis_doc1));
			reader_doc2 = new BufferedReader(new InputStreamReader(fis_doc2));

			out = new BufferedWriter(new FileWriter("output.txt"));
			// System.out.println("Reading File line by line using BufferedReader");
			String line_pref1 = reader_pref1.readLine();
			String line_pref2;
			line_pref2 = reader_pref2.readLine();
			reader_pref2.mark(100000000);
			
			String line_sem1 = reader_sem1.readLine();
			String line_sem2;
			line_sem2 = reader_sem2.readLine();
			reader_sem2.mark(100000000);
			
			String line_sent1 = reader_sent1.readLine();
			String line_sent2;
			line_sent2 = reader_sent2.readLine();
			reader_sent2.mark(100000000);
			String line_doc1;
			line_doc1 = reader_doc1.readLine();
			String line_doc2;
			line_doc2 = reader_doc2.readLine();
			List<String> doc1 = Arrays.asList(line_doc1.split("\\s+"));
			List<String> doc2 = Arrays.asList(line_doc2.split("\\s+"));
			while (line_pref1 != null) {
				w1 = 0.0;
				w2 = 0.0;

				t = 0;
				t2=0;
				d=0;
				line_pref2 = reader_pref2.readLine();
				line_sent2 = reader_sent2.readLine();
				line_sem2 = reader_sem2.readLine();
				while (line_pref2 != null) {
					String[] s1 = line_pref1.split(" ");
					String[] s2 = line_pref2.split(" ");

					String[] sem1 = line_sem1.split(" ");
					String[] sem2 = line_sem2.split(" ");

					for ( int i= 0; i< s1.length ;i++ )
						for(int j= 0; j< s2.length ;j++)
						{
							if (s1[i].equals(s2[j])) {
								// out.write(s+"="+ss);
				
								w1 += tf(doc1, s1[i]);
								w2 += tf(doc2, s2[j]);
								t++;
							}
							if (sem1[i].equals(sem2[j])) {
				
								t2++;
								if (!s1[i].equals(s2[j]))
									d++;
							}
							
						}
					
//					for (String s : s1) {
//						for (String ss : s2) {
//							if (ss.equals(s)) {
//								// out.write(s+"="+ss);
//				
//								w1 += tf(doc1, s);
//								w2 += tf(doc2, s);
//								t++;
//							}
//						}
//					}
//					
//					for (String s : sem1) {
//						for (String ss : sem2) {
//							if (ss.equals(s)) {
//								// out.write(s+"="+ss);
//								t2++;
//							}
//						}
//					}
//					
					
					if ((t<5)&&(t > 2) && (t2>t*3)&&(t2<=t*6)&&(t2<23)&&(t2>10)&&(w1+w2<0.2)&&(w1+w2>0.09)) {
						out.write(line_sent1);
						out.newLine();
						out.write(line_sent2);
						out.newLine();
						out.write(w1 + " " + w2 + " " + (t - 1)+ " "+(t2-1)+ " "+ d );
						out.newLine();
					}
					t = 0;
					t2=0;
					w1 = 0.0;
					w2 = 0.0;
					d=0;
					line_pref2 = reader_pref2.readLine();
					line_sem2 = reader_sem2.readLine();
					line_sent2 = reader_sent2.readLine();
				}

				reader_pref2.reset();
				reader_sem2.reset();
				reader_sent2.reset();
				line_pref1 = reader_pref1.readLine();
				line_sem1 = reader_sem1.readLine();
				line_sent1 = reader_sent1.readLine();

			}

			out.close();
		} catch (FileNotFoundException ex) {
			Logger.getLogger(BufferedReader.class.getName()).log(Level.SEVERE,
					null, ex);
		} catch (IOException ex) {
			Logger.getLogger(BufferedReader.class.getName()).log(Level.SEVERE,
					null, ex);
		} finally {
			try {
				reader_pref1.close();
				reader_pref2.close();
				reader_sem1.close();
				reader_sem2.close();
				reader_sent1.close();
				reader_sent2.close();
				reader_doc1.close();
				reader_doc2.close();
				fis_sent1.close();
				fis_sent2.close();
				fis_pref1.close();
				fis_pref2.close();
				fis_sem1.close();
				fis_sem2.close();
				fis_doc1.close();
				fis_doc2.close();
			} catch (IOException ex) {
				Logger.getLogger(BufferedReader.class.getName()).log(
						Level.SEVERE, null, ex);
			}
		}

	}
}
