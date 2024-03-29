package org.playuniverse.minecraft.mcs.shaded.redis;

import java.io.Closeable;
import java.io.IOException;

import org.playuniverse.minecraft.mcs.shaded.redis.stream.RedisMessage;
import org.playuniverse.minecraft.mcs.shaded.redis.stream.RedisStream;

import com.syntaxphoenix.syntaxapi.event.EventManager;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisPool implements Closeable {

	private final JedisPool pool;

	public RedisPool(JedisPool pool) {
		this.pool = pool;
	}

	public JedisPool getHandle() {
		return pool;
	}

	public Jedis newJedis() {
		return pool.getResource();
	}

	public RedisResource newResource(String section) {
		return new RedisResource(pool.getResource(), section);
	}

	public RedisMessage newMessage() {
		return new RedisMessage(pool.getResource());
	}

	public RedisStream newStream(EventManager manager) {
		return new RedisStream(pool.getResource(), manager);
	}

	public boolean isClosed() {
		return pool.isClosed();
	}

	@Override
	public void close() throws IOException {
		pool.close();
	}

}
