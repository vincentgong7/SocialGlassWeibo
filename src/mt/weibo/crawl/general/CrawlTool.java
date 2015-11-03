package mt.weibo.crawl.general;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import mt.weibo.common.AppKeyCenter;
import mt.weibo.common.MyLineReader;
import mt.weibo.common.Utils;
import mt.weibo.crawl.experiment.ExpUtils;
import mt.weibo.model.Coordinates;

public class CrawlTool {
	

	public static long timeToUnixTime(String strTime) {
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
	
	public static long timeToUnixTime(Date date){
		return date.getTime();
	}
	

	public static List<String> initAppkey(String fileName) {
		return initAppkey(fileName, 0, -1);
	}
	
	public static List<String> initAppkey(String fileName, int keyFrom, int keyTo) {
		List<String> appkeyList = new ArrayList<String>();
		MyLineReader mlr;
		try {
			int i = 0;
			mlr = new MyLineReader(fileName);
			if(keyTo<0){
				keyTo = Integer.MAX_VALUE;
			}
			while (mlr.hasNextLine() && i<keyTo) {
				String item = mlr.nextLine();
				if (!item.equals("") && !item.trim().startsWith("//")) {
					i++;
					if(i<keyFrom){
						continue;
					}
					appkeyList.add(item.trim());
				}
			}
			if (appkeyList.size() > 0) {
				return appkeyList;
			}
			mlr.close();
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<String>();
		}
		return appkeyList;
	}

	public static Coordinates[] initCoordinateArray(String fileName) {
		List<Coordinates> coordList = new ArrayList<Coordinates>();
		Coordinates[] coods = new Coordinates[0];
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
				coods = new Coordinates[coordList.size()];
				coods = coordList.toArray(coods);
			}
			mlr.close();
		} catch (Exception e) {
			e.printStackTrace();
			return coods;
		}
		return coods;
	}

	public static List<Coordinates> initCoordinateList(String fileName) {
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
			mlr.close();
		} catch (Exception e) {
			e.printStackTrace();
			return coordList;
		}
		return coordList;
	}

	public static boolean checkStartCrawlTime(long startCrawlTimeStamp) {
		Date now = new Date();
		long nowTimeStamp = now.getTime();
		if (nowTimeStamp < startCrawlTimeStamp) {
			return false;
		}
		return true;
	}

	public static boolean checkStopCrawlTime(long stopCrawlTimeStamp) {
		Date now = new Date();
		long nowTimeStamp = now.getTime();
		if (nowTimeStamp >= stopCrawlTimeStamp) {
			return true;
		} else {
			return false;
		}
	}

	public static int safeLongToInt(long l) {
		if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
			throw new IllegalArgumentException(l
					+ " cannot be cast to int without changing its value.");
		}
		return (int) l;
	}

	public static void sleep(int i, String logInitial) {
		sleep(i, i, logInitial);
	}

	public static void sleep(int i) {
		sleep(i, i);
	}

	public static void sleep(int i, int k) {
		sleep(i, i, "");
	}

	public static void sleep(int i, int k, String logInitial) {
		int t = Utils.randomInt(i, k);
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd MMM yyyy");
		Calendar cal = new GregorianCalendar();
		String now = sdf.format(cal.getTime());
		cal.add(Calendar.SECOND, t);
		String future = sdf.format(cal.getTime());
		String line = logInitial + " Sleeping " + t / 60 + " minutes, or " + t
				+ " seconds, from " + now + ", to " + future;
		System.out.println(line);
		// ExpUtils.mylog(splitFileNameByHour(this.logName), line);
		try {
			Thread.sleep(t * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static String getNextKey(List<String> keyList, String currentKey) {
		int keyID = keyList.indexOf(currentKey);
		if (keyID < 0 || keyID >= keyList.size()-1) {
			return keyList.get(0);
		} else {
			return keyList.get(keyID + 1);
		}
	}

	public static String splitFileNameByHour(String oriJsonlogName) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
		Calendar cal = new GregorianCalendar();
		String now = sdf.format(cal.getTime());

		// oriJsonlogName-2015052810.txt
		String[] fileName = oriJsonlogName.split("\\.");
		String result = fileName[0] + "-" + now + "." + fileName[1];

		return result;
	}

	public static List<String> initUidList(String uidFileName, String startingUid) {
		List<String> uidList = new ArrayList<String>();
		try {
			MyLineReader mlr = new MyLineReader(uidFileName);
			
			if(null!=startingUid && !"".equals(startingUid)){// if starting from certain uid
				while(mlr.hasNextLine()){
					String uid = mlr.nextLine().trim();
					if(uid.equals(startingUid)){
						uidList.add(uid);
						break;
					}
				}
			}
			
			while (mlr.hasNextLine()) {
				String uid = mlr.nextLine().trim();
				if ("" != uid && !"".equals(uid)) {
					uidList.add(uid);
				}
			}
			mlr.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return uidList;
	}

	public static String autoCrawl(String api, Map<String, String> map, String logName) {
		String json = "";
		String key = AppKeyCenter.getInstance().getNextKey();
		String line = "[" + api +"] " +  map.toString();
		ExpUtils.mylog(CrawlTool.splitFileNameByHour(logName), line);
		try {
			json = ExpUtils.crawlData(api, map, key);
		} catch (Exception e) {
			System.err.println("Something wrong when auto-crawling.");
			System.out.println(api);
			System.out.println(map.toString());
			e.printStackTrace();
		}
		
		return json;
	}

	public static String autoCrawl(String api, Map<String, String> map) {
		String json = "";
		String key = AppKeyCenter.getInstance().getNextKey();
		String line = "[" + api +"] " +  map.toString();
		System.out.println(line);
		try {
			json = ExpUtils.crawlData(api, map, key);
		} catch (Exception e) {
			System.err.println("Something wrong when auto-crawling.");
			System.out.println(api);
			System.out.println(map.toString());
			e.printStackTrace();
		}
		
		return json;
	}

}
