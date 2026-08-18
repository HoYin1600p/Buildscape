package net.minecraft.client.renderer.block.model.properties.conditional;

import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.world.level.block.state.BlockState;

public class IsXmas implements ConditionalBlockModelProperty {
   public boolean get(final BlockState blockState) {
      return ChestRenderer.xmasTextures();
   }
}
