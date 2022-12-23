package com.shnupbups.extrapieces.recipe;

import com.shnupbups.extrapieces.core.PieceSet;
import com.shnupbups.extrapieces.core.PieceType;
import io.github.vampirestudios.artifice.api.ArtificeResourcePack;
import io.github.vampirestudios.artifice.api.builder.data.recipe.MultiIngredientBuilder;
import io.github.vampirestudios.artifice.api.builder.data.recipe.ShapedRecipeBuilder;
import io.github.vampirestudios.artifice.api.builder.data.recipe.ShapelessRecipeBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registry;
import net.minecraft.registry.Registries;

import java.util.Collection;
import java.util.Map;

public class ShapelessPieceRecipe extends PieceRecipe {
	private PieceIngredient[] inputs;

	public ShapelessPieceRecipe(PieceType output, int count, PieceIngredient... inputs) {
		super(output, count);
		this.inputs = inputs;
	}

	public PieceIngredient[] getInputs() {
		return inputs;
	}

	public void add(ArtificeResourcePack.ServerResourcePackBuilder data, Identifier id, PieceSet set) {
		ShapelessRecipeBuilder builder = new ShapelessRecipeBuilder()
				.result(Registries.BLOCK.getId(this.getOutput(set)), this.getCount())
				.group(Registries.BLOCK.getId(getOutput(set)));
		for (PieceIngredient pi : getInputs()) {
			if (pi.isTag()) builder.ingredientTag(pi.getId(set));
			else builder.ingredientItem(pi.getId(set));
		}
		data.addShapelessRecipe(id, builder);
	}

	@Override
	public boolean canAddForSet(PieceSet set) {
		for (PieceIngredient pi : inputs) {
			if (!pi.hasIngredientInSet(set)) return false;
		}
		return true;
	}
}
