package mt.weibo.common;

import java.util.ArrayList;
import java.util.List;

public class AppKeyCenter {

	private String keyFile = Utils.getPath() + "/key.txt"; // default key.txt is located in bin folder
	private List<String> keys;
	private String currentGlobalKey;// the current key for the public auto-crawl center
	
	private static AppKeyCenter instance;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public AppKeyCenter(){
		
	}
	
	public static AppKeyCenter getInstance(){
		if(instance == null){
			instance = new AppKeyCenter();
			instance.init();
		}
		return instance;
	}
	
	private void init() {
		initialKeyList();
		this.currentGlobalKey = "";
	}

	private void initialKeyList() {
		keys = new ArrayList<String>();
		try {
			MyLineReader mlr = new MyLineReader(this.keyFile);
			while(mlr.hasNextLine()){
				String line = mlr.nextLine();
				if(line!=null && !"".equals(line.trim())){
					keys.add(line);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getNextKey(List<String> keyList, String currentKey) {
		int keyID = keyList.indexOf(currentKey);
		if (keyID < 0 || keyID >= keyList.size()-1) {
			return keyList.get(0);
		} else {
			return keyList.get(keyID + 1);
		}
	}
	
	public String getNextKey(){
		if(this.keys==null){
			initialKeyList();
		}
		currentGlobalKey = getNextKey(keys,currentGlobalKey);
		return currentGlobalKey;
	}
	
}
