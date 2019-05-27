package com.ahei.gonetty;

import java.util.HashSet;
import java.util.Set;

public class NameGenerator {
	// class variable
	static final String lexicon = "ABCDEFGHIJKLMNOPQRSTUVWXYZ12345674890abcdefghijklmnopqrstuvwxyz";

	static final java.util.Random rand = new java.util.Random();

	// consider using a Map<String,Boolean> to say whether the identifier is being
	// used or not
	static final Set<String> identifiers = new HashSet<String>();

	public static String generator() {
		StringBuilder builder = new StringBuilder();
		while (builder.toString().length() == 0) {
			int length = rand.nextInt(3) + 3;
			for (int i = 0; i < length; i++) {
				builder.append(lexicon.charAt(rand.nextInt(lexicon.length())));
			}
			if (identifiers.contains(builder.toString())) {
				builder = new StringBuilder();
			}
		}
		return builder.toString();
	}
}
