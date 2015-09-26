package mt.weibo.common;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ShortURL {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String shortURL = "http://t.cn/R2WxsRa";
		System.out.println(expandURL(shortURL));
	}

	public static String expandURL(String shortURL) {
		String location = "";
		URL url;
		try {
			url = new URL(shortURL);
			final HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();
			urlConnection.setInstanceFollowRedirects(false);
			location = urlConnection.getHeaderField("location");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return location;
	}
}
