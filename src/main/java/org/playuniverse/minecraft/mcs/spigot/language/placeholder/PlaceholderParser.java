package org.playuniverse.minecraft.mcs.spigot.language.placeholder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholderParser {

	public static final Pattern PLACEHOLDER = Pattern.compile("(?<placeholder>\\$\\(\\\"(?<key>[a-zA-Z0-9\\_\\-\\. ]+)\\\"\\))");

	private PlaceholderParser() {}

	public static Placeholder[] parse(String data) {
		DefaultPlaceholderStore store = new DefaultPlaceholderStore();
		parse(store, data);
		return store.placeholderArray();
	}

	public static void parse(PlaceholderStore store, String data) {
		Matcher matcher = PLACEHOLDER.matcher(data);
		while (matcher.find()) {
			store.setPlaceholder(new Placeholder(matcher.group("placeholder"), matcher.group("key")));
		}
	}

	public static String apply(PlaceableStore store, String data) {
		String output = data;
		for (Placeable placeable : store.placeableArray()) {
			output = output.replace(placeable.getPlaceKey(), placeable.getPlaceValue());
		}
		return output;
	}

}
