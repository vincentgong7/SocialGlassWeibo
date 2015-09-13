package mt.weibo.crawl.general.dataprocess;

import java.io.File;
import java.util.List;

import weibo4j.model.Status;
import weibo4j.model.WeiboException;
import mt.weibo.common.MyLineReader;
import mt.weibo.db.StatusDB;

public class UserGeoTimelineExtractor {

	private String inputFolderOrFileName;
	private String url;
	private String username;
	private String password;
	private String userTableName;
	private String postTableName;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		UserGeoTimelineExtractor upe = new UserGeoTimelineExtractor();
		upe.setInputFolderOrFile("/Users/vincentgong/Documents/TUD/Master TUD/A Master Thesis/share/IPX/2zhen/crawldata/mylog-workdesk/userpost-sep10/json/post-json-2015090817.txt");
		upe.setDB("jdbc:postgresql://localhost/microblog", "postgres", "admin",
				"socialmedia.user", "socialmedia.post");
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
			MyLineReader mlr = new MyLineReader(f);
			while (mlr.hasNextLine()) {
				String line = mlr.nextLine().trim();

				List<Status> statusList;
				try {
					statusList = StatusDB.getStatusList(line);
				} catch (WeiboException we) {
					System.out.println("Error in constructing status list for one line.");
					we.printStackTrace();
					continue;
				}
				// System.out.println(line);
				Status s = statusList.get(0);
				// System.out.println(s);

				// insert one-line-status into Status table
				StatusDB sdb = new StatusDB(this.url, this.username,
						this.password, this.userTableName, this.postTableName);
				sdb.insertStatusList(statusList); // insert the statuses into
													// table
				sdb.insertUserOnlyOnceFromStatusList(statusList); // insert the
																	// user of
																	// this line
																	// into user
																	// table,
																	// only one
																	// user is
																	// inserted
			}
			mlr.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setDB(String DBUrl, String dbusername, String dbpassword,
			String userDbTableName, String postDbTableName) {
		this.url = DBUrl;
		this.username = dbusername;
		this.password = dbpassword;
		this.userTableName = userDbTableName;
		this.postTableName = postDbTableName;
	}

	private void setInputFolderOrFile(String inputFolderOrFileName) {
		this.inputFolderOrFileName = inputFolderOrFileName;
	}

	private void showReport() {
		// TODO Auto-generated method stub

	}
}
