package com.shnupbups.extrapieces.recipe;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.shnupbups.extrapieces.core.PieceSet;
import com.shnupbups.extrapieces.core.PieceType;
import io.github.vampirestudios.artifice.api.ArtificeResourcePack;
import io.github.vampirestudios.artifice.api.builder.data.recipe.MultiIngredientBuilder;
import io.github.vampirestudios.artifice.api.builder.data.recipe.ShapedRecipeBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;

import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registry;
import net.minecraft.registry.Registries;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ShapedPieceRecipe extends PieceRecipe {
	private ListMultimap<Character, PieceIngredient> key = MultimapBuilder.hashKeys().arrayListValues().build();
	private String[] pattern;

	public ShapedPieceRecipe(PieceType output, int count, String... pattern) {
		super(output, count);
		this.pattern = pattern;
	}

	public ShapedPieceRecipe addToKey(char c, PieceType type) {
		return addToKey(c, new PieceIngredient(type));
	}

	public ShapedPieceRecipe addToKey(char c, ItemConvertible item) {
		return addToKey(c, new PieceIngredient(item));
	}
	
	public ShapedPieceRecipe addToKey(char c, TagKey tag) {
		return addToKey(c, new PieceIngredient(tag));
	}

	public ShapedPieceRecipe addToKey(char c, PieceIngredient ingredient) {
		key.put(c, ingredient);
		return this;
	}

	public Multimap<Character, PieceIngredient> getKey() {
		return key;
	}

	public String[] getPattern() {
		return pattern;
	}

	public List<PieceIngredient> getFromKey(char c) {
		return key.get(c);
	}

	public void add(ArtificeResourcePack.ServerResourcePackBuilder data, Identifier id, PieceSet set) {
		ShapedRecipeBuilder builder = new ShapedRecipeBuilder()
				.result(Registries.BLOCK.getId(this.getOutput(set)), this.getCount())
				.group(Registries.BLOCK.getId(getOutput(set)))
				.pattern(this.getPattern());
		for (Map.Entry<Character, Collection<PieceIngredient>> ingredients : this.getKey().asMap().entrySet()) {
			MultiIngredientBuilder ingredientBuilder = new MultiIngredientBuilder();
			for (PieceIngredient pi : ingredients.getValue()) {
				if (pi.isTag()) ingredientBuilder.tag(pi.getId(set));
				else ingredientBuilder.item(pi.getId(set));
			}
			builder.multiIngredient(ingredients.getKey(), ingredientBuilder);
		}
		data.addShapedRecipe(id, builder);
	}

	@Override
	public boolean canAddForSet(PieceSet set) {
		for (PieceIngredient pi : key.values()) {
			if (!pi.hasIngredientInSet(set)) return false;
		}
		return true;
	}
}
