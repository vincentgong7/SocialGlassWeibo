package mt.weibo.crawl.general.dataprocess;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mt.weibo.crawl.general.CrawlTool;
import mt.weibo.db.MyDBConnection;
import mt.weibo.db.PoiDB;
import weibo4j.model.Places;
import weibo4j.model.WeiboException;
import weibo4j.org.json.JSONArray;
import weibo4j.org.json.JSONException;
import weibo4j.org.json.JSONObject;

public class POIQuery {

	private MyDBConnection mdbc;
	private Connection con;
	private String statusTableName = "socialmedia.post";
	private String poiTableName = "socialmedia.poi";
	private String distriTableName = "socialmedia.poi_district";

	private int interval = 2;
	private String crawlRange = "2000";
	private String api = "place/nearby/pois.json";
	private String logFileName = "poiid_crawl_log.txt";

	public static void main(String[] args) {
		POIQuery pq = new POIQuery();
		pq.process();
	}

	public POIQuery() {
		if (mdbc == null) {
			mdbc = new MyDBConnection();
			con = mdbc.getDBConnection();
		}
	}

	private void process() {

		Statement stmt = null;
		try {
			stmt = con.createStatement();
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			ResultSet poiq = stmt
					.executeQuery("SELECT status_id, latitude,longitude,poiid, is_poiid_checked, is_valid, district FROM "
							+ statusTableName
							+ " where is_poiid_checked = false and is_valid = true");

			Places place;
			while (poiq.next()) {

				// if (poiq.getBoolean("is_poiid_checked")) {// if it has been
				// continue;
				// }

				double lat = poiq.getDouble("latitude");
				double longi = poiq.getDouble("longitude");
				String ori_poiid = poiq.getString("poiid");

				CrawlTool.sleep(this.interval, "[POIID]");
				List<Places> poiList = queryPoi(lat, longi);

				if (poiList == null || poiList.size() == 0) {// if poiList is
																// null, we
																// still mark it
																// as checked
					if(ori_poiid!=null || !"".equals(ori_poiid)){
						poiq.updateString("district", ori_poiid);
					}
					poiq.updateString("poiid", "");
					poiq.updateBoolean("is_poiid_checked", true);
					poiq.updateRow();
					continue;
				}
				if (poiList.size() > 0) {
					place = poiList.get(0);

					if (!"".equals(ori_poiid)) {
						for (Places p : poiList) {
							if (ori_poiid.equals(p.getPoiid())) {
								place = p;
							}
						}
					}

					if (!ori_poiid.equals(place.getPoiid())) {
						poiq.updateString("district", ori_poiid);
						poiq.updateString("poiid", place.getPoiid());
					}
					// either situation we mark it as 'checked'
					poiq.updateBoolean("is_poiid_checked", true);
					poiq.updateRow();

					insertPoiInfo(place);
				}
			}
			poiq.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (mdbc != null) {
				mdbc.close();
			}

		}
	}

	private List<Places> queryPoi(double lat, double longi) {
		System.out.println(lat + "," + longi);
		Map<String, String> map = new HashMap<String, String>();
		map.put("lat", String.valueOf(lat));
		map.put("long", String.valueOf(longi));
		// map.put("range", crawlRange);

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
		pdb.insertDistrictFromPOI(place, distriTableName);
		// pdb.insertPOICat();
		pdb.close();
	}

}
