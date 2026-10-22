package atonkish.reinfcore.util;

import net.minecraft.world.item.Item;

public class ReinforcingMaterial {
  private final String name;
  private final int size;
  private final Item ingredient;

  public ReinforcingMaterial(String name, int size, Item ingredient) {
    this.name = name;
    this.size = size;
    this.ingredient = ingredient;
  }

  public String toString() {
    return this.name;
  }

  public String getName() {
    return this.name;
  }

  public int getSize() {
    return this.size;
  }

  public Item getIngredient() {
    return this.ingredient;
  }
}
