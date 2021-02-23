package org.playuniverse.minecraft.mcs.spigot.config;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

import com.syntaxphoenix.syntaxapi.config.yaml.YamlConfig;
import com.syntaxphoenix.syntaxapi.config.yaml.YamlConfigSection;

public class MigrationContext {

    private final Map<String, Object> values;

    public MigrationContext(YamlConfig configuration) {
        this.values = mapRootSection(configuration);
    }

    public Map<String, Object> getValues() {
        return values;
    }

    public <E> MigrationContext map(String path, Class<E> sample, Function<E, Object> mapper) {
        if (values.containsKey(path)) {
            E value = safeCast(sample, values.remove(path));
            if (value == null)
                return this;
            values.put(path, mapper.apply(value));
        }
        return this;
    }

    public MigrationContext remove(String path) {
        values.remove(path);
        return this;
    }

    public MigrationContext move(String path, String newPath) {
        if (values.containsKey(path))
            values.put(newPath, values.remove(path));
        return this;
    }

    public MigrationContext stack(String stack, String path) {
        if (values.containsKey(path))
            values.put(stack + '.' + path, values.remove(path));
        return this;
    }

    private <E> E safeCast(Class<E> sample, Object value) {
        return sample.isInstance(value) ? sample.cast(value) : null;
    }

    /*
     * Mapping
     */

    public static final String KEY = "%s.%s";

    public static Map<String, Object> mapRootSection(YamlConfigSection section) {
        LinkedHashMap<String, Object> output = new LinkedHashMap<>();
        for (String key : section.getKeys()) {
            Object value = section.get(key);
            if (value instanceof YamlConfigSection) {
                mapSubSection(key, output, (YamlConfigSection) value);
                continue;
            }
            output.put(key, value);
        }
        return output;
    }

    public static void mapSubSection(String previous, Map<String, Object> output, YamlConfigSection section) {
        String path = previous;
        for (String key : section.getKeys()) {
            Object value = section.get(key);
            if (value instanceof YamlConfigSection) {
                mapSubSection(path + '.' + section.getName(), output, (YamlConfigSection) value);
                continue;
            }
            output.put(String.format(KEY, path, key), value);
        }
    }

}