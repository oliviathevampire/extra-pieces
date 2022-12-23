package com.shnupbups.extrapieces.pieces;

import com.shnupbups.extrapieces.ExtraPieces;
import com.shnupbups.extrapieces.blocks.FencePieceBlock;
import com.shnupbups.extrapieces.blocks.PieceBlock;
import com.shnupbups.extrapieces.core.PieceSet;
import com.shnupbups.extrapieces.core.PieceType;
import com.shnupbups.extrapieces.core.PieceTypes;
import com.shnupbups.extrapieces.recipe.ShapedPieceRecipe;
import io.github.vampirestudios.artifice.api.ArtificeResourcePack;
import io.github.vampirestudios.artifice.api.builder.assets.BlockStateBuilder;
import io.github.vampirestudios.artifice.api.builder.assets.ModelBuilder;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;

public class FencePiece extends PieceType {
	public FencePiece() {
		super("fence");
	}

	public FencePieceBlock getNew(PieceSet set) {
		return new FencePieceBlock(set);
	}

	public Identifier getTagId() {
		return new Identifier("minecraft", "fences");
	}

	public ArrayList<ShapedPieceRecipe> getShapedRecipes() {
		ArrayList<ShapedPieceRecipe> recipes = super.getShapedRecipes();
		recipes.add(new ShapedPieceRecipe(this, 3, "bsb", "bsb").addToKey('b', PieceTypes.BASE).addToKey('s', Items.STICK));
		return recipes;
	}

	public void addBlockModels(ArtificeResourcePack.ClientResourcePackBuilder pack, PieceBlock pb) {
		super.addBlockModels(pack, pb);
		addBlockModel(pack, pb, "side");
		addBlockModel(pack, pb, "inventory");
	}

	public void addItemModel(ArtificeResourcePack.ClientResourcePackBuilder pack, PieceBlock pb) {
		ModelBuilder builder = new ModelBuilder()
				.parent(ExtraPieces.prependToPath(ExtraPieces.appendToPath(Registries.BLOCK.getId(pb.getBlock()), "_inventory"), "block/"));
		pack.addItemModel(Registries.BLOCK.getId(pb.getBlock()), builder);
	}

	public void addBlockstate(ArtificeResourcePack.ClientResourcePackBuilder pack, PieceBlock pb) {
		BlockStateBuilder builder = new BlockStateBuilder()
				.multipartCase(new BlockStateBuilder.Case().apply(new BlockStateBuilder.Variant().model(getModelPath(pb))));
		for (Direction d : Direction.values()) {
			if (d != Direction.UP && d != Direction.DOWN) {
				BlockStateBuilder.Case caze = new BlockStateBuilder.Case();
				caze.when(d.asString(), "true");
				BlockStateBuilder.Variant variant = new BlockStateBuilder.Variant()
						.model(getModelPath(pb, "side"))
						.uvlock(true);
				switch (d) {
					case EAST -> variant.rotationY(90);
					case WEST -> variant.rotationY(270);
					case SOUTH -> variant.rotationY(180);
				}
				caze.apply(variant);
				builder.multipartCase(caze);
			}
		}
		pack.addBlockState(Registries.BLOCK.getId(pb.getBlock()), builder);
	}
}
