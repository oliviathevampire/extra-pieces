package com.shnupbups.extrapieces.recipe;

import com.shnupbups.extrapieces.core.PieceSet;
import com.shnupbups.extrapieces.core.PieceType;
import io.github.vampirestudios.artifice.api.ArtificeResourcePack;
import io.github.vampirestudios.artifice.api.builder.data.recipe.StonecuttingRecipeBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class WoodmillingPieceRecipe extends PieceRecipe {
	private final PieceIngredient input;

	public WoodmillingPieceRecipe(PieceType output, int count, PieceIngredient input) {
		super(output, count);
		this.input = input;
	}

	public WoodmillingPieceRecipe(PieceType output, int count, PieceType input) {
		this(output, count, new PieceIngredient(input));
	}
	
	public WoodmillingPieceRecipe(PieceType output, int count, ItemConvertible input) {
		this(output, count, new PieceIngredient(input));
	}
	
	public WoodmillingPieceRecipe(PieceType output, int count, TagKey input) {
		this(output, count, new PieceIngredient(input));
	}

	public PieceIngredient getInput() {
		return input;
	}

	public void add(ArtificeResourcePack.ServerResourcePackBuilder data, Identifier id, PieceSet set) {
		StonecuttingRecipeBuilder builder = new StonecuttingRecipeBuilder()
				.type(new Identifier("woodmill", "woodmilling"))
				.result(Registries.BLOCK.getId(getOutput(set)))
				.group(Registries.BLOCK.getId(getOutput(set)))
				.count(getCount());
		PieceIngredient pi = getInput();
		if(pi.isTag()) builder.ingredientTag(pi.getId(set));
		else builder.ingredientItem(pi.getId(set));
		data.addStonecuttingRecipe(id, builder);
	}

	@Override
	public boolean canAddForSet(PieceSet set) {
		return input.hasIngredientInSet(set);
	}
}
