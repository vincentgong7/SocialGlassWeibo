package mt.weibo.crawl.experiment.analysis.ams;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import mt.weibo.common.MyFolderLineReader;
import mt.weibo.common.MyLineWriter;

public class MultipleDeDu {

	private String outputFileName;
	private File folder;
	private Set<String> postSet;
	private int count;
	private int itemcount;
	
	public MultipleDeDu(File folder, String outputFilename) {
		this.outputFileName = outputFilename;
		this.folder = folder;
		this.postSet = new HashSet<String>();
	}

	public static void main(String[] args) {
//		File folder = new File("/Users/vincentgong/Documents/TUD/Master TUD/A Master Thesis/share/servers/ams/back/2/csv/");
		File folder = new File(args[0]);//folder path
		String outputFilename = args[1];//path/file.txt
		MultipleDeDu mdd = new MultipleDeDu(folder, outputFilename);
		mdd.process();
	}

	private void process() {
		// TODO Auto-generated method stub
		try {
			MyFolderLineReader mflr = new MyFolderLineReader(this.folder);
			while(mflr.hasNextLine()){
				String line = mflr.nextLine();
				//item = {uid, username, gender, lat, long, data/time, postid}
				String postid = line.split(",")[6];
				if(!this.postSet.contains(postid)){
					this.postSet.add(postid);
					MyLineWriter.getInstance().writeLine(this.outputFileName, line);
					count++;
					System.out.println("Number: " + count + ". Content: " + line);
				}
			}
			System.out.println("File: "+ this.itemcount);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	

}
