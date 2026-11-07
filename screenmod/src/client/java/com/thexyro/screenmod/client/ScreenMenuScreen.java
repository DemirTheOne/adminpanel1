package com.thexyro.screenmod.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

/**
 * Ekrana sag tiklayinca acilan menu (Gallery / Settings / Import).
 * ASAMA 1: sadece iskelet. Dugmeler henuz bir sey yapmiyor.
 */
public class ScreenMenuScreen extends Screen {
	private enum Tab { GALLERY, SETTINGS, IMPORT }

	private final BlockPos pos;
	private Tab tab = Tab.GALLERY;
	private boolean keepAspect = true;

	public ScreenMenuScreen(BlockPos pos) {
		super(Component.literal("Screen"));
		this.pos = pos;
	}

	@Override
	protected void init() {
		int w = Math.min(320, this.width - 20);
		int left = (this.width - w) / 2;
		int third = w / 3;

		addTabButton(left, third, Tab.GALLERY, "Gallery");
		addTabButton(left + third, third, Tab.SETTINGS, "Settings");
		addTabButton(left + third * 2, third, Tab.IMPORT, "Import");

		switch (tab) {
			case GALLERY -> {
				// Resim listesi sonraki asamada gelecek.
			}
			case SETTINGS -> {
				EditBox idBox = new EditBox(this.font, left, 90, w - 70, 20, Component.literal("Screen ID"));
				idBox.setHint(Component.literal("Screen ID..."));
				this.addRenderableWidget(idBox);
				Button link = Button.builder(Component.literal("Link"), b -> { })
						.bounds(left + w - 65, 90, 65, 20).build();
				link.active = false;
				this.addRenderableWidget(link);
				this.addRenderableWidget(Button.builder(aspectLabel(), b -> {
					keepAspect = !keepAspect;
					b.setMessage(aspectLabel());
				}).bounds(left, 120, w, 20).build());
			}
			case IMPORT -> {
				Button upload = Button.builder(Component.literal("Upload from Computer"), b -> { })
						.bounds(left, 70, w, 20).build();
				upload.active = false;
				this.addRenderableWidget(upload);
				EditBox urlBox = new EditBox(this.font, left, 115, w - 70, 20, Component.literal("Image URL"));
				urlBox.setHint(Component.literal("Image URL..."));
				this.addRenderableWidget(urlBox);
				Button load = Button.builder(Component.literal("Load"), b -> { })
						.bounds(left + w - 65, 115, 65, 20).build();
				load.active = false;
				this.addRenderableWidget(load);
			}
		}

		this.addRenderableWidget(Button.builder(Component.literal("Close"), b -> this.onClose())
				.bounds(left, this.height - 30, w, 20).build());
	}

	private void addTabButton(int x, int width, Tab target, String label) {
		String text = (tab == target ? "> " : "") + label;
		this.addRenderableWidget(Button.builder(Component.literal(text), b -> {
			tab = target;
			this.rebuildWidgets();
		}).bounds(x, 40, width, 20).build());
	}

	private Component aspectLabel() {
		return Component.literal("Maintain Aspect Ratio: " + (keepAspect ? "ON" : "OFF"));
	}

	@Override
	public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
		super.render(g, mouseX, mouseY, partialTick);
		g.drawCenteredString(this.font, this.title, this.width / 2, 12, 0xFFFFFFFF);
		g.drawCenteredString(this.font, Component.literal("No image currently displayed"), this.width / 2, 25, 0xFFAAAAAA);
		int left = (this.width - Math.min(320, this.width - 20)) / 2;
		switch (tab) {
			case GALLERY -> g.drawCenteredString(this.font, Component.literal("No images uploaded"), this.width / 2, 90, 0xFFAAAAAA);
			case SETTINGS -> g.drawString(this.font, Component.literal("Screen ID:"), left, 78, 0xFFFFFFFF);
			case IMPORT -> g.drawString(this.font, Component.literal("From URL"), left, 103, 0xFFFFFFFF);
		}
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}
