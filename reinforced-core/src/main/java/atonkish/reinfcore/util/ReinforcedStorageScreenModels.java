package atonkish.reinfcore.util;

import java.util.LinkedHashMap;
import java.util.Map;

public class ReinforcedStorageScreenModels {
  public static final Map<ReinforcingMaterial, ReinforcedStorageScreenModel> SINGLE_MAP =
      new LinkedHashMap<>();
  public static final Map<ReinforcingMaterial, ReinforcedStorageScreenModel> DOUBLE_MAP =
      new LinkedHashMap<>();

  public static ReinforcedStorageScreenModel registerMaterialSingleBlock(
      ReinforcingMaterial material) {
    if (!SINGLE_MAP.containsKey(material)) {
      ReinforcedStorageScreenModel screenModel = new ReinforcedStorageScreenModel(material, false);
      SINGLE_MAP.put(material, screenModel);
    }

    return SINGLE_MAP.get(material);
  }

  public static ReinforcedStorageScreenModel registerMaterialDoubleBlock(
      ReinforcingMaterial material) {
    if (!DOUBLE_MAP.containsKey(material)) {
      ReinforcedStorageScreenModel screenModel = new ReinforcedStorageScreenModel(material, true);
      DOUBLE_MAP.put(material, screenModel);
    }

    return DOUBLE_MAP.get(material);
  }
}
