package com.glxcian.shf.stat;

import net.minecraft.world.entity.player.Player;

public final class MiningFortuneStat {
	private MiningFortuneStat() {
	}

	public static int get(Player player) {
		return PlayerProgression.getMiningFortune(player)
			+ EnchantmentStatEffects.getFortuneBonus(player.getMainHandItem(), false);
	}
}
