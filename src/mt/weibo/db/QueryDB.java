/**
 * 
 */
package mt.weibo.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Vinent GONG
 *
 */
public class QueryDB {

	private MyDBConnection mdbc;
	private PreparedStatement preparedStatement = null;
	private String sql = "";
	ResultSet rs = null;
	
	/**
	 * @param args
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws SQLException {
		// TODO Auto-generated method stub
		QueryDB qdb = new QueryDB("Select * from socialmedia.user_json");
		qdb.init();
		ResultSet rs = qdb.Query();
		while(rs.next()){
			System.out.println(rs.getString(1) + " " + rs.getString(2));
		}
		qdb.close();
	}

	public QueryDB(String sql) {
		this.sql = sql;
	}

	public void init() {
		mdbc = new MyDBConnection();
		mdbc.init();
	}

	public ResultSet Query() {
		try {
			preparedStatement = mdbc.getPrepareStatement(sql);
			rs = preparedStatement.executeQuery();
			return rs;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public void close() {

		try {
			if(rs !=null){
				rs.close();
			}
			if (preparedStatement != null) {
				preparedStatement.close();
			}
			if (mdbc != null) {
				mdbc.close();
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
}
