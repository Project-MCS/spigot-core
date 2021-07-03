package org.playuniverse.minecraft.vcompat.reflection.provider.data.hook;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_17_R1.persistence.CraftPersistentDataContainer;
import org.bukkit.craftbukkit.v1_17_R1.persistence.CraftPersistentDataTypeRegistry;
import org.bukkit.persistence.PersistentDataContainer;
import org.playuniverse.minecraft.vcompat.base.data.api.IDataContainer;
import org.playuniverse.minecraft.vcompat.base.data.nbt.NbtContainer;
import org.playuniverse.minecraft.vcompat.reflection.VersionControl;
import org.playuniverse.minecraft.vcompat.reflection.data.WrappedContainer;
import org.playuniverse.minecraft.vcompat.reflection.provider.data.BukkitContainerImpl;
import org.playuniverse.minecraft.vcompat.reflection.provider.data.SyntaxContainerImpl;
import org.playuniverse.minecraft.vcompat.reflection.reflect.handle.ClassLookup;

import com.syntaxphoenix.syntaxapi.nbt.NbtCompound;

import net.minecraft.nbt.CompoundTag;

@SuppressWarnings({
    "rawtypes",
    "unchecked"
})
public final class BukkitContainerAdapterHookImpl {

    private static final BukkitContainerAdapterHookImpl HOOK = new BukkitContainerAdapterHookImpl();

    private final ClassLookup registryRef = new ClassLookup(CraftPersistentDataTypeRegistry.class)
        .searchMethod("create", "createAdapter", Class.class, Class.class, Function.class, Function.class)
        .searchField("adapters", "adapters")
        .searchField("function", "CREATE_ADAPTER");
    private final ClassLookup entityRef = new ClassLookup(CraftEntity.class).searchField("registry", "DATA_TYPE_REGISTRY");

    private BukkitContainerAdapterHookImpl() {}

    private final HashMap<CraftPersistentDataTypeRegistry, Function> map = new HashMap<>();

    private CraftPersistentDataTypeRegistry getEntityRegistry() {
        return (CraftPersistentDataTypeRegistry) entityRef.getFieldValue("registry");
    }

    private void uninjectAll() {
        for (CraftPersistentDataTypeRegistry registry : map.keySet()) {
            Map adapters = (Map) registryRef.getFieldValue(registry, "adapters");
            adapters.remove(BukkitContainerImpl.class);
            adapters.remove(SyntaxContainerImpl.class);
            registryRef.setFieldValue(registry, "function", map.get(registry));
        }
        map.clear();
    }

    private void inject(CraftPersistentDataTypeRegistry registry) {
        if (map.containsKey(registry)) {
            return;
        }
        map.put(registry, (Function) registryRef.getFieldValue(registry, "function"));
        Function function = clazz -> createAdapter(registry, registryRef.getMethod("create").type().returnType(), (Class) clazz);
        registryRef.setFieldValue(registry, "function", function);
    }

    private <E> E createAdapter(CraftPersistentDataTypeRegistry registry, Class<E> adapterType, Class type) {
        if (Objects.equals(BukkitContainerImpl.class, type)) {
            return (E) buildAdapter(registry, BukkitContainerImpl.class, tag -> fromPrimitiveSyntax(tag));
        }
        if (Objects.equals(SyntaxContainerImpl.class, type)) {
            return (E) buildAdapter(registry, SyntaxContainerImpl.class, tag -> fromPrimitiveBukkit(registry, tag));
        }
        return (E) map.get(registry).apply(type);
    }

    private <C extends WrappedContainer> Object buildAdapter(Object handle, Class<C> type, Function<CompoundTag, C> function) {
        return registryRef.run(handle, "create", type, CompoundTag.class, (Function<C, CompoundTag>) input -> toPrimitive(input),
            function);
    }

    private CompoundTag toPrimitive(WrappedContainer input) {
        Object handle = findFinalContainer(input).getHandle();
        if (handle instanceof PersistentDataContainer) {
            if (handle instanceof CraftPersistentDataContainer) {
                return ((CraftPersistentDataContainer) handle).toTagCompound();
            }
            throw new IllegalArgumentException(
                "Expected 'CraftPersistentDataContainer' got '" + handle.getClass().getSimpleName() + " instead'!");
        }
        if (handle instanceof IDataContainer) {
            if (handle instanceof NbtContainer) {
                return (CompoundTag) VersionControl.get().getBukkitConversion().toMinecraftCompound(((NbtContainer) handle).asNbt());
            }
            throw new IllegalArgumentException(
                "Expected 'CraftPersistentDataContainer' got '" + handle.getClass().getSimpleName() + " instead'!");
        }
        throw new IllegalArgumentException("Unknown WrappedContainer implementation!");
    }

    private BukkitContainerImpl fromPrimitiveSyntax(CompoundTag data) {
        VersionControl control = VersionControl.get();
        NbtContainer container = new NbtContainer(control.getDataProvider().getRegistry());
        NbtCompound compound = control.getBukkitConversion().fromMinecraftCompound(data);
        container.fromNbt(compound);
        return new BukkitContainerImpl(container);
    }

    private SyntaxContainerImpl fromPrimitiveBukkit(CraftPersistentDataTypeRegistry registry, CompoundTag data) {
        CraftPersistentDataContainer container = new CraftPersistentDataContainer(registry);
        container.putAll(data);
        return new SyntaxContainerImpl(container);
    }

    private WrappedContainer findFinalContainer(WrappedContainer container) {
        WrappedContainer output = container;
        while (output.getHandle() instanceof WrappedContainer) {
            output = (WrappedContainer) output.getHandle();
        }
        return output;
    }

    public static void unhookAll() {
        HOOK.uninjectAll();
    }

    public static void hookEntity() {
        HOOK.inject(HOOK.getEntityRegistry());
    }

    public static void hook(CraftPersistentDataTypeRegistry registry) {
        HOOK.inject(registry);
    }

}
