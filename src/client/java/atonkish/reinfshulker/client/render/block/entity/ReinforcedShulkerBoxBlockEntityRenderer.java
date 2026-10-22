package atonkish.reinfshulker.client.render.block.entity;

import java.util.function.Consumer;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.ShulkerBoxRenderer;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.phys.Vec3;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector3fc;

import atonkish.reinfcore.util.ReinforcingMaterial;
import atonkish.reinfshulker.block.entity.ReinforcedShulkerBoxBlockEntity;
import atonkish.reinfshulker.client.render.ModTexturedRenderLayers;
import atonkish.reinfshulker.client.render.block.entity.state.ReinforcedShulkerBoxRenderState;

@Environment(EnvType.CLIENT)
public class ReinforcedShulkerBoxBlockEntityRenderer
    implements BlockEntityRenderer<
        ReinforcedShulkerBoxBlockEntity, ReinforcedShulkerBoxRenderState> {
  // The vanilla renderer does the model work; this renderer only picks the reinforced sprites
  private final ShulkerBoxRenderer shulkerBoxRenderer;

  public ReinforcedShulkerBoxBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    this.shulkerBoxRenderer = new ShulkerBoxRenderer(context);
  }

  @Override
  public ReinforcedShulkerBoxRenderState createRenderState() {
    return new ReinforcedShulkerBoxRenderState();
  }

  @Override
  public void extractRenderState(
      ReinforcedShulkerBoxBlockEntity shulkerBoxBlockEntity,
      ReinforcedShulkerBoxRenderState shulkerBoxBlockEntityRenderState,
      float tickProgress,
      Vec3 cameraPosition,
      @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlayCommand) {
    BlockEntityRenderer.super.extractRenderState(
        shulkerBoxBlockEntity,
        shulkerBoxBlockEntityRenderState,
        tickProgress,
        cameraPosition,
        crumblingOverlayCommand);
    shulkerBoxBlockEntityRenderState.direction =
        shulkerBoxBlockEntity.getBlockState().getValueOrElse(ShulkerBoxBlock.FACING, Direction.UP);
    shulkerBoxBlockEntityRenderState.color = shulkerBoxBlockEntity.getColor();
    shulkerBoxBlockEntityRenderState.progress = shulkerBoxBlockEntity.getProgress(tickProgress);
    shulkerBoxBlockEntityRenderState.material = shulkerBoxBlockEntity.getMaterial();
  }

  @Override
  public void submit(
      ReinforcedShulkerBoxRenderState shulkerBoxBlockEntityRenderState,
      PoseStack matrixStack,
      SubmitNodeCollector orderedRenderCommandQueue,
      CameraRenderState cameraRenderState) {
    DyeColor color = shulkerBoxBlockEntityRenderState.color;
    ReinforcingMaterial material = shulkerBoxBlockEntityRenderState.material;
    SpriteId spriteIdentifier =
        color == null
            ? ModTexturedRenderLayers.REINFORCED_SHULKER_TEXTURE_ID_MAP.get(material)
            : ModTexturedRenderLayers.COLORED_REINFORCED_SHULKER_BOXES_TEXTURES_MAP
                .get(material)
                .get(color.getId());

    matrixStack.pushPose();
    matrixStack.mulPose(
        ShulkerBoxRenderer.modelTransform(shulkerBoxBlockEntityRenderState.direction));
    this.shulkerBoxRenderer.submit(
        matrixStack,
        orderedRenderCommandQueue,
        shulkerBoxBlockEntityRenderState.lightCoords,
        OverlayTexture.NO_OVERLAY,
        shulkerBoxBlockEntityRenderState.progress,
        shulkerBoxBlockEntityRenderState.breakProgress,
        spriteIdentifier,
        0);
    matrixStack.popPose();
  }

  public void collectVertices(float openness, Consumer<Vector3fc> consumer) {
    this.shulkerBoxRenderer.getExtents(openness, consumer);
  }
}
