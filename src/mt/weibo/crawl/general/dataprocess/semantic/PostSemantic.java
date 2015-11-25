package mt.weibo.crawl.general.dataprocess.semantic;

public class PostSemantic {
	private String post_id;
	private String user_id;
	private String content;
	private String json;

	public PostSemantic(String post_id, String user_id, String content) {
		// TODO Auto-generated constructor stub
		this.post_id = post_id;
		this.user_id = user_id;
		this.content = content;
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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

	public String toString() {
		String line = "post_id: " + post_id + ", user_id: " + user_id;
		return line;
	}

}
