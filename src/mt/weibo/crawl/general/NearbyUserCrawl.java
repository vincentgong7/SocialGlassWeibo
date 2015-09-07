package mt.weibo.crawl.general;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringEscapeUtils;

import weibo4j.model.WeiboException;
import mt.weibo.common.Utils;
import mt.weibo.crawl.experiment.ExpUtils;
import mt.weibo.model.Coordinates;

public class NearbyUserCrawl {

	private String logSign = "[NearbyUser]";
	private String api = "place/nearby/users.json";
	private String keyFileName;
	private Integer keysFrom = 0;
	private Integer keysTo = -1;
	private List<String> keyList;
	
	private int maxPageCount = 20;

	private String logName;
	private String JsonlogName;

	private String coordinatFileName;
	private List<Coordinates> coords;

	private Integer interval;
	private long startCrawlTimeStamp;
	private long stopCrawlTimeStamp;

	public static void main(String[] args) {
		NearbyUserCrawl nuc = new NearbyUserCrawl();
		nuc.setup(args[0]);
//		nuc.setup("/nearbyuser-config.txt");
		nuc.process();
	}

	private void process() {
		// check start crawl time
		if (!CrawlTool.checkStartCrawlTime(this.startCrawlTimeStamp)) {// wait
																		// to
																		// start
			Long nowTimeStamp = new Date().getTime();
			int waitTimeSecond = CrawlTool
					.safeLongToInt((this.startCrawlTimeStamp - nowTimeStamp) / 1000);
			System.out.println(this.logSign + " Sleep a while to start: "
					+ waitTimeSecond + "s, or:" + waitTimeSecond / 60 + "m.");
			CrawlTool.sleep(waitTimeSecond, this.logSign);
		}
		
		// the variables and flags
		String currentKey = "";
		Coordinates currentCo = null;
		boolean isResultEmpty = false;
		boolean isError = false;

		while (true) {
			// check if it is time to stop
			boolean stopCrawl = CrawlTool
					.checkStopCrawlTime(this.stopCrawlTimeStamp);
			if (stopCrawl) {
				// report this round
				System.out.println("Time's up. Stop crawling now.");
				break;
			}
			
			// prepare the parameters
			currentKey = CrawlTool.getNextKey(keyList, currentKey);
			currentCo = getNextCoordinate(currentCo, isResultEmpty);
			Map<String, String> map = getParaMap(currentCo);
			System.out.println(map.toString());
			
			// sleep interval
			CrawlTool.sleep(this.interval, this.logSign);

			// try to crawl
			try {
				ExpUtils.mylog(CrawlTool.splitFileNameByHour(logName), currentCo.toString());

				// try to get the data
				String result = ExpUtils.crawlData(api, map, currentKey);
//				result = StringEscapeUtils.unescapeJava(result);

				// init and set the flags
				isResultEmpty = false;
				isError = false;
				
				// set the flags
				if ("[]".equals(result)) {
					isResultEmpty = true;
				} else {
					isResultEmpty = false;
				}
				
				if(result.startsWith("{\"error\"")){
					isError = true;
				}else{
					isError = false;
				}
				
				if (!isResultEmpty && !isError) {
					ExpUtils.mylogJson(
							CrawlTool.splitFileNameByHour(JsonlogName), result);
				}else{
					// something wrong
					ExpUtils.mylog(CrawlTool.splitFileNameByHour(logName), result);
				}
			} catch (WeiboException e) {
				e.printStackTrace();
				ExpUtils.mylog(CrawlTool.splitFileNameByHour(logName), e.getError());
				continue;
			}
		}
	}

	private Map<String, String> getParaMap(Coordinates co) {
		Map<String, String> paraMap = new HashMap<String, String>();

		paraMap.put("lat", co.lat);
		paraMap.put("long", co.longi);
		paraMap.put("range", co.radius);
		paraMap.put("count", String.valueOf(co.count));
		paraMap.put("page", String.valueOf(co.page));

		return paraMap;
	}

	private Coordinates getNextCoordinate(Coordinates currentCo,
			boolean lastResultEmpty) {
		Coordinates nextCo;
		if (currentCo == null) {// the first coordinate
			nextCo = this.coords.get(0);
			nextCo.page = 1;
		} else {
			if (lastResultEmpty || currentCo.page >= this.maxPageCount) {// the last result is
															// empty
				nextCo = getNextCoordinateFromList(currentCo);
				nextCo.page = 1;
			} else {// the last result is not empty
				nextCo = currentCo;
				nextCo.page = nextCo.page + 1;
			}
		}
		return nextCo;
	}

	private Coordinates getNextCoordinateFromList(Coordinates currentCo) {
		Coordinates nextCo;
		int currentCoID = this.coords.indexOf(currentCo);
		int nextCoID = currentCoID + 1;
		if (nextCoID < 0 || nextCoID >= this.coords.size()) {
			nextCoID = 0;
		}
		nextCo = this.coords.get(nextCoID);
		if(currentCoID!=nextCoID){
			System.out.println("Coordinate changed.");
			ExpUtils.mylog(CrawlTool.splitFileNameByHour(logName), this.logSign + " Change coordinate.");
		}
		return nextCo;
	}

	private void setup(String configFileName) {
		InputStream is = null;
		try {
			is = new FileInputStream(Utils.getPath() + "/" + configFileName);
			Properties config = new Properties();
			config.load(is);

			// get the config item
			
			// keys
			this.keyFileName = config.getProperty("key_file_name");
			if (config.containsKey("key_from")) {
				this.keysFrom = Integer.valueOf(config.getProperty("key_from"));
			}
			if (config.containsKey("key_to")) {
				this.keysTo = Integer.valueOf(config.getProperty("key_to"));
			}
			
			// log files
			this.logName = config.getProperty("log_name");
			this.JsonlogName = config.getProperty("json_log_name");

			//interval
			this.interval = Integer.valueOf(config.getProperty("interval"));

			// crawl start and end
			this.startCrawlTimeStamp = CrawlTool.timeToUnixTime(config
					.getProperty("start_crawl_time"));
			this.stopCrawlTimeStamp = CrawlTool.timeToUnixTime(config
					.getProperty("stop_crawl_time"));

			// max page
			if(config.containsKey("max_page_count")){
				this.maxPageCount = Integer.valueOf(config.getProperty("max_page_count"));
			}
			
			// coordinates
			this.coordinatFileName = config.getProperty("coordinate_file_name");
						
			// init the appkey, coordinate
			this.keyList = CrawlTool.initAppkey(Utils.getPath() + "/"
					+ this.keyFileName, this.keysFrom, this.keysTo);
			this.coords = CrawlTool.initCoordinateList(Utils.getPath() + "/"
					+ this.coordinatFileName);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
