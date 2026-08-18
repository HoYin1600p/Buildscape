package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.ZombieRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.zombie.Zombie;

public abstract class AbstractZombieRenderer extends HumanoidMobRenderer {
   private static final Identifier ZOMBIE_LOCATION = Identifier.withDefaultNamespace("textures/entity/zombie/zombie.png");
   private static final Identifier BABY_ZOMBIE_LOCATION = Identifier.withDefaultNamespace("textures/entity/zombie/zombie_baby.png");

   protected AbstractZombieRenderer(final EntityRendererProvider.Context context, final HumanoidModel model, final HumanoidModel babyModel, final ArmorModelSet armorSet, final ArmorModelSet babyArmorSet) {
      super(context, model, babyModel, 0.5F);
      this.addLayer(new HumanoidArmorLayer(this, armorSet, babyArmorSet, context.getEquipmentRenderer()));
   }

   public Identifier getTextureLocation(final ZombieRenderState state) {
      return state.isBaby ? BABY_ZOMBIE_LOCATION : ZOMBIE_LOCATION;
   }

   public void extractRenderState(final Zombie entity, final ZombieRenderState state, final float partialTicks) {
      super.extractRenderState((Mob)entity, (HumanoidRenderState)state, partialTicks);
      state.isAggressive = entity.isAggressive();
      state.isConverting = entity.isUnderWaterConverting();
   }

   protected boolean isShaking(final ZombieRenderState state) {
      return super.isShaking(state) || state.isConverting;
   }
}
