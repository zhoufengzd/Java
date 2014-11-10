package org.zen.collection;

public class PrefixTreeNode {
	char letter;
	PrefixTreeNode[] links;
	boolean fullWord;

	PrefixTreeNode(char letter)
	{
		this.letter = letter;
		links = new PrefixTreeNode[26];
		this.fullWord = false;
	}

}
