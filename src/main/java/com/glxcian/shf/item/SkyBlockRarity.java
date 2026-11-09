package com.glxcian.shf.item;

import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

/**
 * Hypixel SkyBlock rarity tiers for vanilla items and custom-data item stacks.
 * Every registered vanilla item (including block items) receives COMMON by
 * default; sourced material-specific exceptions and custom item data override it.
 */
public enum SkyBlockRarity {
	COMMON("COMMON", ChatFormatting.GRAY),
	UNCOMMON("UNCOMMON", ChatFormatting.GREEN),
	RARE("RARE", ChatFormatting.BLUE),
	EPIC("EPIC", ChatFormatting.DARK_PURPLE),
	LEGENDARY("LEGENDARY", ChatFormatting.GOLD),
	MYTHIC("MYTHIC", ChatFormatting.LIGHT_PURPLE),
	DIVINE("DIVINE", ChatFormatting.AQUA),
	SPECIAL("SPECIAL", ChatFormatting.RED),
	VERY_SPECIAL("VERY SPECIAL", ChatFormatting.DARK_RED),
	ULTIMATE("ULTIMATE", ChatFormatting.DARK_PURPLE),
	ADMIN("ADMIN", ChatFormatting.DARK_RED);

	/*
	 * Vanilla equipment tiers are based on the Hypixel SkyBlock Wiki's
	 * vanilla pickaxe, sword, and armor tables. Other vanilla items and block
	 * items default to COMMON; custom variants resolve through their custom ID.
	 */
	private static final Map<String, SkyBlockRarity> VANILLA_RARITIES = Map.ofEntries(
		Map.entry("minecraft:diamond_pickaxe", UNCOMMON),
		Map.entry("minecraft:diamond_sword", UNCOMMON),
		Map.entry("minecraft:chainmail_helmet", UNCOMMON),
		Map.entry("minecraft:chainmail_chestplate", UNCOMMON),
		Map.entry("minecraft:chainmail_leggings", UNCOMMON),
		Map.entry("minecraft:chainmail_boots", UNCOMMON),
		Map.entry("minecraft:diamond_helmet", UNCOMMON),
		Map.entry("minecraft:diamond_chestplate", UNCOMMON),
		Map.entry("minecraft:diamond_leggings", UNCOMMON),
		Map.entry("minecraft:diamond_boots", UNCOMMON)
	);

	private final String displayName;
	private final ChatFormatting color;

	SkyBlockRarity(String displayName, ChatFormatting color) {
		this.displayName = displayName;
		this.color = color;
	}

	public String displayName() {
		return displayName;
	}

	public ChatFormatting color() {
		return color;
	}

	/** Resolves custom-data identity first, then the vanilla item's SkyBlock mapping. */
	public static SkyBlockRarity get(ItemStack stack) {
		String customId = CustomItems.getCustomItemId(stack);
		if (customId != null) {
			if (customId.equals("singleplayerhsfeatures:promising_pickaxe")) {
				return UNCOMMON;
			}
			if (customId.equals("singleplayerhsfeatures:personal_compactor")) {
				return RARE;
			}
			if (customId.startsWith("singleplayerhsfeatures:")) {
				EnchantedResourceCatalog.Resource resource =
					EnchantedResourceCatalog.getByPath(customId.substring("singleplayerhsfeatures:".length()));
				if (resource != null) {
					return resource.rarity();
				}
				return COMMON;
			}
		}
		return get(stack.getItem());
	}

	/** Every vanilla item and block item is covered by this method. */
	public static SkyBlockRarity get(Item item) {
		String itemId = BuiltInRegistries.ITEM.getKey(item).toString();
		return VANILLA_RARITIES.getOrDefault(itemId, COMMON);
	}

	/** Minecraft only has four native rarity values; SkyBlock color is rendered separately. */
	public Rarity vanillaRarity() {
		return switch (this) {
			case COMMON -> Rarity.COMMON;
			case UNCOMMON -> Rarity.UNCOMMON;
			case RARE -> Rarity.RARE;
			case EPIC, LEGENDARY, MYTHIC, DIVINE, SPECIAL, VERY_SPECIAL, ULTIMATE, ADMIN -> Rarity.EPIC;
		};
	}

	/** Hypixel places the item category after rarity for tools and weapons. */
	public static String getItemSuffix(ItemStack stack) {
		String path = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
		if (path.endsWith("_pickaxe")) return " PICKAXE";
		if (path.endsWith("_shovel")) return " SHOVEL";
		if (path.endsWith("_axe")) return " AXE";
		if (path.endsWith("_hoe")) return " HOE";
		if (path.endsWith("_sword")) return " SWORD";
		if (path.endsWith("_spear")) return " SPEAR";
		return switch (path) {
			case "bow" -> " BOW";
			case "crossbow" -> " CROSSBOW";
			case "trident" -> " TRIDENT";
			case "mace" -> " MACE";
			case "fishing_rod" -> " FISHING ROD";
			default -> "";
		};
	}
}
