/**
 * 
 */
package mt.weibo.crawl.process;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import mt.weibo.common.MyLineReader;
import mt.weibo.common.Utils;
import mt.weibo.db.MyDBConnection;
import weibo4j.model.Status;
import weibo4j.model.StatusWapper;
import weibo4j.model.WeiboException;
import weibo4j.org.json.JSONArray;
import weibo4j.org.json.JSONException;
import weibo4j.org.json.JSONObject;

/**
 * @author Vinent GONG
 *
 */
public class PublicTimeLineJsonToData {

	public String targetTable;
	public String sourceFolder;
	public MyDBConnection mdbc;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		PublicTimeLineJsonToData ptjd = new PublicTimeLineJsonToData();
		ptjd.targetTable = "";
		ptjd.sourceFolder = Utils.getResourceFilePath() + Utils.RESOURCE_FOLDER
				+ Utils.PUBLIC_TIMELINE_FOLDER + "processing/";
		ptjd.transform();

	}

	public void transform() {
		
		this.mdbc = new MyDBConnection();
		File folder = new File(sourceFolder);
		if (folder.isDirectory()) {
			File[] fileArray = folder.listFiles();
			for (File f : fileArray) {
				try {
					MyLineReader mlr = new MyLineReader(f);
					mlr.init();
					while (mlr.hasNextLine()) {
						String json = mlr.nextLine();
						System.out.println(json);
						if (json != null && !json.equals("")) {
							StatusWapper sw = constructStatus(json);
							if(sw!=null){
								List statuses = sw.getStatuses();
//								StatusDB sdb = new StatusDB();
//								sdb.insertStatusList(statuses);
//								sdb.close();
								insertStatusList(statuses);
							}
						}
					}
					mlr.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
		this.mdbc.close();
	}

	private StatusWapper constructStatus(String json) {
		// TODO Auto-generated method stub
		try {
			JSONObject jsonStatus = new JSONObject(json);
			JSONArray statuses = null;
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
			jsone.printStackTrace();
		} catch (WeiboException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public void insertStatusList(List<Status> statusList) {
		Iterator<Status> it = statusList.iterator();
		while (it.hasNext()) {
			Status st = it.next();
			InsertStatus(st);
		}
	}

	public void InsertStatus(Status st) {
		PreparedStatement preparedStatement = null;
		String insertTableSQL = "INSERT INTO socialmedia.public_post"
				+ "(status_id, user_id, created_at, createdat_origin, weibo_id, content, source, is_favorited, is_truncated, in_reply_to_status_id, in_reply_to_user_id, in_reply_to_screen_name, "
				+ "thumbnail_pic, bmiddle_pic, original_pic, retweeted_status, geo, latitude, longitude, reposts_count, comments_count, annotations"
				// + ", poiid"
				+ ", createat_timestamp) VALUES"
				+ "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		try {
			String retweetedStatus = "";
			if (st.getRetweetedStatus() != null) {
				retweetedStatus = st.getRetweetedStatus().toString();
			}

			preparedStatement = mdbc.getPrepareStatement(insertTableSQL);
			preparedStatement.setString(1, st.getId());
			preparedStatement.setString(2, st.getUser().getId());
			preparedStatement.setString(3, st.getCreatedAt().toString());
			preparedStatement.setString(4, st.getCreatedAt_origin());
			preparedStatement.setString(5, st.getMid());
			preparedStatement.setString(6, st.getText());
			if (st.getSource() != null) {
				preparedStatement.setString(7, st.getSource().getName());
			} else {
				preparedStatement.setString(7, "");
			}
			preparedStatement.setBoolean(8, st.isFavorited());
			preparedStatement.setBoolean(9, st.isTruncated());
			preparedStatement.setLong(10, st.getInReplyToStatusId());
			preparedStatement.setLong(11, st.getInReplyToUserId());
			preparedStatement.setString(12, st.getInReplyToScreenName());
			preparedStatement.setString(13, st.getThumbnailPic());
			preparedStatement.setString(14, st.getBmiddlePic());
			preparedStatement.setString(15, st.getOriginalPic());
			preparedStatement.setString(16, retweetedStatus);
			preparedStatement.setString(17, st.getGeo());
			preparedStatement.setDouble(18, st.getLatitude());
			preparedStatement.setDouble(19, st.getLongitude());
			preparedStatement.setInt(20, st.getRepostsCount());
			preparedStatement.setInt(21, st.getCommentsCount());
			preparedStatement.setString(22, st.getAnnotations());
			// preparedStatement.setString(23,
			// Utils.parsePoiid(st.getAnnotations()));
			preparedStatement.setLong(23,
					Utils.getUnixTimeStamp(st.getCreatedAt_origin()));
			// execute insert SQL stetement
			preparedStatement.executeUpdate();
			preparedStatement.close();
			System.out.println(st.getCreatedAt_origin() + " " + st.getText());
		} catch (SQLException e) {
			System.out.println("Something wrong when writing the post to DB.");
			e.printStackTrace();
		}

	}


}
