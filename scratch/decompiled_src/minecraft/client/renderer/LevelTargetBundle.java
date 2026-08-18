package net.minecraft.client.renderer;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.resource.ResourceHandle;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public class LevelTargetBundle implements PostChain.TargetBundle {
   public static final Identifier MAIN_TARGET_ID = PostChain.MAIN_TARGET_ID;
   public static final Identifier ENTITY_OUTLINE_TARGET_ID = Identifier.withDefaultNamespace("entity_outline");
   public static final Set MAIN_TARGETS = Set.of(MAIN_TARGET_ID);
   public static final Set OUTLINE_TARGETS = Set.of(MAIN_TARGET_ID, ENTITY_OUTLINE_TARGET_ID);
   public ResourceHandle main = ResourceHandle.invalid();
   public ResourceHandle alwaysOnTopDepth = ResourceHandle.invalid();
   public ResourceHandle depthBounds = ResourceHandle.invalid();
   public ResourceHandle depthBoundsCopy = ResourceHandle.invalid();
   public final List transmittance = new ArrayList();
   public ResourceHandle accumulate = ResourceHandle.invalid();
   public ResourceHandle oitCloudDepth = ResourceHandle.invalid();
   public ResourceHandle oitTerrainWithWaterPatchDepth = ResourceHandle.invalid();
   public @Nullable ResourceHandle entityOutline;

   public LevelTargetBundle() {
      for(int i = 0; i < LevelRenderer.OIT_TRANSMITTANCE_TARGET_COUNT; ++i) {
         this.transmittance.add(ResourceHandle.invalid());
      }

   }

   public void replace(final Identifier id, final ResourceHandle handle) {
      if (id.equals(MAIN_TARGET_ID)) {
         this.main = handle;
      } else {
         if (!id.equals(ENTITY_OUTLINE_TARGET_ID)) {
            throw new IllegalArgumentException("No target with id " + String.valueOf(id));
         }

         this.entityOutline = handle;
      }

   }

   public @Nullable ResourceHandle get(final Identifier id) {
      if (id.equals(MAIN_TARGET_ID)) {
         return this.main;
      } else {
         return id.equals(ENTITY_OUTLINE_TARGET_ID) ? this.entityOutline : null;
      }
   }

   public void clear() {
      this.main = ResourceHandle.invalid();
      this.entityOutline = null;
   }
}
