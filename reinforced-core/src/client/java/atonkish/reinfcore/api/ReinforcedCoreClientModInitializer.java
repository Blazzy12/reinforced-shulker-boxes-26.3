package atonkish.reinfcore.api;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface ReinforcedCoreClientModInitializer {
  void onInitializeReinforcedCoreClient();
}
