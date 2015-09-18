package weibo4j.model;

import weibo4j.org.json.JSONException;
import weibo4j.org.json.JSONObject;

public class District extends WeiboResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 地点信息
	 * 
	 * @author Vincent
	 * 
	 */

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	private String districtID = "";
	private String title = "";
	private String intro = "";
	private double lat = 0d;
	private double longi = 0d;
	private String country = "";
	private String province = "";
	private String city = "";
	private long checkinNum = 0l;

	public District(JSONObject jsonObj) throws WeiboException {
		super();
		init(jsonObj);
	}

	private void init(JSONObject o) throws WeiboException {
		// TODO Auto-generated method stub
		if (o != null) {
			try {
				districtID = o.getString("id");
				title = o.getString("title");
				intro = o.getString("intro");
				lat = o.getDouble("lat");
				longi = o.getDouble("lng");
				country = o.getString("country");
				province = o.getString("province");
				city = o.getString("city");
				checkinNum = o.getLong("checkin_user_num");
			} catch (JSONException e) {
				e.printStackTrace();
				throw new WeiboException(e.getMessage() + ":" + o.toString(), e);
			}
		}
	}

	public String getDistrictID() {
		return districtID;
	}

	public void setDistrictID(String districtID) {
		this.districtID = districtID;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLongi() {
		return longi;
	}

	public void setLongi(double longi) {
		this.longi = longi;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public long getCheckinNum() {
		return checkinNum;
	}

	public void setCheckinNum(long checkinNum) {
		this.checkinNum = checkinNum;
	}

}
