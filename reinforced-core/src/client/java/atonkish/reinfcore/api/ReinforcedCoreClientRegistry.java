package atonkish.reinfcore.api;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.client.gui.screens.MenuScreens;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import atonkish.reinfcore.client.gui.screen.ingame.ReinforcedStorageScreen;
import atonkish.reinfcore.screen.ModScreenHandlerType;
import atonkish.reinfcore.util.ReinforcingMaterial;

@Environment(EnvType.CLIENT)
public class ReinforcedCoreClientRegistry {
  public static Set<ReinforcingMaterial> SINGLE_BLOCK_SET = new HashSet<>();
  public static Set<ReinforcingMaterial> DOUBLE_BLOCK_SET = new HashSet<>();
  public static Set<ReinforcingMaterial> SHULKER_BOX_SET = new HashSet<>();

  public static void registerMaterialSingleBlockScreen(ReinforcingMaterial material) {
    if (!SINGLE_BLOCK_SET.contains(material)) {
      MenuScreens.register(
          ModScreenHandlerType.REINFORCED_SINGLE_BLOCK_MAP.get(material),
          ReinforcedStorageScreen::new);
      SINGLE_BLOCK_SET.add(material);
    }
  }

  public static void registerMaterialDoubleBlockScreen(ReinforcingMaterial material) {
    if (!DOUBLE_BLOCK_SET.contains(material)) {
      MenuScreens.register(
          ModScreenHandlerType.REINFORCED_DOUBLE_BLOCK_MAP.get(material),
          ReinforcedStorageScreen::new);
      DOUBLE_BLOCK_SET.add(material);
    }
  }

  public static void registerMaterialShulkerBoxScreen(ReinforcingMaterial material) {
    if (!SHULKER_BOX_SET.contains(material)) {
      MenuScreens.register(
          ModScreenHandlerType.REINFORCED_SHULKER_BOX_MAP.get(material),
          ReinforcedStorageScreen::new);
      SHULKER_BOX_SET.add(material);
    }
  }
}
