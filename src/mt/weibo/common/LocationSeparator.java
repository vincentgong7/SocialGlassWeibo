/**
 * 
 */
package mt.weibo.common;

/**
 * @author vincentgong
 *
 */
public class LocationSeparator {

	private String location;
	private String country = "";
	private String province = "";
	private String city = "";
	private String region = "";
	
	public LocationSeparator(String location) {
		// keep "四川 成都", in case of "海外 法国", ignore
		this.location = location;
		analyze();
	}

	private void analyze() {
		// TODO Auto-generated method stub
		String[] tmp;
		if(location!=null && !location.equals("")){
			// split the location into array
			if(!location.trim().contains(" ")){// "其他", "广东"
				tmp = new String[1];
				tmp[0] = location;
			}else{
				tmp = location.trim().split(" ");
			}
			
			if("海外".equals(tmp[0])){// abroad
				if(tmp.length>1){
					country =  tmp[1];
				}else{
					country = "海外";
				}
			}
			else if("北京".equals(tmp[0]) || "天津".equals(tmp[0]) || "上海".equals(tmp[0]) || "重庆".equals(tmp[0])){
				country = "中国";
				city = tmp[0];
				region = tmp[1];
			}
			else if("香港".equals(tmp[0]) || "澳门".equals(tmp[0])){
				country = "中华";
				city = tmp[0];
			}
			else if("台湾".equals(tmp[0])){// "台湾 台北市"
				country = "台湾";
				if(tmp.length > 1){
					province = tmp[0];
					city = tmp[1];
				}
			}
			else if("其他".equals(tmp[0])){
				
			}
			else{
				country = "中国";
				if(tmp.length == 1){
					province = tmp[0];
				}else if(tmp.length == 2){
					province = tmp[0];
					if(tmp[1]!= null && !"".equals(tmp[1])){
						if(!tmp[1].equals("其他")){
							city = tmp[1];
						}
					}
				}
			}
			
			
			
//			tmp = location.split(" ");
//			if(!tmp[0].equals("海外")){
//				country = "中国";
//				if(tmp.length==1){//直辖市
//					city = tmp[0];
//				}else{//非直辖市
//					province = tmp[0];
//					city = tmp[1];
//				}
//			}else{
//				if(tmp.length==1){
//					country = tmp[0];
//				}else if(tmp.length==2)
//				country = tmp[1];
//			}
		}
	}

	public boolean isChineseCity(){
		if("中国".equals(country) || "中华".equals(country) || "台湾".equals(country)){
			return true;
		}
		return false;
	}
	
	public String toString(){
		String line = country + ", " + province + ", " + city + ", " + region;
		return line;
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}
	
}
