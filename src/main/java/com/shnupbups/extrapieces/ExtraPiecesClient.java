package com.shnupbups.extrapieces;

import com.shnupbups.extrapieces.register.ModModels;
import com.shnupbups.extrapieces.register.ModRenderLayers;
import io.github.vampirestudios.artifice.api.Artifice;
import net.fabricmc.api.ClientModInitializer;

public class ExtraPiecesClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		Artifice.registerAssetPack(ExtraPieces.getID("ep_assets"), register -> {
			ModModels.init(register);
			register.setDescription("Assets necessary for Extra Pieces.");
		});
		ModRenderLayers.init();
	}
}
