package com.glxcian.shf.item;

import java.util.IdentityHashMap;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

/**
 * Hypixel-style rarity metadata. Explicit registrations support all nine
 * SkyBlock tiers; vanilla items fall back to their base RARITY component.
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

	/** Returns explicit SkyBlock rarity, or derives one from a vanilla item's base rarity. */
	public static SkyBlockRarity get(ItemStack stack) {
		if (CustomItems.isPromisingPickaxe(stack)) {
			return UNCOMMON;
		}
		SkyBlockRarity registered = ITEMS.get(stack.getItem());
		if (registered != null) {
			return registered;
		}
		Rarity vanilla = stack.getOrDefault(DataComponents.RARITY, Rarity.COMMON);
		return switch (vanilla) {
			case COMMON -> COMMON;
			case UNCOMMON -> UNCOMMON;
			case RARE -> RARE;
			case EPIC -> EPIC;
		};
	}
}
