package mt.weibo.crawl.general.dataprocess.semantic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import mt.weibo.common.MyStringBuffer;
import mt.weibo.model.Word;

public class LtpCloud {

	public static void main(String[] args) {
		LtpCloud.getInstance()
				.detect("【最新#全国快递投诉排行# 你觉得靠谱吗？】按照国家邮政局的8月份申诉情况统计，德邦、速尔、国通三家占据投诉榜前三；其中，德邦的\"丢件损毁\"投诉量高居榜首；国通延误情况最严重。与此同时，表现最好的是DHL、京东和顺丰。网友：EMS不进前三不科学~你觉得呢？[思考]");
	}

	private String api_key = "l4a6G1D2wb1oxPtMwd4FRp5UPnjAIKLAKMPlAcPQ";
	private String pattern = "all"; // all or ner
	private String format = "json";
	private static LtpCloud instance;

	private LtpCloud() {

	}

	public static LtpCloud getInstance() {
		if (instance == null) {
			instance = new LtpCloud();
		}
		return instance;
	}

	public List<Word> detect(String text) {
		List<Word> list = new ArrayList<Word>();
		MyStringBuffer msb = new MyStringBuffer();
		try {
			text = URLEncoder.encode(text, "utf-8");
			URL url = new URL("http://ltpapi.voicecloud.cn/analysis/?"
					+ "api_key=" + api_key + "&" + "text=" + text + "&"
					+ "format=" + format + "&" + "pattern=" + pattern);
			URLConnection conn = url.openConnection();
			conn.connect();

			BufferedReader innet = new BufferedReader(new InputStreamReader(
					conn.getInputStream(), "utf-8"));

			String line;
			while ((line = innet.readLine()) != null) {
				// System.out.println(line);
				msb.appendLine(line);
			}
			innet.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		String json = msb.toString();
		System.out.println(json);
		list = getWordList(json);

		return list;
	}


	private List<Word> getWordList(String json) {
		List<Word> list = new ArrayList<Word>();
		JSONArray result = null;
		try {
			result = new JSONArray(json).getJSONArray(0);
			for (int i = 0; i < result.length(); i++) {
				JSONArray ja = result.getJSONArray(i);
				for (int j = 0; j < ja.length(); j++) {
					JSONObject obj = ja.getJSONObject(j);

					int id = obj.getInt("id");
					String cont = obj.getString("cont");
					String pos = obj.getString("pos");
					String ne = obj.getString("ne");
					
//					if("O".equals(ne)){
//						continue;
//					}
					
					int neCategory = identifyNer(ne);
					
					int parent = 0;
					if (obj.has("parent")) {
						parent = obj.getInt("parent");
					}

					String relate = "";
					if (obj.has("relate")) {
						relate = obj.getString("relate");
					}

					String arg = "";
					if (obj.has("arg")) {
						arg = obj.getJSONArray("arg").toString();
					}

					Word word = new Word(id, cont, pos, ne, neCategory, parent, relate, arg);
					list.add(word);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	/*
	 * NER return field 
	 * Nm=数词(number) Ni=机构名(organization) Ns=机构名(organization)
	 * Nh=人名(human) Nt=时间(time) Nr=日期(date) Nz=专有名词(noun) O 表示这个词不是 NE S
	 * 表示这个词单独构成一个 NE B 表示这个词为一个 NE 的开始 I 表示这个词为一个 NE 的中间 E 表示这个词位一个 NE 的结尾
	 * 
	 * eg. 巴格达 南部 地区 B-Ns I-Ns E-Ns
	 */
	private int identifyNer(String ne) {
		int nerCategory = 0;
		if(ne==null || ne=="O" || "O".equals(ne)){
			nerCategory = 0;
		}else if(ne.contains("-")){
			String pat = ne.split("-")[1];
			if(null==pat || "".equals(pat)){
				nerCategory = 0;
			}else{
				if(pat.equals("Nm")){
					nerCategory = 1;
				}else if(pat.equals("Ni") || pat.equals("Ns")){
					nerCategory = 2;
				}else if(pat.equals("Nh")){
					nerCategory = 3;
				}else if(pat.equals("Nt")){
					nerCategory = 4;
				}else if(pat.equals("Nr")){
					nerCategory = 5;
				}else if(pat.equals("Nz")){
					nerCategory = 6;
				}
			}
		}
		return nerCategory;
	}
}
