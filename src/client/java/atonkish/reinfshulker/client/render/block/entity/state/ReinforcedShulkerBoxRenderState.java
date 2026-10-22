package atonkish.reinfshulker.client.render.block.entity.state;

import net.minecraft.client.renderer.blockentity.state.ShulkerBoxRenderState;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import atonkish.reinfcore.util.ReinforcingMaterial;

@Environment(EnvType.CLIENT)
public class ReinforcedShulkerBoxRenderState extends ShulkerBoxRenderState {
  public ReinforcingMaterial material;
}
