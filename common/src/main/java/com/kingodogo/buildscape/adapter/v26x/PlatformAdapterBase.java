package com.kingodogo.buildscape.adapter.v26x;
import com.kingodogo.buildscape.entity.ColoredItemFrameEntity;
import com.kingodogo.buildscape.entity.FallingIcicleEntity;
import com.kingodogo.buildscape.entity.FestiveStockingEntity;
import com.kingodogo.buildscape.entity.FestiveWanderingHomemakerEntity;
import com.kingodogo.buildscape.entity.MangroveBoatEntity;
import com.kingodogo.buildscape.entity.PoplarBoatEntity;
import com.kingodogo.buildscape.entity.SeatEntity;
import com.kingodogo.buildscape.entity.WanderingHomemakerEntity;
import com.kingodogo.buildscape.entity.WanderingHomemakerTrades;
import com.kingodogo.buildscape.platform.Services;
import java.time.LocalDate;
import java.util.Collections;
import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;
import net.minecraft.world.entity.vehicle.boat.Boat;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import com.kingodogo.buildscape.platform.IPlatformAdapter;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import com.mojang.serialization.MapCodec;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
public abstract class PlatformAdapterBase implements IPlatformAdapter {
    @Override public boolean isReplaceable(net.minecraft.world.level.block.state.BlockState state) { return state.canBeReplaced(); }
    @Override public boolean isGlassBlock(net.minecraft.world.level.block.Block block) { return block == net.minecraft.world.level.block.Blocks.GLASS || block instanceof net.minecraft.world.level.block.StainedGlassBlock || block instanceof net.minecraft.world.level.block.TintedGlassBlock; }
    @Override
    public net.minecraft.world.level.block.RenderShape getEntityBlockRenderShape() { return net.minecraft.world.level.block.RenderShape.INVISIBLE; }
    @Override public int getClientParticleSetting() { return com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.getClientParticleSetting(); }

    @Override public Player getClientPlayer() { return com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.getClientPlayer(); }

    @Override public boolean hasShiftDown() { return com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.hasShiftDown(); }

    @Override public boolean hasControlDown() { return com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.hasControlDown(); }



    @Override public boolean widgetMouseClicked(net.minecraft.client.gui.components.AbstractWidget widget, double mouseX, double mouseY, int button) { return com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.widgetMouseClicked(widget, mouseX, mouseY, button); }

    @Override public boolean widgetKeyPressed(net.minecraft.client.gui.components.AbstractWidget widget, int keyCode, int scanCode, int modifiers) { return com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.widgetKeyPressed(widget, keyCode, scanCode, modifiers); }

    @Override public boolean widgetCharTyped(net.minecraft.client.gui.components.AbstractWidget widget, char codePoint, int modifiers) { return com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.widgetCharTyped(widget, codePoint, modifiers); }

    @Override public void setEditBoxFilter(net.minecraft.client.gui.components.EditBox editBox, java.util.function.Predicate<String> filter) { com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.setEditBoxFilter(editBox, filter); }

    @Override public void enableScissor(Object graphics, int x, int y, int width, int height) { com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.enableScissor(graphics, x, y, width, height); }

    @Override public void disableScissor(Object graphics) { com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.disableScissor(graphics); }

    @Override public CommonId registerDynamicTexture(String name, net.minecraft.client.renderer.texture.DynamicTexture texture) { return com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.registerDynamicTexture(name, texture); }

    @Override public net.minecraft.client.renderer.texture.DynamicTexture createDynamicTexture(String name, int width, int height, boolean clear) { return com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.createDynamicTexture(name, width, height, clear); }

    @Override
    @SuppressWarnings("unchecked")
    public boolean getGameRuleBoolean(net.minecraft.world.level.Level level, Object ruleKey, boolean fallback) {
        return fallback;
    }

    @Override public Iterable<net.minecraft.world.item.ItemStack> getInventoryItems(net.minecraft.world.entity.player.Inventory inventory) { return inventory.getNonEquipmentItems(); }
    @Override public boolean hasPlayerPermissions(net.minecraft.world.entity.player.Player player, int level) { return com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.hasPlayerPermissions(player, level); }
    @Override public void openScreen(net.minecraft.client.gui.screens.Screen screen) { com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.openScreen(screen); }
    @Override public void openUri(java.net.URI uri) { net.minecraft.util.Util.getPlatform().openUri(uri); }
    @Override public void setNativeImagePixel(com.mojang.blaze3d.platform.NativeImage image, int x, int y, int abgr) { image.setPixelABGR(x, y, abgr); }
    @Override public void beginGuiOverlayRender() { com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.beginGuiOverlayRender(); }
    @Override public void endGuiOverlayRender() { com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.endGuiOverlayRender(); }
    @Override public void resetShaderColor() { com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.resetShaderColor(); }
    @Override public void renderPillarMarkers(com.mojang.blaze3d.vertex.PoseStack poseStack, net.minecraft.client.Camera camera) { com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.renderPillarMarkers(poseStack, camera); }
    @Override public float getWalkDistance(net.minecraft.world.entity.player.Player player) { return player.walkAnimation.position(); }
    @Override public boolean isWalkAnimationMoving(net.minecraft.world.entity.player.Player player) { return player.walkAnimation.isMoving(); }
    @Override public net.minecraft.network.chat.Component parseComponentJson(String json) { return com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.parseComponentJson(json); }
    @Override public net.minecraft.client.KeyMapping createKeyMapping(String translationKey, int keyCode, String categoryTranslationKey) { return com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.createKeyMapping(translationKey, keyCode, categoryTranslationKey); }
    @Override public boolean hasCurrentUser() { return com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.hasCurrentUser(); }

    @Override public String getCurrentUserUuid() { return com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.getCurrentUserUuid(); }

    @Override
    public Block getBlock(CommonId id) {
        if (id == null) return null;
        Identifier rl = Identifier.fromNamespaceAndPath(id.getNamespace(), id.getPath());
        return BuiltInRegistries.BLOCK.getValue(rl);
    }

    @Override
    public CommonId getBlockId(Block block) {
        if (block == null) return null;
        Identifier rl = BuiltInRegistries.BLOCK.getKey(block);
        return rl != null ? new CommonId(rl.getNamespace(), rl.getPath()) : null;
    }

    @Override
    public Item getItem(CommonId id) {
        if (id == null) return null;
        Identifier rl = Identifier.fromNamespaceAndPath(id.getNamespace(), id.getPath());
        return BuiltInRegistries.ITEM.getValue(rl);
    }

    @Override
    public CommonId getItemId(Item item) {
        if (item == null) return null;
        Identifier rl = BuiltInRegistries.ITEM.getKey(item);
        return rl != null ? new CommonId(rl.getNamespace(), rl.getPath()) : null;
    }

    @Override
    public Item.Properties prepareItemProperties(CommonId id, Item.Properties properties) {
        return properties.setId(net.minecraft.resources.ResourceKey.create(
                net.minecraft.core.registries.Registries.ITEM,
                Identifier.fromNamespaceAndPath(id.getNamespace(), id.getPath())));
    }

    @Override
    public int getItemRawId(Item item) {
        return item != null ? BuiltInRegistries.ITEM.getId(item) : -1;
    }

    @Override
    public Item getItemByRawId(int rawId) {
        return BuiltInRegistries.ITEM.byId(rawId);
    }

    @Override
    public Fluid getFluid(CommonId id) {
        if (id == null) return null;
        if ("buildscape".equals(id.getNamespace())) {
            if ("experience_still".equals(id.getPath())) return com.kingodogo.buildscape.adapter.v26x.fluid.ExperienceFluids.still();
            if ("experience_flowing".equals(id.getPath())) return com.kingodogo.buildscape.adapter.v26x.fluid.ExperienceFluids.flowing();
        }
        Identifier rl = Identifier.fromNamespaceAndPath(id.getNamespace(), id.getPath());
        return BuiltInRegistries.FLUID.getValue(rl);
    }

    @Override
    public CommonId getFluidId(Fluid fluid) {
        if (fluid == null) return null;
        Identifier rl = BuiltInRegistries.FLUID.getKey(fluid);
        return rl != null ? new CommonId(rl.getNamespace(), rl.getPath()) : null;
    }

    @Override
    public Fluid getBucketFluid(Item item) {
        if (item instanceof net.minecraft.world.item.BucketItem bi) {
            return bi.getContent();
        }
        return Fluids.EMPTY;
    }

    @Override
    public Entity createSeatEntity(Level level, double x, double y, double z) {
        return new SeatEntityImpl(level, x, y, z);
    }

    @Override
    public Entity createFallingIcicleEntity(Level level, double x, double y, double z, net.minecraft.world.level.block.state.BlockState state) {
        return new FallingIcicleEntityImpl(level, x, y, z, state);
    }

    @Override
    public Entity createWanderingHomemakerEntity(Level level, boolean festive) {
        return festive ? new FestiveWanderingHomemakerEntityImpl(level) : new WanderingHomemakerEntityImpl(level);
    }

    @Override
    public Entity createBoatEntity(Level level, double x, double y, double z, boolean poplar) {
        return poplar ? new PoplarBoatEntityImpl(level, x, y, z) : new MangroveBoatEntityImpl(level, x, y, z);
    }

    @Override
    public Entity createColoredItemFrameEntity(Level level, net.minecraft.core.BlockPos pos, net.minecraft.core.Direction direction, String color) {
        return new ColoredItemFrameEntityImpl(level, pos, direction, color);
    }

    @Override
    public Entity createFestiveStockingEntity(Level level, net.minecraft.core.BlockPos pos, net.minecraft.core.Direction direction, String color) {
        return new FestiveStockingEntityImpl(level, pos, direction, color);
    }

