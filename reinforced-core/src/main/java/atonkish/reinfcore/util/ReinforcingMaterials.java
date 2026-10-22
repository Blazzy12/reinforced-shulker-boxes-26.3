package atonkish.reinfcore.util;

import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.world.item.Item;

public class ReinforcingMaterials {
  public static final Map<String, ReinforcingMaterial> MAP = new LinkedHashMap<>();

  public static ReinforcingMaterial register(String name, int size, Item ingredient) {
    if (!MAP.containsKey(name)) {
      ReinforcingMaterial material = new ReinforcingMaterial(name, size, ingredient);
      MAP.put(name, material);
    }

    return MAP.get(name);
  }
}
