/**
 * 
 */
package mt.weibo.crawl.experiment.analysis.beijing;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mt.weibo.common.MyLineReader;
import mt.weibo.common.MyLineWriter;
import weibo4j.model.User;
import weibo4j.model.UserWapper;
import weibo4j.model.WeiboException;
import weibo4j.org.json.JSONArray;
import weibo4j.org.json.JSONException;
import weibo4j.org.json.JSONObject;

/**
 * @author vincentgong
 *
 */
public class LocalUserDeDu {

	private File inputFile;
	private Set<String> userSet;
	private String outputFileName;
	private int count;

	public LocalUserDeDu(File inputFile, File outFolder) {
		this.inputFile = inputFile;
		this.userSet = new HashSet<String>();
		this.outputFileName = generateOutputFilename(outFolder);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		File infolder = new File(
//				"/Users/vincentgong/Documents/TUD/Master TUD/A Master Thesis/share/servers/beijing/v1/nearbyusers/");
//		File outfolder = new File("/Users/vincentgong/Documents/TUD/Master TUD/A Master Thesis/share/servers/beijing/v1/nearbyusers_csv");
		File infolder = new File(args[0]);
		File outfolder = new File(args[1]);
		File[] files = infolder.listFiles();
		for (File f : files) {
			LocalUserDeDu sdd = new LocalUserDeDu(f, outfolder);
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
		UserWapper sw;
		try {
			sw = constructWapperUsers(line);
			if (sw == null) {
				return;
			}
			List<User> userList = sw.getUsers();

			if (userList.size() > 0) {
				for (User user : userList) {
					try {
						String uid = user.getId();
						if (!this.userSet.contains(uid)) {
							this.userSet.add(uid);
							
							String username = user.getName();
							// String screenname = user.getScreenName();
							// String url = user.getUrl();
							// String avatarLargeUrl = user.getavatarLarge();
							String gender = user.getGender();
							boolean geo_enabled = user.isGeo_enabled();
							double lat = user.getLat();
							double lon = user.getLon();
							// String homeLocation = user.getLocation();
							// int city = user.getCity();
							// int followersCount = user.getFollowersCount();
							// int bifollowersCount = user.getbiFollowersCount();
							// int friendsCount = user.getFriendsCount();
							// String description = user.getDescription();
							// String createdat_origin = user.getCreatedAt_origin();
							
							// item = uid, name, gender, geo-enabled, lat, lon
							item = uid + "," + username + "," + gender + ","
									+ geo_enabled + "," + lat + "," + lon;
							MyLineWriter.getInstance().writeLine(
									this.outputFileName, item);
							count++;
							System.out.println("Number: " + count
									+ ". Content: " + item);
						}else{
//							System.out.println(uid);
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

	/**
	 * 
	 * @param res
	 * @return
	 * @throws WeiboException
	 */
	public static UserWapper constructWapperUsers(String json)
			throws WeiboException {
		try {
			JSONObject jsonUsers = new JSONObject(json); // asJSONArray();
			JSONArray user = jsonUsers.getJSONArray("users");
			int size = user.length();
			List<User> users = new ArrayList<User>(size);
			for (int i = 0; i < size; i++) {
				users.add(new User(user.getJSONObject(i)));
			}
			long previousCursor = jsonUsers.getLong("previous_curosr");
			long nextCursor = jsonUsers.getLong("next_cursor");
			long totalNumber = jsonUsers.getLong("total_number");
			String hasvisible = jsonUsers.getString("hasvisible");
			return new UserWapper(users, previousCursor, nextCursor,
					totalNumber, hasvisible);
		} catch (JSONException jsone) {
			throw new WeiboException(jsone);
		}
	}

}
