/**
 * 
 */
package mt.weibo.crawl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import mt.weibo.common.Utils;
import mt.weibo.db.MyDBConnection;
import weibo4j.examples.oauth2.Log;
import weibo4j.http.HttpClient;
import weibo4j.http.Response;
import weibo4j.model.PostParameter;
import weibo4j.model.WeiboException;
import weibo4j.util.ArrayUtils;
import weibo4j.util.WeiboConfig;

/**
 * @author vincentgong
 *
 */
public class PlaceNearbyUserPureJson implements Runnable {

	protected static HttpClient client = new HttpClient();
	private int sleepTimeMinutes = 120;
	private int rounds = 0;
	private int totalRecords = 0;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Thread PlaceNearbyUserPureJson = new Thread(
				new PlaceNearbyUserPureJson());
		PlaceNearbyUserPureJson.start();
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

	private void Sleep(int seconds) {
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
		// AMS
		 String lat = "52.374192";
		 String lon = "4.901189";
		// BJ 39.908859, 116.397400` near tiananmeng
		// BJ tiananmeng 39.905502, 116.397632
//		String lat = "39.999688";
//		String lon = "116.326465";
		String range = "11132"; // [200,11132], default: 2000
		String count = "50"; // 0-50
		int page = 20;
		String sort = "0"; // 0: default, time; 1: distance
		int totalPostAmount = 0;
		// String starttime = "1429840861000";
		// String endtime = "1429844461000";
		int sleepTime = 10;

		for (int p = 1; p < page; p++) {
			try {
				sleepTime = Utils.randomInt(17, 23);
				System.out.println("Now take a nap for " + sleepTime
						+ " seconds.");
				Thread.sleep(sleepTime * 1000);

				Map map = new HashMap();
				map.put("lat", lat);
				map.put("long", lon);
				map.put("range", range);
				map.put("count", count);
				map.put("page", String.valueOf(p));
				// map.put("starttime", starttime);
				// map.put("endtime", endtime);

				System.out.println("[This is the page: " + p + "]");
				if (p == page - 1) {
					System.out.println("[This is the page" + (page - 1)
							+ "] !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				}
				if (p == page) {
					System.out.println("[This is the page " + page
							+ "] !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				}

				String result = nearbyUser(map);
				storeUserJsonList(result);
				totalRecords++;
				System.out.println("Rounds: "+ rounds +", page: " + p + ", Total Records: " + totalRecords);
			} catch (WeiboException e) {
				e.printStackTrace();
				Log.logInfo(e.toString());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void storeUserJsonList(String json) {
		PreparedStatement preparedStatement = null;
		MyDBConnection mdbc = new MyDBConnection();
		mdbc.init();

		String insertTableSQL = "INSERT INTO socialmedia.user_json"
				+ "(json, date) VALUES" + "(?,?)";
		try {
			preparedStatement = mdbc.getPrepareStatement(insertTableSQL);
			Date now = new Date();
			preparedStatement.setString(1, json);
			preparedStatement.setString(2, now.toString());
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			System.out.println("Something wrong when writing the post to DB.");
			e.printStackTrace();
			// System.out.println(e.getMessage());
		}
		mdbc.close();
	}

	/**
	 * 获取某个位置周边的动态
	 * 
	 * @param map
	 *            参数列表
	 * @return
	 * @throws WeiboException
	 *             when Weibo service or network is unavailable
	 * @version weibo4j-V2 1.0.2
	 * @see http://open.weibo.com/wiki/2/place/nearby_timeline
	 * @since JDK 1.5
	 */
	public String nearbyUser(Map<String, String> map) throws WeiboException {

		String result = "";
		PostParameter[] parList = ArrayUtils.mapToArray(map);
		Response res = client.get(WeiboConfig.getValue("baseURL")
				+ "place/nearby/users.json", parList, Utils.randomAccessToken());

		if (res == null || res.equals("")) {
			return result;
		}

		return res.asString();
	}
}
