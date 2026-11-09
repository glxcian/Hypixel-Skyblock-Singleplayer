package com.glxcian.shf.mixin;

import com.glxcian.shf.client.ProgressionMenuController;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class NetherStarMenuMixin {
	@Inject(
		method = "use(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;",
		at = @At("HEAD"),
		cancellable = true
	)
	private void singleplayerhsfeatures$openMenu(
		Level level,
		Player player,
		InteractionHand hand,
		CallbackInfoReturnable<InteractionResult> cir
	) {
		if (level instanceof ServerLevel
			&& player.getItemInHand(hand).is(Items.NETHER_STAR)
			&& player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
			ProgressionMenuController.open(serverPlayer);
			cir.setReturnValue(InteractionResult.SUCCESS);
		}
	}
}
