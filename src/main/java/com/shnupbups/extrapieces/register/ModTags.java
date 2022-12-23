package com.shnupbups.extrapieces.register;

import com.shnupbups.extrapieces.ExtraPieces;
import com.shnupbups.extrapieces.blocks.PieceBlock;
import com.shnupbups.extrapieces.core.PieceSet;
import com.shnupbups.extrapieces.core.PieceSets;
import com.shnupbups.extrapieces.core.PieceType;
import io.github.vampirestudios.artifice.api.ArtificeResourcePack;
import io.github.vampirestudios.artifice.api.builder.data.TagBuilder;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class ModTags {

	public static void init(ArtificeResourcePack.ServerResourcePackBuilder data) {
		HashMap<PieceType, HashSet<Identifier>> map = new HashMap<>();

		for (PieceSet set : PieceSets.registry.values()) {
			for (PieceBlock pieceBlock : set.getPieceBlocks()) {
				HashSet<Identifier> identifiers = map.computeIfAbsent(pieceBlock.getType(), ty -> new HashSet<>());
				Block block = pieceBlock.getBlock();

				if (block instanceof PieceBlock) {
					identifiers.add(Registries.BLOCK.getId(block));
				}
			}
		}

		for (Map.Entry<PieceType, HashSet<Identifier>> entry : map.entrySet()) {
			PieceType type = entry.getKey();
			HashSet<Identifier> identifiers = entry.getValue();

			data.addTag("block", entry.getKey().getTagId(), new TagBuilder<Block>()
					.replace(false)
					.values(identifiers.toArray(new Identifier[0]))
			);
			data.addTag("item", entry.getKey().getTagId(), new TagBuilder<Item>()
					.replace(false)
					.values(identifiers.toArray(new Identifier[0]))
			);

			ExtraPieces.debugLog("Added block and item tags for " + type + ", " + identifiers.size() + " entries.");
		}
	}
}
