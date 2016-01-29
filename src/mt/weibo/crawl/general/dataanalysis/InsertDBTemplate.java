package mt.weibo.crawl.general.dataanalysis;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import mt.weibo.db.MyDBConnection;

public class InsertDBTemplate {
	private MyDBConnection mdbc;
	private Connection con;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int port = 9932;
		String database = "shenzhen";
		
		InsertDBTemplate dbt = new InsertDBTemplate(port, database);
		dbt.process();
		dbt.close();
	}
	
	public InsertDBTemplate(int port, String db) {
		if (mdbc == null) {
			mdbc = new MyDBConnection(port, db);
			con = mdbc.getDBConnection();
		}
	}
	
	private void process() {
		// TODO Auto-generated method stub
		PreparedStatement preparedStatement = null;
		String insertTableSQL = "INSERT INTO "
				+ "socialmedia.posts"
				+ " (name, age, gender)"
				+ "VALUES (?, ?, ?)";
		
		try {
			preparedStatement = mdbc.getPrepareStatement(insertTableSQL);
			preparedStatement.setString(1, "test1");
			preparedStatement.setInt(2, 29);
			preparedStatement.setBoolean(3, true);
			
			preparedStatement.executeUpdate();
			preparedStatement.close();

			System.out.println("Inserted one.");
			
		} catch (SQLException e) {
			System.err.println("Something wrong when writing the data to table. ");
			System.err.println(e.getMessage());
		}
	}

	
	private void close() {
		if (mdbc != null) {
			mdbc.close();
		}
	}
}
