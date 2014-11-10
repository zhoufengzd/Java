package DynamicProg;

import CtCILibrary.AssortedMethods;

public class MaxSequence {
	public static void main(String args[]) {
		// RandomGenerator rg = new RandomGenerator();
		// for (int i = 0; i < 5; i++) {
		// System.out.println(GenericUtil.toString(rg.getArrayList(20, -75, 10)));
		// // AssortedMethods.printIntArray(AssortedMethods.randomArray(20, -75, 10));
		// }

		for (int i = 0; i < 5; i++) {
			int[] numbers = AssortedMethods.randomArray(10, -10, 8);
			AssortedMethods.printIntArray(numbers);
			System.out.println(maxSequenceDyn(numbers, 0, 0, 0));
		}
	}

	public static int maxSequence(int[] numbers) {
		if (numbers.length < 1)
			return 0;

		int maxSum = numbers[0];
		int currentSum = maxSum;
		int currentStart = 0, maxStart = 0, maxEnd = 0;
		for (int i = 1; i < numbers.length; i++) {
			currentSum += numbers[i];

			if (currentSum < 0) {
				currentSum = 0;
				currentStart = i + 1;
			}

			if (currentSum > maxSum) {
				maxSum = currentSum;
				maxStart = currentStart;
				maxEnd = i;
			}
		}

		AssortedMethods.printIntArray(numbers);
		System.out.println(String.format("max=%d, range: [%d -- %d]", maxSum, maxStart, maxEnd));
		return maxSum;
	}

	// plus
	public static int maxSequenceDyn(int[] numbers, int maxSum, int currentSum, int i) {
		if (i == numbers.length)
			return maxSum;

		currentSum += numbers[i];
		if (currentSum < 0)
			currentSum = 0;

		if (currentSum > maxSum)
			maxSum = currentSum;

		return maxSequenceDyn(numbers, maxSum, currentSum, i + 1);
	}

}
