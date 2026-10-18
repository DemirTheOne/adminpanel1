package com.thexyro.adminpanel.net;

import com.thexyro.adminpanel.Perms;
import com.thexyro.adminpanel.SpinManager;
import com.thexyro.adminpanel.Sync;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraftforge.network.NetworkEvent;

/** Istemci -> sunucu: "su oyuncuya su hizla spin ver" (hiz 0 = kapat). */
public class SetSpinPacket {
    public final UUID target;
    public final int speed;

    public SetSpinPacket(UUID target, int speed) {
        this.target = target;
        this.speed = speed;
    }

    public static void encode(SetSpinPacket m, FriendlyByteBuf b) {
        b.writeUUID(m.target);
        b.writeVarInt(m.speed);
    }

    public static SetSpinPacket decode(FriendlyByteBuf b) {
        UUID id = b.readUUID();
        return new SetSpinPacket(id, b.readVarInt());
    }

    public static void handle(SetSpinPacket m, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context c = ctx.get();
        c.enqueueWork(() -> {
            ServerPlayer sender = c.getSender();
            if (sender == null || !Perms.isPermitted(sender)) return;
            MinecraftServer server = sender.server;
            if (server.getPlayerList().getPlayer(m.target) == null) return;
            SpinManager.set(server, m.target, Mth.clamp(m.speed, 0, 100));
            Sync.all(server);
        });
        c.setPacketHandled(true);
    }
}
