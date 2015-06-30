package weibo4j.examples.timeline;

import java.util.Iterator;
import java.util.List;

import weibo4j.Timeline;
import weibo4j.examples.oauth2.Log;
import weibo4j.model.Status;
import weibo4j.model.StatusWapper;
import weibo4j.model.WeiboException;


public class GetPublicTimeline {

	public static void main(String[] args) {
		String access_token = "2.00owJ9cFkKE_IEbce3361c680qNh_x";
		Timeline tm = new Timeline(access_token);
		try {
			StatusWapper status = tm.getPublicTimeline();
			List<Status> l = status.getStatuses();
			Iterator<Status> it = l.iterator();
			while(it.hasNext()){
				Status st = it.next();
//				System.out.println(st.getCreatedAt());
				System.out.println(st.getCreatedAt_origin());
				System.out.println();
			}
			Log.logInfo(status.toString());
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}

}
