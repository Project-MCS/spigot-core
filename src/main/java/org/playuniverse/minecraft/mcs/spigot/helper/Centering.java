package org.playuniverse.minecraft.mcs.spigot.helper;

import net.md_5.bungee.api.ChatColor;

public final class Centering {

	private Centering() {}

	public static String center(String input) {
		String[] sentences = input.split("\n");
		int length = 0;
		for (String sentence : sentences) {
			int size = ChatColor.stripColor(sentence).length();
			if (size > length) {
				length = size;
			}
		}
		StringBuilder builder = new StringBuilder();
		for (String sentence : sentences) {
			int size = ChatColor.stripColor(sentence).length();
			int difference = length - size;
			int spaces = difference / 2;
			for (int current = 0; current < spaces; current++) {
				builder.append(' ');
			}
			builder.append(sentence).append('\n');
		}
		String output = builder.toString();
		return output.substring(0, output.length() - 1);
	}

}
