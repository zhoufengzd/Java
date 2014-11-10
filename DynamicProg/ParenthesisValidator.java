package DynamicProg;

import java.util.Collection;

/**
 * 
 * @author Feng Zhou
 *         <p>
 *         Simple Parenthesis pair validation logic: if left parenthesis comes before right parenthesis, then it's fine.
 *         <p>
 *         Prefixes: '(()': true. '())': false.
 */
public class ParenthesisValidator implements org.zen.algorithm.IValidator<Character> {
	public boolean isValid(Collection<Character> prefixes) {
		int leftParensisCount = 0;
		for (Character t : prefixes) {
			if (t.toString().equals("("))
				leftParensisCount++;
			else
				leftParensisCount--;
		}

		return (leftParensisCount > -1);
	}
}