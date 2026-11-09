package com.glxcian.shf.stat;

import com.glxcian.shf.item.CustomItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public final class PromisingPickaxeProgress {
	private static final String BLOCKS_MINED_KEY = "singleplayerhsfeatures:promising_blocks_mined";
	private static final int BLOCKS_PER_BONUS = 100;
	private static final int SPEED_PER_BONUS = 10;
	private static final int MAX_BONUS = 250;

	private PromisingPickaxeProgress() {
	}

	public static long getBlocksMined(ItemStack stack) {
		if (!CustomItems.isPromisingPickaxe(stack)) {
			return 0L;
		}
		CustomData data = stack.get(DataComponents.CUSTOM_DATA);
		if (data == null) {
			return 0L;
		}
		long blocksMined = data.copyTag().getLongOr(BLOCKS_MINED_KEY, 0L);
		if (blocksMined < 0L) {
			throw new IllegalStateException("Promising Pickaxe block count cannot be negative.");
		}
		return blocksMined;
	}

	public static void setBlocksMined(ItemStack stack, long blocksMined) {
		if (!CustomItems.isPromisingPickaxe(stack)) {
			throw new IllegalArgumentException("Only a Promising Pickaxe can store mined block progress.");
		}
		if (blocksMined < 0L) {
			throw new IllegalArgumentException("Promising Pickaxe block count cannot be negative.");
		}
		CustomData.update(DataComponents.CUSTOM_DATA, stack,
			tag -> tag.putLong(BLOCKS_MINED_KEY, blocksMined));
	}

	public static long recordMinedBlock(ItemStack stack) {
		if (!CustomItems.isPromisingPickaxe(stack)) {
			throw new IllegalArgumentException("Only a Promising Pickaxe can record mined blocks.");
		}
		long blocksMined = Math.addExact(getBlocksMined(stack), 1L);
		CustomData.update(DataComponents.CUSTOM_DATA, stack,
			tag -> tag.putLong(BLOCKS_MINED_KEY, blocksMined));
		return blocksMined;
	}

	public static int getMiningSpeedBonus(ItemStack stack) {
		long earnedBonuses = getBlocksMined(stack) / BLOCKS_PER_BONUS;
		return (int) Math.min(MAX_BONUS, earnedBonuses * SPEED_PER_BONUS);
	}
}
