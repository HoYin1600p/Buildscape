package net.minecraft.client.renderer.entity;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.UvMapping;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.scores.Team;
import org.jspecify.annotations.Nullable;

public abstract class LivingEntityRenderer extends EntityRenderer implements RenderLayerParent {
   private static final float EYE_BED_OFFSET = 0.1F;
   protected EntityModel model;
   protected final ItemModelResolver itemModelResolver;
   protected final List layers = Lists.newArrayList();

   public LivingEntityRenderer(final EntityRendererProvider.Context context, final EntityModel model, final float shadow) {
      super(context);
      this.itemModelResolver = context.getItemModelResolver();
      this.model = model;
      this.shadowRadius = shadow;
   }

   protected final boolean addLayer(final RenderLayer layer) {
      return this.layers.add(layer);
   }

   public EntityModel getModel() {
      return this.model;
   }

   protected AABB getBoundingBoxForCulling(final LivingEntity entity, final float partialTicks) {
      AABB aabb = super.getBoundingBoxForCulling(entity, partialTicks);
      if (entity.getItemBySlot(EquipmentSlot.HEAD).is(Items.DRAGON_HEAD)) {
         float extraSize = 0.5F;
         return aabb.inflate(0.5D, 0.5D, 0.5D);
      } else {
         return aabb;
      }
   }

   public void submit(final LivingEntityRenderState state, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final CameraRenderState camera) {
      poseStack.pushPose();
      if (state.hasPose(Pose.SLEEPING)) {
         Direction bedOrientation = state.bedOrientation;
         if (bedOrientation != null) {
            float headOffset = state.eyeHeight - 0.1F;
            poseStack.translate((float)(-bedOrientation.getStepX()) * headOffset, 0.0F, (float)(-bedOrientation.getStepZ()) * headOffset);
         }
      }

      float scale = state.scale;
      poseStack.scale(scale, scale, scale);
      this.setupRotations(state, poseStack, state.bodyRot, scale);
      poseStack.scale(-1.0F, -1.0F, 1.0F);
      this.scale(state, poseStack);
      poseStack.translate(0.0F, -1.501F, 0.0F);
      boolean isBodyVisible = this.isBodyVisible(state);
      boolean forceTransparent = !isBodyVisible && !state.isInvisibleToPlayer;
      RenderType renderType = this.getRenderType(state, isBodyVisible, forceTransparent, state.appearsGlowing());
      if (renderType != null) {
         int overlayCoords = getOverlayCoords(state, this.getWhiteOverlayProgress(state));
         int baseColor = forceTransparent ? 654311423 : -1;
         int tintedColor = ARGB.multiply(baseColor, this.getModelTint(state));
         submitNodeCollector.submitModel(this.model, state, poseStack, renderType, state.lightCoords, overlayCoords, tintedColor, (UvMapping)null, state.outlineColor);
      }

      if (this.shouldRenderLayers(state) && !this.layers.isEmpty()) {
         this.model.setupAnim(state);

         for(RenderLayer layer : this.layers) {
            layer.submit(poseStack, submitNodeCollector, state.lightCoords, state, state.yRot, state.xRot);
         }
      }

      poseStack.popPose();
      super.submit(state, poseStack, submitNodeCollector, camera);
   }

   protected boolean shouldRenderLayers(final LivingEntityRenderState state) {
      return true;
   }

   protected int getModelTint(final LivingEntityRenderState state) {
      return -1;
   }

   public abstract Identifier getTextureLocation(final LivingEntityRenderState state);

   protected @Nullable RenderType getRenderType(final LivingEntityRenderState state, final boolean isBodyVisible, final boolean forceTransparent, final boolean appearGlowing) {
      Identifier texture = this.getTextureLocation(state);
      if (forceTransparent) {
         return RenderTypes.entityTranslucentCull(texture);
      } else if (isBodyVisible) {
         return this.model.renderType(texture);
      } else {
         return appearGlowing ? RenderTypes.outline(texture) : null;
      }
   }

   public static int getOverlayCoords(final LivingEntityRenderState state, final float whiteOverlayProgress) {
      return OverlayTexture.pack(OverlayTexture.u(whiteOverlayProgress), OverlayTexture.v(state.hasRedOverlay));
   }

   protected boolean isBodyVisible(final LivingEntityRenderState state) {
      return !state.isInvisible;
   }

