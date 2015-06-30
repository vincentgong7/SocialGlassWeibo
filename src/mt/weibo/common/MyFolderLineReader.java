/**
 * 
 */
package mt.weibo.common;

import java.io.File;
import java.io.FileInputStream;
import java.util.Scanner;

/**
 * @author vincentgong
 *
 */
public class MyFolderLineReader {

	private File folder;
	private File[] files;
	private Scanner[] scs;
	private Scanner sc;
	private String fileName;
	private int scID;

	public MyFolderLineReader(String folderName) throws Exception {
		this.folder = new File(folderName);
		init();
	}

	public MyFolderLineReader(File folder) throws Exception {
		this.folder = folder;
		init();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			MyFolderLineReader mflr = new MyFolderLineReader(
					"D:/documents/Dropbox/TUD/Master TUD/A Master Thesis/share/exp/Test");
			mflr.init();
			int i=0;
			while (mflr.hasNextLine()) {
				i++;
				String line = mflr.nextLine();
				System.out.println(i+ " " +line);
			}
			mflr.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void init() throws Exception {
		if (!folder.exists()) {
			throw new Exception("File not found." + folder.getName());
		}
		// this.sc = new Scanner(f);
		if (folder.isDirectory()) {
			files = folder.listFiles();
			this.scs = new Scanner[folder.listFiles().length];
			for (int i = 0; i < files.length; i++) {
				File f = files[i];
				this.scs[i] = new Scanner(new FileInputStream(f), "UTF-8");
			}
		}else{
			this.scs = new Scanner[1];
			this.scs[0] = new Scanner(folder);
			this.fileName = folder.getName();
		}
		this.scID = 0;
		this.fileName = this.files[scID].getName();
		this.sc = scs[scID];
	}

	/**
	 * @param
	 * @throws Exception
	 */
	public String nextLine(int startFromLineNumber) throws Exception {
		if (startFromLineNumber > 0) {
			for (int i = 0; i < startFromLineNumber; i++) {
				if (this.sc.hasNextLine()) {
					this.sc.nextLine();
				} else {
					throw new Exception("Requested lines number("
							+ startFromLineNumber
							+ ") exceeds lines in the file: " + i);
				}
			}
		}
		return sc.nextLine();
	}

	public String nextLine() throws Exception {
		return nextLine(0);
	}

	public boolean hasNextLine() {
		if (this.sc != null) {
			if (this.sc.hasNextLine()) {
				return true;
			} else {
				this.scID++;
				if (this.scID == scs.length) {
					return false;
				} else {
					this.sc = scs[this.scID];
					this.fileName = this.files[scID].getName();
					return this.sc.hasNextLine();
				}
			}
		}
		return false;
	}
	
	public void nextFile(){
		this.scID++;
		if (this.scID == scs.length) {
			this.sc = null;
		} else {
			this.sc = scs[this.scID];
			this.fileName = this.files[scID].getName();
		}
	}

	public String getFileName(){
		return this.fileName;
	}
	
	public void close() {
		for(Scanner tsc : scs){
			if(tsc!=null){
				tsc.close();
			}
		}
	}
}
