/**
 * 
 */
package mt.weibo.crawl.experiment.aipx;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

import mt.weibo.common.MyLineReader;
import mt.weibo.common.Utils;
import mt.weibo.crawl.experiment.ExpUtils;
import mt.weibo.crawl.experiment.aipx.model.KeyReport;
import mt.weibo.model.Coordinates;
import weibo4j.model.WeiboException;

/**
 * @author vincentgong
 *
 */
public class IPXv3 {

	private String logName = "log.txt";
	private String JsonlogName = "jsonlog.txt";

	private String apiFileName = "api.txt";
	private String coordinatFileName = "coordinates.txt";
	private String appkeyFileName = "appkey.txt";

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

	private long startCrawlTimeStamp = 0;
	private long stopCrawlTimeStamp = 0;

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
	private Coordinates currentCoordinate;

	private Coordinates[] coods = { new Coordinates("52.374192", "4.901189"),
			new Coordinates("39.905502", "116.397632"),
			new Coordinates("30.658676", "104.066877"),
			new Coordinates("31.248274", "121.453407"),
			new Coordinates("22.546560", "114.065123"),
			new Coordinates("23.127808", "113.267927"),
			new Coordinates("31.294030", "120.586538"),
			new Coordinates("34.277626", "108.922817"),// 西安
			new Coordinates("34.910396", "108.961372"),// 银川
			new Coordinates("29.561249", "106.460470"),// 重庆沙坪坝
			new Coordinates("29.413468", "106.541322"),// 重庆南
			new Coordinates("29.505390", "106.598142"),// 重庆东
			new Coordinates("29.594952", "103.485296"),// 峨眉山市
			new Coordinates("30.724728", "103.970889"),// 成都高新西区
			new Coordinates("30.632877", "104.058700"),// 玉林
			new Coordinates("30.051259", "103.834115"),// 眉山
			new Coordinates("32.068726", "118.775173"),// 南京
			new Coordinates("30.280367", "120.151634"),// 杭州
			new Coordinates("22.280589", "114.158359"),// 香港
			new Coordinates("30.614220", "114.301071"),// 武汉
			new Coordinates("43.829278", "125.320584"),// 长春
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
	private String logSign = " [IPXv3] ";
	private boolean emptyResultGot;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		IPXv3 ipxv3 = new IPXv3();
		ipxv3.setup(args[0]);
		ipxv3.process();
	}

