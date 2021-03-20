package org.playuniverse.minecraft.mcs.spigot.language;

import org.playuniverse.minecraft.mcs.spigot.registry.ITyped;

public interface IMessageHandler<T> extends ITyped<T> {
    
    @SuppressWarnings("unchecked")
    default void handleObject(Object receiver, MessageType type, IMessage<?> message) {
        if(!getType().isInstance(receiver)) {
            return;
        }
        handle((T) receiver, type, message);
    }

    void handle(T receiver, MessageType type, IMessage<?> message);
    
    Language getLanguage(T receiver);
    
    void setLanguage(T receiver, Language language);
    
    void setLanguageIfNotExists(T receiver, Language language);

}
