/**
 * 
 */
package weibo4j.test;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.commons.codec.binary.Base64;

/***
 * @blog   http://www.iswin.org
 * @author iswin
 */
public class LoginTest {
	public static void main(String[] args) throws Exception {
		System.err.println("开始登陆，获取tiket");
		// 设置微博用户名以及密码
		String ticket = requestAccessTicket("Vincent.gong7@yahoo.com", "student2001");
		if (ticket != "false") {
			System.err.println("获取成功:" + ticket);
			System.err.println("开始获取cookies");
			String cookies = sendGetRequest(ticket, null);
			System.err.println("cookies获取成功:" + cookies);
			System.err.println("开始发送微博");
			sendWeiBoMessage("java robot.", cookies);
			System.err.println("发送微博成功");
		} else
			System.err.println("ticket获取失败，请检查用户名或者密码是否正确!");

	}

	public static String sendGetRequest(String url, String cookies)
			throws MalformedURLException, IOException {
		HttpURLConnection conn = (HttpURLConnection) new URL(url)
				.openConnection();
		conn.setRequestProperty("Cookie", cookies);
		conn.setRequestProperty("Referer",
				"http://login.sina.com.cn/?r=/member/my.php?entry=sso");
				//http://login.sina.com.cn/signup/signin.php?entry=sso
		conn.setRequestProperty(
				"User-Agent",
				"Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:34.0) Gecko/20100101 Firefox/34.0");
		conn.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		BufferedReader read = new BufferedReader(new InputStreamReader(
				conn.getInputStream(), "gbk"));
		String line = null;
		StringBuilder ret = new StringBuilder();
		while ((line = read.readLine()) != null) {
			ret.append(line).append("\n");
		}
		StringBuilder ck = new StringBuilder();
		try {
			for (String s : conn.getHeaderFields().get("Set-Cookie")) {
				ck.append(s.split(";")[0]).append(";");
			}

		} catch (Exception e) {
		}
		return ck.toString();
	}

	public static String requestAccessTicket(String username, String password)
			throws MalformedURLException, IOException {
		username = Base64.encodeBase64(username.replace("@", "%40")
				.getBytes()).toString();

		HttpURLConnection conn = (HttpURLConnection) new URL(
				"https://login.sina.com.cn/sso/login.php?client=ssologin.js(v1.4.11)")
				.openConnection();
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Referer",
				"http://login.sina.com.cn/signup/signin.php?entry=sso");
		conn.setRequestProperty(
				"User-Agent",
				"Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:34.0) Gecko/20100101 Firefox/34.0");
		conn.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		DataOutputStream out = new DataOutputStream(conn.getOutputStream());
		out.writeBytes(String
				.format("entry=sso&gateway=1&from=null&savestate=30&useticket=0&pagerefer=&vsnf=1&su=%s&service=sso&sp=%s&sr=1280*800&encoding=UTF-8&cdult=3&domain=sina.com.cn&prelt=0&returntype=TEXT",
						URLEncoder.encode(username), password));
		
		System.out.println("username: " + username);
		System.out.println("password: " + password);
		out.flush();
		out.close();
		BufferedReader read = new BufferedReader(new InputStreamReader(
				conn.getInputStream(), "gbk"));
		String line = null;
		StringBuilder ret = new StringBuilder();
		while ((line = read.readLine()) != null) {
			System.out.println(line);
			ret.append(line).append("\n");
		}
		String res = null;
		try {
			res = ret.substring(ret.indexOf("https:"),
					ret.indexOf(",\"https:") - 1).replace("\\", "");
		} catch (Exception e) {
			res = "false";
		}
		return res;
	}

	@SuppressWarnings("deprecation")
	public static String sendWeiBoMessage(String message, String cookies)
			throws MalformedURLException, IOException {
		HttpURLConnection conn = (HttpURLConnection) new URL(
				"http://www.weibo.com/aj/mblog/add?ajwvr=6").openConnection();
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Cookie", cookies);
		conn.setRequestProperty("Referer",
				"http://www.weibo.com/u/2955825224/home?topnav=1&wvr=6");
		conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");
		conn.setRequestProperty(
				"User-Agent",
				"Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:34.0) Gecko/20100101 Firefox/34.0");
		conn.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		DataOutputStream out = new DataOutputStream(conn.getOutputStream());
		out.writeBytes("location=v6_content_home&appkey=&style_type=1&pic_id=&text="
				+ URLEncoder.encode(message)
				+ "&pdetail=&rank=0&rankid=&module=stissue&pub_type=dialog&_t=0");
		out.flush();
		out.close();
		BufferedReader read = new BufferedReader(new InputStreamReader(
				conn.getInputStream(), "gbk"));
		String line = null;
		StringBuilder ret = new StringBuilder();
		while ((line = read.readLine()) != null) {
			ret.append(line).append("\n");
		}
		return ret.toString();
	}
}
