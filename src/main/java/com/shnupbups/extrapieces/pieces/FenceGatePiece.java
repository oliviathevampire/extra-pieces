package com.shnupbups.extrapieces.pieces;

import com.shnupbups.extrapieces.blocks.FenceGatePieceBlock;
import com.shnupbups.extrapieces.blocks.PieceBlock;
import com.shnupbups.extrapieces.core.PieceSet;
import com.shnupbups.extrapieces.core.PieceType;
import com.shnupbups.extrapieces.core.PieceTypes;
import com.shnupbups.extrapieces.recipe.ShapedPieceRecipe;
import io.github.vampirestudios.artifice.api.ArtificeResourcePack;
import io.github.vampirestudios.artifice.api.builder.assets.BlockStateBuilder;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;

public class FenceGatePiece extends PieceType {
	public FenceGatePiece() {
		super("fence_gate");
	}

	public FenceGatePieceBlock getNew(PieceSet set, SoundEvent se1, SoundEvent se2) {
		return new FenceGatePieceBlock(set,se1,se2);
	}

	@Override
	public PieceBlock getNew(PieceSet set) {
		return null;
	}

	public ArrayList<ShapedPieceRecipe> getShapedRecipes() {
		ArrayList<ShapedPieceRecipe> recipes = super.getShapedRecipes();
		recipes.add(new ShapedPieceRecipe(this, 1, "sbs", "sbs").addToKey('b', PieceTypes.BASE).addToKey('s', Items.STICK));
		return recipes;
	}

	public void addBlockModels(ArtificeResourcePack.ClientResourcePackBuilder pack, PieceBlock pb) {
		super.addBlockModels(pack, pb);
		addBlockModel(pack, pb, "wall");
		addBlockModel(pack, pb, "open");
		addBlockModel(pack, pb, "wall_open");
	}

	public void addBlockstate(ArtificeResourcePack.ClientResourcePackBuilder pack, PieceBlock pb) {
		BlockStateBuilder builder = new BlockStateBuilder();
		for (Direction d : Direction.values()) {
			if (d != Direction.UP && d != Direction.DOWN) {
				BlockStateBuilder.Variant variant = new BlockStateBuilder.Variant();
				variant.model(getModelPath(pb));
				variant.uvlock(true);
				switch (d) {
					case NORTH -> variant.rotationY(180);
					case WEST -> variant.rotationY(90);
					case EAST -> variant.rotationY(270);
				}
				builder.variant("facing=" + d.asString() + ",in_wall=false,open=false", variant);
				variant = new BlockStateBuilder.Variant();
				variant.model(getModelPath(pb, "wall"));
				variant.uvlock(true);
				switch (d) {
					case NORTH -> variant.rotationY(180);
					case WEST -> variant.rotationY(90);
					case EAST -> variant.rotationY(270);
				}
				builder.variant("facing=" + d.asString() + ",in_wall=true,open=false", variant);
				variant = new BlockStateBuilder.Variant();
				variant.model(getModelPath(pb, "open"));
				variant.uvlock(true);
				switch (d) {
					case NORTH -> variant.rotationY(180);
					case WEST -> variant.rotationY(90);
					case EAST -> variant.rotationY(270);
				}
				builder.variant("facing=" + d.asString() + ",in_wall=false,open=true", variant);
				variant = new BlockStateBuilder.Variant();
				variant.model(getModelPath(pb, "wall_open"));
				variant.uvlock(true);
				switch (d) {
					case NORTH -> variant.rotationY(180);
					case WEST -> variant.rotationY(90);
					case EAST -> variant.rotationY(270);
				}
				builder.variant("facing=" + d.asString() + ",in_wall=true,open=true", variant);
			}
		}
		pack.addBlockState(Registries.BLOCK.getId(pb.getBlock()), builder);
	}
}
