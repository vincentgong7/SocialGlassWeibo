package mt.weibo.common;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class AppKeyDeduplication {

	private Set<String> keyset;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		AppKeyDeduplication akDedup = new AppKeyDeduplication();
		// akDedup.dedu(
		// "/Users/vincentgong/Documents/TUD/Master TUD/A Master Thesis/share/IPX/saraExp/poi_query/part3/appkey3.txt",
		// "/Users/vincentgong/Documents/TUD/Master TUD/A Master Thesis/share/IPX/saraExp/poi_query/part3/dedu-appkey3.txt");
		// akDedup.deduFolder("/Users/vincentgong/Documents/TUD/Master TUD/A Master Thesis/share/workbench/saraExp/poi_exp/allkeys/");
		akDedup.deduFile("/Users/vincentgong/Documents/TUD/Master TUD/A Master Thesis/share/workbench/saraExp/user_verified_type/user_type_crawl_kite/appkey2.txt");

	}

	private void deduFolder(String foldername) {
		System.out.println("Start checking...");
		boolean existDeduKey = false;
		try {
			File folder = new File(foldername);
			if (!folder.isDirectory()) {
				System.out.println("This is not a folder.");
				return;
			}
			for (File f : folder.listFiles()) {
				MyLineReader mlr = new MyLineReader(f);
				while (mlr.hasNextLine()) {
					String line = mlr.nextLine();
					if (keyset.contains(line)) {
						System.out.println("dup key: file= " + f.getName()
								+ ", key= " + line);
						existDeduKey = true;
					} else {
						keyset.add(line);
					}
				}
				mlr.close();

				System.out.println();
				System.out.println();
				if (existDeduKey) {
					System.out.println("There is dup key.");
				}
				System.out.println("done.");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void deduFile(String file) {
		File f = new File(file);
		System.out.println("Start checking...");
		boolean existDeduKey = false;

		MyLineReader mlr;
		try {
			mlr = new MyLineReader(f);

			while (mlr.hasNextLine()) {
				String line = mlr.nextLine();
				if (keyset.contains(line)) {
					System.out.println("dup key: file= " + f.getName()
							+ ", key= " + line);
					existDeduKey = true;
				} else {
					keyset.add(line);
				}
			}
			mlr.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println();
		System.out.println();
		if (existDeduKey) {
			System.out.println("There is dup key.");
		}
		System.out.println("done.");
	}

	public AppKeyDeduplication() {
		this.keyset = new HashSet<String>();
	}

	public void dedu(String appkeyFile, String outputFile) {
		System.out.println("Start checking...");
		boolean existDeduKey = false;
		try {
			MyLineReader mlr = new MyLineReader(appkeyFile);
			while (mlr.hasNextLine()) {
				String line = mlr.nextLine();
				if (keyset.contains(line)) {
					System.out.println("dup key: " + line);
					existDeduKey = true;
				} else {
					keyset.add(line);
				}
			}
			mlr.close();

			System.out.println();

			if (existDeduKey) {
				Iterator<String> it = keyset.iterator();
				while (it.hasNext()) {
					MyLineWriter.getInstance().writeLine(outputFile, it.next());
				}

				System.out.println("Dedude appkey file: " + outputFile);
			}

			System.out.println("done.");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
