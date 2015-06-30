/**
 * 
 */
package mt.weibo.model;

import weibo4j.org.json.JSONException;
import weibo4j.org.json.JSONObject;

/**
 * @author vincentgong
 *
 */
public class UserJson {
	public String user_id;
	public String json;

	public UserJson(JSONObject jsonObj) {
		user_id = "0";
		json = jsonObj.toString();
		try {
			if (jsonObj.has("id")) {
				user_id = jsonObj.getString("id");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}


	public String getUser_id() {
		return user_id;
	}


	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
}
