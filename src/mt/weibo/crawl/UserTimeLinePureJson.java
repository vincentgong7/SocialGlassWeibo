/**
 * 
 */
package mt.weibo.crawl;

import java.util.HashMap;
import java.util.Map;

import mt.weibo.common.MyLineReader;
import mt.weibo.common.Toolbox;
import mt.weibo.common.Utils;

/**
 * @author Vinent GONG
 *
 */
public class UserTimeLinePureJson implements Runnable {

	private int sleepTimeMinutes = 3;
	private int rounds = 0;
	private int totalRecords = 0;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Thread UserTimeLinePureJson = new Thread(new UserTimeLinePureJson());
		UserTimeLinePureJson.start();
	}

	@Override
	public void run() {

		// only if first sleep is needed
		// Sleep(sleepTimeMinutes * 60);

		String userLBSinfoFile = Utils.getResourceFilePath()
				+ Utils.RESOURCE_FOLDER + Utils.UESER_TIMELINE_FOLDER
				+ "bj_user_id_amount.csv";
		MyLineReader mlr;
		try {
			mlr = new MyLineReader(userLBSinfoFile);

			mlr.init();
			while (mlr.hasNextLine()) {
				String[] line = mlr.nextLine().split(",");// id, postsAmount
				String uid = line[0];
				int totalPostsAmount = Integer.valueOf(line[1]);
				System.out.println("[Current rounds: " + (rounds + 1)
						+ " ], uid: " + uid + ", amount: " + totalPostsAmount);
				// sleep for a while
				if (rounds != 0)
					Toolbox.Sleep(sleepTimeMinutes * 60); // sleep for 5 minutes
				rounds++;
				// crawl the data
				crawlAndStoreData(uid, totalPostsAmount);
			}

			mlr.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void crawlAndStoreData(String uid, int totalPostsAmount) {

		String count = "50"; // 0-50 posts per page
		String sort = "0"; // 0: default, time; 1: distance
		int page = (int)(Math.floor(totalPostsAmount / Double.valueOf(count)) + 1);

		int sleepTime = 10;

		String filename = "timelinefile/user_timeline_" + uid + ".json";

		for (int p = 1; p <= page; p++) {
			try {
				sleepTime = Utils.randomInt(17, 23);
				System.out.println("Now take a nap for " + sleepTime
						+ " seconds.");
				Thread.sleep(sleepTime * 1000);

				Map map = new HashMap();
				map.put("count", count);
				map.put("page", String.valueOf(p));
				map.put("uid", uid);

				String result = Toolbox.crawlData("place/user_timeline.json",
						map);
				Toolbox.saveDataToFile(result, Utils.UESER_TIMELINE_FOLDER
						+ filename);
				System.out.println("Data has been stored to file. " + filename);
				totalRecords++;
				System.out.println("Rounds: " + rounds + ", page: " + p
						+ ", Total Records: " + totalRecords + ", uid: " + uid);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
