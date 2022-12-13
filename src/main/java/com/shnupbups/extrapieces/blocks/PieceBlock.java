package com.shnupbups.extrapieces.blocks;

import com.shnupbups.extrapieces.core.PieceSet;
import com.shnupbups.extrapieces.core.PieceType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemConvertible;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public interface PieceBlock extends ItemConvertible {
	PieceType getType();

	Block getBlock();

	PieceSet getSet();
	
	default Block getBase() {
		return getSet().getBase();
	}

	default BlockState getBaseState() {
		return getBase().getDefaultState();
	}

	default String getPieceString() {
		return getSet().getName()+" "+getType().getId();
	}

	@Environment(EnvType.CLIENT)
	void randomDisplayTick(BlockState blockState_1, World world_1, BlockPos blockPos_1, Random random_1);

	void scheduledTick(BlockState blockState_1, ServerWorld world_1, BlockPos blockPos_1, Random random_1);
}
