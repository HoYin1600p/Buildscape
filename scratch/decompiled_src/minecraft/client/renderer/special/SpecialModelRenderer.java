package net.minecraft.client.renderer.special;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import java.util.function.Consumer;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public interface SpecialModelRenderer {
   void submit(@Nullable Object argument, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, int overlayCoords, boolean hasFoil, final int outlineColor);

   void getExtents(Consumer output);

   @Nullable Object extractArgument(ItemStack stack);

   public interface BakingContext {
      EntityModelSet entityModelSet();

      SpriteGetter sprites();

      PlayerSkinRenderCache playerSkinRenderCache();
   }

   public interface Unbaked {
      @Nullable SpecialModelRenderer bake(SpecialModelRenderer.BakingContext context);

      MapCodec type();
   }
}
