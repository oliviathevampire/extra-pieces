package com.shnupbups.extrapieces.pieces;

import com.shnupbups.extrapieces.ExtraPieces;
import com.shnupbups.extrapieces.blocks.PieceBlock;
import com.shnupbups.extrapieces.blocks.StairsPieceBlock;
import com.shnupbups.extrapieces.core.PieceSet;
import com.shnupbups.extrapieces.core.PieceType;
import com.shnupbups.extrapieces.core.PieceTypes;
import com.shnupbups.extrapieces.recipe.ShapedPieceRecipe;
import io.github.vampirestudios.artifice.api.ArtificeResourcePack;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.block.enums.StairShape;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.registry.Registry;
import net.minecraft.registry.Registries;

import java.util.ArrayList;

public class StairsPiece extends PieceType {
	public StairsPiece() {
		super("stairs");
	}

	public StairsPieceBlock getNew(PieceSet set) {
		return new StairsPieceBlock(set);
	}

	public Identifier getTagId() {
		return new Identifier("minecraft", "stairs");
	}

	public ArrayList<ShapedPieceRecipe> getShapedRecipes() {
		ArrayList<ShapedPieceRecipe> recipes = super.getShapedRecipes();
		recipes.add(new ShapedPieceRecipe(this, 4, "b  ", "bb ", "bbb").addToKey('b', PieceTypes.BASE));
		return recipes;
	}

	public void addBlockModels(ArtificeResourcePack.ClientResourcePackBuilder pack, PieceBlock pb) {
		super.addBlockModels(pack, pb);
		addBlockModel(pack, pb, "inner");
		addBlockModel(pack, pb, "outer");
	}

	public void addBlockstate(ArtificeResourcePack.ClientResourcePackBuilder pack, PieceBlock pb) {
		pack.addBlockState(Registries.BLOCK.getId(pb.getBlock()), state -> {
			for (Direction d : Direction.values()) {
				if (!(d.equals(Direction.DOWN) || d.equals(Direction.UP))) {
					for (BlockHalf h : BlockHalf.values()) {
						for (StairShape s : StairShape.values()) {
							String varname = "facing=" + d.asString() + ",half=" + h.asString() + ",shape=" + s.asString();
							state.variant(varname, var -> {
								var.uvlock(true);
								int y = 0;
								switch (s) {
									case STRAIGHT -> {
										var.model(getModelPath(pb));
										switch (d) {
											case EAST -> y = 0;
											case WEST -> y = 180;
											case NORTH -> y = 270;
											case SOUTH -> y = 90;
										}
									}
									case OUTER_RIGHT -> {
										var.model(getModelPath(pb, "outer"));
										switch (h) {
											case BOTTOM -> y = switch (d) {
												case EAST -> 0;
												case WEST -> 180;
												case NORTH -> 270;
												case SOUTH -> 90;
												default -> y;
											};
											case TOP -> y = switch (d) {
												case EAST -> 90;
												case WEST -> 270;
												case NORTH -> 0;
												case SOUTH -> 180;
												default -> y;
											};
										}
									}
									case OUTER_LEFT -> {
										var.model(getModelPath(pb, "outer"));
										switch (h) {
											case BOTTOM -> y = switch (d) {
												case EAST -> 270;
												case WEST -> 90;
												case NORTH -> 180;
												case SOUTH -> 0;
												default -> y;
											};
											case TOP -> {
												y = switch (d) {
													case EAST -> 0;
													case WEST -> 180;
													case NORTH -> 270;
													case SOUTH -> 90;
													default -> y;
												};
											}
										}
									}
									case INNER_RIGHT -> {
										var.model(getModelPath(pb, "inner"));
										y = switch (h) {
											case BOTTOM -> switch (d) {
												case EAST -> 0;
												case WEST -> 180;
												case SOUTH -> 90;
												case NORTH -> 270;
												default -> y;
											};
											case TOP -> switch (d) {
												case EAST -> 90;
												case WEST -> 270;
												case NORTH -> 0;
												case SOUTH -> 180;
												default -> y;
											};
										};
									}
									case INNER_LEFT -> {
										var.model(getModelPath(pb, "inner"));
										y = switch (h) {
											case BOTTOM -> switch (d) {
												case EAST -> 270;
												case WEST -> 90;
												case NORTH -> 180;
												case SOUTH -> 0;
												default -> y;
											};
											case TOP -> switch (d) {
												case EAST -> 0;
												case WEST -> 180;
												case NORTH -> 270;
												case SOUTH -> 90;
												default -> y;
											};
										};
									}
								}
								if (h.equals(BlockHalf.TOP)) var.rotationX(180);
								var.rotationY(y);
							});
						}
					}
				}
			}
			state.variant("facing=east,half=bottom,shape=straight", var -> {
				var.model(ExtraPieces.prependToPath(Registries.BLOCK.getId(pb.getBlock()), "block/"));
			});
			state.variant("facing=west,half=bottom,shape=straight", var -> {
				var.model(ExtraPieces.prependToPath(Registries.BLOCK.getId(pb.getBlock()), "block/"));
				var.rotationY(180);
				var.uvlock(true);
			});
		});
	}
}
