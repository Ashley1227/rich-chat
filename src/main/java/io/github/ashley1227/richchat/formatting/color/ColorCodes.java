package io.github.ashley1227.richchat.formatting.color;

import io.github.ashley1227.richchat.RichChatRegistry;
import net.minecraft.util.Formatting;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.Map;

public class ColorCodes {
	private static Map<String, ColorCode> map = new HashMap<>();

	public static ColorCode BLACK = new ColorCode("black", Formatting.BLACK).addAlias("blk").addAlias("blck").addAlias("0");
	public static ColorCode DARK_BLUE = new ColorCode("dark_blue", Formatting.DARK_BLUE).addAlias("dblue").addAlias("1");
	public static ColorCode DARK_GREEN = new ColorCode("dark_green", Formatting.DARK_GREEN).addAlias("dgreen").addAlias("dgrn").addAlias("2");
	public static ColorCode DARK_AQUA = new ColorCode("dark_aqua", Formatting.DARK_AQUA).addAlias("daqua").addAlias("dcyan").addAlias("dteal").addAlias("3");
	public static ColorCode DARK_RED = new ColorCode("dark_red", Formatting.DARK_RED).addAlias("dred").addAlias("4");
	public static ColorCode DARK_PURPLE = new ColorCode("dark_purple", Formatting.DARK_PURPLE).addAlias("dpurple").addAlias("dprpl").addAlias("dppl").addAlias("5");
	public static ColorCode GOLD = new ColorCode("gold", Formatting.GOLD).addAlias("6");
	public static ColorCode GRAY = new ColorCode("gray", Formatting.GRAY).addAlias("grey").addAlias("7");
	public static ColorCode DARK_GRAY = new ColorCode("dark_gray", Formatting.DARK_GRAY).addAlias("dgray").addAlias("dark_grey").addAlias("dgrey").addAlias("8");

	public static ColorCode BLUE = new ColorCode("blue", Formatting.BLUE).addAlias("9");
	public static ColorCode GREEN = new ColorCode("green", Formatting.GREEN).addAlias("grn").addAlias("a");
	public static ColorCode AQUA = new ColorCode("aqua", Formatting.AQUA).addAlias("cyan").addAlias("teal").addAlias("b");
	public static ColorCode RED = new ColorCode("red", Formatting.RED).addAlias("c");
	public static ColorCode PURPLE = new ColorCode("light_purple", Formatting.LIGHT_PURPLE).addAlias("purple").addAlias("prpl").addAlias("d");
	public static ColorCode YELLOW = new ColorCode("yellow", Formatting.YELLOW).addAlias("ylw").addAlias("e");
	public static ColorCode WHITE = new ColorCode("white", Formatting.WHITE).addAlias("reset").addAlias("r").addAlias("f");

	public static void register() {
		register(BLACK);
		register(DARK_BLUE);
		register(DARK_GREEN);
		register(DARK_AQUA);
		register(DARK_RED);
		register(DARK_PURPLE);
		register(GOLD);
		register(GRAY);
		register(DARK_GRAY);
		register(BLUE);
		register(GREEN);
		register(AQUA);
		register(RED);
		register(PURPLE);
		register(YELLOW);
		register(WHITE);
	}

	public static void register(ColorCode c) {
		for (String s : c) {
			map.put(s, c);
		}
		Registry.register(RichChatRegistry.COLOR_CODES, c.getKey(), c);
	}

	public static ColorCode get(String alias) {
		return map.getOrDefault(alias.toLowerCase(), WHITE);
	}
}
