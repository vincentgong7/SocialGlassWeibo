package mt.weibo.crawl.general.dataprocess.pathpatterns;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import mt.weibo.crawl.general.dataprocess.common.DataProcessUtils;

public class Path {
	private String user_id;
	private String day;
	private String checksum;
	private String path;
	private String postPath;
	private double doy_oy;

	private int path_length;
	private boolean is_full_path;
	
	public Path(String user_id, String day, String checksum, int path_length, String path, String postPath, boolean is_full_path, double day_oy){
		this.user_id = user_id;
		this.day = day;
		this.checksum = checksum;
		this.path = path;
		this.postPath = postPath;
		
		this.path_length = path_length;
		this.is_full_path = is_full_path;
		this.doy_oy = day_oy;
		init();
	}

	public Path(Visit v){
		this.user_id = v.getUser_id();
		this.path = v.getPoiid();
		this.postPath = v.getPost_id();
		this.doy_oy = v.getDay_oy();
		init();
	}
	
	public Path(Visit v, Path p){
		this.user_id = p.getUser_id();
		this.path =  v.getPoiid() + "," + p.getPath();
		this.postPath = v.getPost_id() + "," + p.getPostPath();
		this.path_length = p.getPath_length() + 1;
		this.doy_oy = p.getDoy_oy();
		init();
	}
	
	
	private void init(){
		
		if(this.path.contains(",")){
			this.path_length = this.path.split(",").length;
		}else{
			this.path_length = 1;
		}
		
		try {
			this.checksum = DataProcessUtils.makeSHA1Hash(this.path);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		this.is_full_path = false;
	}
	
	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public String getChecksum() {
		return checksum;
	}

	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getPath_length() {
		return path_length;
	}

	public void setPath_length(int path_length) {
		this.path_length = path_length;
	}

	public boolean isIs_full_path() {
		return is_full_path;
	}

	public void setIs_full_path(boolean is_full_path) {
		this.is_full_path = is_full_path;
	}
	
	public String getPostPath() {
		return postPath;
	}

	public void setPostPath(String postPath) {
		this.postPath = postPath;
	}
	
	public double getDoy_oy() {
		return doy_oy;
	}

	public void setDoy_oy(double doy_oy) {
		this.doy_oy = doy_oy;
	}

	public String toString(){
		return "user_id=" + this.user_id + ", path: "+ this.path;
	}


	
}
