package org.playuniverse.minecraft.vcompat.reflection.provider;

import org.playuniverse.minecraft.vcompat.reflection.VersionControl;
import org.playuniverse.minecraft.vcompat.reflection.provider.data.hook.BukkitContainerAdapterHookImpl;

public class VersionControlImpl extends VersionControl {

    public static VersionControlImpl INSTANCE;

    public static VersionControlImpl init() {
        return INSTANCE != null ? INSTANCE : (INSTANCE = new VersionControlImpl());
    }

    private final ToolProviderImpl toolProvider = new ToolProviderImpl(this);
    private final TextureProviderImpl textureProvider = new TextureProviderImpl(this);
    private final EntityProviderImpl entityProvider = new EntityProviderImpl(this);
    private final PlayerProviderImpl playerProvider = new PlayerProviderImpl(this);
    private final BukkitConversionImpl bukkitConversion = new BukkitConversionImpl(this);

    public VersionControlImpl() {
        BukkitContainerAdapterHookImpl.hookEntity();
    }

    @Override
    public ToolProviderImpl getToolProvider() {
        return toolProvider;
    }

    @Override
    public EntityProviderImpl getEntityProvider() {
        return entityProvider;
    }

    @Override
    public PlayerProviderImpl getPlayerProvider() {
        return playerProvider;
    }

    @Override
    public TextureProviderImpl getTextureProvider() {
        return textureProvider;
    }

    @Override
    public BukkitConversionImpl getBukkitConversion() {
        return bukkitConversion;
    }

    @Override
    public void shutdown() {
        dataProvider.getDefaultDistributor().shutdown();
        BukkitContainerAdapterHookImpl.unhookAll();
    }

}