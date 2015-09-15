package weibo4j.examples.place;

import weibo4j.Place;
import weibo4j.examples.oauth2.Log;
import weibo4j.model.Places;
import weibo4j.model.WeiboException;

public class GetPoisShow {

	public static void main(String[] args) {
		String access_token = "2.00owJ9cFnCdKRD2a83307b23e8PHVE";
		String poiid = "8008633032400000004";
//		String poiid = "B2094654D16CABFE419E";
		
		Place p = new Place(access_token);
		try {
			Places pl = p.poisShow(poiid);
			
//			System.out.println(pl.getCategory() + " // " + pl.getCategoryName() + " // " + pl.getCategorys());
//			System.out.println(pl.getCity());
			//Log.logInfo(pl.toString());
		} catch (WeiboException e) {
			e.printStackTrace();
		}

	}

}
