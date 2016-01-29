package mt.weibo.crawl.general.dataprocess.pathpatterns;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TimeZone;

import mt.weibo.crawl.general.dataprocess.common.DataProcessUtils;
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
	private String pathTableName = "socialmedia.path_in_scope_2";
	private String dbname = "shenzhen";
	private MyDBConnection mdbc;
	private Connection con;

	public static void main(String[] args) {
		int port = 5432;
		String dbname = "shenzhen";

		Options options = new Options();
		options.addOption("h", "help", false, "print this message");
		options.addOption("p", true, "database port");
		options.addOption("d", true, "database name");
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd;
		try {
			cmd = parser.parse(options, args);

			if (cmd.hasOption("help")) {
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp(PathExtractor.class.getName(), options);
			} else {
				if (cmd.hasOption("p")) {
					if (cmd.getOptionValue("p") != null) {
						port = Integer.valueOf(cmd.getOptionValue("p"));

					}
				}

				if (cmd.hasOption("d")) {
					if (cmd.getOptionValue("d") != null) {
						dbname = cmd.getOptionValue("d");
					}
				}

				PathExtractor pe = new PathExtractor(port, dbname);
				pe.process();
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public PathExtractor(int port, String dbname) {
		this.port = port;
		this.dbname = dbname;
		if (mdbc == null) {
			mdbc = new MyDBConnection(this.port, this.dbname);
			con = mdbc.getDBConnection();
		}
	}

	private void process() {
		// step 1: get user_id, in the scope
		System.out.println("Start extracting...");

		Statement stmt = null;
		try {
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			String querySql = "select distinct user_id from "
					+ this.userTableName
					+ " where user_id not in (select distinct user_id from "
					+ this.pathTableName + " )";
			System.out.println(querySql);
			ResultSet userRS = stmt.executeQuery(querySql);
			int npUserId = 0;
			while (userRS.next()) {
				String user_id = userRS.getString("user_id");
				npUserId++;
				System.out.println("Now processing number: " + npUserId);
				extractPathForUser(user_id);
			}
			userRS.close();

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
		System.out.println("[Now Processing user_id]: " + user_id);

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

			// step 4: dedu the daymap, delete the visit if it has only one in o
			// day
			Map<String, List<Visit>> filteredVisitMapByDays = filteDayVisits(visitMapByDays);

			// step 5: order the List
			Map<String, List<Visit>> orderedFilteredVisitMapByDays = orderDayVisits(filteredVisitMapByDays);

			// step 6: iterator each list in the map, and generate the paths
			// item
//			List<Path> pathItemList = generateStorePathItem(orderedFilteredVisitMapByDays);
			List<Path> pathItemList = pathItemGenerator(orderedFilteredVisitMapByDays);
			

			// step 7: store them into the db
			StorePathItems(pathItemList);

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
		}
	}

	private List<Path> generateStorePathItem(
			Map<String, List<Visit>> orderedFilteredVisitMapByDays) {
		List<Path> pathItemList = new ArrayList<Path>();
		Iterator<String> it = orderedFilteredVisitMapByDays.keySet().iterator();
		while (it.hasNext()) {
			String dayKey = it.next();
			List<Visit> visitList = orderedFilteredVisitMapByDays.get(dayKey);
			int npPlaces = visitList.size();
			String path = "";
			int pathLength = 0;
			boolean isFullPath;
			for (Visit v : visitList) {
				isFullPath = false;
				String user_id = v.getUser_id();
				path = path + "," + v.getPoiid();
				pathLength++;
				if (pathLength == npPlaces) {
					isFullPath = true;
				}
				if (path.startsWith(",")) {
					// ",xxxx,tttt,yyyy" to "xxxx,tttt,yyyy"
					path = path.substring(1);
				}
				String checksum = "";
				try {
					checksum = DataProcessUtils.makeSHA1Hash(path);
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}

				Path pathItem = new Path(user_id, dayKey, checksum, pathLength,
						path, isFullPath);
				pathItemList.add(pathItem);
			}
		}
		return pathItemList;
	}
	
	
	private List<Path> pathItemGenerator(
			Map<String, List<Visit>> orderedFilteredVisitMapByDays) {
		List<Path> pathItemList = new ArrayList<Path>();
		
		Iterator<String> it = orderedFilteredVisitMapByDays.keySet().iterator();
		while (it.hasNext()) {
			String dayKey = it.next();
			List<Visit> visitList = orderedFilteredVisitMapByDays.get(dayKey);
			int npPlaces = visitList.size();
			
			Queue<Visit> que = new LinkedList<Visit>();
			for(Visit v : visitList){
				que.add(v);
			}
			
			List<Path> simplePathItemList = pathItemCalculator(que);
			
			for(Path p: simplePathItemList){
				p.setDay(dayKey);
				if (p.getPath_length() == npPlaces) {
					p.setIs_full_path(true);
				}
				pathItemList.add(p);
			}
		}
		
		
		return pathItemList;
	}

	private List<Path> pathItemCalculator(Queue<Visit> que) {
		List<Path> CombinedPathItemList = new ArrayList<Path>();
		List<Path> pathItemList = new ArrayList<Path>();;
		Visit visit = que.poll();
		CombinedPathItemList.add(new Path(visit));
		if(!que.isEmpty()){
			pathItemList = pathItemCalculator(que);
			
			for(Path p: pathItemList){
				if(p.getPath_length()>1){
					CombinedPathItemList.add(p);
				}
				Path combinePath = new Path(p, visit);
				CombinedPathItemList.add(combinePath);
			}
		}
		
		return CombinedPathItemList;
	}
	
	

	private void StorePathItems(List<Path> list) {
		if (list.size() < 1) {
			return;
		}

		String sql = "insert into "
				+ this.pathTableName
				+ " (user_id, day, checksum, path, path_length, is_full_path) values (?,?,?,?,?,?)";
		System.out.println(sql);
		try {
			PreparedStatement inserPoiListPs = con.prepareStatement(sql);

			for (Path p : list) {
				try {
					inserPoiListPs.setString(1, p.getUser_id());
					inserPoiListPs.setString(2, p.getDay());
					inserPoiListPs.setString(3, p.getChecksum());
					inserPoiListPs.setString(4, p.getPath());
					inserPoiListPs.setInt(5, p.getPath_length());
					inserPoiListPs.setBoolean(6, p.isIs_full_path());
					inserPoiListPs.executeUpdate();
				} catch (SQLException e) {
					if (e.getMessage() != null
							&& e.getMessage()
									.contains(
											"violates unique constraint \"path_in_scope_pkey\"")) {
						System.err.println(e.getMessage());
						System.out.println();
					} else {
						e.printStackTrace();
					}
					continue;
				}

			}
			inserPoiListPs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private Map<String, List<Visit>> orderDayVisits(
			Map<String, List<Visit>> filteredVisitMapByDays) {
		Map<String, List<Visit>> map = new HashMap<String, List<Visit>>();

		Iterator<String> it = filteredVisitMapByDays.keySet().iterator();
		while (it.hasNext()) {
			String dayKey = it.next();
			List<Visit> visitList = filteredVisitMapByDays.get(dayKey);
			Comparator<Visit> comparator = new Comparator<Visit>() {

				@Override
				public int compare(Visit o1, Visit o2) {
					long result = o1.getTimestamp() - o2.getTimestamp();
					if (result > 0) {
						return 1;
					} else {
						return -1;
					}
				}
			};

			Collections.sort(visitList, comparator);
			map.put(dayKey, visitList);
		}
		return map;
	}

	private Map<String, List<Visit>> filteDayVisits(
			Map<String, List<Visit>> visitMapByDays) {
		Map<String, List<Visit>> map = new HashMap<String, List<Visit>>();
		Iterator<String> it = visitMapByDays.keySet().iterator();
		while (it.hasNext()) {
			String dayKey = it.next();
			List<Visit> l = visitMapByDays.get(dayKey);
			Map<String, Visit> placeMap = new HashMap<String, Visit>();
			for (Visit v : l) {
				String poiid = v.getPoiid();
				if (!placeMap.containsKey(poiid)) {
					placeMap.put(poiid, v);
				}
			}

			List<Visit> poiList = new ArrayList<Visit>();
			Iterator<String> itt = placeMap.keySet().iterator();
			while (itt.hasNext()) {
				String key = itt.next();
				Visit v = placeMap.get(key);
				poiList.add(v);
			}
			int size = poiList.size();
			if (size > 1) {
				map.put(dayKey, poiList);
			}
		}
		if (map.size() > 0) {
			System.out.println("multiple places.");
		}
		return map;
	}

	private Map<String, List<Visit>> daySpliter(List<Visit> visitList) {
		Map<String, List<Visit>> dayMap = new HashMap<String, List<Visit>>();
		for (Visit v : visitList) {
			String day = getDay(v.getTimestamp());
			if (dayMap.containsKey(day)) {
				List<Visit> l = (List<Visit>) dayMap.get(day);
				l.add(v);
				dayMap.put(day, l);
			} else {
				List<Visit> l = new ArrayList<Visit>();
				l.add(v);
				dayMap.put(day, l);
			}
		}
		return dayMap;
	}

	private String getDay(Long timestamp) {
		Date date = new Date(timestamp); // *1000 is to convert seconds to
											// milliseconds
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
		// // the format of your date
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // the format
																	// of your
																	// date
		sdf.setTimeZone(TimeZone.getTimeZone("GMT+8")); // give a timezone
														// reference for
														// formating
		String formattedDate = sdf.format(date);
		System.out.println(formattedDate);
		return formattedDate;
	}
}
