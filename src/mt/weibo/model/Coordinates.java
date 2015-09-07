package mt.weibo.model;

public class Coordinates {
	public String lat;
	public String longi;
	public String radius;
	public int page;
	public int count;
	
	public Coordinates(String lat, String longi){
		this.lat = lat;
		this.longi = longi;
		this.radius = "11132";
		this.page = 0;
		this.count = 50;
	}
	
	public Coordinates(String lat, String longi, String radius){
		this.lat = lat;
		this.longi = longi;
		this.radius = radius;
		this.page = 0;
		this.count = 50;
	}
	
	public Coordinates(String lat, String longi, int page,String radius){
		this.lat = lat;
		this.longi = longi;
		this.radius = radius;
		this.page = page;
		this.count = 50;
	}
	
	public Coordinates(String lat, String longi, int page,String radius, int count){
		this.lat = lat;
		this.longi = longi;
		this.radius = radius;
		this.page = page;
		this.count = count;
	}
	
	public Coordinates(String item){
		this.lat = item.split(",")[0];
		this.longi = item.split(",")[1];
		this.radius = item.split(",")[2];
		this.page = 0;
		this.count = 50;
	}
	
	public String toString(){
		return lat + ", " + longi + ", " + radius + ", " + page + ", " + count;
	}
	
}
