package org.playuniverse.minecraft.mcs.spigot.language.placeholder;

public class Template implements PlaceholderStore {

	private final String original;

	private final String key;
	private final String content;

	private final DefaultPlaceholderStore store = new DefaultPlaceholderStore();

	public Template(String original, String key, String content) {
		this.original = original;
		this.key = key;
		this.content = content;
		PlaceholderParser.parse(store, content);
	}

	public String getOriginal() {
		return original;
	}
	
	public String getReplaceContent() {
		return PlaceholderParser.apply(store, content);
	}

	public String getContent() {
		return content;
	}

	public String getKey() {
		return key;
	}

	@Override
	public void setPlaceholder(Placeholder value) {
		return;
	}

	@Override
	public Placeholder getPlaceholder(String key) {
		return store.getPlaceholder(key);
	}

	@Override
	public Placeholder[] placeholderArray() {
		return store.placeholderArray();
	}

}
