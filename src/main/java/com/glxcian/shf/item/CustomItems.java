package com.glxcian.shf.item;

import com.glxcian.shf.SingleplayerHSFeatures;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ToolMaterial;

public final class CustomItems {
	public static final ResourceKey<Item> COMPACTOR_KEY = ResourceKey.create(
		Registries.ITEM,
		Identifier.fromNamespaceAndPath(SingleplayerHSFeatures.MOD_ID, "compactor")
	);
	public static final Item COMPACTOR = Registry.register(
		BuiltInRegistries.ITEM,
		COMPACTOR_KEY,
		new Item(
			new Item.Properties()
				.setId(COMPACTOR_KEY)
				.stacksTo(1)
				.rarity(Rarity.UNCOMMON)
				.component(DataComponents.ITEM_NAME, Component.literal("Compactor"))
		)
	);
	public static final ResourceKey<Item> PROMISING_PICKAXE_KEY = ResourceKey.create(
		Registries.ITEM,
		Identifier.fromNamespaceAndPath(SingleplayerHSFeatures.MOD_ID, "promising_pickaxe")
	);
	public static final Item PROMISING_PICKAXE = Registry.register(
		BuiltInRegistries.ITEM,
		PROMISING_PICKAXE_KEY,
		new Item(
			new Item.Properties()
				.setId(PROMISING_PICKAXE_KEY)
				.pickaxe(ToolMaterial.IRON, 1.0F, -2.8F)
				.rarity(Rarity.UNCOMMON)
				.component(DataComponents.ITEM_NAME, Component.literal("Promising Pickaxe"))
		)
	);
	private static final Map<String, Item> ENCHANTED_RESOURCES = new LinkedHashMap<>();

	private CustomItems() {
	}

	public static void initialize() {
		for (EnchantedResourceCatalog.Resource resource : EnchantedResourceCatalog.RESOURCES) {
			ResourceKey<Item> key = ResourceKey.create(
				Registries.ITEM,
				Identifier.fromNamespaceAndPath(SingleplayerHSFeatures.MOD_ID, resource.path())
			);
			Item.Properties properties = new Item.Properties()
				.setId(key)
				.rarity(Rarity.UNCOMMON)
				.component(DataComponents.ITEM_NAME, Component.literal(resource.name()));
			if (resource.path().equals("enchanted_iron_ingot")) {
				properties.component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
			}
			Item item = Registry.register(
				BuiltInRegistries.ITEM,
				key,
				new Item(properties)
			);
			ENCHANTED_RESOURCES.put(resource.path(), item);
		}
	}

	public static Item getEnchantedResource(String path) {
		Item item = ENCHANTED_RESOURCES.get(path);
		if (item == null) {
			throw new IllegalArgumentException("Unknown enchanted resource: " + path);
		}
		return item;
	}
}
