package com.thexyro.adminpanel;

import com.thexyro.adminpanel.net.OpenPanelPacket;
import com.thexyro.adminpanel.net.SetSpinPacket;
import com.thexyro.adminpanel.net.SyncPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class Net {
    private static final String PROTOCOL = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(AdminPanelMod.MODID, "main"),
            () -> PROTOCOL,
            NetworkRegistry.acceptMissingOr(PROTOCOL),
            NetworkRegistry.acceptMissingOr(PROTOCOL));

    public static void register() {
        int id = 0;
        CHANNEL.registerMessage(id++, SyncPacket.class, SyncPacket::encode, SyncPacket::decode, SyncPacket::handle);
        CHANNEL.registerMessage(id++, SetSpinPacket.class, SetSpinPacket::encode, SetSpinPacket::decode, SetSpinPacket::handle);
        CHANNEL.registerMessage(id++, OpenPanelPacket.class, OpenPanelPacket::encode, OpenPanelPacket::decode, OpenPanelPacket::handle);
    }

    /** Modu olmayan (vanilla) baglantilara paket gondermez. */
    public static void toPlayer(ServerPlayer player, Object msg) {
        if (NetworkHooks.isVanillaConnection(player.connection.connection)) return;
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), msg);
    }
}
