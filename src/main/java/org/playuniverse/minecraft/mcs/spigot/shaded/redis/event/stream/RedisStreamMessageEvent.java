package org.playuniverse.minecraft.mcs.spigot.shaded.redis.event.stream;

import org.playuniverse.minecraft.mcs.spigot.shaded.redis.event.RedisEvent;
import org.playuniverse.minecraft.mcs.spigot.shaded.redis.model.RModel;
import org.playuniverse.minecraft.mcs.spigot.shaded.redis.model.io.RNamedModel;

public final class RedisStreamMessageEvent extends RedisEvent {

	private final String channel;
	private final String command;
	private final RModel data;

	public RedisStreamMessageEvent(String channel, RNamedModel message) {
		this.channel = channel;
		this.command = message.getName();
		this.data = message.getModel();
	}

	public final String getChannel() {
		return channel;
	}

	public final String getCommand() {
		return command;
	}

	public final RModel getData() {
		return data;
	}

}
