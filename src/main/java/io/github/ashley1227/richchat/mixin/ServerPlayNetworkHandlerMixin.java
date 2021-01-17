package io.github.ashley1227.richchat.mixin;

import io.github.ashley1227.richchat.formatting.Formatter;
import net.minecraft.network.MessageType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {

	@Final
	@Shadow
	private MinecraftServer server;

	@Shadow
	public ServerPlayerEntity player;

	private final Formatter formatter = new Formatter(server);

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcastChatMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/MessageType;Ljava/util/UUID;)V"), method = "method_31286", cancellable = true)
	public void onBroadcastChatMessage(String string, CallbackInfo info) {
		formatter.setServer(server);
		ArrayList<LiteralText> texts = formatter.format(string);
		this.server.getPlayerManager().broadcastChatMessage(
				new TranslatableText(
					"chat.type.text",
					this.player.getDisplayName(),
					texts.get(0)
				),
				MessageType.CHAT,
				this.player.getUuid()
		);

		for(int i = 1; i < texts.size(); i++) {
			this.server.getPlayerManager().broadcastChatMessage(
				texts.get(i),
				MessageType.CHAT,
				this.player.getUuid()
			);
		}
		info.cancel();
	}
}
