/**
 * 
 */
package mt.weibo.crawl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mt.weibo.common.MyLineReader;
import mt.weibo.common.MyLineWriter;


/**
 * @author vincentgong
 *
 */
public class Test {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		// int page = (int) Math.ceil(452/Double.valueOf("50"));
		// double d = 452/50;
		// int page = (int) Math.floor(d);
		// System.out.println(d);
		// System.out.println(Math.floor(d));
//		System.out.println(Utils.getPath());

//		try {
//			MyLineWriter.getInstance().writeLine(
//					Utils.getPath() + "/testWrite.txt", "aaaa");
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
//		String[] keys = Utils.getAccessTokenList();
//		int times = 0;
//		while(times<100){
//			times++;
//			System.out.println("times: "+times+ ", key_id: "+ times%keys.length );
//		}

//		String line = " [IPXv2] [Rounds: 23, Date: Thu May 07 05:51:35 CEST 2015]";
//		int dateStartPosition = line.indexOf("Date: ");
//		int endPosition = line.lastIndexOf("CEST");
//		String strDate = line.substring(dateStartPosition + 6, endPosition - 1);
//		System.out.println(strDate);
		
		
//		MyLineReader mlr = new MyLineReader("/Users/vincentgong/Desktop/keys.txt");
//		mlr.init();
//		while(mlr.hasNextLine()){
//			String line = mlr.nextLine().trim();
//			if(line.equals("")) continue;
//			line = "\"" + line + "\",";
//			System.out.println(line);
//			MyLineWriter.getInstance().writeLine("/Users/vincentgong/Desktop/keys_now.txt", line);
//		}
		
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
//		Calendar cal = new GregorianCalendar();
//		String now = sdf.format(cal.getTime());
//		System.out.println(now);
		
//		String filename = "aaa.txt";
//		System.out.println(filename.split("\\.")[0]);
		
//		MyLineReader mlr = new MyLineReader("/Users/vincentgong/Documents/TUD/Master TUD/A Master Thesis/share/servers/beijing/v1/posts/uid-item.txt");
//		while(mlr.hasNextLine()){
//			String line = mlr.nextLine().split(",")[0];
//			MyLineWriter.getInstance().writeLine("/Users/vincentgong/Documents/TUD/Master TUD/A Master Thesis/share/servers/beijing/v1/posts/uid.txt", line);
//		}
//		mlr.close();
		
		getGeoInfo("{\"type\":\"Point\",\"coordinates\":[-20.068632,-57.519123]}");
		getGeoInfo("{\"type\":\"Point\",\"coordinates\":[20.068632,-57.519123]}");
		getGeoInfo("{\"type\":\"Point\",\"coordinates\":[20.068632,57.519123]}");
	}
	
	private static void getGeoInfo(String geo) {
		String pattern = "[-]{0,1}[0-9]+\\.{0,1}[0-9]*,[-]{0,1}[0-9]+\\.{0,1}[0-9]*";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(geo);
		if (m.find()) {
			if(m.groupCount()>=0){
				String[] coord = m.group(0).split(",");
				double latitude = Double.parseDouble(coord[0]);
				double longitude= Double.parseDouble(coord[1]);
				System.out.println(latitude + ", " + longitude);
			}
		}
	}

}
