/**
 * 
 */
package mt.weibo.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author vincentgong
 *
 */
public class MyDBConnection {

	Connection con = null;

	String url = "jdbc:postgresql://localhost:5432/microblog";
	String user = "postgres";
	String password = "postgres";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public MyDBConnection() {
		init();
	}

	public MyDBConnection(String url, String user, String password) {
		this.url = url;
		this.user = user;
		this.password = password;
		init();
	}

	public MyDBConnection(String url) {
		this.url = url;
		init();
	}

	public MyDBConnection(int port) {
		this.url = "jdbc:postgresql://localhost:" + port + "/microblog";
		init();
	}

	public void init() {
		try {
			if (con == null) {
				con = DriverManager.getConnection(url, user, password);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void close() {
		try {
			if (con != null) {
				con.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Connection getDBConnection() {
		// TODO Auto-generated method stub
		return con;
	}

	public PreparedStatement getPrepareStatement(String sql)
			throws SQLException {
		// TODO Auto-generated method stub
		return getDBConnection().prepareStatement(sql);
	}

}
