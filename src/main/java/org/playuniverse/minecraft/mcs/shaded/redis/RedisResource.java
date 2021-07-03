package org.playuniverse.minecraft.mcs.shaded.redis;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.playuniverse.minecraft.mcs.shaded.redis.model.RModel;
import org.playuniverse.minecraft.mcs.shaded.redis.model.RType;
import org.playuniverse.minecraft.mcs.shaded.redis.model.data.RedisAdapterRegistry;
import org.playuniverse.minecraft.mcs.shaded.redis.model.io.RIOModel;
import org.playuniverse.minecraft.mcs.shaded.redis.model.io.RNamedModel;
import org.playuniverse.minecraft.mcs.shaded.redis.utils.DataSerialization;
import org.playuniverse.minecraft.mcs.shaded.redis.utils.Tools;

import redis.clients.jedis.Jedis;

public class RedisResource implements Closeable {

	private final Jedis jedis;
	private final byte[] base;

	public RedisResource(Jedis jedis, String section) {
		this.jedis = jedis;
		this.base = Tools.generateKey(section);
	}

	public boolean set(String key, RModel model) {
		if (Objects.isNull(model)) {
			return false;
		}
		return set0(key, model);
	}

	public boolean set(String key, Object object) {
		Objects.requireNonNull(object);
		RModel model = RedisAdapterRegistry.GLOBAL.wrap(object);
		if (model == null) {
			return false;
		}
		return set0(key, model);
	}

	private boolean set0(String key, RModel model) {
		byte[] data = RIOModel.MODEL.write(new RNamedModel("root", model));
		if (data == null) {
			return false;
		}
		jedis.hset(base, Tools.generateKey(key), data);
		return true;
	}

	public Optional<RModel> get(String key) {
		return Optional.ofNullable(RIOModel.MODEL.read(jedis.hget(base, Tools.generateKey(key))).getModel());
	}

	public Optional<RModel> get(String key, RType type) {
		return get(key).filter(model -> model.getType() == type);
	}

	public boolean has(String key) {
		return jedis.hexists(base, Tools.generateKey(key));
	}

	public boolean has(String key, RType type) {
		return get(key).filter(model -> model.getType() == type).isPresent();
	}

	public Set<String> getKeys() {
		return jedis.hkeys(base).stream().map(DataSerialization::asString).collect(Collectors.toSet());
	}

	public List<RModel> getValues() {
		return jedis.hvals(base).stream().map(array -> RIOModel.MODEL.read(array).getModel()).filter(Objects::nonNull).collect(Collectors.toList());
	}

	public Map<String, RModel> getEntries() {
		return jedis.hgetAll(base).entrySet().stream()
			.map(entry -> Tools.entry(DataSerialization.asString(entry.getKey()), RIOModel.MODEL.read(entry.getValue()).getModel()))
			.filter(entry -> entry.getValue() != null).collect(Collectors.toMap(Entry::getKey, Entry::getValue));
	}

	@Override
	public void close() throws IOException {
		jedis.close();
	}

}
