package DynamicProg;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SubSet {
	public static void main(String[] args) {
		ArrayList<Integer> dt = new ArrayList<Integer>();
		for (int i = 1; i < 4; i++) {
			dt.add(i);
		}
		SubSet s = new SubSet();

		List<List<Integer>> solutions = s.getSubSet(dt);
		System.out.println(solutions.toString());
	}

	public SubSet() {
	}

	public List<List<Integer>> getSubSet(List<Integer> data) {
		return doGetSubSet(data);
	}

	private List<List<Integer>> doGetSubSet(List<Integer> workingSet) {
		List<List<Integer>> st = new ArrayList<List<Integer>>();
		if (workingSet.size() == 0) {
			st.add(new ArrayList<Integer>());
			return st;
		}

		Integer e = next(workingSet);

		List<Integer> oneElement = new ArrayList<Integer>();
		oneElement.add(e);
		st.add(oneElement);
		for (List<Integer> subSet : doGetSubSet(workingSet)) {
			st.add(subSet);

			List<Integer> expandedSet = new ArrayList<Integer>(subSet);
			expandedSet.add(e);
			st.add(expandedSet);
		}

		return st;
	}

	private Integer next(List<Integer> subset) {
		Iterator<Integer> it = subset.iterator();
		Integer obj = null;
		if (it.hasNext()) {
			obj = it.next();
			it.remove();
		}

		return obj;
	}
}
