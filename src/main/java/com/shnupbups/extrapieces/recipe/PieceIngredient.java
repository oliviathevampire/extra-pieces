package com.shnupbups.extrapieces.recipe;

import com.shnupbups.extrapieces.ExtraPieces;
import com.shnupbups.extrapieces.core.PieceSet;
import com.shnupbups.extrapieces.core.PieceType;
import com.shnupbups.extrapieces.core.PieceTypes;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class PieceIngredient {
	public final PIType type;
	private final Identifier id;

	public PieceIngredient(PieceType type) {
		this.type = PIType.PIECE;
		this.id = type.getId();
	}

	public PieceIngredient(ItemConvertible item) {
		this.type = PIType.ITEM;
		this.id = Registries.ITEM.getId(item.asItem());
	}

	public PieceIngredient(TagKey tag) {
		this.type = PIType.TAG;
		this.id = tag.id();
	}

	public Identifier getId(PieceSet set) {
		if ((type == PIType.PIECE) && set.getPiece(getPieceType()).equals(Blocks.AIR)) {
			ExtraPieces.log("Attempted to get type " + id.toString() + " from set " + set.getName() + " for a recipe, but got air! This is not good!");
		}
		return switch (type) {
			case ITEM, TAG -> id;
			case PIECE -> Registries.ITEM.getId(set.getPiece(getPieceType()).asItem());
		};
	}
	
	public boolean isTag() {
		return type == PIType.TAG;
	}

	public PieceType getPieceType() {
		if(type == PIType.PIECE) {
			Optional<PieceType> pt = PieceTypes.getTypeOrEmpty(id);
			if(pt.isPresent()) return pt.get();
		}
		return null;
	}
	
	public boolean hasIngredientInSet(PieceSet set) {
		return (!type.equals(PIType.PIECE) || set.hasPiece(getPieceType()));
	}

	enum PIType {
		ITEM,
		PIECE,
		TAG
	}
}
