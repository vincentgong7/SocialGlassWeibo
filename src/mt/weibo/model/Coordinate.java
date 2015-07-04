package mt.weibo.model;

public class Coordinate {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	private double lat;
	private double longi;
	private double radius;
	
	public Coordinate(double lat, double longi){
		this.lat = lat;
		this.longi = longi;
		this.radius = 0d;
	}
	
	public Coordinate(double lat, double longi, double radius){
		this.lat = lat;
		this.longi = longi;
		this.radius = radius;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLongi() {
		return longi;
	}

	public void setLongi(double longi) {
		this.longi = longi;
	}

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}
	
	public String toString(){
		return lat + "," + longi + "," + radius;
	}

}
