package org.playuniverse.minecraft.vcompat.reflection.data.type;

import org.playuniverse.minecraft.vcompat.base.data.api.IDataAdapterContext;
import org.playuniverse.minecraft.vcompat.base.data.api.IDataContainer;
import org.playuniverse.minecraft.vcompat.base.data.api.IDataType;
import org.playuniverse.minecraft.vcompat.reflection.VersionControl;
import org.playuniverse.minecraft.vcompat.reflection.data.WrapType;
import org.playuniverse.minecraft.vcompat.utils.minecraft.Skin;

public final class SkinDataType implements IDataType<IDataContainer, Skin> {

    public static final IDataType<IDataContainer, Skin> INSTANCE = new SkinDataType();
    public static final WrapType<?, Skin> WRAPPED_INSTANCE = VersionControl.get().getBukkitConversion().wrap(INSTANCE);

    private SkinDataType() {}

    @Override
    public Class<Skin> getComplex() {
        return Skin.class;
    }

    @Override
    public Class<IDataContainer> getPrimitive() {
        return IDataContainer.class;
    }

    @Override
    public IDataContainer toPrimitive(IDataAdapterContext context, Skin complex) {
        IDataContainer container = context.newContainer();
        container.set("name", complex.getName(), IDataType.STRING);
        container.set("value", complex.getValue(), IDataType.STRING);
        container.set("signature", complex.getSignature(), IDataType.STRING);
        return container;
    }

    @Override
    public Skin fromPrimitive(IDataAdapterContext context, IDataContainer container) {
        System.out.println(container.getClass());
        String name = container.get("name", IDataType.STRING);
        String value = container.get("value", IDataType.STRING);
        String signature = container.get("signature", IDataType.STRING);
        System.out.println(name);
        System.out.println(value);
        System.out.println(signature);
        if(name == null || value == null || signature == null) {
            return null;
        }
        return new Skin(name, value, signature, false);
    }

}