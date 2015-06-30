package mt.weibo.crawl.experiment.aipx.model;

public class KeyReport {

	public String key = "";
	public int keyID = 0;
	public String startDate = "";
	public String endDate = "";
	public int requestTimes = 0;
	public int successTimes = 0;
	public int failTimes = 0;

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public String toString(){
		String str = "\t" + "Key = "+key + ", keyID = "+keyID+ ", requests = " +requestTimes + ", sucess = "+ successTimes + 
				", fail = "+ failTimes + ", start = " + startDate + ", end = " + endDate;
		return str;
	}
}
