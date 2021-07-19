package org.playuniverse.minecraft.mcs.spigot.registry;

public class TypedRegistry<T extends ITyped<?>> extends Registry<Class<?>, T> {

    public T getFor(Object object) {
        if (object == null) {
            return null;
        }
        Class<?> clazz = object.getClass();
        if (clazz.isArray()) {
            clazz = clazz.getComponentType();
        }
        Class<?>[] array = map.keySet().toArray(Class<?>[]::new);
        for (Class<?> key : array) {
            if (!key.equals(clazz)) {
                continue;
            }
            return get(key);
        }
        for (Class<?> key : array) {
            if (!key.isAssignableFrom(clazz)) {
                continue;
            }
            return get(key);
        }
        return null;
    }

    @Override
    public T getOrElse(Class<?> key, T value) {
        T output = get(key);
        if (output == null) {
            if (value.getType().equals(key)) {
                register(value.getType(), value);
                return value;
            }
            return null;
        }
        return output;
    }

    public boolean register(T typed) {
        if (map.containsKey(typed.getType())) {
            return false;
        }
        map.put(typed.getType(), typed);
        return true;
    }

    @Override
    public boolean register(Class<?> key, T value) {
        if (!key.equals(value.getType()) || map.containsKey(key)) {
            return false;
        }
        map.put(key, value);
        return true;
    }

}
