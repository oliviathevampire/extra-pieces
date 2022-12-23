package com.shnupbups.extrapieces.pieces;

import com.shnupbups.extrapieces.blocks.ColumnPieceBlock;
import com.shnupbups.extrapieces.blocks.PieceBlock;
import com.shnupbups.extrapieces.core.PieceSet;
import com.shnupbups.extrapieces.core.PieceType;
import io.github.vampirestudios.artifice.api.ArtificeResourcePack;
import io.github.vampirestudios.artifice.api.builder.assets.BlockStateBuilder;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.Direction;

public class ColumnPiece extends PieceType {
	public ColumnPiece() {
		super("column");
	}

	public ColumnPieceBlock getNew(PieceSet set) {
		return new ColumnPieceBlock(set);
	}

	public void addBlockstate(ArtificeResourcePack.ClientResourcePackBuilder pack, PieceBlock pb) {
		BlockStateBuilder state = new BlockStateBuilder();
		for (Direction.Axis a : Direction.Axis.values()) {
			BlockStateBuilder.Variant variant = new BlockStateBuilder.Variant();
			variant.uvlock(true);
			variant.model(getModelPath(pb));
			if (a != Direction.Axis.Y) {
				variant.rotationX(90);
				if (a == Direction.Axis.X) {
					variant.rotationY(90);
				}
			}
			state.variant("axis=" + a.asString(), variant);
		}
		pack.addBlockState(Registries.BLOCK.getId(pb.getBlock()), state);
	}
}
