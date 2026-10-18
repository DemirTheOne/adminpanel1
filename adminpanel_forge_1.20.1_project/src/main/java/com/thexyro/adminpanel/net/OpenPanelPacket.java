package com.thexyro.adminpanel.net;

import com.thexyro.adminpanel.client.ClientState;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

/** Sunucu -> istemci: paneli ac (/adminpanel komutu bunu yollar). */
public class OpenPanelPacket {
    public static void encode(OpenPanelPacket m, FriendlyByteBuf b) {
    }

    public static OpenPanelPacket decode(FriendlyByteBuf b) {
        return new OpenPanelPacket();
    }

    public static void handle(OpenPanelPacket m, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientState.openPanel()));
        ctx.get().setPacketHandled(true);
    }
}
