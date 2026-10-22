package atonkish.reinfcore.util;

public enum ReinforcedStorageScreenType {
  SINGLE("Single"),
  SCROLL("Scroll");

  private final String type;

  private ReinforcedStorageScreenType(String type) {
    this.type = type;
  }

  @Override
  public String toString() {
    return this.type;
  }
}
