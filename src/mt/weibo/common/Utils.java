/**
 * 
 */
package mt.weibo.common;

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import weibo4j.org.json.JSONException;
import weibo4j.org.json.JSONObject;

/**
 * @author vincentgong
 *
 */
public class Utils {

	public static String[] ACCESS_TOKEN = {
//			"2.00owJ9cFkKE_IEbce3361c680qNh_x",
//			"2.002bnvMC0AC7_J7e09e31a95VZiKRC",
//			"2.002bnvMC7F_iCDa8e0d10b080xPTzE",
//			"2.002bnvMCoNu6rD44e681008c3k4GAD",
	};

	public static final String RESOURCE_FOLDER = "resource/";
	public static final String STORE_FOLDER = "store/";
	public static final String UIDS_FOLDER = "uids/";
	public static final String USER_LBS_FOLDER = "userlbs/";
	public static final String UESER_TIMELINE_FOLDER = "user_timeline/";
	public static final String DB_NAME = "socialmedia.";

	public static final String PUBLIC_TIMELINE_FOLDER = "public_timeline/";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println(Utils.getResourceFilePath());
		for (int i = 0; i < 20; i++) {
			// System.out.println(randomAccessToken());
			randomAccessToken();
		}
	}

	// get the poiid from the annotation if it has
	public static String parsePoiid(String annotations) {
		try {
			if (annotations != null && !annotations.equals("")
					&& annotations.contains("poiid")) {
				JSONObject json = new JSONObject(annotations.substring(1,
						annotations.length() - 1));
				if (json.has("place")) {
					Object obj = json.get("place");
					if (obj != null) {
						String place = obj.toString();
						if (place != null && !place.equals("")) {
							JSONObject placeJson = new JSONObject(place);
							if (placeJson.has("poiid")) {
								String poiid = placeJson.get("poiid")
										.toString();
								return poiid;
							}
						}
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "0";
	}

	public static long getUnixTimeStamp(String createdat_origin) {

		// createdat_origin = "Tue Apr 21 16:48:23 +0800 2015";
		SimpleDateFormat formatter = new SimpleDateFormat(
				"EEE MMM dd HH:mm:ss zzzz yyyy");
		Date date = new Date();
		try {
			date = formatter.parse(createdat_origin);
			System.out.println(date.getTime());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date.getTime();
	}

	public static int randomInt(int min, int max) {
		
		if(min == max){
			return min;
		}
		// NOTE: Usually this should be a field rather than a method
		// variable so that it is not re-seeded every call.
		Random rand = new Random();

		// nextInt is normally exclusive of the top value,
		// so add 1 to make it inclusive
		int randomNum = rand.nextInt((max - min) + 1) + min;

		return randomNum;
	}

	public static String getResourceFilePath() {
		// TODO Auto-generated method stub
		String relativePath = Utils.class.getResource("/").getFile();

		return relativePath;
	}

	public static long getUnixTime(String strDate, String format) {
		// strDate = "Fri Apr 24 11:01:01 +0800 2015";
		// formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzzz yyyy");
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		Date date;
		try {
			date = formatter.parse(strDate);
			return date.getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println(date.getTime());
		return 0;
	}

	public static long getUnixTime(String strDate) {
		return getUnixTime(strDate, "EEE MMM dd HH:mm:ss zzzz yyyy");
	}

	public static String randomAccessToken() {
		int i = Utils.randomInt(0, Utils.ACCESS_TOKEN.length - 1);
		System.out.println(i);
		return Utils.ACCESS_TOKEN[i];
	}

	public static String[] getAccessTokenList() {
		// TODO Auto-generated method stub
		return ACCESS_TOKEN;
	}

	public static String getPath() {
		URL url = Utils.class.getProtectionDomain().getCodeSource()
				.getLocation();
		String filePath = null;
		try {
			filePath = URLDecoder.decode(url.getPath(), "utf-8");// 转化为utf-8编码
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (filePath.endsWith(".jar")) {// 可执行jar包运行的结果里包含".jar"
			// 截取路径中的jar包名
			filePath = filePath.substring(0, filePath.lastIndexOf("/") + 1);
		}

		File file = new File(filePath);

		// /If this abstract pathname is already absolute, then the pathname
		// string is simply returned as if by the getPath method. If this
		// abstract pathname is the empty abstract pathname then the pathname
		// string of the current user directory, which is named by the system
		// property user.dir, is returned.
		filePath = file.getAbsolutePath();// 得到windows下的正确路径
		return filePath;
	}

	public static List getAccessTokenList(Integer keysFrom, Integer keysTo) {
		List list = new ArrayList();

		if (keysTo >= ACCESS_TOKEN.length) {
			keysTo = ACCESS_TOKEN.length;
		}
		if (keysFrom < 0) {
			keysFrom = 0;
			keysTo = ACCESS_TOKEN.length;
		}
		
		int i = -1;
		for (String key : ACCESS_TOKEN) {
			i++;
			if(i>=keysFrom && i<=keysTo){
				list.add(key);
			}
		}
		return list;
	}
	
	public static void setAccessTokenList(List<String> keys){
		if(keys.size()>0){
			ACCESS_TOKEN = new String[keys.size()];
			ACCESS_TOKEN = keys.toArray(ACCESS_TOKEN);
		}
	}

	public static int getKeyID(String key) {
		// TODO Auto-generated method stub
		int i = 0;
		for(String k : ACCESS_TOKEN){
			if(key!=null && k.equals(key)){
				return i;
			}
			i++;
		}
		return 0;
	}

}