	private void setup(String configFileName) {
		InputStream is = null;
		try {
			is = new FileInputStream(Utils.getPath() + "/" + configFileName);
			Properties config = new Properties();
			config.load(is);

			// get the config item
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
			this.apiFileName = config.getProperty("api_file_name");
			this.appkeyFileName = config.getProperty("app_key_file_name");
			this.coordinatFileName = config.getProperty("coordinate_file_name");

			this.startCrawlTimeStamp = timeToUnixTime(config
					.getProperty("start_crawl_time"));
			this.stopCrawlTimeStamp = timeToUnixTime(config
					.getProperty("stop_crawl_time"));

			// init the api, appkey, coordinate
			this.apis = initApi(Utils.getPath() + "/" + this.apiFileName);
			initAppkey(Utils.getPath() + "/" + this.appkeyFileName);
			this.coods = initCoordinate(Utils.getPath() + "/"
					+ this.coordinatFileName);

			// preprocess the config items
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

	private long timeToUnixTime(String strTime) {
		// 2015-05-29 00:00:01
		SimpleDateFormat parser = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
		Date date = new Date();
		try {
			date = parser.parse(strTime);
			return date.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
			return 0;
		}
	}

	private Coordinates[] initCoordinate(String fileName) {
		List<Coordinates> coordList = new ArrayList<Coordinates>();
		try {
			MyLineReader mlr = new MyLineReader(fileName);
			while (mlr.hasNextLine()) {
				String item = mlr.nextLine();
				if (item == null || item.equals("")) {
					continue;
				}
				coordList.add(new Coordinates(item));
			}
			if (coordList.size() > 0) {
				this.coods = new Coordinates[coordList.size()];
				this.coods = coordList.toArray(this.coods);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return this.coods;
		}
		return this.coods;
	}

	private void initAppkey(String fileName) {
		List<String> appkeyList = new ArrayList<String>();
		MyLineReader mlr;
		try {
			mlr = new MyLineReader(fileName);
			while (mlr.hasNextLine()) {
				String item = mlr.nextLine();
				if (!item.equals("") && !item.trim().startsWith("//")) {
					appkeyList.add(item.trim());
				}
			}
			if (appkeyList.size() > 0) {
				Utils.setAccessTokenList(appkeyList);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

	}

	private String[] initApi(String fileName) {
		try {
			List<String> apiList = new ArrayList<String>();
			MyLineReader mlr = new MyLineReader(fileName);
			while (mlr.hasNextLine()) {
				String item = mlr.nextLine();
				if (!item.equals("") && !item.trim().startsWith("//")) {
					apiList.add(item);
				}
			}
			if (apiList.size() > 0) {
				this.apis = new String[apiList.size()];
				this.apis = apiList.toArray(this.apis);
			}
			mlr.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return this.apis;
		}
		return this.apis;
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
		// check start crawl time
		if (!checkStartCrawlTime()) {// wait to start
			Long nowTimeStamp = new Date().getTime();
			int waitTimeSecond = safeLongToInt((this.startCrawlTimeStamp - nowTimeStamp) / 1000);
			System.out.println(this.logSign + "Sleep a while to start: "
					+ waitTimeSecond + "s, or:" + waitTimeSecond / 60 + "m.");
			sleep(waitTimeSecond);
		}

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
				ExpUtils.mylogJson(splitFileNameByHour(JsonlogName),
						this.logSign + jsonLog);
			}
			int times = 0;
			while (times < runTimes && hasNextAvailableKey()) {

				// check if it is time to stop
				boolean stopCrawl = checkStopCrawlTime();
				if (stopCrawl) {
					// report this round
					Date roundEndDate = new Date();
					String stopTimeNowLine = "Time to stop. Current Times:"
							+ times + ", TotalTimes: " + this.totalTimes
							+ ", Stoptimes: " + this.stopTimes
							+ ", Start Date: " + roundStartDate.toString()
							+ ", End Date: " + roundEndDate.toString();
					reportRound(stopTimeNowLine);
					return;
				}

				this.currentKey = getNextAvailableKey();
				keyID = Utils.getKeyID(this.currentKey);

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

				String printLine = " Times in round:" + times
						+ ", app-key times:" + kr.requestTimes + ", api: "
						+ api + ", map:" + map.toString() + ", key: "
						+ this.currentKey + ", key_id:" + this.keyID
						+ ", Date: " + now.toString();
				System.out.println(this.logSign + printLine);
				ExpUtils.mylog(splitFileNameByHour(logName), this.logSign
						+ printLine);

				// interval calling sleep
				System.out
						.println(this.logSign + "This is the times: " + times);
				sleep(reqIntervalTimeFrom, reqIntervalTimeTo);

				// crawl the data
				try {
					String result = ExpUtils.crawlData(api, map,
							this.currentKey);
					if ("[]".equals(result)) {
						this.emptyResultGot = true;
					} else {
						this.emptyResultGot = false;
					}
					if (recordJson) {
						ExpUtils.mylogJson(splitFileNameByHour(JsonlogName),
								result);
					}
					if (reportMap.containsKey(this.currentKey)) {
						KeyReport skr = (KeyReport) reportMap
								.get(this.currentKey);
						skr.successTimes++;
						skr.endDate = new Date().toString();
						reportMap.put(this.currentKey, skr);

						// 150 times per key, then put it into banned list for
						// faster recovery
						if (skr.requestTimes >= 150) {
							String outOf150TimesLine = "150 request times! Let this key take a break. Cureent times: "
									+ times
									+ ", Total times: "
									+ this.totalTimes
									+ ", Stop Times: "
									+ this.stopTimes
									+ ", key_id: "
									+ keyID
									+ ", key: " + this.currentKey;
							// put the current key into banned set
							this.bannedKeysSet.add(this.currentKey);
							ExpUtils.mylog(splitFileNameByHour(logName),
									this.logSign + outOf150TimesLine);
							System.out
									.println(this.logSign + outOf150TimesLine);
						}
					}
				} catch (WeiboException e) {
					System.out.println(this.logSign + "Error Code: "
							+ e.getErrorCode() + ". Error: " + e.getError());
					System.out.println(this.logSign + e.getMessage());
					ExpUtils.mylog(splitFileNameByHour(logName), this.logSign
							+ " Error Code: " + e.getErrorCode() + ". Error: "
							+ e.getError());
					ExpUtils.mylog(splitFileNameByHour(logName), this.logSign
							+ e.getMessage());

					if (e.getMessage().contains(
							"User requests out of rate limit")) {
						this.stopTimes++;

						// write the report
						KeyReport fkr = (KeyReport) reportMap
								.get(this.currentKey);
						fkr.failTimes++;
						fkr.endDate = new Date().toString();
						reportMap.put(this.currentKey, fkr);

						String outOfLimitLine = "Out of limit! Cureent times in round: "
								+ times
								+ ", app-key times: "
								+ fkr.requestTimes
								+ ", Total times: "
								+ this.totalTimes
								+ ", Stop Times: "
								+ stopTimes
								+ ", key_id: "
								+ keyID
								+ ", key: "
								+ this.currentKey;
						// put the current key into banned set
						this.bannedKeysSet.add(this.currentKey);
						ExpUtils.mylog(splitFileNameByHour(logName),
								this.logSign + outOfLimitLine);
						System.out.println(this.logSign + outOfLimitLine);

					}
					continue;
				}
			}// while

			// report this round
			Date roundEndDate = new Date();
			String printLine = "Out of limit. Current Times:" + times
					+ ", TotalTimes: " + this.totalTimes + ", Stoptimes: "
					+ this.stopTimes + ", Start Date: "
					+ roundStartDate.toString() + ", End Date: "
					+ roundEndDate.toString();
			reportRound(printLine);

			// System.out.println(this.logSign + printLine);
			// ExpUtils.mylog(splitFileNameByHour(logName), this.logSign
			// + printLine);
			// System.out.println();
			// System.out.println(this.logSign + "[Report]: "
			// + reportMap.toString());
			// ExpUtils.mylog(splitFileNameByHour(logName), "");
			// ExpUtils.mylog(splitFileNameByHour(logName), this.logSign
			// + reportMap.toString());
			// ExpUtils.mylog(splitFileNameByHour(logName), "");
			// System.out.println();

			// sleep
			int nowSleepTime = Utils.randomInt(retryTimeFrom, retryTimeTo);

			System.out.println(this.logSign + "Sleeping... : " + nowSleepTime
					+ " minutes.");
			ExpUtils.mylog(splitFileNameByHour(logName), this.logSign
					+ "Sleeping... " + nowSleepTime + " mimuites.");

			reportMap.clear();
			bannedKeysSet.clear();

			sleep(nowSleepTime * 60);
		}
	}

	private boolean checkStartCrawlTime() {
		Date now = new Date();
		long nowTimeStamp = now.getTime();
		if (nowTimeStamp < this.startCrawlTimeStamp) {
			return false;
		}
		return true;
	}

	private boolean checkStopCrawlTime() {
		Date now = new Date();
		long nowTimeStamp = now.getTime();
		if (nowTimeStamp >= this.stopCrawlTimeStamp) {
			return true;
		} else {
			return false;
		}
	}

	private void reportRound(String printLine) {

		System.out.println(this.logSign + printLine);
		ExpUtils.mylog(splitFileNameByHour(logName), this.logSign + printLine);
		System.out.println();
		System.out.println(this.logSign + "[Report]: " + reportMap.toString());
		ExpUtils.mylog(splitFileNameByHour(logName), "");
		ExpUtils.mylog(splitFileNameByHour(logName),
				this.logSign + reportMap.toString());
		ExpUtils.mylog(splitFileNameByHour(logName), "");
		System.out.println();
	}

	private Map getParMap(String api) {
		// TODO Auto-generated method stub
		Map map = new HashMap();
		if (api.startsWith("place")) {
			Coordinates co;
			if (this.currentCoordinate == null) {
				this.currentCoordinate = coods[Utils.randomInt(0,
						coods.length - 1)];
			}
			if (this.emptyResultGot) {
				this.currentCoordinate.page = 0;
				co = coods[Utils.randomInt(0, coods.length - 1)];
			} else if (this.currentCoordinate.page < 20) {// continue this
															// coordinate
				co = this.currentCoordinate;
			} else {// find another coordinate
				this.currentCoordinate.page = 0;
				co = coods[Utils.randomInt(0, coods.length - 1)];
			}
			co.page++;
			if (co.page > 20) {
				co.page = 1;
			}
			map.put("lat", co.lat);
			map.put("long", co.longi);
			map.put("range", co.radius);
			map.put("count", "50");
			map.put("page", String.valueOf(co.page));
			this.currentCoordinate = co;
		}
		return map;
	}

	private String getNextAPI() {
		return apis[Utils.randomInt(apiFrom, apiTo)];
	}

	private String getNextAvailableKey() {
		// if the currentKey is not banned, return the current key
		if (!this.currentKey.equals("")
				&& !bannedKeysSet.contains(this.currentKey)) {
			return this.currentKey;
		}

		if (this.turnKey) {
			Iterator it = keys.iterator();
			while (it.hasNext()) {
				String k = (String) it.next();
				if (!bannedKeysSet.contains(k)) {
					return k;
				}
			}
		} else {// random key
			int seq = Utils.randomInt(0, keys.size() - 1);
			String k = (String) keys.get(seq);
			while (bannedKeysSet.contains(k)) {
				seq = Utils.randomInt(0, keys.size() - 1);
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
		sleep(i, i);
	}

	private void sleep(int i, int k) {
		int t = Utils.randomInt(i, k);
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd MMM yyyy");
		Calendar cal = new GregorianCalendar();
		String now = sdf.format(cal.getTime());
		cal.add(Calendar.SECOND, t);
		String future = sdf.format(cal.getTime());
		String line = " Sleeping " + t / 60 + " minutes, or " + t
				+ " seconds, from " + now + ", to " + future;
		System.out.println(this.logSign + line);
		ExpUtils.mylog(splitFileNameByHour(this.logName), line);
		try {
			Thread.sleep(t * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private String splitFileNameByHour(String oriJsonlogName) {
		// TODO Auto-generated method stub

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
		Calendar cal = new GregorianCalendar();
		String now = sdf.format(cal.getTime());

		// oriJsonlogName-2015052810.txt
		String[] fileName = oriJsonlogName.split("\\.");
		String result = fileName[0] + "-" + now + "." + fileName[1];

		return result;
	}

	public int safeLongToInt(long l) {
		if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
			throw new IllegalArgumentException(l
					+ " cannot be cast to int without changing its value.");
		}
		return (int) l;
	}

}
