package net.minecraft.client.renderer.block;

import com.mojang.math.Transformation;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.properties.select.SelectBlockModelProperty;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4fc;
import org.jspecify.annotations.Nullable;

public class SelectBlockModel implements BlockModel {
   private final SelectBlockModelProperty property;
   private final SelectBlockModel.ModelSelector models;

   public SelectBlockModel(final SelectBlockModelProperty property, final SelectBlockModel.ModelSelector models) {
      this.property = property;
      this.models = models;
   }

   public void update(final BlockModelRenderState output, final BlockState blockState, final BlockDisplayContext displayContext, final long seed) {
      Object value = (T)this.property.get(blockState, displayContext);
      BlockModel model = this.models.get(value);
      if (model != null) {
         model.update(output, blockState, displayContext, seed);
      }

   }

   @FunctionalInterface
   public interface ModelSelector {
      @Nullable BlockModel get(@Nullable Object value);
   }

   public static record SwitchCase(List values, BlockModel.Unbaked model) {
   }

   public static record Unbaked(Optional transformation, SelectBlockModel.UnbakedSwitch unbakedSwitch, Optional fallback) implements BlockModel.Unbaked {
      public BlockModel bake(final BlockModel.BakingContext context, final Matrix4fc transformation) {
         Matrix4fc childTransform = Transformation.compose(transformation, this.transformation);
         BlockModel bakedFallback = (BlockModel)this.fallback.map((m) -> m.bake(context, childTransform)).orElse(context.missingBlockModel());
         return this.unbakedSwitch.bake(context, childTransform, bakedFallback);
      }
   }

   public static record UnbakedSwitch(SelectBlockModelProperty property, List cases) {
      public BlockModel bake(final BlockModel.BakingContext context, final Matrix4fc transformation, final BlockModel fallback) {
         Object2ObjectMap bakedModels = new Object2ObjectOpenHashMap();

         for(SelectBlockModel.SwitchCase c : this.cases) {
            BlockModel.Unbaked caseModel = c.model;
            BlockModel bakedCaseModel = caseModel.bake(context, transformation);

            for(Object value : c.values) {
               bakedModels.put(value, bakedCaseModel);
            }
         }

         bakedModels.defaultReturnValue(fallback);
         return new SelectBlockModel(this.property, bakedModels::get);
      }
   }
}
