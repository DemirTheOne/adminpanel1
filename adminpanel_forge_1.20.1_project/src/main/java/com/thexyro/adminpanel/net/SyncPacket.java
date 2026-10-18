package com.thexyro.adminpanel.net;

import com.thexyro.adminpanel.client.ClientState;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

/** Sunucu -> istemci: yetki durumu, kendi spin hizin ve (yetkililer icin) spin yapanlarin listesi. */
public class SyncPacket {
    public final boolean permitted;
    public final boolean owner;
    public final int selfSpeed;
    public final Map<UUID, Integer> spinners;

    public SyncPacket(boolean permitted, boolean owner, int selfSpeed, Map<UUID, Integer> spinners) {
        this.permitted = permitted;
        this.owner = owner;
        this.selfSpeed = selfSpeed;
        this.spinners = spinners;
    }

    public static void encode(SyncPacket m, FriendlyByteBuf b) {
        b.writeBoolean(m.permitted);
        b.writeBoolean(m.owner);
        b.writeVarInt(m.selfSpeed);
        b.writeVarInt(m.spinners.size());
        for (Map.Entry<UUID, Integer> e : m.spinners.entrySet()) {
            b.writeUUID(e.getKey());
            b.writeVarInt(e.getValue());
        }
    }

    public static SyncPacket decode(FriendlyByteBuf b) {
        boolean permitted = b.readBoolean();
        boolean owner = b.readBoolean();
        int selfSpeed = b.readVarInt();
        int n = b.readVarInt();
        Map<UUID, Integer> map = new HashMap<>();
        for (int i = 0; i < n; i++) {
            UUID id = b.readUUID();
            map.put(id, b.readVarInt());
        }
        return new SyncPacket(permitted, owner, selfSpeed, map);
    }

    public static void handle(SyncPacket m, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientState.apply(m)));
        ctx.get().setPacketHandled(true);
    }
}
