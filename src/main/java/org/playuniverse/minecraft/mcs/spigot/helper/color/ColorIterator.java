package org.playuniverse.minecraft.mcs.spigot.helper.color;

import java.util.Iterator;

import org.playuniverse.minecraft.mcs.spigot.helper.ColorHelper;

public abstract class ColorIterator implements Iterator<String> {

	@Override
	public String next() {
		return ColorHelper.toHexColor(nextColor());
	}

	public abstract float[] nextColor();
	
	public abstract void reset();

}
