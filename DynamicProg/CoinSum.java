package DynamicProg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Make changes: limited output of face value Choose the largest coin first (0, 1, 2.., n) so less solutions need calculated.
 * Solutions: Number of largest value coins
 */
public class CoinSum {
	private ArrayList<Integer> _coinDenom;

	public static void main(String[] args) {
		// List<Integer> coins = Arrays.asList(25, 5, 10, 1);// 25: quarter. 10: dime. 5: nickel. 1: penny.
		List<Integer> coins = Arrays.asList(10, 5, 2, 1);// 25: quarter. 10: dime. 5: nickel. 1: penny.
		CoinSum cs = new CoinSum(coins);

		for (int i = 100; i <= 100; i++) {
			List<HashMap<Integer, Integer>> changes = cs.makeChanges(i);
			System.out.printf("makeChanges(%d) = %d\n", i, changes.size());

			for (HashMap<Integer, Integer> sol : changes) {
				for (Integer denom : sol.keySet()) {
					System.out.printf("%d(%d) ", denom, sol.get(denom));
					System.out.println();
				}
			}
		}
	}

	public CoinSum(int[] coinDenom) {
		_coinDenom = new ArrayList<Integer>(coinDenom.length);
		for (int dn : coinDenom)
			_coinDenom.add(dn);

		Collections.sort(_coinDenom, Collections.reverseOrder());
	}

	public CoinSum(List<Integer> coinDenom) {
		_coinDenom = new ArrayList<Integer>(coinDenom);
		Collections.sort(_coinDenom, Collections.reverseOrder());
	}

	public List<HashMap<Integer, Integer>> makeChanges(int sum) {
		return buildSolutions(sum, _coinDenom);
	}

	private List<HashMap<Integer, Integer>> buildSolutions(int sum, List<Integer> availableCoins) {
		if (sum == 0 || availableCoins.size() < 1)
			return null;

		List<HashMap<Integer, Integer>> solutions = new ArrayList<HashMap<Integer, Integer>>();
		Integer denom = availableCoins.get(0);
		if (availableCoins.size() == 1) {
			if (sum % denom != 0)
				return null;

			addSolution(solutions, denom, sum / denom);
			return solutions;
		}
		ArrayList<Integer> subSolutionCoins = new ArrayList<Integer>(availableCoins);
		subSolutionCoins.remove(0);

		List<HashMap<Integer, Integer>> subSolutions = null;
		int leftOver = 0;
		for (int i = 0; i <= sum / denom; i++) {
			leftOver = sum - i * denom;
			if (leftOver == 0) {
				addSolution(solutions, denom, i);
				continue;
			}

			subSolutions = buildSolutions(leftOver, subSolutionCoins);
			if (subSolutions != null) {
				if (i != 0) {
					for (HashMap<Integer, Integer> sol : subSolutions)
						sol.put(denom, i);
				}

				solutions.addAll(subSolutions);
			}
		}

		return solutions;
	}

	private void addSolution(List<HashMap<Integer, Integer>> solutions, Integer denom, Integer count) {
		HashMap<Integer, Integer> sol = new HashMap<Integer, Integer>();
		sol.put(denom, count);
		solutions.add(sol);
	}
}
