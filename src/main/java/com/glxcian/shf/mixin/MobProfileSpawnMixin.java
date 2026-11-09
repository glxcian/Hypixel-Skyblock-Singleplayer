package com.glxcian.shf.mixin;

import com.glxcian.shf.stat.MobProfileConfig;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public abstract class MobProfileSpawnMixin {
	@Inject(
		method = "finalizeSpawn",
		at = @At("TAIL")
	)
	private void singleplayerhsfeatures$applySpawnProfile(
		ServerLevelAccessor level,
		DifficultyInstance difficulty,
		@Coerce Object spawnType,
		SpawnGroupData spawnData,
		CallbackInfoReturnable<SpawnGroupData> cir
	) {
		if (spawnType instanceof Enum<?> reason
			&& (reason.name().equals("NATURAL") || reason.name().equals("CHUNK_GENERATION"))) {
			MobProfileConfig.applyTo((Mob) (Object) this);
		}
	}
}
