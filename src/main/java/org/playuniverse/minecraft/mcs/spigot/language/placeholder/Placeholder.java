package org.playuniverse.minecraft.mcs.spigot.language.placeholder;

public final class Placeholder implements Placeable {

    private final String original;

    private final String key;
    private String value = "";

    public Placeholder(String original, String key) {
        this.original = original;
        this.key = key;
    }

    public Placeholder setValue(String value) {
        this.value = value;
        return this;
    }

    protected String getOriginal() {
        return original;
    }

    public String getValue() {
        return value;
    }

    public String getKey() {
        return key;
    }

    @Override
    public String getPlaceKey() {
        return original;
    }

    @Override
    public String getPlaceValue() {
        return value;
    }

    public static Placeholder of(String key, String value) {
        return new Placeholder("", key).setValue(value);
    }

    public static Placeholder[] array(Placeholder... placeholders) {
        return placeholders;
    }
    
    @Override
    public String toString() {
        return '[' + getKey() + ", " + getValue() + ']';
    }

}
