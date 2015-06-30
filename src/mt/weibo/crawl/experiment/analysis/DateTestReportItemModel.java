/**
 * 
 */
package mt.weibo.crawl.experiment.analysis;

/**
 * @author vincentgong
 *
 */
public class DateTestReportItemModel {
	public String interval = "";
	public double total = 0d;
	public double unique = 0d;
	public double efficiency = 0d;
	
	public void calculateEfficiency(){
		this.efficiency = this.unique/this.total;
	}
}
