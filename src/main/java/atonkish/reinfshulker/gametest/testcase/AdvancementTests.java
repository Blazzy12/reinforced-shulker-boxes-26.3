package atonkish.reinfshulker.gametest.testcase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.Vec3;

import atonkish.reinfcore.gametest.TestFunction;
import atonkish.reinfcore.util.ReinforcingMaterials;
import atonkish.reinfshulker.ReinforcedShulkerBoxesMod;
import atonkish.reinfshulker.gametest.util.MockServerPlayerHelper;
import atonkish.reinfshulker.gametest.util.ReinforcedChestsCompat;
import atonkish.reinfshulker.gametest.util.TestIdentifier;
import atonkish.reinfshulker.gametest.util.TestItems;
import atonkish.reinfshulker.item.ModItems;

public class AdvancementTests {
  private static final Map<DyeColor, Item> SHULKER_BOX_MAP =
      new LinkedHashMap<>() {
        {
          put((DyeColor) null, Items.SHULKER_BOX);
          put(DyeColor.WHITE, Items.DYED_SHULKER_BOX.pick(DyeColor.WHITE));
          put(DyeColor.ORANGE, Items.DYED_SHULKER_BOX.pick(DyeColor.ORANGE));
          put(DyeColor.MAGENTA, Items.DYED_SHULKER_BOX.pick(DyeColor.MAGENTA));
          put(DyeColor.LIGHT_BLUE, Items.DYED_SHULKER_BOX.pick(DyeColor.LIGHT_BLUE));
          put(DyeColor.YELLOW, Items.DYED_SHULKER_BOX.pick(DyeColor.YELLOW));
          put(DyeColor.LIME, Items.DYED_SHULKER_BOX.pick(DyeColor.LIME));
          put(DyeColor.PINK, Items.DYED_SHULKER_BOX.pick(DyeColor.PINK));
          put(DyeColor.GRAY, Items.DYED_SHULKER_BOX.pick(DyeColor.GRAY));
          put(DyeColor.LIGHT_GRAY, Items.DYED_SHULKER_BOX.pick(DyeColor.LIGHT_GRAY));
          put(DyeColor.CYAN, Items.DYED_SHULKER_BOX.pick(DyeColor.CYAN));
          put(DyeColor.PURPLE, Items.DYED_SHULKER_BOX.pick(DyeColor.PURPLE));
          put(DyeColor.BLUE, Items.DYED_SHULKER_BOX.pick(DyeColor.BLUE));
          put(DyeColor.BROWN, Items.DYED_SHULKER_BOX.pick(DyeColor.BROWN));
          put(DyeColor.GREEN, Items.DYED_SHULKER_BOX.pick(DyeColor.GREEN));
          put(DyeColor.RED, Items.DYED_SHULKER_BOX.pick(DyeColor.RED));
          put(DyeColor.BLACK, Items.DYED_SHULKER_BOX.pick(DyeColor.BLACK));
        }
      };

  private static final String TEST_ENVIRONMENT_DEFAULT =
      String.format("%s:advancement/default", ReinforcedShulkerBoxesMod.MOD_ID);
  private static final String TEST_STRUCTURE_EMPTY = "fabric-gametest-api-v1:empty";

