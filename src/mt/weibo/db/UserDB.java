/**
 * 
 */
package mt.weibo.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import mt.weibo.common.Utils;
import weibo4j.model.User;
import weibo4j.model.UserWapper;
import weibo4j.model.WeiboException;
import weibo4j.org.json.JSONArray;
import weibo4j.org.json.JSONException;
import weibo4j.org.json.JSONObject;

/**
 * @author Vinent GONG
 *
 */
public class UserDB {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public static UserWapper constructWapperUsers(String json)
			throws WeiboException {
		JSONObject jsonUsers;
		try {
			jsonUsers = new JSONObject(json);
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

	public static List<User> getUserList(String json) {
		List<User> userList = new ArrayList<User>();
		UserWapper uw;
		try {
			if(json != null && !json.equals("")){
				uw = constructWapperUsers(json);
				userList = uw.getUsers();
			}
			return userList;
		} catch (WeiboException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return userList;
	}

	public static boolean InsertUser(User user, String tableName) {
		MyDBConnection mdbc = new MyDBConnection();
		PreparedStatement preparedStatement = null;
		String insertTableSQL = "INSERT INTO "
				+ Utils.DB_NAME
				+ tableName
				+ "(user_id, created_at, createdat_origin, screen_name, name, province, city, location, description, blog_url, "
				+ "profile_image_url, user_domain, gender, followers_count, friends_count, statuses_count, favourites_count, verified, verified_type, is_allow_all_act_msg, "
				+ "is_allow_all_comment, avatar_large, online_status, bi_followers_count, remark, lang, verified_reason, weihao"
				// + ", location_country, location_province, location_city"
				// todo: get below items from face++
				// + ", age, age_range, "
				// +
				// "gender_detected, glasses, ethnicity, smiling_detected, radius_of_gyration"
				+ ") VALUES"
				+ "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		try {
			// analyze the location
			// LocationSeparator ls = new LocationSeparator(user.getLocation());

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
			// preparedStatement.setString(29, ls.getCountry());
			// preparedStatement.setString(30, ls.getProvince());
			// preparedStatement.setString(31, ls.getCity());
			// (age, age_range,gender_detected, glasses, ethnicity,
			// smiling_detected, radius_of_gyration) from face++
			// preparedStatement.setString(1, user.);
			// preparedStatement.setString(1, user);
			//
			// preparedStatement.setString(1, user);
			// preparedStatement.setString(1, user);
			// preparedStatement.setString(1, user);
			// preparedStatement.setString(1, user);
			// preparedStatement.setString(1, user);

			// execute insert SQL stetement
			preparedStatement.executeUpdate();
			System.out.println(user.getId() + " "
					+ user.getScreenName() + " " + user.getDescription());

			preparedStatement.close();
			mdbc.close();
			return true;
		} catch (SQLException e) {
			System.out.println("Something wrong when writing the post to DB.");
			if (e.getMessage().contains("duplicate key")) {
				System.out.println(e.getMessage());

				mdbc.close();
				return true;
			}
			// e.printStackTrace();
			return false;
		}

	}
}
