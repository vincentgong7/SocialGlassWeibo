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
import mt.weibo.db.StatusDB;
import weibo4j.Place;
import weibo4j.examples.oauth2.Log;
import weibo4j.model.Status;
import weibo4j.model.StatusWapper;
import weibo4j.model.WeiboException;

/**
 * @author vincentgong
 *
 */
public class PlaceNearbyTimeline implements Runnable {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Thread crawlNearbyTimeline = new Thread(new PlaceNearbyTimeline());
		crawlNearbyTimeline.start();
	}

	public PlaceNearbyTimeline() {

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

		StatusDB sd = new StatusDB();
		sd.insertStatusListWithUsers(statusList);

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
		// AMS
		String lat = "52.374192";
		String lon = "4.901189";
		// BJ 39.908859, 116.397400
		// String lat = "39.908859";
		// String lon = "116.397400";
		String range = "11132"; // [200,11132], default: 2000
		String count = "50"; // 0-50
		String starttime = String.valueOf(ConfigDB.getStarttime()); // Unix
																	// timestamp
																	// of the
																	// most
																	// recent
																	// post
		String sort = "0"; // 0: default, time; 1: distance
		Place p = new Place(access_token);
		// Timeline tm = new Timeline(access_token);

		for (int page = 1; page < 20; page++) {
			try {
				List<Status> l = new ArrayList<Status>();
				Map map = new HashMap();
				map.put("lat", lat);
				map.put("long", lon);
				map.put("range", range);
				map.put("count", count);
				map.put("page", String.valueOf(page));
				// if(!starttime.equals("0")){
				// map.put("starttime", "1429772351000");
				// }
				StatusWapper sw = p.nearbyTimeLine(map);
				sw.getTotalNumber();
				// StatusWapper sw = tm.getPublicTimeline();
				// Log.logInfo(sw.toString());

				l = sw.getStatuses();

				// store data
				storeStatusList(l);
				System.out.println("[This is the rounds:] " + page);
				Sleep();
			} catch (WeiboException e) {
				e.printStackTrace();
				Log.logInfo(e.toString());
			}
		}

	}

}
