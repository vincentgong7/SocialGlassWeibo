package mt.weibo.crawl.general.dataprocess.pathpatterns;

import java.util.Date;

public class Visit {
	private String post_id = "";
	private String user_id = "";
	private String poiid = "";
	private long timestamp = 0L;
	private Date date = null;
	private String day = "";
	private double day_oy = 0d;
	
	public Visit(String post_id, String user_id, String poiid,
			Long timestamp, double day_oy) {
		// TODO Auto-generated constructor stub
		this.post_id = post_id;
		this.user_id = user_id;
		this.poiid = poiid;
		this.timestamp = timestamp;
		this.day_oy = day_oy;
	}
	
	public String getPost_id() {
		return post_id;
	}
	public void setPost_id(String post_id) {
		this.post_id = post_id;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getPoiid() {
		return poiid;
	}
	public void setPoiid(String poiid) {
		this.poiid = poiid;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}

	public double getDay_oy() {
		return day_oy;
	}

	public void setDay_oy(double day_oy) {
		this.day_oy = day_oy;
	}
}
