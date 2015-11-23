  package mt.weibo.crawl.general.dataprocess.common;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import mt.weibo.common.LocationSeparator;
import mt.weibo.common.MyLineWriter;
import weibo4j.model.Status;
import weibo4j.model.User;

public class DataProcessUtils {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public static void extractPOIinfo(List<Status> statusList) {
		for(Status s: statusList){
//			String line = s.getAnnotations();
//			if(!line.contains("poiid")){
//				continue;
//			}
			String line = s.getPoiid();
			if("".equals(line)){
				continue;
			}
			try {
				MyLineWriter.getInstance().writeLine("/Users/vincentgong/Documents/TUD/Master TUD/A Master Thesis/share/IPX/2zhen/crawldata/mylog-workdesk/userpost-sep10/json/post-json-2015090817-poiid.txt", s.getAnnotations());
				MyLineWriter.getInstance().writeLine("/Users/vincentgong/Documents/TUD/Master TUD/A Master Thesis/share/IPX/2zhen/crawldata/mylog-workdesk/userpost-sep10/json/post-json-2015090817-poiid.txt", line);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void extractLocationInfo(List<Status> statusList) {
		for(Status s: statusList){
			User u = s.getUser();
			if(u!=null){
				String line = u.getLocation();
				if(line!=null && !"".equals(line)){
					line = line.trim();
					try {
						MyLineWriter.getInstance().writeLine("/Users/vincentgong/Documents/TUD/Master TUD/A Master Thesis/share/IPX/2zhen/crawldata/mylog-workdesk/userpost-sep10/json/post-json-2015090817-user-location.txt", line);
						System.out.println(line);
						LocationSeparator ls = new LocationSeparator(line);
						System.out.println(ls.toString());
						MyLineWriter.getInstance().writeLine("/Users/vincentgong/Documents/TUD/Master TUD/A Master Thesis/share/IPX/2zhen/crawldata/mylog-workdesk/userpost-sep10/json/post-json-2015090817-user-location.txt", ls.toString());
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	/**
	 * 
	 * @param lat1
	 * @param lng1
	 * @param lat2
	 * @param lng2
	 * @return Distance in meters
	 */
	public static float distance(Double lat1, Double lng1, Double lat2,
			Double lng2) {
		double earthRadius = 3958.75;
		double dLat = Math.toRadians(lat2 - lat1);
		double dLng = Math.toRadians(lng2 - lng1);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
				+ Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2)) * Math.sin(dLng / 2)
				* Math.sin(dLng / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double dist = earthRadius * c;

		int meterConversion = 1609;

		return (float) (dist * meterConversion);
	}
	
	/*
	 * create Message Digest hashes
	 */
	public static String makeSHA1Hash(String input)
            throws NoSuchAlgorithmException, UnsupportedEncodingException
        {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            md.reset();
            byte[] buffer = input.getBytes("UTF-8");
            md.update(buffer);
            byte[] digest = md.digest();

            String hexStr = "";
            for (int i = 0; i < digest.length; i++) {
                hexStr +=  Integer.toString( ( digest[i] & 0xff ) + 0x100, 16).substring( 1 );
            }
            return hexStr;
        }
	
}
