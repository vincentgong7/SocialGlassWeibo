/**
 * 
 */
package mt.weibo.crawl.archive;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import mt.weibo.common.Utils;
import mt.weibo.db.MyDBConnection;
import mt.weibo.db.PublicStatusDB;
import mt.weibo.model.StatusJson;
import weibo4j.examples.oauth2.Log;
import weibo4j.http.HttpClient;
import weibo4j.http.Response;
import weibo4j.model.PostParameter;
import weibo4j.model.Status;
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
public class PublicTimelineJson implements Runnable {

	protected static HttpClient client = new HttpClient();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Thread PublicTimeline = new Thread(new PublicTimelineJson());
		PublicTimeline.start();
	}

	public PublicTimelineJson() {

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
		int totalPostAmount = 0;
//		int page = 100;
		int sleepTime = 10;
		int p = 0;
		
		while(true) {
			try {
				p++;
				sleepTime = Utils.randomInt(10, 15);
				Thread.sleep(sleepTime * 1000);
				List<StatusJson> l = new ArrayList<StatusJson>();
				Map map = new HashMap();
				map.put("count", "50");
//				map.put("page", String.valueOf(p));
				System.out.println("[This is the rounds:] " + p);
				l = publicTimeLine(map);
				totalPostAmount += l.size();

				// store data
				storeStatusJsonList(l);
				
				System.out.println("[Current Records: " + l.size()
						+ "    Total: " + totalPostAmount + "  Rounds: " + p);

			} catch (WeiboException e) {
				e.printStackTrace();
				Log.logInfo(e.toString());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * 返回最新的公共微博
	 * 
	 * @param count
	 *            单页返回的记录条数，默认为20。
	 * @param baseApp
	 *            是否仅获取当前应用发布的信息。0为所有，1为仅本应用。
	 * @throws WeiboException
	 *             when Weibo service or network is unavailable
	 * @version weibo4j-V2 1.0.0
	 * @see http://open.weibo.com/wiki/2/statuses/public_timeline
	 * @since JDK 1.5
	 */
	public List<StatusJson> publicTimeLine(Map<String, String> map)
			throws WeiboException {
		PostParameter[] parList = ArrayUtils.mapToArray(map);
		Response res = client.get(WeiboConfig.getValue("baseURL")
		// + "statuses/public_timeline.json", parList, Utils.ACCESS_TOKEN);
		// Response res = client.get(WeiboConfig.getValue("baseURL")
				+ "statuses/public_timeline.json", parList, Utils.randomAccessToken());
		List<StatusJson> statusList = new ArrayList<StatusJson>();

		if (res == null || res.equals("")) {
			return statusList;
		}

		String json;
		JSONObject jsonStatus;
		JSONArray statuses = null;

		try {
			json = res.asString();
			System.out.println(json);
			jsonStatus = res.asJSONObject(); // asJSONArray();

			if (!jsonStatus.isNull("statuses")) {
				statuses = jsonStatus.getJSONArray("statuses");
			}
			int size = statuses.length();
			for (int i = 0; i < size; i++) {
				statusList.add(new StatusJson(statuses.getJSONObject(i)));
			}
		} catch (JSONException jsone) {
			throw new WeiboException(jsone);
		}

		return statusList;
	}

	private void storeStatusJsonList(List<StatusJson> l) {
		Iterator<StatusJson> it = l.iterator();
		PreparedStatement preparedStatement = null;
		String insertTableSQL = "INSERT INTO socialmedia.public_post"
				+ "(status_id, json) VALUES" + "(?,?)";
		MyDBConnection mdbc = new MyDBConnection();
		mdbc.init();
		while (it.hasNext()) {
			StatusJson sj = it.next();
			try {
				preparedStatement = mdbc.getPrepareStatement(insertTableSQL);
				preparedStatement.setString(1, sj.getStatus_id());
				preparedStatement.setString(2, sj.getJson());
				preparedStatement.executeUpdate();
				System.out.println(sj.getStatus_id());
			} catch (SQLException e) {
				System.out
						.println("Something wrong when writing the post to DB.");
				e.printStackTrace();
			}
		}
		mdbc.close();
	}

}
