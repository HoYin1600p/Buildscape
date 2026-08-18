package net.minecraft.client.renderer.item;

import com.google.common.base.Suppliers;
import com.mojang.math.Transformation;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderers;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4fc;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

public class SpecialModelWrapper implements ItemModel {
   private final SpecialModelRenderer specialRenderer;
   private final ModelRenderProperties properties;
   private final Supplier extents;
   private final Matrix4fc transformation;

   public SpecialModelWrapper(final SpecialModelRenderer specialRenderer, final ModelRenderProperties properties, final Matrix4fc transformation) {
      this.specialRenderer = specialRenderer;
      this.properties = properties;
      this.extents = Suppliers.memoize(() -> {
         Set results = new HashSet();
         specialRenderer.getExtents(results::add);
         return (Vector3fc[])results.toArray(new Vector3fc[0]);
      });
      this.transformation = transformation;
   }

   public void update(final ItemStackRenderState output, final ItemStack item, final ItemModelResolver resolver, final ItemDisplayContext displayContext, final @Nullable ClientLevel level, final @Nullable ItemOwner owner, final int seed) {
      output.appendModelIdentityElement(this);
      ItemStackRenderState.LayerRenderState layer = output.newLayer();
      if (item.hasFoil()) {
         ItemStackRenderState.FoilType foilType = ItemStackRenderState.FoilType.STANDARD;
         layer.setFoilType(foilType);
         output.setAnimated();
         output.appendModelIdentityElement(foilType);
      }

      Object argument = (T)this.specialRenderer.extractArgument(item);
      layer.setExtents(this.extents);
      layer.setLocalTransform(this.transformation);
      layer.setupSpecialModel(this.specialRenderer, argument);
      if (argument != null) {
         output.appendModelIdentityElement(argument);
      }

      this.properties.applyToLayer(layer, displayContext);
   }

   public static record Unbaked(Identifier base, Optional transformation, SpecialModelRenderer.Unbaked specialModel) implements ItemModel.Unbaked {
      public static final MapCodec MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Identifier.CODEC.fieldOf("base").forGetter(SpecialModelWrapper.Unbaked::base), Transformation.EXTENDED_CODEC.optionalFieldOf("transformation").forGetter(SpecialModelWrapper.Unbaked::transformation), SpecialModelRenderers.CODEC.fieldOf("model").forGetter(SpecialModelWrapper.Unbaked::specialModel)).apply(i, SpecialModelWrapper.Unbaked::new));

      public void resolveDependencies(final ResolvableModel.Resolver resolver) {
         resolver.markDependency(this.base);
      }

      public ItemModel bake(final ItemModel.BakingContext context, final Matrix4fc transformation) {
         Matrix4fc modelTransform = Transformation.compose(transformation, this.transformation);
         SpecialModelRenderer bakedSpecialModel = this.specialModel.bake(context);
         if (bakedSpecialModel == null) {
            return context.missingItemModel(modelTransform);
         } else {
            ModelRenderProperties properties = this.getProperties(context);
            return new SpecialModelWrapper(bakedSpecialModel, properties, modelTransform);
         }
      }

      private ModelRenderProperties getProperties(final ItemModel.BakingContext context) {
         ModelBaker baker = context.blockModelBaker();
         ResolvedModel model = baker.getModel(this.base);
         TextureSlots textureSlots = model.getTopTextureSlots();
         return ModelRenderProperties.fromResolvedModel(baker, model, textureSlots);
      }

      public MapCodec type() {
         return MAP_CODEC;
      }
   }
}
