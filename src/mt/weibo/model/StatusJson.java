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
public class StatusJson {
	public String status_id;
	public String json;

	public StatusJson(JSONObject jsonObj) {
		status_id = "0";
		json = jsonObj.toString();
		try {
			if (jsonObj.has("id")) {
				status_id = jsonObj.getString("id");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getStatus_id() {
		return status_id;
	}

	public void setStatus_id(String status_id) {
		this.status_id = status_id;
	}

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}
}