   private static float sleepDirectionToRotation(final Direction direction) {
      float var10000;
      switch (direction) {
         case SOUTH:
            var10000 = 90.0F;
            break;
         case WEST:
            var10000 = 0.0F;
            break;
         case NORTH:
            var10000 = 270.0F;
            break;
         case EAST:
            var10000 = 180.0F;
            break;
         default:
            var10000 = 0.0F;
      }

      return var10000;
   }

   protected boolean isShaking(final LivingEntityRenderState state) {
      return state.isFullyFrozen;
   }

   protected void setupRotations(final LivingEntityRenderState state, final PoseStack poseStack, float bodyRot, final float entityScale) {
      if (this.isShaking(state)) {
         bodyRot += (float)(Math.cos((double)((float)Mth.floor(state.ageInTicks) * 3.25F)) * Math.PI * (double)0.4F);
      }

      if (!state.hasPose(Pose.SLEEPING)) {
         poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - bodyRot));
      }

      if (state.deathTime > 0.0F) {
         float fall = (state.deathTime - 1.0F) / 20.0F * 1.6F;
         fall = Mth.sqrt(fall);
         if (fall > 1.0F) {
            fall = 1.0F;
         }

         poseStack.mulPose(Axis.ZP.rotationDegrees(fall * this.getFlipDegrees()));
      } else if (state.isAutoSpinAttack) {
         poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F - state.xRot));
         poseStack.mulPose(Axis.YP.rotationDegrees(state.ageInTicks * -75.0F));
      } else if (state.hasPose(Pose.SLEEPING)) {
         Direction bedOrientation = state.bedOrientation;
         float angle = bedOrientation != null ? sleepDirectionToRotation(bedOrientation) : bodyRot;
         poseStack.mulPose(Axis.YP.rotationDegrees(angle));
         poseStack.mulPose(Axis.ZP.rotationDegrees(this.getFlipDegrees()));
         poseStack.mulPose(Axis.YP.rotationDegrees(270.0F));
      } else if (state.isUpsideDown) {
         poseStack.translate(0.0F, (state.boundingBoxHeight + 0.1F) / entityScale, 0.0F);
         poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
      }

   }

   protected float getFlipDegrees() {
      return 90.0F;
   }

   protected float getWhiteOverlayProgress(final LivingEntityRenderState state) {
      return 0.0F;
   }

   protected void scale(final LivingEntityRenderState state, final PoseStack poseStack) {
   }

   protected boolean shouldShowName(final LivingEntity entity, final double distanceToCameraSq) {
      if (entity.isDiscrete()) {
         float maxDist = 32.0F;
         if (distanceToCameraSq >= 1024.0D) {
            return false;
         }
      }

      Minecraft minecraft = Minecraft.getInstance();
      LocalPlayer player = minecraft.player;
      boolean isVisibleToPlayer = !entity.isInvisibleTo(player);
      if (entity != player) {
         Team team = entity.getTeam();
         Team myTeam = player.getTeam();
         if (team != null) {
            Team.Visibility visibility = team.getNameTagVisibility();
            boolean var10000;
            switch (visibility) {
               case ALWAYS:
                  var10000 = isVisibleToPlayer;
                  break;
               case NEVER:
                  var10000 = false;
                  break;
               case HIDE_FOR_OTHER_TEAMS:
                  var10000 = myTeam == null ? isVisibleToPlayer : team.isAlliedTo(myTeam) && (team.canSeeFriendlyInvisibles() || isVisibleToPlayer);
                  break;
               case HIDE_FOR_OWN_TEAM:
                  var10000 = myTeam == null ? isVisibleToPlayer : !team.isAlliedTo(myTeam) && isVisibleToPlayer;
                  break;
               default:
                  throw new MatchException((String)null, (Throwable)null);
            }

            return var10000;
         }
      }

      return !Minecraft.getInstance().gui.hud.isHidden() && entity != minecraft.getCameraEntity() && isVisibleToPlayer && !entity.isVehicle();
   }

   public boolean isEntityUpsideDown(final LivingEntity mob) {
      Component customName = mob.getCustomName();
      return customName != null && isUpsideDownName(customName.getString());
   }

   protected static boolean isUpsideDownName(final String name) {
      return "Dinnerbone".equals(name) || "Grumm".equals(name);
   }

   protected float getShadowRadius(final LivingEntityRenderState state) {
      return super.getShadowRadius(state) * state.scale;
   }

   public void extractRenderState(final LivingEntity entity, final LivingEntityRenderState state, final float partialTicks) {
      super.extractRenderState(entity, state, partialTicks);
      float headRot = Mth.rotLerp(partialTicks, entity.yHeadRotO, entity.yHeadRot);
      state.bodyRot = solveBodyRot(entity, headRot, partialTicks);
      state.yRot = Mth.wrapDegrees(headRot - state.bodyRot);
      state.xRot = entity.getXRot(partialTicks);
      state.isUpsideDown = this.isEntityUpsideDown(entity);
      if (state.isUpsideDown) {
         state.xRot *= -1.0F;
         state.yRot *= -1.0F;
      }

      if (!entity.isPassenger() && entity.isAlive()) {
         state.walkAnimationPos = entity.walkAnimation.position(partialTicks);
         state.walkAnimationSpeed = entity.walkAnimation.speed(partialTicks);
      } else {
         state.walkAnimationPos = 0.0F;
         state.walkAnimationSpeed = 0.0F;
      }

      Entity minecraft = entity.getVehicle();
      if (minecraft instanceof LivingEntity vehicle) {
         state.wornHeadAnimationPos = vehicle.walkAnimation.position(partialTicks);
      } else {
         state.wornHeadAnimationPos = state.walkAnimationPos;
      }

      state.scale = entity.getScale();
      state.ageScale = entity.getAgeScale();
      state.pose = entity.getPose();
      state.bedOrientation = entity.getBedOrientation();
      if (state.bedOrientation != null) {
         state.eyeHeight = entity.getEyeHeight(Pose.STANDING);
      }

      label48: {
         state.isFullyFrozen = entity.isFullyFrozen();
         state.isBaby = entity.isBaby();
         state.isInWater = entity.isInWater();
         state.isAutoSpinAttack = entity.isAutoSpinAttack();
         state.ticksSinceKineticHitFeedback = entity.getTicksSinceLastKineticHitFeedback(partialTicks);
         state.hasRedOverlay = entity.hurtTime > 0 || entity.deathTime > 0;
         ItemStack headItem = entity.getItemBySlot(EquipmentSlot.HEAD);
         Item var8 = headItem.getItem();
         if (var8 instanceof BlockItem blockItem) {
            Block var12 = blockItem.getBlock();
            if (var12 instanceof AbstractSkullBlock skullBlock) {
               state.wornHeadType = skullBlock.getType();
               state.wornHeadProfile = (ResolvableProfile)headItem.get(DataComponents.PROFILE);
               state.headItem.clear();
               break label48;
            }
         }

         state.wornHeadType = null;
         state.wornHeadProfile = null;
         if (!HumanoidArmorLayer.shouldRender(headItem, EquipmentSlot.HEAD)) {
            this.itemModelResolver.updateForLiving(state.headItem, headItem, ItemDisplayContext.HEAD, entity);
         } else {
            state.headItem.clear();
         }
      }

      state.deathTime = entity.deathTime > 0 ? (float)entity.deathTime + partialTicks : 0.0F;
      Minecraft minecraft = Minecraft.getInstance();
      state.isInvisibleToPlayer = state.isInvisible && entity.isInvisibleTo(minecraft.player);
   }

   protected void extractNameTags(final LivingEntity entity, final LivingEntityRenderState state, final float partialTicks) {
      double nameTagDistance = entity.getAttribute(Attributes.NAME_TAG_DISTANCE).getValue();
      double belowNameDistance = entity.getAttribute(Attributes.BELOW_NAME_DISTANCE).getValue();
      super.extractNameTags(entity, state, partialTicks, nameTagDistance, belowNameDistance);
   }

   private static float solveBodyRot(final LivingEntity entity, final float headRot, final float partialTicks) {
      Entity bodyRot = entity.getVehicle();
      if (bodyRot instanceof LivingEntity riding) {
         float bodyRot = Mth.rotLerp(partialTicks, riding.yBodyRotO, riding.yBodyRot);
         float maxHeadDiff = 85.0F;
         float headDiff = Mth.clamp(Mth.wrapDegrees(headRot - bodyRot), -85.0F, 85.0F);
         bodyRot = headRot - headDiff;
         if (Math.abs(headDiff) > 50.0F) {
            bodyRot += headDiff * 0.2F;
         }

         return bodyRot;
      } else {
         return Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);
      }
   }
}
