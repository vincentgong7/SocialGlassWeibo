/**
 * 
 */
package mt.weibo.crawl.experiment.analysis.ams;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mt.weibo.common.MyLineReader;
import mt.weibo.common.MyLineWriter;
import weibo4j.model.Status;
import weibo4j.model.StatusWapper;
import weibo4j.model.User;
import weibo4j.model.WeiboException;
import weibo4j.org.json.JSONArray;
import weibo4j.org.json.JSONException;
import weibo4j.org.json.JSONObject;

/**
 * @author vincentgong
 *
 */
public class SingleDeDu {

	private File inputFile;
	private Set<String> postSet;
	private String outputFileName;
	private int count;

	public SingleDeDu(File inputFile, File outFolder) {
		this.inputFile = inputFile;
		this.postSet = new HashSet<String>();
		this.outputFileName = generateOutputFilename(outFolder);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// File inputFile = new File(
		// "/Users/vincentgong/Documents/TUD/Master TUD/A Master Thesis/share/servers/ams/back/2/DataTest-keyAll-3s-nearbypost-json-2015052900.txt");
		File infolder = new File(args[0]);
		File outfolder = new File(args[1]);
		File[] files = infolder.listFiles();
		for (File f : files) {
			SingleDeDu sdd = new SingleDeDu(f, outfolder);
			System.out.println(sdd.outputFileName);
			sdd.process();
		}

	}

	private void process() {
		try {
			MyLineReader mlr = new MyLineReader(this.inputFile);
			while (mlr.hasNextLine()) {
				String line = mlr.nextLine().trim();

				if (line == null || !line.startsWith("{")) {
					continue;
				}

				analyze(line); // item = {uid, username, gender, lat, long,
								// data/time, postid}
			}
			mlr.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private String generateOutputFilename(File outFolder) {
		String csvName = outFolder.getAbsolutePath() + "/"
				+ this.inputFile.getName().split("\\.")[0] + "-csv.txt";
		// System.out.println(csvName);
		return csvName;
	}

	private void analyze(String line) {
		String item = "";
		StatusWapper sw;
		try {
			sw = constructWapperStatus(line);
			if (sw == null) {
				return;
			}
			List<Status> statuseList = sw.getStatuses();

			if (statuseList.size() > 0) {
				for (Status st : statuseList) {
					try {
						String postID = st.getId();
						if (!this.postSet.contains(postID)) {

							this.postSet.add(postID);
							double lat = st.getLatitude();
							double longi = st.getLongitude();
							Date date = st.getCreatedAt();
							User user = st.getUser();
							String uid = user.getId();
							String username = user.getName();
							String gender = user.getGender();

							// item = {uid, username, gender, lat, long,
							// data/time, postid}
							item = uid + "," + username + "," + gender + ","
									+ lat + "," + longi + "," + date.toString()
									+ "," + postID;

							MyLineWriter.getInstance().writeLine(
									this.outputFileName, item);
							count++;
							System.out.println("Number: " + count
									+ ". Content: " + item);
						}
					} catch (Exception e) {
						e.printStackTrace();
						continue;
					}
				}
			}

		} catch (WeiboException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		return;
	}

	public StatusWapper constructWapperStatus(String json)
			throws WeiboException {
		// System.out.println(json);
		JSONArray statuses = null;
		try {
			JSONObject jsonStatus = new JSONObject(json); // asJSONArray();
			if (!jsonStatus.isNull("statuses")) {
				statuses = jsonStatus.getJSONArray("statuses");
			}
			if (!jsonStatus.isNull("reposts")) {
				statuses = jsonStatus.getJSONArray("reposts");
			}
			if (statuses == null) {
				return null;
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
			// throw new WeiboException(jsone);
			System.out.println("drop 1");
			return null;
		}
	}

}
