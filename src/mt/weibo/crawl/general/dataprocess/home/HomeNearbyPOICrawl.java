package mt.weibo.crawl.general.dataprocess.home;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mt.weibo.crawl.general.CrawlTool;
import mt.weibo.crawl.general.dataprocess.common.POICrawler;
import mt.weibo.db.MyDBConnection;
import weibo4j.model.Places;

public class HomeNearbyPOICrawl {

	private MyDBConnection mdbc;
	private Connection con;
	private int interval = 2;
	private String crawlRange = "300";
	private String api = "place/nearby/pois.json";
	private String logFileName = "home_nearby_poi_log.txt";
	private String JsonlogName = "home_nearby_poi_json.txt";

	// private String userTablename = "socialmedia.user";
	// private String postTablename = "socialmedia.post";

	public static void main(String[] args) {
		// usage: java -jar HomeLocator.jar [port]
		int port = 5432;
		if (args.length > 0) {
			port = Integer.valueOf(args[0]);
		}
		HomeNearbyPOICrawl hnpc = new HomeNearbyPOICrawl(port);
		hnpc.process();
	}

	public HomeNearbyPOICrawl(int port) {
		if (mdbc == null) {
			mdbc = new MyDBConnection(port);
			con = mdbc.getDBConnection();
		}
	}

	public void process() {
		System.out.println("Working on Localhost.");
		System.out
				.println("Start crawl nearby POIs around users' home location.....");
		Statement stmtUser = null;
		try {
			stmtUser = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			ResultSet usersSet = stmtUser
					.executeQuery("SELECT user_id, home_lat, home_lon, is_nearby_crawled from socialmedia.home where is_nearby_crawled = false");

			int k = 0;
			while (usersSet.next()) {
				k++;
				String userID = usersSet.getString("user_id");
				double lat = usersSet.getDouble("home_lat");
				double lon = usersSet.getDouble("home_lon");
				String line = "Processing nearby_home_poi. User ID:" + userID
						+ ", lat = " + lat + ", lon = " + lon + ", Current: "
						+ k;
				System.out.println(line);

				Map<String, String> map = new HashMap<String, String>();
				map.put("lat", String.valueOf(lat));
				map.put("long", String.valueOf(lon));
				map.put("range", crawlRange);

				CrawlTool.sleep(this.interval, "[HOME_NEARBY_POIs]");
				String result = CrawlTool.autoCrawl(api, map, logFileName);
				// ExpUtils.mylogJson(
				// CrawlTool.splitFileNameByHour(JsonlogName), result);
				List<Places> poiList = POICrawler.constructPlaces(result);
				int placesCount = 0;
				if (poiList != null && poiList.size() != 0) {
					
					for (Places place : poiList) {
						
						// insert into home_nearby_poi table
						String sql = "INSERT into socialmedia.home_nearby_poi(user_id, poiid) VALUES(?,?)";
						PreparedStatement insertHomeNearbyPoi = con
								.prepareStatement(sql);
						insertHomeNearbyPoi.setString(1, userID);
						insertHomeNearbyPoi.setString(2, place.getPoiid());
						insertHomeNearbyPoi.executeUpdate();
						insertHomeNearbyPoi.close();

						// insert into poi table
						POICrawler.insertPoiInfo(place);
						
						placesCount++;
					}
				}
				usersSet.updateBoolean("is_nearby_crawled", true);
				usersSet.updateRow();
				
				System.out.println();
				System.out.println(placesCount + " places added.");
				System.out.println();
			}
			usersSet.close();
			stmtUser.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
