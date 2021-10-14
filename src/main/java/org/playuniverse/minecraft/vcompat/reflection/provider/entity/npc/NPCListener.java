package org.playuniverse.minecraft.vcompat.reflection.provider.entity.npc;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;

import org.playuniverse.minecraft.vcompat.reflection.entity.event.PlayerInteractAtNpcEvent;
import org.playuniverse.minecraft.vcompat.reflection.entity.event.PlayerInteractAtNpcEvent.Action;
import org.playuniverse.minecraft.vcompat.reflection.entity.event.PlayerInteractAtNpcEvent.Hand;
import org.playuniverse.minecraft.vcompat.reflection.provider.entity.PlayerImpl;
import org.playuniverse.minecraft.vcompat.reflection.provider.network.PacketListener;
import org.playuniverse.minecraft.vcompat.reflection.reflect.ClassLookupProvider;
import org.playuniverse.minecraft.vcompat.reflection.reflect.handle.ClassLookup;

import com.syntaxphoenix.syntaxapi.event.EventManager;
import com.syntaxphoenix.syntaxapi.utils.general.Status;
import com.syntaxphoenix.syntaxapi.utils.java.tools.Container;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;

public final class NPCListener extends PacketListener {

    private static class PacketHelper<E extends Packet<?>> {

        private final Class<E> packetType;
        private final PacketAcceptor<E> packetUser;

        private PacketHelper(Class<E> packetType, PacketAcceptor<E> packetUser) {
            this.packetType = packetType;
            this.packetUser = packetUser;
        }

        public boolean inject(ExecutorService service, PlayerImpl player, Packet<?> packet) {
            if (!packetType.isInstance(packet)) {
                return false;
            }
            return packetUser.accept(service, player, packetType.cast(packet));
        }

    }

    @FunctionalInterface
    private static interface PacketAcceptor<E extends Packet<?>> {

        boolean accept(ExecutorService service, PlayerImpl player, E packet);

    }

    private final HashMap<Class<?>, PacketHelper<?>> map = new HashMap<>();
    private final NPCImpl npc;

    private final Container<EventManager> eventManager;

    private final ClassLookup interactLookup = ClassLookupProvider.DEFAULT.getLookup("interactionPacket");

    public NPCListener(Container<EventManager> eventManager, NPCImpl npc) {
        this.eventManager = eventManager;
        this.npc = npc;
        loadPackets();
    }

    private void loadPackets() {
        packet(ServerboundInteractPacket.class, this::receiveInteract);
    }

    /*
     * Packet functions
     */

    private boolean receiveInteract(ExecutorService service, PlayerImpl player, ServerboundInteractPacket packet) {
        if (eventManager.isEmpty() || interactLookup == null) {
            return false;
        }
        Object value = interactLookup.getFieldValue(packet, "entityId");
        if (value == null || !(value instanceof Number)) {
            System.out.println("null");
            return false;
        }
        int id = ((Number) value).intValue();
        if (id != npc.getId()) {
            return false;
        }
        InteractionHelper interactionHelper = new InteractionHelper();
        packet.dispatch(interactionHelper);
        if (interactionHelper.getAction() == null) {
            return false;
        }
        PlayerInteractAtNpcEvent event = new PlayerInteractAtNpcEvent(player, npc, interactionHelper.getAction(),
            interactionHelper.getHand());
        Status status = eventManager.get().callAsync(event, service);
        while(!status.isDone()) {
            try {
                Thread.sleep(4);
            } catch (InterruptedException e) {
                break;
            }
        }
        return event.isCancelled();
    }

    /*
     * Packet Helper
     */

    class InteractionHelper implements ServerboundInteractPacket.Handler {

        private Action action;
        private Hand hand;

        @Override
        public void onAttack() {
            action = Action.LEFT_CLICK;
            hand = Hand.MAIN;
        }

        @Override
        public void onInteraction(InteractionHand hand) {
            action = Action.RIGHT_CLICK;
            this.hand = hand == InteractionHand.MAIN_HAND ? Hand.MAIN : Hand.SECOND;
        }

        @Override
        public void onInteraction(InteractionHand hand, Vec3 position) {} // Ignore because never thrown

        public Action getAction() {
            return action;
        }

        public Hand getHand() {
            return hand;
        }

    }

    /*
     * Packet Stuff
     */

    @Override
    protected boolean onPacket(ExecutorService service, PlayerImpl player, Packet<?> packet) {
        PacketHelper<?> helper = map.get(packet.getClass());
        return helper == null ? false : helper.inject(service, player, packet);
    }

    protected <T extends Packet<?>> void packet(Class<T> packetType, PacketAcceptor<T> packetUser) {
        packets.add(packetType);
        map.put(packetType, new PacketHelper<>(packetType, packetUser));
    }

}
