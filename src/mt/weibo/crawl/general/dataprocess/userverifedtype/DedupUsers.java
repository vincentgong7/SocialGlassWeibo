package mt.weibo.crawl.general.dataprocess.userverifedtype;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import mt.weibo.common.MyLineReader;
import mt.weibo.common.MyLineWriter;

public class DedupUsers {

	private String folder;
	private Set<String> userSet;
	public static void main(String[] args) {
		DedupUsers du = new DedupUsers();
		du.setFolder("/Users/vincentgong/Documents/TUD/Master TUD/A Master Thesis/share/workbench/saraExp/user_verified_type/result/origin/");
		if(args.length >0 ) {
			du.setFolder(args[0]);
			System.out.println("Folder set: " + args[0]);
			System.out.println();
			System.out.println();
			System.out.println();
			System.out.println();
		}
		
		du.process();
	}

	private void process() {
		// TODO Auto-generated method stub
		File fo = new File(folder);
		int i = 1;
		for (File f : fo.listFiles()) {
			if(!f.getName().endsWith("txt") && !f.getName().endsWith("csv")){
				continue;
			}
			try {
				MyLineReader mlr = new MyLineReader(f);
				while(mlr.hasNextLine()){
					String line = mlr.nextLine();
					String uid = line.split(",")[0];
					if(userSet.contains(uid)){
						System.out.println("Skip.");
					}else{
						MyLineWriter.getInstance().writeLine(folder + "/deduedUserType.csv", line);
						userSet.add(uid);
						System.out.println( i+ ": " + line);
						i++;
					}
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		System.out.println();
		System.out.println("done: " + i + "users.");
	}

	private void setFolder(String folder) {
		// TODO Auto-generated method stub
		this.folder = folder;
		userSet = new HashSet<String>();
	}

}
