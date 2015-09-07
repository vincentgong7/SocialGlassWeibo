package mt.weibo.crawl.general.archive;

import java.util.List;

public class KeyTask implements IMyTask {

	private DataWool dw;
	
	@Override
	public void init(DataWool dw) {
		// TODO Auto-generated method stub
		this.dw = dw;
	}

	@Override
	public DataWool getParameters() {
		//check if it has already end
		boolean run = (boolean) dw.getControlMap().get("status");
		if(!run){
			return dw;
		}
		
		//prepare keys
		String nextKey = "";
		int nextKeyID;
		List keys = (List) dw.getConfigMap().get("keys");
		if(!dw.getLastParaMap().containsKey("appkey")){
			nextKeyID = 0;
			nextKey = (String) keys.get(nextKeyID);
		}else{
			int currentKeyID = (int) dw.getLastParaMap().get("appkeyID");
			nextKeyID = currentKeyID + 1;
			if(nextKeyID >= keys.size()){
				nextKeyID = 0;
			}
			nextKey = (String) keys.get(nextKeyID);
		}
		dw.getNewParaMap().put("appkeyID", nextKeyID);
		dw.getNewParaMap().put("appkey", nextKey);
		return dw;
	}

}
