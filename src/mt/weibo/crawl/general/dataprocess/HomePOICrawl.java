package mt.weibo.crawl.general.dataprocess;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import weibo4j.model.District;
import weibo4j.model.Places;
import mt.weibo.crawl.general.CrawlTool;
import mt.weibo.crawl.general.dataprocess.common.POICrawler;
import mt.weibo.db.MyDBConnection;

public class HomePOICrawl {

	
	private MyDBConnection mdbc;
	private String userHomeTablename = "socialmedia.home";
	private int interval = 2;
	private String crawlRange = "300";
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int port = 5432;
		if (args.length > 0) {
			port = Integer.valueOf(args[0]);
		}
		
		HomePOICrawl hpc = new HomePOICrawl(port);
		hpc.process();
		
	}
	
	public HomePOICrawl(int port){
		if (mdbc == null) {
			mdbc = new MyDBConnection(port);
		}
	}

	private void process() {
		Statement stmtUser = null;
		try {
			stmtUser = mdbc.getDBConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
		

		ResultSet homeSet = stmtUser
				.executeQuery("SELECT user_id, home_lat, home_lon, home_poiid, home_district, pois_json, is_poi_crawled, is_nearby_crawled FROM "
						+ userHomeTablename
						+ " where home_lat IS NOT NULL and pois_json IS NULL");
		
		while(homeSet.next()){
			String userID = homeSet.getString("user_id");
			double lat = homeSet.getDouble("home_lat");
			double lon = homeSet.getDouble("home_lon");
			
			CrawlTool.sleep(this.interval, "[POIID_HOME]");
			String json = POICrawler.queryPoi(lat, lon, String.valueOf(crawlRange));
			List<Places> poiList = POICrawler.constructPlaces(json);
			
			if (poiList == null || poiList.size() == 0) {
				homeSet.updateBoolean("is_poi_crawled", true);
				homeSet.updateRow();
			}else{
				Places place;
				place = poiList.get(0);
				homeSet.updateString("home_poiid", place.getPoiid());
				District district = place.getDistrict();
				if(district!=null){
					homeSet.updateString("home_poiid", place.getDistrict().getDistrictID());
				}
				homeSet.updateBoolean("is_poi_crawled", true);
				homeSet.updateString("pois_json", json);
				
				homeSet.updateRow();
				POICrawler.insertPoiInfo(place);
			}
		}
		
		stmtUser.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (mdbc != null) {
				mdbc.close();
			}

		}
	}

}
