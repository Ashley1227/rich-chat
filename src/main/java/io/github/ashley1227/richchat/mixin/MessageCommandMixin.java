package io.github.ashley1227.richchat.mixin;

import io.github.ashley1227.richchat.formatting.Formatter;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.MessageCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Collection;

@Mixin(MessageCommand.class)
public abstract class MessageCommandMixin {
	private static Formatter formatter;

	private static void setFormatter(MinecraftServer server) {
		formatter = new Formatter(server);
	}

	@Inject(at = @At("HEAD"), method = "Lnet/minecraft/server/command/MessageCommand;execute(Lnet/minecraft/server/command/ServerCommandSource;Ljava/util/Collection;Lnet/minecraft/text/Text;)I", cancellable = true)
	private static void execute(ServerCommandSource source, Collection<ServerPlayerEntity> targets, Text message, CallbackInfoReturnable<Integer> info) {
		setFormatter(source.getMinecraftServer());
		ArrayList<LiteralText> texts = formatter.format(message.asString());

		for(ServerPlayerEntity p : targets) {
			p.sendMessage((new TranslatableText("commands.message.display.incoming", formatter.formatPlayerName(source.getDisplayName().asString()), texts.get(0))), false);
			for (int i = 1; i < texts.size(); i++) {
				p.sendMessage(texts.get(i), false);
			}
			source.sendFeedback((new TranslatableText("commands.message.display.outgoing", formatter.formatPlayerName(source.getDisplayName().asString()), texts.get(0))), false);
			for (int i = 1; i < texts.size(); i++) {
				source.sendFeedback(
						texts.get(i), false
				);
			}
		}
		info.setReturnValue(targets.size());
	}
}
