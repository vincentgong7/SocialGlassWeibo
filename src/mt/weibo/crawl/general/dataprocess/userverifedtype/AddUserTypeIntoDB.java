package mt.weibo.crawl.general.dataprocess.userverifedtype;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import mt.weibo.common.MyLineReader;
import mt.weibo.db.MyDBConnection;

public class AddUserTypeIntoDB {
	private String folder;
	private MyDBConnection mdbc;
	private Connection con;
	
	public static void main(String[] args) throws SQLException {
		// TODO Auto-generated method stub
		int port = 9932;
		String database = "shenzhen";
		
		AddUserTypeIntoDB ati = new AddUserTypeIntoDB(port, database);
		
		ati.setFolder("/Users/vincentgong/Documents/TUD/Master TUD/A Master Thesis/share/workbench/saraExp/user_verified_type/result/fox_kite_after_dedu/");
		if(args.length >0 ) {
			ati.setFolder(args[0]);
			System.out.println("Foleder set: " + args[0]);
			System.out.println();
			System.out.println();
			System.out.println();
			System.out.println();
		}
		
		ati.process();
	}
	
	public AddUserTypeIntoDB(int port, String db) {
		if (mdbc == null) {
			mdbc = new MyDBConnection(port, db);
			con = mdbc.getDBConnection();
		}
	}
	
	private void setFolder(String folder) {
		// TODO Auto-generated method stub
		this.folder = folder;
	}
	
	
	
	
	private void process() throws SQLException{
		// TODO Auto-generated method stub
		File fo = new File(folder);
		int i = 1;
		for (File f : fo.listFiles()) {
			if(!f.getName().endsWith("txt") && !f.getName().endsWith("csv")){
				continue;
			}
			try {
				MyLineReader mlr = new MyLineReader(f);
				while(mlr.hasNextLine()){
					String line = mlr.nextLine();
					String uid = line.split(",")[0];
					int type = Integer.valueOf(line.split(",")[1]);
					insertDB(uid, type);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		System.out.println();
		System.out.println("done: " + i + "users.");
		
		close();
		
	}
	
	private void insertDB(String uid, int type){
		
		
		PreparedStatement preparedStatement = null;
		String insertTableSQL = "INSERT INTO "
				+ "socialmedia.user_all_type"
				+ " (user_id, type)"
				+ "VALUES (?, ?)";
		
		try {
			preparedStatement = mdbc.getPrepareStatement(insertTableSQL);
			preparedStatement.setString(1, uid);
			preparedStatement.setInt(2, type);
			
			preparedStatement.executeUpdate();
			preparedStatement.close();

			System.out.println("Inserted: " + uid + ", " + type);
			
		} catch (SQLException e) {
			System.err.println("Something wrong when writing the data to table. " + uid + ", " + type);
			System.err.println(e.getMessage());
		}
		
	}
	private void close() {
		if (mdbc != null) {
			mdbc.close();
		}
	}

}
