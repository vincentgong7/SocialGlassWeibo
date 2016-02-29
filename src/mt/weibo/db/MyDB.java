package mt.weibo.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MyDB {
	
	private static MyDBConnection con;
	
	public static void main(String[] args) throws SQLException {
		// TODO Auto-generated method stub
		
		// query and update
		ResultSet rsQueryUpdate = MyDB.queryUpdate("sql");
		while(rsQueryUpdate.next()){
			System.out.println(rsQueryUpdate.getInt(1));
			rsQueryUpdate.updateString("name", "new_name");
			rsQueryUpdate.updateRow();
		}
		rsQueryUpdate.close();
		
		// simply insert, query, update, delete
		ResultSet rsQuery = MyDB.query("sql");
		while(rsQuery.next()){
			System.out.println(rsQuery.getInt(1));
		}
		rsQuery.close();
		
		// update
		PreparedStatement ps = MyDB.insertUpdate("sql");
		ps.setString(1, "ID");
		ps.setString(2, "Name");
		ps.executeUpdate();
		
		ps.close();
		
		MyDB.close();
	}

	private static void close() {
		// TODO Auto-generated method stub
		if(con != null){
			con.close();
			con = null;
		}
	}

	public static void init(){
		if(con == null) {
			con = new MyDBConnection();
		}
	}
	
	public static void init(int port){
		if(con == null) {
			con = new MyDBConnection(port);
		}
	}
	
	
	public static void init(int port, String dbname){
		if(con == null) {
			con = new MyDBConnection(port, dbname);
		}
	}
	
	public static ResultSet queryUpdate(String sql) throws SQLException{
		init();
		return con.getDBConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
				ResultSet.CONCUR_UPDATABLE).executeQuery(sql);
	}
	
	public static ResultSet query(String sql) throws SQLException{
		init();
		return con.getDBConnection().createStatement().executeQuery(sql);
	}
	
	public static PreparedStatement insertUpdate(String sql) throws SQLException{
		return con.getPrepareStatement(sql);
	}
	
}
