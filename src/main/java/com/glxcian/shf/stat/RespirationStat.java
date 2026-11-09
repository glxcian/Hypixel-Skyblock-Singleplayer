package com.glxcian.shf.stat;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public final class RespirationStat {
	private static final String TURTLE_HELMET_ID = "minecraft:turtle_helmet";
	private static final int TURTLE_HELMET_RESPIRATION = 30;

	private RespirationStat() {
	}

	public static int get(LivingEntity entity) {
		return getItem(entity.getItemBySlot(EquipmentSlot.HEAD));
	}

	public static int getItem(ItemStack stack) {
		String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
		return TURTLE_HELMET_ID.equals(itemId) ? TURTLE_HELMET_RESPIRATION : 0;
	}

	public static boolean shouldSkipAirDrain(LivingEntity entity) {
		int respiration = get(entity);
		if (respiration <= 0) {
			return false;
		}

		int ticksPerSkippedDrain = 1 + (int) Math.ceil(30.0 / respiration);
		return Math.floorMod(entity.tickCount, ticksPerSkippedDrain) == 0;
	}
}
