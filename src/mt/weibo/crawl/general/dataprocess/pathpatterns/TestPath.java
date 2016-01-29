package mt.weibo.crawl.general.dataprocess.pathpatterns;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class TestPath {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Visit v1 = new Visit("p1", "u1", "poi1", 0l);
		Visit v2 = new Visit("p2", "u2", "poi2", 0l);
		Visit v3 = new Visit("p3", "u3", "poi3", 0l);
		Visit v4 = new Visit("p4", "u4", "poi4", 0l);
		
		Queue<Visit> que = new LinkedList<Visit>();
		que.add(v1);
		que.add(v2);
		que.add(v3);
		que.add(v4);
		
		List<Path> pathList = pathItemCalculator(que);
		for(Path p: pathList){
			System.out.println(p.getPath());
		}
		
	}

	private static List<Path> pathItemCalculator(Queue<Visit> que) {
		List<Path> CombinedPathItemList = new ArrayList<Path>();
		List<Path> pathItemList = new ArrayList<Path>();;
		Visit visit = que.poll();
		CombinedPathItemList.add(new Path(visit));
		if(!que.isEmpty()){
			pathItemList = pathItemCalculator(que);
			
			for(Path p: pathItemList){
				if(p.getPath_length()>1){
					CombinedPathItemList.add(p);
				}
				Path combinePath = new Path(p, visit);
				CombinedPathItemList.add(combinePath);
			}
		}
		
		return CombinedPathItemList;
	}
	
}
