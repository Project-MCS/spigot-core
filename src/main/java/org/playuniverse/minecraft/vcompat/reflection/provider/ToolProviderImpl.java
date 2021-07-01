package org.playuniverse.minecraft.vcompat.reflection.provider;

import org.playuniverse.minecraft.vcompat.reflection.ToolProvider;
import org.playuniverse.minecraft.vcompat.reflection.provider.tools.BlockToolsImpl;
import org.playuniverse.minecraft.vcompat.reflection.provider.tools.ServerToolsImpl;
import org.playuniverse.minecraft.vcompat.reflection.provider.tools.SkinToolsImpl;

public class ToolProviderImpl extends ToolProvider<VersionControlImpl> {

    private final BlockToolsImpl blockTools = new BlockToolsImpl();
    private final SkinToolsImpl skinTools = new SkinToolsImpl();
    private final ServerToolsImpl serverTools = new ServerToolsImpl();

    protected ToolProviderImpl(VersionControlImpl versionControl) {
        super(versionControl);
    }

    @Override
    public SkinToolsImpl getSkinTools() {
        return skinTools;
    }

    @Override
    public ServerToolsImpl getServerTools() {
        return serverTools;
    }

    @Override
    public BlockToolsImpl getBlockTools() {
        return blockTools;
    }

}