package org.playuniverse.minecraft.mcs.spigot.language.placeholder;

import java.util.ArrayList;

public class DefaultTemplateStore implements TemplateStore {

	private final ArrayList<Template> templates = new ArrayList<>();

	@Override
	public void setTemplate(Template value) {
		if (hasTemplate(value.getKey())) {
			return;
		}
		templates.add(value);
	}

	public boolean hasTemplate(String key) {
		return templates.stream().anyMatch(template -> template.getKey().equals(key));
	}

	@Override
	public Template getTemplate(String key) {
		return templates.stream().filter(template -> template.getKey().equals(key)).findFirst().orElse(null);
	}

	@Override
	public Template[] templateArray() {
		return templates.toArray(Template[]::new);
	}

}
