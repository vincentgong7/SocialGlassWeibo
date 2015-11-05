/**
 * 
 */
package mt.weibo.crawl.general.dataprocess.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mt.weibo.crawl.general.CrawlTool;
import mt.weibo.db.PoiDB;
import weibo4j.model.Places;
import weibo4j.model.WeiboException;
import weibo4j.org.json.JSONArray;
import weibo4j.org.json.JSONException;
import weibo4j.org.json.JSONObject;

/**
 * @author vincentgong
 *
 */
public class POICrawler {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	private static String api = "place/nearby/pois.json";
	public static String logFileName = "poiid_crawl_log.txt";
	private static String poiTableName = "socialmedia.poi";
	private static String distriTableName = "socialmedia.poi_district";

	public static String queryPoi(double lat, double longi, String crawlRange) {
		System.out.println(lat + "," + longi);
		Map<String, String> map = new HashMap<String, String>();
		map.put("lat", String.valueOf(lat));
		map.put("long", String.valueOf(longi));
		map.put("range", crawlRange);

		String json = CrawlTool.autoCrawl(api, map, logFileName);

		return json;
	}

	public static List<Places> constructPlaces(String json) {
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
	
	public static void insertPoiInfo(Places place) {
		PoiDB pdb = new PoiDB();
		pdb.insertPOI(place, poiTableName);
		pdb.insertDistrictFromPOI(place, distriTableName);
		// pdb.insertPOICat();
		pdb.close();
	}
}
