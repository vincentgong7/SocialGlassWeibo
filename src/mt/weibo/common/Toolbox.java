/**
 * 
 */
package mt.weibo.common;

import java.util.Date;
import java.util.Map;

import weibo4j.http.HttpClient;
import weibo4j.http.Response;
import weibo4j.model.PostParameter;
import weibo4j.model.WeiboException;
import weibo4j.util.ArrayUtils;
import weibo4j.util.WeiboConfig;

/**
 * @author Vinent GONG
 *
 */
public class Toolbox {

	protected static HttpClient client = new HttpClient();
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public static String crawlData(String api, Map<String, String> map) {
		String result = "";
		PostParameter[] parList = ArrayUtils.mapToArray(map);
		Response res;
		String access_token = Utils.randomAccessToken();
		try {
			res = client.get(WeiboConfig.getValue("baseURL")
					+ api, parList, access_token);
			if (res != null && !res.equals("")) {
				result = res.asString();
			}
		} catch (WeiboException e) {
			System.out.println("Something wrong with the process of API calling.");
			
			e.printStackTrace();
		}
		
		return result;
	}

	public static void saveDataToFile(String content, String filename) {
		try {
			//delete the first "/" in case the filename starts with
			if(filename.startsWith("/")) filename = filename.substring(1);
			String fullFileName = Utils.getResourceFilePath() + Utils.RESOURCE_FOLDER  + filename;
			MyLineWriter.getInstance().writeLine(fullFileName , content);
		} catch (Exception e) {
			System.out.println("Wrong when write to the file: " + filename);
			e.printStackTrace();
		}
	}
	
	public static void Sleep(int seconds) {
		// TODO Auto-generated method stub
		int s = 10;
		if (seconds > 0)
			s = seconds;

		try {
			System.out.println();
			Date d = new Date();
			System.out.println(d.toString());
			System.out.println("Sleeping for " + s / 60
					+ " minutes...zzzzzzzzzz");
			System.out.println();
			Thread.sleep(s * 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
