package io.github.ashley1227.richchat.mixin;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.ashley1227.richchat.formatting.Formatter;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.network.MessageType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.SayCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.ArrayList;
import java.util.UUID;

@Mixin(value = SayCommand.class, priority = 1)
public abstract class SayCommandMixin {
	/**
	 * @author Ashley1227
	 * @reason its easier this way?
	 */
	@Overwrite
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register((CommandManager.literal("say").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(CommandManager.argument("message", MessageArgumentType.message())
				.executes((commandContext) -> {
					Formatter formatter = new Formatter(commandContext.getSource().getMinecraftServer());
					ArrayList<LiteralText> texts = formatter.format(MessageArgumentType.getMessage(commandContext, "message").asString());
					Entity entity = commandContext.getSource().getEntity();
					UUID playerUUID = Util.NIL_UUID;
					MessageType type = MessageType.SYSTEM;
					if (entity != null) {
						playerUUID = entity.getUuid();
						type = MessageType.CHAT;
					}
					commandContext.getSource().getMinecraftServer().getPlayerManager().broadcastChatMessage(
						new TranslatableText("chat.type.announcement", commandContext.getSource().getDisplayName(), texts.get(0)),
						type,
						playerUUID
					);

					for (int i = 1; i < texts.size(); i++) {
						commandContext.getSource().getMinecraftServer().getPlayerManager().broadcastChatMessage(
								texts.get(i),
								MessageType.CHAT,
								commandContext.getSource().getEntity().getUuid()
						);
					}
					return 1;
				})));
	}
}
