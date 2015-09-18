package mt.weibo.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import weibo4j.model.District;
import weibo4j.model.Places;

public class PoiDB {

	private MyDBConnection mdbc;
	private String poiTableName = "socialmedia.poi";
	private String poiCatTableName = "socialmedia.poi_category";
	private String distriTableName = "socialmedia.poi_district";

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public PoiDB() {
		this.mdbc = new MyDBConnection();
		this.mdbc.init();
	}

	public PoiDB(String url, String username, String password,
			String poiTableName, String poiCatTableName) {
		this.poiTableName = poiTableName;
		this.poiCatTableName = poiCatTableName;
		this.mdbc = new MyDBConnection(url, username, password);
		this.mdbc.init();
	}

	public void insertPOI(Places p) {
		insertPOI(p, this.poiCatTableName);
	}

	public void insertPOI(Places p, String specifiedPoiTableName) {
		PreparedStatement preparedStatement = null;
		String insertTableSQL = "INSERT INTO "
				+ specifiedPoiTableName
				+ " (poiid, title, address, lat, lon, "
				+ "category, county, city, province, country,"
				+ "url, postcode, categorys, category_name, icon,"
				+ "checkin_num, checkin_user_num, tip_num, photo_num, todo_num, distance)"
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		try {
			preparedStatement = mdbc.getPrepareStatement(insertTableSQL);
			preparedStatement.setString(1, p.getPoiid());
			preparedStatement.setString(2, p.getTitle());
			preparedStatement.setString(3, p.getAddress());
			preparedStatement.setDouble(4, p.getLat());
			preparedStatement.setDouble(5, p.getLon());

			preparedStatement.setString(6, p.getCategory());
			preparedStatement.setString(7, p.getCounty());
			preparedStatement.setString(8, p.getCity());
			preparedStatement.setString(9, p.getProvince());
			preparedStatement.setString(10, p.getCountry());

			preparedStatement.setString(11, p.getUrl());
			preparedStatement.setString(12, p.getPostcode());
			preparedStatement.setString(13, p.getCategorys());
			preparedStatement.setString(14, p.getCategoryName());
			preparedStatement.setString(15, p.getIcon());

			preparedStatement.setLong(16, p.getCheckinNum());
			preparedStatement.setLong(17, p.getCheckinUserNum());
			preparedStatement.setLong(18, p.getTipNum());
			preparedStatement.setLong(19, p.getPhotoNum());
			preparedStatement.setLong(20, p.getTodoNum());
			preparedStatement.setLong(21, p.getDistance());

			preparedStatement.executeUpdate();
			preparedStatement.close();

			System.out.println("Place: " + p.getPoiid() + " " + p.getCountry()
					+ p.getProvince() + p.getCity() + p.getCounty());
		} catch (SQLException e) {
			System.err.println("Something wrong when writing the user to DB.");
			System.err.println(e.getMessage());
			// e.printStackTrace();
		}

	}

	public void insertDistrictFromPOI(Places place) {
		insertDistrictFromPOI(place, distriTableName);
	}

	public void insertDistrictFromPOI(Places p,
			String specifiedDistriTableName) {
		District d = p.getDistrict();
		PreparedStatement preparedStatement = null;
		String insertTableSQL = "INSERT INTO "
				+ specifiedDistriTableName
				+ "(district_id, title, intro, lat, longi, country, province, city, checkinnum)"
				+ "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
		try {
			preparedStatement = mdbc.getPrepareStatement(insertTableSQL);
			preparedStatement.setString(1, d.getDistrictID());
			preparedStatement.setString(2, d.getTitle());
			preparedStatement.setString(3, d.getIntro());
			preparedStatement.setDouble(4, d.getLat());
			preparedStatement.setDouble(5, d.getLongi());
			preparedStatement.setString(6, d.getCountry());
			preparedStatement.setString(7, d.getProvince());
			preparedStatement.setString(8, d.getCity());
			preparedStatement.setLong(9, d.getCheckinNum());
			
			preparedStatement.executeUpdate();
			preparedStatement.close();
			System.out.println("District: " + d.getDistrictID() + " " + d.getTitle() + " " + d.getCountry() + d.getProvince() + d.getCity());
		} catch (SQLException e) {
			System.err.println("Something wrong when writing the user to DB.");
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}

	public void close() {
		if (this.mdbc != null) {
			this.mdbc.close();
		}
	}

}
