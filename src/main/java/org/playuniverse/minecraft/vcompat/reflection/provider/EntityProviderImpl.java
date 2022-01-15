package org.playuniverse.minecraft.vcompat.reflection.provider;

import java.util.EnumMap;
import java.util.function.Function;

import org.bukkit.craftbukkit.v1_18_R1.CraftWorld;
import org.playuniverse.minecraft.vcompat.reflection.EntityProvider;
import org.playuniverse.minecraft.vcompat.reflection.entity.NmsEntity;
import org.playuniverse.minecraft.vcompat.reflection.entity.NmsEntityType;
import org.playuniverse.minecraft.vcompat.reflection.provider.utils.EntityConstructorsImpl;

import net.minecraft.world.level.Level;

public class EntityProviderImpl extends EntityProvider<VersionControlImpl> {

    private final EnumMap<NmsEntityType, Function<Level, NmsEntity>> entityMap = new EnumMap<>(NmsEntityType.class);

    protected EntityProviderImpl(VersionControlImpl versionControl) {
        super(versionControl);
    }

    @SuppressWarnings("unchecked")
    private final Function<Level, NmsEntity> searchConstructor(NmsEntityType type) {
        try {
            return (Function<Level, NmsEntity>) EntityConstructorsImpl.class.getField(type.name()).get(null);
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException ignore) {
            return null;
        }
    }

    private final Function<Level, NmsEntity> getConstructor(NmsEntityType type) {
        return entityMap.computeIfAbsent(type, (key -> searchConstructor(key)));
    }

    @Override
    public NmsEntity createEntity(org.bukkit.World world, NmsEntityType type) {
        if (!(world instanceof CraftWorld)) {
            return null;
        }
        Function<Level, NmsEntity> function;
        if ((function = getConstructor(type)) == null) {
            return null;
        }
        return function.apply(((CraftWorld) world).getHandle());
    }

}