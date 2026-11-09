package com.glxcian.shf.item;

import java.util.IdentityHashMap;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.Item;

/**
 * Hypixel-style rarity metadata for registered custom items.
 * This is deliberately separate from vanilla Rarity, which only has five tiers.
 */
public enum SkyBlockRarity {
	COMMON("COMMON", ChatFormatting.WHITE),
	UNCOMMON("UNCOMMON", ChatFormatting.GREEN),
	RARE("RARE", ChatFormatting.BLUE),
	EPIC("EPIC", ChatFormatting.DARK_PURPLE),
	LEGENDARY("LEGENDARY", ChatFormatting.GOLD),
	MYTHIC("MYTHIC", ChatFormatting.LIGHT_PURPLE),
	DIVINE("DIVINE", ChatFormatting.AQUA),
	SPECIAL("SPECIAL", ChatFormatting.RED),
	VERY_SPECIAL("VERY SPECIAL", ChatFormatting.DARK_RED);

	private static final Map<Item, SkyBlockRarity> ITEMS = new IdentityHashMap<>();

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

	public static void register(Item item, SkyBlockRarity rarity) {
		ITEMS.put(item, rarity);
	}

	public static SkyBlockRarity get(Item item) {
		return ITEMS.get(item);
	}
}
