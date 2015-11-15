package mt.weibo.common;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class POIDeduplication {

	private Set<String> poiset;

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		POIDeduplication poiDedup = new POIDeduplication();
		poiDedup.dedu(
				"/Users/vincentgong/Documents/TUD/Master TUD/A Master Thesis/share/workbench/saraExp/poi_query/poi_ids/",
				"/Users/vincentgong/Documents/TUD/Master TUD/A Master Thesis/share/workbench/saraExp/poi_query/unique_poi_ids.txt");

	}

	public POIDeduplication() {
		this.poiset = new HashSet<String>();
	}

	public void dedu(String poifileFolder, String outputFile) throws Exception {
		System.out.println("Start checking...");

		File folder = new File(poifileFolder);
		if (!folder.isDirectory()) {
			System.out.println("Please provide a folder.");
			return;
		}

		for (File f : folder.listFiles()) {
			MyLineReader mlr = new MyLineReader(f);
			while (mlr.hasNextLine()) {
				String line = mlr.nextLine();

				if (poiset.contains(line)) {
					System.out.println("dup poi: " + line);
				} else {
					poiset.add(line);
				}
			}
			mlr.close();
		}

		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println("Unique POI id: " + this.poiset.size());
		System.out.println("done.");
	}

}
