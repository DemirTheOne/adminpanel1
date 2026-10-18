package com.thexyro.adminpanel;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;

/**
 * Sunucu tarafi spin: oyuncunun GERCEK donusu hic degismez (kendi kamerasi sabit kalir, nisan alip vurabilir).
 * Sadece DIGER oyunculara, o oyuncunun modeli hizla donuyormus gibi sahte rotasyon paketi yollanir.
 */
public class SpinManager {
    /** hiz 1..100 -> tick basina derece. 100 = 150 derece/tick. */
    public static final float DEG_PER_SPEED = 1.5F;

    private static final Map<UUID, Integer> SPEED = new HashMap<>();
    private static final Map<UUID, Float> ANGLE = new HashMap<>();

    public static Map<UUID, Integer> snapshot() {
        return new HashMap<>(SPEED);
    }

    public static int speedOf(UUID id) {
        return SPEED.getOrDefault(id, 0);
    }

    public static void set(MinecraftServer server, UUID id, int speed) {
        if (speed <= 0) {
            SPEED.remove(id);
            ANGLE.remove(id);
            ServerPlayer target = server.getPlayerList().getPlayer(id);
            if (target != null) {
                broadcast(server, target, target.getYRot()); // modeli gercek yonune geri dondur
            }
        } else {
            SPEED.put(id, speed);
            ANGLE.putIfAbsent(id, 0.0F);
        }
    }

    public static void remove(UUID id) {
        SPEED.remove(id);
        ANGLE.remove(id);
    }

    public static void tick(MinecraftServer server) {
        for (Map.Entry<UUID, Integer> e : SPEED.entrySet()) {
            ServerPlayer target = server.getPlayerList().getPlayer(e.getKey());
            if (target == null) continue;
            float a = ANGLE.getOrDefault(e.getKey(), 0.0F);
            a = Mth.wrapDegrees(a + e.getValue() * DEG_PER_SPEED);
            ANGLE.put(e.getKey(), a);
            broadcast(server, target, a);
        }
    }

    private static void broadcast(MinecraftServer server, ServerPlayer target, float yaw) {
        byte y = (byte) Mth.floor(yaw * 256.0F / 360.0F);
        byte x = (byte) Mth.floor(target.getXRot() * 256.0F / 360.0F);
        Packet<?> rot = new ClientboundMoveEntityPacket.Rot(target.getId(), y, x, target.onGround());
        Packet<?> head = new ClientboundRotateHeadPacket(target, y);
        for (ServerPlayer other : server.getPlayerList().getPlayers()) {
            if (other == target || other.level() != target.level()) continue;
            other.connection.send(rot);
            other.connection.send(head);
        }
    }
}
