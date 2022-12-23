package com.shnupbups.extrapieces.pieces;

import com.shnupbups.extrapieces.ExtraPieces;
import com.shnupbups.extrapieces.blocks.PieceBlock;
import com.shnupbups.extrapieces.blocks.WallPieceBlock;
import com.shnupbups.extrapieces.core.PieceSet;
import com.shnupbups.extrapieces.core.PieceType;
import com.shnupbups.extrapieces.core.PieceTypes;
import com.shnupbups.extrapieces.recipe.ShapedPieceRecipe;
import io.github.vampirestudios.artifice.api.ArtificeResourcePack;
import io.github.vampirestudios.artifice.api.builder.assets.BlockStateBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.registry.Registry;
import net.minecraft.registry.Registries;

import java.util.ArrayList;

public class WallPiece extends PieceType {
	public WallPiece() {
		super("wall");
	}

	public WallPieceBlock getNew(PieceSet set) {
		return new WallPieceBlock(set);
	}

	public Identifier getTagId() {
		return new Identifier("minecraft", "walls");
	}

	public ArrayList<ShapedPieceRecipe> getShapedRecipes() {
		ArrayList<ShapedPieceRecipe> recipes = super.getShapedRecipes();
		recipes.add(new ShapedPieceRecipe(this, 6, "bbb", "bbb").addToKey('b', PieceTypes.BASE));
		return recipes;
	}

	public void addBlockModels(ArtificeResourcePack.ClientResourcePackBuilder pack, PieceBlock pb) {
		super.addBlockModels(pack, pb);
		addBlockModel(pack, pb, "side");
		addBlockModel(pack, pb, "side_tall");
		addBlockModel(pack, pb, "inventory");
	}

	public void addItemModel(ArtificeResourcePack.ClientResourcePackBuilder pack, PieceBlock pb) {
		pack.addItemModel(Registries.BLOCK.getId(pb.getBlock()), model -> {
			model.parent(ExtraPieces.prependToPath(ExtraPieces.appendToPath(Registries.BLOCK.getId(pb.getBlock()), "_inventory"), "block/"));
		});
	}

	public void addBlockstate(ArtificeResourcePack.ClientResourcePackBuilder pack, PieceBlock pb) {
		BlockStateBuilder builder = new BlockStateBuilder();
		builder.multipartCase(new BlockStateBuilder.Case()
				.when(Direction.UP.asString(), "true")
				.apply(new BlockStateBuilder.Variant().model(getModelPath(pb)))
		);
		for (Direction d : Direction.values()) {
			if (d != Direction.UP && d != Direction.DOWN) {
				BlockStateBuilder.Variant variant = new BlockStateBuilder.Variant();
				builder.multipartCase(caze -> {
					caze.when(d.asString(), "low");
					caze.apply(var -> {
						var.model(getModelPath(pb, "side"));
						var.uvlock(true);
						switch (d) {
							case EAST -> var.rotationY(90);
							case WEST -> var.rotationY(270);
							case SOUTH -> var.rotationY(180);
						}
					});
				});
				state.multipartCase(caze -> {
					caze.when(d.asString(), "tall");
					caze.apply(var -> {
						var.model(getModelPath(pb, "side_tall"));
						var.uvlock(true);
						switch (d) {
							case EAST -> var.rotationY(90);
							case WEST -> var.rotationY(270);
							case SOUTH -> var.rotationY(180);
						}
					});
				});
			}
		}
		pack.addBlockState(Registries.BLOCK.getId(pb.getBlock()), state -> {

		});
	}
}
