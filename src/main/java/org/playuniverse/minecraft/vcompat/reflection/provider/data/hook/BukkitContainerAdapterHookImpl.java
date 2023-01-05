package org.playuniverse.minecraft.vcompat.reflection.provider.data.hook;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import org.bukkit.craftbukkit.v1_19_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_19_R1.persistence.CraftPersistentDataContainer;
import org.bukkit.craftbukkit.v1_19_R1.persistence.CraftPersistentDataTypeRegistry;
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

    private final ClassLookup registryRef = ClassLookup.of(CraftPersistentDataTypeRegistry.class)
        .searchMethod("create", "createAdapter", Class.class, Class.class, Function.class, Function.class)
        .searchField("adapters", "adapters", Map.class).searchField("function", "CREATE_ADAPTER", Function.class);
    private final ClassLookup entityRef = ClassLookup.of(CraftEntity.class).searchField("registry", "DATA_TYPE_REGISTRY",
        CraftPersistentDataTypeRegistry.class);

    private BukkitContainerAdapterHookImpl() {}

    private final ArrayList<CraftPersistentDataTypeRegistry> list = new ArrayList<>();

    private CraftPersistentDataTypeRegistry getEntityRegistry() {
        return (CraftPersistentDataTypeRegistry) entityRef.getFieldValue("registry");
    }

    private void uninjectAll() {
        for (CraftPersistentDataTypeRegistry registry : list) {
            Map adapters = (Map) registryRef.getFieldValue(registry, "adapters");
            adapters.remove(BukkitContainerImpl.class);
            adapters.remove(SyntaxContainerImpl.class);
        }
        list.clear();
    }

    private void inject(CraftPersistentDataTypeRegistry registry) {
        if (list.contains(registry) || registry == null) {
            return;
        }
        injectAdapters(registry, registryRef.getMethod("create").type().returnType());
        list.add(registry);
    }

    private <E> void injectAdapters(CraftPersistentDataTypeRegistry registry, Class<E> adapterType) {
        Map adapters = (Map) registryRef.getFieldValue(registry, "adapters");
        injectAdapter(adapters, registry, adapterType, BukkitContainerImpl.class);
        injectAdapter(adapters, registry, adapterType, SyntaxContainerImpl.class);
    }

    private <E> void injectAdapter(Map adapters, CraftPersistentDataTypeRegistry registry, Class<E> adapterType, Class type) {
        E adapter = createAdapter(registry, adapterType, type);
        if (adapter == null) {
            return;
        }
        adapters.put(type, adapter);
    }

    private <E> E createAdapter(CraftPersistentDataTypeRegistry registry, Class<E> adapterType, Class type) {
        if (Objects.equals(BukkitContainerImpl.class, type)) {
            return (E) buildAdapter(registry, BukkitContainerImpl.class, tag -> fromPrimitiveSyntax(tag));
        }
        if (Objects.equals(SyntaxContainerImpl.class, type)) {
            return (E) buildAdapter(registry, SyntaxContainerImpl.class, tag -> fromPrimitiveBukkit(registry, tag));
        }
        return null;
    }

    private <C extends WrappedContainer> Object buildAdapter(Object handle, Class<C> type, Function<CompoundTag, C> function) {
        return registryRef.run(handle, "create", type, CompoundTag.class, (Function<C, CompoundTag>) input -> toPrimitive(input), function);
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
