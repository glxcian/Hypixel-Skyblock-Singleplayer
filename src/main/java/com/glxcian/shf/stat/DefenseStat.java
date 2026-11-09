package com.glxcian.shf.stat;

import java.util.Map;
import java.util.OptionalInt;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class DefenseStat {
	private static final Map<String, Integer> DEFENSE_BY_ITEM = new ConcurrentHashMap<>(Map.ofEntries(
		Map.entry("minecraft:leather_helmet", 5),
		Map.entry("minecraft:leather_chestplate", 15),
		Map.entry("minecraft:leather_leggings", 10),
		Map.entry("minecraft:leather_boots", 5),
		Map.entry("minecraft:golden_helmet", 10),
		Map.entry("minecraft:golden_chestplate", 25),
		Map.entry("minecraft:golden_leggings", 15),
		Map.entry("minecraft:golden_boots", 5),
		Map.entry("minecraft:chainmail_helmet", 20),
		Map.entry("minecraft:chainmail_chestplate", 30),
		Map.entry("minecraft:chainmail_leggings", 12),
		Map.entry("minecraft:chainmail_boots", 7),
		Map.entry("minecraft:iron_helmet", 12),
		Map.entry("minecraft:iron_chestplate", 30),
		Map.entry("minecraft:iron_leggings", 25),
		Map.entry("minecraft:iron_boots", 10),
		Map.entry("minecraft:diamond_helmet", 15),
		Map.entry("minecraft:diamond_chestplate", 40),
		Map.entry("minecraft:diamond_leggings", 30),
		Map.entry("minecraft:diamond_boots", 15),
		Map.entry("minecraft:netherite_helmet", 15),
		Map.entry("minecraft:netherite_chestplate", 40),
		Map.entry("minecraft:netherite_leggings", 30),
		Map.entry("minecraft:netherite_boots", 15),
		Map.entry("minecraft:turtle_helmet", 10)
	));

	private DefenseStat() {
	}

	public static void registerCustomDefense(String itemId, int defense) {
		if (defense < 0) {
			throw new IllegalArgumentException("Armor Defense must be non-negative.");
		}
		DEFENSE_BY_ITEM.put(itemId, defense);
	}

	public static OptionalInt getItemDefense(ItemStack stack) {
		Integer defense = DEFENSE_BY_ITEM.get(BuiltInRegistries.ITEM.getKey(stack.getItem()).toString());
		return defense == null ? OptionalInt.empty() : OptionalInt.of(defense);
	}

	public static boolean isEstimated(ItemStack stack) {
		String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
		return itemId.startsWith("minecraft:netherite_") || itemId.equals("minecraft:turtle_helmet");
	}

	public static int get(LivingEntity entity) {
		int defense = 0;
		for (EquipmentSlot slot : EquipmentSlot.values()) {
			if (slot.getType() != EquipmentSlot.Type.HUMANOID_ARMOR) {
				continue;
			}

			ItemStack stack = entity.getItemBySlot(slot);
			if (stack.isEmpty()) {
				continue;
			}

			String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
			Integer itemDefense = DEFENSE_BY_ITEM.get(itemId);
			if (itemDefense == null) {
				return 0;
			}
			defense += itemDefense;
			defense += EnchantmentStatEffects.getProtectionDefenseBonus(stack);
		}

		if (entity instanceof Player player) {
			defense += PlayerProgression.getMiningDefense(player);
		}
		return defense;
	}

	public static boolean canApplySkyBlockDefense(LivingEntity entity) {
		boolean hasArmor = entity instanceof Player player && PlayerProgression.getMiningDefense(player) > 0;
		for (EquipmentSlot slot : EquipmentSlot.values()) {
			if (slot.getType() != EquipmentSlot.Type.HUMANOID_ARMOR) {
				continue;
			}

			ItemStack stack = entity.getItemBySlot(slot);
			if (!stack.isEmpty()) {
				hasArmor = true;
				String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
				if (!DEFENSE_BY_ITEM.containsKey(itemId)) {
					return false;
				}
			}
		}

		return hasArmor;
	}

	public static float applyDamageReduction(float damage, int defense) {
		if (defense <= 0) {
			return damage;
		}

		return damage * 100.0F / (100.0F + defense);
	}
}
