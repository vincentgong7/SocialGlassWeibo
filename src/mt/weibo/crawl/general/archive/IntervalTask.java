package mt.weibo.crawl.general.archive;

public class IntervalTask implements IMyTask{

	private DataWool dw;
	
	@Override
	public void init(DataWool dw) {
		this.dw = dw;
	}

	@Override
	public DataWool getParameters() {
		int interval = (int) dw.getConfigMap().get("interval");
		dw.getNewParaMap().put("interval", interval);
		return dw;
	}
}
