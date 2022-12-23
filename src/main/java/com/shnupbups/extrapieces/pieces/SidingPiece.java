package com.shnupbups.extrapieces.pieces;

import com.google.gson.JsonObject;
import com.shnupbups.extrapieces.ExtraPieces;
import com.shnupbups.extrapieces.blocks.PieceBlock;
import com.shnupbups.extrapieces.blocks.SidingPieceBlock;
import com.shnupbups.extrapieces.core.PieceSet;
import com.shnupbups.extrapieces.core.PieceType;
import com.shnupbups.extrapieces.core.PieceTypes;
import com.shnupbups.extrapieces.recipe.ShapedPieceRecipe;
import com.shnupbups.extrapieces.register.ModProperties;
import io.github.vampirestudios.artifice.api.ArtificeResourcePack;
import io.github.vampirestudios.artifice.api.builder.TypedJsonObject;
import io.github.vampirestudios.artifice.api.builder.assets.BlockStateBuilder;
import io.github.vampirestudios.artifice.api.builder.data.LootTableBuilder;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;

public class SidingPiece extends PieceType {
	public SidingPiece() {
		super("siding");
	}

	public SidingPieceBlock getNew(PieceSet set) {
		return new SidingPieceBlock(set);
	}

	public ArrayList<ShapedPieceRecipe> getShapedRecipes() {
		ArrayList<ShapedPieceRecipe> recipes = super.getShapedRecipes();
		recipes.add(new ShapedPieceRecipe(this, 6, "b", "b", "b").addToKey('b', PieceTypes.BASE));
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
		addBlockModel(pack, pb, "double");
	}

	public void addBlockstate(ArtificeResourcePack.ClientResourcePackBuilder pack, PieceBlock pb) {
		BlockStateBuilder builder = new BlockStateBuilder();
		for (ModProperties.SidingType t : ModProperties.SidingType.values()) {
			switch (t) {
				case SINGLE -> {
					for (Direction d : Direction.values()) {
						if (!(d.equals(Direction.DOWN) || d.equals(Direction.UP))) {
							BlockStateBuilder.Variant variant = new BlockStateBuilder.Variant()
									.uvlock(true)
									.model(getModelPath(pb));
							switch (d) {
								case EAST -> variant.rotationY(90);
								case WEST -> variant.rotationY(270);
								case SOUTH -> variant.rotationY(180);
							}
							builder.variant("type=" + t.asString() + ",facing=" + d.asString(), variant);
						}
					}
				}
				case DOUBLE -> builder.variant("type=" + t.asString(), new BlockStateBuilder.Variant()
						.model(getModelPath(pb, "double"))
				);
			}

		}
		pack.addBlockState(Registries.BLOCK.getId(pb.getBlock()), builder);
	}
}
