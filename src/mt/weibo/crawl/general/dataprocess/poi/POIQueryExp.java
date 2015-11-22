package mt.weibo.crawl.general.dataprocess.poi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mt.weibo.common.AppKeyCenter;
import mt.weibo.crawl.general.CrawlTool;
import mt.weibo.db.MyDB;
import mt.weibo.db.MyDBConnection;
import mt.weibo.db.PoiDB;
import weibo4j.model.Places;
import weibo4j.model.WeiboException;
import weibo4j.org.json.JSONArray;
import weibo4j.org.json.JSONException;
import weibo4j.org.json.JSONObject;

public class POIQueryExp {

	private MyDBConnection mdbc;
	private Connection con;
	private String statusTableName = "socialmedia.post_in_scope";
	private String poiTableName = "socialmedia.poi";
	private String distriTableName = "socialmedia.poi_district";

	private int interval = 3;
	private int crawlRange = 2000; // default = 2000, max = 10000 meters
	private int count = 50; // items per page, default = 20, max = 50;
	private String api = "place/nearby/pois.json";
	private String logFileName = "poiid_crawl_log.txt";
	private int sort = 0; // 0:weight, 1:distance, 2:checkin_np
	private int port = 5432;
	private String poiListTablename = "socialmedia.poi_sina_weighted"; // poi_sina_weighted,
																		// poi_sina_distance,
																		// poi_sina_checkin
	private Long lastMill = 0l;

	public static void main(String[] args) {
		// usage: java -jar poi_query_exp.jar [weight|distance|checkin] interval
		// appkey_file_name
		// example: java -jar poi_query_exp.jar distance 5 appkey.txt
		int port = 5432;
		int sort = 0;
		int interval = 18;
		String appkeyFile = "appkey.txt";
		if (args.length > 0) {
			if (args[0].equals("weight")) {
				sort = 0;
			} else if (args[0].equals("distance")) {
				sort = 1;
			} else if (args[0].equals("checkin")) {
				sort = 2;
			} else {
				System.out.println("Parameter error: " + args[0]);
				System.out
						.println("usage: java -jar POIQueryExp.jar [weight|distance|checkin]");
			}
			interval = Integer.valueOf(args[1]);
			if (args.length == 3) {
				appkeyFile = args[2];
				AppKeyCenter.getInstance(appkeyFile);
			}
		}

		POIQueryExp pq = new POIQueryExp(sort, interval, port);
		pq.process();
	}

	public POIQueryExp(int sort, int interval, int port) {
		this.port = port;
		this.sort = sort;
		this.interval = interval;

		if (sort == 0) {
			this.poiListTablename = "socialmedia.poi_sina_weighted";
		} else if (sort == 1) {
			this.poiListTablename = "socialmedia.poi_sina_distance";
		} else if (sort == 2) {
			this.poiListTablename = "socialmedia.poi_sina_checkin";
		}

		if (mdbc == null) {
			mdbc = new MyDBConnection(port);
			con = mdbc.getDBConnection();
		}
	}

	private void process() {

		Statement stmt = null;
		try {
			// stmt = con.createStatement();
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
//			MyDB.init(port);
			String querySql = "SELECT post_id, user_id, lat, lon FROM "
					+ statusTableName
					+ " where user_id not in (select distinct user_id from "
					+ this.poiListTablename + ")";

			ResultSet postInS = stmt.executeQuery(querySql);

			// SELECT post_id, user_id, lat, lon FROM socialmedia.post_in_scope
			// where user_id not in (select distinct user_id from
			// socialmedia.poi_sina_weighted)
			// SELECT count(*) FROM socialmedia.post_in_scope where user_id not
			// in (select distinct user_id from socialmedia.poi_sina_weighted)

			while (postInS.next()) {

				String post_id = postInS.getString("post_id");
				String user_id = postInS.getString("user_id");
				double lat = postInS.getDouble("lat");
				double longi = postInS.getDouble("lon");

				System.out.println();
				System.out.println("Starting crawl: sort = " + this.sort
						+ ", count = " + count + ", post_id=" + post_id
						+ ", user_id=" + user_id);

				sleepOrNot();
				// CrawlTool.sleep(this.interval, "[POIID]");

				List<Places> poiList = queryPoi(lat, longi, this.sort,
						this.count, this.crawlRange);

				int sequence = 0;
				if (poiList.size() > 0) {

					String sql = "insert into "
							+ this.poiListTablename
							+ " (post_id, user_id, poiid, sequence) values (?,?,?,?)";
					System.out.println(sql);
					PreparedStatement inserPoiListPs = con
							.prepareStatement(sql);

					for (Places p : poiList) {
						try {
							sequence++;

							// INSERT INTO table
							// (column1, column2, ... )
							// VALUES
							// (expression1, expression2, ... );
							// post_id | user_id | poiid | order

							// String sql = "insert into " +
							// this.poiListTablename
							// +
							// " (post_id, user_id, poiid, \"order\") values ('"
							// + post_id + "', '" + user_id + "', '"
							// + p.getPoiid() + "', " + sequence + ")";
							// MyDB.queryUpdate(sql);

							inserPoiListPs.setString(1, post_id);
							inserPoiListPs.setString(2, user_id);
							inserPoiListPs.setString(3, p.getPoiid());
							inserPoiListPs.setInt(4, sequence);
							inserPoiListPs.executeUpdate();

							insertPoiInfo(p);
						} catch (SQLException e) {
							continue;
						}

					}
					inserPoiListPs.close();
				}
			}
			postInS.close();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (mdbc != null) {
				mdbc.close();
			}
		}
	}

	private void sleepOrNot() {
		// TODO Auto-generated method stub
		Date nowtime = new Date();
		Long nowMill = nowtime.getTime();
		if (nowMill - this.lastMill < this.interval * 1000) {
			System.out.println("need some sleep.");
			int sleepTime = (int) ((this.interval * 1000 - (nowMill - this.lastMill)) / 1000);
			CrawlTool.sleep(sleepTime, "[POIID]");
		} else {
			System.out.println("no need sleep at all.");
		}
		Date current = new Date();
		this.lastMill = current.getTime();
	}

	private List<Places> queryPoi(double lat, double longi, int sort,
			int count, int crawlRange) {
		System.out.println(lat + "," + longi);
		Map<String, String> map = new HashMap<String, String>();
		map.put("lat", String.valueOf(lat));
		map.put("long", String.valueOf(longi));
		map.put("sort", String.valueOf(sort));
		map.put("count", String.valueOf(count));
		// map.put("range", crawlRange);

		// System.out.println(map.toString());

		String json = CrawlTool.autoCrawl(api, map, logFileName);
		List<Places> poiList = constructPlaces(json);

		return poiList;
	}

	private List<Places> constructPlaces(String json) {
		JSONObject jsonObj;
		List<Places> list = new ArrayList<Places>();
		try {
			jsonObj = new JSONObject(json);
			JSONArray pois = jsonObj.getJSONArray("pois");
			if (pois != null) {
				int size = pois.length();
				list = new ArrayList<Places>(size);
				for (int i = 0; i < size; i++) {
					list.add(new Places(pois.getJSONObject(i)));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (WeiboException e) {
			e.printStackTrace();
		}
		return list;
	}

	private void insertPoiInfo(Places place) {
		PoiDB pdb = new PoiDB();
		pdb.insertPOI(place, poiTableName);
		if (place.getDistrict() != null) {
			pdb.insertDistrictFromPOI(place, distriTableName);
		}
		// pdb.insertPOICat();
		pdb.close();
	}

}