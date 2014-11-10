package DynamicProg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.zen.algorithm.IValidator;
import org.zen.text.Delimiter;

/**
 * 
 * @author Feng Zhou
 *         <p>
 *         Generic template to support collection permutations.
 *         <p>
 *         Support any collection where sequence matters. Such as character array, or sequence of numbers.
 *         <p>
 *         Allow hook up customized validator. So need to create specialized solutions to filter out permutations. For example,
 *         validate parentheses (right always need match left).
 * 
 * @param <T>
 *            T is the item type. Could be Character, Integer, etc.
 */
public class Permutations<T> {

	public static void main(String[] args) {
		// List<String> testData = Arrays.asList("a", "ab", "abc", "abcd", "abcde");
		List<String> testData = new ArrayList<String>();
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < 5; i++) {
			buffer.append(")(");
			testData.add(buffer.toString());
		}
		org.zen.algorithm.IValidator<Character> pv = null;
		if (testData.get(0).indexOf('(') > -1)
			pv = new ParenthesisValidator();

		for (String s : testData) {
			ArrayList<Character> lst = new ArrayList<Character>();
			for (char c : s.toCharArray()) {
				lst.add(c);
			}

			Permutations<Character> pm = new Permutations<Character>(lst, pv);
			List<Collection<Character>> permuted = pm.getList();
			for (Collection<Character> ps : permuted) {
				// System.out.println(GenericUtil.toString(ps, ""));
			}
			System.out.printf("%s: length: %d. HitCount: %d. Perms: %d\n", s, s.length(), pm.getHitCount(), permuted.size());
		}
	}

	public Permutations(Collection<T> data) {
		_data = data;
	}

	public Permutations(Collection<T> data, org.zen.algorithm.IValidator<T> validator) {
		_permutations = new ArrayList<Collection<T>>();
		_data = data;
		_validator = validator;
	}

	public List<Collection<T>> getList() {
		doPermutation((new ArrayList<T>()), _data);
		return _permutations;
	}

	public int getHitCount() {
		return _hitCount;
	}

	// Move out one element at a time, then continue permutation, then add back the element
	// Permutation = Prefixes + Subset
	private void doPermutation(List<T> prefixes, Collection<T> subset) {
		_hitCount++;
		String s = Delimiter.toString(prefixes, "") + " -- " + Delimiter.toString(subset, "");

		HashSet<T> prefixUsed = new HashSet<T>();
		int index = -1;
		for (T item : subset) {
			index++; // current index

			// Add item into prefixes and see if it's allowed
			if (prefixUsed.contains(item))
				continue;

			List<T> newPrefixes = new ArrayList<T>(prefixes);
			newPrefixes.add(item);
			if (_validator != null && !_validator.isValid(newPrefixes))
				continue;
			prefixUsed.add(item);

			// recursive call until it's done
			List<T> newSubset = new ArrayList<T>(subset);
			newSubset.remove(index);
			if (newSubset.size() == 0)
				_permutations.add(newPrefixes);
			else
				doPermutation(newPrefixes, newSubset);
		}
	}

	private Collection<T> _data;
	private IValidator<T> _validator;
	private List<Collection<T>> _permutations;
	private int _hitCount = 0;
}
