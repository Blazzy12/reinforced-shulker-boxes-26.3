package atonkish.reinfshulker.clientgametest;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;

import me.shedaniel.autoconfig.AutoConfigClient;

import atonkish.reinfcore.ReinforcedCoreConfig;
import atonkish.reinfcore.ReinforcedCoreMod;
import atonkish.reinfcore.client.gui.screen.ingame.ReinforcedStorageScreen;
import atonkish.reinfcore.util.ReinforcedStorageScreenType;

/** Renders the blocks, items and screens of the mod and saves screenshots for inspection. */
public class ReinforcedShulkerBoxesClientGameTest implements FabricClientGameTest {
  private static final String[] BOXES = {
    "copper_shulker_box",
    "white_iron_shulker_box",
    "red_gold_shulker_box",
    "blue_diamond_shulker_box",
    "black_netherite_shulker_box",
  };

  @Override
  public void runTest(ClientGameTestContext context) {
    context.getInput().resizeWindow(1600, 1000);
    context.runOnClient(client -> client.options.guiScale().set(3));

    try (TestSingleplayerContext singleplayer = context.worldBuilder().create()) {
      singleplayer.getConnection().waitForChunksRender();

      BlockPos origin =
          singleplayer
              .getServer()
              .computeOnServer(
                  server -> server.getPlayerList().getPlayers().getFirst().blockPosition());

      // Place a row of boxes (one facing north) in front of the player and fill one with items
      for (int i = 0; i < BOXES.length; i++) {
        BlockPos pos = origin.offset(i * 2 - 4, 0, 4);
        String facing = i == 2 ? "north" : "up";
        singleplayer
            .getServer()
            .runCommand(
                String.format(
                    "setblock %d %d %d reinfshulker:%s[facing=%s]",
                    pos.getX(), pos.getY(), pos.getZ(), BOXES[i], facing));
      }
      BlockPos netherite = origin.offset(4, 0, 4);
      singleplayer
          .getServer()
          .runCommand(
              String.format(
                  "item replace block %d %d %d container.0 with minecraft:diamond 64",
                  netherite.getX(), netherite.getY(), netherite.getZ()));
      singleplayer
          .getServer()
          .runCommand(
              String.format(
                  "item replace block %d %d %d container.50 with minecraft:emerald 7",
                  netherite.getX(), netherite.getY(), netherite.getZ()));

      // Items in the hotbar
      for (int i = 0; i < BOXES.length; i++) {
        singleplayer
            .getServer()
            .runCommand(
                String.format(
                    "item replace entity @p hotbar.%d with reinfshulker:%s", i, BOXES[i]));
      }

      singleplayer
          .getServer()
          .runCommand(
              String.format("tp @p %d %d %d", origin.getX(), origin.getY() + 2, origin.getZ() - 1));
      context.waitTicks(5);
      context.getInput().lookAt(origin.offset(0, 0, 4));
      singleplayer.getConnection().waitForChunksRender();
      context.waitTicks(20);
      context.takeScreenshot("reinfshulker-world");

      // Open the copper (45 slots) and netherite (108 slots) boxes
      openScreen(context, singleplayer, origin.offset(-4, 0, 4));
      context.takeScreenshot("reinfshulker-copper-screen");
      context.setScreen(() -> null);
      openScreen(context, singleplayer, netherite);
      context.takeScreenshot("reinfshulker-netherite-screen");
      context.setScreen(() -> null);

      // Scroll screen type: scroll down to the bottom of the netherite box
      ReinforcedCoreMod.CONFIG.screenType = ReinforcedStorageScreenType.SCROLL;
      openScreen(context, singleplayer, netherite);
      context.takeScreenshot("reinfshulker-netherite-scroll-screen-top");
      context.getInput().setCursorPos(800, 300);
      context.getInput().scroll(-10);
      context.waitTicks(2);
      context.takeScreenshot("reinfshulker-netherite-scroll-screen-bottom");
      context.setScreen(() -> null);
      ReinforcedCoreMod.CONFIG.screenType = ReinforcedStorageScreenType.SINGLE;
      context.waitTicks(3);
      context.takeScreenshot("reinfshulker-world-lid-closing");

      // Reinforced Core config screen (Cloth Config)
      context.setScreen(
          () -> {
            Screen screen =
                AutoConfigClient.getConfigScreen(ReinforcedCoreConfig.class, null).get();
            return screen;
          });
      context.waitTicks(5);
      context.takeScreenshot("reinfcore-config-screen");
      context.setScreen(() -> null);
    }
  }

  private static void openScreen(
      ClientGameTestContext context, TestSingleplayerContext singleplayer, BlockPos pos) {
    singleplayer
        .getServer()
        .runOnServer(
            server -> {
              BlockEntity blockEntity = server.overworld().getBlockEntity(pos);
              if (!(blockEntity instanceof ShulkerBoxBlockEntity shulkerBox)) {
                throw new AssertionError("Missing reinforced shulker box at " + pos);
              }
              server.getPlayerList().getPlayers().getFirst().openMenu(shulkerBox);
            });
    context.waitForScreen(ReinforcedStorageScreen.class);
    context.waitTicks(10);
  }
}
