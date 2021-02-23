package org.playuniverse.minecraft.mcs.spigot.utils.java;

import java.util.HashMap;

import org.pf4j.Plugin;
import org.pf4j.PluginManager;
import org.pf4j.PluginWrapper;
import org.playuniverse.minecraft.mcs.spigot.base.PluginBase;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;

import com.syntaxphoenix.syntaxapi.utils.java.Arrays;

public final class ReflectionProvider {

    private static final HashMap<PluginBase<?>, ReflectionProvider> PROVIDERS = new HashMap<>();

    public static ReflectionProvider of(PluginBase<?> base) {
        if (PROVIDERS.containsKey(base)) {
            return PROVIDERS.get(base);
        }
        ReflectionProvider provider = new ReflectionProvider(base);
        PROVIDERS.put(base, provider);
        return provider;
    }

    private final HashMap<String, Reflections> reflections = new HashMap<>();
    private final PluginManager pluginManager;
    private final PluginBase<?> base;

    private ReflectionProvider(PluginBase<?> base) {
        this.pluginManager = base.getPluginManager();
        this.base = base;
    }

    /*
     * ClassLoaders
     */

    private ClassLoader[] defaults;

    private ClassLoader[] classLoaders() {
        if (defaults != null) {
            return defaults;
        }
        return defaults = Arrays.merge(size -> new ClassLoader[size], ClasspathHelper.classLoaders(), getClass().getClassLoader(),
            ClassLoader.getSystemClassLoader(), ClassLoader.getPlatformClassLoader(), Runtime.getRuntime().getClass().getClassLoader());
    }

    private Object[] buildParameters(String packageName, ClassLoader... loaders) {
        return Arrays.merge(new Object[] {
            packageName
        }, loaders.length == 0 ? classLoaders() : Arrays.merge(size -> new ClassLoader[size], classLoaders(), loaders));
    }

    /*
     * Reflections
     */

    public Reflections of(String packageName) {
        return of((PluginWrapper) null, packageName);
    }

    public Reflections of(Class<? extends Plugin> clazz, String packageName) {
        return of(clazz == null ? null : pluginManager.whichPlugin(clazz), packageName);
    }

    public Reflections of(PluginWrapper wrapper, String packageName) {
        synchronized (reflections) {
            if (reflections.containsKey(packageName)) {
                return reflections.get(packageName);
            }
        }
        Reflections reflect = new Reflections(
            wrapper == null ? buildParameters(packageName) : buildParameters(packageName, wrapper.getPluginClassLoader()));
        synchronized (reflections) {
            reflections.put(packageName, reflect);
        }
        return reflect;
    }

    public boolean has(String packageName) {
        synchronized (reflections) {
            return reflections.containsKey(packageName);
        }
    }

    public boolean delete(String packageName) {
        synchronized (reflections) {
            return reflections.remove(packageName) != null;
        }
    }

    /*
     * Data
     */

    public ReflectionProvider flush() {
        synchronized (reflections) {
            reflections.clear();
        }
        return this;
    }

    public void dispose() {
        PROVIDERS.remove(base, flush());
    }

}
