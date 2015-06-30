package mt.weibo.crawl.experiment.analysis;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import mt.weibo.common.MyLineReader;
import mt.weibo.common.MyLineWriter;
import weibo4j.model.Status;
import weibo4j.model.StatusWapper;
import weibo4j.model.WeiboException;
import weibo4j.org.json.JSONArray;
import weibo4j.org.json.JSONException;
import weibo4j.org.json.JSONObject;

public class PublicStepOne {
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) {
		File folder = new File("/Users/vincentgong/Documents/TUD/Master TUD/A Master Thesis/share/IPX/mylog/analysis/json/public/step0/");
		String outputFolder = "/Users/vincentgong/Documents/TUD/Master TUD/A Master Thesis/share/IPX/mylog/analysis/json/public/step1/";
		
		PublicStepOne ps1 = new PublicStepOne();
		
		if(folder.isDirectory()){
			File[] files = folder.listFiles();
			for(File f: files){
				String fileName[] = f.getName().split("-"); //DataTest-key20-2s-ip-public-json.txt"
				String key = fileName[1];//"20";
				String ip = fileName[3];
				String intervalTime = fileName[2];
				
				ps1.processAFile(f, key, intervalTime, ip, outputFolder);
			}
		}else{
			String fileName[] = folder.getName().split("-"); //DataTest-key20-2s-ip-public-json.txt"
			String key = fileName[1];//"20";
			String ip = fileName[3];
			String intervalTime = fileName[2];
			
			ps1.processAFile(folder, key, intervalTime, ip, outputFolder);
		}
	}
	
	public void processAFile(File f, String key, String intervalTime, String ip, String outputFolder){
		String startDate = ""; //would be modified by the below algorithm
		
		List<String> idList = null;

		int status = 0; // 0 = meta-data, 1 = data

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
					StatusWapper sw;
					sw = constructWapperStatus(line);
					if(sw == null){
						continue;
					}
					List<Status> statuseList = sw.getStatuses();
					if (statuseList.size() > 0) {
						if(idList==null){
							idList = new ArrayList<String>();
						}
						for (Status st : statuseList) {
							idList.add(st.getId());
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
					MyLineWriter.getInstance().writeLine(outputFolder + fileName,
							sb.toString());

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public StatusWapper constructWapperStatus(String json)
			throws WeiboException {
		//System.out.println(json);
		JSONArray statuses = null;
		try {
			JSONObject jsonStatus = new JSONObject(json); // asJSONArray();
			if (!jsonStatus.isNull("statuses")) {
				statuses = jsonStatus.getJSONArray("statuses");
			}
			if (!jsonStatus.isNull("reposts")) {
				statuses = jsonStatus.getJSONArray("reposts");
			}
			int size = statuses.length();
			List<Status> status = new ArrayList<Status>(size);
			for (int i = 0; i < size; i++) {
				status.add(new Status(statuses.getJSONObject(i)));
			}
			long previousCursor = jsonStatus.getLong("previous_curosr");
			long nextCursor = jsonStatus.getLong("next_cursor");
			long totalNumber = jsonStatus.getLong("total_number");
			String hasvisible = jsonStatus.getString("hasvisible");
			return new StatusWapper(status, previousCursor, nextCursor,
					totalNumber, hasvisible);
		} catch (JSONException jsone) {
			//throw new WeiboException(jsone);
			System.out.println("drop 1");
			return null;
		}
	}
}
