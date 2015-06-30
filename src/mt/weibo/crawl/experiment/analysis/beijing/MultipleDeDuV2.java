package mt.weibo.crawl.experiment.analysis.beijing;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import mt.weibo.common.MyFolderLineReader;
import mt.weibo.common.MyLineWriter;

public class MultipleDeDuV2 {

	private String outputFileName;
	private File folder;
	private Set<String> uniqueSet;
	private int count;
	private int itemcount;
	private int fieldPosition;
	
	public MultipleDeDuV2(File folder, String outputFilename, int fieldPosition) {
		this.outputFileName = outputFilename;
		this.folder = folder;
		this.uniqueSet = new HashSet<String>();
		this.fieldPosition = fieldPosition;
	}

	public static void main(String[] args) {
//		File folder = new File("/Users/vincentgong/Documents/TUD/Master TUD/A Master Thesis/share/servers/ams/back/2/csv/");
		File folder = new File(args[0]);//folder path
		String outputFilename = args[1];//path/file.txt
		int deduFieldPosition = 0;
		if(args.length >= 3){
			if(args[2]!=null && !args[2].equals("")){
				deduFieldPosition = Integer.valueOf(args[2]);
			}
		}
		MultipleDeDuV2 mdd = new MultipleDeDuV2(folder, outputFilename, deduFieldPosition);
		mdd.process();
	}

	private void process() {
		// TODO Auto-generated method stub
		try {
			MyFolderLineReader mflr = new MyFolderLineReader(this.folder);
			while(mflr.hasNextLine()){
				String line = mflr.nextLine();
				//for post, item = {uid, username, gender, lat, long, data/time, postid}
				String id = line.split(",")[this.fieldPosition];
				if(!this.uniqueSet.contains(id)){
					this.uniqueSet.add(id);
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
