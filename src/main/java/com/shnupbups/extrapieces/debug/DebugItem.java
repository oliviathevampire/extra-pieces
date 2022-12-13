package com.shnupbups.extrapieces.debug;

import com.shnupbups.extrapieces.blocks.PieceBlock;
import com.shnupbups.extrapieces.core.PieceSet;
import com.shnupbups.extrapieces.core.PieceSets;
import com.shnupbups.extrapieces.register.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;

import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DebugItem extends Item {
	public DebugItem() {
		super(new Item.Settings());
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		if (!context.getWorld().isClient()) {
			BlockPos pos = context.getBlockPos();
			BlockState state = context.getWorld().getBlockState(pos);
			context.getPlayer().getItemCooldownManager().set(this, 20);
			if (state.getBlock() instanceof PieceBlock) {
				PieceBlock pb = (PieceBlock) state.getBlock();
				context.getPlayer().sendMessage(Text.of(state.getBlock() + " is piece! Type: " + pb.getType() + " Set: " + pb.getSet()));
				return ActionResult.SUCCESS;
			} else if (PieceSets.hasSet(state.getBlock())) {
				context.getPlayer().sendMessage(Text.of(state.getBlock() + " is base! " + PieceSets.getSet(state.getBlock())));
				return ActionResult.SUCCESS;
			} else if (PieceSets.isPiece(state.getBlock())) {
				PieceBlock pb = PieceSets.asPieceBlock(state.getBlock());
				context.getPlayer().sendMessage(Text.of(state.getBlock() + " is vanilla piece! Type: " + pb.getType() + " Base: " + pb.getSet().getBase()));
				return ActionResult.SUCCESS;
			} else {
				context.getPlayer().sendMessage(Text.of(state.getBlock() + " is not part of a PieceSet."));
			}
		}
		return ActionResult.PASS;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		if (!world.isClient()) {
			if (ModBlocks.setBuilders.size() != PieceSets.registry.size()) {
				for (PieceSet.Builder psb : ModBlocks.setBuilders.values()) {
					if (!psb.isBuilt())
						player.sendMessage(Text.of("Errored Piece Set: " + psb.toString() + " Make sure the base and any vanilla pieces actually exist!"));
				}
			} else player.sendMessage(Text.of("All Piece Sets seem fine!"));
			player.getItemCooldownManager().set(this, 20);
			return new TypedActionResult<>(ActionResult.SUCCESS, player.getStackInHand(hand));
		}
		return super.use(world, player, hand);
	}
}
