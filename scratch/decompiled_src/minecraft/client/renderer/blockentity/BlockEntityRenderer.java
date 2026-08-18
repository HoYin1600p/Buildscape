package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public interface BlockEntityRenderer {
   BlockEntityRenderState createRenderState();

   default void extractRenderState(final BlockEntity blockEntity, final BlockEntityRenderState state, final float partialTicks, final Vec3 cameraPosition, final ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
      BlockEntityRenderState.extractBase(blockEntity, state, breakProgress);
   }

   void submit(final BlockEntityRenderState state, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final CameraRenderState camera);

   default boolean shouldRenderOffScreen() {
      return false;
   }

   default int getViewDistance() {
      return 64;
   }

   default boolean shouldRender(final BlockEntity blockEntity, final Vec3 cameraPosition) {
      return Vec3.atCenterOf(blockEntity.getBlockPos()).closerThan(cameraPosition, (double)this.getViewDistance());
   }
}