  public static final Collection<TestFunction> TEST_FUNCTIONS =
      new ArrayList<>() {
        {
          // Copper Shulker Box
          for (Item item : SHULKER_BOX_MAP.values()) {
            add(
                AdvancementTests.createTest(
                    String.format(
                        "Obtain Copper Shulker Box recipe advancement by having %s",
                        TestItems.name(item)),
                    item,
                    Identifier.fromNamespaceAndPath(
                        ReinforcedShulkerBoxesMod.MOD_ID,
                        "recipes/decorations/copper_shulker_box")));
          }
          add(
              AdvancementTests.createTest(
                  "Obtain Copper Shulker Box recipe advancement by having Copper Ingot",
                  Items.COPPER_INGOT,
                  Identifier.fromNamespaceAndPath(
                      ReinforcedShulkerBoxesMod.MOD_ID, "recipes/decorations/copper_shulker_box")));
          if (ReinforcedChestsCompat.isLoaded()) {
            add(
                AdvancementTests.createTest(
                    "Obtain Copper Shulker Box recipe advancement by having Reinforced Copper Chest",
                    ReinforcedChestsCompat.getChest(ReinforcingMaterials.MAP.get("copper")),
                    Identifier.fromNamespaceAndPath(
                        ReinforcedShulkerBoxesMod.MOD_ID,
                        "recipes/decorations/copper_shulker_box_from_reinforced_copper_chest")));
          }
          for (Item item :
              List.of(
                  Items.COPPER_CHEST.weathering().unaffected(),
                  Items.COPPER_CHEST.weathering().exposed(),
                  Items.COPPER_CHEST.weathering().weathered(),
                  Items.COPPER_CHEST.weathering().oxidized(),
                  Items.COPPER_CHEST.waxed().unaffected(),
                  Items.COPPER_CHEST.waxed().exposed(),
                  Items.COPPER_CHEST.waxed().weathered(),
                  Items.COPPER_CHEST.waxed().oxidized())) {
            add(
                AdvancementTests.createTest(
                    String.format(
                        "Obtain Copper Shulker Box recipe advancement by having %s",
                        TestItems.name(item)),
                    item,
                    Identifier.fromNamespaceAndPath(
                        ReinforcedShulkerBoxesMod.MOD_ID,
                        "recipes/decorations/copper_shulker_box_from_copper_chests")));
          }

          // Iron Shulker Box
          for (Item item :
              ModItems.REINFORCED_SHULKER_BOX_MAP
                  .get(ReinforcingMaterials.MAP.get("copper"))
                  .values()) {
            add(
                AdvancementTests.createTest(
                    String.format(
                        "Obtain Iron Shulker Box recipe advancement by having %s",
                        TestItems.name(item)),
                    item,
                    Identifier.fromNamespaceAndPath(
                        ReinforcedShulkerBoxesMod.MOD_ID, "recipes/decorations/iron_shulker_box")));
          }
          add(
              AdvancementTests.createTest(
                  "Obtain Iron Shulker Box recipe advancement by having Iron Ingot",
                  Items.IRON_INGOT,
                  Identifier.fromNamespaceAndPath(
                      ReinforcedShulkerBoxesMod.MOD_ID, "recipes/decorations/iron_shulker_box")));
          if (ReinforcedChestsCompat.isLoaded()) {
            add(
                AdvancementTests.createTest(
                    "Obtain Iron Shulker Box recipe advancement by having Iron Chest",
                    ReinforcedChestsCompat.getChest(ReinforcingMaterials.MAP.get("iron")),
                    Identifier.fromNamespaceAndPath(
                        ReinforcedShulkerBoxesMod.MOD_ID,
                        "recipes/decorations/iron_shulker_box_from_iron_chest")));
          }

          // Gold Shulker Box
          for (Item item :
              ModItems.REINFORCED_SHULKER_BOX_MAP
                  .get(ReinforcingMaterials.MAP.get("iron"))
                  .values()) {
            add(
                AdvancementTests.createTest(
                    String.format(
                        "Obtain Gold Shulker Box recipe advancement by having %s",
                        TestItems.name(item)),
                    item,
                    Identifier.fromNamespaceAndPath(
                        ReinforcedShulkerBoxesMod.MOD_ID, "recipes/decorations/gold_shulker_box")));
          }
          add(
              AdvancementTests.createTest(
                  "Obtain Gold Shulker Box recipe advancement by having Gold Ingot",
                  Items.GOLD_INGOT,
                  Identifier.fromNamespaceAndPath(
                      ReinforcedShulkerBoxesMod.MOD_ID, "recipes/decorations/gold_shulker_box")));
          if (ReinforcedChestsCompat.isLoaded()) {
            add(
                AdvancementTests.createTest(
                    "Obtain Gold Shulker Box recipe advancement by having Gold Chest",
                    ReinforcedChestsCompat.getChest(ReinforcingMaterials.MAP.get("gold")),
                    Identifier.fromNamespaceAndPath(
                        ReinforcedShulkerBoxesMod.MOD_ID,
                        "recipes/decorations/gold_shulker_box_from_gold_chest")));
          }

          // Diamond Shulker Box
          for (Item item :
              ModItems.REINFORCED_SHULKER_BOX_MAP
                  .get(ReinforcingMaterials.MAP.get("gold"))
                  .values()) {
            add(
                AdvancementTests.createTest(
                    String.format(
                        "Obtain Diamond Shulker Box recipe advancement by having %s",
                        TestItems.name(item)),
                    item,
                    Identifier.fromNamespaceAndPath(
                        ReinforcedShulkerBoxesMod.MOD_ID,
                        "recipes/decorations/diamond_shulker_box")));
          }
          add(
              AdvancementTests.createTest(
                  "Obtain Diamond Shulker Box recipe advancement by having Diamond Ingot",
                  Items.DIAMOND,
                  Identifier.fromNamespaceAndPath(
                      ReinforcedShulkerBoxesMod.MOD_ID,
                      "recipes/decorations/diamond_shulker_box")));
          if (ReinforcedChestsCompat.isLoaded()) {
            add(
                AdvancementTests.createTest(
                    "Obtain Diamond Shulker Box recipe advancement by having Diamond Chest",
                    ReinforcedChestsCompat.getChest(ReinforcingMaterials.MAP.get("diamond")),
                    Identifier.fromNamespaceAndPath(
                        ReinforcedShulkerBoxesMod.MOD_ID,
                        "recipes/decorations/diamond_shulker_box_from_diamond_chest")));
          }

          // Netherite Shulker Box
          for (Item item :
              ModItems.REINFORCED_SHULKER_BOX_MAP
                  .get(ReinforcingMaterials.MAP.get("diamond"))
                  .values()) {
            add(
                AdvancementTests.createTest(
                    String.format(
                        "Obtain Netherite Shulker Box recipe advancement by having %s",
                        TestItems.name(item)),
                    item,
                    Identifier.fromNamespaceAndPath(
                        ReinforcedShulkerBoxesMod.MOD_ID,
                        "recipes/decorations/netherite_shulker_box_smithing")));
          }
          add(
              AdvancementTests.createTest(
                  "Obtain Netherite Shulker Box recipe advancement by having Netherite Ingot",
                  Items.NETHERITE_INGOT,
                  Identifier.fromNamespaceAndPath(
                      ReinforcedShulkerBoxesMod.MOD_ID,
                      "recipes/decorations/netherite_shulker_box_smithing")));
          if (ReinforcedChestsCompat.isLoaded()) {
            add(
                AdvancementTests.createTest(
                    "Obtain Netherite Shulker Box recipe advancement by having Netherite Chest",
                    ReinforcedChestsCompat.getChest(ReinforcingMaterials.MAP.get("netherite")),
                    Identifier.fromNamespaceAndPath(
                        ReinforcedShulkerBoxesMod.MOD_ID,
                        "recipes/decorations/netherite_shulker_box_from_netherite_chest")));
          }
        }
      };

