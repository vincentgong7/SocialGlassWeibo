package mt.weibo.crawl.general.dataprocess;

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
	
	
}
