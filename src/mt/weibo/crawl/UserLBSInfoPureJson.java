/**
 * 
 */
package mt.weibo.crawl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import mt.weibo.common.MyLineReader;
import mt.weibo.common.MyLineWriter;
import mt.weibo.common.Toolbox;
import mt.weibo.common.Utils;

/**
 * @author Vinent GONG
 *
 */
public class UserLBSInfoPureJson implements Runnable {

//	private int sleepTimeMinutes = 120;
//	private int rounds = 0;
//	private String uidFileName;
	private int totalRecords = 0;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Thread UserPlaceShow = new Thread(new UserLBSInfoPureJson());
		UserPlaceShow.start();
	}

	@Override
	public void run() {

		// only if first sleep is needed
		// Sleep(sleepTimeMinutes * 60);
		
		String uidFileName = "uid.txt";
		String uidFullPath = Utils.getResourceFilePath() + Utils.RESOURCE_FOLDER + Utils.UIDS_FOLDER + uidFileName;
		List<String> userList = getUserIDList(uidFullPath);
		
		Iterator<String> it = userList.iterator();
		while(it.hasNext()){
//			String uid = "2116842364";
			String uid = it.next();
			
			//sleep
			int sleepTime = 10; // the sleep time for each page, that is a random
			// value, default is 10
			sleepTime = Utils.randomInt(10,30);
			System.out.println("Now take a nap for " + sleepTime
					+ " seconds.");
			try {
				Thread.sleep(sleepTime * 1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// crawl the data
			crawlAndStoreData(uid);
		}
	}

	private List<String> getUserIDList(String uidFileName) {
		List<String> userList = new ArrayList<String>();
		// TODO Auto-generated method stub
		// 建议从数据库中读出所有uid，逐个uid写入到好几个文件中（例如每个文件包含1000个uid）
		// 然后在这里读取一个文件，将uid读入list，返回
		// userList.add("2116842364");
		
		try {
			MyLineReader mlr = new MyLineReader(uidFileName);
			mlr.init();
			while(mlr.hasNextLine()){
				String line = mlr.nextLine();
				userList.add(line);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return userList;
	}

	private void crawlAndStoreData(String uid) {
		String count = "50"; // 0-50 posts per page
		String sort = "0"; // 0: default, time; 1: distance

		String filename = "/lbs/user_placeshow_" + uid + "_"
				+ MyLineWriter.getInstance().DateAsString4FileName() + ".json";

		Map map = new HashMap();
		map.put("uid", uid);

		String result = Toolbox.crawlData("place/users/show.json", map);

		Toolbox.saveDataToFile(result, Utils.UIDS_FOLDER + filename);
		System.out.println("Data has been stored to file. " + filename);
		totalRecords++;
		System.out.println("uid: " + uid + ", Total Records: "
				+ totalRecords);
	}

	public void Sleep(int seconds) {
		int s = 10;
		if (seconds > 0)
			s = seconds;

		try {
			System.out.println();
			Date d = new Date();
			System.out.println(d.toString());
			System.out.println("Sleeping for " + s / 60
					+ " minutes...zzzzzzzzzz");
			System.out.println();
			Thread.sleep(s * 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
