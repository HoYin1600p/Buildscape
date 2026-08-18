package net.minecraft.client.renderer.oit;

import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.client.renderer.RenderPipelines;

public record OitPipelineSet(RenderPipeline depthBoundsPipeline, RenderPipeline transmittancePipeline, RenderPipeline accumulatePipeline) {
   public RenderPipeline getPipeline(final OitStage stage) {
      RenderPipeline var10000;
      switch (stage) {
         case DEPTH_BOUNDS:
            var10000 = this.depthBoundsPipeline;
            break;
         case TRANSMITTANCE:
            var10000 = this.transmittancePipeline;
            break;
         case ACCUMULATE:
            var10000 = this.accumulatePipeline;
            break;
         default:
            throw new IllegalArgumentException("Unsupported OIT stage.");
      }

      return var10000;
   }

   public static OitPipelineSet.Builder builder(final String locationSuffix, final RenderPipeline.Builder builder) {
      return new OitPipelineSet.Builder(builder.buildSnippet(), locationSuffix);
   }

   public static class Builder {
      private static final Consumer DISABLE_DEPTH_TEST = (builder) -> builder.withDepthStencilState(Optional.empty());
      private final RenderPipeline.Snippet baseSnippet;
      private final String locationSuffix;
      private Optional depthBoundsModifier = Optional.empty();
      private Optional transmittanceModifier = Optional.empty();
      private Optional accumulateModifier = Optional.empty();

      public Builder(final RenderPipeline.Snippet baseSnippet, final String locationSuffix) {
         this.baseSnippet = baseSnippet;
         this.locationSuffix = locationSuffix;
      }

      public OitPipelineSet.Builder withDepthBoundsModifier(final Consumer modifier) {
         this.depthBoundsModifier = composeModifiers(this.depthBoundsModifier, modifier);
         return this;
      }

      public OitPipelineSet.Builder withTransmittanceModifier(final Consumer modifier) {
         this.transmittanceModifier = composeModifiers(this.transmittanceModifier, modifier);
         return this;
      }

      public OitPipelineSet.Builder withAccumulateModifier(final Consumer modifier) {
         this.accumulateModifier = composeModifiers(this.accumulateModifier, modifier);
         return this;
      }

      public OitPipelineSet.Builder withoutDepthTest() {
         return this.withDepthBoundsModifier(DISABLE_DEPTH_TEST).withTransmittanceModifier(DISABLE_DEPTH_TEST).withAccumulateModifier(DISABLE_DEPTH_TEST);
      }

      private static Optional composeModifiers(final Optional currentModifier, final Consumer newModifier) {
         return currentModifier.isPresent() ? Optional.of((Consumer)(builder) -> {
            ((Consumer)currentModifier.get()).accept(builder);
            newModifier.accept(builder);
         }) : Optional.of(newModifier);
      }

      public OitPipelineSet build() {
         RenderPipeline.Builder depthBoundsBuilder = RenderPipeline.builder(new RenderPipeline.Snippet[]{this.baseSnippet, RenderPipelines.OIT_DEPTH_BOUNDS_SNIPPET}).withLocation("pipeline/oit_depth_bounds_" + this.locationSuffix);
         this.depthBoundsModifier.ifPresent((modifier) -> modifier.accept(depthBoundsBuilder));
         RenderPipeline.Builder transmittanceBuilder = RenderPipeline.builder(new RenderPipeline.Snippet[]{this.baseSnippet, RenderPipelines.OIT_TRANSMITTANCE_SNIPPET}).withLocation("pipeline/oit_transmittance_" + this.locationSuffix);
         this.transmittanceModifier.ifPresent((modifier) -> modifier.accept(transmittanceBuilder));
         RenderPipeline.Builder accumulateBuilder = RenderPipeline.builder(new RenderPipeline.Snippet[]{this.baseSnippet, RenderPipelines.OIT_ACCUMULATE_SNIPPET}).withLocation("pipeline/oit_accumulate_" + this.locationSuffix);
         this.accumulateModifier.ifPresent((modifier) -> modifier.accept(accumulateBuilder));
         return new OitPipelineSet(depthBoundsBuilder.build(), transmittanceBuilder.build(), accumulateBuilder.build());
      }
   }
}
