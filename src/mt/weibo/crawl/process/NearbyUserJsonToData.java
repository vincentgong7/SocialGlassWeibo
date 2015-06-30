/**
 * 
 */
package mt.weibo.crawl.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import weibo4j.model.User;
import weibo4j.model.UserWapper;
import weibo4j.model.WeiboException;
import mt.weibo.common.Utils;
import mt.weibo.db.MyDBConnection;
import mt.weibo.db.QueryDB;
import mt.weibo.db.UserDB;

/**
 * @author Vinent GONG
 *
 */
public class NearbyUserJsonToData {

	public String sourceTable;
	public String targetTable;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		NearbyUserJsonToData ujd = new NearbyUserJsonToData("user_json", "user");
		ujd.process();
	}

	public NearbyUserJsonToData(String sourceTable, String targetTable) {
		this.sourceTable = sourceTable;
		this.targetTable = targetTable;
	}

	public void process() {
		String querySourceTable = "Select * from " + Utils.DB_NAME
				+ sourceTable + " where is_processed = false";
		QueryDB qdb = new QueryDB(querySourceTable);
		qdb.init();
		ResultSet rs = qdb.Query();
		try {
			while (rs.next()) {
				boolean success = false;
				String json = rs.getString(2);
				if(json != null && !json.equals("")){
					System.out.println(json);
					success = interpret(rs.getInt(1), rs.getString(2));
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		qdb.close();
	}

	private boolean interpret(int id, String userJson) {
		// boolean interpreted
		try {
			UserWapper uw = UserDB.constructWapperUsers(userJson);
			List<User> userList = uw.getUsers();
			System.out.println("Amount of users in this List: " + userList.size());
			Iterator<User> it = userList.iterator();
			while (it.hasNext()) {
				User user = it.next();
				boolean success = UserDB.InsertUser(user, targetTable);
				if (success) {
					updateJsonTable(id);
				}
			}
		} catch (WeiboException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	private void updateJsonTable(int itmeID) {
		// TODO Auto-generated method stub
		MyDBConnection mdbc = new MyDBConnection();
		String sql = "UPDATE " + Utils.DB_NAME + sourceTable
				+ " SET is_processed = ? WHERE id = ?";
		try {
			PreparedStatement ps = mdbc.getPrepareStatement(sql);
			ps.setBoolean(1, true);
			ps.setInt(2, itmeID);
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mdbc.close();
	}
}
