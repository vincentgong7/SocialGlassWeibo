/**
 * 
 */
package mt.weibo.db;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author vincentgong
 *
 */
public class ConfigDB {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println(ConfigDB.getStarttime());
	}

	//get the starttime for crawl post
	public static long getStarttime() {
		Long startTime = 0L;
		ResultSet rs = null;
		StatementQuery sq = new StatementQuery();
		sq.init();
		String sql = "Select MAX(createat_timestamp) from socialmedia.post";
		try {
			rs = sq.query(sql);
			if (rs.next()) {
			    //System.out.println(rs.getString(1));
			    startTime = rs.getLong(1);
			}
		} catch (NumberFormatException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sq.close();
		return startTime;
	}

}
