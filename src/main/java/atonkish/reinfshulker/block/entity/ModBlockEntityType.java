package atonkish.reinfshulker.block.entity;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityTypes;

import atonkish.reinfcore.util.ReinforcingMaterial;
import atonkish.reinfshulker.block.ModBlocks;

public class ModBlockEntityType {
  public static final Map<ReinforcingMaterial, BlockEntityType<ReinforcedShulkerBoxBlockEntity>>
      REINFORCED_SHULKER_BOX_MAP = new LinkedHashMap<>();

  public static BlockEntityType<ReinforcedShulkerBoxBlockEntity> registerMaterial(
      String namespace, ReinforcingMaterial material) {
    if (!REINFORCED_SHULKER_BOX_MAP.containsKey(material)) {
      String id = material.getName() + "_shulker_box";
      Collection<Block> blocks = ModBlocks.REINFORCED_SHULKER_BOX_MAP.get(material).values();
      BlockEntityType<ReinforcedShulkerBoxBlockEntity> blockEntityType =
          Registry.register(
              BuiltInRegistries.BLOCK_ENTITY_TYPE,
              Identifier.fromNamespaceAndPath(namespace, id),
              new BlockEntityType<>(
                  (blockPos, blockState) ->
                      new ReinforcedShulkerBoxBlockEntity(material, blockPos, blockState),
                  Set.copyOf(blocks)));
      REINFORCED_SHULKER_BOX_MAP.put(material, blockEntityType);

      // ShulkerBoxBlockEntity's constructor validates the block state against the vanilla type
      blocks.forEach(BlockEntityTypes.SHULKER_BOX::addValidBlock);
    }

    return REINFORCED_SHULKER_BOX_MAP.get(material);
  }
}
