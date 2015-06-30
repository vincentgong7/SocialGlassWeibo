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
public class PublicTimelinePureJson implements Runnable {

	private int sleepTimeMinutes = 5;
	private int rounds = 0;
	private int totalRecords = 0;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Thread PublicTimelinePureJson = new Thread(new PublicTimelinePureJson());
		PublicTimelinePureJson.start();
	}

	@Override
	public void run() {

		// only if first sleep is needed
		// Sleep(sleepTimeMinutes * 60);

		while (true) {
			System.out.println("[Current rounds: " + (rounds + 1) + " ]");
			// sleep for a while
			if (rounds != 0)
				Sleep(sleepTimeMinutes * 60); // sleep for 5 minutes
			rounds++;
			// crawl the data
			crawlAndStoreData();

		}
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

	private void crawlAndStoreData() {

		String count = "50"; // 0-50 posts per page
		int page = 5;
		String sort = "0"; // 0: default, time; 1: distance
		int sleepTime = 10;

		String filename = "public_timeline_" + MyLineWriter.getInstance().DateAsString4FileName() + ".json";
		
		for (int p = 1; p <= page; p++) {
			try {
				sleepTime = Utils.randomInt(100, 130);
				System.out.println("Now take a nap for " + sleepTime
						+ " seconds.");
				Thread.sleep(sleepTime * 1000);

				Map map = new HashMap();
				map.put("count", count);
//				map.put("page", String.valueOf(p));

				String result = Toolbox.crawlData(
						"statuses/public_timeline.json", map);
				Toolbox.saveDataToFile(result,
						Utils.PUBLIC_TIMELINE_FOLDER + filename);
				System.out.println("Data has been stored to file. " + filename);
				totalRecords++;
				System.out.println("Rounds: " + rounds + ", page: " + p
						+ ", Total Records: " + totalRecords);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
