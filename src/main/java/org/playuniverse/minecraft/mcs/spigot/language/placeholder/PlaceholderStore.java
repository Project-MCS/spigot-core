package org.playuniverse.minecraft.mcs.spigot.language.placeholder;

public interface PlaceholderStore extends PlaceableStore {

	void setPlaceholder(Placeholder value);

	Placeholder getPlaceholder(String key);

	Placeholder[] placeholderArray();

	@Override
	default Placeable getPlaceable(String key, boolean flag) {
		return getPlaceholder(key);
	}

	@Override
	default Placeable[] placeableArray() {
		return placeholderArray();
	}

}
