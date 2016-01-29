package mt.weibo.crawl.general.dataprocess.pathpatterns;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import mt.weibo.crawl.general.dataprocess.common.DataProcessUtils;

public class Path {
	private String user_id;
	private String day;
	private String checksum;
	private String path;
	private int path_length;
	private boolean is_full_path;
	
	public Path(String user_id, String day, String checksum, int path_length, String path, boolean is_full_path){
		this.user_id = user_id;
		this.day = day;
		this.checksum = checksum;
		this.path = path;
		this.path_length = path_length;
		this.is_full_path = is_full_path;
		init();
	}

	public Path(Visit v){
		this.user_id = v.getUser_id();
		this.path = v.getPoiid();
		init();
	}
	
	public Path(Path p, Visit v){
		this.user_id = p.getUser_id();
		this.path =  v.getPoiid() + "," + p.getPath();
		this.path_length = p.getPath_length() + 1;
		init();
	}
	
	
	private void init(){
		
		if(this.path.contains(",")){
			this.path_length = this.path.split(",").length + 1;
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
	
	public String toString(){
		return "user_id=" + this.user_id + ", path"+ this.path;
	}
	
}
