package mt.weibo.crawl.general.dataprocess.pathpatterns;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import mt.weibo.crawl.ArgsTemplate;
import mt.weibo.db.MyDBConnection;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class PathExtractor {

	private int port = 5432;
	private String statusTableName = "socialmedia.post_in_scope";
	private String userTableName = "socialmedia.user_in_scope";
	private String pathTableName = "socialmedia.path_in_scope";
	private MyDBConnection mdbc;
	private Connection con;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int port = 5432;

		Options options = new Options();
		options.addOption("h", "help", false, "print this message");
		options.addOption("p", true, "database port");
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd;
		try {
			cmd = parser.parse(options, args);

			if (cmd.hasOption("help")) {
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp(ArgsTemplate.class.getName(), options);
			}
			if (cmd.hasOption("p")) {
				if (cmd.getOptionValue("p") != null) {
					port = Integer.valueOf(cmd.getOptionValue("p"));
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		PathExtractor pe = new PathExtractor(port);
		pe.process();
	}

	public PathExtractor(int port) {
		this.port = port;
		if (mdbc == null) {
			mdbc = new MyDBConnection(port);
			con = mdbc.getDBConnection();
		}
	}

	private void process() {
		// step 1: get user_id, in the scope
		Statement stmt = null;
		try {

			stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			String querySql = "select distinct user_id from "
					+ this.userTableName + ")";

			ResultSet userRS = stmt.executeQuery(querySql);
			while (userRS.next()) {
				String user_id = userRS.getString("user_id");
				extractPathForUser(user_id);
			}
			userRS.close();

			//
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (mdbc != null) {
				mdbc.close();
			}
		}
	}

	private void extractPathForUser(String user_id) {
		// step 2: get places for this user
		List<Visit> visitList = new ArrayList<Visit>();
		Statement stmt = null;
		try {
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			String querySql = "SELECT post_id, poiid, timestamp FROM "
					+ statusTableName + " where user_id = '" + user_id + "'";

			ResultSet postOfUser = stmt.executeQuery(querySql);

			while (postOfUser.next()) {
				String post_id = postOfUser.getString("post_id");
				String poiid = postOfUser.getString("poiid");
				Long timestamp = postOfUser.getLong("timestamp");
				
				Visit visit = new Visit(post_id, user_id, poiid, timestamp);
				visitList.add(visit);
			}
			postOfUser.close();
			
			// step 3: day spliter
			Map<String, List<Visit>> visitMapByDays = daySpliter(visitList);
			
			// step 4: dedu the daymap, delete the visit if it has only one in o day
			Map<String, List<Visit>> filteredVisitMapByDays =  filteDayVisits(visitMapByDays);
			
			// step 5: order the List
			Map<String, List<Visit>> orderedFilteredVisitMapByDays = orderDayVisits(filteredVisitMapByDays);
			
			// step 6: iterator each list in the map, and generate the paths item, store them into the db
			generateStorePathItem(orderedFilteredVisitMapByDays);
			
			System.out.println("done!");
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (mdbc != null) {
				mdbc.close();
			}
		}
	}

	private void generateStorePathItem(
			Map<String, List<Visit>> orderedFilteredVisitMapByDays) {
		Iterator<String> it = orderedFilteredVisitMapByDays.keySet().iterator();
		while(it.hasNext()){
			String dayKey = it.next();
			List<Visit> visitList = orderedFilteredVisitMapByDays.get(dayKey);
			// TODO: iterator the list
			
			// TODO: store them into path table
			
		}
	}

	private Map<String, List<Visit>> orderDayVisits(
			Map<String, List<Visit>> filteredVisitMapByDays) {
		Map<String, List<Visit>> map = new HashMap<String, List<Visit>>();
		
		Iterator<String> it = filteredVisitMapByDays.keySet().iterator();
		while(it.hasNext()){
			String dayKey = it.next();
			List<Visit> visitList = filteredVisitMapByDays.get(dayKey);
			Comparator<Visit> comparator = new Comparator<Visit>(){

				@Override
				public int compare(Visit o1, Visit o2) {
					long result = o2.getTimestamp() - o1.getTimestamp();
					if(result>0){
						return 1;
					}else{
						return -1;
					}
				}
			};
			
			Collections.sort(visitList, comparator);
			map.put(dayKey, visitList);
		}
		return map;
	}

	private Map<String, List<Visit>> filteDayVisits(Map<String, List<Visit>> visitMapByDays) {
		Map<String, List<Visit>> map = new HashMap<String, List<Visit>>();
		Iterator<String> it = visitMapByDays.keySet().iterator();
		while(it.hasNext()){
			String dayKey = it.next();
			List<Visit> l = visitMapByDays.get(dayKey);
			Map<String, Visit> placeMap = new HashMap<String, Visit>();
			for(Visit v: l){
				String poiid = v.getPoiid();
				if(!placeMap.containsKey(poiid)){
					placeMap.put(poiid, v);
				}
			}
			
			List<Visit> poiList = new ArrayList<Visit>();
			Iterator<String> itt = placeMap.keySet().iterator();
			while(itt.hasNext()){
				String key = itt.next();
				Visit v = placeMap.get(key);
				poiList.add(v);
			}
			int size = poiList.size();
			if(size > 1){
				map.put(dayKey, poiList);
			}
		}
		
		return map;
	}

	private Map<String, List<Visit>> daySpliter(List<Visit> visitList) {
		Map<String, List<Visit>> dayMap = new HashMap<String, List<Visit>>();
		for(Visit v: visitList){
			String day = getDay(v.getTimestamp());
			if(dayMap.containsKey(day)){
				List<Visit> l = (List<Visit>) dayMap.get(day);
				l.add(v);
				dayMap.put(day, l);
			}else{
				List<Visit> l = new ArrayList<Visit>();
				l.add(v);
				dayMap.put(day, l);
			}
		}
		return dayMap;
	}

	private String getDay(Long timestamp) {
		Date date = new Date(timestamp*1000L); // *1000 is to convert seconds to milliseconds
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z"); // the format of your date
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // the format of your date
		sdf.setTimeZone(TimeZone.getTimeZone("GMT+8")); // give a timezone reference for formating
		String formattedDate = sdf.format(date);
		System.out.println(formattedDate);
		return formattedDate;
	}
}
