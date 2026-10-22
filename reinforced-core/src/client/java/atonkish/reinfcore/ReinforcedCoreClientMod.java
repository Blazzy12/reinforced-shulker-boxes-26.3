package atonkish.reinfcore;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;

import atonkish.reinfcore.api.ReinforcedCoreClientModInitializer;

@Environment(EnvType.CLIENT)
public class ReinforcedCoreClientMod implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    // entrypoint: "reinfcore-client"
    FabricLoader.getInstance()
        .getEntrypoints(
            String.format("%s-client", ReinforcedCoreMod.MOD_ID),
            ReinforcedCoreClientModInitializer.class)
        .forEach(ReinforcedCoreClientModInitializer::onInitializeReinforcedCoreClient);
  }
}
