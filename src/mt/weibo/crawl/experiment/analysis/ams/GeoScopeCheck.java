package mt.weibo.crawl.experiment.analysis.ams;

import java.io.File;

import mt.weibo.common.MyLineReader;
import mt.weibo.common.MyLineWriter;

public class GeoScopeCheck {

	private File inputFile;
	private File outputFile;
	private double x1;
	private double x2;
	private double y1;
	private double y2;
	private int count;

	public GeoScopeCheck(File inputFile, File outputFile, String cor1,
			String cor2) {
		this.inputFile = inputFile;
		this.outputFile = outputFile;
		this.x1 = Double.valueOf(cor1.trim().split(",")[1]);
		this.x2 = Double.valueOf(cor2.trim().split(",")[1]);
		this.y1 = Double.valueOf(cor1.trim().split(",")[0]);
		this.y2 = Double.valueOf(cor2.trim().split(",")[0]);
		
		if(this.x1 > this.x2){
			double tmpx = this.x1;
			this.x1 = this.x2;
			this.x2 = tmpx;
		}
		if(this.y1 > this.y2){
			double tmpy = this.y1;
			this.y1 = this.y2;
			this.y2 = tmpy;
		}
	}


	public static void main(String[] args) {
		File inputFile = new File(args[0]);
		File outputFile = new File(args[1]);
		
		GeoScopeCheck gsc = new GeoScopeCheck(inputFile, outputFile, args[2], args[3]);
		gsc.process();
	}

	private void process() {
		try {
			MyLineReader mlr = new MyLineReader(this.inputFile);
			while(mlr.hasNextLine()){
				String line = mlr.nextLine();
				//1868448594,荷蘭布萊恩,m,52.359152131,4.796365201,Mon Mar 02 20:03:44 CET 2015,3816143716733251
				double x = Double.valueOf(line.split(",")[4]);
				double y = Double.valueOf(line.split(",")[3]);
				
				if((x>=x1 && x<=x2) && (y>=y1 && y<=y2)){
					MyLineWriter.getInstance().writeLine(outputFile, line);
					count++;
					System.out.println("Number: " + count + ". Content: " + line);
				}
			}
			mlr.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
