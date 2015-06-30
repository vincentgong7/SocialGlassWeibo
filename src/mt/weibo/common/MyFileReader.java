/**
 * 
 */
package mt.weibo.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author vincentgong
 * 
 */
public class MyFileReader {

	public static void main(String[] args) throws Exception {
		MyFileReader mfr = new MyFileReader();
		String re = mfr.getFile("test.txt");
		System.out.println(re);
	}

	public String getFile(String path) throws Exception {

		MyLineReader mlr = new MyLineReader(path);
		StringBuilder sb = new StringBuilder();
		while (mlr.hasNextLine()) {
			String line = mlr.nextLine();
			sb.append(" ");
			sb.append(line.trim());
			sb.append(" ");
		}

		return sb.toString();
	}
	
	public static int getLineNumber(File f) throws IOException {
		// TODO Auto-generated method stub
		BufferedReader reader = new BufferedReader(new FileReader(f));
		int lines = 0;
		while (reader.readLine() != null) lines++;
		reader.close();
		return lines;
	}
	
	public static int getLineNumber(String path) throws IOException {
		File f = new File(path);
		return MyFileReader.getLineNumber(f);
	}

}
