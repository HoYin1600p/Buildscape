package net.minecraft.client.renderer;

import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.pipeline.RenderTarget;
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
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.state.WindowRenderState;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.CubeMapTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.Identifier;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

public class CubeMap implements AutoCloseable {
   private static final int SIDES = 6;
   private static final float PROJECTION_Z_NEAR = 0.05F;
   private static final float PROJECTION_Z_FAR = 10.0F;
   private static final float PROJECTION_FOV = 85.0F;
   private final GpuBuffer vertexBuffer;
   private final Projection projection;
   private final ProjectionMatrixBuffer projectionMatrixUbo;
   private final Identifier location;

   public CubeMap(final Identifier base) {
      this.location = base;
      this.projection = new Projection();
      this.projectionMatrixUbo = new ProjectionMatrixBuffer("cubemap");
      this.vertexBuffer = initializeVertices();
   }

   public void render(final float rotXInDegrees, final float rotYInDegrees) {
      Minecraft minecraft = Minecraft.getInstance();
      WindowRenderState windowState = minecraft.gameRenderer.gameRenderState().windowRenderState;
      this.projection.setupPerspective(0.05F, 10.0F, 85.0F, (float)windowState.width, (float)windowState.height);
      RenderSystem.setProjectionMatrix(this.projectionMatrixUbo.getBuffer(this.projection), ProjectionType.PERSPECTIVE);
      RenderPipeline renderPipeline = RenderPipelines.PANORAMA;
      RenderTarget mainRenderTarget = Minecraft.getInstance().gameRenderer.mainRenderTarget();
      GpuTextureView colorTexture = mainRenderTarget.getColorTextureView();
      GpuTextureView depthTexture = mainRenderTarget.getDepthTextureView();
      RenderSystem.AutoStorageIndexBuffer indices = RenderSystem.getSequentialBuffer(PrimitiveTopology.QUADS);
      GpuBuffer indexBuffer = indices.getBuffer(36);
      Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
      modelViewStack.pushMatrix();
      modelViewStack.rotationX((float)Math.PI);
      modelViewStack.rotateX(rotXInDegrees * ((float)Math.PI / 180F));
      modelViewStack.rotateY(rotYInDegrees * ((float)Math.PI / 180F));
      GpuBufferSlice dynamicTransforms = RenderSystem.getDynamicUniforms().writeTransform(new Matrix4f(modelViewStack));
      modelViewStack.popMatrix();
      RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "Cubemap", colorTexture, Optional.empty(), depthTexture, OptionalDouble.empty());

      try {
         renderPass.setPipeline(RenderSystem.getCompiledPipeline(renderPipeline));
         RenderSystem.bindDefaultUniforms(renderPass);
         renderPass.setVertexBuffer(0, this.vertexBuffer.slice());
         renderPass.setIndexBuffer(indexBuffer, indices.type());
         renderPass.setUniform("DynamicTransforms", dynamicTransforms);
         AbstractTexture texture = minecraft.getTextureManager().getTexture(this.location);
         renderPass.setUniform("Sampler0", texture.getTextureView(), texture.getSampler());
         renderPass.drawIndexed(36, 1, 0, 0, 0);
      } catch (Throwable var17) {
         if (renderPass != null) {
            try {
               renderPass.close();
            } catch (Throwable var16) {
               var17.addSuppressed(var16);
            }
         }

         throw var17;
      }

      if (renderPass != null) {
         renderPass.close();
      }

   }

   private static GpuBuffer initializeVertices() {
      ByteBufferBuilder byteBufferBuilder = ByteBufferBuilder.exactlySized(DefaultVertexFormat.POSITION.getVertexSize() * 4 * 6);

      GpuBuffer var3;
      try {
         BufferBuilder bufferBuilder = new BufferBuilder(byteBufferBuilder, PrimitiveTopology.QUADS, DefaultVertexFormat.POSITION);
         bufferBuilder.addVertex(-1.0F, -1.0F, 1.0F);
         bufferBuilder.addVertex(-1.0F, 1.0F, 1.0F);
         bufferBuilder.addVertex(1.0F, 1.0F, 1.0F);
         bufferBuilder.addVertex(1.0F, -1.0F, 1.0F);
         bufferBuilder.addVertex(1.0F, -1.0F, 1.0F);
         bufferBuilder.addVertex(1.0F, 1.0F, 1.0F);
         bufferBuilder.addVertex(1.0F, 1.0F, -1.0F);
         bufferBuilder.addVertex(1.0F, -1.0F, -1.0F);
         bufferBuilder.addVertex(1.0F, -1.0F, -1.0F);
         bufferBuilder.addVertex(1.0F, 1.0F, -1.0F);
         bufferBuilder.addVertex(-1.0F, 1.0F, -1.0F);
         bufferBuilder.addVertex(-1.0F, -1.0F, -1.0F);
         bufferBuilder.addVertex(-1.0F, -1.0F, -1.0F);
         bufferBuilder.addVertex(-1.0F, 1.0F, -1.0F);
         bufferBuilder.addVertex(-1.0F, 1.0F, 1.0F);
         bufferBuilder.addVertex(-1.0F, -1.0F, 1.0F);
         bufferBuilder.addVertex(-1.0F, -1.0F, -1.0F);
         bufferBuilder.addVertex(-1.0F, -1.0F, 1.0F);
         bufferBuilder.addVertex(1.0F, -1.0F, 1.0F);
         bufferBuilder.addVertex(1.0F, -1.0F, -1.0F);
         bufferBuilder.addVertex(-1.0F, 1.0F, 1.0F);
         bufferBuilder.addVertex(-1.0F, 1.0F, -1.0F);
         bufferBuilder.addVertex(1.0F, 1.0F, -1.0F);
         bufferBuilder.addVertex(1.0F, 1.0F, 1.0F);
         MeshData meshData = bufferBuilder.buildOrThrow();

         try {
            var3 = RenderSystem.getDevice().createBuffer(() -> "Cube map vertex buffer", 32, meshData.vertexBuffer());
         } catch (Throwable var7) {
            if (meshData != null) {
               try {
                  meshData.close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }
            }

            throw var7;
         }

         if (meshData != null) {
            meshData.close();
         }
      } catch (Throwable var8) {
         if (byteBufferBuilder != null) {
            try {
               byteBufferBuilder.close();
            } catch (Throwable var5) {
               var8.addSuppressed(var5);
            }
         }

         throw var8;
      }

      if (byteBufferBuilder != null) {
         byteBufferBuilder.close();
      }

      return var3;
   }

   public void registerTextures(final TextureManager textureManager) {
      textureManager.register(this.location, new CubeMapTexture(this.location));
   }

   public void close() {
      this.vertexBuffer.close();
      this.projectionMatrixUbo.close();
   }
}
