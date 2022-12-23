package com.shnupbups.extrapieces;

import com.shnupbups.extrapieces.api.EPInitializer;
import com.shnupbups.extrapieces.core.PieceSet;
import com.shnupbups.extrapieces.core.PieceSets;
import com.shnupbups.extrapieces.core.PieceTypes;
import com.shnupbups.extrapieces.debug.DebugItem;
import com.shnupbups.extrapieces.register.ModBlocks;
import com.shnupbups.extrapieces.register.ModConfigs;
import io.github.vampirestudios.artifice.api.Artifice;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class ExtraPieces implements ModInitializer {
	public static final String mod_id = "extrapieces";
	public static final String mod_name = "Extra Pieces";
	public static final String piece_pack_version = "2.9.0";
	public static final Logger logger = LogManager.getFormatterLogger(mod_name);

	public static File configDir;
	public static File ppDir;

	public static Identifier getID(String path) {
		return new Identifier(mod_id, path);
	}

	public static void log(String out) {
		logger.info("[" + mod_name + "] " + out);
	}
	
	public static void debugLog(String out) {
		if(ModConfigs.debugOutput) log("[DEBUG] "+out);
	}
	
	public static void moreDebugLog(String out) {
		if(ModConfigs.moreDebugOutput) debugLog(out);
	}

	public static Identifier prependToPath(Identifier id, String prep) {
		return new Identifier(id.getNamespace(), prep + id.getPath());
	}

	public static Identifier appendToPath(Identifier id, String app) {
		return new Identifier(id.getNamespace(), id.getPath() + app);
	}

	public static File getConfigDirectory() {
		if (configDir == null) {
			configDir = new File(FabricLoader.getInstance().getConfigDir().toFile(), mod_id);
			configDir.mkdirs();
		}
		return configDir;
	}

	public static File getPiecePackDirectory() {
		if (ppDir == null) {
			ppDir = new File(getConfigDirectory(), "piecepacks");
			ppDir.mkdirs();
		}
		return ppDir;
	}

	public static boolean isWoodmillInstalled() {
		return FabricLoader.getInstance().isModLoaded("woodmill");
	}

	@Override
	public void onInitialize() {
		ModConfigs.init();
		PieceTypes.init();
		FabricLoader.getInstance().getEntrypoints("extrapieces", EPInitializer.class).forEach(api -> {
			debugLog("EPInitializer " + api.toString());
			api.onInitialize();
		});
		ModConfigs.initPiecePacks();
		Artifice.registerDataPack(getID("ep_data"), serverResourcePackBuilder -> {
			ModBlocks.init(serverResourcePackBuilder);
			try {
				serverResourcePackBuilder.dump(FabricLoader.getInstance().getConfigDir().getParent() + "/dump", "data", ModConfigs.dumpData && ModBlocks.finished);
			} catch (Exception e) {
				ExtraPieces.debugLog("BIG OOF: " + e.getMessage());
			}
		});
		Registry.register(Registries.ITEM, getID("debug_item"), new DebugItem());

		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			if (ModBlocks.setBuilders.size() != PieceSets.registry.size()) {
				for (PieceSet.Builder psb : ModBlocks.setBuilders.values()) {
					if (!psb.isBuilt())
						System.out.println("Piece Set " + psb + " could not be built, make sure the base and any vanilla pieces actually exist!");
				}
			}
		});
	}
}
