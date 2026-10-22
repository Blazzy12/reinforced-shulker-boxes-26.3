package atonkish.reinfcore.mixin;

import java.util.function.Supplier;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CreativeModeTab.class)
public interface ItemGroupAccessor {
  @Mutable
  @Accessor("iconItemStack")
  public void setIcon(ItemStack itemStack);

  @Mutable
  @Accessor("iconGenerator")
  public void setIconGenerator(Supplier<ItemStack> iconGenerator);
}
