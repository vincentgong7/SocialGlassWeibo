package weibo4j.examples.place;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import weibo4j.Place;
import weibo4j.examples.oauth2.Log;
import weibo4j.model.PoisitionCategory;
import weibo4j.model.WeiboException;

public class GetPoisCategory {

	public static void main(String[] args) {
		String access_token = "2.00owJ9cFnCdKRD2a83307b23e8PHVE";
		Place p = new Place(access_token);
		Map map = new HashMap();
		map.put("flag", "1");
		try {
			List<PoisitionCategory> list = p.poisCategory(map);
			for (PoisitionCategory pois : list) {
				System.out.println(pois.getName());
				Log.logInfo(pois.toString());
			}
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}

}
