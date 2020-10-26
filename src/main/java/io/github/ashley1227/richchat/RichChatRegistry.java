package io.github.ashley1227.richchat;

import io.github.ashley1227.richchat.formatting.color.ColorCode;
import io.github.ashley1227.richchat.formatting.emoji.Emoji;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import com.mojang.serialization.Lifecycle;

public class RichChatRegistry {
	private RichChatRegistry(){}
	public static final SimpleRegistry<Emoji> EMOJIS = new SimpleRegistry<>(RegistryKey.ofRegistry(new Identifier(RichChatMod.MODID, "emoji")), Lifecycle.stable());
	public static final Registry<ColorCode> COLOR_CODES = new SimpleRegistry<>(RegistryKey.ofRegistry(new Identifier(RichChatMod.MODID, "color_codes")), Lifecycle.stable());
}
