package atonkish.reinfshulker.gametest.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

import net.fabricmc.loader.api.FabricLoader;

import atonkish.reinfcore.util.ReinforcingMaterial;
import atonkish.reinfshulker.ReinforcedShulkerBoxesMod;

/** Looks up Reinforced Chests items without a compile-time dependency on the mod. */
public final class ReinforcedChestsCompat {
  private ReinforcedChestsCompat() {}

  public static boolean isLoaded() {
    return FabricLoader.getInstance().isModLoaded(ReinforcedShulkerBoxesMod.REINFCHEST_MOD_ID);
  }

  public static Item getChest(ReinforcingMaterial material) {
    return BuiltInRegistries.ITEM.getValue(
        Identifier.fromNamespaceAndPath(
            ReinforcedShulkerBoxesMod.REINFCHEST_MOD_ID, material.getName() + "_chest"));
  }
}
