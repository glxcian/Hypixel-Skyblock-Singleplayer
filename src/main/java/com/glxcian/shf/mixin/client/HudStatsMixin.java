package com.glxcian.shf.mixin.client;

import com.glxcian.shf.stat.DefenseStat;
import com.glxcian.shf.stat.HealthStat;
import com.glxcian.shf.stat.MiningSpeedStat;
import com.glxcian.shf.stat.PlayerProgression;
import com.glxcian.shf.stat.PlayerProgression.Skill;
import com.glxcian.shf.stat.RespirationStat;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Hud.class)
public abstract class HudStatsMixin {
	@Inject(
		method = "extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/DeltaTracker;)V",
		at = @At("TAIL")
	)
	private void singleplayerhsfeatures$renderStats(
		GuiGraphicsExtractor graphics,
		net.minecraft.client.DeltaTracker deltaTracker,
		CallbackInfo ci
	) {
		Minecraft minecraft = Minecraft.getInstance();
		LocalPlayer player = minecraft.player;
		if (player == null) {
			return;
		}

		String health = String.format(
			Locale.ROOT,
			"Health: %.0f/%.0f",
			HealthStat.getCurrent(player),
			HealthStat.getMaximum(player)
		);
		String defense = "Defense: " + DefenseStat.get(player);
		String miningSpeed = "Mining Speed: " + MiningSpeedStat.get(player);
		String respiration = "Respiration: " + RespirationStat.get(player);
		String combat = String.format(
			Locale.ROOT,
			"Combat %d (%,.0f XP to next)",
			PlayerProgression.getLevel(player, Skill.COMBAT),
			PlayerProgression.getExperienceToNextLevel(player, Skill.COMBAT)
		);
		String mining = String.format(
			Locale.ROOT,
			"Mining %d (%,.0f XP to next)",
			PlayerProgression.getLevel(player, Skill.MINING),
			PlayerProgression.getExperienceToNextLevel(player, Skill.MINING)
		);
		graphics.text(minecraft.font, health, 6, 6, 0xFFFFFF, true);
		graphics.text(minecraft.font, defense, 6, 17, 0xFFFFFF, true);
		graphics.text(minecraft.font, miningSpeed, 6, 28, 0xFFFFFF, true);
		graphics.text(minecraft.font, respiration, 6, 39, 0xFFFFFF, true);
		graphics.text(minecraft.font, combat, 6, 50, 0xFFFFFF, true);
		graphics.text(minecraft.font, mining, 6, 61, 0xFFFFFF, true);

		Entity target = minecraft.crosshairPickEntity;
		if (target instanceof Mob mob) {
			String mobHealth = String.format(
				Locale.ROOT,
				"%s: %.0f/%.0f",
				mob.getDisplayName().getString(),
				HealthStat.getCurrent(mob),
				HealthStat.getMaximum(mob)
			);
			graphics.text(minecraft.font, mobHealth, 6, 72, 0xFFFFFF, true);
		}
	}
}
