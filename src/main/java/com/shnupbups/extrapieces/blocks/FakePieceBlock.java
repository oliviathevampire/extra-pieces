package com.shnupbups.extrapieces.blocks;

import com.shnupbups.extrapieces.core.PieceSet;
import com.shnupbups.extrapieces.core.PieceType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class FakePieceBlock implements PieceBlock {
	public final PieceType type;
	public final Block block;
	public final PieceSet set;

	public FakePieceBlock(Block block, PieceType type, PieceSet set) {
		this.type = type;
		this.block = block;
		this.set = set;
	}

	public PieceType getType() {
		return type;
	}

	public PieceSet getSet() {
		return set;
	}

	@Override
	public void randomDisplayTick(BlockState blockState_1, World world_1, BlockPos blockPos_1, Random random_1) {

	}

	@Override
	public void scheduledTick(BlockState blockState_1, ServerWorld world_1, BlockPos blockPos_1, Random random_1) {

	}

	public Block getBlock() {
		return block;
	}

	public Item asItem() {
		return getBlock().asItem();
	}
}
