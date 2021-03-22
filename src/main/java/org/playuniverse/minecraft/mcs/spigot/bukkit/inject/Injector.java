package org.playuniverse.minecraft.mcs.spigot.bukkit.inject;

import org.playuniverse.minecraft.mcs.spigot.registry.ITyped;

import net.sourcewriters.minecraft.versiontools.reflection.reflect.ReflectionProvider;

public abstract class Injector<T> implements ITyped<T> {

    private boolean setup = false;

    public final boolean isSetup() {
        return setup;
    }

    public final void setup(ReflectionProvider provider) {
        if (setup) {
            throw new IllegalStateException("Is already setup!");
        }
        setup = true;
        onSetup(provider);
    }

    protected abstract void onSetup(ReflectionProvider provider);

    public abstract boolean isCompatible(ReflectionProvider provider);

    public abstract void inject(ReflectionProvider provider, T object);

    public abstract void uninject(ReflectionProvider provider, T object);

    public abstract void uninjectAll(ReflectionProvider provider);

    protected abstract void dispose();

}
