package com.glxcian.shf.item;

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
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PlayerHeadItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.Blocks;

public final class CustomItems {
	private static final String PROMISING_PICKAXE_MARKER = "singleplayerhsfeatures:promising_pickaxe";
	public static final ResourceKey<Item> COMPACTOR_KEY = ResourceKey.create(
		Registries.ITEM,
		Identifier.fromNamespaceAndPath("singleplayerhsfeatures", "compactor")
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

	/** Mining collection heads. The profile names are temporary visual placeholders. */
	public static Item GLACITE;
	public static Item TUNGSTEN;
	public static Item UMBER;

	/** Compatibility alias. Promising Pickaxes are marked vanilla iron pickaxe stacks. */
	public static final Item PROMISING_PICKAXE = Items.IRON_PICKAXE;
	private static final Map<String, Item> ENCHANTED_RESOURCES = new LinkedHashMap<>();

	private CustomItems() {
	}

	public static ItemStack createPromisingPickaxe() {
		ItemStack stack = new ItemStack(Items.IRON_PICKAXE);
		CustomData.update(DataComponents.CUSTOM_DATA, stack,
			tag -> tag.putBoolean(PROMISING_PICKAXE_MARKER, true));
		stack.set(DataComponents.ITEM_NAME, Component.literal("Promising Pickaxe"));
		return stack;
	}

	public static boolean isPromisingPickaxe(ItemStack stack) {
		if (!stack.is(Items.IRON_PICKAXE)) {
			return false;
		}
		CustomData data = stack.get(DataComponents.CUSTOM_DATA);
		return data != null && data.copyTag().getBoolean(PROMISING_PICKAXE_MARKER).orElse(false);
	}

	public static void initialize() {
		registerMiningHead("glacite", "Glacite", "MHF_Stone");
		registerMiningHead("tungsten", "Tungsten", "MHF_IronGolem");
		registerMiningHead("umber", "Umber", "MHF_Creeper");

		for (EnchantedResourceCatalog.Resource resource : EnchantedResourceCatalog.RESOURCES) {
			ResourceKey<Item> key = ResourceKey.create(
				Registries.ITEM,
				Identifier.fromNamespaceAndPath("singleplayerhsfeatures", resource.path())
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
			SkyBlockRarity.register(item, SkyBlockRarity.UNCOMMON);
		}
	}

	private static void registerMiningHead(String path, String name, String placeholderProfile) {
		ResourceKey<Item> key = ResourceKey.create(
			Registries.ITEM,
			Identifier.fromNamespaceAndPath("singleplayerhsfeatures", path)
		);
		Item item = Registry.register(
			BuiltInRegistries.ITEM,
			key,
			new PlayerHeadItem(
				Blocks.PLAYER_HEAD,
				Blocks.PLAYER_WALL_HEAD,
				new Item.Properties()
					.setId(key)
					.rarity(Rarity.COMMON)
					.component(DataComponents.ITEM_NAME, Component.literal(name))
					.component(DataComponents.PROFILE, ResolvableProfile.createUnresolved(placeholderProfile))
			)
		);
		SkyBlockRarity.register(item, SkyBlockRarity.COMMON);
		switch (path) {
			case "glacite" -> GLACITE = item;
			case "tungsten" -> TUNGSTEN = item;
			case "umber" -> UMBER = item;
			default -> throw new IllegalArgumentException("Unknown mining collection head: " + path);
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
