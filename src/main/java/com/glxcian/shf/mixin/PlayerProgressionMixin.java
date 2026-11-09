package com.glxcian.shf.mixin;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.glxcian.shf.stat.PlayerProgressionAccess;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerProgressionMixin implements PlayerProgressionAccess {
	@Unique
	private static final long singleplayerhsfeatures$MAX_SKILL_XP = 11_167_242_500L;

	@Unique
	private static final EntityDataAccessor<Long> singleplayerhsfeatures$COMBAT_XP =
		SynchedEntityData.defineId(Player.class, EntityDataSerializers.LONG);

	@Unique
	private static final EntityDataAccessor<Long> singleplayerhsfeatures$MINING_XP =
		SynchedEntityData.defineId(Player.class, EntityDataSerializers.LONG);

	@Unique
	private static final EntityDataAccessor<Long> singleplayerhsfeatures$FARMING_XP =
		SynchedEntityData.defineId(Player.class, EntityDataSerializers.LONG);

	@Unique
	private static final EntityDataAccessor<Long> singleplayerhsfeatures$FORAGING_XP =
		SynchedEntityData.defineId(Player.class, EntityDataSerializers.LONG);

	@Unique
	private static final EntityDataAccessor<Long> singleplayerhsfeatures$FISHING_XP =
		SynchedEntityData.defineId(Player.class, EntityDataSerializers.LONG);

	@Unique
	private static final EntityDataAccessor<Long> singleplayerhsfeatures$ENCHANTING_XP =
		SynchedEntityData.defineId(Player.class, EntityDataSerializers.LONG);

	@Unique
	private static final EntityDataAccessor<Long> singleplayerhsfeatures$ALCHEMY_XP =
		SynchedEntityData.defineId(Player.class, EntityDataSerializers.LONG);

	@Unique
	private static final EntityDataAccessor<Long> singleplayerhsfeatures$TAMING_XP =
		SynchedEntityData.defineId(Player.class, EntityDataSerializers.LONG);

	@Unique
	private static final EntityDataAccessor<String> singleplayerhsfeatures$COLLECTIONS =
		SynchedEntityData.defineId(Player.class, EntityDataSerializers.STRING);

	@Inject(method = "defineSynchedData(Lnet/minecraft/network/syncher/SynchedEntityData$Builder;)V", at = @At("TAIL"))
	private void singleplayerhsfeatures$defineProgressionData(SynchedEntityData.Builder builder, CallbackInfo ci) {
		builder.define(singleplayerhsfeatures$COMBAT_XP, 0L);
		builder.define(singleplayerhsfeatures$MINING_XP, 0L);
		builder.define(singleplayerhsfeatures$FARMING_XP, 0L);
		builder.define(singleplayerhsfeatures$FORAGING_XP, 0L);
		builder.define(singleplayerhsfeatures$FISHING_XP, 0L);
		builder.define(singleplayerhsfeatures$ENCHANTING_XP, 0L);
		builder.define(singleplayerhsfeatures$ALCHEMY_XP, 0L);
		builder.define(singleplayerhsfeatures$TAMING_XP, 0L);
		builder.define(singleplayerhsfeatures$COLLECTIONS, "{}");
	}

	@Inject(method = "addAdditionalSaveData(Lnet/minecraft/world/level/storage/ValueOutput;)V", at = @At("TAIL"))
	private void singleplayerhsfeatures$saveProgression(ValueOutput output, CallbackInfo ci) {
		output.putLong("SingleplayerHSFeaturesCombatXP", this.singleplayerhsfeatures$getCombatExperience());
		output.putLong("SingleplayerHSFeaturesMiningXP", this.singleplayerhsfeatures$getMiningExperience());
		output.putLong("SingleplayerHSFeaturesFarmingXP", this.singleplayerhsfeatures$getFarmingExperience());
		output.putLong("SingleplayerHSFeaturesForagingXP", this.singleplayerhsfeatures$getForagingExperience());
		output.putLong("SingleplayerHSFeaturesFishingXP", this.singleplayerhsfeatures$getFishingExperience());
		output.putLong("SingleplayerHSFeaturesEnchantingXP", this.singleplayerhsfeatures$getEnchantingExperience());
		output.putLong("SingleplayerHSFeaturesAlchemyXP", this.singleplayerhsfeatures$getAlchemyExperience());
		output.putLong("SingleplayerHSFeaturesTamingXP", this.singleplayerhsfeatures$getTamingExperience());
		output.putString("SingleplayerHSFeaturesCollections", this.singleplayerhsfeatures$getCollectionsJson());
	}

	@Inject(method = "readAdditionalSaveData(Lnet/minecraft/world/level/storage/ValueInput;)V", at = @At("TAIL"))
	private void singleplayerhsfeatures$loadProgression(ValueInput input, CallbackInfo ci) {
		this.singleplayerhsfeatures$setCombatExperience(input.getLongOr("SingleplayerHSFeaturesCombatXP", 0L));
		this.singleplayerhsfeatures$setMiningExperience(input.getLongOr("SingleplayerHSFeaturesMiningXP", 0L));
		this.singleplayerhsfeatures$setFarmingExperience(input.getLongOr("SingleplayerHSFeaturesFarmingXP", 0L));
		this.singleplayerhsfeatures$setForagingExperience(input.getLongOr("SingleplayerHSFeaturesForagingXP", 0L));
		this.singleplayerhsfeatures$setFishingExperience(input.getLongOr("SingleplayerHSFeaturesFishingXP", 0L));
		this.singleplayerhsfeatures$setEnchantingExperience(input.getLongOr("SingleplayerHSFeaturesEnchantingXP", 0L));
		this.singleplayerhsfeatures$setAlchemyExperience(input.getLongOr("SingleplayerHSFeaturesAlchemyXP", 0L));
		this.singleplayerhsfeatures$setTamingExperience(input.getLongOr("SingleplayerHSFeaturesTamingXP", 0L));
		String collectionsJson = input.getStringOr("SingleplayerHSFeaturesCollections", "{}");
		JsonElement parsed = JsonParser.parseString(collectionsJson);
		if (!parsed.isJsonObject()) {
			throw new IllegalArgumentException("Saved collections data must be a JSON object.");
		}
		Map<String, Long> collections = new HashMap<>();
		for (Map.Entry<String, JsonElement> entry : parsed.getAsJsonObject().entrySet()) {
			if (!entry.getValue().isJsonPrimitive() || !entry.getValue().getAsJsonPrimitive().isNumber()) {
				throw new IllegalArgumentException("Saved collection count for " + entry.getKey() + " must be numeric.");
			}
			long count;
			try {
				count = entry.getValue().getAsBigDecimal().longValueExact();
			} catch (NumberFormatException | ArithmeticException exception) {
				throw new IllegalArgumentException("Saved collection count for " + entry.getKey() + " must be an integer.", exception);
			}
			if (count < 0) {
				throw new IllegalArgumentException("Saved collection count for " + entry.getKey() + " must be non-negative.");
			}
			collections.put(entry.getKey(), count);
		}
		this.singleplayerhsfeatures$setCollections(collections);
	}

	@Override
	public long singleplayerhsfeatures$getCombatExperience() {
		return this.singleplayerhsfeatures$getData().get(singleplayerhsfeatures$COMBAT_XP);
	}

	@Override
	public void singleplayerhsfeatures$setCombatExperience(long experience) {
		this.singleplayerhsfeatures$getData().set(singleplayerhsfeatures$COMBAT_XP,
			singleplayerhsfeatures$validateExperience(experience));
	}

	@Override
	public long singleplayerhsfeatures$getMiningExperience() {
		return this.singleplayerhsfeatures$getData().get(singleplayerhsfeatures$MINING_XP);
	}

	@Override
	public void singleplayerhsfeatures$setMiningExperience(long experience) {
		this.singleplayerhsfeatures$getData().set(singleplayerhsfeatures$MINING_XP,
			singleplayerhsfeatures$validateExperience(experience));
	}

	@Override
	public long singleplayerhsfeatures$getFarmingExperience() {
		return this.singleplayerhsfeatures$getData().get(singleplayerhsfeatures$FARMING_XP);
	}

	@Override
	public void singleplayerhsfeatures$setFarmingExperience(long experience) {
		this.singleplayerhsfeatures$getData().set(singleplayerhsfeatures$FARMING_XP,
			this.singleplayerhsfeatures$validateExperience(experience));
	}

	@Override
	public long singleplayerhsfeatures$getForagingExperience() {
		return this.singleplayerhsfeatures$getData().get(singleplayerhsfeatures$FORAGING_XP);
	}

	@Override
	public void singleplayerhsfeatures$setForagingExperience(long experience) {
		this.singleplayerhsfeatures$getData().set(singleplayerhsfeatures$FORAGING_XP,
			this.singleplayerhsfeatures$validateExperience(experience));
	}

	@Override
	public long singleplayerhsfeatures$getFishingExperience() {
		return this.singleplayerhsfeatures$getData().get(singleplayerhsfeatures$FISHING_XP);
	}

	@Override
	public void singleplayerhsfeatures$setFishingExperience(long experience) {
		this.singleplayerhsfeatures$getData().set(singleplayerhsfeatures$FISHING_XP,
			this.singleplayerhsfeatures$validateExperience(experience));
	}

	@Override
	public long singleplayerhsfeatures$getEnchantingExperience() {
		return this.singleplayerhsfeatures$getData().get(singleplayerhsfeatures$ENCHANTING_XP);
	}

	@Override
	public void singleplayerhsfeatures$setEnchantingExperience(long experience) {
		this.singleplayerhsfeatures$getData().set(singleplayerhsfeatures$ENCHANTING_XP,
			this.singleplayerhsfeatures$validateExperience(experience));
	}

	@Override
	public long singleplayerhsfeatures$getAlchemyExperience() {
		return this.singleplayerhsfeatures$getData().get(singleplayerhsfeatures$ALCHEMY_XP);
	}

	@Override
	public void singleplayerhsfeatures$setAlchemyExperience(long experience) {
		this.singleplayerhsfeatures$getData().set(singleplayerhsfeatures$ALCHEMY_XP,
			this.singleplayerhsfeatures$validateExperience(experience));
	}

	@Override
	public long singleplayerhsfeatures$getTamingExperience() {
		return this.singleplayerhsfeatures$getData().get(singleplayerhsfeatures$TAMING_XP);
	}

	@Override
	public void singleplayerhsfeatures$setTamingExperience(long experience) {
		this.singleplayerhsfeatures$getData().set(singleplayerhsfeatures$TAMING_XP,
			this.singleplayerhsfeatures$validateExperience(experience));
	}

	@Override
	public Map<String, Long> singleplayerhsfeatures$getCollections() {
		return this.singleplayerhsfeatures$parseCollections(this.singleplayerhsfeatures$getCollectionsJson());
	}

	@Override
	public void singleplayerhsfeatures$setCollections(Map<String, Long> collections) {
		JsonObject json = new JsonObject();
		collections.forEach((itemId, count) -> {
			if (count < 0) {
				throw new IllegalArgumentException("Collection count for " + itemId + " must be non-negative.");
			}
			json.addProperty(itemId, count);
		});
		this.singleplayerhsfeatures$getData().set(singleplayerhsfeatures$COLLECTIONS, json.toString());
	}

	@Unique
	private String singleplayerhsfeatures$getCollectionsJson() {
		return this.singleplayerhsfeatures$getData().get(singleplayerhsfeatures$COLLECTIONS);
	}

	@Unique
	private Map<String, Long> singleplayerhsfeatures$parseCollections(String json) {
		JsonElement parsed = JsonParser.parseString(json);
		if (!parsed.isJsonObject()) {
			throw new IllegalStateException("Synchronized collection data must be a JSON object.");
		}
		Map<String, Long> collections = new HashMap<>();
		for (Map.Entry<String, JsonElement> entry : parsed.getAsJsonObject().entrySet()) {
			collections.put(entry.getKey(), entry.getValue().getAsLong());
		}
		return collections;
	}

	@Unique
	private SynchedEntityData singleplayerhsfeatures$getData() {
		return ((EntityDataAccessMixin) (Object) this).singleplayerhsfeatures$getEntityData();
	}

	@Unique
	private long singleplayerhsfeatures$validateExperience(long experience) {
		if (experience < 0L) {
			throw new IllegalArgumentException("Saved skill experience must be non-negative.");
		}
		return Math.min(experience, singleplayerhsfeatures$MAX_SKILL_XP);
	}
}
