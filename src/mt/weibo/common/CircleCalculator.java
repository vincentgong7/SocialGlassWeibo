package mt.weibo.common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class CircleCalculator {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CircleCalculator cc = new CircleCalculator(10, 2, 2);
		cc.process();
		cc.iteratorList();
		System.out.println("done");
	}

	private double r;
	private double R;
	private double interval;
	private List<Coord> tmpList;
	private List<Coord> list;
	private Set<String> coordSet;

	public CircleCalculator(double R, double r, double interval) {
		this.R = R;
		this.r = r;
		this.interval = interval;
		if (interval > r) {
			this.interval = this.r;
		} else if (interval <= 0) {
			this.interval = this.r / 10;
		}
		this.tmpList = new ArrayList<Coord>();
		this.list = new ArrayList<Coord>();
		this.coordSet = new HashSet<String>();
	}

	public List<Coord> process() {
		processFirstQuadrant();
		processOtherQuadrant();
		finalProcess();
		return this.list;
	}

	private void finalProcess() {
		Iterator<String> it = this.coordSet.iterator();
		while (it.hasNext()) {
			String[] item = it.next().split(",");
			double x = Double.valueOf(item[0]);
			double y = Double.valueOf(item[1]);
			double r = Double.valueOf(item[2]);
			Coord c = new Coord(x, y, r);
			this.list.add(c);
		}
	}

	private void processOtherQuadrant() {
		for (Coord c : tmpList) {
			double x = c.x;
			double y = c.y;
			double r = c.radius;
			String line;
			
			double xx;
			double yy;

			// the first quadrant
			xx = x;
			yy = y;
			line = x + "," + y + "," + r;
			coordSet.add(line);

			// the second quadrant
			xx = x;
			yy = y == 0 ? y : -y;
			line = xx + "," + yy + "," + r;
			coordSet.add(line);

			// the third quadrant
			xx = x == 0 ? 0 : -x;
			yy = y == 0 ? 0 : -y;
			line = xx + "," + yy + "," + r;
			coordSet.add(line);

			// the fourth quadrant
			xx = x == 0 ? 0 : -x;
			yy = y;
			line = xx + "," + yy + "," + r;
			coordSet.add(line);
		}
	}

	private void processFirstQuadrant() {
		double x = 0;
		double n = Math.ceil((R - interval) / interval);
		for (int i = 0; i <= n; i++) {
			double y = i * interval;

			double yy = y;
			double l = Math.sqrt(R * R - yy * yy);

			if (i % 2 == 0) {// case 1
				processMainGroup(y, l, 0);
			} else {
				// case 2
				processComplementGroup(y, l);
			}
		}
	}

	private void processComplementGroup(double y, double l) {
		l = l - interval;
		processMainGroup(y, l, interval);
	}

	private void processMainGroup(double yy, double l, double startingValue) {
		double nn = Math.ceil((l - interval) / (2 * interval));
		for (int ii = 0; ii <= nn; ii++) {
			double xx = 2 * interval * ii + startingValue;
			// (xx,yy,interval)
			tmpList.add(new Coord(xx, yy, interval));
			// String line = xx + "," + yy;
			// System.out.println(line);
		}
	}

	private void iteratorList() {
		for (Coord c : list) {
			// String line = "" + c.x+"," + c.y + "," + c.radius+"";
			String line = "" + c.x + "," + c.y;
			System.out.println(line);
		}
	}

}
