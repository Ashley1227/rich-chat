package io.github.ashley1227.richchat.formatting.color;

import java.util.ArrayList;
import java.util.Iterator;

import net.minecraft.util.Formatting;

public class ColorCode implements Iterable<String> {

	private String key;
	private Formatting formatting;

	private ArrayList<String> aliases;

	public ColorCode(String key, Formatting formatting) {
		this.key = key;
		this.formatting = formatting;

		this.aliases = new ArrayList<>();
		this.aliases.add(key);
	}
	public ArrayList<String> getAliases() {
		return aliases;
	}
	public ColorCode addAlias(String alias) {
		this.aliases.add(alias);
		return this;
	}

	public String getKey() {
		return this.key;
	}

	public Formatting formatting() {
		return this.formatting;
	}

	public String toString() {
		return this.getKey();
	}

	@Override
	public Iterator<String> iterator() {
		return aliases.iterator();
	}
}
