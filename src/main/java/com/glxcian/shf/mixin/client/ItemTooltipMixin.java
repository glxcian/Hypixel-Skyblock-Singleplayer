package com.glxcian.shf.mixin.client;

import com.glxcian.shf.stat.DefenseStat;
import com.glxcian.shf.stat.EnchantmentStatEffects;
import com.glxcian.shf.stat.CollectionRegistry;
import com.glxcian.shf.stat.DamageStat;
import com.glxcian.shf.stat.MiningSpeedStat;
import com.glxcian.shf.stat.MiningSpeedConfig;
import com.glxcian.shf.stat.PlayerProgression;
import com.glxcian.shf.stat.PromisingPickaxeProgress;
import com.glxcian.shf.stat.RespirationStat;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemTooltipMixin {
	@Inject(method = "getHoverName()Lnet/minecraft/network/chat/Component;", at = @At("RETURN"), cancellable = true)
	private void singleplayerhsfeatures$promisingPickaxeHoverName(CallbackInfoReturnable<Component> cir) {
		ItemStack stack = (ItemStack) (Object) this;
		if (stack.is(com.glxcian.shf.item.CustomItems.PROMISING_PICKAXE)) {
			cir.setReturnValue(Component.literal("Promising Pickaxe").withStyle(ChatFormatting.GREEN));
		}
	}

	@Inject(
		method = "getTooltipLines(Lnet/minecraft/world/item/Item$TooltipContext;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/TooltipFlag;)Ljava/util/List;",
		at = @At("RETURN"),
		cancellable = true
	)
	private void singleplayerhsfeatures$addSkyBlockStats(
		Item.TooltipContext context,
		net.minecraft.world.entity.player.Player player,
		net.minecraft.world.item.TooltipFlag flag,
		CallbackInfoReturnable<List<Component>> cir
	) {
		ItemStack stack = (ItemStack) (Object) this;
		List<Component> lines = new ArrayList<>(cir.getReturnValue());
		List<Component> stats = new ArrayList<>();
		List<EnchantmentStatEffects.TooltipEnchantment> enchantments =
			EnchantmentStatEffects.getTooltipEnchantments(stack);
		List<String> visibleEnchantmentNames = lines.stream().map(Component::getString).toList();
		java.util.Set<String> enchantmentNames = enchantments.stream()
			.map(enchantment -> enchantment.name().getString())
			.collect(java.util.stream.Collectors.toSet());
		lines.removeIf(line -> enchantmentNames.contains(line.getString()));
		boolean promisingPickaxe = stack.is(com.glxcian.shf.item.CustomItems.PROMISING_PICKAXE);
		if (promisingPickaxe) {
			Component name = Component.literal("Promising Pickaxe").withStyle(ChatFormatting.GREEN);
			if (lines.isEmpty()) {
				lines.add(name);
			} else {
				lines.set(0, name);
			}
		} else if (!lines.isEmpty()
			&& com.glxcian.shf.item.EnchantedResourceCatalog.getByPath(
				net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath()
			) instanceof com.glxcian.shf.item.EnchantedResourceCatalog.Resource resource) {
			lines.set(0, Component.literal(resource.name()).withStyle(ChatFormatting.YELLOW));
		}
		int statsInsertionIndex = Math.min(1, lines.size());
		int breakingPower = MiningSpeedStat.getBreakingPower(stack);
		if (breakingPower > 0) {
			lines.add(statsInsertionIndex, Component.literal("Breaking Power: " + breakingPower)
				.withStyle(ChatFormatting.GRAY));
			statsInsertionIndex++;
			if (promisingPickaxe) {
				lines.add(statsInsertionIndex++, Component.empty());
			}
		}

		DamageStat.getItemDamage(stack).ifPresent(damage -> {
			MutableComponent damageLine = statLabel("\u2694", ChatFormatting.RED, "Damage: ")
				.append(Component.literal("+" + damage).withStyle(ChatFormatting.RED));
			int enchantmentBonus = (int) Math.round(
				damage * EnchantmentStatEffects.getUniversalDamageBonus(stack) / 100.0
			);
			if (enchantmentBonus > 0) {
				damageLine.append(Component.literal(" (+" + enchantmentBonus + ")").withStyle(ChatFormatting.RED));
			}
			if (DamageStat.isEstimated(stack)) {
				damageLine.append(Component.literal(" (estimated)").withStyle(ChatFormatting.DARK_GRAY));
			}
			stats.add(damageLine);
		});
		boolean efficiencySpeedShown = false;
		for (EnchantmentStatEffects.TooltipEnchantment enchantment : enchantments) {
			if (visibleEnchantmentNames.contains(enchantment.name().getString())) {
				stats.add(Component.literal("")
					.append(enchantment.name().copy())
					.append(enchantment.effect())
					.withStyle(ChatFormatting.BLUE));
				if (enchantment.id().equals("efficiency")) {
					int efficiencySpeed = EnchantmentStatEffects.getMiningSpeedBonus(stack);
					if (efficiencySpeed > 0) {
						efficiencySpeedShown = true;
						stats.add(statLabel("\u26cf", ChatFormatting.GOLD, "Mining Speed: ")
							.append(Component.literal("+" + MiningSpeedStat.getItem(stack))
								.withStyle(ChatFormatting.GOLD)));
					}
				} else if (enchantment.id().equals("fortune")) {
					int miningFortune = EnchantmentStatEffects.getMiningFortuneBonus(stack);
					int farmingFortune = EnchantmentStatEffects.getFarmingFortuneBonus(stack);
					if (miningFortune > 0) {
						stats.add(statLabel("\u26cf", ChatFormatting.GOLD, "Mining Fortune: ")
							.append(Component.literal("+" + miningFortune).withStyle(ChatFormatting.GOLD)));
					} else if (farmingFortune > 0) {
						stats.add(statLabel("\u2692", ChatFormatting.GOLD, "Farming Fortune: ")
							.append(Component.literal("+" + farmingFortune).withStyle(ChatFormatting.GOLD)));
					}
				}
			}
		}
		DefenseStat.getItemDefense(stack).ifPresent(defense -> {
			MutableComponent defenseLine = statLabel("\u26e8", ChatFormatting.GREEN, "Defense: ")
				.append(Component.literal("+" + defense).withStyle(ChatFormatting.GREEN));
			if (DefenseStat.isEstimated(stack)) {
				defenseLine.append(Component.literal(" (estimated)").withStyle(ChatFormatting.DARK_GRAY));
			}
			stats.add(defenseLine);
		});
		int miningSpeed = player == null
			? MiningSpeedStat.getItem(stack)
			: MiningSpeedStat.getItem(stack, player);
		if (miningSpeed > 0 && !efficiencySpeedShown) {
			MutableComponent miningSpeedLine = statLabel("\u26cf", ChatFormatting.GOLD, "Mining Speed: ")
				.append(Component.literal("+" + miningSpeed).withStyle(ChatFormatting.GOLD));
			if (MiningSpeedConfig.isEstimated(stack)) {
				miningSpeedLine.append(Component.literal(" (estimated)").withStyle(ChatFormatting.DARK_GRAY));
			}
			stats.add(miningSpeedLine);
		}
		if (stack.is(net.minecraft.world.item.Items.TURTLE_HELMET)) {
			stats.add(statLabel("\u224b", ChatFormatting.BLUE, "Respiration: ")
				.append(Component.literal("+" + RespirationStat.getItem(stack)).withStyle(ChatFormatting.BLUE)));
		}
		if (promisingPickaxe) {
			long blocksMined = PromisingPickaxeProgress.getBlocksMined(stack);
			int storedPotential = PromisingPickaxeProgress.getMiningSpeedBonus(stack);
			stats.add(Component.empty());
			stats.add(Component.literal("Ability: Stored Potential").withStyle(ChatFormatting.GOLD));
			stats.add(Component.literal("Grants +10 Mining Speed for every 100 blocks mined.")
				.withStyle(ChatFormatting.GRAY));
			stats.add(Component.literal("(Max +250 Mining Speed)").withStyle(ChatFormatting.DARK_GRAY));
			stats.add(Component.empty());
			stats.add(Component.literal("Blocks Mined: " + blocksMined).withStyle(ChatFormatting.GREEN));
			stats.add(Component.literal("Mining Speed: " + storedPotential).withStyle(ChatFormatting.GREEN));
			stats.add(Component.empty());
			stats.add(Component.literal("This item can be reforged!").withStyle(ChatFormatting.GRAY));
			stats.add(Component.literal("UNCOMMON PICKAXE").withStyle(ChatFormatting.GREEN));
		}
		if (CollectionRegistry.isCollectionItem(stack)) {
			stats.add(Component.literal("Collection Item").withStyle(ChatFormatting.DARK_GRAY));
		}
		if (player != null && CollectionRegistry.isCollectionItem(stack)) {
			String itemId = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
			long collectionCount = PlayerProgression.getCollectionCount(player, itemId);
			if (collectionCount > 0) {
				stats.add(Component.literal("Collection: " + collectionCount).withStyle(ChatFormatting.DARK_GREEN));
			}
		}
		lines.addAll(statsInsertionIndex, stats);
		cir.setReturnValue(lines);
	}

	private static MutableComponent statLabel(String icon, ChatFormatting iconColor, String label) {
		return Component.literal(icon + " ").withStyle(iconColor)
			.append(Component.literal(label).withStyle(ChatFormatting.GRAY));
	}
}
