package com.glxcian.shf.mixin;

import com.glxcian.shf.client.ProgressionMenuController;
import com.glxcian.shf.client.ProgressionMenu;
import com.glxcian.shf.item.CustomItems;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Commands.class)
public abstract class ProgressionCommandsMixin {
	@Inject(method = "<init>", at = @At("TAIL"))
	private void singleplayerhsfeatures$registerProgressionCommands(
		Commands.CommandSelection selection,
		CommandBuildContext context,
		CallbackInfo ci
	) {
		Commands commands = (Commands) (Object) this;
		commands.getDispatcher().register(Commands.literal("sbmenu")
			.executes(command -> {
				ProgressionMenuController.open(command.getSource().getPlayerOrException());
				return 1;
			}));
		commands.getDispatcher().register(Commands.literal("skills")
			.executes(command -> {
				ProgressionMenuController.open(command.getSource().getPlayerOrException(), ProgressionMenu.Page.SKILLS);
				return 1;
			}));
		commands.getDispatcher().register(Commands.literal("collections")
			.executes(command -> {
				ProgressionMenuController.open(command.getSource().getPlayerOrException(), ProgressionMenu.Page.COLLECTIONS);
				return 1;
			}));
		commands.getDispatcher().register(Commands.literal("collection")
			.executes(command -> {
				ProgressionMenuController.open(command.getSource().getPlayerOrException(), ProgressionMenu.Page.COLLECTIONS);
				return 1;
			}));
		commands.getDispatcher().register(Commands.literal("combat")
			.executes(command -> {
				ProgressionMenuController.openSkillLevels(
					command.getSource().getPlayerOrException(),
					com.glxcian.shf.stat.PlayerProgression.Skill.COMBAT,
					0
				);
				return 1;
			}));
		commands.getDispatcher().register(Commands.literal("mining")
			.executes(command -> {
				ProgressionMenuController.openSkillLevels(
					command.getSource().getPlayerOrException(),
					com.glxcian.shf.stat.PlayerProgression.Skill.MINING,
					0
				);
				return 1;
			}));
		for (com.glxcian.shf.stat.PlayerProgression.Skill skill : com.glxcian.shf.stat.PlayerProgression.Skill.values()) {
			if (skill == com.glxcian.shf.stat.PlayerProgression.Skill.COMBAT
				|| skill == com.glxcian.shf.stat.PlayerProgression.Skill.MINING) {
				continue;
			}
			commands.getDispatcher().register(Commands.literal(skill.displayName().toLowerCase(java.util.Locale.ROOT))
				.executes(command -> {
					ProgressionMenuController.openSkillLevels(
						command.getSource().getPlayerOrException(), skill, 0
					);
					return 1;
				}));
		}
		commands.getDispatcher().register(Commands.literal("levels")
			.executes(command -> {
				ProgressionMenuController.openGlobalLevel(command.getSource().getPlayerOrException());
				return 1;
			}));
		commands.getDispatcher().register(Commands.literal("compactor")
			.executes(command -> {
				giveItem(command.getSource().getPlayerOrException(), CustomItems.COMPACTOR, "Compactor");
				return 1;
			}));
		commands.getDispatcher().register(Commands.literal("promisingpickaxe")
			.executes(command -> {
				giveItem(command.getSource().getPlayerOrException(), CustomItems.PROMISING_PICKAXE, "Promising Pickaxe");
				return 1;
			}));
	}

	private static void giveItem(ServerPlayer player, Item item, String itemName) {
		ItemStack stack = new ItemStack(item);
		if (!player.getInventory().add(stack) && !stack.isEmpty()) {
			player.drop(stack, false, net.minecraft.util.Prediction.SERVER_ONLY);
		}
		player.sendSystemMessage(Component.literal("Given " + itemName + "."));
	}
}
