package com.glxcian.shf.stat;

import java.util.OptionalInt;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.entity.player.Player;

public final class DamageStat {
	private static final double STAT_POINTS_PER_ATTACK_DAMAGE = 5.0;

	private DamageStat() {
	}

	public static OptionalInt getItemDamage(ItemStack stack) {
		if (com.glxcian.shf.item.CustomItems.isPromisingPickaxe(stack)) {
			return OptionalInt.of(20);
		}
		ItemAttributeModifiers modifiers = stack.get(DataComponents.ATTRIBUTE_MODIFIERS);
		if (modifiers == null) {
			if (stack.getItem() instanceof BowItem || stack.getItem() instanceof CrossbowItem) {
				return OptionalInt.of(30);
			}
			return OptionalInt.empty();
		}

		double attackDamage = modifiers.compute(Attributes.ATTACK_DAMAGE, 1.0, EquipmentSlot.MAINHAND);
		if (!Double.isFinite(attackDamage) || attackDamage <= 1.0) {
			if (stack.getItem() instanceof BowItem || stack.getItem() instanceof CrossbowItem) {
				return OptionalInt.of(30);
			}
			return OptionalInt.empty();
		}

		double pickaxeAdjustment = isPickaxe(stack) ? 5.0 : 0.0;
		return OptionalInt.of((int) Math.round(attackDamage * STAT_POINTS_PER_ATTACK_DAMAGE + pickaxeAdjustment));
	}

	public static boolean isEstimated(ItemStack stack) {
		if (com.glxcian.shf.item.CustomItems.isPromisingPickaxe(stack)) {
			return false;
		}
		String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
		String path = itemId.substring(itemId.indexOf(':') + 1);
		return stack.getItem() instanceof BowItem
			|| stack.getItem() instanceof CrossbowItem
			|| path.equals("mace")
			|| path.equals("trident")
			|| path.contains("spear")
			|| isPickaxe(stack) && !itemId.startsWith("minecraft:");
	}

	public static float getPlayerDamageMultiplier(ItemStack stack) {
		if (stack.getItem() instanceof BowItem || stack.getItem() instanceof CrossbowItem) {
			return 1.0F;
		}
		ItemAttributeModifiers modifiers = stack.get(DataComponents.ATTRIBUTE_MODIFIERS);
		double attackDamage = modifiers == null
			? 1.0
			: modifiers.compute(Attributes.ATTACK_DAMAGE, 1.0, EquipmentSlot.MAINHAND);
		if (!Double.isFinite(attackDamage) || attackDamage <= 0.0) {
			return (float) STAT_POINTS_PER_ATTACK_DAMAGE;
		}

		return (float) (getItemDamage(stack).orElse((int) Math.round(attackDamage * STAT_POINTS_PER_ATTACK_DAMAGE))
			/ attackDamage / STAT_POINTS_PER_ATTACK_DAMAGE);
	}

	public static float getPlayerDamageMultiplier(
		ItemStack stack,
		Player player,
		LivingEntity target,
		ServerLevel level,
		DamageSource source
	) {
		float multiplier = getPlayerDamageMultiplier(stack);
		if (target instanceof net.minecraft.world.entity.Mob) {
			multiplier *= 1.0F + PlayerProgression.getCombatDamageBonus(player) / 100.0F;
		}
		multiplier *= 1.0F + PlayerProgression.getSkyBlockStrength(player) / 100.0F;
		if (!(stack.getItem() instanceof BowItem) && !(stack.getItem() instanceof CrossbowItem)) {
			ItemAttributeModifiers modifiers = stack.get(DataComponents.ATTRIBUTE_MODIFIERS);
			double attackDamage = modifiers == null
				? 1.0
				: modifiers.compute(Attributes.ATTACK_DAMAGE, 1.0, EquipmentSlot.MAINHAND);
			if (attackDamage > 0.0) {
				float nativeDamage = EnchantmentHelper.modifyDamage(
					level, stack, target, source, (float) attackDamage
				);
				if (nativeDamage > 0.0F) {
					multiplier *= (float) attackDamage / nativeDamage;
				}
			}
			multiplier *= 1.0F + EnchantmentStatEffects.getSkyBlockDamagePercent(stack, target) / 100.0F;
		}
		return multiplier;
	}

	private static boolean isPickaxe(ItemStack stack) {
		String path = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
		return path.endsWith("_pickaxe");
	}
}
