/**
 * 
 */
package mt.weibo.crawl.archive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mt.weibo.db.ConfigDB;
import mt.weibo.db.MyDBConnection;
import mt.weibo.db.PublicStatusDB;
import mt.weibo.db.StatusDB;
import weibo4j.Place;
import weibo4j.Timeline;
import weibo4j.examples.oauth2.Log;
import weibo4j.model.Status;
import weibo4j.model.StatusWapper;
import weibo4j.model.WeiboException;

/**
 * @author vincentgong
 *
 */
public class PublicTimeline implements Runnable {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Thread PublicTimeline = new Thread(new PublicTimeline());
		PublicTimeline.start();
	}

	public PublicTimeline() {

	}

	@Override
	public void run() {

		// while(true){
		// crawl the data
		crawlAndStoreData();

		// sleep for a while
		Sleep();
		// }
	}

	private void storeStatusList(List<Status> statusList) {

		MyDBConnection mdbc = new MyDBConnection();
		mdbc.init();

		PublicStatusDB psd = new PublicStatusDB(mdbc);
		psd.insertStatusListWithUsers(statusList);

		mdbc.close();
	}

	private void Sleep() {
		// TODO Auto-generated method stub
		try {
			Thread.sleep(10 * 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void crawlAndStoreData() {
		String access_token = "2.00owJ9cFnCdKRD2a83307b23e8PHVE";
		Timeline tm = new Timeline(access_token);
		int totalPostAmount = 0;
		
		for (int page = 1; page < 100; page++) {
			try {
				Sleep();
				List<Status> l = new ArrayList<Status>();
				Map map = new HashMap();
				map.put("count", "50");
				map.put("page", String.valueOf(page));
				StatusWapper sw = tm.getPublicTimeline();
				Log.logInfo(sw.toString());
				
				System.out.println("[This is the rounds:] " + page);
				l = sw.getStatuses();
				totalPostAmount += l.size();
				System.out.println("[Current Records: " + l.size()
						+ "    Total: " + totalPostAmount + "  page: " + page);
				// store data
				//storeStatusList(l);
				System.out.println("[This is the rounds:] " + page);
				
			} catch (WeiboException e) {
				e.printStackTrace();
				Log.logInfo(e.toString());
			}
		}

	}

}
