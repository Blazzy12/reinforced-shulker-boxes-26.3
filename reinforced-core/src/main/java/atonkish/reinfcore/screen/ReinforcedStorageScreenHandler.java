package atonkish.reinfcore.screen;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ShulkerBoxSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import atonkish.reinfcore.ReinforcedCoreMod;
import atonkish.reinfcore.mixin.SlotAccessor;
import atonkish.reinfcore.util.ReinforcedStorageScreenModel;
import atonkish.reinfcore.util.ReinforcedStorageScreenModels;
import atonkish.reinfcore.util.ReinforcedStorageScreenType;
import atonkish.reinfcore.util.ReinforcingMaterial;
import atonkish.reinfcore.util.math.Point2i;

public class ReinforcedStorageScreenHandler extends AbstractContainerMenu {
  private static final int SLOT_SIZE = 18;
  private static final int GAP_BETWEEN_PLAYER_INVENTORY_STORAGE_AND_PLAYER_INVENTORY_HOTBAR = 4;

  private static final int SCROLL_SCREEN_COLS = 9;

  private final Container inventory;
  private final ReinforcingMaterial material;
  private final boolean isDoubleBlock;
  private final boolean isShulkerBox;
  private final int cols;
  private final int rows;
  private final ReinforcedStorageScreenModel screenModel;

  public ReinforcedStorageScreenHandler(
      MenuType<?> type,
      ReinforcingMaterial material,
      boolean isDoubleBlock,
      boolean isShulkerBox,
      int syncId,
      Inventory playerInventory,
      Container inventory) {
    super(type, syncId);
    this.inventory = inventory;
    this.material = material;
    this.isDoubleBlock = isDoubleBlock;
    this.isShulkerBox = isShulkerBox;
    inventory.startOpen(playerInventory.player);

    int size = inventory.getContainerSize();
    this.cols = ReinforcedStorageScreenModel.getContainerInventoryColumns(size);
    this.rows = ReinforcedStorageScreenModel.getContainerInventoryRows(size, this.cols);

    this.screenModel =
        this.isDoubleBlock
            ? ReinforcedStorageScreenModels.DOUBLE_MAP.get(this.material)
            : ReinforcedStorageScreenModels.SINGLE_MAP.get(this.material);

    this.addSlots(playerInventory);
  }

  public static ReinforcedStorageScreenHandler createSingleBlockScreen(
      ReinforcingMaterial material, int syncId, Inventory playerInventory, Container inventory) {
    return new ReinforcedStorageScreenHandler(
        ModScreenHandlerType.REINFORCED_SINGLE_BLOCK_MAP.get(material),
        material,
        false,
        false,
        syncId,
        playerInventory,
        inventory);
  }

  public static ReinforcedStorageScreenHandler createSingleBlockScreen(
      ReinforcingMaterial material, int syncId, Inventory playerInventory) {
    int size = ReinforcedStorageScreenModel.getContainerInventorySize(material, false);
    Container inventory = new SimpleContainer(size);
    return createSingleBlockScreen(material, syncId, playerInventory, inventory);
  }

  public static ReinforcedStorageScreenHandler createDoubleBlockScreen(
      ReinforcingMaterial material, int syncId, Inventory playerInventory, Container inventory) {
    return new ReinforcedStorageScreenHandler(
        ModScreenHandlerType.REINFORCED_DOUBLE_BLOCK_MAP.get(material),
        material,
        true,
        false,
        syncId,
        playerInventory,
        inventory);
  }

  public static ReinforcedStorageScreenHandler createDoubleBlockScreen(
      ReinforcingMaterial material, int syncId, Inventory playerInventory) {
    int size = ReinforcedStorageScreenModel.getContainerInventorySize(material, true);
    Container inventory = new SimpleContainer(size);
    return createDoubleBlockScreen(material, syncId, playerInventory, inventory);
  }

  public static ReinforcedStorageScreenHandler createShulkerBoxScreen(
      ReinforcingMaterial material, int syncId, Inventory playerInventory, Container inventory) {
    return new ReinforcedStorageScreenHandler(
        ModScreenHandlerType.REINFORCED_SHULKER_BOX_MAP.get(material),
        material,
        false,
        true,
        syncId,
        playerInventory,
        inventory);
  }

  public static ReinforcedStorageScreenHandler createShulkerBoxScreen(
      ReinforcingMaterial material, int syncId, Inventory playerInventory) {
    int size = ReinforcedStorageScreenModel.getContainerInventorySize(material, false);
    Container inventory = new SimpleContainer(size);
    return createShulkerBoxScreen(material, syncId, playerInventory, inventory);
  }

