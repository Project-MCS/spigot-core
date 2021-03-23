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

    public final boolean inject(ReflectionProvider provider, T object) {
        if (setup) {
            inject(provider, object);
            return true;
        }
        return false;
    }

    public final boolean uninject(ReflectionProvider provider, T object) {
        if (setup) {
            uninject(provider, object);
            return true;
        }
        return false;

    }

    public final boolean uninjectAll(ReflectionProvider provider) {
        if (setup) {
            uninjectAll(provider);
            return true;
        }
        return false;
    }

    protected abstract void inject0(ReflectionProvider provider, T object);

    protected abstract void uninject0(ReflectionProvider provider, T object);

    protected abstract void uninjectAll0(ReflectionProvider provider);

    protected abstract void dispose();

}
