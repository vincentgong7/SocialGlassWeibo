/**
 * 
 */
package mt.weibo.crawl.experiment.aipx;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import mt.weibo.common.Utils;
import mt.weibo.crawl.experiment.ExpUtils;
import mt.weibo.model.Coordinates;
import weibo4j.model.WeiboException;

/**
 * @author vincentgong
 *
 */
// Sleep 1 hour and try
public class SingleKeyRecoveryTime2 {

	private String logName = "log.txt";
	private Integer keysFrom = 0;
	private Integer keysTo;
	private Integer sleepTimeFrom = 0;
	private Integer sleepTimeTo;
	private Integer apiFrom;
	private Integer apiTo;
	private boolean recordJson = false;
	private int runTimes = 100;
	private boolean turnKey = false;
	private int stopTimes = 0;
	private int totalTimes = 0;
	public Date startDate;
	public Date endDate;
	private Set bannedKeysSet;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SingleKeyRecoveryTime2 ipx = new SingleKeyRecoveryTime2();

		// command: -key,3,5 -api,2,2 -sleep,2,5 -log,IPTest.txt -json,1
		// -times,0
		// 0 times means unlimited times
		// -key,-1,x means use the key in turn: from first one the last one

		String[] keyRange = args[0].split(",");
		String[] apiRange = args[1].split(",");
		String[] sleepTimeRange = args[2].split(",");
		String[] logName = args[3].split(",");
		String[] recordJson = args[4].split(",");
		String[] runTimes = args[5].split(",");

		ipx.setKeys(keyRange[1], keyRange[2]);
		ipx.setApis(apiRange[1], apiRange[2]);
		ipx.setSleepTime(sleepTimeRange[1], sleepTimeRange[2]);
		ipx.setLogName(logName[1]);
		ipx.setRecordJson(recordJson[1]);
		ipx.setRunTimes(runTimes[1]);
		ipx.startDate = new Date();
		ipx.bannedKeysSet = new HashSet();

		ipx.process();
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
		this.sleepTimeFrom = Integer.valueOf(sleepFrom);
		this.sleepTimeTo = Integer.valueOf(sleepTo);
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
		String[] apis = { "statuses/public_timeline.json",
				"tags/suggestions.json", "suggestions/users/hot.json",
				"common/get_timezone.json", "place/nearby_timeline.json",
				"place/nearby/users.json",
		// "emotions.json",
		// "trends/daily",
		// "trends/weekly.json",
		// "trends/hourly.json",
		};

		if (apiFrom < 0 || apiFrom > apis.length - 1) {
			apiFrom = 0;
		}
		if (apiTo < 0 || apiTo > apis.length - 1) {
			apiTo = apis.length - 1;
		}

		String keys[] = Utils.getAccessTokenList();

		if (keysFrom < 0) {
			this.turnKey = true;
		} else if (keysFrom > keys.length) {
			keysFrom = 0;
		}

		if (keysTo < 0 || keysTo > keys.length - 1) {
			keysTo = keys.length - 1;
		}

		Coordinates[] coods = { new Coordinates("52.374192", "4.901189"),
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

		int times = 0;
		while (times < this.runTimes) {
			sleep(sleepTimeFrom, sleepTimeTo);
			times++;
			this.totalTimes++;
			String key = "";

			int keyTurn = 0;
			if (this.turnKey) {
				keyTurn = times % keys.length;
			} else {
				keyTurn = Utils.randomInt(keysFrom, keysTo);
			}

			int judgeTimes = 0;
			while (this.bannedKeysSet.contains(keyTurn)) {
				judgeTimes++;
				keyTurn++;
				if (keyTurn == keys.length) {
					keyTurn = 0;
				}
				if (this.turnKey) {
					if (judgeTimes >= keys.length) {
						sleep(63 * 60);
						judgeTimes = 0;
						this.bannedKeysSet.clear();
						times = 1;
						keyTurn = 0;
					}
				} else {
					if (judgeTimes >= this.keysTo - this.keysFrom + 1) {
						System.out.println("Sleeping.....");
						ExpUtils.mylog(logName, "Sleeping.....");
						sleep(63 * 60);
						judgeTimes = 0;
						this.bannedKeysSet.clear();
						times = 1;
						keyTurn = this.keysFrom;
					}
				}
			}

			key = keys[keyTurn];
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
			String currentStatus = "Times:" + times + ", Total times: "+ this.totalTimes +", api: " + api
					+ ", map:" + map.toString() + ", key: " + key + ", Date: "
					+ date.toString();
			ExpUtils.mylog(logName, currentStatus);

			System.out.println(currentStatus);

			try {
				String result = ExpUtils.crawlData(api, map, key);
				if (recordJson) {
					ExpUtils.mylog(logName, result);
				}
			} catch (WeiboException e) {
				System.out.println(e.getError());
				System.out.println(e.getMessage());
				ExpUtils.mylog(logName, e.getError());
				ExpUtils.mylog(logName, e.getMessage());
				if (e.getMessage().contains("limit")) {
					this.stopTimes++;
					String line = "Out of limit! Cureent times: " + times
							+ ", Total times: " + this.stopTimes + ", key_id: "
							+ keyTurn + ", key: " + key;
					this.bannedKeysSet.add(keyTurn);
					ExpUtils.mylog(logName, line);
					System.out.println(line);

					if (times == 1) {
						endDate = new Date();
						String outIpline = "Out of limit of IP. TotalTimes: "
								+ this.totalTimes + ", Start Date: "
								+ this.startDate.toString() + ", End Date: "
								+ this.endDate.toString();
						System.out.println(outIpline);
						ExpUtils.mylog(logName, outIpline);
						outIpline = "Tried again now, but failed. Sleep again.";
						System.out.println(outIpline);
						ExpUtils.mylog(logName, outIpline);
					}
					continue;
				} else {
					continue;
				}
				// e.printStackTrace();

			}
		}

	}

	private int timeToNextHour() {
		Calendar now = Calendar.getInstance();
		int minutes = now.get(Calendar.MINUTE);
		int timeToNextHour = 60 + 2 - minutes;
		return timeToNextHour;
	}

	private void sleep(int i) {
		try {
			System.out.println("Sleeping " + i/60 + " minutes.");
			Thread.sleep(i * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void sleep(int i, int k) {
		int t = Utils.randomInt(i, k);
		try {
			Thread.sleep(t * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
