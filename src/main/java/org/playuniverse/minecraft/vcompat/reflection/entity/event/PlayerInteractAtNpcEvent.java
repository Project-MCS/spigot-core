package org.playuniverse.minecraft.vcompat.reflection.entity.event;

import org.playuniverse.minecraft.vcompat.reflection.entity.NmsNpc;
import org.playuniverse.minecraft.vcompat.reflection.entity.NmsPlayer;

import com.syntaxphoenix.syntaxapi.event.Cancelable;
import com.syntaxphoenix.syntaxapi.event.Event;

public final class PlayerInteractAtNpcEvent extends Event implements Cancelable {

    public static enum Action {
        LEFT_CLICK,
        RIGHT_CLICK;
    }

    public static enum Hand {
        MAIN,
        SECOND;
    }

    private final NmsPlayer player;
    private final NmsNpc npc;
    
    private final Action action;
    private final Hand hand;

    private boolean cancelled = false;

    public PlayerInteractAtNpcEvent(NmsPlayer player, NmsNpc npc, Action action, Hand hand) {
        this.player = player;
        this.npc = npc;
        this.action = action;
        this.hand = hand;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public NmsPlayer getPlayer() {
        return player;
    }

    public NmsNpc getNpc() {
        return npc;
    }

    public Action getAction() {
        return action;
    }
    
    public Hand getHand() {
        return hand;
    }

}
