package com.shnupbups.extrapieces.pieces;

import com.shnupbups.extrapieces.blocks.CornerPieceBlock;
import com.shnupbups.extrapieces.blocks.PieceBlock;
import com.shnupbups.extrapieces.core.PieceSet;
import com.shnupbups.extrapieces.core.PieceType;
import com.shnupbups.extrapieces.core.PieceTypes;
import com.shnupbups.extrapieces.recipe.ShapedPieceRecipe;
import io.github.vampirestudios.artifice.api.ArtificeResourcePack;
import io.github.vampirestudios.artifice.api.builder.assets.BlockStateBuilder;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;

public class CornerPiece extends PieceType {
	public CornerPiece() {
		super("corner");
	}

	public CornerPieceBlock getNew(PieceSet set) {
		return new CornerPieceBlock(set);
	}

	public ArrayList<ShapedPieceRecipe> getShapedRecipes() {
		ArrayList<ShapedPieceRecipe> recipes = super.getShapedRecipes();
		recipes.add(new ShapedPieceRecipe(this, 4, "bbb", "bb ", "b  ").addToKey('b', PieceTypes.BASE));
		return recipes;
	}

	public void addBlockstate(ArtificeResourcePack.ClientResourcePackBuilder pack, PieceBlock pb) {
		BlockStateBuilder builder = new BlockStateBuilder();
		for (Direction d : Direction.values()) {
			if (d != Direction.UP && d != Direction.DOWN) {
				BlockStateBuilder.Variant variant = new BlockStateBuilder.Variant();
				variant.model(getModelPath(pb));
				variant.uvlock(true);
				switch (d) {
					case SOUTH -> variant.rotationY(180);
					case EAST -> variant.rotationY(90);
					case WEST -> variant.rotationY(270);
				}
				builder.variant("facing=" + d.asString(), variant);
			}
		}
		pack.addBlockState(Registries.BLOCK.getId(pb.getBlock()), builder);
	}
}
