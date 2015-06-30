package weibo4j.examples.place;

import java.util.HashMap;
import java.util.Map;

import weibo4j.Place;
import weibo4j.examples.oauth2.Log;
import weibo4j.model.UserWapper;
import weibo4j.model.WeiboException;

public class GetNearbyUsers {

	public static void main(String[] args) {
		String access_token = "2.00owJ9cFnCdKRD2a83307b23e8PHVE";
		String lat = "52.374192";
		String lon = "4.901189";
		String range = "11132"; // 100 to 11132, default 2000
		
		Place p = new Place(access_token);
		try {
//			UserWapper uw = p.nearbyUsers(lat, lon);
			Map map = new HashMap();
			map.put("lat", lat);
			map.put("long", lon);
			map.put("range", range);
			map.put("count", "30");
			map.put("page", "40");
			UserWapper uw = p.nearbyUsers(map);
			Log.logInfo(uw.toString());
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}
	

}
