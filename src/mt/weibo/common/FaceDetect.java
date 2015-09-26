package mt.weibo.common;

import java.util.ArrayList;
import java.util.List;

import mt.weibo.model.Face;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facepp.error.FaceppParseException;
import com.facepp.http.HttpRequests;
import com.facepp.http.PostParameters;

public class FaceDetect {

	public static final String APP_KEY = "24ef3972eeec994d12c2f28bca1db9f2";
	public static final String APP_SECRET = "lthNqIG1zzTp_ZWyX4WFtBW8zx9baWsY ";

	private String appKey = FaceDetect.APP_KEY;
	private String appSecret = FaceDetect.APP_SECRET;
	private boolean isInChina = false;
	private HttpRequests httpRequests;

	public static void main(String[] args) {
		FaceDetect faceDet = new FaceDetect();
		List<Face> faecList = faceDet
				.detect("http://www.details.com/blogs/daily-details/black_plastic_glasses.jpg");
		 System.out.println(faecList);
	}

	public FaceDetect() {
		this.appKey = FaceDetect.APP_KEY;
		this.appSecret = FaceDetect.APP_SECRET;
		this.isInChina = false;
	}

	public FaceDetect(String appKey, String appSecret) {
		this.appKey = appKey;
		this.appSecret = appSecret;
		this.isInChina = false;
	}

	public FaceDetect(String appKey, String appSecret, boolean isInChina) {
		this.appKey = appKey;
		this.appSecret = appSecret;
		this.isInChina = isInChina;
	}

	public List<Face> detect(String picURL) {
		List<Face> list = new ArrayList<Face>();
		if (httpRequests == null) {
			httpRequests = new HttpRequests(this.appKey, this.appSecret,
					this.isInChina, true);
		}
		JSONObject result = null;
		try {
			PostParameters pp = new PostParameters();
			pp.addAttribute("attribute", "gender,age,race,smiling,glass,pose");
			pp.setUrl(picURL);
			result = httpRequests.detectionDetect(pp);
//			System.out.println(result);
			JSONArray faces = result.getJSONArray("face");
			for (int i = 0; i < faces.length(); ++i) {
				JSONObject f = faces.getJSONObject(i);
				JSONObject attribute = f.getJSONObject("attribute");

				int age = attribute.getJSONObject("age").getInt("value");
				int range = attribute.getJSONObject("age").getInt("range");
				String gender = attribute.getJSONObject("gender").getString(
						"value");
				double genderConfidant = attribute.getJSONObject("gender")
						.getDouble("confidence");
				String race = attribute.getJSONObject("race")
						.getString("value");
				double raceConfidant = attribute.getJSONObject("race")
						.getDouble("confidence");
				String glass = attribute.getJSONObject("glass").getString(
						"value");
				double glassConfident = attribute.getJSONObject("glass")
						.getDouble("confidence");
				double smiling = attribute.getJSONObject("smiling").getDouble(
						"value");

				Face face = new Face(age, range, gender, genderConfidant, race,
						raceConfidant, glass, glassConfident, smiling);
				list.add(face);
			}
		} catch (FaceppParseException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return list;
	}

}
