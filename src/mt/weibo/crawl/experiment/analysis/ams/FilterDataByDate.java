package mt.weibo.crawl.experiment.analysis.ams;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import mt.weibo.common.MyFolderLineReader;
import mt.weibo.common.MyLineWriter;

public class FilterDataByDate {

	private Date startDate;
	private Date endDate;
	private File folder;
	private String outputFile;
	private int count;

	public FilterDataByDate(String startDate, String endDate, File folder,
			String outputFile) {
		// TODO Auto-generated constructor stub
		this.startDate = parseDate(startDate, "EEE MMM d HH:mm:ss zzz yyyy");
		this.endDate = parseDate(endDate, "EEE MMM d HH:mm:ss zzz yyyy");
		this.folder = folder;
		this.outputFile = outputFile;
	}

	private Date parseDate(String strDate, String format) {
		// TODO Auto-generated method stub
		SimpleDateFormat parser = new SimpleDateFormat(format);
		Date date = new Date();
		try {
			date = parser.parse(strDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date;
	}

	public static void main(String[] args) {
		String startDate = "Mon Jun 03 00:00:01 CET 2015";
		String endDate = "Mon Jun 03 23:59:59 CET 2015";
		File folder = new File(args[0]);//folder path
		String outputFile = args[1];//"path/result.txt"

		FilterDataByDate fdd = new FilterDataByDate(startDate, endDate, folder,
				outputFile);
		fdd.process();

		/*
		 * SimpleDateFormat parser = new SimpleDateFormat(
		 * "EEE MMM d HH:mm:ss zzz yyyy");
		 * 
		 * String line =
		 * "1868448594,荷蘭布萊恩,m,52.359152131,4.796365201,Mon Mar 02 20:03:44 CET 2015,3816143716733251"
		 * ; String[] item = line.split(","); // item = {uid, username, gender,
		 * lat, long, data/time, postid} String strDate = item[5];
		 * 
		 * // Mon Mar 02 20:03:44 CET 2015 SimpleDateFormat parser = new
		 * SimpleDateFormat( "EEE MMM d HH:mm:ss zzz yyyy"); Date date; try {
		 * date = parser.parse(strDate);
		 * 
		 * } catch (ParseException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */

	}

	private void process() {
		// TODO Auto-generated method stub
		try {
			MyFolderLineReader mflr = new MyFolderLineReader(this.folder);
			while(mflr.hasNextLine()){
				String filename = mflr.getFileName();
				if(!filename.endsWith(".txt")){
					mflr.nextFile();
					continue;
				}
				String line = mflr.nextLine();
				String strDate = line.split(",")[5];
				Date date = parseDate(strDate, "EEE MMM d HH:mm:ss zzz yyyy");
				if(date.after(startDate) && date.before(endDate)){
					MyLineWriter.getInstance().writeLine(outputFile, line);
					count++;
					System.out.println("Number: " + count + ". Content: " + line);
				}
			}
			mflr.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
