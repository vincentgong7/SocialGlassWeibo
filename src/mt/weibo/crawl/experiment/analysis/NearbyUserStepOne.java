package mt.weibo.crawl.experiment.analysis;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import mt.weibo.common.MyLineReader;
import mt.weibo.common.MyLineWriter;
import mt.weibo.model.UserJson;
import weibo4j.model.WeiboException;
import weibo4j.org.json.JSONArray;
import weibo4j.org.json.JSONException;
import weibo4j.org.json.JSONObject;

public class NearbyUserStepOne {
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) {
		// File folder = new
		// File("/Users/vincentgong/Documents/TUD/Master TUD/A Master Thesis/share/exp/publicTimeLine_json_0s_3pc/");

		File folder = new File(
				"/Users/vincentgong/Documents/TUD/Master TUD/A Master Thesis/share/IPX/mylog/analysis/json/nearbyuser/step0/");
		String outputFolder = "/Users/vincentgong/Documents/TUD/Master TUD/A Master Thesis/share/IPX/mylog/analysis/json/nearbyuser/step1/";

		NearbyUserStepOne nbs1 = new NearbyUserStepOne();

		if (folder.isDirectory()) {
			File[] files = folder.listFiles();
			for (File f : files) {// DataTest-key0-5s-Chelsea-nearbyuser-json.txt
				String fileName[] = f.getName().split("-");
				String key = fileName[1];
				String ip = fileName[3];
				String intervalTime = fileName[2];

				nbs1.processFile(f, key, intervalTime, ip, outputFolder);
			}
		} else {
			String fileName[] = folder.getName().split("-");
			String key = fileName[1];
			String ip = fileName[3];
			String intervalTime = fileName[2];
			
			nbs1.processFile(folder, key, intervalTime, ip, outputFolder);
		}
	}

	public void processFile(File f, String key, String intervalTime, String ip,
			String outputFolder) {
		int status = 0; // 0 = meta-data, 1 = data
		List<String> idList = null;
		String startDate = "";

		MyLineReader mlr;
		try {
			mlr = new MyLineReader(f);
			mlr.init();
			while (mlr.hasNextLine()) {
				String line = mlr.nextLine();

				if (line.trim().startsWith("[IPXv2]") && status == 0) { // generate
																		// the
																		// date
					// [IPXv2] [Rounds: 3, Date: Thu May 07 05:51:35 CEST 2015]
					status = 0; // meta-data

					int dateStartPosition = line.indexOf("Date: ");
					int endPosition = line.lastIndexOf("CEST");
					String strDate = line.substring(dateStartPosition + 6,
							endPosition - 1);
					strDate = strDate.replace(" ", "_");
					strDate = strDate.replace(":", "_");

					startDate = strDate;

				} else if (line.trim().startsWith("{")) {
					// json

					status = 1; // data
					List<UserJson> userList = null;
					userList = nearbyUser(line);
					if (userList.size() > 0) {
						if (idList == null) {
							idList = new ArrayList<String>();
						}
						for (UserJson uj : userList) {
							idList.add(uj.getUser_id());
						}
					}
				} else if (line.trim().startsWith("[IPXv2]") && status == 1) {
					status = 0;
					StringBuffer sb = new StringBuffer();
					for (String id : idList) {
						sb.append(id);
						sb.append(":");
					}
					String fileName = startDate + "-" + key + "-" + ip + "-"
							+ intervalTime + ".csv";
					MyLineWriter.getInstance().writeLine(
							outputFolder + fileName, sb.toString());

					idList = null;

					int dateStartPosition = line.indexOf("Date: ");
					int endPosition = line.lastIndexOf("CEST");
					String strDate = line.substring(dateStartPosition + 6,
							endPosition - 1);
					strDate = strDate.replace(" ", "_");
					strDate = strDate.replace(":", "_");

					startDate = strDate;
				}
			}
			mlr.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	public static List<UserJson> nearbyUser(String json) {

		List<UserJson> userList = new ArrayList<UserJson>();

		JSONObject jsonUser;
		JSONArray users = null;

		try {
			System.out.println(json);
			jsonUser = new JSONObject(json); // asJSONArray();

			if (!jsonUser.isNull("users")) {
				users = jsonUser.getJSONArray("users");
			}
			int size = users.length();
			for (int i = 0; i < size; i++) {
				userList.add(new UserJson(users.getJSONObject(i)));
			}
		} catch (JSONException jsone) {
		}

		return userList;
	}
}
