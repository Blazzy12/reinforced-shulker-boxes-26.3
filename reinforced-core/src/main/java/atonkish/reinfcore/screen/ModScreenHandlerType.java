package atonkish.reinfcore.screen;

import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

import atonkish.reinfcore.ReinforcedCoreMod;
import atonkish.reinfcore.util.ReinforcingMaterial;

public class ModScreenHandlerType {
  public static final Map<ReinforcingMaterial, MenuType<ReinforcedStorageScreenHandler>>
      REINFORCED_SINGLE_BLOCK_MAP = new LinkedHashMap<>();
  public static final Map<ReinforcingMaterial, MenuType<ReinforcedStorageScreenHandler>>
      REINFORCED_DOUBLE_BLOCK_MAP = new LinkedHashMap<>();
  public static final Map<ReinforcingMaterial, MenuType<ReinforcedStorageScreenHandler>>
      REINFORCED_SHULKER_BOX_MAP = new LinkedHashMap<>();

  public static MenuType<ReinforcedStorageScreenHandler> registerMaterialSingleBlock(
      ReinforcingMaterial material) {
    if (!REINFORCED_SINGLE_BLOCK_MAP.containsKey(material)) {
      String id = "single_" + material.getName() + "_block";
      MenuType.MenuSupplier<ReinforcedStorageScreenHandler> factory =
          createSingleBlockScreenHandlerFactory(material);
      MenuType<ReinforcedStorageScreenHandler> screenHandlerType = register(id, factory);
      REINFORCED_SINGLE_BLOCK_MAP.put(material, screenHandlerType);
    }

    return REINFORCED_SINGLE_BLOCK_MAP.get(material);
  }

  public static MenuType<ReinforcedStorageScreenHandler> registerMaterialDoubleBlock(
      ReinforcingMaterial material) {
    if (!REINFORCED_DOUBLE_BLOCK_MAP.containsKey(material)) {
      String id = "double_" + material.getName() + "_block";
      MenuType.MenuSupplier<ReinforcedStorageScreenHandler> factory =
          createDoubleBlockScreenHandlerFactory(material);
      MenuType<ReinforcedStorageScreenHandler> screenHandlerType = register(id, factory);
      REINFORCED_DOUBLE_BLOCK_MAP.put(material, screenHandlerType);
    }

    return REINFORCED_DOUBLE_BLOCK_MAP.get(material);
  }

  public static MenuType<ReinforcedStorageScreenHandler> registerMaterialShulkerBox(
      ReinforcingMaterial material) {
    if (!REINFORCED_SHULKER_BOX_MAP.containsKey(material)) {
      String id = material.getName() + "_shulker_box";
      MenuType.MenuSupplier<ReinforcedStorageScreenHandler> factory =
          createShulkerBoxScreenHandlerFactory(material);
      MenuType<ReinforcedStorageScreenHandler> screenHandlerType = register(id, factory);
      REINFORCED_SHULKER_BOX_MAP.put(material, screenHandlerType);
    }

    return REINFORCED_SHULKER_BOX_MAP.get(material);
  }

  private static MenuType<ReinforcedStorageScreenHandler> register(
      String id, MenuType.MenuSupplier<ReinforcedStorageScreenHandler> factory) {
    Identifier identifier = Identifier.fromNamespaceAndPath(ReinforcedCoreMod.MOD_ID, id);
    MenuType<ReinforcedStorageScreenHandler> type =
        new MenuType<ReinforcedStorageScreenHandler>(factory, FeatureFlags.VANILLA_SET);
    return Registry.register(BuiltInRegistries.MENU, identifier, type);
  }

  private static MenuType.MenuSupplier<ReinforcedStorageScreenHandler>
      createSingleBlockScreenHandlerFactory(ReinforcingMaterial material) {
    return (int syncId, Inventory playerInventory) ->
        ReinforcedStorageScreenHandler.createShulkerBoxScreen(material, syncId, playerInventory);
  }

  private static MenuType.MenuSupplier<ReinforcedStorageScreenHandler>
      createDoubleBlockScreenHandlerFactory(ReinforcingMaterial material) {
    return (int syncId, Inventory playerInventory) ->
        ReinforcedStorageScreenHandler.createDoubleBlockScreen(material, syncId, playerInventory);
  }

  private static MenuType.MenuSupplier<ReinforcedStorageScreenHandler>
      createShulkerBoxScreenHandlerFactory(ReinforcingMaterial material) {
    return (int syncId, Inventory playerInventory) ->
        ReinforcedStorageScreenHandler.createShulkerBoxScreen(material, syncId, playerInventory);
  }
}
