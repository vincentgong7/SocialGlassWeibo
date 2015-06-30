package weibo4j.examples.place;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import mt.weibo.common.Utils;
import weibo4j.Place;
import weibo4j.examples.oauth2.Log;
import weibo4j.model.Status;
import weibo4j.model.StatusWapper;
import weibo4j.model.WeiboException;
import weibo4j.org.json.JSONException;
import weibo4j.org.json.JSONObject;

public class GetNearbyTimeLine {

	// public static void main(String[] args) {
	// String access_token = "2.00owJ9cFnCdKRD2a83307b23e8PHVE";
	// String lat = "52.374192";
	// String lon = "4.901189";
	// String range = "2000"; //[200,11132], default: 2000
	// Place p = new Place(access_token);
	// try {
	// // StatusWapper sw = p.nearbyTimeLine(lat, lon);
	// Map map = new HashMap();
	// map.put("lat", lat);
	// map.put("long", lon);
	// map.put("range", range);
	// StatusWapper sw = p.nearbyTimeLine(map);
	// Log.logInfo(sw.toString());
	// System.out.println(sw.toString());
	// } catch (WeiboException e) {
	// e.printStackTrace();
	// }
	// }

	public static void main(String[] args) {
		String access_token = Utils.randomAccessToken();
		
		//AMS
//		String lat = "52.374192";
//		String lon = "4.901189";
		// BJ tiananmeng 39.905502, 116.397632
		String lat = "39.905502";
		String lon = "116.397632";
		
		String range = "7000"; // [200,11132], default: 2000
		Place p = new Place(access_token);
		try {
			// StatusWapper sw = p.nearbyTimeLine(lat, lon);
			Map map = new HashMap();
			map.put("lat", lat);
			map.put("long", lon);
			map.put("range", range);
			map.put("count", "20");
			map.put("page", "2");
			StatusWapper sw = p.nearbyTimeLine(map);

//			List<Status> l = sw.getStatuses();
//			Iterator<Status> it = l.iterator();
//			while (it.hasNext()) {
//				Status st = it.next();
//				System.out.println(st.getAnnotations());
//
//				String annotation = st.getAnnotations();
//				if (annotation != null && !annotation.equals("")) {
//					JSONObject json = new JSONObject(annotation.substring(1,
//							annotation.length() - 1));
//					// System.out.println(annotation.substring(1,
//					// annotation.length()-1));
//					Object obj = json.get("place");
//					if (obj != null) {
//						String place = obj.toString();
//						if (place != null && !place.equals("")) {
//							JSONObject placeJson = new JSONObject(place);
//							String poiid = placeJson.get("poiid").toString();
//							System.out.println(poiid);
//						}
//					}
//
//				}
//
//			}
			// Log.logInfo(sw.toString());
			// System.out.println(sw.toString());
		} catch (WeiboException e) {
			e.printStackTrace();
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
	}

}
