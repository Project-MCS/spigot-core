package org.playuniverse.minecraft.mcs.shaded.redis.model.data;

import java.util.Set;

import org.playuniverse.minecraft.mcs.shaded.redis.model.RCompound;
import org.playuniverse.minecraft.mcs.shaded.redis.model.RModel;
import org.playuniverse.minecraft.vcompat.base.data.AbstractDataContainer;
import org.playuniverse.minecraft.vcompat.base.data.api.IDataAdapterContext;

public class RedisContainer extends AbstractDataContainer<RModel> implements IDataAdapterContext {
    
	private final RCompound root = new RCompound();

    public RedisContainer() {
        super(RedisAdapterRegistry.GLOBAL);
    }

	@Override
	public RedisContainer newContainer() {
		return new RedisContainer();
	}

	@Override
	public IDataAdapterContext getContext() {
		return this;
	}

	public RCompound getRoot() {
		return root;
	}

    @Override
    public boolean has(String key) {
        return root.has(key);
    }

    @Override
    public RModel getRaw(String key) {
        return root.get(key);
    }

    @Override
    public void set(String key, RModel value) {
        root.set(key, value);
    }

	@Override
	public boolean remove(String key) {
		return root.remove(key) != null;
	}

	@Override
	public Set<String> getKeyspaces() {
		return root.getKeys();
	}

	@Override
	public boolean isEmpty() {
		return root.isEmpty();
	}

	@Override
	public int size() {
		return root.size();
	}

}
