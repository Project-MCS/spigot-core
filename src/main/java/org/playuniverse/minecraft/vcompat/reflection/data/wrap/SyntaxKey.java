package org.playuniverse.minecraft.vcompat.reflection.data.wrap;

import static org.playuniverse.minecraft.vcompat.utils.constants.DefaultConstants.NAMESPACE_STRING;

import org.bukkit.plugin.Plugin;
import org.playuniverse.minecraft.vcompat.reflection.data.WrappedKey;

import com.syntaxphoenix.syntaxapi.utils.key.IKey;
import com.syntaxphoenix.syntaxapi.utils.key.Namespace;
import com.syntaxphoenix.syntaxapi.utils.key.NamespacedKey;

public final class SyntaxKey extends WrappedKey<NamespacedKey> {

    private final NamespacedKey key;

    public SyntaxKey(Plugin plugin, String key) {
        this.key = Namespace.of(plugin.getName().toLowerCase()).create(key);
    }

    public SyntaxKey(String name, String key) {
        this.key = Namespace.of(name).create(key);
    }

    public SyntaxKey(NamespacedKey key) {
        this.key = key;
    }

    public SyntaxKey(IKey key) {
        if (key instanceof NamespacedKey) {
            this.key = (NamespacedKey) key;
        } else {
            this.key = NamespacedKey.fromStringOrCompute(key.asString(), key0 -> Namespace.of(NAMESPACE_STRING).create(key0));
        }
    }

    @Override
    public NamespacedKey getHandle() {
        return key;
    }

    @Override
    public Namespace getNamespace() {
        return key.getNamespace();
    }

    @Override
    public NamespacedKey getNamespacedKey() {
        return key;
    }

    @Override
    public String getName() {
        return key.getNamespace().getName();
    }

    @Override
    public String getKey() {
        return key.getKey();
    }

    @Override
    public String toString() {
        return key.asString();
    }

}