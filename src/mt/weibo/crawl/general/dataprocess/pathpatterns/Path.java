package mt.weibo.crawl.general.dataprocess.pathpatterns;

public class Path {
	private String user_id;
	private String day;
	private String checksum;
	private String path;
	private int path_length;
	
	public Path(String user_id, String day, String checksum, int path_length, String path){
		this.user_id = user_id;
		this.day = day;
		this.checksum = checksum;
		this.path = path;
		this.path_length = path_length;
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
	
	
}
