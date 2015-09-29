package mt.weibo.crawl.general.dataprocess;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import mt.weibo.common.FaceDetect;
import mt.weibo.db.MyDBConnection;
import mt.weibo.model.Face;

public class FaceDetectProcess {


	private MyDBConnection mdbc;
	private Connection con;
	private String userTableName = "socialmedia.user";
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// should be run periodicity
		FaceDetectProcess fdp = new FaceDetectProcess();
		fdp.process();
	}
	
	public FaceDetectProcess(){
		if (mdbc == null) {
			mdbc = new MyDBConnection();
			con = mdbc.getDBConnection();
		}
	}

	
	private void process() {
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			ResultSet user = stmt
					.executeQuery("SELECT user_id, is_face_detected, avatar_large, age, age_range, gender_detected, glasses, glass_confidence,ethnicity, smiling_detected FROM "
							+ userTableName + " where "
									+ " is_face_detected = false and "
									+ "is_valid = true");
			
			while(user.next()){
				String picUrl = user.getString("avatar_large");
				FaceDetect fd = new FaceDetect();
				List<Face> faceList = fd.detect(picUrl);
				Face face;
				if(faceList.size()>0){
					face = faceList.get(0);
					System.out.print("userid="+ user.getString("user_id")+", ");
					System.out.println(face);
					
					//update db
					user.updateInt("age", face.getAge());
					user.updateInt("age_range", face.getRange());
					user.updateString("gender_detected", face.getGender());
					user.updateString("ethnicity", face.getRace());
					user.updateString("glasses", face.getGlass());
					user.updateDouble("glass_confidence", face.getGlassConfidence());
					user.updateDouble("smiling_detected", face.getSmiling());
					user.updateBoolean("is_face_detected", true);
					user.updateRow();
				}
			}
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
