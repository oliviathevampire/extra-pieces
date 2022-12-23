package com.shnupbups.extrapieces.pieces;

import com.google.gson.JsonObject;
import com.shnupbups.extrapieces.ExtraPieces;
import com.shnupbups.extrapieces.blocks.PieceBlock;
import com.shnupbups.extrapieces.blocks.SlabPieceBlock;
import com.shnupbups.extrapieces.core.PieceSet;
import com.shnupbups.extrapieces.core.PieceType;
import com.shnupbups.extrapieces.core.PieceTypes;
import com.shnupbups.extrapieces.recipe.ShapedPieceRecipe;
import io.github.vampirestudios.artifice.api.ArtificeResourcePack;
import io.github.vampirestudios.artifice.api.builder.TypedJsonObject;
import io.github.vampirestudios.artifice.api.builder.assets.BlockStateBuilder;
import io.github.vampirestudios.artifice.api.builder.data.LootTableBuilder;
import net.minecraft.block.enums.SlabType;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.ArrayList;

public class SlabPiece extends PieceType {
	public SlabPiece() {
		super("slab");
	}

	public SlabPieceBlock getNew(PieceSet set) {
		return new SlabPieceBlock(set);
	}

	public Identifier getTagId() {
		return new Identifier("minecraft", "slabs");
	}

	public ArrayList<ShapedPieceRecipe> getShapedRecipes() {
		ArrayList<ShapedPieceRecipe> recipes = super.getShapedRecipes();
		recipes.add(new ShapedPieceRecipe(this, 6, "bbb").addToKey('b', PieceTypes.BASE));
		return recipes;
	}

	public int getStonecuttingCount() {
		return 2;
	}

	@Override
	public void addLootTable(ArtificeResourcePack.ServerResourcePackBuilder data, PieceBlock pb) {
		LootTableBuilder builder = new LootTableBuilder()
				.type(new Identifier("block"));
		LootTableBuilder.Pool pool = new LootTableBuilder.Pool()
				.rolls(1);

		LootTableBuilder.Pool.Entry entry = new LootTableBuilder.Pool.Entry()
				.type(new Identifier("item"))
				.name(Registries.BLOCK.getId(pb.getBlock()));
		LootTableBuilder.Pool.Entry.Function function = new LootTableBuilder.Pool.Entry.Function(new JsonObject());
		function.add("count", 2);
		TypedJsonObject condition = new TypedJsonObject()
				.add("block", Registries.BLOCK.getId(pb.getBlock()).toString())
				.add("properties", new TypedJsonObject().add("type", "double"));
		function.condition(new Identifier("block_state_property"), condition);
		entry.function(new Identifier("set_count"), function);
		entry.function(new Identifier("explosion_decay"), new LootTableBuilder.Pool.Entry.Function(new JsonObject()));
		builder.pool(pool);
		data.addLootTable(ExtraPieces.prependToPath(Registries.BLOCK.getId(pb.getBlock()), "blocks/"), builder);
	}

	public void addBlockModels(ArtificeResourcePack.ClientResourcePackBuilder pack, PieceBlock pb) {
		super.addBlockModels(pack, pb);
		addBlockModel(pack, pb, "top");
		addBlockModel(pack, pb, "double");
	}

	public void addBlockstate(ArtificeResourcePack.ClientResourcePackBuilder pack, PieceBlock pb) {
		BlockStateBuilder builder = new BlockStateBuilder();
		for (SlabType t : SlabType.values()) {
			BlockStateBuilder.Variant variant = new BlockStateBuilder.Variant();
			switch (t) {
				case BOTTOM -> variant.model(getModelPath(pb));
				case TOP -> variant.model(getModelPath(pb, "top"));
				case DOUBLE -> variant.model(getModelPath(pb, "double"));
			}
			builder.variant("type=" + t.asString(), variant);
		}
		pack.addBlockState(Registries.BLOCK.getId(pb.getBlock()), builder);
	}
}
