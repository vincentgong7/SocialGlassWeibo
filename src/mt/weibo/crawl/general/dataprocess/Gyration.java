package mt.weibo.crawl.general.dataprocess;

import java.sql.ResultSet;
import java.sql.SQLException;

import mt.weibo.crawl.general.dataprocess.common.DataProcessUtils;
import mt.weibo.db.MyDB;

public class Gyration {

	private String user_in_scope_tablename = "socialmedia.user_in_scope_gyration";
	private String post_in_scope_tablename = "socialmedia.post_in_scope";
	private String dbname = "shenzhen";

	public Gyration(String dbname, int port) {
		// TODO Auto-generated constructor stub
		MyDB.init(port, dbname);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int port = 5432;
		if (args.length > 0) {
			port = Integer.valueOf(args[0]);
		}
		Gyration gyration = new Gyration("shenzhen", port);
		gyration.process();
	}

	private void process() {

		// queryupdate users from user_in_scope
		// user_id, name, profile_pic, city_role, gyration, gender, age
		int times = 0;
		ResultSet userRS;
		try {
			userRS = MyDB.queryUpdate("Select * from "
					+ user_in_scope_tablename + " where gyration = -1");
			while (userRS.next()) {
				times++;
				String userID = userRS.getString("user_id");

				// post_id, user_id, content, source, lat, lon, poiid,
				// "timestamp"

				// calculate the average lat and lon
				ResultSet avgRS = MyDB
						.query("select avg(lat) as avg_lat, avg(lon) as avg_lon from "
								+ post_in_scope_tablename
								+ " where user_id = '"
								+ userID
								+ "' group by user_id");
				double centerLat = 0d;
				double centerLon = 0d;

				if (avgRS.next()) {
					centerLat = avgRS.getDouble("avg_lat");
					centerLon = avgRS.getDouble("avg_lon");
				}
				avgRS.close();

				// calculate the distance from each points to central point

				float sum = 0f;
				ResultSet pointsRS = MyDB.query("select lat, lon from "
						+ post_in_scope_tablename + " where user_id = '"
						+ userID + "'");
				int count = 0;
				while (pointsRS.next()) {
					double lat = pointsRS.getDouble("lat");
					double lon = pointsRS.getDouble("lon");
					count++;
					double distance = DataProcessUtils.distance(lat, lon,
							centerLat, centerLon);
					sum += Math.pow(distance / 1000, 2);
				}
				pointsRS.close();

				double gyration = Math.sqrt(sum / count);
				System.out.println("times = " + times + ", count = " + count
						+ ", gyration = " + gyration);
				userRS.updateDouble("gyration", gyration);
				userRS.updateInt("np_places", count);
				userRS.updateRow();
			}
			userRS.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
