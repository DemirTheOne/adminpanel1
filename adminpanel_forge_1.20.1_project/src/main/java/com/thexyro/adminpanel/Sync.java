package com.thexyro.adminpanel;

import com.thexyro.adminpanel.net.SyncPacket;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class Sync {
    public static void one(ServerPlayer p) {
        boolean permitted = Perms.isPermitted(p);
        Map<UUID, Integer> map = permitted ? SpinManager.snapshot() : new HashMap<>();
        Net.toPlayer(p, new SyncPacket(permitted, Perms.isOwner(p), SpinManager.speedOf(p.getUUID()), map));
    }

    public static void all(MinecraftServer server) {
        for (ServerPlayer p : server.getPlayerList().getPlayers()) {
            one(p);
        }
    }
}
