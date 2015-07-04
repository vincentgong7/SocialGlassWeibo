/**
 * 
 */
package mt.weibo.crawl.experiment.aipx;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import mt.weibo.common.Utils;
import mt.weibo.crawl.experiment.ExpUtils;
import mt.weibo.crawl.experiment.aipx.model.KeyReport;
import mt.weibo.model.Coordinates;
import weibo4j.model.WeiboException;

/**
 * @author vincentgong
 *
 */
public class IPXv2Beijing {

	private String logName = "log.txt";
	private String JsonlogName = "jsonlog.txt";

	private Integer keysFrom = 0;
	private Integer keysTo;

	private Integer reqIntervalTimeFrom = 0;
	private Integer reqIntervalTimeTo;

	private Integer retryTimeFrom = 0;
	private Integer retryTimeTo;

	private Integer apiFrom;
	private Integer apiTo;

	private boolean recordJson = false;

	private int runTimes = 1000;

	private boolean turnKey = false;

	private int stopTimes = 0;
	private int totalTimes = 0;
	public Date startDate1;
	public Date endDate1;
	private Set bannedKeysSet;
	public Map reportMap;
	private int runRounds = 0;
	private List keys;
	private int keyID;
	private String currentKey = "";
	private int nearbyuserPage = 0;
	
	private Coordinates[] coods = { 
//			new Coordinates("52.374192", "4.901189"),
			new Coordinates("39.905502", "116.397632"),//北京
//			new Coordinates("30.658676", "104.066877"),
//			new Coordinates("31.248274", "121.453407"),
//			new Coordinates("22.546560", "114.065123"),
//			new Coordinates("23.127808", "113.267927"),
//			new Coordinates("31.294030", "120.586538"),
//			new Coordinates("34.277626", "108.922817"),// 西安
//			new Coordinates("34.910396", "108.961372"),// 银川
//			new Coordinates("29.561249", "106.460470"),// 重庆沙坪坝
//			new Coordinates("29.413468", "106.541322"),// 重庆南
//			new Coordinates("29.505390", "106.598142"),// 重庆东
//			new Coordinates("29.594952", "103.485296"),// 峨眉山市
//			new Coordinates("30.724728", "103.970889"),// 成都高新西区
//			new Coordinates("30.632877", "104.058700"),// 玉林
//			new Coordinates("30.051259", "103.834115"),// 眉山
//			new Coordinates("32.068726", "118.775173"),// 南京
//			new Coordinates("30.280367", "120.151634"),// 杭州
//			new Coordinates("22.280589", "114.158359"),// 香港
//			new Coordinates("30.614220", "114.301071"),// 武汉
//			new Coordinates("43.829278", "125.320584"),// 长春
	};

