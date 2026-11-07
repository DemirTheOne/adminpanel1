package com.thexyro.screenmod.client;

import com.thexyro.screenmod.ScreenBlock;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.Minecraft;

public class ScreenModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ScreenBlock.OPEN_SCREEN = pos -> Minecraft.getInstance().setScreen(new ScreenMenuScreen(pos));
	}
}
