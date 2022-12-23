package com.shnupbups.extrapieces.pieces;

import com.google.gson.JsonObject;
import com.shnupbups.extrapieces.ExtraPieces;
import com.shnupbups.extrapieces.blocks.LayerPieceBlock;
import com.shnupbups.extrapieces.blocks.PieceBlock;
import com.shnupbups.extrapieces.core.PieceSet;
import com.shnupbups.extrapieces.core.PieceType;
import com.shnupbups.extrapieces.core.PieceTypes;
import com.shnupbups.extrapieces.recipe.ShapedPieceRecipe;
import io.github.vampirestudios.artifice.api.ArtificeResourcePack;
import io.github.vampirestudios.artifice.api.builder.TypedJsonObject;
import io.github.vampirestudios.artifice.api.builder.assets.BlockStateBuilder;
import io.github.vampirestudios.artifice.api.builder.assets.ModelBuilder;
import io.github.vampirestudios.artifice.api.builder.data.LootTableBuilder;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;

public class LayerPiece extends PieceType {
	public LayerPiece() {
		super("layer");
	}

	public LayerPieceBlock getNew(PieceSet set) {
		return new LayerPieceBlock(set);
	}

	public ArrayList<ShapedPieceRecipe> getShapedRecipes() {
		ArrayList<ShapedPieceRecipe> recipes = super.getShapedRecipes();
		recipes.add(new ShapedPieceRecipe(this, 12, "bbb").addToKey('b', PieceTypes.SLAB));
		return recipes;
	}

	public void addBlockModels(ArtificeResourcePack.ClientResourcePackBuilder pack, PieceBlock pb) {
		addBlockModel(pack, pb, "height_2");
		addBlockModel(pack, pb, "height_4");
		addBlockModel(pack, pb, "height_6");
		addBlockModel(pack, pb, "height_8");
		addBlockModel(pack, pb, "height_10");
		addBlockModel(pack, pb, "height_12");
		addBlockModel(pack, pb, "height_14");
		addBlockModel(pack, pb, "height_16");
	}

	public void addItemModel(ArtificeResourcePack.ClientResourcePackBuilder pack, PieceBlock pb) {
		pack.addItemModel(Registries.BLOCK.getId(pb.getBlock()), new ModelBuilder()
				.parent(getModelPath(pb, "height_2"))
		);
	}

	public void addBlockstate(ArtificeResourcePack.ClientResourcePackBuilder pack, PieceBlock pb) {
		BlockStateBuilder builder = new BlockStateBuilder();
		for(Direction dir : Direction.values()) {
			for (int i = 1; i <= 8; i++) {
				final int j = i * 2;
				BlockStateBuilder.Variant variant = new BlockStateBuilder.Variant()
						.model(getModelPath(pb, "height_" + j))
						.uvlock(true);
				switch (dir) {
					case DOWN -> {
						variant.rotationX(180);
						return;
					}
					case NORTH -> {
						variant.rotationX(90);
						return;
					}
					case SOUTH -> {
						variant.rotationX(90);
						variant.rotationY(180);
						return;
					}
					case EAST -> {
						variant.rotationX(90);
						variant.rotationY(90);
						return;
					}
					case WEST -> {
						variant.rotationX(90);
						variant.rotationY(270);
					}
				}
				builder.variant("facing=" + dir.asString() + ",layers=" + i, variant);
			}
		}
		pack.addBlockState(Registries.BLOCK.getId(pb.getBlock()), builder);
	}

	public int getStonecuttingCount() {
		return 8;
	}

	@Override
	public void addLootTable(ArtificeResourcePack.ServerResourcePackBuilder data, PieceBlock pb) {
		LootTableBuilder builder = new LootTableBuilder().type(new Identifier("block"));
		LootTableBuilder.Pool pool = new LootTableBuilder.Pool().rolls(1);
		LootTableBuilder.Pool.Entry entry = new LootTableBuilder.Pool.Entry()
				.type(new Identifier("item"))
				.name(Registries.BLOCK.getId(pb.getBlock()));
		for (int i = 1; i <= 8; i++) {
			LootTableBuilder.Pool.Entry.Function function = new LootTableBuilder.Pool.Entry.Function(new JsonObject());
			TypedJsonObject condition = new TypedJsonObject()
					.add("block", Registries.BLOCK.getId(pb.getBlock()).toString())
					.add("properties", new TypedJsonObject().add("layers", i));
			function.condition(new Identifier("block_state_property"), condition);
			function.add("count", 3);
			entry.function(new Identifier("set_count"), function);
		}
		entry.function(new Identifier("explosion_decay"), new LootTableBuilder.Pool.Entry.Function(new JsonObject()));
		pool.entry(entry);
		builder.pool(pool);
		data.addLootTable(ExtraPieces.prependToPath(Registries.BLOCK.getId(pb.getBlock()), "blocks/"), builder);
	}
}
