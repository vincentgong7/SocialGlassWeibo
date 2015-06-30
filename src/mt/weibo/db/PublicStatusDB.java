/**
 * 
 */
package mt.weibo.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import mt.weibo.common.LocationSeparator;
import mt.weibo.common.Utils;
import weibo4j.model.Status;
import weibo4j.model.User;

/**
 * @author vincentgong
 *
 */
public class PublicStatusDB {

	private MyDBConnection mdbc;

	public PublicStatusDB(MyDBConnection mdbc) {
		this.mdbc = mdbc;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

	public void insertStatusListWithUsers(List<Status> statusList) {
		insertStatusList(statusList);
		insertUserListFromStatusList(statusList);
	}

	// store the UserList into DB
	public void insertUserListFromStatusList(List<Status> statusList) {
		Iterator<Status> it = statusList.iterator();
		while (it.hasNext()) {
			Status st = it.next();
			User user = st.getUser();
			InsertUser(user);
		}
	}

	public void insertStatusWithUser(Status st) {
		InsertStatus(st);
		InsertUser(st.getUser());
	}

	public void insertStatusList(List<Status> statusList) {
		Iterator<Status> it = statusList.iterator();
		while (it.hasNext()) {
			Status st = it.next();
			InsertStatus(st);
		}
	}

	public void InsertUser(User user) {
		PreparedStatement preparedStatement = null;
		String insertTableSQL = "INSERT INTO socialmedia.public_user"
				+ "(user_id, created_at, createdat_origin, screen_name, name, province, city, location, description, blog_url, "
				+ "profile_image_url, user_domain, gender, followers_count, friends_count, statuses_count, favourites_count, verified, verified_type, is_allow_all_act_msg, "
				+ "is_allow_all_comment, avatar_large, online_status, bi_followers_count, remark, lang, verified_reason, weihao"
//				+ ", location_country, location_province, location_city"
				//todo: get below items from face++
//				+ ", age, age_range, "
//				+ "gender_detected, glasses, ethnicity, smiling_detected, radius_of_gyration"
				+ ") VALUES"
				+ "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		try {
			//analyze the location
//			LocationSeparator ls = new LocationSeparator(user.getLocation());
			
			preparedStatement = mdbc.getPrepareStatement(insertTableSQL);
			preparedStatement.setString(1, user.getId());
			preparedStatement.setString(2, user.getCreatedAt().toString());
			preparedStatement.setString(3, user.getCreatedAt_origin());
			preparedStatement.setString(4, user.getScreenName());
			preparedStatement.setString(5, user.getName());
			preparedStatement.setInt(6, user.getProvince());
			preparedStatement.setInt(7, user.getCity());
			preparedStatement.setString(8, user.getLocation());
			preparedStatement.setString(9, user.getDescription());
			preparedStatement.setString(10, user.getUrl());
			
			preparedStatement.setString(11, user.getProfileImageUrl());
			preparedStatement.setString(12, user.getUserDomain());
			preparedStatement.setString(13, user.getGender());
			preparedStatement.setInt(14, user.getFollowersCount());
			preparedStatement.setInt(15, user.getFriendsCount());
			preparedStatement.setInt(16, user.getStatusesCount());
			preparedStatement.setInt(17, user.getFavouritesCount());
			preparedStatement.setBoolean(18, user.isVerified());
			preparedStatement.setInt(19, user.getverifiedType());
			preparedStatement.setBoolean(20, user.isallowAllActMsg());

			preparedStatement.setBoolean(21, user.isallowAllComment());
			preparedStatement.setString(22, user.getavatarLarge());
			preparedStatement.setInt(23, user.getonlineStatus());
			preparedStatement.setInt(24, user.getbiFollowersCount());
			preparedStatement.setString(25, user.getRemark());
			preparedStatement.setString(26, user.getLang());
			preparedStatement.setString(27, user.getVerifiedReason());
			preparedStatement.setString(28, user.getWeihao());
//			preparedStatement.setString(29, ls.getCountry());
//			preparedStatement.setString(30, ls.getProvince());
//			preparedStatement.setString(31, ls.getCity());
//			(age, age_range,gender_detected, glasses, ethnicity, smiling_detected, radius_of_gyration) from face++
//			preparedStatement.setString(1, user.);
//			preparedStatement.setString(1, user);
//			
//			preparedStatement.setString(1, user);
//			preparedStatement.setString(1, user);
//			preparedStatement.setString(1, user);
//			preparedStatement.setString(1, user);
//			preparedStatement.setString(1, user);
			
			// execute insert SQL stetement
			preparedStatement.executeUpdate();
			System.out.println(user.getCreatedAt_origin() + " "
					+ user.getScreenName() + " " + user.getDescription());
		} catch (SQLException e) {
			System.out.println("Something wrong when writing the post to DB.");
			e.printStackTrace();
		}

	}

	public void InsertStatus(Status st) {
		PreparedStatement preparedStatement = null;
		String insertTableSQL = "INSERT INTO socialmedia.public_post"
				+ "(status_id, user_id, created_at, createdat_origin, weibo_id, content, source, is_favorited, is_truncated, in_reply_to_status_id, in_reply_to_user_id, in_reply_to_screen_name, "
				+ "thumbnail_pic, bmiddle_pic, original_pic, retweeted_status, geo, latitude, longitude, reposts_count, comments_count, annotations"
//				+ ", poiid"
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
			if(st.getSource()!=null){
				preparedStatement.setString(7, st.getSource().getName());	
			}else{
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
//			preparedStatement.setString(23,
//					Utils.parsePoiid(st.getAnnotations()));
			preparedStatement.setLong(23, Utils.getUnixTimeStamp(st.getCreatedAt_origin()));
			// execute insert SQL stetement
			preparedStatement.executeUpdate();

			System.out.println(st.getCreatedAt_origin() + " " + st.getText());
		} catch (SQLException e) {
			System.out.println("Something wrong when writing the post to DB.");
			e.printStackTrace();
		}

	}

}
