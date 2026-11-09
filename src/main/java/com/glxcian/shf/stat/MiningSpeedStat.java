package com.glxcian.shf.stat;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class MiningSpeedStat {
	private MiningSpeedStat() {
	}

	public static int get(LivingEntity entity) {
		int miningSpeed = 0;
		for (EquipmentSlot slot : EquipmentSlot.values()) {
			if (slot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR) {
				miningSpeed += MiningSpeedConfig.get(entity.getItemBySlot(slot));
			}
		}
		if (entity instanceof Player player) {
			miningSpeed += getItem(player.getMainHandItem(), player);
		}
		return miningSpeed;
	}

	public static int getItem(ItemStack stack) {
		return MiningSpeedConfig.get(stack)
			+ EnchantmentStatEffects.getMiningSpeedBonus(stack)
			+ PromisingPickaxeProgress.getMiningSpeedBonus(stack);
	}

	public static int getItem(ItemStack stack, Player player) {
		return getItem(stack);
	}

	public static int getBreakingPower(ItemStack stack) {
		String path = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
		if (!path.endsWith("_pickaxe")) {
			return 0;
		}
		if (com.glxcian.shf.item.CustomItems.isPromisingPickaxe(stack)) {
			return 3;
		}
		return switch (path) {
			case "wooden_pickaxe", "stone_pickaxe", "golden_pickaxe" -> 1;
			case "iron_pickaxe" -> 2;
			case "diamond_pickaxe" -> 4;
			case "netherite_pickaxe" -> 5;
			default -> 1;
		};
	}

	public static float applyToBlockSpeed(Player player, float vanillaSpeed) {
		int miningSpeed = get(player);
		ItemStack heldItem = player.getMainHandItem();
		String heldItemPath = BuiltInRegistries.ITEM.getKey(heldItem.getItem()).getPath();
		boolean holdingPickaxe = heldItemPath.endsWith("_pickaxe");
		float multiplier = holdingPickaxe
			? miningSpeed / (float) MiningSpeedConfig.BASE_PICKAXE_MINING_SPEED
			: 1.0F + miningSpeed / (float) MiningSpeedConfig.BASE_PICKAXE_MINING_SPEED;
		return vanillaSpeed * Math.max(0.0F, multiplier);
	}
}
