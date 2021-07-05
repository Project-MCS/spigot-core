package org.playuniverse.minecraft.mcs.spigot.config;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import com.syntaxphoenix.syntaxapi.config.BaseSection;
import com.syntaxphoenix.syntaxapi.config.SectionMap;

public class MigrationContext {

    protected final Map<String, Object> values = new LinkedHashMap<>();

    public MigrationContext(BaseSection section) {
        mapRootSection(section.toMap());
    }

    @SuppressWarnings("unchecked")
    private final void mapRootSection(SectionMap<String, Object> map) {
        for (Entry<String, Object> entry : map.entrySet()) {
            Object obj = entry.getValue();
            if (obj == null) {
                continue;
            }
            if (obj instanceof SectionMap) {
                mapSection(entry.getKey(), (SectionMap<String, Object>) obj);
                continue;
            }
            values.put(entry.getKey(), obj);
        }
    }

    @SuppressWarnings("unchecked")
    private final void mapSection(String previous, SectionMap<String, Object> map) {
        previous = previous + '.';
        for (Entry<String, Object> entry : map.entrySet()) {
            Object obj = entry.getValue();
            if (obj == null) {
                continue;
            }
            if (obj instanceof SectionMap) {
                mapSection(previous + entry.getKey(), (SectionMap<String, Object>) obj);
                continue;
            }
            values.put(previous + entry.getKey(), obj);
        }
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

}