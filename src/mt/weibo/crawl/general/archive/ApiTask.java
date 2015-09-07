package mt.weibo.crawl.general.archive;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import mt.weibo.common.Utils;
import mt.weibo.model.Coordinates;

public class ApiTask implements IMyTask {

	private DataWool dw;

	@Override
	public void init(DataWool dw) {
		this.dw = dw;
	}

	@Override
	public DataWool getParameters() {
		// check if it has already end
		boolean run = (boolean) dw.getControlMap().get("status");
		if (!run) {
			return dw;
		}

		// prepare the API
		String api = getNextApi();
		prepareParaForApi(api);

		return dw;
	}

	private String getNextApi() {
		String api = "";
		String apis[] = (String[]) dw.getConfigMap().get("apis");
		int apiFrom = (int) dw.getConfigMap().get("apiFrom");
		int apiTo = (int) dw.getConfigMap().get("apiTo");
		api = apis[Utils.randomInt(apiFrom, apiTo)];
		dw.getNewParaMap().put("api", api);
		return api;
	}

	private void prepareParaForApi(String api) {
		Map resultMap = dw.getResultMap();
		Map lastParaMap = dw.getLastParaMap();
		Coordinates coord[] = (Coordinates[]) dw.configMap.get("coord");

		if (api.startsWith("place/nearby_timeline")
				|| api.startsWith("place/nearby/users")) {
			// coordinates are needed
			Coordinates nextCo;
			int nextCoID;
			int nextCoPage = 1;
			Coordinates lastCo;
			int lastCoID;

			if (!lastParaMap.containsKey("coordinate")) {
				// 上一个api使用了coordinate
				lastCo = (Coordinates) lastParaMap.get("coordinate");
				lastCoID = (int) lastParaMap.get("lastCoID");

				if (resultMap.containsKey("resultText")) {
					String resultTxt = (String) resultMap.get("resultText");
					if ("[]".equals(resultTxt)) {
						// empty result retrieved
						// skip this coordinate, and init the pages
						nextCoID = lastCoID + 1;
						nextCoPage = 1;
					}
				}

				// modify the page
				nextCoID = lastCoID;
				nextCoPage = lastCo.page + 1;
				if (nextCoPage > 20) {
					nextCoID = lastCoID + 1;
					nextCoPage = 1;
				}

				if (nextCoID >= coord.length) {
					nextCoID = 0;
				}
				nextCo = coord[nextCoID];
				nextCo.page = nextCoPage;

				dw.newParaMap.put("coordinate", nextCo);
				dw.newParaMap.put("coordinateID", nextCoID);

			} else if (api.startsWith("place/user_timeline")) {

				Queue<String> uidQ = (Queue<String>) dw.getConfigMap().get(
						"uidQ");
				String uid;
				int page = 1;
				int count = 50;

				if (dw.getLastParaMap().containsKey("uid")) {
					uid = (String) dw.getLastParaMap().get("uid");
				} else {
					uid = uidQ.peek();
				}

				if (dw.getLastParaMap().containsKey("page")) {
					page = (int) dw.getLastParaMap().get("page");
					page = page + 1;
					if (page >= 20) {
						uid = uidQ.peek();
						page = 1;
					}
				}
				dw.getNewParaMap().put("uid", uid);
				dw.getNewParaMap().put("page", page);
				dw.getNewParaMap().put("count", count);
			}
		}

	}
}
