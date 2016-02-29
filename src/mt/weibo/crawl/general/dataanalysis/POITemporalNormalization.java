package mt.weibo.crawl.general.dataanalysis;

import java.io.File;

import mt.weibo.common.MyLineReader;
import mt.weibo.common.MyLineWriter;

public class POITemporalNormalization {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String inputCSV = "/Users/vincentgong/Downloads/poi_cate_temporal_inst_original.csv";
		String outputCSV = "/Users/vincentgong/Downloads/poi_cate_temporal_inst_norm.csv";
		POITemporalNormalization nor = new POITemporalNormalization();
		nor.process(inputCSV, outputCSV);
	}

	private void process(String inputCSV, String outputCSV) throws Exception {
		// TODO Auto-generated method stub
		double data[][] = new double[10][48];

		int npLine = 0;
		File inputFile = new File(inputCSV);
		MyLineReader mlr = new MyLineReader(inputFile);
		String title = mlr.nextLine();
		while (mlr.hasNextLine()) {
			String line[] = mlr.nextLine().split(",");
//			System.out.println(line.toString());
			for (int p = 0; p < line.length; p++) {
				double number = Double.valueOf(line[p]);
				data[p][npLine] = number;
			}
			npLine++;
		}

		for (int item = 1; item <= 9; item++) {
			double itemData[] = data[item];
			double max = 0, min = Double.MAX_VALUE;

			for (int d = 0; d < itemData.length; d++) {
				double e = itemData[d];
				if (e < min) {
					min = e;
				}
				if (e > max) {
					max = e;
				}
			}
			for (int d = 0; d < itemData.length; d++) {
				double e = itemData[d];
				double result = (e - min) / (max - min);
				data[item][d] = result;
			}

		}

		File output = new File(outputCSV);
		MyLineWriter.getInstance().writeLine(output, title);
		System.out.println(title);
		for (int nline = 0; nline < 48; nline++) {
			String line = "";
			for (int npCat = 0; npCat < 10; npCat++) {
				double e = data[npCat][nline];
				line = line + "," + e;
			}
			line = line.substring(1, line.length()-1);
			MyLineWriter.getInstance().writeLine(output, line);
			System.out.println(line);
		}

	}

}
