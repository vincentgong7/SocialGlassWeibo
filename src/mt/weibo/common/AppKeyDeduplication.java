package mt.weibo.common;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class AppKeyDeduplication {

	private Set<String> keyset;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		AppKeyDeduplication akDedup = new AppKeyDeduplication();
		akDedup.dedu(
				"/Users/vincentgong/Documents/TUD/Master TUD/A Master Thesis/share/IPX/saraExp/poi_query/part3/appkey3.txt",
				"/Users/vincentgong/Documents/TUD/Master TUD/A Master Thesis/share/IPX/saraExp/poi_query/part3/dedu-appkey3.txt");
		
	}

	public AppKeyDeduplication(){
		this.keyset = new HashSet<String>();
	}
	
	public void dedu(String appkeyFile, String outputFile) {
		System.out.println("Start checking...");
		boolean existDeduKey = false;
		try {
			MyLineReader mlr = new MyLineReader(appkeyFile);
			while(mlr.hasNextLine()){
				String line = mlr.nextLine();
				if(keyset.contains(line)){
					System.out.println("dup key: " + line);
					existDeduKey = true;
				}else{
					keyset.add(line);
				}
			}
			mlr.close();
			
			System.out.println();
			
			if(existDeduKey){
				Iterator<String> it = keyset.iterator();
				while(it.hasNext()){
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
