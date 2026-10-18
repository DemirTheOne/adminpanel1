package com.thexyro.adminpanel.client;

import com.thexyro.adminpanel.net.SyncPacket;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientState {
    public static boolean permitted = false;
    public static boolean owner = false;
    /** Sunucunun bu oyuncuya verdigi spin hizi (0 = kapali). Ucuncu sahis gorunumu icin kullanilir. */
    public static int selfSpeed = 0;
    /** Anahtar tusuyla acarken kullanilan son hiz. */
    public static int lastSpeed = 40;
    /** Spin yapan herkes (sadece yetkililere gonderilir). */
    public static Map<UUID, Integer> spinners = new HashMap<>();

    /** Ucuncu sahis gorunumunde kendi modelinin sahte yonu. */
    public static float angle = 0.0F;
    public static float prevAngle = 0.0F;

    public static void apply(SyncPacket m) {
        Minecraft mc = Minecraft.getInstance();
        int old = selfSpeed;
        permitted = m.permitted;
        owner = m.owner;
        selfSpeed = m.selfSpeed;
        spinners = m.spinners;
        if (old == 0 && selfSpeed > 0 && mc.player != null) {
            angle = mc.player.yBodyRot;
            prevAngle = angle;
        }
        Screen s = mc.screen;
        if (s instanceof SpinBotScreen sb) {
            if (permitted) {
                sb.refresh();
            } else {
                mc.setScreen(null);
            }
        } else if (s instanceof AdminPanelScreen && !permitted) {
            mc.setScreen(null);
        }
    }

    public static void openPanel() {
        if (permitted) {
            Minecraft.getInstance().setScreen(new AdminPanelScreen());
        }
    }
}
