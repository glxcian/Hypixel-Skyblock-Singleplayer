package com.glxcian.shf.mixin;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.glxcian.shf.mixin.EntityDataAccessMixin;
import com.glxcian.shf.stat.MobLevelAccess;
import com.glxcian.shf.stat.MobDamageAccess;

@Mixin(Mob.class)
public abstract class MobLevelMixin implements MobLevelAccess, MobDamageAccess {
	@Unique
	private static final EntityDataAccessor<Integer> singleplayerhsfeatures$MOB_LEVEL =
		SynchedEntityData.defineId(Mob.class, EntityDataSerializers.INT);

	@Unique
	private static final EntityDataAccessor<Float> singleplayerhsfeatures$LAST_DAMAGE =
		SynchedEntityData.defineId(Mob.class, EntityDataSerializers.FLOAT);

	@Unique
	private static final EntityDataAccessor<Integer> singleplayerhsfeatures$LAST_DAMAGE_TICK =
		SynchedEntityData.defineId(Mob.class, EntityDataSerializers.INT);

	@Inject(method = "defineSynchedData(Lnet/minecraft/network/syncher/SynchedEntityData$Builder;)V", at = @At("TAIL"))
	private void singleplayerhsfeatures$defineMobLevel(
		SynchedEntityData.Builder builder,
		CallbackInfo ci
	) {
		builder.define(singleplayerhsfeatures$MOB_LEVEL, 1);
		builder.define(singleplayerhsfeatures$LAST_DAMAGE, 0.0F);
		builder.define(singleplayerhsfeatures$LAST_DAMAGE_TICK, -1);
	}

	@Inject(method = "addAdditionalSaveData(Lnet/minecraft/world/level/storage/ValueOutput;)V", at = @At("TAIL"))
	private void singleplayerhsfeatures$saveMobLevel(ValueOutput output, CallbackInfo ci) {
		output.putInt("SingleplayerHSFeaturesMobLevel", this.singleplayerhsfeatures$getMobLevel());
	}

	@Inject(method = "readAdditionalSaveData(Lnet/minecraft/world/level/storage/ValueInput;)V", at = @At("TAIL"))
	private void singleplayerhsfeatures$loadMobLevel(ValueInput input, CallbackInfo ci) {
		int level = input.getIntOr("SingleplayerHSFeaturesMobLevel", 1);
		this.singleplayerhsfeatures$getEntityData().set(singleplayerhsfeatures$MOB_LEVEL, Math.max(1, level));
	}

	@Override
	public int singleplayerhsfeatures$getMobLevel() {
		return this.singleplayerhsfeatures$getEntityData().get(singleplayerhsfeatures$MOB_LEVEL);
	}

	@Override
	public void singleplayerhsfeatures$setMobLevel(int level) {
		this.singleplayerhsfeatures$getEntityData().set(singleplayerhsfeatures$MOB_LEVEL, level);
	}

	@Override
	public float singleplayerhsfeatures$getLastDamage() {
		return this.singleplayerhsfeatures$getEntityData().get(singleplayerhsfeatures$LAST_DAMAGE);
	}

	@Override
	public int singleplayerhsfeatures$getLastDamageTick() {
		return this.singleplayerhsfeatures$getEntityData().get(singleplayerhsfeatures$LAST_DAMAGE_TICK);
	}

	@Override
	public void singleplayerhsfeatures$setLastDamage(float damage, int tick) {
		this.singleplayerhsfeatures$getEntityData().set(singleplayerhsfeatures$LAST_DAMAGE, damage);
		this.singleplayerhsfeatures$getEntityData().set(singleplayerhsfeatures$LAST_DAMAGE_TICK, tick);
	}

	@Unique
	private SynchedEntityData singleplayerhsfeatures$getEntityData() {
		return ((EntityDataAccessMixin) (Object) this).singleplayerhsfeatures$getEntityData();
	}
}
