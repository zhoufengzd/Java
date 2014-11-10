package org.zen.text;

public final class RegexPatterns {
	public final static String Comma = ",";
	public final static String Space = "\\s";
	public final static String SpaceOptional = "\\s*";
	public final static String SpaceRequired = "\\s+";
	public final static String CommaOrSpace1 = "[,\\s]%s";

	public final static String WordFmt = "\\b%s\\b";

	public final static String DelimiterWithSpaceFmt = "\\s*%s\\s*";
	public final static String NotCharFmt = "[^%s]";

	public final static String EmptyLine = "^\\s*$";

	public final static String Name = "[A-Z]%s[a-z-]+"; // Smith and O’Brian and Doe-Ray
	public final static String FirstNameLastName = String.format("(%s)(%s)(%s)", RegexPatterns.Name, "\\s+", RegexPatterns.Name);
	public final static String LastNameFirstName = String.format("(%s)(%s)(%s)", RegexPatterns.Name, "\\s*,\\s*", RegexPatterns.Name);

	// / <example>Provider=SQLOLEDB;Server=LocalHost;</example>
	public final static String KeyValueEscapeChars = ";=#";
	public final static String KVName = String.format("(?<name>[^%s]+)", KeyValueEscapeChars); // any char except those escape chars
	public final static String KVValue = "(?<value>.*?)"; // anything as the value, but not greedy
	public final static String KVEndMark = "(?<end>$|(?<!;);$|(?<!;);(?!;))";
	public final static String KeyValuePair = String.format("%s\\s*=\\s*%s%s", KVName, KVValue, KVEndMark);
}
