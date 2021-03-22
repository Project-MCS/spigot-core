package org.playuniverse.minecraft.mcs.spigot.bukkit.inject;

import org.playuniverse.minecraft.mcs.spigot.registry.TypedRegistry;

import net.sourcewriters.minecraft.versiontools.reflection.reflect.ReflectionProvider;

public class Injections {

    private final TypedRegistry<Injector<?>> injectors = new TypedRegistry<>();
    private final ReflectionProvider provider;

    public Injections(ReflectionProvider provider) {
        this.provider = provider;
    }

    public TypedRegistry<Injector<?>> getInjectorRegistry() {
        return injectors;
    }

    public ReflectionProvider getProvider() {
        return provider;
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
        for(Injector<?> injector : injectors.values()) {
            injector.dispose();
        }
        injectors.dispose();
    }

}
