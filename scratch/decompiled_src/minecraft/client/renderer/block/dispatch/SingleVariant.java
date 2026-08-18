package net.minecraft.client.renderer.block.dispatch;

import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.util.RandomSource;

public class SingleVariant implements BlockStateModel {
   private final BlockStateModelPart model;

   public SingleVariant(final BlockStateModelPart model) {
      this.model = model;
   }

   public void collectParts(final RandomSource random, final List output) {
      output.add(this.model);
   }

   public Material.Baked particleMaterial() {
      return this.model.particleMaterial();
   }

   public @BakedQuad.MaterialFlags int materialFlags() {
      return this.model.materialFlags();
   }

   public static record Unbaked(Variant variant) implements BlockStateModel.Unbaked {
      public static final Codec CODEC = Variant.CODEC.xmap(SingleVariant.Unbaked::new, SingleVariant.Unbaked::variant);

      public BlockStateModel bake(final ModelBaker modelBakery) {
         return new SingleVariant(this.variant.bake(modelBakery));
      }

      public void resolveDependencies(final ResolvableModel.Resolver resolver) {
         this.variant.resolveDependencies(resolver);
      }
   }
}
