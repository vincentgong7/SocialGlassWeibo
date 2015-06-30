/**
 * 
 */
package mt.weibo.crawl.experiment.apis;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import mt.weibo.common.Utils;
import mt.weibo.crawl.experiment.ExpUtils;
import mt.weibo.crawl.experiment.aipx.Coordinates;
import weibo4j.model.WeiboException;

/**
 * @author vincentgong
 *
 */
public class IPAPI {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		IPAPI ipx = new IPAPI();
		ipx.process();
	}

	private void process() {
		String[] apis = { 
				//"trends/daily", 
//				"statuses/public_timeline.json",
				//"trends/weekly.json",
				//"emotions.json", 
				//"trends/hourly.json",
				//"tags/suggestions.json",
				//"suggestions/users/hot.json",
				//"common/get_timezone.json", 
//				"place/nearby_timeline.json,
				"place/nearby/users.json" };
		
		Coordinates[] coods = { new Coordinates("52.374192", "4.901189"),
				new Coordinates("39.905502", "116.397632"),
				new Coordinates("30.658676", "104.066877"),
				new Coordinates("31.248274", "121.453407"),
				new Coordinates("22.546560", "114.065123"),
				new Coordinates("23.127808", "113.267927"),
				new Coordinates("31.294030", "120.586538") };

		int times = 0;
		while (true) {
			sleep(1);
			times++;
			String key = Utils.randomAccessToken();
			Map map = new HashMap();
			int i = Utils.randomInt(0, apis.length - 1);
			String api = apis[i];
			if (api.startsWith("place")) {
				Coordinates co = coods[Utils.randomInt(0, coods.length - 1)];
				map.put("lat", co.lat);
				map.put("long", co.longi);
				map.put("range", "11132");
			}
			Date date = new Date();
			String currentStatus = "Times:" + times + ", api: " + api
					+ ", map:" + map.toString() + ", key: " + key + ", Date: "
					+ date.toString();
			ExpUtils.mylog(currentStatus);

			System.out.println(currentStatus);

			try {
				String result = ExpUtils.crawlData(api, map, key);
			} catch (WeiboException e) {
				System.out.println(e.getError());
				System.out.println(e.getMessage());
				ExpUtils.mylog(e.getError());
				ExpUtils.mylog(e.getMessage());
				if (e.getMessage().contains("limit")) {
					ExpUtils.mylog("Stop!");
					return;
				} else {
					continue;
				}
				// e.printStackTrace();

			}
		}

	}

	private void sleep(int i) {
		// TODO Auto-generated method stub
		try {
			Thread.sleep(i * 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