  private static TestFunction createTest(String name, Item item, Identifier advancementId) {
    Identifier testIdentifier =
        TestIdentifier.of(ReinforcedShulkerBoxesMod.MOD_ID, AdvancementTests.class, name);

    return new TestFunction(
        testIdentifier,
        AdvancementTests.TEST_ENVIRONMENT_DEFAULT,
        AdvancementTests.TEST_STRUCTURE_EMPTY,
        20,
        0,
        true,
        Rotation.NONE,
        false,
        1,
        1,
        false,
        (context) -> {
          // Arrange
          ServerPlayer player =
              MockServerPlayerHelper.spawn(
                  context, GameType.SURVIVAL, Vec3.atLowerCornerOf(BlockPos.ZERO));
          AdvancementHolder entry =
              context.getLevel().getServer().getAdvancements().get(advancementId);
          AdvancementProgress progress = player.getAdvancements().getOrStartProgress(entry);

          // Act
          CompletableFuture<Void> futurePartialAct1 = new CompletableFuture<>();
          CompletableFuture<Void> futurePartialAct2 = new CompletableFuture<>();

          Map<String, Boolean> progressMap = new HashMap<String, Boolean>();
          String progressMapKeyBeforeHavingItem = "beforeHavingItem";
          String progressMapKeyAfterHavingItem = "afterHavingItem";

          long tickOrigin = 0;
          context.runAtTickTime(
              tickOrigin,
              () -> {
                progressMap.put(progressMapKeyBeforeHavingItem, progress.isDone());

                player.addItem(new ItemStack(item));

                futurePartialAct1.complete(null);
              });

          long tickObtained = 1;
          context.runAtTickTime(
              tickObtained,
              () -> {
                progressMap.put(progressMapKeyAfterHavingItem, progress.isDone());

                futurePartialAct2.complete(null);
              });

          // Assert
          CompletableFuture.allOf(futurePartialAct1, futurePartialAct2)
              .thenRun(
                  () -> {
                    try {
                      context.assertFalse(
                          progressMap.get(progressMapKeyBeforeHavingItem),
                          Component.nullToEmpty(
                              String.format(
                                  "Expected that advancement %s has not been done yet, but it has been already done.",
                                  entry)));
                      context.assertTrue(
                          progressMap.get(progressMapKeyAfterHavingItem),
                          Component.nullToEmpty(
                              String.format(
                                  "Expected that advancement %s has been done, but it has not been done yet.",
                                  entry)));
                    } catch (Exception e) {
                      ReinforcedShulkerBoxesMod.LOGGER.error(
                          "[{}] {}", testIdentifier, e.getMessage());
                      throw e;
                    } finally {
                      MockServerPlayerHelper.destroy(context, player);
                    }

                    context.succeed();
                  });
        });
  }
}
