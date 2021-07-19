package org.playuniverse.minecraft.mcs.spigot.constant;

import org.playuniverse.minecraft.mcs.spigot.language.IMessageHandler;
import org.playuniverse.minecraft.mcs.spigot.language.Language;
import org.playuniverse.minecraft.mcs.spigot.language.MessageBuilder;
import org.playuniverse.minecraft.mcs.spigot.language.placeholder.DefaultPlaceholderStore;
import org.playuniverse.minecraft.mcs.spigot.registry.OrderedTypedRegistry;
import org.playuniverse.minecraft.mcs.spigot.registry.Registry;
import org.playuniverse.minecraft.mcs.spigot.registry.TypedRegistry;
import org.playuniverse.minecraft.mcs.spigot.registry.UniqueRegistry;
import org.playuniverse.minecraft.mcs.spigot.utils.plugin.PluginSettings;

import com.syntaxphoenix.syntaxapi.utils.key.NamespacedKey;

public final class Singleton {

    private Singleton() {}

    /*
     * 
     */

    public static final class General {

        private General() {}

        /*
         * 
         */

        public static final PluginSettings SETTINGS = new PluginSettings();

        //

    }

    //

    public static final class Registries {

        private Registries() {}

        /*
         * 
         */

        public static final OrderedTypedRegistry<IMessageHandler<?>> MESSAGE_HANDLER = new OrderedTypedRegistry<>();
        public static final TypedRegistry<MessageBuilder<?>> MESSAGE_BUILDER = new TypedRegistry<>();

        public static final Registry<Language, Registry<NamespacedKey, Object>> TRANSLATIONS = new Registry<>();
        public static final Registry<NamespacedKey, Object> MESSAGES = new Registry<>();
        public static final UniqueRegistry<Language> LANGUAGES = new UniqueRegistry<>();
        
        public static final DefaultPlaceholderStore PLACEHOLDERS = new DefaultPlaceholderStore();

        //

        public static void flush() {
            MESSAGE_HANDLER.dispose();
            MESSAGE_BUILDER.dispose();
            LANGUAGES.dispose();
            MESSAGES.dispose();
            for (Registry<NamespacedKey, Object> registry : TRANSLATIONS.values()) {
                registry.dispose();
            }
            TRANSLATIONS.dispose();
        }

    }

}
