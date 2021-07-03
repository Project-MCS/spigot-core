package org.playuniverse.minecraft.vcompat.reflection;

import java.io.File;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

import org.bukkit.Bukkit;
import org.playuniverse.minecraft.vcompat.base.data.nbt.NbtAdapterRegistry;
import org.playuniverse.minecraft.vcompat.base.data.nbt.NbtContainer;
import org.playuniverse.minecraft.vcompat.reflection.data.WrappedContainer;
import org.playuniverse.minecraft.vcompat.reflection.data.persistence.DataDistributor;
import org.playuniverse.minecraft.vcompat.reflection.data.wrap.SimpleSyntaxContainer;

public class DataProvider extends VersionHandler<VersionControl> {

    public static final Function<UUID, String> DEFAULT_NAMING = UUID::toString;
    public static final Supplier<UUID> DEFAULT_RANDOM = UUID::randomUUID;

    protected final NbtAdapterRegistry registry = new NbtAdapterRegistry();
    protected final DataDistributor<UUID> defaultDistributor = createDistributor(
        new File(Bukkit.getWorlds().get(0).getWorldFolder(), "pluginData"));

    protected DataProvider(VersionControl versionControl) {
        super(versionControl);
    }

    public final NbtAdapterRegistry getRegistry() {
        return registry;
    }

    public WrappedContainer createContainer() {
        return new SimpleSyntaxContainer<>(new NbtContainer(registry));
    }

    public WrappedContainer createPersistentContainer() {
        return new SimpleSyntaxContainer<>(defaultDistributor.create());
    }

    public DataDistributor<UUID> getDefaultDistributor() {
        return defaultDistributor;
    }

    public DataDistributor<UUID> createDistributor(File location) {
        return new DataDistributor<>(this, location, DEFAULT_NAMING, DEFAULT_RANDOM);
    }

    public <K> DataDistributor<K> createDistributor(File location, Function<K, String> namingFunction, Supplier<K> randomFunction) {
        return new DataDistributor<>(this, location, namingFunction, randomFunction);
    }

}