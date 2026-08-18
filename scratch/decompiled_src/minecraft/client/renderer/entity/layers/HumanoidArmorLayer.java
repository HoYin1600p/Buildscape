package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;

public class HumanoidArmorLayer extends RenderLayer {
   private final ArmorModelSet modelSet;
   private final ArmorModelSet babyModelSet;
   private final EquipmentLayerRenderer equipmentRenderer;

   public HumanoidArmorLayer(final RenderLayerParent renderer, final ArmorModelSet modelSet, final EquipmentLayerRenderer equipmentRenderer) {
      this(renderer, modelSet, modelSet, equipmentRenderer);
   }

   public HumanoidArmorLayer(final RenderLayerParent renderer, final ArmorModelSet modelSet, final ArmorModelSet babyModelSet, final EquipmentLayerRenderer equipmentRenderer) {
      super(renderer);
      this.modelSet = modelSet;
      this.babyModelSet = babyModelSet;
      this.equipmentRenderer = equipmentRenderer;
   }

   public static boolean shouldRender(final ItemStack itemStack, final EquipmentSlot slot) {
      Equippable equippable = (Equippable)itemStack.get(DataComponents.EQUIPPABLE);
      return equippable != null && shouldRender(equippable, slot);
   }

   private static boolean shouldRender(final Equippable equippable, final EquipmentSlot slot) {
      return equippable.assetId().isPresent() && equippable.slot() == slot;
   }

   public void submit(final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final int lightCoords, final HumanoidRenderState state, final float yRot, final float xRot) {
      this.renderArmorPiece(poseStack, submitNodeCollector, state.chestEquipment, EquipmentSlot.CHEST, lightCoords, state);
      this.renderArmorPiece(poseStack, submitNodeCollector, state.legsEquipment, EquipmentSlot.LEGS, lightCoords, state);
      this.renderArmorPiece(poseStack, submitNodeCollector, state.feetEquipment, EquipmentSlot.FEET, lightCoords, state);
      this.renderArmorPiece(poseStack, submitNodeCollector, state.headEquipment, EquipmentSlot.HEAD, lightCoords, state);
   }

   private void renderArmorPiece(final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final ItemStack itemStack, final EquipmentSlot slot, final int lightCoords, final HumanoidRenderState state) {
      Equippable equippable = (Equippable)itemStack.get(DataComponents.EQUIPPABLE);
      if (equippable != null && shouldRender(equippable, slot)) {
         HumanoidModel model = (A)this.getArmorModel(state, slot);
         EquipmentClientInfo.LayerType layerType = state.isBaby && state.entityType != EntityTypes.ARMOR_STAND ? EquipmentClientInfo.LayerType.HUMANOID_BABY : (this.usesInnerModel(slot) ? EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS : EquipmentClientInfo.LayerType.HUMANOID);
         this.equipmentRenderer.renderLayers(layerType, (ResourceKey)equippable.assetId().orElseThrow(), model, state, itemStack, poseStack, submitNodeCollector, lightCoords, state.outlineColor);
      }
   }

   private HumanoidModel getArmorModel(final HumanoidRenderState state, final EquipmentSlot slot) {
      return (HumanoidModel)(state.isBaby ? this.babyModelSet : this.modelSet).get(slot);
   }

   private boolean usesInnerModel(final EquipmentSlot slot) {
      return slot == EquipmentSlot.LEGS;
   }
}