  private void addSlots(Inventory playerInventory) {
    Point2i containerInventoryPoint = this.screenModel.getContainerInventoryPoint();
    Point2i playerInventoryPoint = this.screenModel.getPlayerInventoryPoint();

    for (int index = 0; index < this.inventory.getContainerSize(); ++index) {
      int col = index % this.cols;
      int row = (index - col) / this.cols;

      int x = containerInventoryPoint.getX() + col * SLOT_SIZE + 1;
      int y = containerInventoryPoint.getY() + row * SLOT_SIZE + 1;

      if (ReinforcedCoreMod.CONFIG.screenType == ReinforcedStorageScreenType.SCROLL
          && row >= ReinforcedCoreMod.CONFIG.scrollScreen.rows) {
        // HACK: slot position far away outside if scroll screen type
        x = Integer.MIN_VALUE;
        y = Integer.MIN_VALUE;
      }

      Slot slot =
          this.isShulkerBox
              ? new ShulkerBoxSlot(this.inventory, index, x, y)
              : new Slot(this.inventory, index, x, y);
      this.addSlot(slot);
    }

    for (int index = 9; index < 36; ++index) {
      int col = (index - 9) % 9;
      int row = (index - col - 9) / 9;

      int x = playerInventoryPoint.getX() + col * SLOT_SIZE + 1;
      int y = playerInventoryPoint.getY() + row * SLOT_SIZE + 1;

      Slot slot = new Slot(playerInventory, index, x, y);
      this.addSlot(slot);
    }

    for (int index = 0; index < 9; ++index) {
      int col = index;

      int x = playerInventoryPoint.getX() + col * SLOT_SIZE + 1;
      int y =
          playerInventoryPoint.getY()
              + 3 * SLOT_SIZE
              + GAP_BETWEEN_PLAYER_INVENTORY_STORAGE_AND_PLAYER_INVENTORY_HOTBAR
              + 1;

      Slot slot = new Slot(playerInventory, index, x, y);
      this.addSlot(slot);
    }
  }

  @Override
  public boolean stillValid(Player player) {
    return this.inventory.stillValid(player);
  }

  public void scrollItems(float position) {
    if (ReinforcedCoreMod.CONFIG.screenType != ReinforcedStorageScreenType.SCROLL) {
      return;
    }

    int i =
        (this.inventory.getContainerSize() + SCROLL_SCREEN_COLS - 1) / SCROLL_SCREEN_COLS
            - ReinforcedCoreMod.CONFIG.scrollScreen.rows;
    int srow = (int) ((double) (position * (float) i) + 0.5);
    if (srow < 0) {
      srow = 0;
    }

    Point2i containerInventoryPoint = this.screenModel.getContainerInventoryPoint();

    for (int index = 0; index < this.inventory.getContainerSize(); ++index) {
      int col = index % this.cols;
      int row = (index - col) / this.cols;

      int x = containerInventoryPoint.getX() + col * SLOT_SIZE + 1;
      int y = containerInventoryPoint.getY() + (row - srow) * SLOT_SIZE + 1;

      if (row < srow || row >= srow + ReinforcedCoreMod.CONFIG.scrollScreen.rows) {
        // HACK: slot position far away outside if scroll screen type
        x = Integer.MIN_VALUE;
        y = Integer.MIN_VALUE;
      }

      Slot slot = this.getSlot(index);
      ((SlotAccessor) slot).setX(x);
      ((SlotAccessor) slot).setY(y);
    }
  }

  public boolean shouldShowScrollbar() {
    return ReinforcedCoreMod.CONFIG.screenType == ReinforcedStorageScreenType.SCROLL
        && this.inventory.getContainerSize()
            > SCROLL_SCREEN_COLS * ReinforcedCoreMod.CONFIG.scrollScreen.rows;
  }

  @Override
  public ItemStack quickMoveStack(Player player, int slot) {
    ItemStack itemStack = ItemStack.EMPTY;
    Slot slot2 = (Slot) this.slots.get(slot);
    if (slot2 != null && slot2.hasItem()) {
      ItemStack itemStack2 = slot2.getItem();
      itemStack = itemStack2.copy();
      if (slot < this.inventory.getContainerSize()
          ? !this.moveItemStackTo(
              itemStack2, this.inventory.getContainerSize(), this.slots.size(), true)
          : !this.moveItemStackTo(itemStack2, 0, this.inventory.getContainerSize(), false)) {
        return ItemStack.EMPTY;
      }
      if (itemStack2.isEmpty()) {
        slot2.setByPlayer(ItemStack.EMPTY);
      } else {
        slot2.setChanged();
      }
    }
    return itemStack;
  }

  @Override
  public void removed(Player player) {
    super.removed(player);
    this.inventory.stopOpen(player);
  }

  public Container getInventory() {
    return this.inventory;
  }

  public ReinforcingMaterial getMaterial() {
    return this.material;
  }

  public boolean getIsDoubleBlock() {
    return this.isDoubleBlock;
  }

  public boolean getIsShulkerBox() {
    return this.isShulkerBox;
  }

  public int getColumns() {
    return this.cols;
  }

  public int getRows() {
    return this.rows;
  }
}
