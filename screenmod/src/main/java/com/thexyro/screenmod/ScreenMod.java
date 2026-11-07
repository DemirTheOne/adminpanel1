package com.thexyro.screenmod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ScreenMod implements ModInitializer {
	public static final String MOD_ID = "screenmod";

	public static Block SCREEN_BLOCK;
	public static Item SCREEN_ITEM;

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}

	@Override
	public void onInitialize() {
		// Blok
		ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, id("screen"));
		SCREEN_BLOCK = Registry.register(BuiltInRegistries.BLOCK, blockKey,
				new ScreenBlock(BlockBehaviour.Properties.of()
						.strength(1.5F, 6.0F)
						.sound(SoundType.METAL)
						.setId(blockKey)));

		// Esya (tarif yok: craft edilemez, sadece creative'den ya da /give ile alinir)
		ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, id("screen"));
		SCREEN_ITEM = Registry.register(BuiltInRegistries.ITEM, itemKey,
				new BlockItem(SCREEN_BLOCK, new Item.Properties().setId(itemKey).useBlockDescriptionPrefix()));

		// Creative envanteri ("Functional Blocks" sekmesi)
		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS)
				.register(entries -> entries.accept(SCREEN_ITEM));
	}
}
