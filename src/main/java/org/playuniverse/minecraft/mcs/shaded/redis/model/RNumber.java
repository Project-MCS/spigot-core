package org.playuniverse.minecraft.mcs.shaded.redis.model;

public abstract class RNumber extends RModel {
	
	@Override
	public abstract Number getValue();
	
	@Override
	public abstract RNumber clone();

}
