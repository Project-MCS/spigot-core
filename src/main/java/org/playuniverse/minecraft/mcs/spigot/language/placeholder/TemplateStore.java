package org.playuniverse.minecraft.mcs.spigot.language.placeholder;

public interface TemplateStore {

	void setTemplate(Template value);

	Template getTemplate(String key);

	Template[] templateArray();

}
