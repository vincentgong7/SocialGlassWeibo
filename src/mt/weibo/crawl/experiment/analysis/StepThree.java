/**
 * 
 */
package mt.weibo.crawl.experiment.analysis;

import java.util.HashMap;
import java.util.Map;

import mt.weibo.common.MyLineReader;
import mt.weibo.common.MyLineWriter;

/**
 * @author vincentgong
 *
 */
public class StepThree {
	
	public Map<String,DateTestReportItemModel> dataMap;
	
	public static void main(String[] args) {
		String sourceFolder = "/Users/vincentgong/Documents/TUD/Master TUD/A Master Thesis/share/IPX/mylog/analysis/json/nearbyuser/step2/nearbyuser_report.txt";
		String outputFolder = "/Users/vincentgong/Documents/TUD/Master TUD/A Master Thesis/share/IPX/mylog/analysis/json/nearbyuser/step3/";
		
		StepThree ps3 = new StepThree();
		ps3.process(sourceFolder,outputFolder);
	}

	public StepThree(){
		this.dataMap = new HashMap<String,DateTestReportItemModel>();
	}
	
	private void process(String sourceFolder, String outputFolder) {
		try {
			MyLineReader mlr = new MyLineReader(sourceFolder);
			while(mlr.hasNextLine()){
				String line = mlr.nextLine();
				String data[] = line.split(",");
				//Fri:May:08:18:15:23,1key,Chelsea,2s,2764.0,2307.0,0.8346599131693199
				//date,key,ip,interval,totalIds,uniqueIds,efficiency
				String interval = data[3];
				DateTestReportItemModel re;
				if(dataMap.containsKey(interval)){
					re = dataMap.get(interval);
					re.total = re.total + Double.valueOf(data[4]);
					re.unique = re.unique + Double.valueOf(data[5]);
					dataMap.put(interval, re);
				}else{
					re= new DateTestReportItemModel();
					re.interval = data[3];
					re.total = Double.valueOf(data[4]);
					re.unique = Double.valueOf(data[5]);
					dataMap.put(interval, re);
				}
				System.out.println("processed: " + line);
			}
			mlr.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String columns = "interval, total, unique, effiency";
		try {
			MyLineWriter.getInstance().writeLine(outputFolder + "/Final_Report.txt", columns);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		for(DateTestReportItemModel re: dataMap.values()){
			re.calculateEfficiency();
			String line = re.interval + "," + re.total + "," + re.unique + "," + re.efficiency;
			try {
				MyLineWriter.getInstance().writeLine(outputFolder + "/Final_Report.txt", line);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
