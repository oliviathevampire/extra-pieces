package com.shnupbups.extrapieces.core;

import blue.endless.jankson.JsonArray;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.JsonPrimitive;
import com.shnupbups.extrapieces.ExtraPieces;
import com.shnupbups.extrapieces.blocks.FakePieceBlock;
import com.shnupbups.extrapieces.blocks.PieceBlock;
import io.github.cottonmc.cotton.datapack.tags.TagEntryManager;
import io.github.cottonmc.cotton.datapack.tags.TagType;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.*;

public class PieceSet {
	public static final ArrayList<PieceType> NO_SLAB;
	public static final ArrayList<PieceType> NO_SLAB_OR_STAIRS;
	public static final ArrayList<PieceType> NO_SLAB_STAIRS_OR_WALL;
	public static final ArrayList<PieceType> JUST_EXTRAS_AND_WALL;
	public static final ArrayList<PieceType> JUST_EXTRAS_AND_FENCE_GATE;

	private final Block base;
	private final String name;
	private PieceType[] genTypes;
	private Map<PieceType, PieceBlock> pieces = new HashMap<>();
	private boolean registered = false;
	private boolean stonecuttable;
	private ArrayList<PieceType> uncraftable = new ArrayList<>();
	private Identifier mainTexture;
	private Identifier topTexture;
	private Identifier bottomTexture;
	private boolean opaque;

	PieceSet(Block base, String name, List<PieceType> types) {
		this.base = base;
		this.name = name.toLowerCase();
		Identifier id = Registry.BLOCK.getId(base);
		this.mainTexture = new Identifier(id.getNamespace(), "block/"+id.getPath());
		this.topTexture = mainTexture;
		this.bottomTexture = topTexture;
		this.opaque = base.isOpaque(base.getDefaultState());
		this.genTypes = types.toArray(new PieceType[types.size()]);
		PieceSets.registerSet(base, this);
		this.stonecuttable = (base.getDefaultState().getMaterial().equals(Material.STONE) || base.getDefaultState().getMaterial().equals(Material.METAL));
	}

	public PieceSet setOpaque() {
		this.opaque = true;
		return this;
	}

	public PieceSet setOpaque(boolean opaque) {
		this.opaque = opaque;
		return this;
	}

	public PieceSet setTransparent() {
		this.opaque = false;
		return this;
	}

	public PieceSet setTexture(Identifier id) {
		Identifier newId = new Identifier(id.getNamespace(),(id.getPath().contains("block/")?id.getPath():"block/"+id.getPath()));
		this.mainTexture = newId;
		this.topTexture = newId;
		this.bottomTexture = newId;
		return this;
	}

	public PieceSet setTopTexture(Identifier id) {
		Identifier newId = new Identifier(id.getNamespace(),(id.getPath().contains("block/")?id.getPath():"block/"+id.getPath()));
		this.topTexture = newId;
		this.bottomTexture = newId;
		return this;
	}

	public PieceSet setBottomTexture(Identifier id) {
		Identifier newId = new Identifier(id.getNamespace(),(id.getPath().contains("block/")?id.getPath():"block/"+id.getPath()));
		this.bottomTexture = newId;
		return this;
	}

	public PieceSet setTexture(String id) {
		return setTexture(new Identifier(id));
	}

	public PieceSet setTopTexture(String id) {
		return setTopTexture(new Identifier(id));
	}

	public PieceSet setBottomTexture(String id) {
		return setBottomTexture(new Identifier(id));
	}

	public boolean isOpaque() {
		return opaque;
	}

	public boolean isTransparent() {
		return !isOpaque();
	}

	public Identifier getMainTexture() {
		return mainTexture;
	}

	public Identifier getTopTexture() {
		return topTexture;
	}

	public Identifier getBottomTexture() {
		return bottomTexture;
	}

	public boolean hasCustomTexture() {
		return hasBottomTexture()||hasTopTexture()||hasMainTexture();
	}

	public boolean hasMainTexture() {
		Identifier id = Registry.BLOCK.getId(base);
		Identifier def = new Identifier(id.getNamespace(), "block/"+id.getPath());
		return !getMainTexture().equals(def);
	}

	public boolean hasTopTexture() {
		return !getTopTexture().equals(getMainTexture());
	}

