package net.minecraft.client.renderer.entity;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Camera;
import net.minecraft.client.Options;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.entity.ClientMannequin;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MapRenderer;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.EquipmentAssetManager;
import net.minecraft.client.resources.model.sprite.AtlasManager;
import net.minecraft.client.resources.palette.PalettedTextureManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.jspecify.annotations.Nullable;

public class EntityRenderDispatcher implements ResourceManagerReloadListener {
   private Map renderers = ImmutableMap.of();
   private Map playerRenderers = Map.of();
   private Map mannequinRenderers = Map.of();
   public final TextureManager textureManager;
   public @Nullable Camera camera;
   public Entity crosshairPickEntity;
   private final BlockModelResolver blockModelResolver;
   private final ItemModelResolver itemModelResolver;
   private final MapRenderer mapRenderer;
   private final AtlasManager atlasManager;
   private final Font font;
   public final Options options;
   private final Supplier entityModels;
   private final EquipmentAssetManager equipmentAssets;
   private final PlayerSkinRenderCache playerSkinRenderCache;
   private final PalettedTextureManager palettedTextures;

   public int getPackedLightCoords(final Entity entity, final float partialTickTime) {
      return this.getRenderer(entity).getPackedLightCoords(entity, partialTickTime);
   }

   public EntityRenderDispatcher(final TextureManager textureManager, final BlockModelResolver blockModelResolver, final ItemModelResolver itemModelResolver, final MapRenderer mapRenderer, final AtlasManager atlasManager, final Font font, final Options options, final Supplier entityModels, final EquipmentAssetManager equipmentAssets, final PlayerSkinRenderCache playerSkinRenderCache, final PalettedTextureManager palettedTextures) {
      this.textureManager = textureManager;
      this.blockModelResolver = blockModelResolver;
      this.itemModelResolver = itemModelResolver;
      this.mapRenderer = mapRenderer;
      this.atlasManager = atlasManager;
      this.playerSkinRenderCache = playerSkinRenderCache;
      this.font = font;
      this.options = options;
      this.entityModels = entityModels;
      this.equipmentAssets = equipmentAssets;
      this.palettedTextures = palettedTextures;
   }

   public EntityRenderer getRenderer(final Entity entity) {
      Objects.requireNonNull(entity);
      byte var3 = 0;
      Object var10000;
      switch (entity.typeSwitch<invokedynamic>(entity, var3)) {
         case 0:
            AbstractClientPlayer player = (AbstractClientPlayer)entity;
            var10000 = this.getAvatarRenderer(this.playerRenderers, player);
            break;
         case 1:
            ClientMannequin mannequin = (ClientMannequin)entity;
            var10000 = this.getAvatarRenderer(this.mannequinRenderers, mannequin);
            break;
         default:
            var10000 = (EntityRenderer)this.renderers.get(entity.getType());
      }

      return (EntityRenderer)var10000;
   }

   private AvatarRenderer getAvatarRenderer(final Map renderers, final Avatar entity) {
      PlayerModelType model = ((ClientAvatarEntity)entity).getSkin().model();
      AvatarRenderer playerRenderer = (AvatarRenderer)renderers.get(model);
      return playerRenderer != null ? playerRenderer : (AvatarRenderer)renderers.get(PlayerModelType.WIDE);
   }

   public AvatarRenderer getRenderer(final AvatarRenderState state) {
      AvatarRenderer playerRenderer = (AvatarRenderer)this.playerRenderers.get(state.skin.model());
      return playerRenderer != null ? playerRenderer : (AvatarRenderer)this.playerRenderers.get(PlayerModelType.WIDE);
   }

   public EntityRenderer getRenderer(final EntityRenderState entityRenderState) {
      if (entityRenderState instanceof AvatarRenderState avatarRenderState) {
         return this.getRenderer(avatarRenderState);
      } else {
         return (EntityRenderer)this.renderers.get(entityRenderState.entityType);
      }
   }

   public void prepare(final Camera camera, final Entity crosshairPickEntity) {
      this.camera = camera;
      this.crosshairPickEntity = crosshairPickEntity;
   }

   public boolean shouldRender(final Entity entity, final Frustum culler, final double camX, final double camY, final double camZ, final float partialTicks) {
      EntityRenderer renderer = this.getRenderer(entity);
      return renderer.shouldRender(entity, culler, camX, camY, camZ, partialTicks);
   }

   public EntityRenderState extractEntity(final Entity entity, final float partialTicks) {
      EntityRenderer renderer = this.getRenderer(entity);

      try {
         return renderer.createRenderState(entity, partialTicks);
      } catch (Throwable var8) {
         CrashReport report = CrashReport.forThrowable(var8, "Extracting render state for an entity in world");
         CrashReportCategory entityCat = report.addCategory("Entity being extracted");
         entity.fillCrashReportCategory(entityCat);
         CrashReportCategory rendererCategory = this.fillRendererDetails(renderer, report);
         rendererCategory.setDetail("Delta", partialTicks);
         throw new ReportedException(report);
      }
   }

   public void submit(final EntityRenderState renderState, final CameraRenderState camera, final double x, final double y, final double z, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector) {
      EntityRenderer renderer = this.getRenderer(renderState);

      try {
         Vec3 pos = renderer.getRenderOffset(renderState);
         double relativeX = x + pos.x();
         double relativeY = y + pos.y();
         double relativeZ = z + pos.z();
         poseStack.pushPose();
         poseStack.translate(relativeX, relativeY, relativeZ);
         renderer.submit(renderState, poseStack, submitNodeCollector, camera);
         if (renderState.displayFireAnimation) {
            submitNodeCollector.submitFlame(poseStack, renderState, Mth.rotationAroundAxis(Mth.Y_AXIS, camera.orientation, new Quaternionf()));
         }

         if (renderState instanceof AvatarRenderState) {
            poseStack.translate(-pos.x(), -pos.y(), -pos.z());
         }

         if (!renderState.shadowPieces.isEmpty()) {
            submitNodeCollector.submitShadow(poseStack, renderState.shadowRadius, renderState.shadowPieces);
         }

         if (!(renderState instanceof AvatarRenderState)) {
            poseStack.translate(-pos.x(), -pos.y(), -pos.z());
         }

         poseStack.popPose();
      } catch (Throwable var19) {
         CrashReport report = CrashReport.forThrowable(var19, "Rendering entity in world");
         CrashReportCategory entityCat = report.addCategory("EntityRenderState being rendered");
         renderState.fillCrashReportCategory(entityCat);
         this.fillRendererDetails(renderer, report);
         throw new ReportedException(report);
      }
   }

   private CrashReportCategory fillRendererDetails(final EntityRenderer renderer, final CrashReport report) {
      CrashReportCategory category = report.addCategory("Renderer details");
      category.setDetail("Assigned renderer", renderer);
      return category;
   }

   public void resetCamera() {
      this.camera = null;
   }

   public double distanceToSqr(final Entity entity) {
      return this.camera.position().distanceToSqr(entity.position());
   }

   public void onResourceManagerReload(final ResourceManager resourceManager) {
      EntityRendererProvider.Context context = new EntityRendererProvider.Context(this, this.blockModelResolver, this.itemModelResolver, this.mapRenderer, resourceManager, (EntityModelSet)this.entityModels.get(), this.equipmentAssets, this.atlasManager, this.font, this.playerSkinRenderCache, this.palettedTextures);
      this.renderers = EntityRenderers.createEntityRenderers(context);
      this.playerRenderers = EntityRenderers.createAvatarRenderers(context);
      this.mannequinRenderers = EntityRenderers.createAvatarRenderers(context);
   }
}
