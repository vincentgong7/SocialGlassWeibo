/**
 * 
 */
package mt.weibo.crawl.process;

import java.sql.ResultSet;
import java.sql.SQLException;

import mt.weibo.common.Toolbox;
import mt.weibo.common.Utils;
import mt.weibo.db.QueryDB;

/**
 * @author Vinent GONG
 *
 */
public class UserIDToFile {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		UserIDToFile uid2f = new UserIDToFile();
		uid2f.exportIDToFile("uid.txt");
	}

	private void exportIDToFile(String fileName) {
		String fullFilePath = Utils.UIDS_FOLDER + fileName;
		QueryDB qdb = new QueryDB("Select user_id from " + Utils.DB_NAME
				+ "user");
		qdb.init();
		ResultSet rs = qdb.Query();
		try {
			int count = 0;
			while (rs.next()) {
				count++;
				String uid = rs.getString(1);
				Toolbox.saveDataToFile(uid, fullFilePath);
				System.out.println("User ID has been exported. Amount: "
						+ count);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			qdb.close();
		}
	}

}