    private static ResourceKey<EntityType<?>> entityKey(String path) {
        return ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("buildscape", path));
    }

    private EntityType<?> fallingIcicleType;
    private EntityType<?> festiveStockingType;
    private EntityType<?> mangroveBoatType;
    private EntityType<?> coloredItemFrameType;
    private EntityType<?> seatType;
    private EntityType<?> poplarBoatType;
    private EntityType<?> wanderingHomemakerType;
    private EntityType<?> festiveWanderingHomemakerType;

    @Override
    public EntityType<?> getFallingIcicleEntityType() {
        if (fallingIcicleType == null) {
            fallingIcicleType = EntityType.Builder.<FallingIcicleEntityImpl>of(FallingIcicleEntityImpl::new, MobCategory.MISC)
                    .sized(0.98F, 0.98F).clientTrackingRange(10).updateInterval(20).build(entityKey("falling_icicle"));
        }
        return fallingIcicleType;
    }

    @Override
    public EntityType<?> getFestiveStockingEntityType() {
        if (festiveStockingType == null) {
            festiveStockingType = EntityType.Builder.<FestiveStockingEntityImpl>of(FestiveStockingEntityImpl::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F).clientTrackingRange(10).updateInterval(20).build(entityKey("festive_stocking"));
        }
        return festiveStockingType;
    }

    @Override
    public EntityType<?> getMangroveBoatEntityType() {
        if (mangroveBoatType == null) {
            mangroveBoatType = EntityType.Builder.<MangroveBoatEntityImpl>of(MangroveBoatEntityImpl::new, MobCategory.MISC)
                    .sized(1.375F, 0.5625F).clientTrackingRange(10).updateInterval(3).build(entityKey("mangrove_boat"));
        }
        return mangroveBoatType;
    }

    @Override
    public EntityType<?> getColoredItemFrameEntityType() {
        if (coloredItemFrameType == null) {
            coloredItemFrameType = EntityType.Builder.<ColoredItemFrameEntityImpl>of(ColoredItemFrameEntityImpl::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F).clientTrackingRange(10).updateInterval(20).build(entityKey("colored_item_frame"));
        }
        return coloredItemFrameType;
    }

    @Override
    public EntityType<?> getSeatEntityType() {
        if (seatType == null) {
            seatType = EntityType.Builder.<SeatEntityImpl>of(SeatEntityImpl::new, MobCategory.MISC)
                    .sized(0.1F, 0.1F).clientTrackingRange(10).updateInterval(20).build(entityKey("seat"));
        }
        return seatType;
    }

    @Override
    public EntityType<?> getPoplarBoatEntityType() {
        if (poplarBoatType == null) {
            poplarBoatType = EntityType.Builder.<PoplarBoatEntityImpl>of(PoplarBoatEntityImpl::new, MobCategory.MISC)
                    .sized(1.375F, 0.5625F).clientTrackingRange(10).updateInterval(3).build(entityKey("poplar_boat"));
        }
        return poplarBoatType;
    }

    @Override
    public EntityType<?> getWanderingHomemakerEntityType() {
        if (wanderingHomemakerType == null) {
            wanderingHomemakerType = EntityType.Builder.<WanderingHomemakerEntityImpl>of(WanderingHomemakerEntityImpl::new, MobCategory.CREATURE)
                    .sized(0.6F, 1.95F).clientTrackingRange(10).updateInterval(3).build(entityKey("wandering_homemaker"));
        }
        return wanderingHomemakerType;
    }

    @Override
    public EntityType<?> getFestiveWanderingHomemakerEntityType() {
        if (festiveWanderingHomemakerType == null) {
            festiveWanderingHomemakerType = EntityType.Builder.<FestiveWanderingHomemakerEntityImpl>of(FestiveWanderingHomemakerEntityImpl::new, MobCategory.CREATURE)
                    .sized(0.6F, 1.95F).clientTrackingRange(10).updateInterval(3).build(entityKey("festive_wandering_homemaker"));
        }
        return festiveWanderingHomemakerType;
    }

    @Override
    public Level getEntityLevel(Entity entity) {
        return entity.level();
    }

    @Override
    public Iterable<Item> getAllItems() {
        return BuiltInRegistries.ITEM;
    }

    @Override
    public net.minecraft.resources.ResourceKey<Registry<Item>> getItemRegistryKey() {
        return net.minecraft.core.registries.Registries.ITEM;
    }

    public static <V, T extends V> T safeRegister(Registry<V> registry, Identifier id, T value) {
        com.kingodogo.buildscape.platform.Services.PLATFORM.register(registry,
                new CommonId(id.getNamespace(), id.getPath()), value);
        return value;
    }

    @Override
    public void wrapRegistryAction(Runnable action) {
        action.run();
    }

    @Override
    public <V> void register(Registry<V> registry, CommonId id, V value) {
        Registry.register(registry, Identifier.fromNamespaceAndPath(id.getNamespace(), id.getPath()), value);
    }

    @Override
    public net.minecraft.stats.Stat<?> registerCustomStat(String registryName, CommonId statId) {
        Identifier value = Identifier.fromNamespaceAndPath(statId.getNamespace(), statId.getPath());
        safeRegister(BuiltInRegistries.CUSTOM_STAT, Identifier.withDefaultNamespace(registryName), value);
        return net.minecraft.stats.Stats.CUSTOM.get(value, net.minecraft.stats.StatFormatter.DEFAULT);
    }

    @Override
    public void registerParticleType(CommonId id, net.minecraft.core.particles.ParticleType<?> type) {
        Identifier rl = Identifier.fromNamespaceAndPath(id.getNamespace(), id.getPath());
        safeRegister(BuiltInRegistries.PARTICLE_TYPE, rl, type);
    }

    @Override
    public void registerParticleProviders() {
        if (!isClient()) return;
        com.kingodogo.buildscape.adapter.v26x.ParticleFactory.registerProviders();
    }

    @Override
    public void registerCopperFireFlameParticle() {
        registerParticleProviders();
    }

    @Override
    public void rotateX(com.mojang.blaze3d.vertex.PoseStack poseStack, float degrees) {
        poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(degrees));
    }

    @Override
    public void rotateY(com.mojang.blaze3d.vertex.PoseStack poseStack, float degrees) {
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(degrees));
    }

    @Override
    public void rotateZ(com.mojang.blaze3d.vertex.PoseStack poseStack, float degrees) {
        poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(degrees));
    }

    @Override public void fill(Object poseStackOrGraphics, int minX, int minY, int maxX, int maxY, int color) { com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.fill(poseStackOrGraphics, minX, minY, maxX, maxY, color); }

    @Override public void drawShadow(Object poseStackOrGraphics, net.minecraft.client.gui.Font font, String text, int x, int y, int color) { com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.drawShadow(poseStackOrGraphics, font, text, x, y, color); }

    @Override public void draw(Object poseStackOrGraphics, net.minecraft.client.gui.Font font, String text, float x, float y, int color) { com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.draw(poseStackOrGraphics, font, text, x, y, color); }

    @Override public void drawCenteredString(Object poseStackOrGraphics, net.minecraft.client.gui.Font font, net.minecraft.network.chat.Component component, int x, int y, int color) { com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.drawCenteredString(poseStackOrGraphics, font, component, x, y, color); }

    @Override public void drawCenteredString(Object poseStackOrGraphics, net.minecraft.client.gui.Font font, String text, int x, int y, int color) { com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.drawCenteredString(poseStackOrGraphics, font, text, x, y, color); }

    @Override
    public void bindTexture(CommonId id) {
    }

    @Override public void blit(Object poseStackOrGraphics, CommonId texture, int x, int y, float u, float v, int width, int height, int sheetW, int sheetH) { com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.blit(poseStackOrGraphics, texture, x, y, u, v, width, height, sheetW, sheetH); }

    @Override public void blit(Object poseStackOrGraphics, CommonId texture, int x, int y, int width, int height, float u, float v, int uWidth, int vHeight, int sheetW, int sheetH) { com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.blit(poseStackOrGraphics, texture, x, y, width, height, u, v, uWidth, vHeight, sheetW, sheetH); }

    @Override public void renderComponentTooltip(Object poseStackOrGraphics, net.minecraft.client.gui.Font font, java.util.List<net.minecraft.network.chat.Component> components, int mouseX, int mouseY) { com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.renderComponentTooltip(poseStackOrGraphics, font, components, mouseX, mouseY); }

    @Override
    public void registerWorldGen() {
        WorldGenFactory.register();
    }

    @Override
    public net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider createRandomStateProvider(List<BlockState> states) {
        return WorldGenFactory.createRandomStateProvider(states);
    }
    @Override
    public net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator getCreakingHeartTreeDecorator() {
        return WorldGenFactory.getCreakingHeartTreeDecorator();
    }
    @Override
    public net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator createMangroveLeaveVineDecorator(float probability) {
        return WorldGenFactory.createMangroveLeaveVineDecorator(probability);
    }
    @Override
    public net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator createMangroveMossCarpetDecorator(float probability) {
        return WorldGenFactory.createMangroveMossCarpetDecorator(probability);
    }
    @Override
    public net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator createMangrovePropaguleDecorator(float probability, net.minecraft.util.valueproviders.IntProvider p1, net.minecraft.util.valueproviders.IntProvider p2, int requiredEmptyBlocks) {
        return WorldGenFactory.createMangrovePropaguleDecorator(probability, p1, p2, requiredEmptyBlocks);
    }
    @Override
    public net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator createMangroveRootDecorator(net.minecraft.util.valueproviders.IntProvider p1, net.minecraft.util.valueproviders.IntProvider p2, float probability, net.minecraft.util.valueproviders.IntProvider p3) {
        return WorldGenFactory.createMangroveRootDecorator(p1, p2, probability, p3);
    }
    @Override
    public net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer createMangroveUpwardsBranchingTrunkPlacer(int baseHeight, int heightRandA, int heightRandB, net.minecraft.util.valueproviders.IntProvider extraBranchSteps, float placeBranchPerLogProbability, net.minecraft.util.valueproviders.IntProvider extraBranchLength) {
        return WorldGenFactory.createMangroveUpwardsBranchingTrunkPlacer(baseHeight, heightRandA, heightRandB, extraBranchSteps, placeBranchPerLogProbability, extraBranchLength);
    }
    @Override
    public net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer createMangroveRandomSpreadFoliagePlacer(net.minecraft.util.valueproviders.IntProvider radius, net.minecraft.util.valueproviders.IntProvider offset, net.minecraft.util.valueproviders.IntProvider foliageHeight, int leafPlacementAttempts) {
        return WorldGenFactory.createMangroveRandomSpreadFoliagePlacer(radius, offset, foliageHeight, leafPlacementAttempts);
    }
    @Override
    public net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration createTreeConfiguration(
            net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider trunkProvider,
            net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer trunkPlacer,
            net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider foliageProvider,
            net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer foliagePlacer,
            net.minecraft.world.level.levelgen.feature.featuresize.FeatureSize featureSize,
            List<net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator> decorators,
            boolean ignoreVines
    ) {
        net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration.TreeConfigurationBuilder builder =
                new net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration.TreeConfigurationBuilder(
                        trunkProvider, trunkPlacer, foliageProvider, foliagePlacer, featureSize,
                        net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider.simple(net.minecraft.world.level.block.Blocks.DIRT));
        if (decorators != null && !decorators.isEmpty()) {
            builder.decorators(decorators);
        }
        if (ignoreVines) {
            builder.ignoreVines();
        }
        return builder.build();
    }
    @Override
    public net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider createWeightedStateProvider(
            List<BlockState> states,
            List<Integer> weights
    ) {
        net.minecraft.util.random.WeightedList.Builder<BlockState> builder = net.minecraft.util.random.WeightedList.builder();
        for (int i = 0; i < states.size(); i++) {
            builder.add(states.get(i), weights.get(i));
        }
        return new net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider(builder);
    }
    @Override
    public net.minecraft.world.level.levelgen.feature.ConfiguredFeature<?, ?> createPatchFeature(
            net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider stateProvider,
            int tries,
            int xzSpread,
            int ySpread
    ) {
        return WorldGenFactory.createPatchFeature(stateProvider, tries, xzSpread, ySpread);
    }
    @Override
    public void registerConfiguredFeature(CommonId id, Object configuredFeature) {
    }

    @Override public com.mojang.blaze3d.vertex.PoseStack toPoseStack(Object poseStackOrGraphics) { return com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.toPoseStack(poseStackOrGraphics); }

    @Override public void pushGuiPose(Object context) { com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.pushGuiPose(context); }
    @Override public void popGuiPose(Object context) { com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.popGuiPose(context); }
    @Override public void translateGuiPose(Object context, float x, float y, float z) { com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.translateGuiPose(context, x, y, z); }
    @Override public void scaleGuiPose(Object context, float x, float y) { com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.scaleGuiPose(context, x, y); }
    @Override public void renderClientOverlay(Object context, int width, int height) {
        com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.renderClientOverlay(context, width, height);
    }

    @Override public void renderWidget(Object poseStackOrGraphics, net.minecraft.client.gui.components.AbstractWidget widget, int mouseX, int mouseY, float partialTick) { com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.renderWidget(poseStackOrGraphics, widget, mouseX, mouseY, partialTick); }

    @Override
    public CommonId getEntityTypeId(net.minecraft.world.entity.EntityType<?> entityType) {
        Object key = net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
        return key != null ? CommonId.parse(key.toString()) : new CommonId("minecraft", "pig");
    }

    @Override
    public boolean hasCustomHoverName(ItemStack stack) {
        return stack != null && stack.has(net.minecraft.core.component.DataComponents.CUSTOM_NAME);
    }

    @Override
    public net.minecraft.world.entity.EquipmentSlot getEquipmentSlot(ItemStack stack) {
        var equippable = stack.get(net.minecraft.core.component.DataComponents.EQUIPPABLE);
        return equippable == null ? net.minecraft.world.entity.EquipmentSlot.MAINHAND : equippable.slot();
    }

    @Override public net.minecraft.world.phys.AABB getModelBounds(Object model) { return com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.getModelBounds(model); }

    @Override public void renderItemFixed(ItemStack stack, com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource, int combinedLight, int combinedOverlay, Object model) { com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.renderItemFixed(stack, poseStack, bufferSource, combinedLight, combinedOverlay, model); }

    @Override public void renderItemStatic(ItemStack stack, com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource, int combinedLight, int combinedOverlay, int seed) { com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.renderItemStatic(stack, poseStack, bufferSource, combinedLight, combinedOverlay, seed); }



    @Override public Object getItemModel(ItemStack stack, Level level, int seed) { return com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.getItemModel(stack, level, seed); }

    @Override
    public void registerRecipeSerializers() {
        RecipeFactory.register();
    }

    @Override
    public byte[] readResourceBytes(net.minecraft.server.packs.resources.ResourceManager resourceManager, CommonId location) {
        if (resourceManager == null || location == null) return null;
        try {
            net.minecraft.resources.Identifier id = net.minecraft.resources.Identifier.fromNamespaceAndPath(location.getNamespace(), location.getPath());
            java.util.Optional<net.minecraft.server.packs.resources.Resource> res = resourceManager.getResource(id);
            if (res.isPresent()) {
                try (java.io.InputStream stream = res.get().open()) {
                    return stream.readAllBytes();
                }
            }
        } catch (Throwable exception) {
            com.kingodogo.buildscape.BuildscapeCommon.LOGGER.warn("Failed in 26.x readResourceBytes", exception);}
        return null;
    }

    @Override
    public net.minecraft.server.packs.resources.PreparableReloadListener createRecipeReloadListener() {
        return new net.minecraft.server.packs.resources.PreparableReloadListener() {
            @Override
            public java.util.concurrent.CompletableFuture<Void> reload(
                    SharedState sharedState,
                    java.util.concurrent.Executor backgroundExecutor,
                    PreparationBarrier barrier,
                    java.util.concurrent.Executor gameExecutor
            ) {
                return java.util.concurrent.CompletableFuture.supplyAsync(() -> com.kingodogo.buildscape.recipe.framework.BuildScapeRecipeLoader.INSTANCE.prepareRecipes(sharedState.resourceManager()), backgroundExecutor)
                        .thenCompose(barrier::wait)
                        .thenAcceptAsync(recipes -> com.kingodogo.buildscape.recipe.framework.BuildScapeRecipeLoader.INSTANCE.applyRecipes(recipes), gameExecutor);
            }
        };
    }

    @Override
    public void injectRecipes(net.minecraft.world.item.crafting.RecipeManager recipeManager, java.util.List<com.kingodogo.buildscape.recipe.framework.parser.RecipeIR.CompiledRecipe> recipes) {
        RecipeFactory.injectRecipes(recipeManager, recipes);
    }

    @Override
    public CreativeModeTab createCreativeTab(CommonId tabId, String titleKey, java.util.function.Supplier<ItemStack> iconSupplier, java.util.List<String> orderedItemIds, java.util.Map<String, Item> items) {
        return CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
                .title(net.minecraft.network.chat.Component.translatable(titleKey))
                .icon(iconSupplier)
                .displayItems(com.kingodogo.buildscape.adapter.v26x.CreativeTabBridge.createGenerator(output -> {
                    java.util.Set<String> added = new java.util.HashSet<>();
                    for (String id : orderedItemIds) {
                        Item item = items.get(id);
                        if (item != null && added.add(id)) {
                            output.accept(new ItemStack(item));
                        }
                    }
                    for (java.util.Map.Entry<String, Item> entry : items.entrySet()) {
                        if (added.add(entry.getKey())) {
                            output.accept(new ItemStack(entry.getValue()));
                        }
                    }
                }))
                .build();
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean supportsBiomeBrushEnchantment(ItemStack stack, Object enchantment) {
        if (enchantment instanceof net.minecraft.core.Holder) {
            net.minecraft.core.Holder<net.minecraft.world.item.enchantment.Enchantment> h =
                    (net.minecraft.core.Holder<net.minecraft.world.item.enchantment.Enchantment>) enchantment;
            return h.is(net.minecraft.world.item.enchantment.Enchantments.UNBREAKING)
                    || h.is(net.minecraft.world.item.enchantment.Enchantments.MENDING);
        }
        return false;
    }

    @Override
    public Item createBiomeBrushItem(com.kingodogo.buildscape.item.BiomeBrushItem.BiomeBrushTier tier, Item.Properties properties) {
        return new com.kingodogo.buildscape.item.BiomeBrushItem(tier, properties) {
            public boolean supportsEnchantment(ItemStack stack, net.minecraft.core.Holder<net.minecraft.world.item.enchantment.Enchantment> enchantment) {
                return supportsBiomeBrushEnchantment(stack, enchantment);
            }

            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
                super.appendHoverText(stack, context, display, tooltip, flag);
                addHoverText(stack, tooltip);
            }
        };
    }

    @Override
    public net.minecraft.world.item.BlockItem createTrophyBlockItem(com.kingodogo.buildscape.trophy.TrophyBlock block, com.kingodogo.buildscape.trophy.TrophyDefinition definition, Item.Properties properties) {
        prepareItemProperties(new CommonId(com.kingodogo.buildscape.BuildscapeCommon.MOD_ID, definition.getId()), properties);
        return new com.kingodogo.buildscape.trophy.TrophyBlockItem(block, definition, properties) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
                super.appendHoverText(stack, context, display, tooltip, flag);
                addHoverText(stack, tooltip);
            }
        };
    }

    @Override
    public com.kingodogo.buildscape.trophy.TrophyBlock createTrophyBlock(com.kingodogo.buildscape.trophy.TrophyDefinition definition) {
        return new com.kingodogo.buildscape.trophy.TrophyBlock(definition, BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
                .setId(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.BLOCK,
                        Identifier.fromNamespaceAndPath(com.kingodogo.buildscape.BuildscapeCommon.MOD_ID, definition.getId())))
                .strength(definition.getHardness(), definition.getResistance())
                .sound(definition.getSoundType())
                .lightLevel(state -> definition.getLightEmission())
                .emissiveRendering(state -> true)
                .noOcclusion()) {
            @Override
            protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
                return null;
            }

            @Override
            public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData) {
                return createTrophyStack(level.getBlockEntity(pos));
            }

            @Override
            public java.util.List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootParams.Builder builder) {
                return java.util.Collections.singletonList(createTrophyStack(builder.getOptionalParameter(net.minecraft.world.level.storage.loot.parameters.LootContextParams.BLOCK_ENTITY)));
            }
        };
    }

    @Override
    public SoundEvent createSoundEvent(CommonId id) {
        Identifier rl = Identifier.fromNamespaceAndPath(id.getNamespace(), id.getPath());
        return SoundEvent.createVariableRangeEvent(rl);
    }

    @Override
    public void playBlockSound(Level level, net.minecraft.core.BlockPos pos, CommonId soundId) {
        Identifier rl = Identifier.fromNamespaceAndPath(soundId.getNamespace(), soundId.getPath());
        SoundEvent se = BuiltInRegistries.SOUND_EVENT.getValue(rl);
        if (se != null) {
            level.playSound(null, pos, se, net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }

    @Override
    public void neighborChanged(Level level, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state, Block fromBlock, net.minecraft.core.BlockPos fromPos) {
        level.neighborChanged(state, pos, fromBlock, null, false);
    }

    @Override
    public void hurtAndBreak(net.minecraft.world.item.ItemStack stack, int amount, net.minecraft.world.entity.LivingEntity entity, net.minecraft.world.InteractionHand hand) {
        net.minecraft.world.entity.EquipmentSlot slot = hand == net.minecraft.world.InteractionHand.MAIN_HAND
                ? net.minecraft.world.entity.EquipmentSlot.MAINHAND
                : net.minecraft.world.entity.EquipmentSlot.OFFHAND;
        stack.hurtAndBreak(amount, entity, slot);
    }

    @Override
    public void sendActionBarMessage(net.minecraft.world.entity.player.Player player, net.minecraft.network.chat.Component message) {
        if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            serverPlayer.sendSystemMessage(message, true);
        } else {
            player.sendSystemMessage(message);
        }
    }

    @Override
    public void sendSystemMessage(net.minecraft.world.entity.player.Player player, net.minecraft.network.chat.Component message) {
        player.sendSystemMessage(message);
    }

    @Override
    public void sendCommandSuccess(net.minecraft.commands.CommandSourceStack source, String message, boolean broadcastToOps) {
        source.sendSuccess(() -> net.minecraft.network.chat.Component.literal(message), broadcastToOps);
    }

    @Override
    public void sendCommandFailure(net.minecraft.commands.CommandSourceStack source, String message) {
        source.sendFailure(net.minecraft.network.chat.Component.literal(message));
    }

    @Override
    public net.minecraft.network.chat.MutableComponent literal(String text) {
        return net.minecraft.network.chat.Component.literal(text);
    }

    @Override
    public net.minecraft.network.chat.MutableComponent translatable(String key) {
        return net.minecraft.network.chat.Component.translatable(key);
    }

    @Override
    public void setItemCustomName(net.minecraft.world.item.ItemStack stack, net.minecraft.network.chat.Component name) {
        stack.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME, name);
    }

    @Override
    public String getItemCustomName(net.minecraft.world.item.ItemStack stack) {
        if (stack == null || stack.isEmpty()) return null;
        net.minecraft.network.chat.Component customName = stack.get(net.minecraft.core.component.DataComponents.CUSTOM_NAME);
        return customName != null ? customName.getString() : null;
    }

    @Override
    public net.minecraft.network.chat.MutableComponent translatable(String key, Object... args) {
        return net.minecraft.network.chat.Component.translatable(key, args);
    }

    @Override
    public void playButtonClick(Level level, net.minecraft.core.BlockPos pos) {
        level.playSound(null, pos, net.minecraft.sounds.SoundEvents.UI_BUTTON_CLICK.value(), net.minecraft.sounds.SoundSource.BLOCKS, 0.5f, 1.0f);
    }

    @Override
    public void playButtonClick(Level level, net.minecraft.core.BlockPos pos, float pitch) {
        level.playSound(null, pos, net.minecraft.sounds.SoundEvents.UI_BUTTON_CLICK.value(), net.minecraft.sounds.SoundSource.BLOCKS, 0.5f, pitch);
    }

    @Override
    public void playNoteHarp(Level level, net.minecraft.core.BlockPos pos, float pitch) {
        level.playSound(null, pos, net.minecraft.sounds.SoundEvents.NOTE_BLOCK_HARP.value(), net.minecraft.sounds.SoundSource.RECORDS, 3.0F, pitch);
    }

    @Override
    public void spawnDustParticles(net.minecraft.server.level.ServerLevel level, double x, double y, double z, int color, float scale) {
        level.sendParticles(new DustParticleOptions(color, scale), x, y, z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
    }

    @Override
    public void playEatSound(Level level, net.minecraft.core.BlockPos pos) {
        level.playSound(null, pos, net.minecraft.sounds.SoundEvents.GENERIC_EAT.value(), net.minecraft.sounds.SoundSource.PLAYERS, 0.8F, 1.0F);
    }

    @Override
    public void playDrinkSound(Level level, net.minecraft.core.BlockPos pos) {
        level.playSound(null, pos, net.minecraft.sounds.SoundEvents.GENERIC_DRINK.value(), net.minecraft.sounds.SoundSource.PLAYERS, 0.8F, 1.0F);
    }

    @Override
    public void playHoneyDrinkSound(Level level, net.minecraft.core.BlockPos pos) {
        level.playSound(null, pos, net.minecraft.sounds.SoundEvents.HONEY_DRINK.value(), net.minecraft.sounds.SoundSource.PLAYERS, 0.8F, 1.0F);
    }

    @Override
    public void playFlintAndSteelSound(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos) {
        level.playSound(null, pos, net.minecraft.sounds.SoundEvents.FLINTANDSTEEL_USE, net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    @Override
    public void playCandleExtinguishSound(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos) {
        level.playSound(null, pos, net.minecraft.sounds.SoundEvents.CANDLE_EXTINGUISH, net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    @Override
    public void playCandleAmbientSound(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos, float pitch) {
        level.playSound(null, pos, net.minecraft.sounds.SoundEvents.CANDLE_AMBIENT, net.minecraft.sounds.SoundSource.BLOCKS, 0.8F, pitch);
    }

    @Override
    public void playCandleAmbientLocalSound(net.minecraft.world.level.Level level, double x, double y, double z, float volume, float pitch) {
        level.playLocalSound(x, y, z, net.minecraft.sounds.SoundEvents.CANDLE_AMBIENT, net.minecraft.sounds.SoundSource.BLOCKS, volume, pitch, false);
    }

    @Override
    public net.minecraft.world.item.DyeColor getDyeColor(net.minecraft.world.item.ItemStack stack) {
        if (stack == null || stack.isEmpty()) return null;
        return stack.get(net.minecraft.core.component.DataComponents.DYE);
    }

    @Override
    public int getDyeColorValue(net.minecraft.world.item.DyeColor color) {
        return color != null ? color.getTextureDiffuseColor() : 0xFFFFFF;
    }

    @Override
    public void syncChunk(net.minecraft.server.level.ServerLevel level, net.minecraft.world.level.chunk.LevelChunk chunk) {
        net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket packet =
                new net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket(chunk, level.getLightEngine(), null, null);
        level.getChunkSource().chunkMap.getPlayers(chunk.getPos(), false).forEach(player -> player.connection.send(packet));
    }

    @Override
    public void markChunkUnsaved(net.minecraft.world.level.chunk.LevelChunk chunk) {
        chunk.markUnsaved();
    }

    @Override
    public void markChunkUnsaved(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos) {
        if (level != null && pos != null) {
            markChunkUnsaved(level.getChunkAt(pos));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setSectionBiome(net.minecraft.world.level.chunk.LevelChunkSection section, int localQx, int localQy, int localQz, net.minecraft.core.Holder<net.minecraft.world.level.biome.Biome> biome) {
        if (section.getBiomes() instanceof net.minecraft.world.level.chunk.PalettedContainer<?> container) {
            ((net.minecraft.world.level.chunk.PalettedContainer<net.minecraft.core.Holder<net.minecraft.world.level.biome.Biome>>) container).set(localQx, localQy, localQz, biome);
        }
    }

    @Override
    public CommonId getBiomeId(net.minecraft.world.level.Level level, net.minecraft.core.Holder<net.minecraft.world.level.biome.Biome> biomeHolder) {
        if (biomeHolder == null) return null;
        Identifier rl = level.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.BIOME).getKey(biomeHolder.value());
        return rl != null ? new CommonId(rl.getNamespace(), rl.getPath()) : null;
    }

    @Override
    public net.minecraft.core.Holder<net.minecraft.world.level.biome.Biome> getBiomeHolder(net.minecraft.server.level.ServerLevel level, CommonId id) {
        if (id == null) return null;
        Identifier rl = Identifier.fromNamespaceAndPath(id.getNamespace(), id.getPath());
        net.minecraft.resources.ResourceKey<net.minecraft.world.level.biome.Biome> key =
                net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.BIOME, rl);
        return level.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.BIOME).get(key).orElse(null);
    }

    @Override
    public int getUnbreakingLevel(net.minecraft.world.item.ItemStack stack) {
        if (stack == null || stack.isEmpty()) return 0;
        net.minecraft.world.item.enchantment.ItemEnchantments enchantments = stack.get(net.minecraft.core.component.DataComponents.ENCHANTMENTS);
        if (enchantments != null) {
            for (net.minecraft.core.Holder<net.minecraft.world.item.enchantment.Enchantment> holder : enchantments.keySet()) {
                if (holder.is(net.minecraft.world.item.enchantment.Enchantments.UNBREAKING)) {
                    return enchantments.getLevel(holder);
                }
            }
        }
        return 0;
    }

    @Override
    public int getEfficiencyLevel(net.minecraft.world.item.ItemStack stack) {
        if (stack == null || stack.isEmpty()) return 0;
        net.minecraft.world.item.enchantment.ItemEnchantments enchantments = stack.get(net.minecraft.core.component.DataComponents.ENCHANTMENTS);
        if (enchantments != null) {
            for (net.minecraft.core.Holder<net.minecraft.world.item.enchantment.Enchantment> holder : enchantments.keySet()) {
                if (holder.is(net.minecraft.world.item.enchantment.Enchantments.EFFICIENCY)) return enchantments.getLevel(holder);
            }
        }
        return 0;
    }

    @Override
    public float getHoeMiningSpeed(net.minecraft.world.item.ItemStack stack) {
        if (stack != null && !stack.isEmpty()) {
            net.minecraft.world.item.component.Tool tool = stack.get(net.minecraft.core.component.DataComponents.TOOL);
            if (tool != null) {
                return tool.defaultMiningSpeed();
            }
        }
        return 2.0F;
    }

    @Override
    public boolean hasSilkTouch(net.minecraft.world.item.ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        net.minecraft.world.item.enchantment.ItemEnchantments enchantments = stack.get(net.minecraft.core.component.DataComponents.ENCHANTMENTS);
        if (enchantments != null) {
            for (net.minecraft.core.Holder<net.minecraft.world.item.enchantment.Enchantment> holder : enchantments.keySet()) {
                if (holder.is(net.minecraft.world.item.enchantment.Enchantments.SILK_TOUCH)) return enchantments.getLevel(holder) > 0;
            }
        }
        return false;
    }

    @Override
    public void awardAdvancement(net.minecraft.server.level.ServerPlayer player, CommonId advancementId, String criterion) {
        Identifier id = Identifier.fromNamespaceAndPath(advancementId.getNamespace(), advancementId.getPath());
        net.minecraft.advancements.AdvancementHolder adv = player.level().getServer().getAdvancements().get(id);
        if (adv != null) {
            player.getAdvancements().award(adv, criterion);
        }
    }

    @Override
    public void awardStat(net.minecraft.server.level.ServerPlayer player, CommonId statId) {
        if (player == null || statId == null) return;
        Identifier id = Identifier.fromNamespaceAndPath(statId.getNamespace(), statId.getPath());
        net.minecraft.stats.Stat<Identifier> stat = net.minecraft.stats.Stats.CUSTOM.get(id);
        if (stat != null) {
            player.awardStat(stat);
        }
    }

    @Override
    public net.minecraft.nbt.CompoundTag getCustomData(net.minecraft.world.item.ItemStack stack, boolean create) {
        net.minecraft.world.item.component.CustomData customData = stack.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA);
        return (customData != null) ? customData.copyTag() : (create ? new net.minecraft.nbt.CompoundTag() : null);
    }

    @Override
    public void updateCustomData(net.minecraft.world.item.ItemStack stack, java.util.function.Consumer<net.minecraft.nbt.CompoundTag> updater) {
        net.minecraft.world.item.component.CustomData.update(net.minecraft.core.component.DataComponents.CUSTOM_DATA, stack, updater);
    }

    @Override
    public net.minecraft.world.item.ItemStack getCraftingRemainingItem(net.minecraft.world.item.ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return net.minecraft.world.item.ItemStack.EMPTY;
        }
        net.minecraft.world.item.ItemStackTemplate remainder = stack.getItem().getCraftingRemainder();
        return remainder == null ? net.minecraft.world.item.ItemStack.EMPTY : remainder.create();
    }

    @Override
    public boolean isArmorItem(net.minecraft.world.item.ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        return stack.is(net.minecraft.tags.ItemTags.HEAD_ARMOR)
                || stack.is(net.minecraft.tags.ItemTags.CHEST_ARMOR)
                || stack.is(net.minecraft.tags.ItemTags.LEG_ARMOR)
                || stack.is(net.minecraft.tags.ItemTags.FOOT_ARMOR);
    }

    @Override
    public void configureFireworkStar(ItemStack stack, byte shapeId, int[] colors, boolean flicker, boolean trail) {
        storeCustomFireworkShape(stack, "Explosion", 0, shapeId, colors, flicker, trail);
        stack.set(net.minecraft.core.component.DataComponents.FIREWORK_EXPLOSION, createNativeFireworkExplosion(colors, flicker, trail));
    }

    @Override
    public void configureFireworkRocket(ItemStack stack, int flight, byte shapeId, int[] colors, boolean flicker, boolean trail) {
        storeCustomFireworkShape(stack, "Fireworks", flight, shapeId, colors, flicker, trail);
        stack.set(net.minecraft.core.component.DataComponents.FIREWORKS, new net.minecraft.world.item.component.Fireworks(
                Math.max(0, Math.min(3, flight)), java.util.List.of(createNativeFireworkExplosion(colors, flicker, trail))));
    }

    @Override
    public int[] getFireworkStarColors(ItemStack stack) {
        net.minecraft.world.item.component.FireworkExplosion explosion = stack.get(net.minecraft.core.component.DataComponents.FIREWORK_EXPLOSION);
        return explosion == null ? new int[0] : explosion.colors().toIntArray();
    }

    @Override
    public int getDyeFireworkColor(ItemStack stack) {
        CommonId id = getItemId(stack.getItem());
        if (id == null) return 0;
        for (net.minecraft.world.item.DyeColor color : net.minecraft.world.item.DyeColor.values()) {
            if ((color.getSerializedName() + "_dye").equals(id.getPath())) return color.getFireworkColor();
        }
        return 0;
    }

    private static net.minecraft.world.item.component.FireworkExplosion createNativeFireworkExplosion(int[] colors, boolean flicker, boolean trail) {
        return new net.minecraft.world.item.component.FireworkExplosion(
                net.minecraft.world.item.component.FireworkExplosion.Shape.SMALL_BALL,
                new it.unimi.dsi.fastutil.ints.IntArrayList(colors),
                it.unimi.dsi.fastutil.ints.IntLists.emptyList(), trail, flicker);
    }

    private void storeCustomFireworkShape(ItemStack stack, String key, int flight, byte shapeId, int[] colors, boolean flicker, boolean trail) {
        updateCustomData(stack, root -> {
            net.minecraft.nbt.CompoundTag explosion = new net.minecraft.nbt.CompoundTag();
            explosion.putByte("Type", shapeId);
            explosion.putIntArray("Colors", colors);
            explosion.putBoolean("Flicker", flicker);
            explosion.putBoolean("Trail", trail);
            if ("Explosion".equals(key)) root.put(key, explosion);
            else {
                net.minecraft.nbt.CompoundTag fireworks = new net.minecraft.nbt.CompoundTag();
                net.minecraft.nbt.ListTag list = new net.minecraft.nbt.ListTag();
                list.add(explosion);
                fireworks.put("Explosions", list);
                fireworks.putByte("Flight", (byte) Math.max(0, Math.min(3, flight)));
                root.put(key, fireworks);
            }
        });
    }

    @Override
    public CommonId getDimensionId(net.minecraft.world.level.Level level) {
        if (level == null) return CommonId.of("minecraft", "overworld");
        Identifier id = level.dimension().identifier();
        return CommonId.of(id.getNamespace(), id.getPath());
    }

    @Override
    public boolean isNight(net.minecraft.world.level.Level level) {
        return level != null && level.isDarkOutside();
    }

    @Override
    public long packChunkPos(int chunkX, int chunkZ) {
        return net.minecraft.world.level.ChunkPos.pack(chunkX, chunkZ);
    }

    @Override
    public long packChunkPos(net.minecraft.world.level.ChunkPos chunkPos) {
        return chunkPos != null ? chunkPos.pack() : 0L;
    }

    @Override
    public long packChunkPos(net.minecraft.core.BlockPos pos) {
        return net.minecraft.world.level.ChunkPos.pack(pos);
    }

    @Override
    public boolean isItemInTag(net.minecraft.world.item.ItemStack stack, CommonId tagId) {
        if (stack == null || stack.isEmpty() || tagId == null) return false;
        try {
            Identifier id = Identifier.fromNamespaceAndPath(tagId.getNamespace(), tagId.getPath());
            net.minecraft.tags.TagKey<net.minecraft.world.item.Item> tagKey =
                    net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, id);
            return stack.is(tagKey);
        } catch (Exception exception) {
            com.kingodogo.buildscape.BuildscapeCommon.LOGGER.warn("Failed in 26.x isItemInTag", exception);
            return false;
        }
    }

    @Override
    public boolean isBlockInTag(net.minecraft.world.level.block.Block block, CommonId tagId) {
        if (block == null || tagId == null) return false;
        try {
            Identifier id = Identifier.fromNamespaceAndPath(tagId.getNamespace(), tagId.getPath());
            net.minecraft.tags.TagKey<net.minecraft.world.level.block.Block> tagKey =
                    net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.BLOCK, id);
            return block.defaultBlockState().is(tagKey);
        } catch (Exception exception) {
            com.kingodogo.buildscape.BuildscapeCommon.LOGGER.warn("Failed in 26.x isBlockInTag", exception);
            return false;
        }
    }

    @Override
    public boolean isEntityTypeInTag(net.minecraft.world.entity.Entity entity, CommonId tagId) {
        if (entity == null || tagId == null) return false;
        try {
            Identifier id = Identifier.fromNamespaceAndPath(tagId.getNamespace(), tagId.getPath());
            net.minecraft.tags.TagKey<net.minecraft.world.entity.EntityType<?>> tagKey =
                    net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, id);
            return entity.getType().builtInRegistryHolder().is(tagKey);
        } catch (Exception exception) {
            com.kingodogo.buildscape.BuildscapeCommon.LOGGER.warn("Failed in 26.x isEntityTypeInTag", exception);
            return false;
        }
    }

    @Override
    public void markEntityVelocityChanged(net.minecraft.world.entity.Entity entity) {
        entity.hurtMarked = true;
    }

    @Override
    public InteractionResult sidedSuccess(boolean clientSide) {
        return clientSide ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
    }

    @Override
    public boolean doesBedExplode(Level level) {
        return level.environmentAttributes().getDimensionValue(net.minecraft.world.attribute.EnvironmentAttributes.BED_RULE).explodes();
    }

    @Override
    public net.minecraft.world.item.trading.MerchantOffer createMerchantOffer(net.minecraft.world.item.ItemStack cost, net.minecraft.world.item.ItemStack result, int maxUses, int xp, float priceMultiplier) {
        return new net.minecraft.world.item.trading.MerchantOffer(new net.minecraft.world.item.trading.ItemCost(cost.getItem(), cost.getCount()), result, maxUses, xp, priceMultiplier);
    }

    @Override
    public net.minecraft.world.item.crafting.Ingredient createTagIngredient(CommonId id) {
        net.minecraft.tags.TagKey<Item> tag = createItemTagKey(id);
        java.util.List<net.minecraft.core.Holder<Item>> holders = new java.util.ArrayList<>();
        for (net.minecraft.core.Holder<Item> holder : net.minecraft.core.registries.BuiltInRegistries.ITEM.getTagOrEmpty(tag)) {
            holders.add(holder);
        }
        return holders.isEmpty()
                ? net.minecraft.world.item.crafting.Ingredient.of()
                : net.minecraft.world.item.crafting.Ingredient.of(net.minecraft.core.HolderSet.direct(holders));
    }

    @Override
    public net.minecraft.tags.TagKey<net.minecraft.world.item.Item> createItemTagKey(CommonId id) {
        return net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM,
                net.minecraft.resources.Identifier.fromNamespaceAndPath(id.getNamespace(), id.getPath()));
    }

    @Override
    public boolean isGlassBaseBlock(net.minecraft.world.level.block.Block block) {
        return block instanceof net.minecraft.world.level.block.TransparentBlock;
    }

    @Override
    public boolean isEnchantingTableBlock(net.minecraft.world.level.block.Block block) {
        return block instanceof net.minecraft.world.level.block.EnchantingTableBlock;
    }

    @Override
    public boolean isFood(net.minecraft.world.item.ItemStack stack) {
        return stack != null && !stack.isEmpty() && stack.has(net.minecraft.core.component.DataComponents.FOOD);
    }

    @Override
    public boolean isWaterPotion(net.minecraft.world.item.ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        if (stack.is(net.minecraft.world.item.Items.WATER_BUCKET)) return true;
        if (stack.getItem() instanceof net.minecraft.world.item.PotionItem) {
            net.minecraft.world.item.alchemy.PotionContents contents = stack.get(net.minecraft.core.component.DataComponents.POTION_CONTENTS);
            return contents != null && contents.is(net.minecraft.world.item.alchemy.Potions.WATER);
        }
        return false;
    }

    @Override
    public boolean isEnchantedItem(net.minecraft.world.item.ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        return stack.isEnchanted() || stack.hasFoil()
                || !stack.getEnchantments().isEmpty()
                || !net.minecraft.world.item.enchantment.EnchantmentHelper.getEnchantmentsForCrafting(stack).isEmpty();
    }

    @Override
    public net.minecraft.world.item.ItemStack createWaterPotion() {
        return net.minecraft.world.item.alchemy.PotionContents.createItemStack(
                net.minecraft.world.item.Items.POTION,
                net.minecraft.world.item.alchemy.Potions.WATER
        );
    }

    @Override
    public void applyFoodEffects(net.minecraft.world.entity.player.Player player, net.minecraft.world.level.Level level, net.minecraft.world.item.ItemStack foodStack) {
        if (player == null || foodStack == null || foodStack.isEmpty()) return;
        net.minecraft.world.food.FoodProperties food = foodStack.get(net.minecraft.core.component.DataComponents.FOOD);
        if (food != null) {
            player.getFoodData().eat(food);
        }
    }

    @Override
    public void applyPotionEffects(net.minecraft.world.entity.LivingEntity entity, net.minecraft.world.item.ItemStack potionStack) {
        if (entity == null || potionStack == null || potionStack.isEmpty()) return;
        net.minecraft.world.item.alchemy.PotionContents contents = potionStack.get(net.minecraft.core.component.DataComponents.POTION_CONTENTS);
        if (contents != null) {
            contents.applyToLivingEntity(entity, 1.0F);
        }
    }

    @Override
    public String getTagString(net.minecraft.nbt.CompoundTag tag, String key, String defaultValue) {
        if (tag == null || !tag.contains(key)) return defaultValue;
        return tag.getString(key).orElse(defaultValue);
    }

    @Override
    public int getTagInt(net.minecraft.nbt.CompoundTag tag, String key, int defaultValue) {
        if (tag == null || !tag.contains(key)) return defaultValue;
        return tag.getInt(key).orElse(defaultValue);
    }

    @Override
    public boolean getTagBoolean(net.minecraft.nbt.CompoundTag tag, String key, boolean defaultValue) {
        if (tag == null || !tag.contains(key)) return defaultValue;
        return tag.getBoolean(key).orElse(defaultValue);
    }

    @Override
    public net.minecraft.nbt.CompoundTag getTagCompound(net.minecraft.nbt.CompoundTag tag, String key) {
        if (tag == null || !tag.contains(key)) return null;
        return tag.getCompound(key).orElse(null);
    }

    @Override
    public java.util.Set<String> getTagKeys(net.minecraft.nbt.CompoundTag tag) {
        return tag != null ? tag.keySet() : java.util.Collections.emptySet();
    }

    @Override
    public net.minecraft.nbt.CompoundTag readCompressedTag(java.io.File file) throws java.io.IOException {
        return net.minecraft.nbt.NbtIo.readCompressed(file.toPath(), net.minecraft.nbt.NbtAccounter.unlimitedHeap());
    }

    @Override
    public void writeCompressedTag(java.io.File file, net.minecraft.nbt.CompoundTag tag) throws java.io.IOException {
        net.minecraft.nbt.NbtIo.writeCompressed(tag, file.toPath());
    }

    @Override
    public java.util.List<String> getTagStringList(net.minecraft.nbt.CompoundTag tag, String key) {
        java.util.List<String> result = new java.util.ArrayList<>();
        if (tag == null || !tag.contains(key)) return result;
        net.minecraft.nbt.ListTag list = tag.getList(key).orElse(null);
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                result.add(list.getString(i).orElse(""));
            }
        }
        return result;
    }

    @Override
    public void putTagStringList(net.minecraft.nbt.CompoundTag tag, String key, java.util.List<String> list) {
        if (tag == null) return;
        net.minecraft.nbt.ListTag listTag = new net.minecraft.nbt.ListTag();
        if (list != null) {
            for (String s : list) {
                listTag.add(net.minecraft.nbt.StringTag.valueOf(s));
            }
        }
        tag.put(key, listTag);
    }

    @Override
    public void spawnMob(net.minecraft.server.level.ServerLevel level, net.minecraft.world.entity.EntityType<?> type, net.minecraft.world.item.ItemStack stack, net.minecraft.core.BlockPos pos) {
        if (level == null || type == null) return;
        type.spawn(level, stack, null, pos, net.minecraft.world.entity.EntitySpawnReason.SPAWN_ITEM_USE, true, false);
    }

    @Override
    public boolean isSameItemSameComponents(net.minecraft.world.item.ItemStack a, net.minecraft.world.item.ItemStack b) {
        return net.minecraft.world.item.ItemStack.isSameItemSameComponents(a, b);
    }

    @Override
    public void applyNauseaEffect(net.minecraft.world.entity.LivingEntity entity, int durationTicks, int amplifier) {
        if (entity != null) {
            entity.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.NAUSEA, durationTicks, amplifier, true, true));
        }
    }

    @Override
    public void hurtFreezeDamage(net.minecraft.world.entity.LivingEntity entity, float amount) {
        if (entity != null) {
            entity.hurt(entity.damageSources().freeze(), amount);
        }
    }

    @Override
    public int getSelectedSlot(net.minecraft.world.entity.player.Inventory inventory) {
        return inventory != null ? inventory.getSelectedSlot() : 0;
    }

    @Override
    public <T extends net.minecraft.world.level.block.entity.BlockEntity> net.minecraft.world.level.block.entity.BlockEntityType<T> createBlockEntityType(com.kingodogo.buildscape.platform.BlockEntityFactory<T> factory, java.util.function.Predicate<net.minecraft.world.level.block.state.BlockState> isValid) {
        return new net.minecraft.world.level.block.entity.BlockEntityType<>(factory::create, java.util.Set.of()) {
            @Override
            public boolean isValid(net.minecraft.world.level.block.state.BlockState state) {
                return isValid.test(state);
            }
        };
    }

    @Override
    public boolean isOnGround(net.minecraft.world.entity.Entity entity) {
        return entity.onGround();
    }

    @Override
    public void spawnMobFromEgg(net.minecraft.server.level.ServerLevel level, net.minecraft.core.BlockPos pos, net.minecraft.world.item.ItemStack eggStack) {
        net.minecraft.world.entity.EntityType<?> entityType = net.minecraft.world.item.SpawnEggItem.getType(eggStack);
        if (entityType != null) {
            net.minecraft.world.phys.Vec3 spawnPos = net.minecraft.world.phys.Vec3.atCenterOf(pos).add(0, 0.5, 0);
            spawnMob(level, entityType, eggStack, new net.minecraft.core.BlockPos((int) spawnPos.x, (int) spawnPos.y, (int) spawnPos.z));
            level.sendParticles(net.minecraft.core.particles.ParticleTypes.POOF, spawnPos.x, spawnPos.y + 0.5, spawnPos.z, 8, 0.3, 0.3, 0.3, 0.05);
        }
    }

    @Override
    public Item createBuildersPouchItem(Item.Properties properties) {
        return new com.kingodogo.buildscape.item.BuildersPouchItem(properties) {
            @Override
            public InteractionResult use(Level level, Player player, InteractionHand hand) {
                if (!level.isClientSide()) {
                    Component title = com.kingodogo.buildscape.util.ComponentHelper.translatable("container.buildscape.builders_pouch");
                    player.openMenu(new net.minecraft.world.SimpleMenuProvider((id, inventory, ignored) ->
                            new com.kingodogo.buildscape.menu.BuildersPouchMenu(id, inventory, hand), title));
                }
                return InteractionResult.SUCCESS;
            }

            @Override
            public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay displayContext, Consumer<Component> tooltip, TooltipFlag flag) {
                com.kingodogo.buildscape.item.BuildersPouchItem.appendBuildersPouchTooltip(stack, tooltip);
                super.appendHoverText(stack, context, displayContext, tooltip, flag);
            }
        };
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean supportsHammerEnchantment(ItemStack stack, Object enchantment) {
        if (enchantment instanceof net.minecraft.core.Holder) {
            net.minecraft.core.Holder<net.minecraft.world.item.enchantment.Enchantment> h =
                    (net.minecraft.core.Holder<net.minecraft.world.item.enchantment.Enchantment>) enchantment;
            return h.is(net.minecraft.world.item.enchantment.Enchantments.UNBREAKING)
                    || h.is(net.minecraft.world.item.enchantment.Enchantments.MENDING)
                    || h.is(net.minecraft.world.item.enchantment.Enchantments.SILK_TOUCH);
        }
        return false;
    }

    @Override
    public Item createHammerItem(com.kingodogo.buildscape.item.HammerItem.HammerTier tier, Item.Properties properties) {
        return new com.kingodogo.buildscape.item.HammerItem(tier, properties) {
            public boolean supportsEnchantment(ItemStack stack, net.minecraft.core.Holder<net.minecraft.world.item.enchantment.Enchantment> enchantment) {
                return supportsHammerEnchantment(stack, enchantment);
            }

            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
                appendHammerTooltip(tooltip);
                super.appendHoverText(stack, context, display, tooltip, flag);
            }

            @Override
            public InteractionResult use(Level level, Player player, InteractionHand hand) {
                if (hand == InteractionHand.MAIN_HAND) {
                    ItemStack offHand = player.getOffhandItem();
                    if (!offHand.isEmpty() && offHand.getItem() instanceof BlockItem) {
                        return InteractionResult.SUCCESS;
                    }
                }
                return InteractionResult.PASS;
            }
        };
    }

    @Override
    public void playNoteBlockChime(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos, float pitch) {
        level.playSound(null, pos, net.minecraft.sounds.SoundEvents.NOTE_BLOCK_CHIME.value(), net.minecraft.sounds.SoundSource.PLAYERS, 1.0f, pitch);
    }

    private static final java.util.Map<java.util.UUID, net.minecraft.nbt.CompoundTag> V26X_ENTITY_DATA = new java.util.concurrent.ConcurrentHashMap<>();
    private static final java.util.Map<net.minecraft.world.level.block.entity.BlockEntity, net.minecraft.nbt.CompoundTag> V26X_BLOCK_ENTITY_DATA = new java.util.concurrent.ConcurrentHashMap<>();

    @Override
    public net.minecraft.nbt.CompoundTag getEntityData(net.minecraft.world.entity.Entity entity) {
        if (entity == null) return new net.minecraft.nbt.CompoundTag();
        return V26X_ENTITY_DATA.computeIfAbsent(entity.getUUID(), k -> new net.minecraft.nbt.CompoundTag());
    }

    @Override
    public net.minecraft.nbt.CompoundTag getBlockEntityData(net.minecraft.world.level.block.entity.BlockEntity be) {
        if (be == null) return new net.minecraft.nbt.CompoundTag();
        return V26X_BLOCK_ENTITY_DATA.computeIfAbsent(be, k -> new net.minecraft.nbt.CompoundTag());
    }

    @Override
    public long getTagLong(net.minecraft.nbt.CompoundTag tag, String key, long defaultValue) {
        return tag != null && tag.contains(key) ? tag.getLong(key).orElse(defaultValue) : defaultValue;
    }

    @Override
    public boolean hasTagUUID(net.minecraft.nbt.CompoundTag tag, String key) {
        return tag != null && tag.contains(key);
    }

    @Override
    public java.util.UUID getTagUUID(net.minecraft.nbt.CompoundTag tag, String key) {
        if (tag == null || !tag.contains(key)) return null;
        try {
            return java.util.UUID.fromString(tag.getString(key).orElse(""));
        } catch (Exception exception) {
            com.kingodogo.buildscape.BuildscapeCommon.LOGGER.warn("Failed to read UUID from tag key {}", key, exception);
            return null;
        }
    }

    @Override
    public void putTagUUID(net.minecraft.nbt.CompoundTag tag, String key, java.util.UUID uuid) {
        if (tag != null && uuid != null) tag.putString(key, uuid.toString());
    }

    @Override
    public int getMaxBuildHeight(net.minecraft.world.level.Level level) {
        return level != null ? level.getMaxY() : 320;
    }

    @Override
    public net.minecraft.core.BlockPos getSharedSpawnPos(net.minecraft.world.level.Level level) {
        if (level == null) return net.minecraft.core.BlockPos.ZERO;
        return level.getRespawnData().pos();
    }

    @Override
    public Item createConfettiItem(Item.Properties properties) {
        return new com.kingodogo.buildscape.item.ConfettiItem(properties) {
            @Override
            public InteractionResult use(Level level, Player player, InteractionHand hand) {
                ItemStack itemstack = player.getItemInHand(hand);
                if (!level.isClientSide()) {
                    level.playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.PLAYERS, 0.8F, 1.4F);
                    level.playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.FIREWORK_ROCKET_TWINKLE, SoundSource.PLAYERS, 0.6F, 1.6F);
                    if (!player.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    player.getCooldowns().addCooldown(itemstack, 10);
                }
                return InteractionResult.SUCCESS;
            }

            @Override
            public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay displayContext, Consumer<Component> tooltip, TooltipFlag flag) {
                com.kingodogo.buildscape.item.ConfettiItem.appendConfettiTooltip(stack, tooltip);
                super.appendHoverText(stack, context, displayContext, tooltip, flag);
            }
        };
    }

    @Override
    public Item createBottleOfMistItem(Item.Properties properties) {
        return new com.kingodogo.buildscape.item.BottleOfMistItem(properties) {
            @Override
            public InteractionResult use(Level level, Player player, InteractionHand hand) {
                ItemStack stack = player.getItemInHand(hand);
                useBuildscape(level, player, stack);
                return sidedSuccess(level.isClientSide());
            }

            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
                appendBuildscapeTooltip(tooltip);
                super.appendHoverText(stack, context, display, tooltip, flag);
            }
        };
    }

    @Override
    public Item createWrenchItem(Item.Properties properties) {
        return new com.kingodogo.buildscape.item.WrenchItem(properties) {
            @Override public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
                appendBuildscapeTooltip(tooltip);
                super.appendHoverText(stack, context, display, tooltip, flag);
            }
        };
    }

    @Override
    public Item createFestiveGlintShardItem(Item.Properties properties) {
        return new com.kingodogo.buildscape.item.FestiveGlintShardItem(properties) {
            @Override public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
                appendBuildscapeTooltip(tooltip);
            }
        };
    }

    @Override
    public Item createBigOrnamentTemplateItem(Item.Properties properties) {
        return new com.kingodogo.buildscape.item.BigOrnamentTemplateItem(properties) {
            @Override public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
                super.appendHoverText(stack, context, display, tooltip, flag);
                appendBuildscapeTooltip(tooltip);
            }
        };
    }

    @Override
    public Item createStringlightFrameItem(Item.Properties properties) {
        return new com.kingodogo.buildscape.item.StringlightFrameItem(properties) {
            @Override public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
                appendBuildscapeTooltip(tooltip);
                super.appendHoverText(stack, context, display, tooltip, flag);
            }
        };
    }

    @Override
    public boolean hasEntityTag(net.minecraft.world.entity.Entity entity, String tag) {
        return entity != null && entity.entityTags().contains(tag);
    }

    @Override
    public boolean addEntityTag(net.minecraft.world.entity.Entity entity, String tag) {
        return entity != null && entity.addTag(tag);
    }

    @Override
    public boolean removeEntityTag(net.minecraft.world.entity.Entity entity, String tag) {
        return entity != null && entity.removeTag(tag);
    }

    @Override
    public void loadAllItems(net.minecraft.nbt.CompoundTag tag, net.minecraft.core.NonNullList<net.minecraft.world.item.ItemStack> items) {
        net.minecraft.core.HolderLookup.Provider provider = net.minecraft.core.RegistryAccess.fromRegistryOfRegistries(net.minecraft.core.registries.BuiltInRegistries.REGISTRY);
        net.minecraft.world.level.storage.ValueInput input = net.minecraft.world.level.storage.TagValueInput.create(net.minecraft.util.ProblemReporter.DISCARDING, provider, tag);
        net.minecraft.world.ContainerHelper.loadAllItems(input, items);
    }

    @Override
    public void saveAllItems(net.minecraft.nbt.CompoundTag tag, net.minecraft.core.NonNullList<net.minecraft.world.item.ItemStack> items) {
        net.minecraft.core.HolderLookup.Provider provider = net.minecraft.core.RegistryAccess.fromRegistryOfRegistries(net.minecraft.core.registries.BuiltInRegistries.REGISTRY);
        net.minecraft.world.level.storage.TagValueOutput output = net.minecraft.world.level.storage.TagValueOutput.createWithContext(net.minecraft.util.ProblemReporter.DISCARDING, provider);
        net.minecraft.world.ContainerHelper.saveAllItems(output, items);
        tag.remove("Items");
        tag.merge(output.buildResult());
    }

    @Override
    public net.minecraft.world.item.ItemStack loadSingleItemStack(net.minecraft.nbt.CompoundTag tag) {
        if (tag == null || tag.isEmpty()) return net.minecraft.world.item.ItemStack.EMPTY;
        net.minecraft.core.HolderLookup.Provider provider = net.minecraft.core.RegistryAccess.fromRegistryOfRegistries(net.minecraft.core.registries.BuiltInRegistries.REGISTRY);
        net.minecraft.world.level.storage.ValueInput input = net.minecraft.world.level.storage.TagValueInput.create(net.minecraft.util.ProblemReporter.DISCARDING, provider, tag);
        return input.read("root", net.minecraft.world.item.ItemStack.CODEC).orElseGet(() -> {
            net.minecraft.core.NonNullList<net.minecraft.world.item.ItemStack> list = net.minecraft.core.NonNullList.withSize(1, net.minecraft.world.item.ItemStack.EMPTY);
            net.minecraft.world.ContainerHelper.loadAllItems(input, list);
            return list.get(0);
        });
    }

    @Override
    public net.minecraft.nbt.CompoundTag saveSingleItemStack(net.minecraft.world.item.ItemStack stack) {
        if (stack == null || stack.isEmpty()) return new net.minecraft.nbt.CompoundTag();
        net.minecraft.core.HolderLookup.Provider provider = net.minecraft.core.RegistryAccess.fromRegistryOfRegistries(net.minecraft.core.registries.BuiltInRegistries.REGISTRY);
        net.minecraft.world.level.storage.TagValueOutput output = net.minecraft.world.level.storage.TagValueOutput.createWithContext(net.minecraft.util.ProblemReporter.DISCARDING, provider);
        output.store("root", net.minecraft.world.item.ItemStack.CODEC, stack);
        return output.buildResult();
    }

    @Override
    public Item createPatternItem(Item.Properties properties, String tooltipKey) {
        return new com.kingodogo.buildscape.item.PatternItem(properties, tooltipKey) {
            @Override
            public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay displayContext, Consumer<Component> tooltip, TooltipFlag flag) {
                super.appendHoverText(stack, context, displayContext, tooltip, flag);
                appendPatternTooltip(stack, tooltip);
            }
        };
    }

    @Override
    public BlockItem createGlassJarItem(Block block, Item.Properties properties) {
        return new com.kingodogo.buildscape.item.GlassJarItem(block, properties);
    }

    @Override
    public BlockItem createGoldenJarItem(Block block, Item.Properties properties) {
        return new com.kingodogo.buildscape.item.GoldenJarItem(block, properties) {
            @Override
            public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay displayContext, Consumer<Component> tooltip, TooltipFlag flag) {
                super.appendHoverText(stack, context, displayContext, tooltip, flag);
                com.kingodogo.buildscape.item.GoldenJarItem.addAdvancementTooltip(stack, tooltip);
            }
        };
    }

    @Override
    public BlockItem createFestiveStarItem(Block block, Item.Properties properties) {
        return new com.kingodogo.buildscape.item.FestiveStarItem(block, properties) {
            @Override
            public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay displayContext, Consumer<Component> tooltip, TooltipFlag flag) {
                super.appendHoverText(stack, context, displayContext, tooltip, flag);
                com.kingodogo.buildscape.item.FestiveStarItem.appendFestiveStarTooltip(stack, tooltip);
            }
        };
    }

    @Override
    public BlockItem createMuffBlockItem(Block block, Item.Properties properties) {
        return new com.kingodogo.buildscape.item.MuffBlockItem(block, properties) {
            @Override
            public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay displayContext, Consumer<Component> tooltip, TooltipFlag flag) {
                com.kingodogo.buildscape.item.MuffBlockItem.appendMuffBlockTooltip(tooltip);
                super.appendHoverText(stack, context, displayContext, tooltip, flag);
            }
        };
    }

    @Override
    public BlockItem createMistBlockItem(Block block, Item.Properties properties) {
        return new com.kingodogo.buildscape.item.MistBlockItem(block, properties) {
            @Override
            public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay displayContext, Consumer<Component> tooltip, TooltipFlag flag) {
                appendMistBlockTooltip(tooltip);
                super.appendHoverText(stack, context, displayContext, tooltip, flag);
            }
        };
    }

    @Override
    public BlockItem createFestiveStockingItem(Block block, Item.Properties properties, String colorVariant) {
        return new com.kingodogo.buildscape.item.FestiveStockingItem(block, properties, colorVariant);
    }

    @Override
    public BlockItem createCopperChestItem(Block block, Item.Properties properties) {
        return new com.kingodogo.buildscape.item.CopperChestItem(block, properties);
    }

    @Override
    public Item createInfinitePhoenixFireworkStarItem(Item.Properties properties) {
        return new com.kingodogo.buildscape.item.InfinitePhoenixFireworkStarItem(properties) {
            @Override
            public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay displayContext, Consumer<Component> tooltip, TooltipFlag flag) {
                super.appendHoverText(stack, context, displayContext, tooltip, flag);
                com.kingodogo.buildscape.item.InfinitePhoenixFireworkStarItem.appendInfiniteUsesTooltip(tooltip);
            }
        };
    }

    @Override
    public Item createColoredItemFrameItem(Item.Properties properties, String colorVariant) {
        return new com.kingodogo.buildscape.item.ColoredItemFrameItem(properties, colorVariant) {
            @Override
            public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay displayContext, Consumer<Component> tooltip, TooltipFlag flag) {
                appendColoredItemFrameTooltip(stack, tooltip);
                super.appendHoverText(stack, context, displayContext, tooltip, flag);
            }
        };
    }

    @Override
    public Item createExperienceBucketItem(Item.Properties properties) {
        return new net.minecraft.world.item.BucketItem(
                com.kingodogo.buildscape.adapter.v26x.fluid.ExperienceFluids.still(),
                properties.stacksTo(1).craftRemainder(net.minecraft.world.item.Items.BUCKET)) {
            @Override
            public InteractionResult use(Level level, Player player, InteractionHand hand) {
                if (!player.isShiftKeyDown()) {
                    InteractionResult placement = super.use(level, player, hand);
                    if (placement != InteractionResult.PASS) return placement;
                }
                player.startUsingItem(hand);
                return InteractionResult.CONSUME;
            }

            @Override
            public ItemStack finishUsingItem(ItemStack stack, Level level, net.minecraft.world.entity.LivingEntity entity) {
                if (entity instanceof Player player) {
                    if (!level.isClientSide()) {
                        int xp = 25 + player.getRandom().nextInt(6);
                        player.giveExperiencePoints(xp);
                    }

                    if (entity instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                        net.minecraft.advancements.triggers.CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, stack);
                        serverPlayer.awardStat(net.minecraft.stats.Stats.ITEM_USED.get(this));
                    }

                    if (!player.getAbilities().instabuild) {
                        return new ItemStack(net.minecraft.world.item.Items.BUCKET);
                    }
                }
                return stack;
            }

            @Override
            public int getUseDuration(ItemStack stack, net.minecraft.world.entity.LivingEntity entity) {
                return 32;
            }

            @Override
            public net.minecraft.world.item.ItemUseAnimation getUseAnimation(ItemStack stack) {
                return net.minecraft.world.item.ItemUseAnimation.DRINK;
            }
        };
    }

    @Override
    public Item createMangroveBoatItem(Item.Properties properties) {
        return new Item(properties);
    }

    @Override
    public Item createPoplarBoatItem(Item.Properties properties) {
        return new Item(properties.stacksTo(1)) {
            @Override
            public net.minecraft.world.InteractionResult use(Level level, net.minecraft.world.entity.player.Player player, net.minecraft.world.InteractionHand hand) {
                return switch (Services.PLATFORM.placePoplarBoat(this, level, player, hand)) {
                    case FAIL -> net.minecraft.world.InteractionResult.FAIL;
                    case SUCCESS -> level.isClientSide() ? net.minecraft.world.InteractionResult.SUCCESS : net.minecraft.world.InteractionResult.SUCCESS_SERVER;
                    case PASS -> net.minecraft.world.InteractionResult.PASS;
                };
            }
        };
    }

    @Override
    public ItemStack removeItem(List<ItemStack> items, int slot, int amount) {
        return net.minecraft.world.ContainerHelper.removeItem(items, slot, amount);
    }

    @Override
    public ItemStack takeItem(List<ItemStack> items, int slot) {
        return net.minecraft.world.ContainerHelper.takeItem(items, slot);
    }

    @Override
    public int applyBiomeToArea(net.minecraft.server.level.ServerLevel serverLevel, BlockPos pos1, BlockPos pos2, net.minecraft.core.Holder<net.minecraft.world.level.biome.Biome> biomeHolder, ItemStack stack) {
        int minX = Math.min(pos1.getX(), pos2.getX());
        int maxX = Math.max(pos1.getX(), pos2.getX());
        int minY = Math.min(pos1.getY(), pos2.getY());
        int maxY = Math.max(pos1.getY(), pos2.getY());
        int minZ = Math.min(pos1.getZ(), pos2.getZ());
        int maxZ = Math.max(pos1.getZ(), pos2.getZ());

        int unbreakingLevel = getUnbreakingLevel(stack);
        int affectedBlocks = 0;
        Set<net.minecraft.world.level.chunk.LevelChunk> modifiedChunks = new HashSet<>();

        net.minecraft.world.level.chunk.LevelChunk lastChunk = null;
        int lastChunkX = Integer.MIN_VALUE;
        int lastChunkZ = Integer.MIN_VALUE;

        net.minecraft.world.level.chunk.LevelChunkSection lastSection = null;
        int lastSectionIndex = Integer.MIN_VALUE;

        int lastQuartX = Integer.MIN_VALUE;
        int lastQuartY = Integer.MIN_VALUE;
        int lastQuartZ = Integer.MIN_VALUE;

        boolean broken = false;
        for (int x = minX; x <= maxX; x++) {
            int shiftedX = x - 2;
            int chunkX = shiftedX >> 4;
            int qX = shiftedX >> 2;
            int localQx = qX & 3;

            for (int z = minZ; z <= maxZ; z++) {
                int shiftedZ = z - 2;
                int chunkZ = shiftedZ >> 4;
                int qZ = shiftedZ >> 2;
                int localQz = qZ & 3;

                net.minecraft.world.level.chunk.LevelChunk chunk;
                if (chunkX == lastChunkX && chunkZ == lastChunkZ && lastChunk != null) {
                    chunk = lastChunk;
                } else {
                    chunk = serverLevel.getChunkSource().getChunkNow(chunkX, chunkZ);
                    lastChunk = chunk;
                    lastChunkX = chunkX;
                    lastChunkZ = chunkZ;
                    lastSection = null;
                    lastSectionIndex = Integer.MIN_VALUE;
                    lastQuartX = Integer.MIN_VALUE;
                    lastQuartY = Integer.MIN_VALUE;
                    lastQuartZ = Integer.MIN_VALUE;
                    if (chunk != null) {
                        modifiedChunks.add(chunk);
                    }
                }

                if (chunk == null) {
                    continue;
                }

                for (int y = minY; y <= maxY; y++) {
                    if (stack != null && !stack.isEmpty() && stack.getDamageValue() >= stack.getMaxDamage()) {
                        broken = true;
                        break;
                    }

                    int shiftedY = y - 2;
                    int sectionIndex = chunk.getSectionIndex(shiftedY);
                    int qY = shiftedY >> 2;
                    int localQy = qY & 3;

                    net.minecraft.world.level.chunk.LevelChunkSection section;
                    if (sectionIndex == lastSectionIndex && lastSection != null) {
                        section = lastSection;
                    } else {
                        if (sectionIndex >= 0 && sectionIndex < chunk.getSections().length) {
                            section = chunk.getSections()[sectionIndex];
                        } else {
                            section = null;
                        }
                        lastSection = section;
                        lastSectionIndex = sectionIndex;
                        lastQuartX = Integer.MIN_VALUE;
                        lastQuartY = Integer.MIN_VALUE;
                        lastQuartZ = Integer.MIN_VALUE;
                    }

                    if (section != null) {
                        if (qX != lastQuartX || qY != lastQuartY || qZ != lastQuartZ) {
                            setSectionBiome(section, localQx, localQy, localQz, biomeHolder);
                            lastQuartX = qX;
                            lastQuartY = qY;
                            lastQuartZ = qZ;
                        }
                    }

                    affectedBlocks++;

                    if (stack != null && !stack.isEmpty()) {
                        boolean shouldDamage = true;
                        if (unbreakingLevel > 0) {
                            shouldDamage = serverLevel.getRandom().nextInt(unbreakingLevel + 1) == 0;
                        }
                        if (shouldDamage) {
                            stack.setDamageValue(stack.getDamageValue() + 1);
                        }
                    }
                }
                if (broken) {
                    break;
                }
            }
            if (broken) {
                break;
            }
        }

        for (net.minecraft.world.level.chunk.LevelChunk chunk : modifiedChunks) {
            markChunkUnsaved(chunk);
            syncChunk(serverLevel, chunk);
        }

        return affectedBlocks;
    }

    @Override
    public void scanChunkForBlock(net.minecraft.server.level.ServerLevel level, net.minecraft.world.level.chunk.LevelChunk chunk, Predicate<BlockState> predicate, BiConsumer<BlockPos, BlockState> action) {
        net.minecraft.world.level.chunk.LevelChunkSection[] sections = chunk.getSections();
        for (int sectionIndex = 0; sectionIndex < sections.length; sectionIndex++) {
            net.minecraft.world.level.chunk.LevelChunkSection section = sections[sectionIndex];
            if (section.hasOnlyAir() || !section.maybeHas(predicate)) {
                continue;
            }
            int bottomY = chunk.getSectionYFromSectionIndex(sectionIndex) << 4;

            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    for (int x = 0; x < 16; x++) {
                        BlockState state = section.getBlockState(x, y, z);
                        if (predicate.test(state)) {
                            BlockPos pos = new BlockPos(
                                    chunk.getPos().getMinBlockX() + x,
                                    bottomY + y,
                                    chunk.getPos().getMinBlockZ() + z
                            );
                            action.accept(pos, state);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void playWoolBreak(Level level, double x, double y, double z) {
        level.playSound(null, x, y, z, SoundEvents.WOOL_BREAK, SoundSource.PLAYERS, 1.0f, 1.2f);
    }

    @Override
    public void playExperienceOrbPickup(Level level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 0.5f, 1.0f);
    }

    @Override
    public void playExperienceOrbPickup(Level level, double x, double y, double z) {
        playExperienceOrbPickup(level, x, y, z, 0.5f, 0.8f);
    }

    @Override
    public void playExperienceOrbPickup(Level level, double x, double y, double z, float volume, float pitch) {
        level.playSound(null, x, y, z, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, volume, pitch);
    }

    @Override
    public void playDispenserFail(Level level, double x, double y, double z) {
        level.playSound(null, x, y, z, SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 1.0f, 0.8f);
    }

    @Override
    public void playBoneMealUse(Level level, double x, double y, double z) {
        level.playSound(null, x, y, z, SoundEvents.BONE_MEAL_USE, SoundSource.PLAYERS, 1.0f, 1.0f);
    }

    @Override
    public void playDragonBreathFill(Level level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.BOTTLE_FILL_DRAGONBREATH, SoundSource.PLAYERS, 1.0f, 1.2f);
    }

    @Override
    public void playFireExtinguish(Level level, Player player, BlockPos pos) {
        level.playSound(player, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.6f, 0.8f);
    }

    @Override
    public void playFireExtinguish(Level level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0f, 1.0f);
    }

    @Override
    public void playFireExtinguish(Level level, double x, double y, double z) {
        level.playSound(null, x, y, z, SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 0.1f, 0.1f);
    }

    @Override
    public void playWaterAmbient(Level level, BlockPos pos) {
        level.playLocalSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, SoundEvents.WATER_AMBIENT, SoundSource.BLOCKS, 0.1F, 1.2F, false);
    }

    @Override
    public void playPowderSnowStep(Level level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.POWDER_SNOW_STEP, SoundSource.BLOCKS, 0.5F, 1.0F);
    }

    @Override
    public void playWaxOn(Level level, Player player, BlockPos pos) {
        level.playSound(player, pos, SoundEvents.HONEYCOMB_WAX_ON, SoundSource.BLOCKS, 1.0f, 1.0f);
    }

    @Override
    public void playWaxOff(Level level, Player player, BlockPos pos) {
        level.playSound(player, pos, SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1.0f, 1.0f);
    }

    @Override
    public void playAxeScrape(Level level, Player player, BlockPos pos) {
        level.playSound(player, pos, SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 1.0f, 1.0f);
    }

    @Override
    public void playAxeStrip(Level level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    @Override
    public void playBottleFill(Level level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0f, 1.0f);
    }

    @Override
    public void playBottleEmpty(Level level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0f, 1.0f);
    }

    @Override
    public void playBucketFill(Level level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0f, 1.0f);
    }

    @Override
    public void playBucketEmpty(Level level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0f, 1.0f);
    }

    @Override
    public void playBucketFillFluid(Level level, BlockPos pos, boolean isLava) {
        level.playSound(null, pos, isLava ? SoundEvents.BUCKET_FILL_LAVA : SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0f, 1.0f);
    }

    @Override
    public void playBucketEmptyFluid(Level level, BlockPos pos, boolean isLava) {
        level.playSound(null, pos, isLava ? SoundEvents.BUCKET_EMPTY_LAVA : SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0f, 1.0f);
    }

    @Override
    public void playItemPickup(Level level, BlockPos pos, float pitch) {
        playItemPickup(level, pos, 0.8F, pitch);
    }

    @Override
    public void playItemPickup(Level level, BlockPos pos, float volume, float pitch) {
        level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, volume, pitch);
    }

    @Override
    public void playLeverClick(Level level, BlockPos pos, float pitch) {
        level.playSound(null, pos, SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.5f, pitch);
    }

    @Override
    public void playStoneButtonClick(Level level, BlockPos pos, boolean on) {
        level.playSound(null, pos, on ? SoundEvents.STONE_BUTTON_CLICK_ON : SoundEvents.STONE_BUTTON_CLICK_OFF, SoundSource.BLOCKS, 0.3F, 1.0F);
    }

    @Override
    public void playStoneButtonClick(Level level, BlockPos pos, boolean on, float volume, float pitch) {
        level.playSound(null, pos, on ? SoundEvents.STONE_BUTTON_CLICK_ON : SoundEvents.STONE_BUTTON_CLICK_OFF, SoundSource.BLOCKS, volume, pitch);
    }

    @Override
    public void playEvokerPrepareSummon(net.minecraft.server.level.ServerLevel level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.EVOKER_PREPARE_SUMMON, SoundSource.NEUTRAL, 1.0F, 1.0F);
    }

    @Override
    public void playDyeUse(Level level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.DYE_USE, SoundSource.BLOCKS, 1.0f, 1.0f);
    }

    @Override
    public void playItemFrameAdd(Level level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 1.0f, 1.0f);
    }

    @Override
    public void playItemFrameRemove(Level level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0f, 1.0f);
    }

    @Override
    public void playItemFrameRotate(Level level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.ITEM_FRAME_ROTATE_ITEM, SoundSource.BLOCKS, 1.0f, 1.0f);
    }

    @Override
    public void playEyeblossomTransition(Level level, BlockPos pos, boolean night) {
        level.playSound(null, pos, night ? SoundEvents.AMETHYST_BLOCK_CHIME : SoundEvents.AZALEA_LEAVES_BREAK, SoundSource.BLOCKS, night ? 1.0F : 0.8F, night ? 1.2F : 0.9F);
    }

    @Override
    public void playBoneDiceSet(Level level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.WOODEN_BUTTON_CLICK_ON, SoundSource.BLOCKS, 0.8F, 1.2F);
        level.playSound(null, pos, SoundEvents.BONE_BLOCK_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    @Override
    public void playBoneDiceRoll(Level level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.WOODEN_BUTTON_CLICK_ON, SoundSource.BLOCKS, 0.8F, 1.2F);
        level.playSound(null, pos, SoundEvents.BONE_BLOCK_HIT, SoundSource.BLOCKS, 1.0F, 1.2F);
    }

    @Override
    public void playShear(Level level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 0.8F, 1.0F);
    }

    @Override
    public void playGlassPlace(Level level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.GLASS_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    @Override
    public void playGrassPlace(Level level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.GRASS_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    @Override
    public void playStonePlace(Level level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.STONE_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    @Override
    public void playGlassBreak(Level level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    @Override
    public void playGrassBreak(Level level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.GRASS_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    @Override
    public void playAnvilUse(Level level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 0.5F, 1.5F);
    }

    @Override public void playButtonClick() { com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.playButtonClick(); }

    @Override public void playNoteBlockBell() { com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.playNoteBlockBell(); }

    @Override public void playNoteBlockDidgeridoo() { com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.playNoteBlockDidgeridoo(); }

    @Override
    public void playVegetationStepSound(Level level, BlockPos pos, Entity entity, SoundType sounds) {
        if (entity instanceof Player player && player.onGround() && level.isClientSide()) {
            float stepInterval = 2.0f;
            int currentStep = (int) (player.moveDist / stepInterval);
            int lastStep = (int) ((player.moveDist - (float) player.getDeltaMovement().horizontalDistance()) / stepInterval);

            if (currentStep != lastStep && player.getDeltaMovement().horizontalDistanceSqr() > 1.0E-4) {
                float volume = 0.25f;
                float pitch = 1.2f;
                if (sounds instanceof com.kingodogo.buildscape.block.CustomSoundType customSounds) {
                    volume = customSounds.getStepVolume();
                    pitch = customSounds.getStepPitch();
                }

                level.playLocalSound(
                        pos.getX() + 0.5,
                        pos.getY() + 0.5,
                        pos.getZ() + 0.5,
                        sounds.getStepSound(),
                        SoundSource.BLOCKS,
                        volume,
                        pitch,
                        false
                );
            }
        }
    }

    @Override
    public boolean hasPermission(Player player, int permissionLevel) {
        if (player instanceof ServerPlayer serverPlayer) {
            net.minecraft.server.permissions.PermissionSet set = serverPlayer.permissions();
            if (set instanceof net.minecraft.server.permissions.LevelBasedPermissionSet lbps) {
                return lbps.level().isEqualOrHigherThan(net.minecraft.server.permissions.PermissionLevel.byId(permissionLevel));
            }
            return permissionLevel <= 0;
        }
        return false;
    }

    @Override
    public boolean hasCommandPermission(net.minecraft.commands.CommandSourceStack source, int level) {
        net.minecraft.server.permissions.PermissionSet set = source.permissions();
        if (set instanceof net.minecraft.server.permissions.LevelBasedPermissionSet lbps) {
            return lbps.level().isEqualOrHigherThan(net.minecraft.server.permissions.PermissionLevel.byId(level));
        }
        return level <= 0;
    }

    @Override
    public net.minecraft.server.MinecraftServer getServer(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            return serverPlayer.level().getServer();
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void updateGameRule(Player player, String ruleName, boolean value) {
        if (!(player instanceof ServerPlayer serverPlayer) || !hasPermission(serverPlayer, 2)) {
            return;
        }
        net.minecraft.server.MinecraftServer server = serverPlayer.level().getServer();
        if (server == null) return;
        net.minecraft.world.level.gamerules.GameRules rules = server.getGameRules();
        if (rules == null) return;

        if (ruleName.equals("fastLeafDecay") && com.kingodogo.buildscape.world.ModGameRules.FAST_LEAF_DECAY instanceof net.minecraft.world.level.gamerules.GameRule<?> rule) {
            rules.set((net.minecraft.world.level.gamerules.GameRule<Boolean>) rule, value, server);
        } else if (ruleName.equals("disableEndermanGriefing") && com.kingodogo.buildscape.world.ModGameRules.DISABLE_ENDERMAN_GRIEFING instanceof net.minecraft.world.level.gamerules.GameRule<?> rule) {
            rules.set((net.minecraft.world.level.gamerules.GameRule<Boolean>) rule, value, server);
        } else if (ruleName.equals("disableCreeperGriefing") && com.kingodogo.buildscape.world.ModGameRules.DISABLE_CREEPER_GRIEFING instanceof net.minecraft.world.level.gamerules.GameRule<?> rule) {
            rules.set((net.minecraft.world.level.gamerules.GameRule<Boolean>) rule, value, server);
        } else if (ruleName.equals("disableGhastGriefing") && com.kingodogo.buildscape.world.ModGameRules.DISABLE_GHAST_GRIEFING instanceof net.minecraft.world.level.gamerules.GameRule<?> rule) {
            rules.set((net.minecraft.world.level.gamerules.GameRule<Boolean>) rule, value, server);
        } else if (ruleName.equals("isCakeStack") && com.kingodogo.buildscape.world.ModGameRules.IS_CAKE_STACK instanceof net.minecraft.world.level.gamerules.GameRule<?> rule) {
            rules.set((net.minecraft.world.level.gamerules.GameRule<Boolean>) rule, value, server);
        } else if (ruleName.equals("isWaterbottleStack") && com.kingodogo.buildscape.world.ModGameRules.IS_WATER_BOTTLE_STACK instanceof net.minecraft.world.level.gamerules.GameRule<?> rule) {
            rules.set((net.minecraft.world.level.gamerules.GameRule<Boolean>) rule, value, server);
        }
    }

    @Override
    public float[] getDyeDiffuseColors(net.minecraft.world.item.DyeColor dyeColor) {
        int color = dyeColor.getTextureDiffuseColor();
        return new float[]{((color >> 16) & 0xFF) / 255.0F, ((color >> 8) & 0xFF) / 255.0F, (color & 0xFF) / 255.0F};
    }

    @Override
    public float getWalkDist(Player player) {
        return player.moveDist;
    }

    @Override
    public float getWalkDistO(Player player) {
        return player.moveDist;
    }

    @Override
    public void registerCommonLifecycleInteractions() {
        registerFlowerPotPlants();
        registerCompostables();
    }

    private void registerFlowerPotPlants() {
        registerPot("closed_eyeblossom", "potted_closed_eyeblossom");
        registerPot("open_eyeblossom", "potted_open_eyeblossom");
        registerPot("golden_dandelion", "potted_golden_dandelion");
        registerPot("cactus_flower", "potted_cactus_flower");
        registerPot("bush", "potted_bush");
        registerPot("red_bush", "potted_red_bush");
        registerPot("firefly_bush", "potted_firefly_bush");
        registerPot("dry_grass", "potted_dry_grass");
        registerPot("frost_rose", "potted_frost_rose");
        registerPot("red_monets", "potted_red_monets");
        registerPot("blue_monets", "potted_blue_monets");
        registerPot("purple_monets", "potted_purple_monets");
        registerPot("light_blue_monets", "potted_light_blue_monets");
        registerPot("pink_monets", "potted_pink_monets");
        registerPot("yellow_monets", "potted_yellow_monets");
        registerPot("poplar_sapling", "potted_poplar_sapling");
        registerPot("pale_oak_sapling", "potted_pale_oak_sapling");
        registerPot("cherry_sapling", "potted_cherry_sapling");
        registerPot("mangrove_propagule", "potted_mangrove_propagule");
        registerPot("wildflowers", "potted_wildflowers");
        registerPot("clover", "potted_clover");
        registerPot("leaf_litter", "potted_leaf_litter");
        registerPot("tall_dry_grass", "potted_tall_dry_grass");
        registerPot("snowy_bush", "potted_snowy_bush");
        registerPot("snowy_short_grass", "potted_snowy_short_grass");
        registerPot("snowy_tall_grass", "potted_snowy_tall_grass");
        registerPot("snowy_fern", "potted_snowy_fern");
        registerPot("snowy_large_fern", "potted_snowy_large_fern");
    }

    private void registerPot(String flowerId, String pottedId) {
        Block flower = getBlock(new CommonId("buildscape", flowerId));
        Block potted = getBlock(new CommonId("buildscape", pottedId));
        if (flower != null && potted != null) {
            try {
                for (java.lang.reflect.Field f : net.minecraft.world.level.block.FlowerPotBlock.class.getDeclaredFields()) {
                    if (java.util.Map.class.isAssignableFrom(f.getType()) && java.lang.reflect.Modifier.isStatic(f.getModifiers())) {
                        f.setAccessible(true);
                        @SuppressWarnings("unchecked")
                        java.util.Map<Block, Block> map = (java.util.Map<Block, Block>) f.get(null);
                        if (map != null) {
                            map.put(flower, potted);
                            break;
                        }
                    }
                }
            } catch (Throwable exception) {
            com.kingodogo.buildscape.BuildscapeCommon.LOGGER.warn("Failed in 26.x registerPot", exception);}
        }
    }

    private void registerCompostables() {
        registerCompost("red_rose_vines", 0.5f);
        registerCompost("black_rose_vines", 0.5f);
        registerCompost("blue_rose_vines", 0.5f);
        registerCompost("white_rose_vines", 0.5f);
        registerCompost("frost_rose", 0.65f);
        registerCompost("red_monets", 0.65f);
        registerCompost("blue_monets", 0.65f);
        registerCompost("purple_monets", 0.65f);
        registerCompost("light_blue_monets", 0.65f);
        registerCompost("pink_monets", 0.65f);
        registerCompost("yellow_monets", 0.65f);
        registerCompost("red_petal", 0.65f);
        registerCompost("blue_petal", 0.65f);
        registerCompost("orange_petal", 0.65f);
        registerCompost("pink_petal", 0.65f);
        registerCompost("purple_petal", 0.65f);
        registerCompost("red_spore_blossom", 0.65f);
        registerCompost("cyan_spore_blossom", 0.65f);
        registerCompost("blue_spore_blossom", 0.65f);
        registerCompost("purple_spore_blossom", 0.65f);
        registerCompost("orange_spore_blossom", 0.65f);
        registerCompost("snowy_short_grass", 0.3f);
        registerCompost("snowy_tall_grass", 0.3f);
        registerCompost("snowy_fern", 0.3f);
        registerCompost("snowy_large_fern", 0.3f);
        registerCompost("snowy_bush", 0.3f);
        registerCompost("mangrove_leaves", 0.3f);
        registerCompost("snowy_leaves", 0.3f);
        registerCompost("snowy_oak_leaves", 0.3f);
        registerCompost("snowy_spruce_leaves", 0.3f);
        registerCompost("snowy_birch_leaves", 0.3f);
        registerCompost("snowy_jungle_leaves", 0.3f);
        registerCompost("snowy_acacia_leaves", 0.3f);
        registerCompost("snowy_dark_oak_leaves", 0.3f);
        registerCompost("snowy_mangrove_leaves", 0.3f);
        registerCompost("snowy_azalea_leaves", 0.3f);
        registerCompost("snowy_flowering_azalea_leaves", 0.3f);
        registerCompost("brown_mushroom_shelves", 0.65f);
        registerCompost("red_mushroom_shelves", 0.65f);
    }

    private void registerCompost(String itemId, float chance) {
        Item item = getItem(new CommonId("buildscape", itemId));
        if (item != null && item != net.minecraft.world.item.Items.AIR) {
            net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(item, chance);
        }
    }

    public void registerExperienceCauldronInteractions(java.util.function.BiConsumer<Item,
            net.minecraft.core.cauldron.CauldronInteraction> registrar) {
        Item xpBucket = getItem(new CommonId("buildscape", "experience_bucket"));
        Block xpCauldron = getBlock(new CommonId("buildscape", "experience_cauldron"));
        if (xpBucket == null || xpBucket == net.minecraft.world.item.Items.AIR
                || xpCauldron == null || xpCauldron == net.minecraft.world.level.block.Blocks.AIR) {
            throw new IllegalStateException("Experience cauldron entries are not registered");
        }
        registrar.accept(xpBucket, (net.minecraft.core.cauldron.CauldronInteraction) (state, level, pos, player, hand, stack) -> {
            if (!level.isClientSide()) {
                player.awardStat(net.minecraft.stats.Stats.FILL_CAULDRON);
                level.setBlockAndUpdate(pos, xpCauldron.defaultBlockState().setValue(net.minecraft.world.level.block.LayeredCauldronBlock.LEVEL, 3));
                level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                if (!player.getAbilities().instabuild) {
                    player.setItemInHand(hand, new ItemStack(net.minecraft.world.item.Items.BUCKET));
                }
            }
            return sidedSuccess(level.isClientSide());
        });
        registrar.accept(net.minecraft.world.item.Items.EXPERIENCE_BOTTLE, (net.minecraft.core.cauldron.CauldronInteraction) (state, level, pos, player, hand, stack) -> {
            if (!level.isClientSide()) {
                player.awardStat(net.minecraft.stats.Stats.FILL_CAULDRON);
                level.setBlockAndUpdate(pos, xpCauldron.defaultBlockState().setValue(net.minecraft.world.level.block.LayeredCauldronBlock.LEVEL, 1));
                level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                    ItemStack returnStack = new ItemStack(net.minecraft.world.item.Items.GLASS_BOTTLE);
                    if (!player.getInventory().add(returnStack)) {
                        player.drop(returnStack, false);
                    }
                }
            }
            return sidedSuccess(level.isClientSide());
        });
    }

    @Override public void renderModelPart(net.minecraft.client.model.geom.ModelPart part, com.mojang.blaze3d.vertex.PoseStack poseStack, com.mojang.blaze3d.vertex.VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) { com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.renderModelPart(part, poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha); }

    @Override public int getRenderDistanceChunks() { return com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.getRenderDistanceChunks(); }

    @Override public void renderLineBox(com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float r, float g, float b, float a) { com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.renderLineBox(poseStack, bufferSource, minX, minY, minZ, maxX, maxY, maxZ, r, g, b, a); }

    @Override public boolean isScreenOpen() { return com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.isScreenOpen(); }

    @Override public net.minecraft.world.phys.Vec3 getCameraPosition(net.minecraft.client.Camera camera) { return com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.getCameraPosition(camera); }

    @Override public void renderFallingIcicleBlock(net.minecraft.world.level.Level level, net.minecraft.world.level.block.state.BlockState blockState, net.minecraft.core.BlockPos blockPos, net.minecraft.core.BlockPos startPos, com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource) { com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.renderFallingIcicleBlock(level, blockState, blockPos, startPos, poseStack, bufferSource); }

    @Override
    public int getLightColor(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos) {
        if (level == null || pos == null) return 0;
        int blockLight = level.getBrightness(net.minecraft.world.level.LightLayer.BLOCK, pos);
        int skyLight = level.getBrightness(net.minecraft.world.level.LightLayer.SKY, pos);
        return (skyLight << 20) | (blockLight << 4);
    }

    @Override
    public boolean isMapItemWithData(net.minecraft.world.item.ItemStack stack, net.minecraft.world.level.Level level) {
        if (stack == null || stack.isEmpty() || level == null) return false;
        return stack.getItem() instanceof net.minecraft.world.item.MapItem && net.minecraft.world.item.MapItem.getSavedData(stack, level) != null;
    }






    @Override public void renderColoredFrame(com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource, int packedLight, Object backTexture, boolean hasMap) { com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.renderColoredFrame(poseStack, bufferSource, packedLight, backTexture, hasMap); }

    @Override public void renderColoredFrameItem(com.kingodogo.buildscape.entity.ColoredItemFrameEntity entity, net.minecraft.world.item.ItemStack itemStack, com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource, int packedLight, boolean isMap, boolean isInvisible) { com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.renderColoredFrameItem(entity, itemStack, poseStack, bufferSource, packedLight, isMap, isInvisible); }

    @Override public void renderStockingQuad(com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource, int packedLight, Object texture, boolean flipped) { com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.renderStockingQuad(poseStack, bufferSource, packedLight, texture, flipped); }

    @Override public void renderBlockModel(net.minecraft.world.level.block.state.BlockState blockState, com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource, int light, int overlay) { com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.renderBlockModel(blockState, poseStack, bufferSource, light, overlay); }

    @Override public void renderBlockModelWithTint(net.minecraft.world.level.block.state.BlockState state, net.minecraft.core.BlockPos pos, net.minecraft.world.level.Level level, com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource, int light, int overlay) { com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.renderBlockModelWithTint(state, pos, level, poseStack, bufferSource, light, overlay); }

    @Override public void renderColoredQuad(com.mojang.blaze3d.vertex.PoseStack poseStack, Object buffer,
                                  float x0, float y0, float z0, float u0, float v0,
                                  float x1, float y1, float z1, float u1, float v1,
                                  float x2, float y2, float z2, float u2, float v2,
                                  float x3, float y3, float z3, float u3, float v3,
                                  float r, float g, float b, float a,
                                  int light, int overlay,
                                  float nx, float ny, float nz) { com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.renderColoredQuad(poseStack, buffer, x0, y0, z0, u0, v0, x1, y1, z1, u1, v1, x2, y2, z2, u2, v2, x3, y3, z3, u3, v3, r, g, b, a, light, overlay, nx, ny, nz); }

    @Override public void renderJarFluid(com.kingodogo.buildscape.block.GlassJarBlockEntity blockEntity, com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource, int light, int overlay) { com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.renderJarFluid(blockEntity, poseStack, bufferSource, light, overlay); }

    @Override public void renderGlassJar(com.kingodogo.buildscape.block.GlassJarBlockEntity blockEntity, float partialTicks, com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource, int combinedLight, int combinedOverlay) { com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.renderGlassJar(blockEntity, partialTicks, poseStack, bufferSource, combinedLight, combinedOverlay); }

    @Override public void renderWobblyBlock(net.minecraft.world.level.block.state.BlockState state, long currentTick, long wobbleStartTick, boolean hasWobble, float partialTicks, com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource, int light, int overlay) { com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.renderWobblyBlock(state, currentTick, wobbleStartTick, hasWobble, partialTicks, poseStack, bufferSource, light, overlay); }

    @Override public void renderCopperChest(com.kingodogo.buildscape.block.CopperChestBlockEntity blockEntity, float partialTicks, com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource, int combinedLight, int combinedOverlay) { com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.renderCopperChest(blockEntity, partialTicks, poseStack, bufferSource, combinedLight, combinedOverlay); }

    @Override public void registerMenuScreens() { com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.registerMenuScreens(); }

    @Override public void registerRenderers() { com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.registerRenderers(); }

    @Override
    public void registerLayerDefinitions(java.util.function.BiConsumer<Object, java.util.function.Supplier<Object>> registrar) {
    }

    @Override
    @SuppressWarnings("unchecked")
    public void registerEntityAttributes(java.util.function.BiConsumer<net.minecraft.world.entity.EntityType<? extends net.minecraft.world.entity.LivingEntity>, net.minecraft.world.entity.ai.attributes.AttributeSupplier> consumer) {
        net.minecraft.world.entity.EntityType<? extends net.minecraft.world.entity.LivingEntity> homemaker = (net.minecraft.world.entity.EntityType<? extends net.minecraft.world.entity.LivingEntity>) getWanderingHomemakerEntityType();
        net.minecraft.world.entity.EntityType<? extends net.minecraft.world.entity.LivingEntity> festive = (net.minecraft.world.entity.EntityType<? extends net.minecraft.world.entity.LivingEntity>) getFestiveWanderingHomemakerEntityType();
        net.minecraft.world.entity.ai.attributes.AttributeSupplier supplier = net.minecraft.world.entity.Mob.createMobAttributes().build();
        if (homemaker != null) consumer.accept(homemaker, supplier);
        if (festive != null) consumer.accept(festive, supplier);
    }

    @Override public void renderEntity(net.minecraft.world.entity.Entity entity, double x, double y, double z, float yaw, float partialTicks, com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource, int packedLight) { com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.renderEntity(entity, x, y, z, yaw, partialTicks, poseStack, bufferSource, packedLight); }

    @Override public com.mojang.blaze3d.vertex.VertexConsumer getTranslucentBuffer(Object bufferSource) { return com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.getTranslucentBuffer(bufferSource); }

    @Override public net.minecraft.client.renderer.texture.TextureAtlasSprite getBlockAtlasSprite(CommonId id) { return com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.getBlockAtlasSprite(id); }

    @Override public boolean isTranslucent(net.minecraft.world.level.block.state.BlockState state) { return com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.isTranslucent(state); }


    @Override public BlockColorSample sampleBlockColor(net.minecraft.world.level.block.state.BlockState state) { return com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.sampleBlockColor(state); }



    @Override public net.minecraft.client.gui.screens.Screen createConfigScreen(net.minecraft.client.gui.screens.Screen parent) { return com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.createConfigScreen(parent); }

    @Override public net.minecraft.client.gui.components.Button createButton(int x, int y, int width, int height, net.minecraft.network.chat.Component message, net.minecraft.client.gui.components.Button.OnPress onPress) { return com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.createButton(x, y, width, height, message, onPress); }

    @Override public net.minecraft.client.gui.components.Button createCustomButton(int x, int y, int width, int height, net.minecraft.network.chat.Component message, net.minecraft.client.gui.components.Button.OnPress onPress, com.kingodogo.buildscape.client.screen.widget.CustomButtonRenderer renderer) { return com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.createCustomButton(x, y, width, height, message, onPress, renderer); }

    @Override public net.minecraft.client.gui.screens.Screen wrapScreen(net.minecraft.network.chat.Component title, net.minecraft.client.gui.screens.Screen parent, com.kingodogo.buildscape.client.screen.IScreenDelegate delegate) { return com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.wrapScreen(title, parent, delegate); }

    @Override public net.minecraft.client.gui.screens.Screen createGuiEditorScreen(net.minecraft.client.gui.screens.Screen parent, String tabName, com.kingodogo.buildscape.client.screen.AbstractConfigTab sourceTab) { return com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.createGuiEditorScreen(parent, tabName, sourceTab); }

    @Override public net.minecraft.client.gui.screens.Screen createInventoryItemSelectorScreen(net.minecraft.client.gui.screens.Screen parent, com.kingodogo.buildscape.client.screen.PillarItemsConfigTab configTab) { return com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.createInventoryItemSelectorScreen(parent, configTab); }

    @Override public void renderGuiItem(Object poseStackOrGraphics, net.minecraft.world.item.ItemStack stack, int x, int y) { com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.renderGuiItem(poseStackOrGraphics, stack, x, y); }

    @Override public void renderGuiItemDecorations(Object poseStackOrGraphics, net.minecraft.client.gui.Font font, net.minecraft.world.item.ItemStack stack, int x, int y) { com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.renderGuiItemDecorations(poseStackOrGraphics, font, stack, x, y); }

    @Override public java.util.List<net.minecraft.network.chat.Component> getTooltipFromItem(net.minecraft.world.item.ItemStack stack) { return com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.getTooltipFromItem(stack); }

    @Override public net.minecraft.client.gui.components.AbstractWidget wrapCustomWidget(int x, int y, int width, int height, net.minecraft.network.chat.Component message, com.kingodogo.buildscape.client.screen.widget.ICustomWidget customWidget) { return com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.wrapCustomWidget(x, y, width, height, message, customWidget); }

    public static class ColoredItemFrameEntityImpl extends ItemFrame implements ColoredItemFrameEntity {

        private static final EntityDataAccessor<String> DATA_COLOR = SynchedEntityData
                .defineId(ColoredItemFrameEntityImpl.class, EntityDataSerializers.STRING);
        private static final EntityDataAccessor<String> DATA_PARTICLE_PATTERN = SynchedEntityData
                .defineId(ColoredItemFrameEntityImpl.class, EntityDataSerializers.STRING);
        private static final EntityDataAccessor<String> DATA_PARTICLE_COLORS = SynchedEntityData
                .defineId(ColoredItemFrameEntityImpl.class, EntityDataSerializers.STRING);

        public ColoredItemFrameEntityImpl(EntityType<? extends ItemFrame> entityType, Level level) {
            super(entityType, level);
        }

        public ColoredItemFrameEntityImpl(EntityType<? extends ItemFrame> entityType, Level level, BlockPos pos, Direction direction) {
            super(entityType, level, pos, direction);
        }

        public ColoredItemFrameEntityImpl(Level level, BlockPos pos, Direction direction, String color) {
            super(EntityTypes.ITEM_FRAME, level, pos, direction);
            this.setColorVariant(color);
            if ("invisible".equals(color)) {
                this.setInvisible(true);
            }
        }

        @Override
        protected void defineSynchedData(SynchedEntityData.Builder builder) {
            super.defineSynchedData(builder);
            builder.define(DATA_COLOR, "white");
            builder.define(DATA_PARTICLE_PATTERN, "none");
            builder.define(DATA_PARTICLE_COLORS, "");
        }

        @Override
        public String getColorVariant() {
            return this.getEntityData().get(DATA_COLOR);
        }

        @Override
        public void setColorVariant(String color) {
            this.getEntityData().set(DATA_COLOR, color != null ? color : "white");
        }

        @Override
        public String getParticlePattern() {
            return this.getEntityData().get(DATA_PARTICLE_PATTERN);
        }

        @Override
        public void setParticlePattern(String pattern) {
            this.getEntityData().set(DATA_PARTICLE_PATTERN, (pattern == null || pattern.isEmpty()) ? "none" : pattern);
        }

        @Override
        public String getParticleColorsRaw() {
            return this.getEntityData().get(DATA_PARTICLE_COLORS);
        }

        @Override
        public void setParticleColorsRaw(String colors) {
            this.getEntityData().set(DATA_PARTICLE_COLORS, colors == null ? "" : colors);
        }

        @Override
        protected ItemStack getFrameItemStack() {
            return getFrameItemForColor(this.getColorVariant());
        }

        @Override
        public ItemStack getPickResult() {
            ItemStack frameItem = this.getFrameItemStack();
            ItemStack displayedItem = this.getItem();

            Services.PLATFORM.updateCustomData(frameItem, tag -> {
                if (!displayedItem.isEmpty()) {
                    CommonId itemId = Services.PLATFORM.getItemId(displayedItem.getItem());
                    if (itemId != null) {
                        tag.putString("ITEM", itemId.toString());
                    }
                }

                CompoundTag persistentData = Services.PLATFORM.getEntityData(this);
                String pattern = Services.PLATFORM.getTagString(persistentData, "BuildScapeParticlePattern", null);
                if (pattern != null) {
                    tag.putString("PATTERN", pattern);
                }

                List<String> colors = Services.PLATFORM.getTagStringList(persistentData, "BuildScapeParticleColors");
                if (!colors.isEmpty()) {
                    Services.PLATFORM.putTagStringList(tag, "COLORS", colors);
                }
            });

            return frameItem;
        }

        @Override
        protected void addAdditionalSaveData(ValueOutput output) {
            super.addAdditionalSaveData(output);
            output.putString("ColorVariant", this.getColorVariant());
            output.putString("BuildScapeParticlePattern", this.getParticlePattern());
            output.putString("BuildScapeParticleColorsRaw", this.getParticleColorsRaw());

            CompoundTag persistentData = Services.PLATFORM.getEntityData(this);
            String frameId = Services.PLATFORM.getTagString(persistentData, "BuildScapeFrameId", null);
            if (frameId != null) {
                output.putString("BuildScapeFrameId", frameId);
            }
        }

        @Override
        protected void readAdditionalSaveData(ValueInput input) {
            super.readAdditionalSaveData(input);
            this.setColorVariant(input.getStringOr("ColorVariant", "white"));
            this.setParticlePattern(input.getStringOr("BuildScapeParticlePattern", input.getStringOr("PATTERN", "none")));
            this.setParticleColorsRaw(input.getStringOr("BuildScapeParticleColorsRaw", ""));

            String frameId = input.getStringOr("BuildScapeFrameId", "");
            if (!frameId.isEmpty()) {
                CompoundTag persistentData = Services.PLATFORM.getEntityData(this);
                if (persistentData != null) {
                    persistentData.putString("BuildScapeFrameId", frameId);
                }
            }
        }

        private ItemStack getFrameItemForColor(String color) {
            String c = (color == null || color.isEmpty()) ? "white" : color.toLowerCase(java.util.Locale.ROOT);
            return new ItemStack(Services.PLATFORM.getItem(new CommonId("buildscape", c + "_item_frame")));
        }
    }

    public static class FallingIcicleEntityImpl extends Entity implements FallingIcicleEntity {

        private BlockState blockState = Blocks.AIR.defaultBlockState();
        private boolean hasLanded = false;
        private int fallTime = 0;
        private BlockPos startPos;

        public FallingIcicleEntityImpl(EntityType<?> entityType, Level level) {
            super(entityType, level);
        }

        public FallingIcicleEntityImpl(Level level, double x, double y, double z, BlockState state) {
            this(Services.PLATFORM.getFallingIcicleEntityType(), level);
            this.blockState = state;
            this.setPos(x, y, z);
            this.setDeltaMovement(Vec3.ZERO);
            this.xo = x;
            this.yo = y;
            this.zo = z;
            this.blocksBuilding = true;
            this.startPos = this.blockPosition();
        }

        @Override
        public BlockPos getStartPos() {
            return this.startPos != null ? this.startPos : this.blockPosition();
        }

        @Override
        public BlockState getBlockState() {
            return this.blockState;
        }

        @Override
        protected void defineSynchedData(SynchedEntityData.Builder builder) {
            // The reference sends the immutable block state in the spawn packet, with no tracked fields.
        }

        @Override
        public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
            return false;
        }

        @Override
        public void tick() {
            if (!this.isNoGravity()) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.04D, 0.0D));
            }

            this.move(MoverType.SELF, this.getDeltaMovement());

            if (!this.level().isClientSide() && fallTime > 2) {
                checkEntityCollision();
            }

            this.setDeltaMovement(this.getDeltaMovement().scale(0.98D));
            fallTime++;

            if (this.onGround() && !hasLanded) {
                hasLanded = true;
                if (!this.level().isClientSide() && !blockState.isAir()) {
                    breakAndDrop();
                }
                this.discard();
                return;
            }

            if (fallTime > 600 || this.getY() < this.level().getMinY() - 64) {
                this.discard();
            }
        }

        private void checkEntityCollision() {
            AABB boundingBox = this.getBoundingBox().inflate(0.25, 0.5, 0.25);
            Vec3 motion = this.getDeltaMovement();
            if (motion.y < 0) {
                boundingBox = boundingBox.expandTowards(0, motion.y, 0);
            }

            List<LivingEntity> entities = this.level().getEntitiesOfClass(LivingEntity.class, boundingBox);
            for (LivingEntity entity : entities) {
                float fallDistance = (float) (this.startPos != null ? this.startPos.getY() - this.getY() : fallTime * 0.04);
                float damage = Math.min(Math.max(2.0f, fallDistance * 2.0f), 40.0f);

                entity.hurt(this.level().damageSources().fallingStalactite(this), damage);
                entity.setDeltaMovement(entity.getDeltaMovement().add(0, -0.3, 0));

                if (!blockState.isAir()) {
                    breakAndDrop();
                }

                this.discard();
                return;
            }
        }

        private void breakAndDrop() {
            ItemStack itemStack = new ItemStack(blockState.getBlock().asItem(), 1);
            ItemEntity itemEntity = new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), itemStack);
            itemEntity.setDefaultPickUpDelay();
            this.level().addFreshEntity(itemEntity);

            this.level().playSound(null, this.blockPosition(), blockState.getSoundType().getBreakSound(), net.minecraft.sounds.SoundSource.BLOCKS, 1.0f, 1.0f);
            this.level().levelEvent(2001, this.blockPosition(), Block.getId(blockState));
        }

        @Override
        protected void readAdditionalSaveData(ValueInput input) {
            this.blockState = input.read("BlockState", BlockState.CODEC).orElse(Blocks.ICE.defaultBlockState());
            if (this.blockState.isAir()) this.blockState = Blocks.ICE.defaultBlockState();
            this.fallTime = input.getIntOr("FallTime", 0);
            this.startPos = input.getInt("StartX")
                    .map(x -> new BlockPos(x, input.getIntOr("StartY", 0), input.getIntOr("StartZ", 0)))
                    .orElse(null);
        }

        @Override
        protected void addAdditionalSaveData(ValueOutput output) {
            output.store("BlockState", BlockState.CODEC, this.blockState);
            output.putInt("FallTime", this.fallTime);
            if (this.startPos != null) {
                output.putInt("StartX", this.startPos.getX());
                output.putInt("StartY", this.startPos.getY());
                output.putInt("StartZ", this.startPos.getZ());
            }
        }

        @Override
        public net.minecraft.network.protocol.Packet<net.minecraft.network.protocol.game.ClientGamePacketListener>
                getAddEntityPacket(net.minecraft.server.level.ServerEntity tracker) {
            return new net.minecraft.network.protocol.game.ClientboundAddEntityPacket(this, tracker, Block.getId(this.blockState));
        }

        @Override
        public void recreateFromPacket(net.minecraft.network.protocol.game.ClientboundAddEntityPacket packet) {
            super.recreateFromPacket(packet);
            this.blockState = Block.stateById(packet.getData());
        }

        @Override
        public boolean isAttackable() {
            // Like the reference, falling icicles are hazards rather than attackable targets.
            return false;
        }
    }

    public static class FestiveStockingEntityImpl extends HangingEntity implements FestiveStockingEntity {

        private static final EntityDataAccessor<ItemStack> DATA_ITEM = SynchedEntityData
                .defineId(FestiveStockingEntityImpl.class, EntityDataSerializers.ITEM_STACK);

        private String colorVariant = "festive";

        public FestiveStockingEntityImpl(EntityType<? extends HangingEntity> entityType, Level level) {
            super(entityType, level);
        }

        public FestiveStockingEntityImpl(EntityType<? extends HangingEntity> entityType, Level level, BlockPos pos) {
            super(entityType, level, pos);
        }

        public FestiveStockingEntityImpl(Level level, BlockPos pos, Direction direction, String color) {
            super(EntityTypes.ITEM_FRAME, level, pos);
            this.colorVariant = color != null ? color : "festive";
            if (direction != null) {
                this.setDirection(direction);
            }
        }

        @Override
        public String getColorVariant() {
            return colorVariant;
        }

        @Override
        public void setColorVariant(String color) {
            this.colorVariant = color != null ? color : "festive";
        }

        @Override
        public void setDirection(Direction direction) {
            if (direction != null && direction.getAxis().isHorizontal()) {
                super.setDirection(direction);
                if (this.pos != null) {
                    this.recalculateBoundingBox();
                }
            }
        }

        @Override
        protected void defineSynchedData(SynchedEntityData.Builder builder) {
            super.defineSynchedData(builder);
            builder.define(DATA_ITEM, ItemStack.EMPTY);
        }

        @Override
        public void setPos(double x, double y, double z) {
            super.setPos(x, y, z);
            this.recalculateBoundingBox();
        }

        @Override
        protected AABB calculateBoundingBox(BlockPos pos, Direction direction) {
            if (pos == null || direction == null) {
                return new AABB(this.getX(), this.getY(), this.getZ(), this.getX(), this.getY(), this.getZ());
            }

            double d0 = (double) pos.getX() + 0.5D;
            double d1 = (double) pos.getY() + 0.5D;
            double d2 = (double) pos.getZ() + 0.5D;
            double d4 = 12 % 32 == 0 ? 0.5D : 0.0D;
            double d5 = 16 % 32 == 0 ? 0.5D : 0.0D;

            d0 -= (double) direction.getStepX() * 0.46875D;
            d1 -= (double) direction.getStepY() * 0.46875D;
            d2 -= (double) direction.getStepZ() * 0.46875D;

            Direction offsetDir;
            if (direction.getAxis() == Direction.Axis.Y) {
                offsetDir = Direction.NORTH;
            } else {
                offsetDir = switch (direction) {
                    case NORTH -> Direction.EAST;
                    case SOUTH -> Direction.WEST;
                    case EAST -> Direction.SOUTH;
                    case WEST -> Direction.NORTH;
                    default -> Direction.NORTH;
                };
            }

            d0 += d4 * (double) offsetDir.getStepX();
            d1 += d5 * (double) offsetDir.getStepY();
            d2 += d4 * (double) offsetDir.getStepZ();

            double d6, d7, d8;
            if (direction.getAxis() == Direction.Axis.Z) {
                d6 = 12.0D;
                d7 = 16.0D;
                d8 = 2.0D;
            } else if (direction.getAxis() == Direction.Axis.X) {
                d6 = 2.0D;
                d7 = 16.0D;
                d8 = 12.0D;
            } else {
                d6 = 12.0D;
                d7 = 2.0D;
                d8 = 16.0D;
            }

            d6 /= 32.0D;
            d7 /= 32.0D;
            d8 /= 32.0D;

            return new AABB(d0 - d6, d1 - d7, d2 - d8, d0 + d6, d1 + d7, d2 + d8);
        }

        public int getWidth() {
            return 12;
        }

        public int getHeight() {
            return 16;
        }

        @Override
        public void dropItem(ServerLevel level, @Nullable Entity entity) {
            this.playSound(SoundEvents.ITEM_FRAME_REMOVE_ITEM, 1.0F, 1.0F);
            ItemStack storedItem = this.getItem();
            boolean hasSilkTouch = false;

            if (entity instanceof Player player) {
                ItemStack tool = player.getMainHandItem();
                if (Services.PLATFORM.hasSilkTouch(tool)) {
                    hasSilkTouch = true;
                }
            }

            ItemStack stockingItem = getStockingItemForColor(this.colorVariant);

            if (hasSilkTouch && !storedItem.isEmpty()) {
                Services.PLATFORM.updateCustomData(stockingItem, tag -> {
                    CompoundTag storedTag = Services.PLATFORM.saveSingleItemStack(storedItem);
                    tag.put("StoredItem", storedTag);
                });
            }

            this.spawnAtLocation(level, stockingItem);

            if (!hasSilkTouch && !storedItem.isEmpty()) {
                this.spawnAtLocation(level, storedItem);
            }
        }

        @Override
        public void playPlacementSound() {
            this.playSound(SoundEvents.ITEM_FRAME_ADD_ITEM, 1.0F, 1.0F);
        }

        @Override
        public InteractionResult interact(Player player, InteractionHand hand, Vec3 location) {
            ItemStack heldItem = player.getItemInHand(hand);
            ItemStack storedItem = this.getItem();

            if (heldItem.isEmpty() && player.isShiftKeyDown() && !storedItem.isEmpty()) {
                if (!this.level().isClientSide() && this.level() instanceof ServerLevel serverLevel) {
                    ItemStack toGive = storedItem.copy();
                    this.setItem(ItemStack.EMPTY);

                    if (!player.getInventory().add(toGive)) {
                        ItemEntity itemEntity = new ItemEntity(serverLevel, this.getX(), this.getY(), this.getZ(), toGive);
                        itemEntity.setDefaultPickUpDelay();
                        serverLevel.addFreshEntity(itemEntity);
                    }

                    this.playSound(SoundEvents.ITEM_FRAME_REMOVE_ITEM, 1.0F, 1.0F);
                }
                return InteractionResult.SUCCESS;
            }

            if (!heldItem.isEmpty()) {
                if (storedItem.isEmpty()) {
                    if (!this.level().isClientSide()) {
                        ItemStack toStore = heldItem.copy();
                        int maxStack = toStore.getMaxStackSize();
                        int toTake = player.isShiftKeyDown() ? Math.min(heldItem.getCount(), maxStack) : 1;
                        toStore.setCount(toTake);
                        this.setItem(toStore, true);
                        heldItem.shrink(toTake);
                        if (heldItem.isEmpty()) {
                            player.setItemInHand(hand, ItemStack.EMPTY);
                        }
                    }
                    return InteractionResult.SUCCESS;
                } else if (Services.PLATFORM.isSameItemSameComponents(storedItem, heldItem)
                        && storedItem.getCount() < storedItem.getMaxStackSize()) {
                    if (!this.level().isClientSide()) {
                        int maxStack = storedItem.getMaxStackSize();
                        int spaceAvailable = maxStack - storedItem.getCount();
                        int toAdd = player.isShiftKeyDown() ? Math.min(heldItem.getCount(), spaceAvailable) : 1;
                        int canAdd = Math.min(toAdd, spaceAvailable);
                        storedItem.grow(canAdd);
                        this.setItem(storedItem, true);
                        heldItem.shrink(canAdd);
                        if (heldItem.isEmpty()) {
                            player.setItemInHand(hand, ItemStack.EMPTY);
                        }
                    }
                    return InteractionResult.SUCCESS;
                }
            }

            return InteractionResult.PASS;
        }

        @Override
        public ItemStack getItem() {
            return this.getEntityData().get(DATA_ITEM);
        }

        @Override
        public void setItem(ItemStack stack) {
            this.setItem(stack, true);
        }

        public void setItem(ItemStack stack, boolean update) {
            this.getEntityData().set(DATA_ITEM, stack.isEmpty() ? ItemStack.EMPTY : stack);
            if (!stack.isEmpty() && update) {
                this.playSound(SoundEvents.ITEM_FRAME_ADD_ITEM, 1.0F, 1.0F);
            }
            if (update && this.pos != null) {
                this.level().updateNeighbourForOutputSignal(this.pos, Blocks.AIR);
            }
        }

        @Override
        protected void addAdditionalSaveData(ValueOutput output) {
            super.addAdditionalSaveData(output);
            ItemStack storedItem = this.getItem();
            if (!storedItem.isEmpty()) {
                output.store("Item", ItemStack.CODEC, storedItem);
            }
            output.putString("ColorVariant", colorVariant);
        }

        @Override
        protected void readAdditionalSaveData(ValueInput input) {
            super.readAdditionalSaveData(input);
            ItemStack item = input.read("Item", ItemStack.CODEC).orElse(ItemStack.EMPTY);
            this.setItem(item, false);
            this.colorVariant = input.getStringOr("ColorVariant", "festive");
        }

        @Override
        public boolean survives() {
            if (this.hasLevelCollision(this.getPopBox())) {
                return false;
            } else {
                BlockPos blockpos = this.pos.relative(this.getDirection().getOpposite());
                if (!this.level().getBlockState(blockpos).isSolid()) {
                    return false;
                } else {
                    return this.canCoexist(true);
                }
            }
        }

        @Override
        public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
            if (this.isInvulnerableToBase(source)) {
                return false;
            } else if (!source.is(net.minecraft.tags.DamageTypeTags.IS_EXPLOSION) && !this.getItem().isEmpty()) {
                this.dropItem(level, source.getEntity());
                this.playSound(SoundEvents.ITEM_FRAME_REMOVE_ITEM, 1.0F, 1.0F);
                this.setItem(ItemStack.EMPTY);
                return true;
            } else {
                return super.hurtServer(level, source, amount);
            }
        }

        @Override
        public ItemStack getPickResult() {
            ItemStack result = getStockingItemForColor(this.colorVariant);
            ItemStack storedItem = this.getItem();
            if (!storedItem.isEmpty()) {
                Services.PLATFORM.updateCustomData(result, tag -> {
                    CompoundTag storedTag = Services.PLATFORM.saveSingleItemStack(storedItem);
                    tag.put("StoredItem", storedTag);
                });
            }
            return result;
        }

        private ItemStack getStockingItemForColor(String color) {
            String c = (color == null || color.isEmpty()) ? "festive" : color.toLowerCase(java.util.Locale.ROOT);
            String name = "festive".equals(c) ? "festive_stocking" : c + "_festive_stocking";
            return new ItemStack(Services.PLATFORM.getItem(new CommonId("buildscape", name)));
        }
    }

    public static class FestiveWanderingHomemakerEntityImpl extends WanderingTrader implements FestiveWanderingHomemakerEntity {
        private int despawnDelay = 48000;

        public FestiveWanderingHomemakerEntityImpl(EntityType<? extends WanderingTrader> type, Level level) {
            super(type, level);
        }

        public FestiveWanderingHomemakerEntityImpl(Level level) {
            this(EntityTypes.WANDERING_TRADER, level);
        }

        @Override
        public void aiStep() {
            super.aiStep();
            if (!this.level().isClientSide()) {
                if (this.getTradingPlayer() == null) {
                    if (--this.despawnDelay <= 0) {
                        this.discard();
                    }
                }
            }
        }

        @Override
        protected void addAdditionalSaveData(ValueOutput output) {
            super.addAdditionalSaveData(output);
            output.putInt("DespawnDelay", this.despawnDelay);
        }

        @Override
        protected void readAdditionalSaveData(ValueInput input) {
            super.readAdditionalSaveData(input);
            this.despawnDelay = input.getIntOr("DespawnDelay", 48000);
        }

        @Override
        protected void updateTrades(ServerLevel serverLevel) {
            MerchantOffers offers = this.getOffers();
            offers.clear();

            java.util.Random rand = new java.util.Random(this.getRandom().nextLong());
            List<MerchantOffer> list = WanderingHomemakerTrades.getFestiveTrades(rand);
            Collections.shuffle(list, rand);
            for (int j = 0; j < Math.min(6, list.size()); j++) {
                offers.add(list.get(j));
            }

            LocalDate today = LocalDate.now();
            int month = today.getMonthValue();
            int day = today.getDayOfMonth();
            int rareChance = (month == 12 && day == 25) ? 15 : 250;
            if (this.getRandom().nextInt(rareChance) == 0) {
                offers.add(Services.PLATFORM.createMerchantOffer(
                        new ItemStack(Items.DIAMOND, 5),
                        new ItemStack(Services.PLATFORM.getItem(new CommonId("buildscape", "frost_rose")), 1),
                        5, 1, 0.05f));
            }
        }
    }

    public static class MangroveBoatEntityImpl extends Boat implements MangroveBoatEntity {

        public MangroveBoatEntityImpl(EntityType<? extends Boat> entityType, Level level) {
            super(entityType, level, () -> Services.PLATFORM.getItem(new CommonId("buildscape", "mangrove_boat")));
        }

        public MangroveBoatEntityImpl(Level level, double x, double y, double z) {
            this(EntityTypes.MANGROVE_BOAT, level);
            this.setPos(x, y, z);
            this.xo = x;
            this.yo = y;
            this.zo = z;
        }
    }

    public static class PoplarBoatEntityImpl extends Boat implements PoplarBoatEntity {

        public PoplarBoatEntityImpl(EntityType<? extends Boat> entityType, Level level) {
            super(entityType, level, () -> Services.PLATFORM.getItem(new CommonId("buildscape", "poplar_boat")));
        }

        public PoplarBoatEntityImpl(Level level, double x, double y, double z) {
            this(EntityTypes.OAK_BOAT, level);
            this.setPos(x, y, z);
            this.xo = x;
            this.yo = y;
            this.zo = z;
        }
    }

    public static class SeatEntityImpl extends Entity implements SeatEntity {

        public SeatEntityImpl(EntityType<?> type, Level level) {
            super(type, level);
            this.noPhysics = true;
        }

        public SeatEntityImpl(Level level, double x, double y, double z) {
            this(EntityTypes.MARKER, level);
            this.setPos(x, y, z);
        }

        @Override
        protected void defineSynchedData(SynchedEntityData.Builder builder) {
            // The reference seat has no custom synched data; it only carries a passenger.
        }

        @Override
        public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
            return false;
        }

        @Override
        protected void readAdditionalSaveData(ValueInput input) {
            // The reference seat has no custom persistent state beyond Entity's position/passengers.
        }

        @Override
        protected void addAdditionalSaveData(ValueOutput output) {
            // The reference seat has no custom persistent state beyond Entity's position/passengers.
        }

        @Override
        public void tick() {
            super.tick();
            if (!this.level().isClientSide()) {
                if (this.getPassengers().isEmpty()) {
                    this.discard();
                }
            }
        }
    }

    public static class WanderingHomemakerEntityImpl extends WanderingTrader implements WanderingHomemakerEntity {
        private int despawnDelay = 48000;

        public WanderingHomemakerEntityImpl(EntityType<? extends WanderingTrader> type, Level level) {
            super(type, level);
        }

        public WanderingHomemakerEntityImpl(Level level) {
            this(EntityTypes.WANDERING_TRADER, level);
        }

        @Override
        public void aiStep() {
            super.aiStep();
            if (!this.level().isClientSide()) {
                if (this.getTradingPlayer() == null) {
                    if (--this.despawnDelay <= 0) {
                        this.discard();
                    }
                }
            }
        }

        @Override
        protected void addAdditionalSaveData(ValueOutput output) {
            super.addAdditionalSaveData(output);
            output.putInt("DespawnDelay", this.despawnDelay);
        }

        @Override
        protected void readAdditionalSaveData(ValueInput input) {
            super.readAdditionalSaveData(input);
            this.despawnDelay = input.getIntOr("DespawnDelay", 48000);
        }

        @Override
        protected void updateTrades(ServerLevel serverLevel) {
            MerchantOffers offers = this.getOffers();
            offers.clear();

            List<MerchantOffer> list = WanderingHomemakerTrades.getStandardTrades();
            java.util.Random rand = new java.util.Random(this.getRandom().nextLong());
            Collections.shuffle(list, rand);
            for (int j = 0; j < Math.min(5, list.size()); j++) {
                offers.add(list.get(j));
            }

            if (this.getRandom().nextInt(5000) == 0) {
                offers.add(Services.PLATFORM.createMerchantOffer(
                        new ItemStack(Items.DIAMOND, 12),
                        new ItemStack(Services.PLATFORM.getItem(new CommonId("buildscape", "ancient_ashen_scroll")), 1),
                        1, 1, 0.0f));
            }
        }
    }

    @Override public Entity createArmorStandEntity(Level level, double x, double y, double z) {
        return com.kingodogo.buildscape.adapter.v26x.client.ClientPillarEntities.createArmorStand(level, x, y, z);
    }
    @Override public void setupArmorStand(Entity stand) {
        com.kingodogo.buildscape.adapter.v26x.client.ClientPillarEntities.setupArmorStand(stand);
    }
    @Override public void updateArmorStand(Entity stand, ItemStack stack, net.minecraft.world.entity.EquipmentSlot slot, boolean standItem) {
        com.kingodogo.buildscape.adapter.v26x.client.ClientPillarEntities.updateArmorStand(stand, stack, slot, standItem);
    }
    @Override public boolean isArmor(ItemStack stack) {
        return com.kingodogo.buildscape.adapter.v26x.client.ClientPillarEntities.isArmor(stack);
    }
    @Override public boolean isGui3dModel(Object model) {
        return com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.isGui3dModel(model);
    }
    @Override public Entity createMobPillarEntity(ItemStack egg, Level level, BlockPos pos, Object state) {
        return com.kingodogo.buildscape.adapter.v26x.client.ClientPillarEntities.createMob(egg, level, pos, state);
    }
    @Override public void applyMobState(Entity entity, Object state) {
        com.kingodogo.buildscape.adapter.v26x.client.ClientPillarEntities.applyMobState(entity, state);
    }
}
