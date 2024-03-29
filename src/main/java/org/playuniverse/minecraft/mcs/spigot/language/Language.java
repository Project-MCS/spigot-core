
package org.playuniverse.minecraft.mcs.spigot.language;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.playuniverse.minecraft.mcs.spigot.registry.IUnique;

public class Language implements IUnique {

    private final String id;

    private String name;

    public Language(String id) {
        this(id, id);
    }

    public Language(String id, String name) {
        this.id = id;
        this.name = name;
    }

    final Language setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getId() {
        return id;
    }

    /*
     * 
     */

    @Override
    public String toString() {
        return name == null ? id : id + "::" + name;
    }

    public static Language fromString(String input) {
        if (!input.contains("::"))
            return new Language(input);
        String[] parts = input.split("::", 2);
        return new Language(parts[0], parts[1]);
    }

    /*
     * 
     */

    public static class LanguageType implements PersistentDataType<String, Language> {

        public static final LanguageType INSTANCE = new LanguageType();

        @Override
        public Class<String> getPrimitiveType() {
            return String.class;
        }

        @Override
        public Class<Language> getComplexType() {
            return Language.class;
        }

        @Override
        public String toPrimitive(Language language, PersistentDataAdapterContext context) {
            return language.toString();
        }

        @Override
        public Language fromPrimitive(String encode, PersistentDataAdapterContext context) {
            return fromString(encode);
        }

    }

}