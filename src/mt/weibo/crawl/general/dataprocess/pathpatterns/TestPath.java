package mt.weibo.crawl.general.dataprocess.pathpatterns;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class TestPath {

	List<Path> storeList = new ArrayList<Path>();
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TestPath tp = new TestPath();
		Visit v1 = new Visit("a", "u1", "a", 0l, 0d);
		Visit v2 = new Visit("b", "u2", "b", 0l, 0d);
		Visit v3 = new Visit("c", "u3", "c", 0l, 0d);
		Visit v4 = new Visit("d", "u4", "d", 0l, 0d);

		Queue<Visit> que = new LinkedList<Visit>();
		que.add(v1);
		que.add(v2);
		que.add(v3);
		que.add(v4);

		tp.pathItemCalculator(que);
		for (Path p : tp.storeList) {
			System.out.println(p.getPath());
		}

	}

	private List<Path> pathItemCalculator(Queue<Visit> que) {
		List<Path> CombinedPathItemList = new ArrayList<Path>();
		List<Path> pathItemList = new ArrayList<Path>();
		if (!que.isEmpty()) {
			Visit visit = que.poll();
			//store(new Path(visit))
			storeList.add(new Path(visit));
			CombinedPathItemList.add(new Path(visit));

			pathItemList = pathItemCalculator(que);

			for (Path p : pathItemList) {

				Path combinePath = new Path(visit, p); // v + p
				//CombinedPathItemList.add(p);
				//store(combinePath)
				storeList.add(combinePath);
				CombinedPathItemList.add(combinePath);
			}
		}

		return CombinedPathItemList;
	}

}
