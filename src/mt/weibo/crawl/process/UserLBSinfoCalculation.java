/**
 * 
 */
package mt.weibo.crawl.process;

import java.io.File;

import mt.weibo.common.MyLineReader;
import mt.weibo.common.Toolbox;
import mt.weibo.common.Utils;
import weibo4j.org.json.JSONException;
import weibo4j.org.json.JSONObject;

/**
 * @author Vinent GONG
 *
 */
public class UserLBSinfoCalculation {

	public String lbsFolderPath;
	public String outputFile;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		UserLBSinfoCalculation lbsc = new UserLBSinfoCalculation();
		lbsc.lbsFolderPath = Utils.USER_LBS_FOLDER;
		lbsc.outputFile = Utils.UESER_TIMELINE_FOLDER + "user_id_amount.txt";
		lbsc.calculate();
	}

	public void calculate() {
		// TODO Auto-generated method stub
		File f = new File(Utils.getResourceFilePath() + Utils.RESOURCE_FOLDER + lbsFolderPath);
		if (f.isDirectory()) {
			File[] fileArray = f.listFiles();
			for (File file : fileArray) {
				try {
					MyLineReader mlr = new MyLineReader(file);
					mlr.init();
					while (mlr.hasNextLine()) {
						String line = mlr.nextLine();
						String idAndAmount = calAmountOfPost(line);
						if (idAndAmount != null && !idAndAmount.equals("")) {
							Toolbox.saveDataToFile(idAndAmount, outputFile);
						}
					}
					mlr.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	}

	private String calAmountOfPost(String line) {
		// TODO Auto-generated method stub
		String result = "";
		if (line != null && !line.equals("")) {
			try {
				String uid = null, totoalGeoPost = null;
				JSONObject jo = new JSONObject(line);
				if (jo.has("uid")) {
					uid = jo.getString("uid");
				}
				if (jo.has("geo_statuses_num")) {
					totoalGeoPost = jo.getString("geo_statuses_num");
				}
				if (uid != null && totoalGeoPost != null) {
					result = uid + "," + totoalGeoPost;
					System.out.println(result);
					return result;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return null;
	}

}
