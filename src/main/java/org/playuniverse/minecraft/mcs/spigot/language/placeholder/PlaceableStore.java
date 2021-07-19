package org.playuniverse.minecraft.mcs.spigot.language.placeholder;

public interface PlaceableStore {

	default Placeable getPlaceable(String key) {
		return getPlaceable(key, false);
	}

	Placeable getPlaceable(String key, boolean flag);

	Placeable[] placeableArray();
	
	boolean isEmpty();

}
