package mt.weibo.crawl.general.dataprocess.poi;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import mt.weibo.common.MyLineReader;
import mt.weibo.db.MyDBConnection;

public class POICategoryCreater {

	private MyDBConnection mdbc;
	private String categoryTablename = "socialmedia.poi_category";
	private String categoryCSVFolder;

	public POICategoryCreater(String categoryTablename,
			String categoryCSVFolder, String url, String username,
			String password) {
		this.categoryTablename = categoryTablename;
		this.categoryCSVFolder = categoryCSVFolder;

		if (mdbc == null) {
			mdbc = new MyDBConnection(url, username, password);
			// mdbc = new MyDBConnection();
		}
	}

	public static void main(String[] args) {

		if (args.length != 4) {
			System.out
					.println("usage: java -jar POICategoryCreater.jar jdbc:postgresql://localhost:5432/microblog postgres postgres poi_folder/");
			return;
		}

		String categoryTablename = "socialmedia.poi_category";

		String url = args[0];
		String username = args[1];
		String password = args[2];
		String categoryCSVFolder = args[3];

		// String url = "localhost";
		// String username = "postgres";
		// String password = "postgres";
		// String categoryCSVFolder =
		// "/Users/vincentgong/Documents/TUD/Master TUD/A Master Thesis/share/configuration/poi_category/";

		POICategoryCreater pcc = new POICategoryCreater(categoryTablename,
				categoryCSVFolder, url, username, password);

		pcc.process();
		pcc.close();
	}

	private void process() {
		// TODO Auto-generated method stub
		File folder = new File(categoryCSVFolder);
		for (File f : folder.listFiles()) {
			try {
				MyLineReader mlr = new MyLineReader(f);
				while (mlr.hasNextLine()) {
					String line = mlr.nextLine().trim();
					storePOICateToDB(line);
				}
				mlr.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	private void storePOICateToDB(String line) {
		if (null != line && !"".equals(line)) {
			String[] item = line.split(",");
			String id = item[0];
			String pid = item[1];
			String name = item[3];
			String nameCN = item[2];

			PreparedStatement preparedStatement = null;
			String insertTableSQL = "INSERT INTO " + this.categoryTablename
					+ "(category_id, parent_id, name, name_cn)" + " VALUES"
					+ "(?,?,?,?)";

			try {
				preparedStatement = mdbc.getPrepareStatement(insertTableSQL);
				preparedStatement.setString(1, id);
				preparedStatement.setString(2, pid);
				preparedStatement.setString(3, name);
				preparedStatement.setString(4, nameCN);

				// execute insert SQL stetement
				preparedStatement.executeUpdate();
				preparedStatement.close();
				System.out.println(line);

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void close() {
		if (this.mdbc != null) {
			this.mdbc.close();
		}
	}
}
