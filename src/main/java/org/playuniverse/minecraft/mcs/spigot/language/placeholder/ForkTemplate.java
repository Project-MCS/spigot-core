package org.playuniverse.minecraft.mcs.spigot.language.placeholder;

public class ForkTemplate extends Template {

	private final Template parent;

	public ForkTemplate(Template parent, String key) {
		super(parent.getOriginal(), key, parent.getContent());
		this.parent = parent;
	}

	public Template getParent() {
		return parent;
	}

}
