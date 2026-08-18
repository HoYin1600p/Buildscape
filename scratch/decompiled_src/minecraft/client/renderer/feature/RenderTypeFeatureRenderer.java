package net.minecraft.client.renderer.feature;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexSorting;
import com.mojang.renderpearl.api.commands.RenderPass;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.StagedVertexBuffer;
import net.minecraft.client.renderer.oit.OitStage;
import net.minecraft.client.renderer.rendertype.PreparedRenderType;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.jspecify.annotations.Nullable;

public abstract class RenderTypeFeatureRenderer implements FeatureRenderer {
   private RenderTypeFeatureRenderer.@Nullable Group currentGroup;
   private final List groups = new ArrayList();

   protected abstract void buildGroup(FeatureFrameContext context, List submits);

   protected final VertexConsumer getVertexBuilder(final RenderType renderType) {
      return this.currentGroup().getVertexBuilder(renderType);
   }

   private RenderTypeFeatureRenderer.Group currentGroup() {
      return (RenderTypeFeatureRenderer.Group)Objects.requireNonNull(this.currentGroup, "Not preparing group");
   }

   public final void prepareGroup(final FeatureFrameContext context, final List submits, final boolean strictlyOrdered) {
      this.currentGroup = new RenderTypeFeatureRenderer.Group(context.stagedVertexBuffer(), !strictlyOrdered);
      this.buildGroup(context, submits);
      this.groups.add(this.currentGroup);

      for(RenderTypeFeatureRenderer.Group group : this.groups) {
         for(StagedVertexBuffer.Draw draw : group.draws) {
            context.stagedVertexBuffer().requestIndexCount(draw);
         }
      }

      this.currentGroup = null;
   }

   public void executeGroup(final FeatureFrameContext context, final @Nullable OitStage stage, final RenderPass renderPass, final int groupIndex, final List submits, final boolean strictlyOrdered) {
      RenderTypeFeatureRenderer.Group group = (RenderTypeFeatureRenderer.Group)this.groups.get(groupIndex);

      for(int i = 0; i < group.draws.size(); ++i) {
         PreparedRenderType renderType = (PreparedRenderType)group.drawRenderTypes.get(i);
         StagedVertexBuffer.ExecuteInfo info = context.stagedVertexBuffer().getExecuteInfo((StagedVertexBuffer.Draw)group.draws.get(i));
         if (info != null) {
            if (stage != null) {
               renderType.drawFromBufferOit(info, stage, renderPass);
            } else {
               renderType.drawFromBuffer(info, renderPass);
            }
         }
      }

   }

   public void finishExecute(final FeatureFrameContext context) {
      this.groups.clear();
   }

   private static class Group {
      private final StagedVertexBuffer stagedBuffer;
      private final boolean canReorder;
      private final List draws = new ArrayList();
      private final List drawRenderTypes = new ArrayList();
      private @Nullable RenderType lastRenderType;
      private StagedVertexBuffer.@Nullable Draw lastDraw;

      private Group(final StagedVertexBuffer stagedBuffer, final boolean canReorder) {
         this.stagedBuffer = stagedBuffer;
         this.canReorder = canReorder;
      }

      public VertexConsumer getVertexBuilder(final RenderType renderType) {
         if (this.lastDraw == null || this.lastRenderType != renderType || !renderType.canConsolidateConsecutiveGeometry()) {
            this.lastDraw = this.getOrAddDraw(renderType);
            this.lastRenderType = renderType;
         }

         return this.stagedBuffer.getVertexBuilder(this.lastDraw);
      }

      private StagedVertexBuffer.Draw getOrAddDraw(final RenderType renderType) {
         PreparedRenderType preparedRenderType = renderType.prepare();
         int existingIndex = this.canReorder && renderType.canConsolidateConsecutiveGeometry() ? this.drawRenderTypes.indexOf(preparedRenderType) : -1;
         if (existingIndex != -1) {
            return (StagedVertexBuffer.Draw)this.draws.get(existingIndex);
         } else {
            boolean useImprovedTransparency = Minecraft.getInstance().gameRenderer.useImprovedTransparency();
            VertexSorting quadSorting = renderType.sortOnUpload() && !useImprovedTransparency ? RenderSystem.getProjectionType().vertexSorting() : null;
            StagedVertexBuffer.Draw draw = this.stagedBuffer.appendDraw(renderType.format(), renderType.primitiveTopology(), quadSorting);
            this.draws.add(draw);
            this.drawRenderTypes.add(preparedRenderType);
            return draw;
         }
      }
   }
}
