package DynamicProg;

/**
 * 
 * Longest Common Subsequence
 */
public class CommonSequence {
	static int[][] cachedMap;

	public static void main(String args[]) {
		String s = "ABAZDC", t = "BACBAD";
		cachedMap = new int[s.length()][t.length()];

		int matchedLength = getLCS(s.toCharArray(), s.length() - 1, t.toCharArray(), t.length() - 1);
		System.out.println(matchedLength);
	}

	public static int getLCS(char[] src, int srcIndex, char[] tgt, int tgtIndex)
	{
		int result = 0;
		if (srcIndex < 0 || tgtIndex < 0)
			return 0;

		if (cachedMap[srcIndex][tgtIndex] != 0) {
			System.out.println(String.format("cachedMap[%d][%d] = %d", srcIndex, tgtIndex, cachedMap[srcIndex][tgtIndex]));
			return cachedMap[srcIndex][tgtIndex];
		}

		if (src[srcIndex] == tgt[tgtIndex])
			result = 1 + getLCS(src, srcIndex - 1, tgt, tgtIndex - 1); // no harm in matching up
		else
			result = Math.max(getLCS(src, srcIndex - 1, tgt, tgtIndex), getLCS(src, srcIndex, tgt, tgtIndex - 1));

		cachedMap[srcIndex][tgtIndex] = result;
		return result;
	}
}
