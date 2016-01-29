package mt.weibo.crawl.general.dataprocess.userverifedtype;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import mt.weibo.common.MyLineReader;
import mt.weibo.common.MyLineWriter;
import mt.weibo.db.StatusDB;
import weibo4j.model.Status;
import weibo4j.model.User;

public class UserVerifiedTypeExtractor {

	private String inputFolderOrFileName;

	public static void main(String[] args) {
		UserVerifiedTypeExtractor upe = new UserVerifiedTypeExtractor();
		// upe.setInputFolderOrFile("D:/documents/Dropbox/TUD/Master TUD/A Master Thesis/share/IPX/2ndZhen/crawldata/mylog-workstation/mylog2015sep-Userpost/json/post-json-2015090817.txt");
		 upe.setInputFolderOrFile("/Users/vincentgong/Documents/TUD/Master TUD/A Master Thesis/share/workbench/saraExp/user_verified_type/kite2015");
		 
		// command: java -jar UserVerifiedTypeExtractor ./
		if(args.length >0 ) {
			upe.setInputFolderOrFile(args[0]);
			System.out.println("Foleder set: " + args[0]);
			System.out.println();
			System.out.println();
			System.out.println();
			System.out.println();
		}
		
		upe.process();
		upe.showReport();

	}

	private void process() {
		File folderOrFile = new File(inputFolderOrFileName);

		if (!folderOrFile.isDirectory()) {
			System.out.println("This is not a folder.");
			parseStatus(folderOrFile);
		} else {
			for (File f : folderOrFile.listFiles()) {
				parseStatus(f);
			}
		}
	}

	private void parseStatus(File f) {
		try {
			if(!f.getName().endsWith(".json") && !f.getName().endsWith(".txt")){
				return;
			}
			MyLineReader mlr = new MyLineReader(f);
			while (mlr.hasNextLine()) {
				String line = mlr.nextLine().trim();

				List<Status> statusList;
				try {
					statusList = StatusDB.getStatusList(line);
				} catch (Exception e) {
					System.out
							.println("Error in constructing status list for one line.");
					System.out.println(line);
					e.printStackTrace();
					continue;
				}
				
				String filepath = inputFolderOrFileName + "/user_type.csv";
				exportUserVerifiedTypeOnlyOnceFromStatusList(statusList, filepath);

			}
			mlr.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	private void setInputFolderOrFile(String inputFolderOrFileName) {
		this.inputFolderOrFileName = inputFolderOrFileName;
	}

	private void showReport() {
		// TODO Auto-generated method stub

	}
	
	public void exportUserVerifiedTypeOnlyOnceFromStatusList(List<Status> statusList, String filepath) {
		Iterator<Status> it = statusList.iterator();
		while (it.hasNext()) {
			Status st = it.next();
			if (!st.isDeleted()) {
				User user = st.getUser();
				String userID = user.getId();
				int userVerifiedType = user.getverifiedType();
				String line = userID + "," + userVerifiedType;
				try {
					MyLineWriter.getInstance().writeLine(filepath, line);
					System.out.println(line);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return;
			}
		}
	}
}
