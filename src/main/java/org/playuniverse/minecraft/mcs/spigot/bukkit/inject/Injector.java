package org.playuniverse.minecraft.mcs.spigot.bukkit.inject;

import org.playuniverse.minecraft.mcs.spigot.registry.ITyped;
import org.playuniverse.minecraft.vcompat.reflection.reflect.ClassLookupProvider;

public abstract class Injector<T> implements ITyped<T> {

    private boolean setup = false;

    public final boolean isSetup() {
        return setup;
    }

    public final void setup(ClassLookupProvider provider) {
        if (setup) {
            throw new IllegalStateException("Is already setup!");
        }
        setup = true;
        onSetup(provider);
    }

    protected abstract void onSetup(ClassLookupProvider provider);

    public abstract boolean isCompatible(ClassLookupProvider provider);

    public final boolean inject(ClassLookupProvider provider, T object) {
        if (setup) {
            inject0(provider, object);
            return true;
        }
        return false;
    }

    public final boolean uninject(ClassLookupProvider provider, T object) {
        if (setup) {
            uninject0(provider, object);
            return true;
        }
        return false;

    }

    public final boolean uninjectAll(ClassLookupProvider provider) {
        if (setup) {
            uninjectAll0(provider);
            return true;
        }
        return false;
    }

    protected abstract void inject0(ClassLookupProvider provider, T object);

    protected abstract void uninject0(ClassLookupProvider provider, T object);

    protected abstract void uninjectAll0(ClassLookupProvider provider);

    protected abstract void dispose();

}
