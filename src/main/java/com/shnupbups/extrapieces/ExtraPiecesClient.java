package com.shnupbups.extrapieces;

import com.shnupbups.extrapieces.register.ModModels;
import com.shnupbups.extrapieces.register.ModRenderLayers;
import io.github.vampirestudios.artifice.api.Artifice;
import io.github.vampirestudios.artifice.api.ArtificeResourcePack;
import net.fabricmc.api.ClientModInitializer;

public class ExtraPiecesClient implements ClientModInitializer {

	public void onInitializeClient() {
		//try {
		Artifice.registerAssets(ExtraPieces.getID("ep_assets"), ArtificeResourcePack.ofAssets(assets -> {
			ModModels.init(assets);
			assets.setDescription("Assets necessary for Extra Pieces.");
		}))/*.dumpResources(FabricLoader.getInstance().getConfigDirectory().getParent()+"/dump")*/;
		/*} catch(Exception e) {
			ExtraPieces.log("BIG OOF: "+e.getMessage());
		}*/

		ModRenderLayers.init();
	}
}
