package atonkish.reinfcore.item;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;

import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;

import atonkish.reinfcore.ReinforcedCoreMod;

public class ModItemGroups {
  public static final ResourceKey<CreativeModeTab> REINFORCED_STORAGE;

  public static void init() {
    Registry.register(
        BuiltInRegistries.CREATIVE_MODE_TAB,
        ModItemGroups.REINFORCED_STORAGE,
        FabricCreativeModeTab.builder()
            .title(
                Component.translatable(
                    String.format(
                        "itemGroup.%s.%s",
                        ReinforcedCoreMod.MOD_ID,
                        ModItemGroups.REINFORCED_STORAGE.identifier().getPath())))
            .build());
  }

  private static ResourceKey<CreativeModeTab> register(String id) {
    return ResourceKey.create(
        Registries.CREATIVE_MODE_TAB,
        Identifier.fromNamespaceAndPath(ReinforcedCoreMod.MOD_ID, id));
  }

  static {
    REINFORCED_STORAGE = ModItemGroups.register("reinforced_storage");
  }
}
