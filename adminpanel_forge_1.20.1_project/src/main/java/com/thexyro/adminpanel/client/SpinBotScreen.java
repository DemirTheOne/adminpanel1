package com.thexyro.adminpanel.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.thexyro.adminpanel.Net;
import com.thexyro.adminpanel.net.SetSpinPacket;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public class SpinBotScreen extends Screen {
    private final Screen parent;
    private UUID selected;
    private int speed = 0;
    private KeyMapping waitingFor = null;

    public SpinBotScreen(Screen parent) {
        super(Component.literal("SpinBot"));
        this.parent = parent;
    }

    /** Sunucudan yeni durum gelince cagrilir. */
    public void refresh() {
        this.rebuildWidgets();
    }

    @Override
    protected void init() {
        Minecraft mc = this.minecraft;
        if (mc.player == null || mc.getConnection() == null) {
            return;
        }
        if (selected == null) {
            selected = mc.player.getUUID();
        }
        int spinning = ClientState.spinners.getOrDefault(selected, 0);
        if (spinning > 0) {
            speed = spinning;
        } else if (speed <= 0) {
            speed = ClientState.lastSpeed;
        }

        // Sol: sunucudaki oyuncular
        List<PlayerInfo> list = new ArrayList<>(mc.getConnection().getOnlinePlayers());
        list.sort(Comparator.comparing(i -> i.getProfile().getName().toLowerCase()));
        int y = 40;
        for (PlayerInfo info : list) {
            if (y > this.height - 60) break;
            final UUID id = info.getProfile().getId();
            int s = ClientState.spinners.getOrDefault(id, 0);
            String label = (id.equals(selected) ? "> " : "") + info.getProfile().getName()
                    + (s > 0 ? "  [ACIK " + s + "]" : "");
            this.addRenderableWidget(Button.builder(Component.literal(label), b -> {
                selected = id;
                this.rebuildWidgets();
            }).bounds(20, y, 170, 20).build());
            y += 22;
        }

        // Sag: secili oyuncunun ayarlari
        final int x = 210;
        final int w = 200;
        final boolean on = spinning > 0;
        this.addRenderableWidget(Button.builder(
                Component.literal(on ? "SpinBot: ACIK (kapat)" : "SpinBot: KAPALI (ac)"),
                b -> send(selected, on ? 0 : speed)).bounds(x, 60, w, 20).build());
        this.addRenderableWidget(new SpeedSlider(x, 90, w, 20, speed));
        this.addRenderableWidget(keyButton(x, 130, "Ac/Kapat tusu", Keys.TOGGLE));
        this.addRenderableWidget(keyButton(x, 155, "Panel tusu", Keys.OPEN));
        this.addRenderableWidget(Button.builder(Component.literal("Geri"), b -> this.onClose())
                .bounds(x, this.height - 40, w, 20).build());
    }

    private Button keyButton(int x, int y, String name, KeyMapping km) {
        Component label;
        if (waitingFor == km) {
            label = Component.literal(name + ": bir tusa bas... (ESC iptal, DEL sil)");
        } else {
            label = Component.literal(name + ": ").append(km.getTranslatedKeyMessage());
        }
        return Button.builder(label, b -> {
            waitingFor = km;
            this.rebuildWidgets();
        }).bounds(x, y, 200, 20).build();
    }

    private void send(UUID target, int s) {
        Net.CHANNEL.sendToServer(new SetSpinPacket(target, s));
        if (s > 0) {
            ClientState.lastSpeed = s;
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (waitingFor != null) {
            if (keyCode != GLFW.GLFW_KEY_ESCAPE) {
                InputConstants.Key key = keyCode == GLFW.GLFW_KEY_DELETE
                        ? InputConstants.UNKNOWN
                        : InputConstants.Type.KEYSYM.getOrCreate(keyCode);
                this.minecraft.options.setKey(waitingFor, key);
                this.minecraft.options.save();
            }
            waitingFor = null;
            this.rebuildWidgets();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(g);
        g.drawCenteredString(this.font, this.title, this.width / 2, 15, 0xFFD700);
        String name = "?";
        if (this.minecraft.getConnection() != null && selected != null) {
            PlayerInfo info = this.minecraft.getConnection().getPlayerInfo(selected);
            if (info != null) {
                name = info.getProfile().getName();
            }
        }
        g.drawString(this.font, "Hedef: " + name, 210, 45, 0xFFFFFF);
        g.drawString(this.font, "Oyuncular", 20, 28, 0xAAAAAA);
        super.render(g, mouseX, mouseY, partialTick);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(parent);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    /** 1 - 100 arasi hiz cubugu. */
    private class SpeedSlider extends AbstractSliderButton {
        SpeedSlider(int x, int y, int w, int h, int initial) {
            super(x, y, w, h, Component.empty(), (Math.max(1, Math.min(100, initial)) - 1) / 99.0);
            this.updateMessage();
        }

        private int speedFromValue() {
            return 1 + (int) Math.round(this.value * 99.0);
        }

        @Override
        protected void updateMessage() {
            this.setMessage(Component.literal("Hiz: " + speedFromValue()));
        }

        @Override
        protected void applyValue() {
            speed = speedFromValue();
            ClientState.lastSpeed = speed;
        }

        @Override
        public void onRelease(double mouseX, double mouseY) {
            super.onRelease(mouseX, mouseY);
            // Hedef zaten donuyorsa yeni hizi hemen uygula.
            if (ClientState.spinners.getOrDefault(selected, 0) > 0) {
                send(selected, speed);
            }
        }
    }
}
