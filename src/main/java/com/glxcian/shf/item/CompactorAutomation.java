package com.glxcian.shf.item;

import com.glxcian.shf.item.EnchantedResourceCatalog.Resource;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public final class CompactorAutomation {
	private static final int MAIN_INVENTORY_SIZE = 36;

	private CompactorAutomation() {
	}

	public static void tick(ServerPlayer player) {
		CustomItems.migrateLegacyItems(player);
		Inventory inventory = player.getInventory();
		if (!hasCompactor(inventory)) {
			return;
		}

		for (Resource resource : EnchantedResourceCatalog.RESOURCES) {
			compact(inventory, resource);
		}
	}

	private static boolean hasCompactor(Inventory inventory) {
		for (int slot = 0; slot < MAIN_INVENTORY_SIZE; slot++) {
			if (CustomItems.isPersonalCompactor(inventory.getItem(slot))) {
				return true;
			}
		}
		return false;
	}

	private static void compact(Inventory inventory, Resource resource) {
		int inputCount = countInput(inventory, resource.inputId());
		int craftCount = inputCount / resource.amount();
		if (craftCount <= 0) {
			return;
		}

		ItemStack output = CustomItems.createEnchantedResource(resource.path());
		int inputToConsume = craftCount * resource.amount();
		int freedInputSlots = countFreedInputSlots(inventory, resource.inputId(), inputToConsume);
		if (availableOutputCapacity(inventory, output, freedInputSlots) < craftCount) {
			return;
		}

		consumeInput(inventory, resource.inputId(), inputToConsume);
		insertOutput(inventory, output, craftCount);
	}

	private static int countInput(Inventory inventory, String itemId) {
		int count = 0;
		for (int slot = 0; slot < MAIN_INVENTORY_SIZE; slot++) {
			ItemStack stack = inventory.getItem(slot);
			if (matches(stack, itemId)) {
				count = Math.addExact(count, stack.getCount());
			}
		}
		return count;
	}

	private static int countFreedInputSlots(Inventory inventory, String itemId, int amount) {
		int freedSlots = 0;
		for (int slot = 0; slot < MAIN_INVENTORY_SIZE && amount > 0; slot++) {
			ItemStack stack = inventory.getItem(slot);
			if (!matches(stack, itemId)) {
				continue;
			}
			if (stack.getCount() <= amount) {
				amount -= stack.getCount();
				freedSlots++;
			} else {
				amount = 0;
			}
		}
		return freedSlots;
	}

	private static int availableOutputCapacity(Inventory inventory, ItemStack output, int freedInputSlots) {
		int capacity = 0;
		int emptySlots = 0;
		for (int slot = 0; slot < MAIN_INVENTORY_SIZE; slot++) {
			ItemStack stack = inventory.getItem(slot);
			if (stack.isEmpty()) {
				emptySlots++;
			} else if (ItemStack.isSameItemSameComponents(stack, output)) {
				capacity += stack.getMaxStackSize() - stack.getCount();
			}
		}
		return capacity + (emptySlots + freedInputSlots) * output.getMaxStackSize();
	}

	private static void consumeInput(Inventory inventory, String itemId, int amount) {
		for (int slot = 0; slot < MAIN_INVENTORY_SIZE && amount > 0; slot++) {
			ItemStack stack = inventory.getItem(slot);
			if (!matches(stack, itemId)) {
				continue;
			}
			int consumed = Math.min(amount, stack.getCount());
			stack.shrink(consumed);
			amount -= consumed;
			if (stack.isEmpty()) {
				inventory.setItem(slot, ItemStack.EMPTY);
			}
		}
		if (amount != 0) {
			throw new IllegalStateException("Compactor input changed while crafting " + itemId + ".");
		}
	}

	private static void insertOutput(Inventory inventory, ItemStack output, int count) {
		int remaining = count;
		for (int slot = 0; slot < MAIN_INVENTORY_SIZE && remaining > 0; slot++) {
			ItemStack stack = inventory.getItem(slot);
			if (!stack.isEmpty() && ItemStack.isSameItemSameComponents(stack, output)) {
				int moved = Math.min(remaining, stack.getMaxStackSize() - stack.getCount());
				if (moved > 0) {
					stack.grow(moved);
					remaining -= moved;
				}
			}
		}
		for (int slot = 0; slot < MAIN_INVENTORY_SIZE && remaining > 0; slot++) {
			if (inventory.getItem(slot).isEmpty()) {
				int moved = Math.min(remaining, output.getMaxStackSize());
				ItemStack inserted = output.copy();
				inserted.setCount(moved);
				inventory.setItem(slot, inserted);
				remaining -= moved;
			}
		}
		if (remaining > 0) {
			throw new IllegalStateException("Compactor output did not fit in the player's inventory.");
		}
	}

	private static boolean matches(ItemStack stack, String itemId) {
		if (stack.isEmpty()) {
			return false;
		}
		String customId = CustomItems.getCustomItemId(stack);
		if (itemId.startsWith("singleplayerhsfeatures:")) {
			return itemId.equals(customId);
		}
		return customId == null && BuiltInRegistries.ITEM.getKey(stack.getItem()).toString().equals(itemId);
	}
}
