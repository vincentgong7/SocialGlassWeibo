/**
 * 
 */
package mt.weibo.crawl.archive;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import mt.weibo.common.Utils;
import mt.weibo.db.MyDBConnection;
import mt.weibo.model.UserJson;
import weibo4j.examples.oauth2.Log;
import weibo4j.http.HttpClient;
import weibo4j.http.Response;
import weibo4j.model.PostParameter;
import weibo4j.model.WeiboException;
import weibo4j.org.json.JSONArray;
import weibo4j.org.json.JSONException;
import weibo4j.org.json.JSONObject;
import weibo4j.util.ArrayUtils;
import weibo4j.util.WeiboConfig;

/**
 * @author vincentgong
 *
 */
public class PlaceNearbyUserJson implements Runnable {

	protected static HttpClient client = new HttpClient();
	private int sleepTimeMinutes = 120;
	
			/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Thread PlaceNearbyUserJson = new Thread(
				new PlaceNearbyUserJson());
		PlaceNearbyUserJson.start();
	}


	@Override
	public void run() {
		
		// only if first sleep is needed
		//Sleep(sleepTimeMinutes * 60);
		
		int rounds = 0;
		while (true) {
			System.out.println("[Current rounds: " + (rounds+1) +" ]");
			// sleep for a while
			if(rounds!=0) Sleep(sleepTimeMinutes * 60); // sleep for 5 minutes
			rounds ++;
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
			System.out.println("Sleeping for " + s/60 + " minutes...zzzzzzzzzz");
			System.out.println();
			Thread.sleep(s * 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void crawlAndStoreData() {
		// AMS
		// String lat = "52.374192";
		// String lon = "4.901189";
		// BJ 39.908859, 116.397400
		String lat = "39.999688";
		String lon = "116.326465";
		String range = "11132"; // [200,11132], default: 2000
		String count = "50"; // 0-50
		int page = 20;
		String sort = "0"; // 0: default, time; 1: distance
		int totalPostAmount = 0;
		String starttime = "1429840861000";
		String endtime = "1429844461000";
		int sleepTime = 10;

		for (int p = 1; p < page; p++) {
			try {
				sleepTime = Utils.randomInt(17, 23);
				System.out.println("Now take a nap for " + sleepTime + " seconds.");
				Thread.sleep(sleepTime * 1000);
				List<UserJson> l = new ArrayList<UserJson>();
				Map map = new HashMap();
				map.put("lat", lat);
				map.put("long", lon);
				map.put("range", range);
				map.put("count", count);
				map.put("page", String.valueOf(p));
				// map.put("starttime", starttime);
				// map.put("endtime", endtime);

				System.out.println("[This is the page: " + p + "]");
				if (p == page-1) {
					System.out
							.println("[This is the page" + (page-1) +"] !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				}
				if (p == page) {
					System.out
							.println("[This is the page " + page +"] !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				}

				l = nearbyUser(map);
				totalPostAmount += l.size();
//				System.out.println("[Current page: " + l.size()
//						+ "    Total: " + totalPostAmount + "  page: " + p);
				// store data
				storeUserJsonList(l);
				System.out.println("[Current page: " + l.size()
						+ "    Total: " + totalPostAmount + "  page: " + p);
			} catch (WeiboException e) {
				e.printStackTrace();
				Log.logInfo(e.toString());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void storeUserJsonList(List<UserJson> l) {
		Iterator<UserJson> it = l.iterator();
		PreparedStatement preparedStatement = null;
		MyDBConnection mdbc = new MyDBConnection();
		mdbc.init();
		while (it.hasNext()) {
			UserJson uj = it.next();

			String insertTableSQL = "INSERT INTO socialmedia.user"
					+ "(user_id, json) VALUES" + "(?,?)";
			try {
				preparedStatement = mdbc.getPrepareStatement(insertTableSQL);
				preparedStatement.setString(1, uj.getUser_id());
				preparedStatement.setString(2, uj.getJson());
				preparedStatement.executeUpdate();
				//System.out.println(sj.getStatus_id());
//				System.out.println();
			} catch (SQLException e) {
				System.out
						.println("Something wrong when writing the post to DB.");
//				e.printStackTrace();
				System.out.println(e.getMessage());
			}
		}
		mdbc.close();
	}

	// private void storeStatusJson(StatusJson sj) {
	// PreparedStatement preparedStatement = null;
	// String insertTableSQL = "INSERT INTO socialmedia.public_post"
	// + "(status_id, json) VALUES" + "(?,?)";
	// try {
	// MyDBConnection mdbc = new MyDBConnection();
	// mdbc.init();
	// preparedStatement = mdbc.getPrepareStatement(insertTableSQL);
	// preparedStatement.setString(1, sj.getStatus_id());
	// preparedStatement.setString(2, sj.getJson());
	// preparedStatement.executeUpdate();
	// mdbc.close();
	// System.out.println(sj.getStatus_id());
	// } catch (SQLException e) {
	// System.out.println("Something wrong when writing the post to DB.");
	// e.printStackTrace();
	// }
	// }

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
	public List<UserJson> nearbyUser(Map<String, String> map)
			throws WeiboException {
		PostParameter[] parList = ArrayUtils.mapToArray(map);
		Response res = client.get(
				WeiboConfig.getValue("baseURL") + "place/nearby/users.json", parList, Utils.randomAccessToken());
		List<UserJson> userList = new ArrayList<UserJson>();

		if (res == null || res.equals("")) {
			return userList;
		}

		String json;
		JSONObject jsonUser;
		JSONArray users = null;

		try {
			json = res.asString();
			System.out.println(json);
			jsonUser = res.asJSONObject(); // asJSONArray();
			
			if (!jsonUser.isNull("users")) {
				users = jsonUser.getJSONArray("users");
			}
			int size = users.length();
			for (int i = 0; i < size; i++) {
				userList.add(new UserJson(users.getJSONObject(i)));
			}
		} catch (JSONException jsone) {
			throw new WeiboException(jsone);
		}

		return userList;
	}
}