	public String[] apis = { "statuses/public_timeline.json",
			"tags/suggestions.json", "suggestions/users/hot.json",
			"common/get_timezone.json", "place/nearby_timeline.json",
			"place/nearby/users.json",
	// "emotions.json",
	// "trends/daily",
	// "trends/weekly.json",
	// "trends/hourly.json",
	};
	private String logSign = " [IPXv2]Beijing ";


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		IPXv2Beijing ipxv2 = new IPXv2Beijing();
		ipxv2.setup(args[0]);
		ipxv2.process();
	}

	private void setup(String configFileName) {
		InputStream is = null;
		try {
			is = new FileInputStream(Utils.getPath() + "/" + configFileName);
			Properties config = new Properties();
			config.load(is);

			this.keysFrom = Integer.valueOf(config.getProperty("key_from"));
			this.keysTo = Integer.valueOf(config.getProperty("key_to"));
			this.turnKey = Boolean.valueOf(config.getProperty("turn_key"));
			this.apiFrom = Integer.valueOf(config.getProperty("api_from"));
			this.apiTo = Integer.valueOf(config.getProperty("api_to"));
			this.reqIntervalTimeFrom = Integer.valueOf(config
					.getProperty("request_interval_from"));
			this.reqIntervalTimeTo = Integer.valueOf(config
					.getProperty("request_interval_to"));
			this.retryTimeFrom = Integer.valueOf(config
					.getProperty("retry_time_from"));
			this.retryTimeTo = Integer.valueOf(config
					.getProperty("retry_time_to"));
			this.logName = config.getProperty("log_name");
			this.recordJson = Boolean
					.valueOf(config.getProperty("record_json"));
			this.runTimes = Integer.valueOf(config.getProperty("run_time"));
			this.runRounds = Integer.valueOf(config.getProperty("run_rounds"));
			this.JsonlogName = config.getProperty("json_log_name");

			if (apiFrom < 0 || apiFrom > apis.length - 1) {
				apiFrom = 0;
			}
			if (apiTo < 0 || apiTo > apis.length - 1) {
				apiTo = apis.length - 1;
			}

			// String keys[] = Utils.getAccessTokenList();
			bannedKeysSet = new HashSet();
			reportMap = new HashMap();
			keys = Utils.getAccessTokenList(keysFrom, keysTo);
			if (runTimes == 0) {
				runTimes = Integer.MAX_VALUE;
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public void IPXv2() {

	}

	private void setRunTimes(String times) {
		if (times.equals("0")) {
			this.runTimes = Integer.MAX_VALUE;// 2147483647
		} else {
			this.runTimes = Integer.valueOf(times);
		}
	}

	private void setRecordJson(String isRecordJson) {
		if (isRecordJson.equals("1")) {
			this.recordJson = true;
		} else {
			this.recordJson = false;
		}
	}

	private void setLogName(String logName) {
		this.logName = logName;
	}

	private void setSleepTime(String sleepFrom, String sleepTo) {
		this.reqIntervalTimeFrom = Integer.valueOf(sleepFrom);
		this.reqIntervalTimeTo = Integer.valueOf(sleepTo);
	}

	private void setApis(String apiFrom, String apiTo) {
		this.apiFrom = Integer.valueOf(apiFrom);
		this.apiTo = Integer.valueOf(apiTo);
	}

	private void setKeys(String key1, String key2) {
		this.keysFrom = Integer.valueOf(key1);
		this.keysTo = Integer.valueOf(key2);
	}

	private void process() {
		int rounds = 0;
		while (rounds < runRounds) {
			rounds++;
			// sleep(reqIntervalTimeFrom, reqIntervalTimeTo);
			Date roundStartDate = new Date();
			if (recordJson) {
				String jsonLog = "[Rounds: " + rounds + ", Date: "
						+ roundStartDate.toString() + "]";
				System.out
						.println(this.logSign + "Start to write to Json log.");
				ExpUtils.mylogJson(JsonlogName, this.logSign + jsonLog);
			}
			int times = 0;
			while (times < runTimes && hasNextAvailableKey()) {
				this.currentKey = getNextAvailableKey();
				keyID = Utils.getKeyID(this.currentKey);
				System.out
						.println(this.logSign + "This is the times: " + times);
				sleep(reqIntervalTimeFrom, reqIntervalTimeTo);
				times++;
				totalTimes++;
				String api = getNextAPI();
				Map map = getParMap(api);
				Date now = new Date();
				//
				KeyReport kr;
				if (reportMap.containsKey(this.currentKey)) {
					kr = (KeyReport) reportMap.get(this.currentKey);
				} else {
					kr = new KeyReport();
				}
				if (kr.startDate.equals("")) {
					kr.startDate = now.toString();
				}
				kr.requestTimes = kr.requestTimes + 1;
				kr.keyID = keyID;
				kr.key = this.currentKey;
				reportMap.put(this.currentKey, kr);

				String printLine = " Times:" + times + ", api: " + api
						+ ", map:" + map.toString() + ", key: " + this.currentKey
						+ ", Date: " + now.toString();
				System.out.println(this.logSign + printLine);
				ExpUtils.mylog(logName, this.logSign + printLine);
				try {
					String result = ExpUtils.crawlData(api, map, this.currentKey);
					if (recordJson) {
						ExpUtils.mylogJson(JsonlogName, result);
					}
					if (reportMap.containsKey(this.currentKey)) {
						KeyReport skr = (KeyReport) reportMap.get(this.currentKey);
						skr.successTimes++;
						skr.endDate = new Date().toString();
						reportMap.put(this.currentKey, skr);
					}
				} catch (WeiboException e) {
					System.out.println(this.logSign + "Error Code: "
							+ e.getErrorCode() + ". Error: " + e.getError());
					System.out.println(this.logSign + e.getMessage());
					ExpUtils.mylog(logName, this.logSign + " Error Code: " + e.getErrorCode()
							+ ". Error: " + e.getError());
					ExpUtils.mylog(logName, this.logSign + e.getMessage());

					if (e.getMessage().contains(
							"User requests out of rate limit")) {
						this.stopTimes++;
						String line = "Out of limit! Cureent times: " + times
								+ ", Total times: " + this.totalTimes
								+ ", Stop Times: " + stopTimes + ", key_id: "
								+ keyID + ", key: " + this.currentKey;

						this.bannedKeysSet.add(this.currentKey);
						ExpUtils.mylog(logName, this.logSign + line);
						System.out.println(this.logSign + line);

						if (reportMap.containsKey(this.currentKey)) {
							KeyReport fkr = (KeyReport) reportMap.get(this.currentKey);
							fkr.failTimes++;
							fkr.endDate = new Date().toString();
							reportMap.put(this.currentKey, fkr);
						}

					}
					continue;

				}
			}// while

			Date roundEndDate = new Date();
			String printLine = "Out of limit. Current Times:" + times
					+ ", TotalTimes: " + this.totalTimes + ", Stoptimes: "
					+ stopTimes + ", Start Date: " + roundStartDate.toString()
					+ ", End Date: " + roundEndDate.toString();

			System.out.println(this.logSign + printLine);
			ExpUtils.mylog(logName, this.logSign + printLine);
			System.out.println();
			System.out
					.println(this.logSign + "[Report]: " + reportMap.toString());
			ExpUtils.mylog(logName, "");
			ExpUtils.mylog(logName, this.logSign + reportMap.toString());
			ExpUtils.mylog(logName, "");
			System.out.println();

			int nowSleepTime = Utils.randomInt(retryTimeFrom, retryTimeTo);

			System.out.println(this.logSign + "Sleeping... : " + nowSleepTime
					+ " minutes.");
			ExpUtils.mylog(logName, this.logSign + "Sleeping... " + nowSleepTime
					+ " mimuites.");

			reportMap.clear();
			bannedKeysSet.clear();

			sleep(nowSleepTime * 60);
		}
	}

	private Map getParMap(String api) {
		// TODO Auto-generated method stub
		Map map = new HashMap();
		if (api.startsWith("place")) {
			Coordinates co = coods[Utils.randomInt(0, coods.length - 1)];
			nearbyuserPage++;
			if(nearbyuserPage>=20) nearbyuserPage = 1;
			map.put("lat", co.lat);
			map.put("long", co.longi);
			map.put("range", "11132");
			map.put("count", "50");
			map.put("page", String.valueOf(nearbyuserPage));
		}
		return map;
	}

	private String getNextAPI() {
		return apis[Utils.randomInt(apiFrom, apiTo)];
	}

	private String getNextAvailableKey() {
		//if the currentKey is not banned, return the current key
		if(!this.currentKey.equals("") && !bannedKeysSet.contains(this.currentKey)){
			return this.currentKey;
		}
		
		if(this.turnKey){
			Iterator it = keys.iterator();
			while (it.hasNext()) {
				String k = (String) it.next();
				if (!bannedKeysSet.contains(k)) {
					return k;
				}
			}
		}else{//random key
			int seq = Utils.randomInt(0, keys.size()-1);
			String k = (String) keys.get(seq);
			while(bannedKeysSet.contains(k)){
				seq = Utils.randomInt(0, keys.size()-1);
				k = (String) keys.get(seq);
			}
			return k;
		}

		return "";
	}

	private boolean hasNextAvailableKey() {
		return keys.size() > bannedKeysSet.size();
	}

	private int timeToNextHour() {
		Calendar now = Calendar.getInstance();
		int minutes = now.get(Calendar.MINUTE);
		int timeToNextHour = 60 + 2 - minutes;
		return timeToNextHour;
	}

	private void sleep(int i) {
		sleep(i,i);
	}

	private void sleep(int i, int k) {
		int t = Utils.randomInt(i, k);
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd MMM yyyy");
		Calendar cal = new GregorianCalendar();
		String now = sdf.format(cal.getTime());
		cal.add(Calendar.SECOND, t);
		String future = sdf.format(cal.getTime());
		String line =" Sleeping " + t / 60
				+ " minutes, or " + t + " seconds, from " + now + ", to " + future;
		System.out.println(this.logSign + line);
		ExpUtils.mylog(this.logName, line);
		try {
			Thread.sleep(t * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
