package net.minecraft.client.renderer.feature;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexSorting;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.mojang.renderpearl.api.commands.RenderPass;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.mojang.renderpearl.api.textures.FilterMode;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.StagedVertexBuffer;
import net.minecraft.client.renderer.feature.submit.SubmitNode;
import net.minecraft.client.renderer.oit.OitStage;
import net.minecraft.client.renderer.state.level.QuadParticleRenderState;
import net.minecraft.client.renderer.texture.AbstractTexture;
import org.jspecify.annotations.Nullable;

public class QuadParticleFeatureRenderer implements FeatureRenderer {
   public static final FeatureRendererType TYPE = FeatureRendererType.create("Particle");
   private final List groups = new ArrayList();
   private @Nullable GpuBufferSlice dynamicTransforms;

   public void prepareGroup(final FeatureFrameContext context, final List submits, final boolean strictlyOrdered) {
      if (!submits.isEmpty()) {
         StagedVertexBuffer stagedVertexBuffer = context.stagedVertexBuffer();
         Map drawByLayer = new IdentityHashMap();
         Map textures = new IdentityHashMap();

         for(QuadParticleFeatureRenderer.Submit submit : submits) {
            QuadParticleRenderState particles = submit.particles();
            if (!particles.isEmpty()) {
               for(SingleQuadParticle.Layer layer : particles.layers()) {
                  if (layer.translucent() == submit.translucent()) {
                     StagedVertexBuffer.Draw draw = (StagedVertexBuffer.Draw)drawByLayer.computeIfAbsent(layer, (var1) -> stagedVertexBuffer.appendDraw(DefaultVertexFormat.PARTICLE, PrimitiveTopology.QUADS, (VertexSorting)null));
                     particles.buildLayer(layer, stagedVertexBuffer.getVertexBuilder(draw));
                     textures.put(layer, context.textureManager().getTexture(layer.textureAtlasLocation()));
                     stagedVertexBuffer.requestIndexCount(draw);
                  }
               }
            }
         }

         boolean translucent = ((QuadParticleFeatureRenderer.Submit)submits.getFirst()).translucent();
         this.groups.add(new QuadParticleFeatureRenderer.PreparedGroup(drawByLayer, textures, translucent));
      }
   }

   public void finishPrepare(final FeatureFrameContext context) {
      this.dynamicTransforms = RenderSystem.getDynamicUniforms().writeTransform(RenderSystem.getModelViewMatrixCopy());
   }

   public void executeGroup(final FeatureFrameContext context, final @Nullable OitStage stage, final RenderPass renderPass, final int groupIndex, final List submits, final boolean strictlyOrdered) {
      QuadParticleFeatureRenderer.PreparedGroup group = (QuadParticleFeatureRenderer.PreparedGroup)this.groups.get(groupIndex);
      renderPass.pushDebugGroup(() -> "Particles - " + (group.translucent ? "Translucent" : "Solid"));
      RenderSystem.bindDefaultUniforms(renderPass);
      renderPass.setUniform("DynamicTransforms", (GpuBufferSlice)Objects.requireNonNull(this.dynamicTransforms));
      renderPass.setUniform("Sampler2", context.lightmap(), RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR));
      drawLayers(context.stagedVertexBuffer(), group, renderPass, stage);
      renderPass.popDebugGroup();
   }

   private static void drawLayers(final StagedVertexBuffer stagedBuffer, final QuadParticleFeatureRenderer.PreparedGroup group, final RenderPass renderPass, final @Nullable OitStage stage) {
      for(Map.Entry entry : group.layers.entrySet()) {
         StagedVertexBuffer.ExecuteInfo executeInfo = stagedBuffer.getExecuteInfo((StagedVertexBuffer.Draw)entry.getValue());
         if (executeInfo != null) {
            renderPass.setPipeline(RenderSystem.getCompiledPipeline(stage != null ? getOitPipeline(stage, (SingleQuadParticle.Layer)entry.getKey()) : ((SingleQuadParticle.Layer)entry.getKey()).pipeline()));
            renderPass.setVertexBuffer(0, executeInfo.vertexBuffer().slice());
            renderPass.setIndexBuffer(executeInfo.indexBuffer(), executeInfo.indexType());
            AbstractTexture texture = (AbstractTexture)group.textures.get(entry.getKey());
            renderPass.setUniform("Sampler0", texture.getTextureView(), texture.getSampler());
            renderPass.drawIndexed(executeInfo.indexCount(), 1, executeInfo.firstIndex(), executeInfo.baseVertex(), 0);
         }
      }

   }

   private static RenderPipeline getOitPipeline(final OitStage stage, final SingleQuadParticle.Layer layer) {
      if (layer.oitPipelineSet() == null) {
         throw new IllegalStateException("OIT pipeline set for particle layer not specified.");
      } else {
         return layer.oitPipelineSet().getPipeline(stage);
      }
   }

   public void finishExecute(final FeatureFrameContext context) {
      this.groups.clear();
      this.dynamicTransforms = null;
   }

   private static record PreparedGroup(Map layers, Map textures, boolean translucent) {
   }

   public static record Submit(QuadParticleRenderState particles, boolean translucent) implements SubmitNode {
      public FeatureRendererType featureType() {
         return QuadParticleFeatureRenderer.TYPE;
      }
   }
}
