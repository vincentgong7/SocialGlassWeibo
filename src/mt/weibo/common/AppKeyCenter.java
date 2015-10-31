package mt.weibo.common;

import java.util.ArrayList;
import java.util.List;

public class AppKeyCenter {

//	private String keyFile = Utils.getPath() + "/appkey.txt"; // default appkey.txt is located in bin folder
	private String currentKeyFilepath = Utils.getPath() + "/" + DEFAULT_APP_KEY_FILENAME;
	private List<String> keys;
	private String currentGlobalKey;// the current key for the public auto-crawl center
	
	private static AppKeyCenter instance;
	private static final String DEFAULT_APP_KEY_FILENAME = "appkey.txt";
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public AppKeyCenter(){
		
	}
	
	public static AppKeyCenter getInstance(String keyFilename){
		if(instance == null){
			instance = new AppKeyCenter();
			instance.init(keyFilename);
		}
		return instance;
	}
	
	public static AppKeyCenter getInstance(){
		return getInstance(AppKeyCenter.DEFAULT_APP_KEY_FILENAME);
	}
	
	private void init(String keyFilename) {
		String keyfilePath = checkAndGetKeyfilePath(keyFilename);
		currentKeyFilepath = keyfilePath;
		initialKeyList();
		this.currentGlobalKey = "";
	}

	private String checkAndGetKeyfilePath(String keyFilename) {
		if(keyFilename.startsWith("/")){
			keyFilename = keyFilename.substring(1, keyFilename.length());
		}
		
		String keyfilePath = Utils.getPath() + "/" + keyFilename;
		return keyfilePath;
	}

	private void initialKeyList() {
		keys = new ArrayList<String>();
		try {
			MyLineReader mlr = new MyLineReader(this.currentKeyFilepath);
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
			int random = Utils.randomInt(0, keyList.size() -1 ); //starting from a random key, rather than the 1st one
			return keyList.get(random);
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
