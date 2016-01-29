package mt.weibo.crawl.general.dataanalysis;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Toolbox {

	public static void main(String[] args) throws ParseException {
		// TODO Auto-generated method stub
		String dateOrigin = "Sun Oct 11 11:08:08 +0800 2015";
		SimpleDateFormat parserSDF = new SimpleDateFormat("EEE MMM d HH:mm:ss Z yyyy");
		Date date = parserSDF.parse(dateOrigin);
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);  
		int hours = cal.get(Calendar.HOUR_OF_DAY);
		int mins = cal.get(Calendar.MINUTE);
		double changedMins = ((double) mins)/60;
		double temporal = hours + changedMins + 6;
		System.out.println(temporal);
	}
	
	public static double coverWeiboPostOriginalCreatedatToTemporal(String postsOriginCreatedat) throws ParseException{
		SimpleDateFormat parserSDF = new SimpleDateFormat("EEE MMM d HH:mm:ss Z yyyy");
		Date date = parserSDF.parse(postsOriginCreatedat);
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);  
		int hours = cal.get(Calendar.HOUR_OF_DAY);
		int mins = cal.get(Calendar.MINUTE);
		double changedMins = ((double) mins)/60;
		double temporal = hours + changedMins + 6;
		return temporal;
	}

}
