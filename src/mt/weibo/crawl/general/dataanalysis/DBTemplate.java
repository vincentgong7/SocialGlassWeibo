package mt.weibo.crawl.general.dataanalysis;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import mt.weibo.db.MyDBConnection;

public class DBTemplate {

	private MyDBConnection mdbc;
	private Connection con;

	public static void main(String[] args) throws SQLException {
		// TODO Auto-generated method stub
		int port = 9932;
		String database = "shenzhen";
		
		DBTemplate dbt = new DBTemplate(port, database);
		dbt.process();
		dbt.close();
	}

	public DBTemplate(int port, String db) {
		if (mdbc == null) {
			mdbc = new MyDBConnection(port, db);
			con = mdbc.getDBConnection();
		}
	}

	private void process() throws SQLException {
		// TODO Auto-generated method stub
		Statement stmt = null;
		stmt = con.createStatement();
		stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
				ResultSet.CONCUR_UPDATABLE);

		System.out.println("[Process] Querying the database...");

		ResultSet rs = stmt
				.executeQuery("SELECT user_id, is_face_detected, avatar_large, age, age_range, gender_detected, glasses, glass_confidence,ethnicity, smiling_detected FROM "
						+ "socialmedia.post_in_scope"
						+ " where "
						+ " is_face_detected = false and " + "is_valid = true");

		while (rs.next()) {
			String str = rs.getString("avatar_large");
			System.out.println(str);

			rs.updateInt("age", 10);
			rs.updateString("gender_detected", "test");
			rs.updateDouble("glass_confidence", 7.07d);
			rs.updateBoolean("is_face_detected", true);

			rs.updateRow();
		}

		rs.close();
		stmt.close();
	}

	private void close() {
		if (mdbc != null) {
			mdbc.close();
		}
	}
}
