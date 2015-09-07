package mt.weibo.crawl.general;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import mt.weibo.common.MyLineReader;
import mt.weibo.common.Utils;
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

	public static List<String> initUidList(String uidFileName) {
		// TODO Auto-generated method stub
		List<String> uidList = new ArrayList<String>();
		try {
			MyLineReader mlr = new MyLineReader(uidFileName);
			while (mlr.hasNextLine()) {
				String uid = mlr.nextLine();
				if ("" != uid && !"".equals(uid)) {
					uidList.add(uid);
				}
			}
			mlr.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return uidList;
	}

	public static String getNextUid(List<String> uidList, String currentUid) {
		// TODO Auto-generated method stub
		int uidID = uidList.indexOf(currentUid);
		uidID ++;
		if(uidID<0 || uidID > uidList.size()){
			uidID = 0;
		}
		return uidList.get(uidID);
	}

}
