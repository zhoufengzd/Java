package org.zen.collection;

import java.util.ArrayList;
import java.util.List;

import org.zen.common.def.Pair;

public class PrefixTree {

	static PrefixTreeNode createTree()
	{
		return (new PrefixTreeNode('\0'));
	}

	static void insertWord(PrefixTreeNode root, String word)
	{
		int offset = 'a';
		char[] letters = word.toLowerCase().toCharArray();
		PrefixTreeNode curNode = root;

		for (char c : letters)
		{
			if (curNode.links[c - offset] == null)
				curNode.links[c - offset] = new PrefixTreeNode(c);
			curNode = curNode.links[c - offset];
		}
		curNode.fullWord = true;
	}

	static boolean find(PrefixTreeNode root, String word)
	{
		char[] letters = word.toCharArray();
		int l = letters.length;
		int offset = 97;
		PrefixTreeNode curNode = root;

		int i;
		for (i = 0; i < l; i++)
		{
			if (curNode == null)
				return false;
			curNode = curNode.links[letters[i] - offset];
		}

		if (i == l && curNode == null)
			return false;

		if (curNode != null && !curNode.fullWord)
			return false;

		return true;
	}

	static void printTree(PrefixTreeNode root, int level, char[] branch)
	{
		if (root == null)
			return;

		for (int i = 0; i < root.links.length; i++)
		{
			branch[level] = root.letter;
			printTree(root.links[i], level + 1, branch);
		}

		if (root.fullWord)
		{
			System.out.print("-" + branch[0]);
			for (int j = 1; j <= level; j++)
				System.out.print(branch[j]);
			System.out.println();
		}
	}

	public static void main(String[] args)
	{
		PrefixTreeNode tree = createTree();

		Trie<Character> t = new Trie<Character>();
		String[] words = { "an", "ant", "all", "allot", "alloy", "aloe", "are", "ate", "be" };
		for (String wd : words) {
			List<Character> wdList = new ArrayList<Character>();
			for (char c : wd.toLowerCase().toCharArray())
				wdList.add(c);
			t.addWord(wdList);
			insertWord(tree, wd);
		}

		TreeHelper<Character> helper = new TreeHelper<Character>();
		List<Pair<Node<Character>, List<Node<Character>>>> pathList = helper.getPath(t);
		for (Pair<Node<Character>, List<Node<Character>>> entry : pathList) {
			StringBuilder buffer = new StringBuilder();
			for (Node<Character> nd : entry.getSecond())
				buffer.append(nd);

			TrieNode<Character> tnd = (TrieNode<Character>) entry.getFirst();
			if (tnd.isWord()) {
				buffer.append(tnd);
				System.out.println(buffer.toString());
			}
		}

		List<Character> wd1 = new ArrayList<Character>();
		wd1.add('a');
		wd1.add('l');
		wd1.add('o');
		System.out.println(wd1.toString() + ": " + t.find(wd1));
		List<Character> wd2 = new ArrayList<Character>();
		wd2.add('a');
		wd2.add('n');
		System.out.println(wd2.toString() + ": " + t.find(wd2));

		char[] branch = new char[50];
		printTree(tree, 0, branch);

		String searchWord = "all";
		if (find(tree, searchWord))
		{
			System.out.println("The word was found");
		}
		else
		{
			System.out.println("The word was NOT found");
		}
	}
}
