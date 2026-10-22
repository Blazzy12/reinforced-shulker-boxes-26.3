package atonkish.reinfshulker.gametest.testcase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.level.block.Rotation;

import atonkish.reinfcore.gametest.TestFunction;
import atonkish.reinfcore.util.ReinforcingMaterial;
import atonkish.reinfcore.util.ReinforcingMaterials;
import atonkish.reinfshulker.ReinforcedShulkerBoxesMod;
import atonkish.reinfshulker.gametest.util.ReinforcedChestsCompat;
import atonkish.reinfshulker.gametest.util.TestIdentifier;
import atonkish.reinfshulker.gametest.util.TestItems;
import atonkish.reinfshulker.item.ModItems;

public class RecipeTests {
  private static final Map<DyeColor, Item> SHULKER_BOX_MAP =
      new LinkedHashMap<>() {
        {
          put((DyeColor) null, Items.SHULKER_BOX);
          for (DyeColor color : DyeColor.values()) {
            put(color, Items.DYED_SHULKER_BOX.pick(color));
          }
        }
      };

  private static final String TEST_ENVIRONMENT_DEFAULT =
      String.format("%s:recipe/default", ReinforcedShulkerBoxesMod.MOD_ID);
  private static final String TEST_STRUCTURE_EMPTY = "fabric-gametest-api-v1:empty";

