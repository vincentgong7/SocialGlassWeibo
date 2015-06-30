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
	
	public LocationSeparator(String location) {
		//"四川 成都", "海外 法国"
		this.location = location;
		analyze();
	}

	private void analyze() {
		// TODO Auto-generated method stub
		String[] tmp;
		if(location!=null && !location.equals("")){
			tmp = location.split(" ");
			if(!tmp[0].equals("海外")){
				country = "中国";
				if(tmp.length==1){//直辖市
					city = tmp[0];
				}else{//非直辖市
					province = tmp[0];
					city = tmp[1];
				}
			}else{
				if(tmp.length==1){
					country = tmp[0];
				}else if(tmp.length==2)
				country = tmp[1];
			}
		}
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
	
}
