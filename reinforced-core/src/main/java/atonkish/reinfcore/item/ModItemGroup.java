package atonkish.reinfcore.item;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import atonkish.reinfcore.mixin.ItemGroupAccessor;

public class ModItemGroup {
  public static void setIcon(CreativeModeTab itemGroup, ItemLike item) {
    // Item components are bound after mod initialization, so the stack must be created lazily
    ((ItemGroupAccessor) itemGroup).setIconGenerator(() -> new ItemStack(item));
    ((ItemGroupAccessor) itemGroup).setIcon(null);
  }
}
