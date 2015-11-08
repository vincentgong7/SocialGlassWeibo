/**
 * 
 */
package mt.weibo.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author vincentgong
 *
 */
public class StatementQuery {

	MyDBConnection mdbc = null;
	ResultSet rs = null;
	PreparedStatement ps = null;

	/**
	 * @param args
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws SQLException {
		StatementQuery sq = new StatementQuery();
		sq.init();
		ResultSet rs = sq.query("Select count(*) from socialmedia.post");
		if(rs.next()){
			System.out.println(rs.getInt(1));
		}
		sq.close();
	}

	public void init() {
		mdbc = new MyDBConnection();
		mdbc.init();
	}

	public ResultSet query(String sql) {
		try {
			ps = mdbc.getPrepareStatement(sql);;
			rs = ps.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}

	public void close() {
		mdbc.close();
//		try {
//			if (rs != null) {
//				rs.close();
//			}
//			if (st != null) {
//				st.close();
//			}
//			if (mdbc != null) {
//				mdbc.close();
//			}
//		} catch (SQLException ex) {
//			Logger lgr = Logger.getLogger(PostgresVersion.class.getName());
//			lgr.log(Level.WARNING, ex.getMessage(), ex);
//		}
	}
}
