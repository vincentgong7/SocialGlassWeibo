package mt.weibo.crawl.general.dataprocess;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mt.weibo.db.MyDBConnection;
import ch.hsr.geohash.GeoHash;
import ch.hsr.geohash.WGS84Point;

public class HomeLocator {

	private MyDBConnection mdbc;
	private Connection con;
//	private String userTablename = "socialmedia.user";
//	private String postTablename = "socialmedia.post";

	public static void main(String[] args) {
		// usage: java -jar HomeLocator.jar [port]
		int port = 5432;
		if (args.length > 0) {
			port = Integer.valueOf(args[0]);
		}
		HomeLocator homelocator = new HomeLocator(port);
		homelocator.process();
	}

	public HomeLocator(int port) {
		if (mdbc == null) {
			mdbc = new MyDBConnection(port);
			con = mdbc.getDBConnection();
		}
	}

	public void process() {
		System.out.println("Working on Localhost.");
		System.out.println("Start identify user home location.....");
		Statement stmtUser = null;
		try {
			stmtUser = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			ResultSet usersSet = stmtUser
					.executeQuery("SELECT distinct user_id from socialmedia.post_for_home where user_id not in (select user_id from socialmedia.home)");

			int k = 0;
			while (usersSet.next()) {
				k++;
				String userID = usersSet.getString("user_id");
				String line = "Processing. User ID:"
						+ userID + ", Current: " + k;
				System.out.println(line);
				Statement stmtPost = con.createStatement(
						ResultSet.TYPE_SCROLL_SENSITIVE,
						ResultSet.CONCUR_UPDATABLE);

				ResultSet postSet = stmtPost
						.executeQuery("SELECT user_id, lat, lon FROM socialmedia.post_for_home"
								+ " where user_id = '" + userID + "'");

				List<WGS84Point> coordList = new ArrayList<WGS84Point>();
				int i = 0;
				while (postSet.next()) {
					double lat = postSet.getDouble("lat");
					double lon = postSet.getDouble("lon");
					WGS84Point point;
					try {
						point = new WGS84Point(lat, lon);
					} catch (java.lang.IllegalArgumentException e) {
						continue;
					}
					coordList.add(point);
					i++;
				}

				System.out.println(i + " places added for this user.");
				if (coordList.size() > 0) {
					WGS84Point homePoint = findHome(coordList);
					double homeLat = homePoint.getLatitude();
					double homeLongi = homePoint.getLongitude();
					System.out.println("Home address: (" + homeLat + ","
							+ homeLongi + ").");
					stmtPost.executeUpdate("INSERT into socialmedia.home (user_id, home_lat, home_lon, places_in_city) "
							+ "VALUES ('"
							+ userID
							+ "',"
							+ homeLat
							+ ","
							+ homeLongi + ", " + i + ")");

					System.out.println("Stored.");
				}

				postSet.close();
				stmtPost.close();
				System.out.println();
				System.out.println();
			}
			usersSet.close();
			stmtUser.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// WGS84Point homePoint = findHome()
	}

	/**
	 * Find the "home" (ie, the place with the most posts)
	 * 
	 * @param points
	 *            list of coordinates
	 * @return the coordinates of the home
	 */
	public WGS84Point findHome(List<WGS84Point> points) {
		Map<GeoHash, List<WGS84Point>> grid = new HashMap<GeoHash, List<WGS84Point>>();

		int precision = 2;
		for (WGS84Point point : points) {
			try {
				GeoHash hash = GeoHash.withCharacterPrecision(
						point.getLatitude(), point.getLongitude(), precision);

				if (!grid.containsKey(hash)) {
					grid.put(hash, new ArrayList<WGS84Point>());
				}

				grid.get(hash).add(point);
			} catch (IllegalArgumentException e) {
				System.out.println("Something wrong with home detection.");
				e.printStackTrace();
			}

		}
		return doFindHome(grid, precision + 1);
	}

	private WGS84Point doFindHome(Map<GeoHash, List<WGS84Point>> grid,
			int precision) {

		GeoHash center = getSquareWithMostPoints(grid);

		if (precision > 8) {
			return center.getBoundingBoxCenterPoint();
		}

		List<GeoHash> adjacent = new ArrayList<GeoHash>(Arrays.asList(center
				.getAdjacent()));
		adjacent.add(center);

		Map<GeoHash, List<WGS84Point>> newgrid = new HashMap<GeoHash, List<WGS84Point>>();

		for (GeoHash neighbour : adjacent) {
			List<WGS84Point> coords = grid.get(neighbour);
			if (coords != null) {
				for (WGS84Point point : coords) {
					GeoHash hash = GeoHash.withCharacterPrecision(
							point.getLatitude(), point.getLongitude(),
							precision);

					if (!newgrid.containsKey(hash)) {
						newgrid.put(hash, new ArrayList<WGS84Point>());
					}
					newgrid.get(hash).add(point);
				}
			}

		}

		return doFindHome(newgrid, precision + 1);

	}

	private GeoHash getSquareWithMostPoints(Map<GeoHash, List<WGS84Point>> grid) {
		Map.Entry<GeoHash, List<WGS84Point>> maxEntry = null;

		for (Map.Entry<GeoHash, List<WGS84Point>> entry : grid.entrySet()) {
			if (maxEntry == null
					|| entry.getValue().size() > maxEntry.getValue().size()) {
				maxEntry = entry;
			}
		}
		return maxEntry.getKey();
	}

	/**
	 * Get a foursquare Location object (that can be used for a Venue) for the
	 * specificied coordinates
	 * 
	 * @param point
	 * @return A Foursquare Location object
	 */
	// public Location getLocation(WGS84Point point) {
	//
	// // Toponym adminArea = getViaGeoNames(point);
	// // if (adminArea != null) {
	// // return getSimpleName(point, adminArea.getLatitude(),
	// // adminArea.getLongitude());
	// // } else {
	// return getSimpleName(point, point.getLatitude(), point.getLongitude());
	// // }
	//
	// }

	/*
	 * private Toponym getViaGeoNames(WGS84Point point) { // GeoHash hash =
	 * GeoHash.withCharacterPrecision(point.getLatitude(), point.getLongitude(),
	 * 9); WebService.setUserName("christitos"); try { List<Toponym> results =
	 * WebService.findNearby(point.getLatitude(), point.getLongitude(),
	 * FeatureClass.A, new String[] { "ADM2" }); if (results.size() > 0) {
	 * return results.get(0); } } catch (Exception e) {
	 * logger.error("Exception retrieving geoname", e); } return null; }
	 */

	/**
	 * Get the name of the location of the specified coordinates.
	 * 
	 * Uses Google Maps API for geocoding. The API calls are cached in Redis
	 * 
	 * @param point
	 * @param latitude
	 * @param longitude
	 * @return
	 */
	// private Location getSimpleName(WGS84Point point, double latitude,
	// double longitude) {
	//
	// Location loc = new Location();
	// loc.setLatitude(point.getLatitude());
	// loc.setLongitude(point.getLongitude());
	//
	// GeoHash hash = GeoHash.withCharacterPrecision(latitude, longitude, 7);
	// Gson gson = new Gson();
	//
	// String resString = cache.get("geocode3:" + hash.toBase32());
	//
	// GeocodingResult[] results = null;
	// if (resString != null) {
	// if (!resString.equals("-1")) {
	// results = gson.fromJson(resString, GeocodingResult[].class);
	// }
	// } else {
	// results = google.doGeoCodingRequest(
	// new LatLng(latitude, longitude), AddressType.LOCALITY,
	// AddressType.POLITICAL);
	// }
	//
	// if (results != null && results.length > 0) {
	// for (AddressComponent component : results[0].addressComponents) {
	// List<AddressComponentType> types = Arrays
	// .asList(component.types);
	// if (types.contains(AddressComponentType.LOCALITY)) {
	// loc.setCity(component.longName);
	// } else if (types.contains(AddressComponentType.COUNTRY)) {
	// String country = component.longName;
	// if (country.equals("The Netherlands")) {
	// country = "Netherlands";
	// }
	// loc.setCountry(country);
	// loc.setCountryCode(component.shortName);
	// }
	// }
	// if (loc.getCity() == null) {
	// for (AddressComponent component : results[0].addressComponents) {
	// List<AddressComponentType> types = Arrays
	// .asList(component.types);
	// if (types
	// .contains(AddressComponentType.ADMINISTRATIVE_AREA_LEVEL_2)) {
	// loc.setCity(component.longName);
	// }
	// }
	// }
	// cache.put("geocode3:" + hash.toBase32(), gson.toJson(results),
	// ConfManager.getCacheExpiryTimeSec());
	//
	// } else {
	// cache.put("geocode3:" + hash.toBase32(), "-1",
	// ConfManager.getCacheExpiryTimeSec());
	//
	// }
	//
	// return loc;
	// }
}
