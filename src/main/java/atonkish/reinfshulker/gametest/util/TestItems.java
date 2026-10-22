package atonkish.reinfshulker.gametest.util;

import java.util.List;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemContainerContents;

/**
 * Helpers for building test cases.
 *
 * <p>Item components are only bound once data packs are loaded, so test cases must not create
 * {@link ItemStack}s while they are being defined (during mod initialization). Build them inside
 * the test body instead.
 */
public final class TestItems {
  private TestItems() {}

  public static String name(Item item) {
    return Component.translatable(item.getDescriptionId()).getString();
  }

  public static ItemStack withDirt(Item item) {
    ItemStack itemStack = new ItemStack(item);
    itemStack.set(
        DataComponents.CONTAINER,
        ItemContainerContents.fromItems(List.of(new ItemStack(Items.DIRT))));
    return itemStack;
  }
}
