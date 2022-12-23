package com.shnupbups.extrapieces.core;

import com.ibm.icu.text.Normalizer;
import com.shnupbups.extrapieces.ExtraPieces;
import com.shnupbups.extrapieces.blocks.PieceBlock;
import com.shnupbups.extrapieces.blocks.PieceBlockItem;

import com.shnupbups.extrapieces.recipe.*;
import io.github.vampirestudios.artifice.api.ArtificeResourcePack;
import io.github.vampirestudios.artifice.api.builder.TypedJsonObject;
import io.github.vampirestudios.artifice.api.builder.assets.BlockStateBuilder;
import io.github.vampirestudios.artifice.api.builder.assets.ModelBuilder;
import io.github.vampirestudios.artifice.api.builder.data.LootTableBuilder;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;

import java.util.ArrayList;
import java.util.function.Consumer;

public abstract class PieceType {
	private final Identifier id;

	public PieceType(String id) {
		this(ExtraPieces.getID(id));
	}

	public PieceType(Identifier id) {
		this.id = id;
	}

	/**
	 * Gets the name of this {@link PieceType} with {@code baseName_} appended to the front, in all lowercase.<br>
	 * Used for registry.
	 *
	 * @return The name of this {@link PieceType}, in all lowercase.
	 */
	public String getBlockId(String baseName) {
		return baseName.toLowerCase() + "_" + getId().getPath();
	}

	/**
	 * Gets the id of this {@link PieceType}<br>
	 * Used for registry.
	 *
	 * @return The id of this {@link PieceType}
	 */
	public Identifier getId() {
		return this.id;
	}

	public String getTranslationKey() {
		return "piece." + id.getNamespace() + "." + id.getPath();
	}

	/**
	 * Gets the id of the block and item tag of this {@link PieceType}<br>
	 * Used for registry.<br>
	 * Defaults to {@link #getId()} wth an 's' appended
	 *
	 * @return The id of this {@link PieceType}'s tag
	 */
	public Identifier getTagId() {
		return new Identifier(this.id.toString() + "s");
	}

	public abstract PieceBlock getNew(PieceSet set);

	public PacketByteBuf writePieceType(PacketByteBuf buf) {
		buf.writeInt(getId().toString().length());
		buf.writeString(getId().toString());
		return buf;
	}

	public String toString() {
		return getId().toString();
	}

	@Deprecated
	/**
	 * Use {@link #getShapedRecipes()} or {@link #getCraftingRecipes()}
	 */
	public ArrayList<ShapedPieceRecipe> getRecipes() {
		return getShapedRecipes();
	}

	public ArrayList<ShapedPieceRecipe> getShapedRecipes() {
		return new ArrayList<>();
	}

	public ArrayList<ShapelessPieceRecipe> getShapelessRecipes() {
		return new ArrayList<>();
	}

	public ArrayList<PieceRecipe> getCraftingRecipes() {
		ArrayList<PieceRecipe> recipes = new ArrayList<>();
		recipes.addAll(getRecipes());
		recipes.addAll(getShapelessRecipes());
		return recipes;
	}

	public StonecuttingPieceRecipe getStonecuttingRecipe() {
		return new StonecuttingPieceRecipe(this, getStonecuttingCount(), PieceTypes.BASE);
	}

	public WoodmillingPieceRecipe getWoodmillingRecipe() {
		return new WoodmillingPieceRecipe(this, getStonecuttingCount(), PieceTypes.BASE);
	}

	public int getStonecuttingCount() {
		return 1;
	}

	public void addLootTable(ArtificeResourcePack.ServerResourcePackBuilder data, PieceBlock pb) {
		LootTableBuilder loot = new LootTableBuilder()
				.type(new Identifier("block"))
				.pool(new LootTableBuilder.Pool()
						.rolls(1)
						.entry(new LootTableBuilder.Pool.Entry()
								.type(new Identifier("item"))
								.name(Registries.BLOCK.getId(pb.getBlock()))
								.condition(new Identifier("survives_explosion"), new TypedJsonObject())
						)
				);
		data.addLootTable(ExtraPieces.prependToPath(Registries.BLOCK.getId(pb.getBlock()), "blocks/"), loot);
	}


	public void addModels(ArtificeResourcePack.ClientResourcePackBuilder pack, PieceBlock pb) {
		addBlockModels(pack, pb);
		addItemModel(pack, pb);
	}

	public void addBlockModels(ArtificeResourcePack.ClientResourcePackBuilder pack, PieceBlock pb) {
		ModelBuilder model = new ModelBuilder();
		model.parent(ExtraPieces.prependToPath(this.getId(), "block/dummy_"));
		model.texture("particle", pb.getSet().getMainTexture());
		model.texture("main", pb.getSet().getMainTexture());
		model.texture("top", pb.getSet().getTopTexture());
		model.texture("bottom", pb.getSet().getBottomTexture());
		pack.addBlockModel(Registries.BLOCK.getId(pb.getBlock()), model);
	}

	public void addItemModel(ArtificeResourcePack.ClientResourcePackBuilder pack, PieceBlock pb) {
		ModelBuilder model = new ModelBuilder()
				.parent(ExtraPieces.prependToPath(Registries.BLOCK.getId(pb.getBlock()), "block/"));
		pack.addItemModel(Registries.BLOCK.getId(pb.getBlock()), model);
	}

	public void addBlockModel(ArtificeResourcePack.ClientResourcePackBuilder pack, PieceBlock pb, String append) {
		ModelBuilder model = new ModelBuilder()
				.parent(ExtraPieces.prependToPath(ExtraPieces.appendToPath(this.getId(), "_" + append), "block/dummy_"))
				.texture("particle", pb.getSet().getMainTexture())
				.texture("main", pb.getSet().getMainTexture())
				.texture("top", pb.getSet().getTopTexture())
				.texture("bottom", pb.getSet().getBottomTexture());
		pack.addBlockModel(ExtraPieces.appendToPath(Registries.BLOCK.getId(pb.getBlock()), "_" + append), model);
	}

	public void addBlockstate(ArtificeResourcePack.ClientResourcePackBuilder pack, PieceBlock pb) {
		BlockStateBuilder state = new BlockStateBuilder()
				.variant("", new BlockStateBuilder.Variant().model(ExtraPieces.prependToPath(Registries.BLOCK.getId(pb.getBlock()), "block/")));
		pack.addBlockState(Registries.BLOCK.getId(pb.getBlock()), state);

	}

	public PieceBlockItem getBlockItem(PieceBlock pb) {
		return new PieceBlockItem(pb, new Item.Settings());
	}
	
	public Identifier getModelPath(PieceBlock pb) {
		return ExtraPieces.prependToPath(Registries.BLOCK.getId(pb.getBlock()), "block/");
	}
	
	public Identifier getModelPath(PieceBlock pb, String append) {
		return ExtraPieces.prependToPath(ExtraPieces.appendToPath(Registries.BLOCK.getId(pb.getBlock()), "_"+append), "block/");
	}
}

