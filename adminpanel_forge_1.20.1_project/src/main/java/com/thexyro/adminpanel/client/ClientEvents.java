package com.thexyro.adminpanel.client;

import com.thexyro.adminpanel.AdminPanelMod;
import com.thexyro.adminpanel.Net;
import com.thexyro.adminpanel.SpinManager;
import com.thexyro.adminpanel.net.SetSpinPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AdminPanelMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEvents {
    private static float savedBody;
    private static float savedBodyO;
    private static float savedHead;
    private static float savedHeadO;
    private static boolean overridden = false;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();

        ClientState.prevAngle = ClientState.angle;
        if (ClientState.selfSpeed > 0) {
            ClientState.angle = Mth.wrapDegrees(ClientState.angle + ClientState.selfSpeed * SpinManager.DEG_PER_SPEED);
        }

        if (mc.player == null) return;

        while (Keys.OPEN.consumeClick()) {
            if (mc.screen == null && ClientState.permitted) {
                mc.setScreen(new AdminPanelScreen());
            }
        }
        while (Keys.TOGGLE.consumeClick()) {
            if (mc.screen == null && ClientState.permitted) {
                int s = ClientState.selfSpeed > 0 ? 0 : ClientState.lastSpeed;
                Net.CHANNEL.sendToServer(new SetSpinPacket(mc.player.getUUID(), s));
            }
        }
    }

    /** Kendi modelini ucuncu sahis kamerada donuyor gosterir. Birinci sahis kamera (yRot) hic degismez. */
    @SubscribeEvent
    public static void onRenderPre(RenderPlayerEvent.Pre event) {
        Player p = event.getEntity();
        if (p != Minecraft.getInstance().player || ClientState.selfSpeed <= 0) return;
        savedBody = p.yBodyRot;
        savedBodyO = p.yBodyRotO;
        savedHead = p.yHeadRot;
        savedHeadO = p.yHeadRotO;
        p.yBodyRot = ClientState.angle;
        p.yBodyRotO = ClientState.prevAngle;
        p.yHeadRot = ClientState.angle;
        p.yHeadRotO = ClientState.prevAngle;
        overridden = true;
    }

    @SubscribeEvent
    public static void onRenderPost(RenderPlayerEvent.Post event) {
        if (!overridden) return;
        Player p = event.getEntity();
        p.yBodyRot = savedBody;
        p.yBodyRotO = savedBodyO;
        p.yHeadRot = savedHead;
        p.yHeadRotO = savedHeadO;
        overridden = false;
    }
}
