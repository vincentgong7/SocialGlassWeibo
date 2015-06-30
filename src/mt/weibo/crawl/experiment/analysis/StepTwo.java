package mt.weibo.crawl.experiment.analysis;

import java.util.HashSet;
import java.util.Set;

import mt.weibo.common.MyFolderLineReader;
import mt.weibo.common.MyLineWriter;

public class StepTwo {

	public String folder = "";
	public String reportFileName = "";
	
	public static void main(String[] args) {
		StepTwo pst = new StepTwo();
		pst.setFolder("/Users/vincentgong/Documents/TUD/Master TUD/A Master Thesis/share/IPX/mylog/analysis/json/nearbyuser/step1/");
		pst.setReportFileName("/Users/vincentgong/Documents/TUD/Master TUD/A Master Thesis/share/IPX/mylog/analysis/json/nearbyuser/step2/nearbyuser_report.txt");
		pst.process();
	}

	private void process() {
		try {
			MyFolderLineReader mflr = new MyFolderLineReader(this.folder);
			while(mflr.hasNextLine()){
				String fileName = mflr.getFileName();
				if(!fileName.endsWith(".csv")){
					mflr.nextFile();
					continue;
				}
				String[] ids = mflr.nextLine().split(":");
				Set<String> idSet = new HashSet<String>();
				for(String id : ids){
					if(!id.equals("")){
						idSet.add(id);
					}
				}
				double totalIds = ids.length;
				double uniqueIds = idSet.size();
				double efficiency = uniqueIds/totalIds;
				//Thu_May_07_03_38_16-17-Chelsea-0.csv
				String date = fileName.split("-")[0].replace("_", ":");
				String key = fileName.split("-")[1];
				String ip = fileName.split("-")[2];
				String interval = fileName.split("-")[3].substring(0, fileName.split("-")[3].length()-4);
				//2->2s
				if(!interval.endsWith("s")){
					interval = interval + "s";
				}
				String reportLine = date+"," + key + "," + ip + "," + interval + "," + totalIds + "," + uniqueIds + "," + efficiency;
				System.out.println("Interval: " + interval + ", effiency: " + efficiency);
				MyLineWriter.getInstance().writeLine(reportFileName, reportLine);
			}
			mflr.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void setReportFileName(String reportFileName) {
		this.reportFileName = reportFileName;
	}

	private void setFolder(String folder) {
		this.folder = folder;
	}
	
}
