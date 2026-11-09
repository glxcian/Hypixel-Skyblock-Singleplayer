package com.glxcian.shf.stat;

import com.glxcian.shf.item.CustomItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.enchantment.Enchantments;

public final class PromisingPickaxeProgress {
	private static final String BLOCKS_MINED_KEY = "singleplayerhsfeatures:promising_blocks_mined";
	private static final int[] EFFICIENCY_THRESHOLDS = {50, 250, 750, 1_750, 3_750};

	private PromisingPickaxeProgress() {
	}

	public static long getBlocksMined(ItemStack stack) {
		if (!stack.is(CustomItems.PROMISING_PICKAXE)) {
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

	public static long recordMinedBlock(ServerPlayer player, ItemStack stack) {
		if (!stack.is(CustomItems.PROMISING_PICKAXE)) {
			throw new IllegalArgumentException("Only a Promising Pickaxe can record mined blocks.");
		}

		long blocksMined = Math.addExact(getBlocksMined(stack), 1L);
		CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putLong(BLOCKS_MINED_KEY, blocksMined));
		int efficiencyLevel = getEfficiencyLevel(blocksMined);
		if (efficiencyLevel > 0) {
			stack.enchant(
				player.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.EFFICIENCY),
				efficiencyLevel
			);
		}
		return blocksMined;
	}

	public static int getEfficiencyLevel(ItemStack stack) {
		return getEfficiencyLevel(getBlocksMined(stack));
	}

	private static int getEfficiencyLevel(long blocksMined) {
		int level = 0;
		for (int threshold : EFFICIENCY_THRESHOLDS) {
			if (blocksMined < threshold) {
				break;
			}
			level++;
		}
		return level;
	}

	public static int getNextEfficiencyThreshold(ItemStack stack) {
		int level = getEfficiencyLevel(stack);
		return level == EFFICIENCY_THRESHOLDS.length ? 0 : EFFICIENCY_THRESHOLDS[level];
	}

	public static int getEfficiencyThreshold(int level) {
		if (level < 1 || level > EFFICIENCY_THRESHOLDS.length) {
			throw new IllegalArgumentException("Promising Pickaxe Efficiency level must be between I and V.");
		}
		return EFFICIENCY_THRESHOLDS[level - 1];
	}
}
