/**
 * 
 */
package mt.weibo.crawl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import mt.weibo.common.MyLineWriter;
import mt.weibo.common.Toolbox;
import mt.weibo.common.Utils;

/**
 * @author Vinent GONG
 *
 */
public class UserStatusPureJson implements Runnable {

	private int sleepTimeMinutes = 120;
	private int rounds = 0;
	private int totalRecords = 0;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Thread UserStatusPureJson = new Thread(new UserStatusPureJson());
		UserStatusPureJson.start();
	}

	@Override
	public void run() {

		// only if first sleep is needed
		// Sleep(sleepTimeMinutes * 60);

		String uid = "2116842364";
		// crawl the data
		crawlAndStoreData(uid);
	}

	public void Sleep(int seconds) {
		// TODO Auto-generated method stub
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

	private void crawlAndStoreData(String uid) {

		String count = "50"; // 0-50 posts per page
		int page = 20;
		String sort = "0"; // 0: default, time; 1: distance
		int sleepTime = 10; // the sleep time for each page, that is a random
							// value, default is 10

		String filename = "user_timeline_"+ uid + "_"
				+ MyLineWriter.getInstance().DateAsString4FileName() + ".json";
		
		
		//calculate the page amount
		int calPage = tryToCorrectPage(uid);
		if(calPage>0 && calPage<20) page = calPage;
		
		for (int p = 1; p < page; p++) {
			try {
				sleepTime = Utils.randomInt(1, 4);
				System.out.println("Now take a nap for " + sleepTime
						+ " seconds.");
				Thread.sleep(sleepTime * 1000);

				Map map = new HashMap();
				map.put("uid", uid);
				map.put("count", count);
				map.put("page", String.valueOf(p));

				String result = Toolbox.crawlData("place/user_timeline.json",
						map);
				
				Toolbox.saveDataToFile(result, Utils.STORE_FOLDER + Utils.UESER_TIMELINE_FOLDER + filename);
				System.out.println("Data has been stored to file. " + filename);
				totalRecords++;
				System.out.println("page: " + p
						+ ", Total Records: " + totalRecords);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private int tryToCorrectPage(String uid) {
		// TODO Auto-generated method stub
		Map map = new HashMap();
		map.put("uid", uid);
		String result = Toolbox.crawlData("place/user_timeline.json",
				map);
		
		return 0;
	}
}
