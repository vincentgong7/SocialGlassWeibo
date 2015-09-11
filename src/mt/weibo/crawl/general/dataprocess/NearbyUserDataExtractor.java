package mt.weibo.crawl.general.dataprocess;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import weibo4j.model.User;
import mt.weibo.common.MyLineReader;
import mt.weibo.common.MyLineWriter;
import mt.weibo.db.UserDB;

public class NearbyUserDataExtractor {

	private String inputFileName;
	private Map<String, User> userMap;
	private int totalUserAmount;
	private int uniqueUserAmount;
	private double uniqueRate;

	public static void main(String[] args) {
		NearbyUserDataExtractor nude = new NearbyUserDataExtractor();
		nude.setInputFilename("/Users/vincentgong/Documents/TUD/Master TUD/A Master Thesis/share/IPX/2zhen/crawldata/mylog-workdesk/nearbyuser-sep7/json/");
		nude.process();
		nude.outputIDFile("/Users/vincentgong/Documents/TUD/Master TUD/A Master Thesis/share/IPX/2zhen/crawldata/mylog-workdesk/nearbyuser-sep7/userid.txt");
		List<User> userList = nude.getUserList();
		Map<String, User> userMap = nude.getUserMap();
	}

	private void setInputFilename(String inputFileName) {
		this.inputFileName = inputFileName;
		userMap = new HashMap<String, User>();
	}

	private void process() {
		File f = new File(this.inputFileName);
		if (f.isDirectory()) {// f is a folder with json files
			File[] files = f.listFiles();
			for (File jsonFile : files) {
				System.out.println("processing file: " + jsonFile.getName());
				parseUser(jsonFile);
			}
		} else {// f is only a file with json
			System.out.println("processing file: " + f.getName());
			parseUser(f);
		}
		
		this.uniqueRate = Double.valueOf(this.uniqueUserAmount)
				/ Double.valueOf(this.totalUserAmount);
		System.out.println("finished, total user: " + this.totalUserAmount
				+ ", uniqueUserAmount: " + this.uniqueUserAmount
				+ ", unique rate: " + this.uniqueRate);

	}

	private void parseUser(File jsonFile) {
		try {
			MyLineReader mlr = new MyLineReader(jsonFile);
			while (mlr.hasNextLine()) {
				String jsonLine = mlr.nextLine();
				List<User> list;
				try {
					list = UserDB.getUserList(jsonLine);
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println(jsonLine);
					continue;
				}

				for (User u : list) {
					this.totalUserAmount++;
					this.userMap.put(u.getId(), u);
					// System.out.println(u.getId());
				}
			}
			mlr.close();
			this.uniqueUserAmount = this.userMap.size();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void outputIDFile(String outputFileName) {
		Iterator<Map.Entry<String, User>> it = userMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, User> pair = it.next();
			String uid = pair.getKey();
			try {
				MyLineWriter.getInstance().writeLine(outputFileName, uid);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
	}

	private Map<String, User> getUserMap() {
		return this.userMap;
	}

	private List<User> getUserList() {
		List<User> list = new ArrayList<User>(this.userMap.values());
		return list;
	}

}
