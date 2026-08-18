package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.animal.fish.TropicalFishLargeModel;
import net.minecraft.client.model.animal.fish.TropicalFishSmallModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.TropicalFishRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.animal.fish.TropicalFish;

public class TropicalFishPatternLayer extends RenderLayer {
   private static final Identifier KOB_TEXTURE = Identifier.withDefaultNamespace("textures/entity/fish/tropical_a_pattern_1.png");
   private static final Identifier SUNSTREAK_TEXTURE = Identifier.withDefaultNamespace("textures/entity/fish/tropical_a_pattern_2.png");
   private static final Identifier SNOOPER_TEXTURE = Identifier.withDefaultNamespace("textures/entity/fish/tropical_a_pattern_3.png");
   private static final Identifier DASHER_TEXTURE = Identifier.withDefaultNamespace("textures/entity/fish/tropical_a_pattern_4.png");
   private static final Identifier BRINELY_TEXTURE = Identifier.withDefaultNamespace("textures/entity/fish/tropical_a_pattern_5.png");
   private static final Identifier SPOTTY_TEXTURE = Identifier.withDefaultNamespace("textures/entity/fish/tropical_a_pattern_6.png");
   private static final Identifier FLOPPER_TEXTURE = Identifier.withDefaultNamespace("textures/entity/fish/tropical_b_pattern_1.png");
   private static final Identifier STRIPEY_TEXTURE = Identifier.withDefaultNamespace("textures/entity/fish/tropical_b_pattern_2.png");
   private static final Identifier GLITTER_TEXTURE = Identifier.withDefaultNamespace("textures/entity/fish/tropical_b_pattern_3.png");
   private static final Identifier BLOCKFISH_TEXTURE = Identifier.withDefaultNamespace("textures/entity/fish/tropical_b_pattern_4.png");
   private static final Identifier BETTY_TEXTURE = Identifier.withDefaultNamespace("textures/entity/fish/tropical_b_pattern_5.png");
   private static final Identifier CLAYFISH_TEXTURE = Identifier.withDefaultNamespace("textures/entity/fish/tropical_b_pattern_6.png");
   private final TropicalFishSmallModel modelSmall;
   private final TropicalFishLargeModel modelLarge;

   public TropicalFishPatternLayer(final RenderLayerParent renderer, final EntityModelSet modelSet) {
      super(renderer);
      this.modelSmall = new TropicalFishSmallModel(modelSet.bakeLayer(ModelLayers.TROPICAL_FISH_SMALL_PATTERN));
      this.modelLarge = new TropicalFishLargeModel(modelSet.bakeLayer(ModelLayers.TROPICAL_FISH_LARGE_PATTERN));
   }

   public void submit(final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final int lightCoords, final TropicalFishRenderState state, final float yRot, final float xRot) {
      TropicalFish.Pattern variant = state.pattern;
      Object var10000;
      switch (variant.base()) {
         case SMALL:
            var10000 = this.modelSmall;
            break;
         case LARGE:
            var10000 = this.modelLarge;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      EntityModel model = (EntityModel<TropicalFishRenderState>)var10000;
      Identifier var10;
      switch (variant) {
         case KOB:
            var10 = KOB_TEXTURE;
            break;
         case SUNSTREAK:
            var10 = SUNSTREAK_TEXTURE;
            break;
         case SNOOPER:
            var10 = SNOOPER_TEXTURE;
            break;
         case DASHER:
            var10 = DASHER_TEXTURE;
            break;
         case BRINELY:
            var10 = BRINELY_TEXTURE;
            break;
         case SPOTTY:
            var10 = SPOTTY_TEXTURE;
            break;
         case FLOPPER:
            var10 = FLOPPER_TEXTURE;
            break;
         case STRIPEY:
            var10 = STRIPEY_TEXTURE;
            break;
         case GLITTER:
            var10 = GLITTER_TEXTURE;
            break;
         case BLOCKFISH:
            var10 = BLOCKFISH_TEXTURE;
            break;
         case BETTY:
            var10 = BETTY_TEXTURE;
            break;
         case CLAYFISH:
            var10 = CLAYFISH_TEXTURE;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      Identifier patternTexture = var10;
      coloredCutoutModelCopyLayerRender(model, patternTexture, poseStack, submitNodeCollector, lightCoords, state, state.patternColor, 1);
   }
}