  public static final Collection<TestFunction> TEST_FUNCTIONS =
      new ArrayList<>() {
        {
          // Copper Shulker Box
          addAll(craftingUpgradeTests(null, "copper", Items.COPPER_INGOT));
          addAll(coloringTests("copper"));
          addAll(chestTests("copper"));
          for (Item copperChest : Items.COPPER_CHEST.asList()) {
            add(
                RecipeTests.chestTest(
                    String.format("Craft Copper Shulker Box from %s", TestItems.name(copperChest)),
                    copperChest,
                    "copper"));
          }

          // Iron Shulker Box
          addAll(craftingUpgradeTests("copper", "iron", Items.IRON_INGOT));
          addAll(coloringTests("iron"));
          addAll(chestTests("iron"));

          // Gold Shulker Box
          addAll(craftingUpgradeTests("iron", "gold", Items.GOLD_INGOT));
          addAll(coloringTests("gold"));
          addAll(chestTests("gold"));

          // Diamond Shulker Box
          addAll(craftingUpgradeTests("gold", "diamond", Items.DIAMOND));
          addAll(coloringTests("diamond"));
          addAll(chestTests("diamond"));

          // Netherite Shulker Box
          for (DyeColor color : SHULKER_BOX_MAP.keySet()) {
            Item base = shulkerBox("diamond", color);
            Item result = shulkerBox("netherite", color);
            add(
                RecipeTests.createTest(
                    String.format("Smithing %s", TestItems.name(base)),
                    RecipeType.SMITHING,
                    () ->
                        new SmithingRecipeInput(
                            new ItemStack(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                            TestItems.withDirt(base),
                            new ItemStack(Items.NETHERITE_INGOT)),
                    () -> TestItems.withDirt(result)));
          }
          addAll(coloringTests("netherite"));
          addAll(chestTests("netherite"));
        }
      };

  private static Item shulkerBox(String material, DyeColor color) {
    return ModItems.REINFORCED_SHULKER_BOX_MAP
        .get(ReinforcingMaterials.MAP.get(material))
        .get(color);
  }

  /** Crafting a reinforced shulker box from a lower tier one surrounded by the material. */
  private static List<TestFunction> craftingUpgradeTests(
      String baseMaterial, String material, Item ingredient) {
    List<TestFunction> tests = new ArrayList<>();
    for (DyeColor color : SHULKER_BOX_MAP.keySet()) {
      Item base =
          baseMaterial == null ? SHULKER_BOX_MAP.get(color) : shulkerBox(baseMaterial, color);
      Item result = shulkerBox(material, color);
      tests.add(
          RecipeTests.createTest(
              String.format("Craft %s", TestItems.name(base)),
              RecipeType.CRAFTING,
              () -> {
                ItemStack m = new ItemStack(ingredient);
                return CraftingInput.of(
                    3, 3, List.of(m, m, m, m, TestItems.withDirt(base), m, m, m, m));
              },
              () -> TestItems.withDirt(result)));
    }

    return tests;
  }

  /** Dyeing a reinforced shulker box keeps its contents. */
  private static List<TestFunction> coloringTests(String material) {
    List<TestFunction> tests = new ArrayList<>();
    for (DyeColor baseColor : SHULKER_BOX_MAP.keySet()) {
      for (DyeColor dyeColor : DyeColor.values()) {
        if (dyeColor.equals(baseColor)) {
          continue;
        }

        Item base = shulkerBox(material, baseColor);
        Item dyed = shulkerBox(material, dyeColor);
        tests.add(
            RecipeTests.createTest(
                String.format("Coloring from %s to %s", TestItems.name(base), TestItems.name(dyed)),
                RecipeType.CRAFTING,
                () ->
                    CraftingInput.of(
                        2,
                        2,
                        List.of(
                            TestItems.withDirt(base),
                            new ItemStack(Items.DYE.pick(dyeColor)),
                            ItemStack.EMPTY,
                            ItemStack.EMPTY)),
                () -> TestItems.withDirt(dyed)));
      }
    }

    return tests;
  }

  /** Crafting from the matching Reinforced Chests chest, when that mod is installed. */
  private static List<TestFunction> chestTests(String material) {
    if (!ReinforcedChestsCompat.isLoaded()) {
      return List.of();
    }

    ReinforcingMaterial reinforcingMaterial = ReinforcingMaterials.MAP.get(material);
    Item chest = ReinforcedChestsCompat.getChest(reinforcingMaterial);
    String name =
        material.equals("copper")
            ? "Craft Copper Shulker Box from Reinforced Copper Chest"
            : String.format(
                "Craft %s from %s",
                TestItems.name(shulkerBox(material, null)), TestItems.name(chest));
    return List.of(RecipeTests.chestTest(name, chest, material));
  }

  private static TestFunction chestTest(String name, Item chest, String material) {
    Item result = shulkerBox(material, null);
    return RecipeTests.createTest(
        name,
        RecipeType.CRAFTING,
        () -> {
          ItemStack shell = new ItemStack(Items.SHULKER_SHELL);
          return CraftingInput.of(
              3,
              3,
              List.of(
                  shell,
                  ItemStack.EMPTY,
                  ItemStack.EMPTY,
                  new ItemStack(chest),
                  ItemStack.EMPTY,
                  ItemStack.EMPTY,
                  shell,
                  ItemStack.EMPTY,
                  ItemStack.EMPTY));
        },
        () -> new ItemStack(result));
  }

  private static <I extends RecipeInput, T extends Recipe<I>> TestFunction createTest(
      String name, RecipeType<T> type, Supplier<I> inputSupplier, Supplier<ItemStack> expected) {
    Identifier testIdentifier =
        TestIdentifier.of(ReinforcedShulkerBoxesMod.MOD_ID, RecipeTests.class, name);

    return new TestFunction(
        testIdentifier,
        RecipeTests.TEST_ENVIRONMENT_DEFAULT,
        RecipeTests.TEST_STRUCTURE_EMPTY,
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
          ServerLevel world = context.getLevel();
          RecipeManager recipeManager = world.recipeAccess();
          I input = inputSupplier.get();
          T recipe = recipeManager.getRecipeFor(type, input, world).orElseThrow().value();

          // Act
          ItemStack actual = recipe.assemble(input);

          // Assert
          try {
            context.assertTrue(
                ItemStack.matches(actual, expected.get()),
                Component.nullToEmpty("Recipe result differs from expected."));
          } catch (Exception e) {
            ReinforcedShulkerBoxesMod.LOGGER.error("[{}] {}", testIdentifier, e.getMessage());
            throw e;
          }

          context.succeed();
        });
  }
}