	public boolean hasBottomTexture() {
		return !getBottomTexture().equals(getTopTexture());
	}

	/**
	 * Adds a vanilla (or just not-generated) block as a piece to this set.
	 * @param type The piece type of the block
	 * @param block The block to add
	 * @return This set
	 */
	public PieceSet addVanillaPiece(PieceType type, Block block) {
		FakePieceBlock fpb = new FakePieceBlock(block, type, this);
		PieceSets.registerVanillaPiece(block, fpb);
		this.excludePiece(type);
		pieces.put(type, fpb);
		setUncraftable(type);
		//System.out.println("ADDING VANILLA PIECE! "+block+" as type "+type+" to set "+getName()+" with base "+getBase());
		return this;
	}

	public boolean isVanillaPiece(PieceType type) { return pieces.get(type) instanceof FakePieceBlock && PieceSets.isVanillaPiece(pieces.get(type).getBlock()); }

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("PieceSet{ base: ").append(getBase()).append(", pieces: [");
		for(PieceType p:pieces.keySet()) {
			sb.append("{").append(p.toString()).append(" = ").append(pieces.get(p).getBlock());
			if(isVanillaPiece(p)) sb.append(", Vanilla Piece!");
			else if(!isCraftable(p)) sb.append(", Uncraftable!");
			sb.append("} , ");
		}
		sb.delete(sb.length()-2,sb.length());
		sb.append("] ");
		if(isStonecuttable()) sb.append(", Stonecuttable! ");
		if(isTransparent()) sb.append(", Transparent! ");
		if(hasMainTexture()) sb.append(", Main Texture = ").append(getMainTexture()).append(" ");
		if(hasTopTexture()) sb.append(", Top Texture = ").append(getTopTexture()).append(" ");
		if(hasBottomTexture()) sb.append(", Bottom Texture = ").append(getBottomTexture()).append(" ");
		sb.append("}");
		return sb.toString();
	}

	public boolean isStonecuttable() {
		return stonecuttable;
	}

	public PieceSet setStonecuttable(boolean stonecuttable) {
		this.stonecuttable=stonecuttable;
		return this;
	}

	public boolean isCraftable(PieceType type) {
		return !uncraftable.contains(type);
	}

	public PieceSet setUncraftable(PieceType type) {
		uncraftable.add(type);
		return this;
	}

	/**
	 * Creates the instances of each {@link PieceType} in this {@link PieceSet}.
	 * @return This {@link PieceSet} with all {@link PieceType}s generated.
	 */
	public PieceSet generate() {
		for (PieceType p: PieceType.getTypes()) {
			if(shouldGenPiece(p) && !hasPiece(p)) {
				pieces.put(p, (PieceBlock)p.getNew(this));
			}
		}
		return this;
	}

	/**
	 * Registers each {@link PieceType} in this {@link PieceSet} to the {@link Registry}.<br>
	 * If {@link #isGenerated()} returns {@code false}, runs {@link #generate()}.
	 * @throws IllegalStateException If a {@link PieceSet} has already been registered with the same base {@link Block}
	 * @return This {@link PieceSet}
	 */
	public PieceSet register() {
		if(isRegistered()) throw new IllegalStateException("Base block "+base.getTranslationKey()+" already has PiecesSet registered! Cannot register again!");
		if(!isGenerated()) generate();
		for(PieceType b : genTypes) {
			Identifier id = new Identifier(b.getId().getNamespace(),b.getBlockId(getName()));
			Registry.register(Registry.BLOCK, id, pieces.get(b).getBlock());
			BlockItem item = new BlockItem(pieces.get(b).getBlock(), (new Item.Settings()).group(ExtraPieces.groups.get(b)));
			item.appendBlocks(Item.BLOCK_ITEMS, item);
			Registry.register(Registry.ITEM, Registry.BLOCK.getId(pieces.get(b).getBlock()), item);
		}
		registered = true;
		//System.out.println("DEBUG! PieceSet register: "+this.toString());
		return this;
	}

	/**
	 * Gets the name of this {@link PieceSet}.<br>
	 * Used for registry.
	 * @return The name of this {@link PieceSet}.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets a {@link PieceType} from this {@link PieceSet}, if it exists.<br>
	 * If {@link #isGenerated()} returns {@code false}, runs {@link #generate()} first.
	 * @param piece The {@link PieceType} to get.
	 * @return The {@link PieceType} from this {@link PieceSet}, or null if no such PieceType exists.
	 */
	public Block getPiece(PieceType piece) {
		if(!isGenerated()) generate();
		if(hasPiece(piece)) return pieces.get(piece).getBlock();
		return null;
	}

	/**
	 * Gets the {@link Block} which this {@link PieceSet} is based upon.
	 * @return The {@link Block} which this {@link PieceSet} is based upon.
	 */
	public Block getBase() {
		return base;
	}

	private boolean shouldGenPiece(PieceType piece) {
		return Arrays.asList(genTypes).contains(piece);
	}

	/**
	 * Gets whether this {@link PieceSet} has a {@link PieceType} of type {@code piece}.
	 * @param piece The {@link PieceType} to query for.
	 * @return Whether this {@link PieceSet} has a {@link PieceType} of type {@code piece}.
	 */
	public boolean hasPiece(PieceType piece) {
		return (pieces.containsKey(piece));
	}

	public ArrayList<PieceType> getPieceTypes() {
		return new ArrayList<>(pieces.keySet());
	}

	public ArrayList<PieceBlock> getPieceBlocks() {
		return new ArrayList<>(pieces.values());
	}

	public Map<PieceType, PieceBlock> getPieces() {
		return pieces;
	}

	/**
	 * Gets whether each {@link PieceType} for this {@link PieceSet} has been generated.<br>
	 * Generation is done with {@link #generate()}.
	 * @return Whether each {@link PieceType} for this {@link PieceSet} has been generated.
	 */
	public boolean isGenerated() {
		for(PieceType p : genTypes) {
			if(!pieces.containsKey(p)) return false;
		}
		return true;
	}

	/**
	 * Gets whether this {@link PieceSet}'s {@link PieceType}s have been added to the {@link Registry}.<br>
	 * Registration is done with {@link #register()}.
	 * @return Whether this {@link PieceSet}'s {@link PieceType}s have been added to the {@link Registry}.
	 */
	public boolean isRegistered() {
		return registered;
	}

	public List<PieceType> getGenTypes() {
		return Arrays.asList(genTypes);
	}

	public List<PieceType> getVanillaTypes() {
		ArrayList<PieceType> vt = new ArrayList<>();
		List gt = Arrays.asList(genTypes);
		for(PieceType p:this.getPieces().keySet()) {
			if (!gt.contains(p)) {
				vt.add(p);
			}
		}
		return vt;
	}

	public List<PieceType> getExcludedTypes() {
		ArrayList et = (ArrayList<PieceType>)PieceType.getTypesNoBase().clone();
		et.removeAll(this.getPieceTypes());
		return et;
	}

	public List<PieceType> getUncraftableTypes() {
		ArrayList uc = ((ArrayList<PieceType>)uncraftable.clone());
		uc.removeAll(this.getVanillaTypes());
		return uc;
	}

	public PieceSet excludePiece(PieceType type) {
		List<PieceType> types = Arrays.asList(this.genTypes);
		ArrayList<PieceType> newTypes = new ArrayList<>(types);
		newTypes.remove(type);
		this.genTypes = newTypes.toArray(new PieceType[newTypes.size()]);
		return this;
	}

	static {
		NO_SLAB = new ArrayList<>(PieceType.getTypesNoBase());
		NO_SLAB.remove(PieceType.SLAB);

		NO_SLAB_OR_STAIRS = new ArrayList<>(NO_SLAB);
		NO_SLAB_OR_STAIRS.remove(PieceType.STAIRS);

		NO_SLAB_STAIRS_OR_WALL = new ArrayList<>(NO_SLAB_OR_STAIRS);
		NO_SLAB_STAIRS_OR_WALL.remove(PieceType.WALL);

		JUST_EXTRAS_AND_WALL = new ArrayList<>(NO_SLAB_OR_STAIRS);
		JUST_EXTRAS_AND_WALL.remove(PieceType.FENCE);
		JUST_EXTRAS_AND_WALL.remove(PieceType.FENCE_GATE);

		JUST_EXTRAS_AND_FENCE_GATE = new ArrayList<>(NO_SLAB_STAIRS_OR_WALL);
		JUST_EXTRAS_AND_FENCE_GATE.remove(PieceType.FENCE);
	}

	public JsonObject toJson() {
		JsonObject ob = new JsonObject();
		ob.put("base", new JsonPrimitive(Registry.BLOCK.getId(this.getBase())));
		if(this.isStonecuttable()!=(base.getDefaultState().getMaterial().equals(Material.STONE) || base.getDefaultState().getMaterial().equals(Material.METAL))) {
			ob.put("stonecuttable", new JsonPrimitive(this.isStonecuttable()));
		}
		if(this.isOpaque()!=this.getBase().getDefaultState().isOpaque()) {
			ob.put("opaque", new JsonPrimitive(this.isOpaque()));
		}
		if(this.hasCustomTexture()) {
			JsonObject tx = new JsonObject();
			if(hasMainTexture()) {
				tx.put("main", new JsonPrimitive(this.getMainTexture()));
			}
			if(hasTopTexture()) {
				tx.put("top", new JsonPrimitive(this.getTopTexture()));
			}
			if(hasBottomTexture()) {
				tx.put("bottom", new JsonPrimitive(this.getBottomTexture()));
			}
			ob.put("textures", tx);
		}
		if(!this.getVanillaTypes().isEmpty()) {
			JsonObject vp = new JsonObject();
			for(PieceType p:this.getVanillaTypes()) {
				vp.put(p.toString(), new JsonPrimitive(Registry.BLOCK.getId(this.getPiece(p))));
			}
			ob.put("vanilla_pieces", vp);
		}
		if(!this.getUncraftableTypes().isEmpty()) {
			JsonArray uc = new JsonArray();
			for(PieceType p:this.getUncraftableTypes()) {
				uc.add(new JsonPrimitive(p));
			}
			ob.put("uncraftable", uc);
		}
		if(!this.getExcludedTypes().isEmpty()) {
			JsonArray ex = new JsonArray();
			for(PieceType p:this.getExcludedTypes()) {
				ex.add(new JsonPrimitive(p));
			}
			ob.put("exclude", ex);
		}
		return ob;
	}

	public static PieceSet fromJson(String name, JsonObject ob) {
		Block base = Registry.BLOCK.get(new Identifier(ob.get(String.class, "base")));
		PieceSet set = PieceSets.createSet(base, name);
		if(ob.containsKey("stonecuttable")) {
			set.setStonecuttable(Boolean.getBoolean(((JsonPrimitive)ob.get("stonecuttable")).asString()));
		}
		if(ob.containsKey("opaque")) {
			set.setOpaque(Boolean.getBoolean(((JsonPrimitive)ob.get("opaque")).asString()));
		}
		if(ob.containsKey("textures")) {
			JsonObject tx = ob.getObject("textures");
			if(tx.containsKey("main")) {
				set.setTexture(new Identifier(tx.get(String.class, "main")));
			}
			if(tx.containsKey("top")) {
				set.setTopTexture(new Identifier(tx.get(String.class, "top")));
			}
			if(tx.containsKey("bottom")) {
				set.setBottomTexture(new Identifier(tx.get(String.class, "bottom")));
			}
		}
		if(ob.containsKey("vanilla_pieces")) {
			JsonObject vp = ob.getObject("vanilla_pieces");
			for(String s:vp.keySet()) {
				PieceType pt = PieceType.getType(s);
				set.addVanillaPiece(pt, Registry.BLOCK.get(new Identifier(vp.get(String.class, s))));
			}
		}
		if(ob.containsKey("exclude")) {
			JsonArray ex = ob.get(JsonArray.class, "exclude");
			for(JsonElement je:ex) {
				JsonPrimitive jp = (JsonPrimitive)je;
				String s = jp.asString();
				PieceType pt = PieceType.getType(s);
				set.excludePiece(pt);
			}
		}
		if(ob.containsKey("uncraftable")) {
			JsonArray uc = ob.get(JsonArray.class, "uncraftable");
			for(JsonElement je:uc) {
				JsonPrimitive jp = (JsonPrimitive)je;
				String s = jp.asString();
				PieceType pt = PieceType.getType(s);
				set.setUncraftable(pt);
			}
		}
		return set;
	}
}
