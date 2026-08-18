package net.minecraft.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.renderpearl.api.buffers.GpuBuffer;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.mojang.renderpearl.api.commands.RenderPass;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import java.util.Optional;
import java.util.OptionalDouble;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

public class DebugCrosshairRenderer implements AutoCloseable {
   private static final float CROSSHAIR_SCALE = 0.01F;
   private static final int CROSSHAIR_INDEX_COUNT = 36;
   private final GpuBuffer crosshairBuffer;
   private final RenderSystem.AutoStorageIndexBuffer crosshairIndicies = RenderSystem.getSequentialBuffer(PrimitiveTopology.LINES);

   public DebugCrosshairRenderer() {
      ByteBufferBuilder byteBufferBuilder = ByteBufferBuilder.exactlySized(DefaultVertexFormat.POSITION_COLOR_NORMAL_LINE_WIDTH.getVertexSize() * 12 * 2);

      try {
         BufferBuilder bufferBuilder = new BufferBuilder(byteBufferBuilder, PrimitiveTopology.LINES, DefaultVertexFormat.POSITION_COLOR_NORMAL_LINE_WIDTH);
         bufferBuilder.addVertex(0.0F, 0.0F, 0.0F).setColor(-16777216).setNormal(1.0F, 0.0F, 0.0F).setLineWidth(4.0F);
         bufferBuilder.addVertex(1.0F, 0.0F, 0.0F).setColor(-16777216).setNormal(1.0F, 0.0F, 0.0F).setLineWidth(4.0F);
         bufferBuilder.addVertex(0.0F, 0.0F, 0.0F).setColor(-16777216).setNormal(0.0F, 1.0F, 0.0F).setLineWidth(4.0F);
         bufferBuilder.addVertex(0.0F, 1.0F, 0.0F).setColor(-16777216).setNormal(0.0F, 1.0F, 0.0F).setLineWidth(4.0F);
         bufferBuilder.addVertex(0.0F, 0.0F, 0.0F).setColor(-16777216).setNormal(0.0F, 0.0F, 1.0F).setLineWidth(4.0F);
         bufferBuilder.addVertex(0.0F, 0.0F, 1.0F).setColor(-16777216).setNormal(0.0F, 0.0F, 1.0F).setLineWidth(4.0F);
         bufferBuilder.addVertex(0.0F, 0.0F, 0.0F).setColor(-65536).setNormal(1.0F, 0.0F, 0.0F).setLineWidth(2.0F);
         bufferBuilder.addVertex(1.0F, 0.0F, 0.0F).setColor(-65536).setNormal(1.0F, 0.0F, 0.0F).setLineWidth(2.0F);
         bufferBuilder.addVertex(0.0F, 0.0F, 0.0F).setColor(-16711936).setNormal(0.0F, 1.0F, 0.0F).setLineWidth(2.0F);
         bufferBuilder.addVertex(0.0F, 1.0F, 0.0F).setColor(-16711936).setNormal(0.0F, 1.0F, 0.0F).setLineWidth(2.0F);
         bufferBuilder.addVertex(0.0F, 0.0F, 0.0F).setColor(-8421377).setNormal(0.0F, 0.0F, 1.0F).setLineWidth(2.0F);
         bufferBuilder.addVertex(0.0F, 0.0F, 1.0F).setColor(-8421377).setNormal(0.0F, 0.0F, 1.0F).setLineWidth(2.0F);
         MeshData meshData = bufferBuilder.buildOrThrow();

         try {
            this.crosshairBuffer = RenderSystem.getDevice().createBuffer(() -> "Crosshair vertex buffer", 32, meshData.vertexBuffer());
         } catch (Throwable var8) {
            if (meshData != null) {
               try {
                  meshData.close();
               } catch (Throwable var7) {
                  var8.addSuppressed(var7);
               }
            }

            throw var8;
         }

         if (meshData != null) {
            meshData.close();
         }
      } catch (Throwable var9) {
         if (byteBufferBuilder != null) {
            try {
               byteBufferBuilder.close();
            } catch (Throwable var6) {
               var9.addSuppressed(var6);
            }
         }

         throw var9;
      }

      if (byteBufferBuilder != null) {
         byteBufferBuilder.close();
      }

   }

   public void close() {
      this.crosshairBuffer.close();
   }

   public void render(final CameraRenderState cameraState, final int guiScale, final GpuTextureView colorTexture, final GpuTextureView depthTexture) {
      Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
      modelViewStack.pushMatrix();
      modelViewStack.translate(0.0F, 0.0F, -1.0F);
      modelViewStack.rotateX(cameraState.xRot * ((float)Math.PI / 180F));
      modelViewStack.rotateY(cameraState.yRot * ((float)Math.PI / 180F));
      float crosshairScale = 0.01F * (float)guiScale;
      modelViewStack.scale(-crosshairScale, crosshairScale, -crosshairScale);
      RenderPipeline renderPipelineOutline = RenderPipelines.LINES;
      RenderPipeline renderPipelineFill = RenderPipelines.LINES_DEPTH_BIAS;
      GpuBuffer indexBuffer = this.crosshairIndicies.getBuffer(36);
      GpuBufferSlice dynamicTransform = RenderSystem.getDynamicUniforms().writeTransform(new Matrix4f(modelViewStack));
      RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "3d crosshair", colorTexture, Optional.empty(), depthTexture, OptionalDouble.empty());

      try {
         renderPass.setPipeline(RenderSystem.getCompiledPipeline(renderPipelineOutline));
         RenderSystem.bindDefaultUniforms(renderPass);
         renderPass.setVertexBuffer(0, this.crosshairBuffer.slice());
         renderPass.setIndexBuffer(indexBuffer, this.crosshairIndicies.type());
         renderPass.setUniform("DynamicTransforms", dynamicTransform);
         renderPass.drawIndexed(18, 1, 0, 0, 0);
         renderPass.setPipeline(RenderSystem.getCompiledPipeline(renderPipelineFill));
         renderPass.drawIndexed(18, 1, 18, 0, 0);
      } catch (Throwable var15) {
         if (renderPass != null) {
            try {
               renderPass.close();
            } catch (Throwable var14) {
               var15.addSuppressed(var14);
            }
         }

         throw var15;
      }

      if (renderPass != null) {
         renderPass.close();
      }

      modelViewStack.popMatrix();
   }
}
