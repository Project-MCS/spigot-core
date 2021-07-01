package org.playuniverse.minecraft.mcs.spigot.bukkit.inject;

import org.playuniverse.minecraft.mcs.spigot.registry.TypedRegistry;
import org.playuniverse.minecraft.vcompat.reflection.reflect.ClassLookupProvider;

public class Injections {

    private final TypedRegistry<Injector<?>> injectors = new TypedRegistry<>();
    private final ClassLookupProvider provider;

    public Injections(ClassLookupProvider provider) {
        this.provider = provider;
    }

    public TypedRegistry<Injector<?>> getInjectorRegistry() {
        return injectors;
    }

    public ClassLookupProvider getProvider() {
        return provider;
    }

    public void setup() {
        for (Injector<?> injector : injectors.values()) {
            if (injector.isSetup()) {
                continue;
            }
            injector.setup(provider);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> boolean inject(T object) {
        Injector<?> injector = injectors.getFor(object);
        if (injector == null) {
            return false;
        }
        ((Injector<T>) injector).inject(provider, object);
        return true;
    }

    @SuppressWarnings("unchecked")
    public <T> boolean uninject(T object) {
        Injector<?> injector = injectors.getFor(object);
        if (injector == null) {
            return false;
        }
        ((Injector<T>) injector).uninject(provider, object);
        return true;
    }

    public void uninjectAll() {
        for (Injector<?> injector : injectors.values()) {
            injector.uninjectAll(provider);
        }
    }

    public void dispose() {
        for (Injector<?> injector : injectors.values()) {
            injector.dispose();
        }
        injectors.dispose();
    }

}
