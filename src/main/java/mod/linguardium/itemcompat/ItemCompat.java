package mod.linguardium.itemcompat;

import net.fabricmc.api.ModInitializer;



public class ItemCompat implements ModInitializer {
	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		ShearsTag.init();
	}

}
