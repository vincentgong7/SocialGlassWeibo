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

import mt.weibo.common.Utils;
import mt.weibo.crawl.experiment.ExpUtils;

import org.apache.commons.lang.StringEscapeUtils;

import weibo4j.model.WeiboException;

public class UserGeoTimeline {

	private String logSign = "[UserGeoTimeline]";
	private String api = "place/user_timeline.json";
	private String keyFileName;
	private Integer keysFrom = 0;
	private Integer keysTo = -1;
	private List<String> keyList;

	private String logName;
	private String JsonlogName;

	private Integer interval;
	private long startCrawlTimeStamp;
	private long stopCrawlTimeStamp;

	// user id
	private String uidFileName;
	private List<String> uidList;
	private String startingUid = "";
	private int maxPageCount = 20;
	private int count = 50;

	public static void main(String[] args) {
		UserGeoTimeline ugt = new UserGeoTimeline();
		ugt.setup(args[0]);
		// ugt.setup("/userpost-config.txt");
		ugt.process();
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
		int page = 0;
		String currentUid = "";
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

			// prepare the key
			currentKey = CrawlTool.getNextKey(keyList, currentKey);

			// prepare the uid, page, count
			if (null == currentUid || "".equals(currentUid)) {
				currentUid = getNextUidTillEnd(currentUid);

				if (currentUid == "finish" || "finish".equals(currentUid)) {// all
																			// uids
																			// have
																			// been
																			// crawled
					String line = "All uids have been crawled, now finish! "
							+ new Date();
					System.out.println(line);
					ExpUtils.mylog(CrawlTool.splitFileNameByHour(logName), line);
					break;
				}
				page = 1;
			}
			if (isResultEmpty) {
				// empty result get, goto next id, page = 1
				currentUid = getNextUidTillEnd(currentUid);
				page = 1;

				if (currentUid == "finish" || "finish".equals(currentUid)) {// all
																			// uids
																			// have
																			// been
																			// crawled
					String line = "All uids have been crawled, now finish! "
							+ new Date();
					System.out.println(line);
					ExpUtils.mylog(CrawlTool.splitFileNameByHour(logName), line);
					break;
				}
			} else {
				// try the same id and next page
				page++;
			}

			// prepare the parameter map
			Map<String, String> map = getParaMap(currentUid, page, count);
			System.out.println(map.toString());

			// sleep interval
			CrawlTool.sleep(this.interval, this.logSign);

			// try to crawl
			try {
				String line = "uid=" + currentUid + ", page=" + page
						+ ", count=" + count;
				ExpUtils.mylog(CrawlTool.splitFileNameByHour(logName), line);

				// try to get the data
				String result = ExpUtils.crawlData(api, map, currentKey);
				// result = StringEscapeUtils.unescapeJava(result);

				// init and set the flags
				isResultEmpty = false;
				isError = false;

				if ("[]".equals(result)) {
					isResultEmpty = true;
				} else {
					isResultEmpty = false;
				}

				if (result.startsWith("{\"error\"")) {
					isError = true;
				} else {
					isError = false;
				}

				if (!isResultEmpty && !isError) {
					ExpUtils.mylogJson(
							CrawlTool.splitFileNameByHour(JsonlogName), result);
				} else {
					// something wrong
					ExpUtils.mylog(CrawlTool.splitFileNameByHour(logName),
							result);
				}
			} catch (WeiboException e) {
				e.printStackTrace();
				ExpUtils.mylog(CrawlTool.splitFileNameByHour(logName),
						e.getError());
				continue;
			}
		}
	}

	private Map<String, String> getParaMap(String currentUid, int page,
			int count) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("uid", currentUid);
		map.put("page", String.valueOf(page));
		map.put("count", String.valueOf(count));
		return map;
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

			// interval
			this.interval = Integer.valueOf(config.getProperty("interval"));

			// crawl start and end
			this.startCrawlTimeStamp = CrawlTool.timeToUnixTime(config
					.getProperty("start_crawl_time"));
			this.stopCrawlTimeStamp = CrawlTool.timeToUnixTime(config
					.getProperty("stop_crawl_time"));

			// max page
			if (config.containsKey("max_page_count")) {
				this.maxPageCount = Integer.valueOf(config
						.getProperty("max_page_count"));
			}

			if (config.containsKey("count")) {
				this.count = Integer.valueOf(config.getProperty("count"));
			}

			// User ID file
			this.uidFileName = config.getProperty("uid_file_name");
			if (config.containsKey("start_uid")) {// uid starting point
				this.startingUid = config.getProperty("start_uid");
			}

			// init the appkey, uid
			this.keyList = CrawlTool.initAppkey(Utils.getPath() + "/"
					+ this.keyFileName, this.keysFrom, this.keysTo);
			this.uidList = CrawlTool.initUidList(Utils.getPath() + "/"
					+ this.uidFileName, this.startingUid);

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

	public String getNextUidTillEnd(String currentUid) {
		int uidID = uidList.indexOf(currentUid);
		int nextUidID;
		nextUidID = uidID + 1;
		if (nextUidID < 0) {
			nextUidID = 0;
		}
		if (nextUidID >= this.uidList.size()) {
			return "finish";
		}
		if (uidID != nextUidID) {
			System.out.println("change uid.");
			ExpUtils.mylog(CrawlTool.splitFileNameByHour(this.logName),
					this.logSign + " Change uid.");
		}
		return uidList.get(nextUidID);
	}

}
