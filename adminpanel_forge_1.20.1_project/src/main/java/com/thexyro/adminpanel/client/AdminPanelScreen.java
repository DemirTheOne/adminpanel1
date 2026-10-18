package com.thexyro.adminpanel.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class AdminPanelScreen extends Screen {
    public AdminPanelScreen() {
        super(Component.literal("Admin Panel"));
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        // Yeni ozellikler buraya yeni dugme olarak eklenir.
        this.addRenderableWidget(Button.builder(Component.literal("SpinBot"),
                b -> this.minecraft.setScreen(new SpinBotScreen(this)))
                .bounds(cx - 100, 60, 200, 20).build());
        this.addRenderableWidget(Button.builder(Component.literal("Kapat"), b -> this.onClose())
                .bounds(cx - 100, this.height - 40, 200, 20).build());
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(g);
        g.drawCenteredString(this.font, this.title, this.width / 2, 25, 0xFFD700);
        g.drawCenteredString(this.font, "Ozellikler", this.width / 2, 45, 0xAAAAAA);
        if (ClientState.owner) {
            g.drawCenteredString(this.font, "/adminpanel give op <isim>   |   /adminpanel un op <isim>",
                    this.width / 2, this.height - 65, 0x888888);
        }
        super.render(g, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
