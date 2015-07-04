package mt.weibo.common;

public class LatLonUtil { 
    
    private static final double PI = 3.14159265; 
    private static final double EARTH_RADIUS = 6378137; 
    private static final double RAD = Math.PI / 180.0; 
 
    //@see http://snipperize.todayclose.com/snippet/php/SQL-Query-to-Find-All-Retailers-Within-a-Given-Radius-of-a-Latitude-and-Longitude--65095/  
    //The circumference of the earth is 24,901 miles. 
    //24,901/360 = 69.17 miles / degree   
    /** 
     * @param raidus in metres 
     * return minLat,minLng,maxLat,maxLng 
     */ 
    public static double[] getAround(double lat,double lon,int raidus){ 
         
        Double latitude = lat; 
        Double longitude = lon; 
         
//      Double degree = (24901*1609)/360.0; //40,075
//        Double degree = (40075*1000)/360.0;
        double degree = 1113.2d;
        double raidusMeter = raidus; 
         
        Double dpmLat = 1/degree; 
        Double radiusLat = dpmLat*raidusMeter; 
        Double minLat = latitude - radiusLat; 
        Double maxLat = latitude + radiusLat; 
         
        Double mpdLng = degree*Math.cos(latitude * (PI/180)); 
        Double dpmLng = 1 / mpdLng; 
        Double radiusLng = dpmLng*raidusMeter; 
        Double minLng = longitude - radiusLng; 
        Double maxLng = longitude + radiusLng; 
        System.out.println("["+minLat+","+minLng+","+maxLat+","+maxLng+"]"); 
        return new double[]{minLat,minLng,maxLat,maxLng}; 
    } 
     
    /** 
     * Calculate the distance from two coordinates (double), metres 
     * @param lng1 
     * @param lat1 
     * @param lng2 
     * @param lat2 
     * @return 
     */ 
    public static double getDistance(double lng1, double lat1, double lng2, double lat2) 
    { 
       double radLat1 = lat1*RAD; 
       double radLat2 = lat2*RAD; 
       double a = radLat1 - radLat2; 
       double b = (lng1 - lng2)*RAD; 
       double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2) + 
        Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2))); 
       s = s * EARTH_RADIUS; 
       s = Math.round(s * 10000) / 10000; 
       return s; 
    } 
     
    public static void main(String[] args){ 
        Double lat1 = 34.264648; 
        Double lon1 = 108.952736; 
         
        int radius = 1000;
        //[34.25566276027792,108.94186385411045,34.27363323972208,108.96360814588955] 
        double[] result = getAround(lat1,lon1,radius); 
         
        //911717.0   34.264648,108.952736,39.904549,116.407288 
        double dis = getDistance(lon1,lat1,result[3],result[2]);  
        System.out.println(dis); 
    } 
} 