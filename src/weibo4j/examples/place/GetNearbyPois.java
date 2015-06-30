package weibo4j.examples.place;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import weibo4j.Place;
import weibo4j.examples.oauth2.Log;
import weibo4j.model.Places;
import weibo4j.model.WeiboException;

public class GetNearbyPois {
	public static void main(String[] args) {
		String access_token = "2.00owJ9cFnCdKRD2a83307b23e8PHVE";
		String lat = "52.374192";
		String lon = "4.901189";
		String range = "2000"; // [200, 10000]
		Map map = new HashMap();
		map.put("lat", lat);
		map.put("long", lon);
		map.put("range", range);
		Place p = new Place(access_token);
		try {
			List<Places> list = p.nearbyPois(lat, lon);
			for (Places pl : list) {
				Log.logInfo(pl.toString());
			}
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}
}
