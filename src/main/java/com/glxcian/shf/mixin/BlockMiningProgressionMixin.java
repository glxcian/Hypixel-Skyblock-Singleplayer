package com.glxcian.shf.mixin;

import com.glxcian.shf.stat.PlayerProgression;
import com.glxcian.shf.stat.MiningFortuneStat;
import com.glxcian.shf.stat.EnchantmentStatEffects;
import com.glxcian.shf.stat.MiningExperience;
import com.glxcian.shf.stat.SkillExperience;
import java.util.List;
import java.util.ArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public abstract class BlockMiningProgressionMixin {
	@Inject(
		method = "getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemInstance;)Ljava/util/List;",
		at = @At("RETURN"),
		cancellable = true
	)
	private static void singleplayerhsfeatures$recordMinedResources(
		BlockState state,
		ServerLevel level,
		BlockPos position,
		BlockEntity blockEntity,
		Entity breaker,
		ItemInstance tool,
		CallbackInfoReturnable<List<ItemStack>> cir
	) {
		if (!(breaker instanceof ServerPlayer player) || player.getAbilities().instabuild) {
			return;
		}

		List<ItemStack> drops = new ArrayList<>(cir.getReturnValue());
		boolean farming = SkillExperience.farming(state) > 0.0;
		boolean miningFortuneAffected = !farming && MiningExperience.isFortuneAffected(state);
		ItemStack heldTool = player.getMainHandItem();
		boolean smeltingTouch = EnchantmentStatEffects.hasSmeltingTouch(heldTool);
		if (smeltingTouch) {
			smeltDrops(drops);
		}
		if (farming || miningFortuneAffected) {
			int fortune = farming
				? PlayerProgression.getFarmingFortune(player)
					+ EnchantmentStatEffects.getFortuneBonus(heldTool, true)
				: MiningFortuneStat.get(player);
			applyFortune(drops, fortune, level);
		}
		if (smeltingTouch || farming || miningFortuneAffected) {
			cir.setReturnValue(drops);
		}

		for (ItemStack drop : drops) {
			PlayerProgression.addCollectionItems(player, drop);
		}
	}

	private static void smeltDrops(List<ItemStack> drops) {
		for (int i = 0; i < drops.size(); i++) {
			ItemStack drop = drops.get(i);
			if (drop.isEmpty()) {
				continue;
			}
			String path = net.minecraft.core.registries.BuiltInRegistries.ITEM
				.getKey(drop.getItem()).getPath();
			net.minecraft.world.item.Item smeltedItem = switch (path) {
				case "cobblestone" -> net.minecraft.world.item.Items.STONE;
				case "stone" -> net.minecraft.world.item.Items.SMOOTH_STONE;
				case "sand", "red_sand" -> net.minecraft.world.item.Items.GLASS;
				case "raw_iron", "iron_ore", "deepslate_iron_ore" -> net.minecraft.world.item.Items.IRON_INGOT;
				case "raw_gold", "gold_ore", "deepslate_gold_ore" -> net.minecraft.world.item.Items.GOLD_INGOT;
				case "raw_copper", "copper_ore", "deepslate_copper_ore" -> net.minecraft.world.item.Items.COPPER_INGOT;
				case "ancient_debris" -> net.minecraft.world.item.Items.NETHERITE_SCRAP;
				case "clay_ball" -> net.minecraft.world.item.Items.BRICK;
				case "cactus" -> BuiltInRegistries.ITEM.get(Identifier.fromNamespaceAndPath("minecraft", "green_dye"));
				case "wet_sponge" -> net.minecraft.world.item.Items.SPONGE;
				case "kelp" -> net.minecraft.world.item.Items.DRIED_KELP;
				case "chorus_fruit" -> net.minecraft.world.item.Items.POPPED_CHORUS_FRUIT;
				case "netherrack" -> net.minecraft.world.item.Items.NETHER_BRICK;
				default -> path.endsWith("_log") || path.endsWith("_wood")
					|| path.endsWith("_stem") || path.endsWith("_hyphae")
					? net.minecraft.world.item.Items.CHARCOAL
					: null;
			};
			if (smeltedItem != null) {
				drops.set(i, new ItemStack(smeltedItem, drop.getCount()));
			}
		}
	}

	private static void applyFortune(List<ItemStack> drops, int fortune, ServerLevel level) {
		if (fortune <= 0) {
			return;
		}

		List<ItemStack> bonusDrops = new ArrayList<>();
		for (ItemStack drop : drops) {
			int bonusCount = drop.getCount() * fortune / 100;
			if (level.getRandom().nextInt(100) < drop.getCount() * fortune % 100) {
				bonusCount++;
			}
			while (bonusCount > 0) {
				ItemStack bonus = drop.copy();
				int count = Math.min(bonusCount, bonus.getMaxStackSize());
				bonus.setCount(count);
				bonusDrops.add(bonus);
				bonusCount -= count;
			}
		}
		drops.addAll(bonusDrops);
	}
}
