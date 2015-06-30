package weibo4j.examples.place;

import weibo4j.Place;
import weibo4j.examples.oauth2.Log;
import weibo4j.model.WeiboException;
import weibo4j.org.json.JSONObject;

public class GetUserInfoInLbs {

	public static void main(String[] args) {
		String access_token = "2.00owJ9cFnCdKRD2a83307b23e8PHVE";
		String uid = "2116842364";
		Place p = new Place(access_token);
		try {
			JSONObject sw = p.userInfoInLBS(uid);
			Log.logInfo(sw.toString());
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}
}
