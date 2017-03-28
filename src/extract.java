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

public class extract {
	public static void main(String args[]) {
		FileInputStream fis = null;
		FileInputStream fis2 = null;
		BufferedReader reader = null;
		BufferedReader reader2 = null;
		BufferedWriter out = null;
		BufferedWriter outPreffered = null;
		BufferedWriter outSem = null;
		BufferedWriter outSentence = null;
		BufferedWriter outPOS = null;
		Matcher matcher, matcherValue, matcherMM, matcher3;
		int  sentNum;
		try {
			fis = new FileInputStream("ACOG.xml");
			fis2 = new FileInputStream("ACOG.xml");
			reader = new BufferedReader(new InputStreamReader(fis));
			reader2 = new BufferedReader(new InputStreamReader(fis2));
			outPreffered = new BufferedWriter(new FileWriter("ACOG_PreferredName.txt"));
			outSem = new BufferedWriter(new FileWriter("ACOG_Semantic.txt"));
			outSentence = new BufferedWriter(new FileWriter("ACOG_Sentence.txt"));
			outPOS = new BufferedWriter(new FileWriter("ACOG_POS.txt"));
			out = new BufferedWriter(new FileWriter("Pre_ACOG.txt"));
			// System.out.println("Reading File line by line using BufferedReader");
			String line = reader.readLine();
			String line2;
			line2 = reader2.readLine();
			reader2.mark(1000000000);
			String startSen = "";
			String EndSen = "";
			String conditiont = "";
			String attributet = "";
			String value = "";
			String valuet = "";
			sentNum=0;
			Pattern patternSen = Pattern
					.compile("<Annotation Id=\"(.*?)\" Type=\"Sentence\" StartNode=\"(.*?)\" EndNode=\"(.*?)\">");
			Pattern patternMm = Pattern
					.compile("<Annotation Id=\"(.*?)\" Type=\"MetaMap\" StartNode=\"(.*?)\" EndNode=\"(.*?)\">");
			Pattern patternPOS = Pattern
					.compile("<Annotation Id=\"(.*?)\" Type=\"Token\" StartNode=\"(.*?)\" EndNode=\"(.*?)\">");
			Pattern patternNode = Pattern.compile("Node id=\"(.*?)\"/>(.*?)<");
			Pattern patternValue = Pattern
					.compile("<Value className=\"java.lang.String\">(.*?)</Value>");
			// Pattern patternPreferred =
			// Pattern.compile("<Value className=\"java.lang.String\">(.*?)</Value>");
			while (line != null) {
				
				
				matcher = patternSen.matcher(line);

				if (matcher.find()) {
					sentNum++;
					out.write("[sentNum: "+sentNum +"]");
					outPreffered.write("[sentNum: "+sentNum +"]");
					outSem.write("[sentNum: "+sentNum +"]");
					outSentence.write("[sentNum: "+sentNum +"]");
					outPOS.write("[sentNum: "+sentNum +"]");
					startSen = matcher.group(2);
					EndSen = matcher.group(3);
					out.write(line);

					line2 = reader2.readLine();

					while (line2 != null) {
						matcher = patternNode.matcher(line2);
						while (matcher.find()) {
							// sout.write(matcher.group(1)+ " ");
							if (Integer.parseInt(matcher.group(1)) >= Integer
									.parseInt(startSen)
									&& Integer.parseInt(matcher.group(1)) <= Integer
											.parseInt(EndSen)) {
								out.write(matcher.group(2));
								outSentence.write(matcher.group(2));
							}
						}
						matcher = patternMm.matcher(line2);
						if (matcher.find())
							if(Integer.parseInt(matcher.group(2)) >= Integer.parseInt(startSen)&& Integer.parseInt(matcher.group(2)) < Integer.parseInt(EndSen) ) {
							while (!line2.contains("</Annotation>")) {
								if (line2.contains("<Name className=\"java.lang.String\">PreferredName</Name>")) {
									line2 = reader2.readLine();
									matcherValue = patternValue.matcher(line2);
									if (matcherValue.find()) {
										outPreffered.write(matcherValue.group(1) + " // ");
										out.write("PreferredName:" + matcherValue.group(1)+" ");
									}
								}
								if (line2.contains("<Name className=\"java.lang.String\">SemanticTypesString</Name>")) {
									line2 = reader2.readLine();
									matcherValue = patternValue.matcher(line2);
									if (matcherValue.find()) {
										outSem.write(matcherValue.group(1) + " ");
										out.write("SemanticType:" + matcherValue.group(1)+" ");
									}
								}
								line2 = reader2.readLine();
							}
						}
						matcher = patternPOS.matcher(line2);
						if (matcher.find())
							if(Integer.parseInt(matcher.group(2)) >= Integer.parseInt(startSen)&& Integer.parseInt(matcher.group(2)) < Integer.parseInt(EndSen) ) {
							while (!line2.contains("</Annotation>")) {
								if (line2.contains("<Name className=\"java.lang.String\">category</Name>")) {
									line2 = reader2.readLine();
									matcherValue = patternValue.matcher(line2);
									if (matcherValue.find()) {
										outPOS.write(matcherValue.group(1) + " // ");
										out.write("POS:" + matcherValue.group(1)+" ");
									}
								}
								if (line2.contains("<Name className=\"java.lang.String\">SemanticTypesString</Name>")) {
									line2 = reader2.readLine();
									matcherValue = patternValue.matcher(line2);
									if (matcherValue.find()) {
										outSem.write(matcherValue.group(1) + " ");
										out.write("SemanticType : " + matcherValue.group(1)+" ");
									}
								}
								line2 = reader2.readLine();
							}
						}

						line2 = reader2.readLine();
					}
					out.newLine();
					outPreffered.newLine();
					outSem.newLine();
					outSentence.newLine();
					outPOS.newLine();
					reader2.reset();
				}

				line = reader.readLine();
			}

			out.close();
			outPreffered.close();
			outSem.close();
			outSentence.close();
			outPOS.close();
		} catch (FileNotFoundException ex) {
			Logger.getLogger(BufferedReader.class.getName()).log(Level.SEVERE,
					null, ex);
		} catch (IOException ex) {
			Logger.getLogger(BufferedReader.class.getName()).log(Level.SEVERE,
					null, ex);
		} finally {
			try {
				reader.close();
				reader2.close();
				fis.close();
				fis2.close();
			} catch (IOException ex) {
				Logger.getLogger(BufferedReader.class.getName()).log(
						Level.SEVERE, null, ex);
			}
		}
	}
}
