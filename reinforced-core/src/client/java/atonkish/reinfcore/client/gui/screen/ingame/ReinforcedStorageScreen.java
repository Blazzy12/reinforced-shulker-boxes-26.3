package atonkish.reinfcore.client.gui.screen.ingame;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import atonkish.reinfcore.ReinforcedCoreMod;
import atonkish.reinfcore.screen.ReinforcedStorageScreenHandler;
import atonkish.reinfcore.util.ReinforcedStorageScreenModel;
import atonkish.reinfcore.util.ReinforcedStorageScreenModels;
import atonkish.reinfcore.util.math.Point2i;

@Environment(EnvType.CLIENT)
public class ReinforcedStorageScreen
    extends AbstractContainerScreen<ReinforcedStorageScreenHandler> {
  private static final int SLOT_SIZE = 18;
  private static final int TEXT_LINE_HEIGHT = 11;
  private static final int PADDING_TOP = 17;
  private static final int PADDING_BOTTOM = 7;
  private static final int PADDING_LEFT = 7;
  private static final int PADDING_RIGHT = 7;
  private static final int GAP_BETWEEN_CONTAINER_INVENTORY_AND_PLAYER_INVENTORY = 14;
  private static final int GAP_BETWEEN_PLAYER_INVENTORY_STORAGE_AND_PLAYER_INVENTORY_HOTBAR = 4;
  private static final int GAP_BETWEEN_CONTAINER_INVENTORY_AND_SCROLL_BAR = 4;

  private static final int SINGLE_SCREEN_DEFAULT_COLS = 9;
  private static final int SCROLL_SCREEN_COLS = 9;

  // The border of the generic container panel is tiled around a plain filled area
  private static final Identifier BACKGROUND_TEXTURE =
      Identifier.withDefaultNamespace("textures/gui/container/generic_54.png");
  private static final int BACKGROUND_CORNER = 4;
  private static final int BACKGROUND_X = 0;
  private static final int BACKGROUND_Y = 0;
  private static final int BACKGROUND_WIDTH = 176;
  private static final int BACKGROUND_HEIGHT = 222;
  private static final int BACKGROUND_COLOR = 0xFFC6C6C6;

  private static final Identifier CONTAINER_TEXTURE =
      Identifier.withDefaultNamespace("textures/gui/container/generic_54.png");
  private static final int CONTAINER_INVENTORY_X = 7;
  private static final int CONTAINER_INVENTORY_Y = 17;
  private static final int CONTAINER_INVENTORY_COLS = 9;
  private static final int CONTAINER_INVENTORY_ROWS = 6;
  private static final int PLAYER_INVENTORY_X = 7;
  private static final int PLAYER_INVENTORY_Y = 139;
  private static final int PLAYER_INVENTORY_WIDTH = 162;
  private static final int PLAYER_INVENTORY_HEIGHT = 76;

  private static final Identifier SCROLLBAR_BACKGROUND_TEXTURE =
      Identifier.withDefaultNamespace("textures/gui/container/creative_inventory/tab_items.png");
  private static final int SCROLLBAR_BACKGROUND_X = 174;
  private static final int SCROLLBAR_BACKGROUND_Y = 17;
  private static final int SCROLLBAR_BACKGROUND_WIDTH = 14;
  private static final int SCROLLBAR_BACKGROUND_HEIGHT = 112;

  private static final Identifier SCROLLER_TEXTURE =
      Identifier.withDefaultNamespace("container/creative_inventory/scroller");
  private static final Identifier SCROLLER_DISABLED_TEXTURE =
      Identifier.withDefaultNamespace("container/creative_inventory/scroller_disabled");
  private static final int SCROLLER_WIDTH = 12;
  private static final int SCROLLER_HEIGHT = 15;

  private final ReinforcedStorageScreenModel screenModel;
  private final int cols;
  private final int rows;

  private float scrollPosition;
  private boolean scrolling;

  public ReinforcedStorageScreen(
      ReinforcedStorageScreenHandler handler, Inventory inventory, Component title) {
    super(
        handler,
        inventory,
        title,
        ReinforcedStorageScreen.computeImageWidth(handler),
        ReinforcedStorageScreen.computeImageHeight(handler));

    this.scrollPosition = 0.0f;
    this.scrolling = false;

    this.screenModel =
        handler.getIsDoubleBlock()
            ? ReinforcedStorageScreenModels.DOUBLE_MAP.get(handler.getMaterial())
            : ReinforcedStorageScreenModels.SINGLE_MAP.get(handler.getMaterial());

    this.cols = handler.getColumns();
    this.rows = handler.getRows();

    this.titleLabelX = PADDING_LEFT + 1;
    this.titleLabelY = PADDING_TOP - TEXT_LINE_HEIGHT;
    this.inventoryLabelX =
        PADDING_LEFT + (this.cols - SINGLE_SCREEN_DEFAULT_COLS) * SLOT_SIZE / 2 + 1;
    this.inventoryLabelY =
        this.imageHeight
            - (TEXT_LINE_HEIGHT
                + 3 * SLOT_SIZE
                + GAP_BETWEEN_PLAYER_INVENTORY_STORAGE_AND_PLAYER_INVENTORY_HOTBAR
                + 1 * SLOT_SIZE
                + PADDING_BOTTOM);
  }

  private static int computeImageWidth(ReinforcedStorageScreenHandler handler) {
    int width = PADDING_LEFT + handler.getColumns() * SLOT_SIZE + PADDING_RIGHT;
    if (handler.shouldShowScrollbar()) {
      width += GAP_BETWEEN_CONTAINER_INVENTORY_AND_SCROLL_BAR + SCROLLBAR_BACKGROUND_WIDTH;
    }

    return width;
  }

  private static int computeImageHeight(ReinforcedStorageScreenHandler handler) {
    return PADDING_TOP
        + handler.getRows() * SLOT_SIZE
        + GAP_BETWEEN_CONTAINER_INVENTORY_AND_PLAYER_INVENTORY
        + 3 * SLOT_SIZE
        + GAP_BETWEEN_PLAYER_INVENTORY_STORAGE_AND_PLAYER_INVENTORY_HOTBAR
        + 1 * SLOT_SIZE
        + PADDING_BOTTOM;
  }

  @Override
  public void extractBackground(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
    super.extractBackground(context, mouseX, mouseY, delta);
    this.drawBackgroundTexture(context);
    this.drawSlotTexture(context);
    if (this.hasScrollbar()) {
      this.drawScrollbarTexture(context);
    }
  }

  private void drawBackgroundTexture(GuiGraphicsExtractor context) {
    // CHECKSTYLE.SUPPRESS: VariableDeclarationUsageDistance
    int hnum =
        (this.imageWidth - BACKGROUND_CORNER * 2) / (BACKGROUND_WIDTH - BACKGROUND_CORNER * 2);
    // CHECKSTYLE.SUPPRESS: VariableDeclarationUsageDistance
    int hrem =
        (this.imageWidth - BACKGROUND_CORNER * 2) % (BACKGROUND_WIDTH - BACKGROUND_CORNER * 2);

    // CHECKSTYLE.SUPPRESS: VariableDeclarationUsageDistance
    int vnum =
        (this.imageHeight - BACKGROUND_CORNER * 2) / (BACKGROUND_HEIGHT - BACKGROUND_CORNER * 2);
    // CHECKSTYLE.SUPPRESS: VariableDeclarationUsageDistance
    int vrem =
        (this.imageHeight - BACKGROUND_CORNER * 2) % (BACKGROUND_HEIGHT - BACKGROUND_CORNER * 2);

    //
    // corner
    //

    // left-top
    context.blit(
        RenderPipelines.GUI_TEXTURED,
        BACKGROUND_TEXTURE,
        this.leftPos,
        this.topPos,
        BACKGROUND_X,
        BACKGROUND_Y,
        BACKGROUND_CORNER,
        BACKGROUND_CORNER,
        256,
        256);

    // right-top
    context.blit(
        RenderPipelines.GUI_TEXTURED,
        BACKGROUND_TEXTURE,
        this.leftPos + this.imageWidth - BACKGROUND_CORNER,
        this.topPos,
        BACKGROUND_WIDTH - BACKGROUND_CORNER,
        BACKGROUND_Y,
        BACKGROUND_CORNER,
        BACKGROUND_CORNER,
        256,
        256);

    // right-top
    context.blit(
        RenderPipelines.GUI_TEXTURED,
        BACKGROUND_TEXTURE,
        this.leftPos,
        this.topPos + this.imageHeight - BACKGROUND_CORNER,
        BACKGROUND_X,
        BACKGROUND_HEIGHT - BACKGROUND_CORNER,
        BACKGROUND_CORNER,
        BACKGROUND_CORNER,
        256,
        256);

    // right-bottom
    context.blit(
        RenderPipelines.GUI_TEXTURED,
        BACKGROUND_TEXTURE,
        this.leftPos + this.imageWidth - BACKGROUND_CORNER,
        this.topPos + this.imageHeight - BACKGROUND_CORNER,
        BACKGROUND_WIDTH - BACKGROUND_CORNER,
        BACKGROUND_HEIGHT - BACKGROUND_CORNER,
        BACKGROUND_CORNER,
        BACKGROUND_CORNER,
        256,
        256);

    //
    // edge
    //

    for (int hcnt = 0; hcnt < hnum; ++hcnt) {
      // top
      context.blit(
          RenderPipelines.GUI_TEXTURED,
          BACKGROUND_TEXTURE,
          this.leftPos + BACKGROUND_CORNER + hcnt * (BACKGROUND_WIDTH - BACKGROUND_CORNER * 2),
          this.topPos,
          BACKGROUND_CORNER,
          BACKGROUND_Y,
          BACKGROUND_WIDTH - BACKGROUND_CORNER * 2,
          BACKGROUND_CORNER,
          256,
          256);

      // bottom
      context.blit(
          RenderPipelines.GUI_TEXTURED,
          BACKGROUND_TEXTURE,
          this.leftPos + BACKGROUND_CORNER + hcnt * (BACKGROUND_WIDTH - BACKGROUND_CORNER * 2),
          this.topPos + this.imageHeight - BACKGROUND_CORNER,
          BACKGROUND_CORNER,
          BACKGROUND_HEIGHT - BACKGROUND_CORNER,
          BACKGROUND_WIDTH - BACKGROUND_CORNER * 2,
          BACKGROUND_CORNER,
          256,
          256);
    }

    for (int vcnt = 0; vcnt < vnum; ++vcnt) {
      // left
      context.blit(
          RenderPipelines.GUI_TEXTURED,
          BACKGROUND_TEXTURE,
          this.leftPos,
          this.topPos + BACKGROUND_CORNER + vcnt * (BACKGROUND_HEIGHT - BACKGROUND_CORNER * 2),
          BACKGROUND_X,
          BACKGROUND_CORNER,
          BACKGROUND_CORNER,
          BACKGROUND_HEIGHT - BACKGROUND_CORNER * 2,
          256,
          256);

      // right
      context.blit(
          RenderPipelines.GUI_TEXTURED,
          BACKGROUND_TEXTURE,
          this.leftPos + this.imageWidth - BACKGROUND_CORNER,
          this.topPos + BACKGROUND_CORNER + vcnt * (BACKGROUND_HEIGHT - BACKGROUND_CORNER * 2),
          BACKGROUND_WIDTH - BACKGROUND_CORNER,
          BACKGROUND_CORNER,
          BACKGROUND_CORNER,
          BACKGROUND_HEIGHT - BACKGROUND_CORNER * 2,
          256,
          256);
    }

    // top
    context.blit(
        RenderPipelines.GUI_TEXTURED,
        BACKGROUND_TEXTURE,
        this.leftPos + BACKGROUND_CORNER + hnum * (BACKGROUND_WIDTH - BACKGROUND_CORNER * 2),
        this.topPos,
        BACKGROUND_CORNER,
        BACKGROUND_Y,
        hrem,
        BACKGROUND_CORNER,
        256,
        256);

    // bottom
    context.blit(
        RenderPipelines.GUI_TEXTURED,
        BACKGROUND_TEXTURE,
        this.leftPos + BACKGROUND_CORNER + hnum * (BACKGROUND_WIDTH - BACKGROUND_CORNER * 2),
        this.topPos + this.imageHeight - BACKGROUND_CORNER,
        BACKGROUND_CORNER,
        BACKGROUND_HEIGHT - BACKGROUND_CORNER,
        hrem,
        BACKGROUND_CORNER,
        256,
        256);

    // left
    context.blit(
        RenderPipelines.GUI_TEXTURED,
        BACKGROUND_TEXTURE,
        this.leftPos,
        this.topPos + BACKGROUND_CORNER + vnum * (BACKGROUND_HEIGHT - BACKGROUND_CORNER * 2),
        BACKGROUND_X,
        BACKGROUND_CORNER,
        BACKGROUND_CORNER,
        vrem,
        256,
        256);

    // right
    context.blit(
        RenderPipelines.GUI_TEXTURED,
        BACKGROUND_TEXTURE,
        this.leftPos + this.imageWidth - BACKGROUND_CORNER,
        this.topPos + BACKGROUND_CORNER + vnum * (BACKGROUND_HEIGHT - BACKGROUND_CORNER * 2),
        BACKGROUND_WIDTH - BACKGROUND_CORNER,
        BACKGROUND_CORNER,
        BACKGROUND_CORNER,
        vrem,
        256,
        256);

    //
    // area
    //

    context.fill(
        this.leftPos + BACKGROUND_CORNER,
        this.topPos + BACKGROUND_CORNER,
        this.leftPos + this.imageWidth - BACKGROUND_CORNER,
        this.topPos + this.imageHeight - BACKGROUND_CORNER,
        BACKGROUND_COLOR);
  }

  private void drawSlotTexture(GuiGraphicsExtractor context) {
    //
    // container inventory
    //

    Point2i containerInventoryPoint = this.screenModel.getContainerInventoryPoint();

    int hnum = this.cols / CONTAINER_INVENTORY_COLS;
    int hrem = (this.cols % CONTAINER_INVENTORY_COLS) * SLOT_SIZE;

    int vnum = this.rows / CONTAINER_INVENTORY_ROWS;
    int vrem = (this.rows % CONTAINER_INVENTORY_ROWS) * SLOT_SIZE;

    for (int vcnt = 0; vcnt < vnum; ++vcnt) {
      for (int hcnt = 0; hcnt < hnum; ++hcnt) {
        context.blit(
            RenderPipelines.GUI_TEXTURED,
            CONTAINER_TEXTURE,
            this.leftPos
                + containerInventoryPoint.getX()
                + hcnt * CONTAINER_INVENTORY_COLS * SLOT_SIZE,
            this.topPos
                + containerInventoryPoint.getY()
                + vcnt * CONTAINER_INVENTORY_ROWS * SLOT_SIZE,
            CONTAINER_INVENTORY_X,
            CONTAINER_INVENTORY_Y,
            CONTAINER_INVENTORY_COLS * SLOT_SIZE,
            CONTAINER_INVENTORY_ROWS * SLOT_SIZE,
            256,
            256);
      }

      context.blit(
          RenderPipelines.GUI_TEXTURED,
          CONTAINER_TEXTURE,
          this.leftPos
              + containerInventoryPoint.getX()
              + hnum * CONTAINER_INVENTORY_COLS * SLOT_SIZE,
          this.topPos
              + containerInventoryPoint.getY()
              + vcnt * CONTAINER_INVENTORY_ROWS * SLOT_SIZE,
          CONTAINER_INVENTORY_X,
          CONTAINER_INVENTORY_Y,
          hrem,
          CONTAINER_INVENTORY_ROWS * SLOT_SIZE,
          256,
          256);
    }

    for (int hcnt = 0; hcnt < hnum; ++hcnt) {
      context.blit(
          RenderPipelines.GUI_TEXTURED,
          CONTAINER_TEXTURE,
          this.leftPos
              + containerInventoryPoint.getX()
              + hcnt * CONTAINER_INVENTORY_COLS * SLOT_SIZE,
          this.topPos
              + containerInventoryPoint.getY()
              + vnum * CONTAINER_INVENTORY_ROWS * SLOT_SIZE,
          CONTAINER_INVENTORY_X,
          CONTAINER_INVENTORY_Y,
          CONTAINER_INVENTORY_COLS * SLOT_SIZE,
          vrem,
          256,
          256);
    }

    context.blit(
        RenderPipelines.GUI_TEXTURED,
        CONTAINER_TEXTURE,
        this.leftPos + containerInventoryPoint.getX() + hnum * CONTAINER_INVENTORY_COLS * SLOT_SIZE,
        this.topPos + containerInventoryPoint.getY() + vnum * CONTAINER_INVENTORY_ROWS * SLOT_SIZE,
        CONTAINER_INVENTORY_X,
        CONTAINER_INVENTORY_Y,
        hrem,
        vrem,
        256,
        256);

    //
    // player inventory
    //

    Point2i playerInventoryPoint = this.screenModel.getPlayerInventoryPoint();

    context.blit(
        RenderPipelines.GUI_TEXTURED,
        CONTAINER_TEXTURE,
        this.leftPos + playerInventoryPoint.getX(),
        this.topPos + playerInventoryPoint.getY(),
        PLAYER_INVENTORY_X,
        PLAYER_INVENTORY_Y,
        PLAYER_INVENTORY_WIDTH,
        PLAYER_INVENTORY_HEIGHT,
        256,
        256);
  }

  private void drawScrollbarTexture(GuiGraphicsExtractor context) {
    //
    // backgraound
    //

    int vnum = (this.rows * SLOT_SIZE - 2) / (SCROLLBAR_BACKGROUND_HEIGHT - 2);
    int vrem = (this.rows * SLOT_SIZE - 2) % (SCROLLBAR_BACKGROUND_HEIGHT - 2);

    context.blit(
        RenderPipelines.GUI_TEXTURED,
        SCROLLBAR_BACKGROUND_TEXTURE,
        this.leftPos + this.imageWidth - (SCROLLBAR_BACKGROUND_WIDTH + PADDING_RIGHT),
        this.topPos + PADDING_TOP,
        SCROLLBAR_BACKGROUND_X,
        SCROLLBAR_BACKGROUND_Y,
        SCROLLBAR_BACKGROUND_WIDTH,
        1,
        256,
        256);

    for (int vcnt = 0; vcnt < vnum; ++vcnt) {
      context.blit(
          RenderPipelines.GUI_TEXTURED,
          SCROLLBAR_BACKGROUND_TEXTURE,
          this.leftPos + this.imageWidth - (SCROLLBAR_BACKGROUND_WIDTH + PADDING_RIGHT),
          this.topPos + PADDING_TOP + 1 + vcnt * (SCROLLBAR_BACKGROUND_HEIGHT - 2),
          SCROLLBAR_BACKGROUND_X,
          SCROLLBAR_BACKGROUND_Y + 1,
          SCROLLBAR_BACKGROUND_WIDTH,
          SCROLLBAR_BACKGROUND_HEIGHT - 2,
          256,
          256);
    }

    context.blit(
        RenderPipelines.GUI_TEXTURED,
        SCROLLBAR_BACKGROUND_TEXTURE,
        this.leftPos + this.imageWidth - (SCROLLBAR_BACKGROUND_WIDTH + PADDING_RIGHT),
        this.topPos + PADDING_TOP + 1 + vnum * (SCROLLBAR_BACKGROUND_HEIGHT - 2),
        SCROLLBAR_BACKGROUND_X,
        SCROLLBAR_BACKGROUND_Y + 1,
        SCROLLBAR_BACKGROUND_WIDTH,
        vrem,
        256,
        256);

    context.blit(
        RenderPipelines.GUI_TEXTURED,
        SCROLLBAR_BACKGROUND_TEXTURE,
        this.leftPos + this.imageWidth - (SCROLLBAR_BACKGROUND_WIDTH + PADDING_RIGHT),
        this.topPos + PADDING_TOP + this.rows * SLOT_SIZE - 1,
        SCROLLBAR_BACKGROUND_X,
        SCROLLBAR_BACKGROUND_Y + SCROLLBAR_BACKGROUND_HEIGHT - 1,
        SCROLLBAR_BACKGROUND_WIDTH,
        1,
        256,
        256);

    //
    // button
    //

    int ymin = this.topPos + PADDING_TOP + 1;
    int ymax = ymin + this.rows * SLOT_SIZE;

    Identifier identifier = this.hasScrollbar() ? SCROLLER_TEXTURE : SCROLLER_DISABLED_TEXTURE;
    context.blitSprite(
        RenderPipelines.GUI_TEXTURED,
        identifier,
        this.leftPos
            + PADDING_LEFT
            + this.cols * SLOT_SIZE
            + GAP_BETWEEN_CONTAINER_INVENTORY_AND_SCROLL_BAR
            + 1,
        ymin + (int) ((float) (ymax - ymin - (SCROLLER_HEIGHT + 2)) * this.scrollPosition),
        SCROLLER_WIDTH,
        SCROLLER_HEIGHT);
  }

  @Override
  public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
    if (this.hasScrollbar() && click.button() == 0) {
      if (this.isClickInScrollbar(click.x(), click.y())) {
        this.scrolling = this.hasScrollbar();
        return true;
      }
    }

    return super.mouseClicked(click, doubled);
  }

  @Override
  public boolean mouseReleased(MouseButtonEvent click) {
    if (this.hasScrollbar() && click.button() == 0) {
      this.scrolling = false;
    }

    return super.mouseReleased(click);
  }

  private boolean hasScrollbar() {
    return this.menu.shouldShowScrollbar();
  }

  @Override
  public boolean mouseScrolled(
      double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
    if (!this.hasScrollbar()) {
      return false;
    }

    int i =
        Mth.positiveCeilDiv(this.menu.getInventory().getContainerSize(), SCROLL_SCREEN_COLS)
            - ReinforcedCoreMod.CONFIG.scrollScreen.rows;
    float f = (float) (verticalAmount / (double) i);
    this.scrollPosition = Mth.clamp(this.scrollPosition - f, 0.0f, 1.0f);
    this.menu.scrollItems(this.scrollPosition);
    return true;
  }

  protected boolean isClickInScrollbar(double mouseX, double mouseY) {
    int i =
        this.leftPos
            + PADDING_LEFT
            + this.cols * SLOT_SIZE
            + GAP_BETWEEN_CONTAINER_INVENTORY_AND_SCROLL_BAR
            + 1;
    int j = this.topPos + PADDING_TOP + 1;
    int k = i + SCROLLER_WIDTH + 1;
    int l = j + this.rows * SLOT_SIZE;
    return mouseX >= (double) i
        && mouseY >= (double) j
        && mouseX < (double) k
        && mouseY < (double) l;
  }

  @Override
  public boolean mouseDragged(MouseButtonEvent click, double offsetX, double offsetY) {
    if (this.hasScrollbar() && this.scrolling) {
      int i = this.topPos + PADDING_TOP + 1;
      int j = i + this.rows * SLOT_SIZE;
      this.scrollPosition =
          ((float) click.y() - (float) i - (float) SCROLLER_HEIGHT / 2.0f)
              / ((float) (j - i) - (float) SCROLLER_HEIGHT);
      this.scrollPosition = Mth.clamp(this.scrollPosition, 0.0f, 1.0f);
      this.menu.scrollItems(this.scrollPosition);
      return true;
    }

    return super.mouseDragged(click, offsetX, offsetY);
  }
}
