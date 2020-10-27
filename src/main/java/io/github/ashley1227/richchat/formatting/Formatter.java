package io.github.ashley1227.richchat.formatting;

import com.vladsch.flexmark.ast.*;
import com.vladsch.flexmark.ext.autolink.AutolinkExtension;
import com.vladsch.flexmark.ext.gfm.strikethrough.Strikethrough;
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.parser.ParserEmulationProfile;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import io.github.ashley1227.richchat.RichChatMod;
import io.github.ashley1227.richchat.formatting.color.ColorCodes;
import io.github.ashley1227.richchat.formatting.emoji.Emojis;
import io.github.ashley1227.richchat.formatting.extensions.color.ColorExtension;
import io.github.ashley1227.richchat.formatting.extensions.color.ColorNode;
import io.github.ashley1227.richchat.formatting.extensions.emoji.EmojiExtension;
import io.github.ashley1227.richchat.formatting.extensions.emoji.EmojiNode;
import io.github.ashley1227.richchat.formatting.extensions.mention.MentionExtension;
import io.github.ashley1227.richchat.formatting.extensions.mention.MentionNode;
import io.github.ashley1227.richchat.formatting.extensions.obfuscation.ObfuscatedExtension;
import io.github.ashley1227.richchat.formatting.extensions.obfuscation.ObfuscatedNode;
import io.github.ashley1227.richchat.formatting.extensions.spoiler.SpoilerExtension;
import io.github.ashley1227.richchat.formatting.extensions.spoiler.SpoilerNode;
import io.github.ashley1227.richchat.formatting.extensions.underline.UnderlineExtension;
import io.github.ashley1227.richchat.formatting.extensions.underline.UnderlineNode;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;

import java.util.ArrayList;
import java.util.Arrays;

import static net.minecraft.util.Formatting.*;

public class Formatter {
	public EmojiFormatter emojiFormatter = new EmojiFormatter();

	//	public StringBuilder builder;
	private final Parser parser;

	private MinecraftServer server;

	// TODO: add configuration.
	public static String MENTION_SYMBOL = "@";

	private boolean dirty = false;

	public Formatter() {
		this(null);
	}

	public Formatter(MinecraftServer server) {
		this.server = server;
		MutableDataSet options = new MutableDataSet();

		options.setFrom(ParserEmulationProfile.GITHUB);
		options.set(Parser.EXTENSIONS, Arrays.asList(
				StrikethroughExtension.create(),
				AutolinkExtension.create(),

				EmojiExtension.create(),
				UnderlineExtension.create(),
				ObfuscatedExtension.create(),
				SpoilerExtension.create(),
				MentionExtension.create(),
				ColorExtension.create()
		));
		options.set(Parser.UNDERSCORE_DELIMITER_PROCESSOR, false);


		parser = Parser.builder(options).build();
		reset();
	}

	public void setServer(MinecraftServer server) {
		if (this.server == null)
			this.server = server;
	}

	public void reset() {
		//todo why empty?
	}

	/**
	 * @param string the message to format
	 *               <p>
	 *               Formats markdown into Minecraft text format.
	 */
	public ArrayList<LiteralText> format(String string) {
		this.reset();

		String[] strings = formatNewLines(string);

		ArrayList<LiteralText> texts = new ArrayList<>();

		for (String s : strings) {
			LiteralText text = new LiteralText("");

			// Do markdown stuff
			Node node = parser.parse(s).getFirstChild();

			while (node != null) {
				addNode(text, node);
				node = node.getNext();
			}
			texts.add(text);
		}

		// Finishing up
		return texts;
	}

	public String[] formatNewLines(String string) {
		return string.split("\\\\n");
	}


	public LiteralText formatPlayerName(String string) {
		return (LiteralText) (
				new LiteralText(MENTION_SYMBOL + string)
						.setStyle(
								Style.EMPTY.withUnderline(true).withItalic(true).withColor(BLUE)
										.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
												"/w " + string + " "))));
			}

			/**
			 * @param text text to add to
			 * @param node the node we're adding to the string builder
			 *             <p>
			 *             add a markdown node to the string builder.
			 */
			public void addNode(LiteralText text, Node node) {
				if (node == null)
					return;
				addFormatting(text, node);
				if (dirty) {
					this.dirty = false;
					text.append(RESET.toString());
				}
			}

			/**
			 * @param text text to add formatting to
			 * @param node the node to add the formatting from
			 */
			public void addFormatting(LiteralText text, Node node) {
				if (node instanceof Text) {
					text.append(String.valueOf(node.getChars()));
					return;
				}
				if (node instanceof EmojiNode) {
					EmojiNode emoji = (EmojiNode) node;
					text.append(emojiFormatter.format(emoji.getText().toStringOrNull()));
					markDirty();
					return;
				}
				if (node instanceof StrongEmphasis) {
					text.setStyle(text.getStyle().withFormatting(BOLD));
					addChildren(text, node);
					markDirty();
					return;
				}
				if (node instanceof Emphasis) {
					text.setStyle(text.getStyle().withFormatting(ITALIC));
					addChildren(text, node);
					markDirty();
					return;
				}
				if (node instanceof UnderlineNode) {
					text.setStyle(text.getStyle().withFormatting(UNDERLINE));
					addChildren(text, node);
					markDirty();
					return;
				}
				if (node instanceof Strikethrough) {
					text.setStyle(text.getStyle().withFormatting(STRIKETHROUGH));
					addChildren(text, node);
					markDirty();
					return;
				}
				if (node instanceof ObfuscatedNode) {
					text.setStyle(text.getStyle().withFormatting(OBFUSCATED));
					addChildren(text, node);
					markDirty();
					return;
				}
				if (node instanceof SpoilerNode) {
					SpoilerNode spoiler = (SpoilerNode) node;
					LiteralText t = new LiteralText(String.valueOf(spoiler.getText()).replaceAll(".", "▎"));
					text.append(t);

					Style style = t.getStyle();
					style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
							format(String.valueOf(spoiler.getText())).get(0)));
					return;
				}
				if (node instanceof Link) {
					Link link = (Link) node;
					text.append(genLink(link.getUrl().toStringOrNull(), link.getText().toStringOrNull()));
					return;
				}
				if (node instanceof AutoLink) {
					AutoLink link = (AutoLink) node;
					text.append(genLink(link.getUrl().toStringOrNull(), link.getText().toStringOrNull()));
					return;
				}
				if (node instanceof ColorNode) {
					text.setStyle(text.getStyle().withFormatting(ColorCodes.get(String.valueOf(node.getChars())).formatting()));
					return;
				}
				if (node instanceof MentionNode) {
					String name = String.valueOf(node.getChars());
					text.append(formatPlayerName(name));
					RichChatMod.playNotificationSound(server, name);
					return;
				}
				addChildren(text, node);
			}

			public void markDirty() {
				this.dirty = true;
			}

			public LiteralText genLink(String URL, String text) {
				LiteralText linkText = new LiteralText(text);
				Style style = Style.EMPTY
					.withClickEvent(
						new ClickEvent(
							ClickEvent.Action.OPEN_URL, URL)
					)
					.withColor(BLUE).withItalic(true).withUnderline(true);
		linkText.setStyle(style);
		return linkText;
	}

	public void addChildren(LiteralText text, Node node) {
		Node n = node.getFirstChild();
		if (n == null)
			return;
		while (n != null) {
			addNode(text, n);
			n = n.getNext();
		}
	}

	public static class EmojiFormatter {
		public String format(String string) {
			return Emojis.getEmoji(string).toString();
		}
	}
}

