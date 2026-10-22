package atonkish.reinfcore.mixin;

import net.minecraft.world.inventory.Slot;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Slot.class)
public interface SlotAccessor {
  @Mutable
  @Accessor("x")
  public void setX(int x);

  @Mutable
  @Accessor("y")
  public void setY(int y);
}
