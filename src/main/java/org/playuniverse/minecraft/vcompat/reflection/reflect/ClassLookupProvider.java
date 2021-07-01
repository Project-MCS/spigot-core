package org.playuniverse.minecraft.vcompat.reflection.reflect;

import static org.playuniverse.minecraft.vcompat.reflection.reflect.FakeLookup.FAKE;

import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Consumer;

import org.playuniverse.minecraft.vcompat.reflection.reflect.handle.ClassLookup;
import org.playuniverse.minecraft.vcompat.reflection.reflect.handle.ClassLookupCache;
import org.playuniverse.minecraft.vcompat.version.ServerVersion;
import org.playuniverse.minecraft.vcompat.version.Versions;

import com.syntaxphoenix.syntaxapi.reflection.ClassCache;

public class ClassLookupProvider {

    public static final String CB_PATH_FORMAT = "org.bukkit.craftbukkit.%s.%s";
    public static final String NMS_PATH_FORMAT = "net.minecraft.server.%s.%s";

    public static final ClassLookupProvider DEFAULT = new ClassLookupProvider(provider -> ClassLookups.globalSetup(provider));

    protected final ClassLookupCache cache;

    protected final String cbPath;
    protected final String nmsPath;

    protected final ServerVersion version;

    private boolean skip = false;

    public ClassLookupProvider() {
        this((Consumer<ClassLookupProvider>) null);
    }

    public ClassLookupProvider(Consumer<ClassLookupProvider> setup) {
        this(new ClassLookupCache(), setup);
    }

    public ClassLookupProvider(ClassLookupCache cache) {
        this(cache, null);
    }

    public ClassLookupProvider(ClassLookupCache cache, Consumer<ClassLookupProvider> setup) {
        this.cache = cache;
        this.version = Versions.getServer();
        this.cbPath = String.format(CB_PATH_FORMAT, Versions.getServerAsString(), "%s");
        this.nmsPath = String.format(NMS_PATH_FORMAT, Versions.getServerAsString(), "%s");
        if (setup != null) {
            setup.accept(this);
        }
    }

    public ServerVersion getVersion() {
        return version;
    }
    
    /*
     * Delete
     */
    
    public void deleteByName(String name) {
        cache.delete(name);
    }
    
    public void deleteByPackage(String path) {
        Entry<String, ClassLookup>[] array = cache.entries();
        for(Entry<String, ClassLookup> entry : array) {
            if(!entry.getValue().getOwner().getPackageName().equals(path)) {
                continue;
            }
            cache.delete(entry.getKey());
        }
    }

    /*
     * Skip
     */

    public ClassLookupProvider require(boolean skip) {
        this.skip = !skip;
        return this;
    }

    public ClassLookupProvider skip(boolean skip) {
        this.skip = skip;
        return this;
    }

    public boolean skip() {
        return skip;
    }

    /*
     * Reflection
     */

    public ClassLookupCache getReflection() {
        return cache;
    }

    public String getNmsPath() {
        return nmsPath;
    }

    public String getCbPath() {
        return cbPath;
    }

    public ClassLookup createNMSLookup(String name, String path) {
        return skip ? FAKE : cache.create(name, getNMSClass(path));
    }

    public ClassLookup createCBLookup(String name, String path) {
        return skip ? FAKE : cache.create(name, getCBClass(path));
    }

    public ClassLookup createLookup(String name, String path) {
        return skip ? FAKE : cache.create(name, getClass(path));
    }

    public ClassLookup createLookup(String name, Class<?> clazz) {
        return skip ? FAKE : cache.create(name, clazz);
    }

    public Optional<ClassLookup> getOptionalLookup(String name) {
        return cache.get(name);
    }

    public ClassLookup getLookup(String name) {
        return cache.get(name).orElse(null);
    }

    public Class<?> getNMSClass(String path) {
        return getClass(String.format(nmsPath, path));
    }

    public Class<?> getCBClass(String path) {
        return getClass(String.format(cbPath, path));
    }

    public Class<?> getClass(String path) {
        return ClassCache.getClass(path);
    }

}