package DynamicProg;

public class Knapsack {

	public static void main(String args[]) {
		int[] value = { 7, 9, 5, 12, 14, 6, 12 };
		int[] weights = { 3, 4, 2, 6, 7, 3, 5 };
		boolean[] usedMark = new boolean[value.length];

		System.out.println(getBestValue(value, weights, usedMark, value.length - 1, 3, 15));
		for (int i = 0; i < usedMark.length; i++) {
			if (usedMark[i])
				System.out.printf("%d, ", i);
		}
	}

	// itemIndex: current index to check the item
	public static int getBestValue(int[] value, int[] weights, boolean[] usedMark, int itemIndex, int itemAllowed,
			int weightAllowed)
	{
		if (itemIndex < 0 || weightAllowed <= 0 || itemAllowed <= 0)
			return 0;

		if (weights[itemIndex] > weightAllowed) {
			return getBestValue(value, weights, usedMark, itemIndex - 1, itemAllowed, weightAllowed); // can’t use nth item
		}
		else {
			int withItemResult = value[itemIndex]
					+ getBestValue(value, weights, usedMark, itemIndex - 1, itemAllowed - 1, weightAllowed - weights[itemIndex]);
			int withoutItemResult = getBestValue(value, weights, usedMark, itemIndex - 1, itemAllowed, weightAllowed);
			if (withItemResult > withoutItemResult) {
				usedMark[itemIndex] = true;
				return withItemResult;
			}
			else {
				return withoutItemResult;
			}
		}
	}
}
