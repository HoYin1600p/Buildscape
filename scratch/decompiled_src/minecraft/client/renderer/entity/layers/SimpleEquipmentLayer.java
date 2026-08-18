package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.Function;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;
import org.jspecify.annotations.Nullable;

public class SimpleEquipmentLayer extends RenderLayer {
   private final EquipmentLayerRenderer equipmentRenderer;
   private final EquipmentClientInfo.LayerType layer;
   private final Function itemGetter;
   private final EntityModel adultModel;
   private final @Nullable EntityModel babyModel;
   private final int order;

   public SimpleEquipmentLayer(final RenderLayerParent renderer, final EquipmentLayerRenderer equipmentRenderer, final EquipmentClientInfo.LayerType layer, final Function itemGetter, final EntityModel adultModel, final @Nullable EntityModel babyModel, final int order) {
      super(renderer);
      this.equipmentRenderer = equipmentRenderer;
      this.layer = layer;
      this.itemGetter = itemGetter;
      this.adultModel = adultModel;
      this.babyModel = babyModel;
      this.order = order;
   }

   public SimpleEquipmentLayer(final RenderLayerParent renderer, final EquipmentLayerRenderer equipmentRenderer, final EquipmentClientInfo.LayerType layer, final Function itemGetter, final EntityModel adultModel, final @Nullable EntityModel babyModel) {
      this(renderer, equipmentRenderer, layer, itemGetter, adultModel, babyModel, 0);
   }

   public void submit(final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final int lightCoords, final LivingEntityRenderState state, final float yRot, final float xRot) {
      ItemStack equipment = (ItemStack)this.itemGetter.apply(state);
      Equippable equippable = (Equippable)equipment.get(DataComponents.EQUIPPABLE);
      if (equippable != null && !equippable.assetId().isEmpty() && (!state.isBaby || this.babyModel != null)) {
         EntityModel model = (EM)(state.isBaby ? this.babyModel : this.adultModel);
         this.equipmentRenderer.renderLayers(this.layer, (ResourceKey)equippable.assetId().get(), model, state, equipment, poseStack, submitNodeCollector, lightCoords, (Identifier)null, state.outlineColor, this.order);
      }
   }
}
