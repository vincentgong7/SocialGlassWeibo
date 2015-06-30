/**
 * 
 */
package mt.weibo.crawl.experiment;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.Map;

import mt.weibo.common.MyLineWriter;
import mt.weibo.common.MyStringBuffer;
import mt.weibo.common.Utils;
import weibo4j.http.HttpClient;
import weibo4j.http.Response;
import weibo4j.model.PostParameter;
import weibo4j.model.WeiboException;
import weibo4j.util.ArrayUtils;
import weibo4j.util.WeiboConfig;

/**
 * @author vincentgong
 *
 */
public class ExpUtils {

	protected static HttpClient client = new HttpClient();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		coordinatesGenerator(args[0], args[1] , args[2]);
//		coordinatesGenerator(Utils.getPath() + "/coordinates.txt", "39.437440,115.383970" , "40.058288,117.026426");
	}

	public static String crawlData(String api, Map<String, String> map)
			throws WeiboException {
		String key = Utils.randomAccessToken();
		return crawlData(api, map, key);
	}

	public static String crawlData(String api, Map<String, String> map,
			String key) throws WeiboException {
		String result = "";
		PostParameter[] parList = ArrayUtils.mapToArray(map);
		Response res;
		String access_token = key;

		res = client.get(WeiboConfig.getValue("baseURL") + api, parList,
				access_token);
		if (res != null && !res.equals("")) {
			result = res.asString();
		}
		return result;
	}

	public static void mylog(String logName, String line) {
		try {
			Date date = new Date();
			line = date.toString() + ": " + line;
			MyLineWriter.getInstance().writeLine(
					Utils.getPath() + "/mylog/" + logName, line);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void mylog(String line) {
		mylog("log.txt", line);
	}

	public static void mylogJson(String jsonlogName, String jsonLog) {
		// TODO Auto-generated method stub
		try {
			MyLineWriter.getInstance().writeLine(
					Utils.getPath() + "/mylog/" + jsonlogName, jsonLog);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void coordinatesGenerator(String outputFilename, String poA, String poB){
		double Ax = Double.valueOf(poA.split(",")[1]);
		double Ay = Double.valueOf(poA.split(",")[0]);
		
		double Bx = Double.valueOf(poB.split(",")[1]);
		double By = Double.valueOf(poB.split(",")[0]);
		
		int xtimes = (int) Math.ceil((Bx - Ax)*10);
		int ytimes = (int) Math.ceil((By - Ay)*10);
		
		MyStringBuffer msb = new MyStringBuffer();
		DecimalFormat df = new DecimalFormat("#.#####");
		
		for(int i=0;i<=xtimes;i++){
			double pointx = Double.parseDouble(df.format(Ax + i*0.1));
			for(int t = 0; t<=ytimes; t++){
				double pointy = Ay + Double.valueOf(t)*0.1;
				pointy = Double.parseDouble(df.format(Ay + t*0.1));
				String line = pointy + "," + pointx + "," + "11132";
				msb.appendLine(line);
			}
		}
		try {
			MyLineWriter.getInstance().writeLine(outputFilename, msb.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
