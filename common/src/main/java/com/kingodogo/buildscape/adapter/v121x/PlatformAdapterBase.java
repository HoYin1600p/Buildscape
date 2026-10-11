package com.kingodogo.buildscape.adapter.v121x;
import com.kingodogo.buildscape.entity.ColoredItemFrameEntity;
import com.kingodogo.buildscape.entity.FallingIcicleEntity;
import com.kingodogo.buildscape.entity.FestiveStockingEntity;
import com.kingodogo.buildscape.entity.FestiveWanderingHomemakerEntity;
import com.kingodogo.buildscape.entity.PoplarBoatEntity;
import com.kingodogo.buildscape.entity.SeatEntity;
import com.kingodogo.buildscape.entity.WanderingHomemakerEntity;
import com.kingodogo.buildscape.entity.WanderingHomemakerTrades;
import com.kingodogo.buildscape.platform.Services;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.AABB;

import com.kingodogo.buildscape.platform.BlockEntityFactory;
import com.kingodogo.buildscape.platform.IPlatformAdapter;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import net.minecraft.world.ContainerHelper;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import com.kingodogo.buildscape.adapter.v121x.client.ClientAccess;
import com.kingodogo.buildscape.adapter.v121x.client.RenderFactory;

public abstract class PlatformAdapterBase implements IPlatformAdapter {
    @Override
    public Map<String, BlockEntityType<?>> createClusterBlockEntityTypes(Map<String, Block> blocks) {
        return RestoredContentTypes.createBlockEntities(blocks);
    }

    @Override
    public Item createRegisteredItem(com.kingodogo.buildscape.item.ItemDefinition definition, Block block) {
        String id = definition.getId();
        var properties = com.kingodogo.buildscape.item.ItemFactory.createProperties(id, definition.getProperties());
        if (id.equals("copper_torch")) {
            return new net.minecraft.world.item.StandingAndWallBlockItem(block,
                    getBlock(new CommonId("buildscape", "copper_wall_torch")), properties, Direction.DOWN);
        }
        if (block instanceof net.minecraft.world.level.block.DoorBlock) {
            return new net.minecraft.world.item.DoubleHighBlockItem(block, properties);
        }
        return IPlatformAdapter.super.createRegisteredItem(definition, block);
    }
    // Cache keys stay opaque here; only client helpers interpret baked models.
    private final Map<Object, AABB> v121xModelBoundsCache = new java.util.WeakHashMap<>();
    private final Map<String, double[]> v121xSpriteCache = new java.util.concurrent.ConcurrentHashMap<>();
    // Loader implementations construct these types through access-widened vanilla APIs.
    public abstract <T extends net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider>
            net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType<T> createBlockStateProviderType(com.mojang.serialization.MapCodec<T> codec);
    public abstract <T extends net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator>
            net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType<T> createTreeDecoratorType(com.mojang.serialization.MapCodec<T> codec);
    public abstract <T extends net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer>
            net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType<T> createFoliagePlacerType(com.mojang.serialization.MapCodec<T> codec);
    public abstract <T extends net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer>
            net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType<T> createTrunkPlacerType(com.mojang.serialization.MapCodec<T> codec);
    /** Loader access to the original sprite image; the returned image remains owned by Minecraft. */
    protected abstract com.mojang.blaze3d.platform.NativeImage getSpriteImage(net.minecraft.client.renderer.texture.SpriteContents contents);

    @Override public boolean isReplaceable(net.minecraft.world.level.block.state.BlockState state) { return state.canBeReplaced(); }
    @Override public boolean isGlassBlock(net.minecraft.world.level.block.Block block) { return block == net.minecraft.world.level.block.Blocks.GLASS || block instanceof net.minecraft.world.level.block.StainedGlassBlock || block instanceof net.minecraft.world.level.block.TintedGlassBlock; }
    @Override
    public net.minecraft.world.level.block.RenderShape getEntityBlockRenderShape() { return net.minecraft.world.level.block.RenderShape.ENTITYBLOCK_ANIMATED; }
    @Override
    public int getClientParticleSetting() {
        return ClientAccess.getClientParticleSetting();
    }

    @Override
    public Player getClientPlayer() {
        return ClientAccess.getClientPlayer();
    }

    @Override
    public boolean hasShiftDown() {
        return ClientAccess.hasShiftDown();
    }

    @Override
    public boolean hasControlDown() {
        return ClientAccess.hasControlDown();
    }

    @Override
    public boolean widgetMouseClicked(net.minecraft.client.gui.components.AbstractWidget widget, double mouseX, double mouseY, int button) {
        return ClientAccess.widgetMouseClicked(widget, mouseX, mouseY, button);
    }

    @Override
    public boolean widgetKeyPressed(net.minecraft.client.gui.components.AbstractWidget widget, int keyCode, int scanCode, int modifiers) {
        return ClientAccess.widgetKeyPressed(widget, keyCode, scanCode, modifiers);
    }

    @Override
    public boolean widgetCharTyped(net.minecraft.client.gui.components.AbstractWidget widget, char codePoint, int modifiers) {
        return ClientAccess.widgetCharTyped(widget, codePoint, modifiers);
    }

    @Override
    public void setEditBoxFilter(net.minecraft.client.gui.components.EditBox editBox, java.util.function.Predicate<String> filter) {
        ClientAccess.setEditBoxFilter(editBox, filter);
    }

    @Override
    public void enableScissor(Object graphics, int x, int y, int width, int height) {
        ClientAccess.enableScissor(graphics, x, y, width, height);
    }

    @Override
    public void disableScissor(Object graphics) {
        ClientAccess.disableScissor(graphics);
    }

    @Override
    public CommonId registerDynamicTexture(String name, net.minecraft.client.renderer.texture.DynamicTexture texture) {
        return ClientAccess.registerDynamicTexture(name, texture);
    }

    @Override
    public net.minecraft.client.renderer.texture.DynamicTexture createDynamicTexture(String name, int width, int height, boolean clear) {
        return ClientAccess.createDynamicTexture(name, width, height, clear);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean getGameRuleBoolean(net.minecraft.world.level.Level level, Object ruleKey, boolean fallback) {
        if (level != null && ruleKey instanceof net.minecraft.world.level.GameRules.Key<?> key) {
            return level.getGameRules().getBoolean((net.minecraft.world.level.GameRules.Key<net.minecraft.world.level.GameRules.BooleanValue>) key);
        }
        return fallback;
    }

    @Override public Iterable<net.minecraft.world.item.ItemStack> getInventoryItems(net.minecraft.world.entity.player.Inventory inventory) { return inventory.items; }
    @Override public boolean hasPlayerPermissions(net.minecraft.world.entity.player.Player player, int level) { return player != null && player.hasPermissions(level); }
    @Override
    public void openScreen(net.minecraft.client.gui.screens.Screen screen) {
        ClientAccess.openScreen(screen);
    }
    @Override public void openUri(java.net.URI uri) { net.minecraft.Util.getPlatform().openUri(uri); }
    @Override public void setNativeImagePixel(com.mojang.blaze3d.platform.NativeImage image, int x, int y, int abgr) { image.setPixelRGBA(x, y, abgr); }
    @Override
    public void beginGuiOverlayRender() {
        ClientAccess.beginGuiOverlayRender();
    }
    @Override
    public void endGuiOverlayRender() {
        ClientAccess.endGuiOverlayRender();
    }
    @Override
    public void resetShaderColor() {
        ClientAccess.resetShaderColor();
    }
    @Override
    public void renderPillarMarkers(com.mojang.blaze3d.vertex.PoseStack poseStack, net.minecraft.client.Camera camera) {
    }
    @Override public float getWalkDistance(net.minecraft.world.entity.player.Player player) { return player.walkDist; }
    @Override public boolean isWalkAnimationMoving(net.minecraft.world.entity.player.Player player) { return player.walkDist > player.walkDistO; }
    @Override
    public net.minecraft.network.chat.Component parseComponentJson(String json) {
        return ClientAccess.parseComponentJson(json);
    }
    @Override
    public net.minecraft.client.KeyMapping createKeyMapping(String translationKey, int keyCode, String categoryTranslationKey) {
        return ClientAccess.createKeyMapping(translationKey, keyCode, categoryTranslationKey);
    }
    @Override
    public boolean hasCurrentUser() {
        return ClientAccess.hasCurrentUser();
    }

    @Override
    public String getCurrentUserUuid() {
        return ClientAccess.getCurrentUserUuid();
    }

    @Override
    public Block getBlock(CommonId id) {
        if (id == null) return null;
        return getClusterContent(BuiltInRegistries.BLOCK, id);
    }

    @Override
    public CommonId getBlockId(Block block) {
        if (block == null) return null;
        ResourceLocation rl = BuiltInRegistries.BLOCK.getKey(block);
        return rl != null ? new CommonId(rl.getNamespace(), rl.getPath()) : null;
    }

    @Override
    public Item getItem(CommonId id) {
        if (id == null) return null;
        return getClusterContent(BuiltInRegistries.ITEM, id);
    }

    private static <T> T getClusterContent(Registry<T> registry, CommonId id) {
        ResourceLocation requested = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), id.getPath());
        if (registry.containsKey(requested)) return registry.get(requested);
        for (var alias : com.kingodogo.buildscape.registry.VanillaReplacementAliases.forCluster("v121x")) {
            if (alias.source().equals(id.toString())) {
                ResourceLocation replacement = ResourceLocation.parse(alias.target());
                if (registry.containsKey(replacement)) return registry.get(replacement);
            }
        }
        String counterpart = switch (id.getNamespace()) {
            case "minecraft" -> "buildscape";
            case "buildscape" -> "minecraft";
            default -> null;
        };
        if (counterpart != null) {
            ResourceLocation restored = ResourceLocation.fromNamespaceAndPath(counterpart, id.getPath());
            if (registry.containsKey(restored)) return registry.get(restored);
        }
        return registry.get(requested);
    }

    @Override
    public CommonId getItemId(Item item) {
        if (item == null) return null;
        ResourceLocation rl = BuiltInRegistries.ITEM.getKey(item);
        return rl != null ? new CommonId(rl.getNamespace(), rl.getPath()) : null;
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
        ResourceLocation rl = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), id.getPath());
        return BuiltInRegistries.FLUID.get(rl);
    }

    @Override
    public CommonId getFluidId(Fluid fluid) {
        if (fluid == null) return null;
        ResourceLocation rl = BuiltInRegistries.FLUID.getKey(fluid);
        return rl != null ? new CommonId(rl.getNamespace(), rl.getPath()) : null;
    }

    @Override
    public abstract Fluid getBucketFluid(Item item);

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
    public Entity createBoatEntity(Level level, double x, double y, double z) {
        return new PoplarBoatEntityImpl(level, x, y, z);
    }

    @Override
    public Entity createColoredItemFrameEntity(Level level, net.minecraft.core.BlockPos pos, net.minecraft.core.Direction direction, String color) {
        return new ColoredItemFrameEntityImpl(level, pos, direction, color);
    }

    @Override
    public Entity createFestiveStockingEntity(Level level, net.minecraft.core.BlockPos pos, net.minecraft.core.Direction direction, String color) {
        return new FestiveStockingEntityImpl(level, pos, direction, color);
    }

    private EntityType<?> fallingIcicleType;
    private EntityType<?> festiveStockingType;
    private EntityType<?> coloredItemFrameType;
    private EntityType<?> seatType;
    private EntityType<?> poplarBoatType;
    private EntityType<?> wanderingHomemakerType;
    private EntityType<?> festiveWanderingHomemakerType;

    @Override
    public EntityType<?> getFallingIcicleEntityType() {
        if (fallingIcicleType == null) {
            fallingIcicleType = EntityType.Builder.<FallingIcicleEntityImpl>of(FallingIcicleEntityImpl::new, MobCategory.MISC)
                    .sized(0.98F, 0.98F).clientTrackingRange(10).updateInterval(20).build("falling_icicle");
        }
        return fallingIcicleType;
    }

    @Override
    public EntityType<?> getFestiveStockingEntityType() {
        if (festiveStockingType == null) {
            festiveStockingType = EntityType.Builder.<FestiveStockingEntityImpl>of(FestiveStockingEntityImpl::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F).clientTrackingRange(10).updateInterval(20).build("festive_stocking");
        }
        return festiveStockingType;
    }

@Override
    public EntityType<?> getColoredItemFrameEntityType() {
        if (coloredItemFrameType == null) {
            coloredItemFrameType = EntityType.Builder.<ColoredItemFrameEntityImpl>of(ColoredItemFrameEntityImpl::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F).clientTrackingRange(10).updateInterval(20).build("colored_item_frame");
        }
        return coloredItemFrameType;
    }

    @Override
    public EntityType<?> getSeatEntityType() {
        if (seatType == null) {
            seatType = EntityType.Builder.<SeatEntityImpl>of(SeatEntityImpl::new, MobCategory.MISC)
                    .sized(0.1F, 0.1F).clientTrackingRange(10).updateInterval(20).build("seat");
        }
        return seatType;
    }

    @Override
    public EntityType<?> getPoplarBoatEntityType() {
        if (poplarBoatType == null) {
            poplarBoatType = EntityType.Builder.<PoplarBoatEntityImpl>of(PoplarBoatEntityImpl::new, MobCategory.MISC)
                    .sized(1.375F, 0.5625F).clientTrackingRange(10).updateInterval(3).build("poplar_boat");
        }
        return poplarBoatType;
    }

    @Override
    public EntityType<?> getWanderingHomemakerEntityType() {
        if (wanderingHomemakerType == null) {
            wanderingHomemakerType = EntityType.Builder.<WanderingHomemakerEntityImpl>of(WanderingHomemakerEntityImpl::new, MobCategory.CREATURE)
                    .sized(0.6F, 1.95F).clientTrackingRange(10).updateInterval(3).build("wandering_homemaker");
        }
        return wanderingHomemakerType;
    }

    @Override
    public EntityType<?> getFestiveWanderingHomemakerEntityType() {
        if (festiveWanderingHomemakerType == null) {
            festiveWanderingHomemakerType = EntityType.Builder.<FestiveWanderingHomemakerEntityImpl>of(FestiveWanderingHomemakerEntityImpl::new, MobCategory.CREATURE)
                    .sized(0.6F, 1.95F).clientTrackingRange(10).updateInterval(3).build("festive_wandering_homemaker");
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

    public static <V, T extends V> T safeRegister(Registry<V> registry, ResourceLocation rl, T value) {
        Services.PLATFORM.wrapRegistryAction(() -> Services.PLATFORM.register(registry,
                new CommonId(rl.getNamespace(), rl.getPath()), value));
        return value;
    }

    @Override
    public abstract void wrapRegistryAction(Runnable action);

    @Override
    public abstract <V> void register(Registry<V> registry, CommonId id, V value);

    @Override
    public net.minecraft.stats.Stat<?> registerCustomStat(String registryName, CommonId statId) {
        ResourceLocation value = ResourceLocation.fromNamespaceAndPath(statId.getNamespace(), statId.getPath());
        safeRegister(BuiltInRegistries.CUSTOM_STAT, ResourceLocation.withDefaultNamespace(registryName), value);
        return net.minecraft.stats.Stats.CUSTOM.get(value, net.minecraft.stats.StatFormatter.DEFAULT);
    }

    @Override
    public void registerParticleType(CommonId id, ParticleType<?> type) {
        ResourceLocation rl = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), id.getPath());
        safeRegister(BuiltInRegistries.PARTICLE_TYPE, rl, type);
    }

    @Override
    public abstract void registerParticleProviders();

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
        ClientAccess.rotateZ(poseStack, degrees);
    }

    @Override
    public void fill(Object poseStackOrGraphics, int minX, int minY, int maxX, int maxY, int color) {
        ClientAccess.fill(poseStackOrGraphics, minX, minY, maxX, maxY, color);
    }

    @Override
    public void drawShadow(Object poseStackOrGraphics, net.minecraft.client.gui.Font font, String text, int x, int y, int color) {
        ClientAccess.drawShadow(poseStackOrGraphics, font, text, x, y, color);
    }

    @Override
    public void draw(Object poseStackOrGraphics, net.minecraft.client.gui.Font font, String text, float x, float y, int color) {
        ClientAccess.draw(poseStackOrGraphics, font, text, x, y, color);
    }

    @Override
    public void drawCenteredString(Object poseStackOrGraphics, net.minecraft.client.gui.Font font, net.minecraft.network.chat.Component component, int x, int y, int color) {
        ClientAccess.drawCenteredString(poseStackOrGraphics, font, component, x, y, color);
    }

    @Override
    public void drawCenteredString(Object poseStackOrGraphics, net.minecraft.client.gui.Font font, String text, int x, int y, int color) {
        ClientAccess.drawCenteredString(poseStackOrGraphics, font, text, x, y, color);
    }

    @Override
    public void bindTexture(CommonId id) {
        ClientAccess.bindTexture(id);
    }

    @Override
    public void blit(Object poseStackOrGraphics, CommonId texture, int x, int y, float u, float v, int width, int height, int sheetW, int sheetH) {
        ClientAccess.blit(poseStackOrGraphics, texture, x, y, u, v, width, height, sheetW, sheetH);
    }

    @Override
    public void blit(Object poseStackOrGraphics, CommonId texture, int x, int y, int width, int height, float u, float v, int uWidth, int vHeight, int sheetW, int sheetH) {
        ClientAccess.blit(poseStackOrGraphics, texture, x, y, width, height, u, v, uWidth, vHeight, sheetW, sheetH);
    }

    @Override
    public void renderComponentTooltip(Object poseStackOrGraphics, net.minecraft.client.gui.Font font, java.util.List<net.minecraft.network.chat.Component> components, int mouseX, int mouseY) {
        ClientAccess.renderComponentTooltip(poseStackOrGraphics, font, components, mouseX, mouseY);
    }

    @Override
    public com.mojang.blaze3d.vertex.PoseStack toPoseStack(Object poseStackOrGraphics) {
        return com.kingodogo.buildscape.adapter.v121x.client.GuiProvider.toPoseStack(poseStackOrGraphics);
    }

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
                        trunkProvider, trunkPlacer, foliageProvider, foliagePlacer, featureSize);
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
        net.minecraft.util.random.SimpleWeightedRandomList.Builder<BlockState> builder = net.minecraft.util.random.SimpleWeightedRandomList.builder();
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

    @Override public void pushGuiPose(Object context) { com.mojang.blaze3d.vertex.PoseStack pose = toPoseStack(context); if (pose != null) pose.pushPose(); }
    @Override public void popGuiPose(Object context) { com.mojang.blaze3d.vertex.PoseStack pose = toPoseStack(context); if (pose != null) pose.popPose(); }
    @Override public void translateGuiPose(Object context, float x, float y, float z) { com.mojang.blaze3d.vertex.PoseStack pose = toPoseStack(context); if (pose != null) pose.translate(x, y, z); }
    @Override public void scaleGuiPose(Object context, float x, float y) { com.mojang.blaze3d.vertex.PoseStack pose = toPoseStack(context); if (pose != null) pose.scale(x, y, 1.0f); }
    @Override
    public void renderClientOverlay(Object context, int width, int height) {
        ClientAccess.renderClientOverlay(context, width, height);
    }

    @Override
    public void renderWidget(Object poseStackOrGraphics, net.minecraft.client.gui.components.AbstractWidget widget, int mouseX, int mouseY, float partialTick) {
        ClientAccess.renderWidget(poseStackOrGraphics, widget, mouseX, mouseY, partialTick);
    }

    @Override
    public CommonId getEntityTypeId(net.minecraft.world.entity.EntityType<?> entityType) {
        ResourceLocation key = net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
        return key != null ? new CommonId(key.getNamespace(), key.getPath()) : new CommonId("minecraft", "pig");
    }

    @Override
    public boolean hasCustomHoverName(ItemStack stack) {
        return stack != null && stack.has(net.minecraft.core.component.DataComponents.CUSTOM_NAME);
    }

    @Override
    public abstract net.minecraft.world.entity.EquipmentSlot getEquipmentSlot(ItemStack stack);

@Override
    public net.minecraft.world.phys.AABB getModelBounds(Object model) {
        return ClientAccess.getModelBounds(v121xModelBoundsCache, model);
    }

    @Override
    public void renderItemFixed(ItemStack stack, com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource, int combinedLight, int combinedOverlay, Object model) {
        ClientAccess.renderItemFixed(stack, poseStack, bufferSource, combinedLight, combinedOverlay, model);
    }

    @Override
    public void renderItemStatic(ItemStack stack, com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource, int combinedLight, int combinedOverlay, int seed) {
        ClientAccess.renderItemStatic(stack, poseStack, bufferSource, combinedLight, combinedOverlay, seed);
    }

    @Override
    public void registerRecipeSerializers() {
        RecipeFactory.register();
    }

    @Override
    public byte[] readResourceBytes(net.minecraft.server.packs.resources.ResourceManager resourceManager, CommonId location) {
        if (resourceManager == null || location == null) return null;
        try {
            net.minecraft.resources.ResourceLocation rl = net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(location.getNamespace(), location.getPath());
            java.util.Optional<net.minecraft.server.packs.resources.Resource> res = resourceManager.getResource(rl);
            if (res.isPresent()) {
                try (java.io.InputStream stream = res.get().open()) {
                    return stream.readAllBytes();
                }
            }
        } catch (Throwable ignored) {}
        return null;
    }

    @Override
    public net.minecraft.server.packs.resources.PreparableReloadListener createRecipeReloadListener() {
        return new net.minecraft.server.packs.resources.PreparableReloadListener() {
            @Override
            public java.util.concurrent.CompletableFuture<Void> reload(
                    PreparationBarrier barrier,
                    net.minecraft.server.packs.resources.ResourceManager resourceManager,
                    net.minecraft.util.profiling.ProfilerFiller preparationsProfiler,
                    net.minecraft.util.profiling.ProfilerFiller reloadProfiler,
                    java.util.concurrent.Executor backgroundExecutor,
                    java.util.concurrent.Executor gameExecutor
            ) {
                return java.util.concurrent.CompletableFuture.supplyAsync(() -> com.kingodogo.buildscape.recipe.framework.BuildScapeRecipeLoader.INSTANCE.prepareRecipes(resourceManager), backgroundExecutor)
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
    public CreativeModeTab createCreativeTab(CommonId tabId, String titleKey, Supplier<ItemStack> iconSupplier, List<String> orderedItemIds, Map<String, Item> items) {
        return CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
                .title(Component.translatable(titleKey))
                .icon(iconSupplier)
                .displayItems((params, output) -> {
                    Set<String> added = new HashSet<>();
                    for (String id : orderedItemIds) {
                        Item item = items.get(id);
                        if (item != null && added.add(id)) {
                            output.accept(new ItemStack(item));
                        }
                    }
                    for (Map.Entry<String, Item> entry : items.entrySet()) {
                        if (added.add(entry.getKey())) {
                            output.accept(new ItemStack(entry.getValue()));
                        }
                    }
                })
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
            public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
                super.appendHoverText(stack, context, tooltip, flag);
                addHoverText(stack, tooltip::add);
            }
        };
    }

    @Override
    public net.minecraft.world.item.BlockItem createTrophyBlockItem(com.kingodogo.buildscape.trophy.TrophyBlock block, com.kingodogo.buildscape.trophy.TrophyDefinition definition, Item.Properties properties) {
        return new com.kingodogo.buildscape.trophy.TrophyBlockItem(block, definition, properties) {
            @Override
            public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
                super.appendHoverText(stack, context, tooltip, flag);
                addHoverText(stack, tooltip::add);
            }
        };
    }

    @Override
    public com.kingodogo.buildscape.trophy.TrophyBlock createTrophyBlock(com.kingodogo.buildscape.trophy.TrophyDefinition definition) {
        return new com.kingodogo.buildscape.trophy.TrophyBlock(definition, BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
                .strength(definition.getHardness(), definition.getResistance())
                .sound(definition.getSoundType())
                .lightLevel(state -> definition.getLightEmission())
                .emissiveRendering((state, getter, pos) -> true)
                .noOcclusion()) {
            @Override
            protected com.mojang.serialization.MapCodec<? extends HorizontalDirectionalBlock> codec() {
                return null;
            }

            @Override
            public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
                return createTrophyStack(level.getBlockEntity(pos));
            }

            @Override
            public List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootParams.Builder builder) {
                return java.util.Collections.singletonList(createTrophyStack(builder.getOptionalParameter(net.minecraft.world.level.storage.loot.parameters.LootContextParams.BLOCK_ENTITY)));
            }
        };
    }

    @Override
    public SoundEvent createSoundEvent(CommonId id) {
        ResourceLocation rl = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), id.getPath());
        return SoundEvent.createVariableRangeEvent(rl);
    }

    @Override
    public void playBlockSound(Level level, net.minecraft.core.BlockPos pos, CommonId soundId) {
        ResourceLocation rl = ResourceLocation.fromNamespaceAndPath(soundId.getNamespace(), soundId.getPath());
        SoundEvent se = BuiltInRegistries.SOUND_EVENT.get(rl);
        if (se != null) {
            level.playSound(null, pos, se, net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }

    @Override
    public void neighborChanged(Level level, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state, Block fromBlock, net.minecraft.core.BlockPos fromPos) {
        level.neighborChanged(state, pos, fromBlock, fromPos, false);
    }

    @Override
    public void hurtAndBreak(ItemStack stack, int amount, LivingEntity entity, InteractionHand hand) {
        EquipmentSlot slot = hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
        stack.hurtAndBreak(amount, entity, slot);
    }

    @Override
    public void sendActionBarMessage(Player player, Component message) {
        player.displayClientMessage(message, true);
    }

    @Override
    public void sendSystemMessage(Player player, Component message) {
        player.sendSystemMessage(message);
    }

    @Override
    public void sendCommandSuccess(net.minecraft.commands.CommandSourceStack source, String message, boolean broadcastToOps) {
        source.sendSuccess(() -> Component.literal(message), broadcastToOps);
    }

    @Override
    public void sendCommandFailure(net.minecraft.commands.CommandSourceStack source, String message) {
        source.sendFailure(Component.literal(message));
    }

    @Override
    public MutableComponent literal(String text) {
        return Component.literal(text);
    }

    @Override
    public MutableComponent translatable(String key) {
        return Component.translatable(key);
    }

    @Override
    public void setItemCustomName(ItemStack stack, Component name) {
        stack.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME, name);
    }

    @Override
    public String getItemCustomName(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return null;
        Component customName = stack.get(net.minecraft.core.component.DataComponents.CUSTOM_NAME);
        return customName != null ? customName.getString() : null;
    }

    @Override
    public MutableComponent translatable(String key, Object... args) {
        return Component.translatable(key, args);
    }

    @Override
    public void playButtonClick(Level level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.BLOCKS, 0.5f, 1.0f);
    }

    @Override
    public void playButtonClick(Level level, BlockPos pos, float pitch) {
        level.playSound(null, pos, SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.BLOCKS, 0.5f, pitch);
    }

    @Override
    public void playNoteHarp(Level level, BlockPos pos, float pitch) {
        level.playSound(null, pos, SoundEvents.NOTE_BLOCK_HARP.value(), SoundSource.RECORDS, 3.0F, pitch);
    }

    @Override
    public void spawnDustParticles(ServerLevel level, double x, double y, double z, int color, float scale) {
        Vec3 rgb = Vec3.fromRGB24(color);
        level.sendParticles(new DustParticleOptions(new Vector3f((float) rgb.x, (float) rgb.y, (float) rgb.z), scale), x, y, z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
    }

    @Override
    public void playEatSound(Level level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.GENERIC_EAT, SoundSource.PLAYERS, 0.8F, 1.0F);
    }

    @Override
    public void playDrinkSound(Level level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.GENERIC_DRINK, SoundSource.PLAYERS, 0.8F, 1.0F);
    }

    @Override
    public void playHoneyDrinkSound(Level level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.HONEY_DRINK, SoundSource.PLAYERS, 0.8F, 1.0F);
    }

    @Override
    public void playFlintAndSteelSound(Level level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    @Override
    public void playCandleExtinguishSound(Level level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.CANDLE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    @Override
    public void playCandleAmbientSound(Level level, BlockPos pos, float pitch) {
        level.playSound(null, pos, SoundEvents.CANDLE_AMBIENT, SoundSource.BLOCKS, 0.8F, pitch);
    }

    @Override
    public void playCandleAmbientLocalSound(Level level, double x, double y, double z, float volume, float pitch) {
        level.playLocalSound(x, y, z, SoundEvents.CANDLE_AMBIENT, SoundSource.BLOCKS, volume, pitch, false);
    }

    @Override
    public DyeColor getDyeColor(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return null;
        if (stack.getItem() instanceof DyeItem dye) {
            return dye.getDyeColor();
        }
        return null;
    }

    @Override
    public int getDyeColorValue(DyeColor color) {
        return color != null ? color.getTextureDiffuseColor() : 0xFFFFFF;
    }

    @Override
    public void syncChunk(ServerLevel level, LevelChunk chunk) {
        ClientboundLevelChunkWithLightPacket packet =
                new ClientboundLevelChunkWithLightPacket(chunk, level.getLightEngine(), null, null);
        level.getChunkSource().chunkMap.getPlayers(chunk.getPos(), false).forEach(player -> player.connection.send(packet));
    }

    @Override
    public void markChunkUnsaved(LevelChunk chunk) {
        chunk.setUnsaved(true);
    }

    @Override
    public void markChunkUnsaved(Level level, BlockPos pos) {
        if (level != null && pos != null) {
            markChunkUnsaved(level.getChunkAt(pos));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setSectionBiome(LevelChunkSection section, int localQx, int localQy, int localQz, Holder<Biome> biome) {
        if (section.getBiomes() instanceof PalettedContainer<?> container) {
            ((PalettedContainer<Holder<Biome>>) container).set(localQx, localQy, localQz, biome);
        }
    }

    @Override
    public CommonId getBiomeId(Level level, Holder<Biome> biomeHolder) {
        if (biomeHolder == null) return null;
        ResourceLocation rl = level.registryAccess().registryOrThrow(Registries.BIOME).getKey(biomeHolder.value());
        return rl != null ? new CommonId(rl.getNamespace(), rl.getPath()) : null;
    }

    @Override
    public Holder<Biome> getBiomeHolder(ServerLevel level, CommonId id) {
        if (id == null) return null;
        ResourceLocation rl = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), id.getPath());
        ResourceKey<Biome> key = ResourceKey.create(Registries.BIOME, rl);
        return level.registryAccess().registryOrThrow(Registries.BIOME).getHolder(key).orElse(null);
    }

    @Override
    public int getUnbreakingLevel(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return 0;
        ItemEnchantments enchantments = stack.get(DataComponents.ENCHANTMENTS);
        if (enchantments != null) {
            for (Holder<Enchantment> holder : enchantments.keySet()) {
                if (holder.is(Enchantments.UNBREAKING)) {
                    return enchantments.getLevel(holder);
                }
            }
        }
        return 0;
    }

    @Override
    public int getEfficiencyLevel(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return 0;
        ItemEnchantments enchantments = stack.get(DataComponents.ENCHANTMENTS);
        if (enchantments != null) {
            for (Holder<Enchantment> holder : enchantments.keySet()) {
                if (holder.is(Enchantments.EFFICIENCY)) return enchantments.getLevel(holder);
            }
        }
        return 0;
    }

    @Override
    public float getHoeMiningSpeed(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof net.minecraft.world.item.DiggerItem digger) {
            return digger.getTier().getSpeed();
        }
        return 2.0F;
    }

    @Override
    public boolean hasSilkTouch(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        ItemEnchantments enchantments = stack.get(DataComponents.ENCHANTMENTS);
        if (enchantments != null) {
            for (Holder<Enchantment> holder : enchantments.keySet()) {
                if (holder.is(Enchantments.SILK_TOUCH)) return enchantments.getLevel(holder) > 0;
            }
        }
        return false;
    }

    @Override
    public void awardAdvancement(ServerPlayer player, CommonId advancementId, String criterion) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(advancementId.getNamespace(), advancementId.getPath());
        AdvancementHolder adv = player.getServer().getAdvancements().get(id);
        if (adv != null) {
            player.getAdvancements().award(adv, criterion);
        }
    }

    @Override
    public void awardStat(ServerPlayer player, CommonId statId) {
        if (player == null || statId == null) return;
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(statId.getNamespace(), statId.getPath());
        net.minecraft.stats.Stat<ResourceLocation> stat = net.minecraft.stats.Stats.CUSTOM.get(id);
        if (stat != null) {
            player.awardStat(stat);
        }
    }

    @Override
    public CompoundTag getCustomData(ItemStack stack, boolean create) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return data != null ? data.copyTag() : (create ? new CompoundTag() : null);
    }

    @Override
    public void updateCustomData(ItemStack stack, Consumer<CompoundTag> updater) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, updater);
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack stack) {
        if (stack == null || stack.isEmpty() || !stack.getItem().hasCraftingRemainingItem()) return ItemStack.EMPTY;
        return new ItemStack(stack.getItem().getCraftingRemainingItem());
    }

    @Override
    public boolean isArmorItem(ItemStack stack) {
        return stack != null && !stack.isEmpty() && stack.getItem() instanceof net.minecraft.world.item.ArmorItem;
    }

    @Override
    public void configureFireworkStar(ItemStack stack, byte shapeId, int[] colors, boolean flicker, boolean trail) {
        storeCustomFireworkShape(stack, "Explosion", 0, shapeId, colors, flicker, trail);
        stack.set(DataComponents.FIREWORK_EXPLOSION, createNativeFireworkExplosion(colors, flicker, trail));
    }

    @Override
    public void configureFireworkRocket(ItemStack stack, int flight, byte shapeId, int[] colors, boolean flicker, boolean trail) {
        storeCustomFireworkShape(stack, "Fireworks", flight, shapeId, colors, flicker, trail);
        stack.set(DataComponents.FIREWORKS, new net.minecraft.world.item.component.Fireworks(
                Math.max(0, Math.min(3, flight)), java.util.List.of(createNativeFireworkExplosion(colors, flicker, trail))));
    }

    @Override
    public int[] getFireworkStarColors(ItemStack stack) {
        net.minecraft.world.item.component.FireworkExplosion explosion = stack.get(DataComponents.FIREWORK_EXPLOSION);
        return explosion == null ? new int[0] : explosion.colors().toIntArray();
    }

    @Override
    public int getDyeFireworkColor(ItemStack stack) {
        return stack.getItem() instanceof net.minecraft.world.item.DyeItem dye ? dye.getDyeColor().getFireworkColor() : 0;
    }

    private static net.minecraft.world.item.component.FireworkExplosion createNativeFireworkExplosion(int[] colors, boolean flicker, boolean trail) {
        return new net.minecraft.world.item.component.FireworkExplosion(
                net.minecraft.world.item.component.FireworkExplosion.Shape.SMALL_BALL,
                new it.unimi.dsi.fastutil.ints.IntArrayList(colors),
                it.unimi.dsi.fastutil.ints.IntLists.emptyList(), trail, flicker);
    }

    private void storeCustomFireworkShape(ItemStack stack, String key, int flight, byte shapeId, int[] colors, boolean flicker, boolean trail) {
        updateCustomData(stack, root -> {
            CompoundTag explosion = new CompoundTag();
            explosion.putByte("Type", shapeId);
            explosion.putIntArray("Colors", colors);
            explosion.putBoolean("Flicker", flicker);
            explosion.putBoolean("Trail", trail);
            if ("Explosion".equals(key)) root.put(key, explosion);
            else {
                CompoundTag fireworks = new CompoundTag();
                net.minecraft.nbt.ListTag list = new net.minecraft.nbt.ListTag();
                list.add(explosion);
                fireworks.put("Explosions", list);
                fireworks.putByte("Flight", (byte) Math.max(0, Math.min(3, flight)));
                root.put(key, fireworks);
            }
        });
    }

    @Override
    public CommonId getDimensionId(Level level) {
        if (level == null) return CommonId.of("minecraft", "overworld");
        ResourceLocation id = level.dimension().location();
        return CommonId.of(id.getNamespace(), id.getPath());
    }

    @Override
    public boolean isNight(Level level) {
        return level != null && level.isNight();
    }

    @Override
    public long packChunkPos(int chunkX, int chunkZ) {
        return ChunkPos.asLong(chunkX, chunkZ);
    }

    @Override
    public long packChunkPos(ChunkPos chunkPos) {
        return chunkPos != null ? chunkPos.toLong() : 0L;
    }

    @Override
    public long packChunkPos(BlockPos pos) {
        return new ChunkPos(pos).toLong();
    }

    @Override
    public boolean isItemInTag(ItemStack stack, CommonId tagId) {
        if (stack == null || stack.isEmpty() || tagId == null) return false;
        try {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(tagId.getNamespace(), tagId.getPath());
            TagKey<Item> tag = TagKey.create(Registries.ITEM, id);
            return stack.is(tag);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isBlockInTag(Block block, CommonId tagId) {
        if (block == null || tagId == null) return false;
        try {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(tagId.getNamespace(), tagId.getPath());
            return block.defaultBlockState().is(TagKey.create(Registries.BLOCK, id));
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isEntityTypeInTag(Entity entity, CommonId tagId) {
        if (entity == null || tagId == null) return false;
        try {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(tagId.getNamespace(), tagId.getPath());
            return entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, id));
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void markEntityVelocityChanged(Entity entity) {
        entity.hasImpulse = true;
    }

    @Override
    public net.minecraft.world.InteractionResult sidedSuccess(boolean clientSide) {
        return net.minecraft.world.InteractionResult.sidedSuccess(clientSide);
    }

    @Override
    public boolean doesBedExplode(Level level) {
        return !net.minecraft.world.level.block.BedBlock.canSetSpawn(level);
    }

    @Override
    public net.minecraft.world.item.trading.MerchantOffer createMerchantOffer(net.minecraft.world.item.ItemStack cost, net.minecraft.world.item.ItemStack result, int maxUses, int xp, float priceMultiplier) {
        return new net.minecraft.world.item.trading.MerchantOffer(new net.minecraft.world.item.trading.ItemCost(cost.getItem(), cost.getCount()), result, maxUses, xp, priceMultiplier);
    }

    @Override
    public net.minecraft.world.item.crafting.Ingredient createTagIngredient(CommonId id) {
        return net.minecraft.world.item.crafting.Ingredient.of(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, ResourceLocation.fromNamespaceAndPath(id.getNamespace(), id.getPath())));
    }

    @Override
    public net.minecraft.tags.TagKey<net.minecraft.world.item.Item> createItemTagKey(CommonId id) {
        return net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM,
                net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(id.getNamespace(), id.getPath()));
    }

    @Override
    public boolean isGlassBaseBlock(Block block) {
        return block instanceof net.minecraft.world.level.block.TransparentBlock;
    }

    @Override
    public boolean isEnchantingTableBlock(Block block) {
        return block instanceof net.minecraft.world.level.block.EnchantingTableBlock;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack != null && !stack.isEmpty() && stack.has(DataComponents.FOOD);
    }

    @Override
    public boolean isWaterPotion(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        if (stack.is(Items.WATER_BUCKET)) return true;
        PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
        return contents != null && contents.is(Potions.WATER);
    }

    @Override
    public boolean isEnchantedItem(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        return stack.isEnchanted() || stack.hasFoil()
                || !stack.getEnchantments().isEmpty()
                || !net.minecraft.world.item.enchantment.EnchantmentHelper.getEnchantmentsForCrafting(stack).isEmpty();
    }

    @Override
    public ItemStack createWaterPotion() {
        return PotionContents.createItemStack(Items.POTION, Potions.WATER);
    }

    @Override
    public void applyFoodEffects(Player player, Level level, ItemStack foodStack) {
        if (player == null || foodStack == null || foodStack.isEmpty()) return;
        foodStack.getItem().finishUsingItem(foodStack, level, player);
    }

    @Override
    public void applyPotionEffects(LivingEntity entity, ItemStack potionStack) {
        if (entity == null || potionStack == null || potionStack.isEmpty()) return;
        PotionContents contents = potionStack.get(DataComponents.POTION_CONTENTS);
        if (contents != null) {
            contents.forEachEffect(entity::addEffect);
        }
    }

    @Override
    public String getTagString(CompoundTag tag, String key, String defaultValue) {
        if (tag == null || !tag.contains(key)) return defaultValue;
        return tag.getString(key);
    }

    @Override
    public int getTagInt(CompoundTag tag, String key, int defaultValue) {
        if (tag == null || !tag.contains(key)) return defaultValue;
        return tag.getInt(key);
    }

    @Override
    public boolean getTagBoolean(CompoundTag tag, String key, boolean defaultValue) {
        if (tag == null || !tag.contains(key)) return defaultValue;
        return tag.getBoolean(key);
    }

    @Override
    public CompoundTag getTagCompound(CompoundTag tag, String key) {
        if (tag == null || !tag.contains(key)) return null;
        return tag.getCompound(key);
    }

    @Override
    public java.util.Set<String> getTagKeys(CompoundTag tag) {
        return tag != null ? tag.getAllKeys() : java.util.Collections.emptySet();
    }

    @Override
    public CompoundTag readCompressedTag(java.io.File file) throws java.io.IOException {
        return net.minecraft.nbt.NbtIo.readCompressed(file.toPath(), net.minecraft.nbt.NbtAccounter.unlimitedHeap());
    }

    @Override
    public void writeCompressedTag(java.io.File file, CompoundTag tag) throws java.io.IOException {
        net.minecraft.nbt.NbtIo.writeCompressed(tag, file.toPath());
    }

    @Override
    public List<String> getTagStringList(CompoundTag tag, String key) {
        List<String> result = new ArrayList<>();
        if (tag == null || !tag.contains(key)) return result;
        ListTag list = tag.getList(key, 8);
        for (int i = 0; i < list.size(); i++) {
            result.add(list.getString(i));
        }
        return result;
    }

    @Override
    public void putTagStringList(CompoundTag tag, String key, List<String> list) {
        if (tag == null) return;
        ListTag listTag = new ListTag();
        if (list != null) {
            for (String s : list) {
                listTag.add(StringTag.valueOf(s));
            }
        }
        tag.put(key, listTag);
    }

    @Override
    public void spawnMob(ServerLevel level, EntityType<?> type, ItemStack stack, BlockPos pos) {
        if (level == null || type == null) return;
        type.spawn(level, stack, null, pos, MobSpawnType.SPAWN_EGG, true, false);
    }

    @Override
    public boolean isSameItemSameComponents(ItemStack a, ItemStack b) {
        return ItemStack.matches(a, b);
    }

    @Override
    public void applyNauseaEffect(LivingEntity entity, int durationTicks, int amplifier) {
        if (entity != null) {
            entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, durationTicks, amplifier, true, true));
        }
    }

    @Override
    public void hurtFreezeDamage(LivingEntity entity, float amount) {
        if (entity != null) {
            entity.hurt(entity.damageSources().freeze(), amount);
        }
    }

    @Override
    public int getSelectedSlot(Inventory inventory) {
        return inventory != null ? inventory.selected : 0;
    }

    @Override
    public abstract <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(BlockEntityFactory<T> factory, Predicate<BlockState> isValid);

    @Override
    public boolean isOnGround(Entity entity) {
        return entity.onGround();
    }

    @Override
    public void spawnMobFromEgg(ServerLevel level, BlockPos pos, ItemStack eggStack) {
        if (eggStack.getItem() instanceof SpawnEggItem spawnEgg) {
            EntityType<?> entityType = spawnEgg.getType(eggStack);
            if (entityType != null) {
                Vec3 spawnPos = Vec3.atCenterOf(pos).add(0, 0.5, 0);
                spawnMob(level, entityType, eggStack, new BlockPos((int) spawnPos.x, (int) spawnPos.y, (int) spawnPos.z));
                level.sendParticles(ParticleTypes.POOF, spawnPos.x, spawnPos.y + 0.5, spawnPos.z, 8, 0.3, 0.3, 0.3, 0.05);
            }
        }
    }

    @Override
    public Item createBuildersPouchItem(Item.Properties properties) {
        return new com.kingodogo.buildscape.item.BuildersPouchItem(properties) {
            @Override
            public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
                ItemStack pouch = player.getItemInHand(hand);
                if (!level.isClientSide) {
                    Component title = com.kingodogo.buildscape.util.ComponentHelper.translatable("container.buildscape.builders_pouch");
                    player.openMenu(new net.minecraft.world.SimpleMenuProvider((id, inventory, ignored) ->
                            new com.kingodogo.buildscape.menu.BuildersPouchMenu(id, inventory, hand), title));
                }
                return InteractionResultHolder.sidedSuccess(pouch, level.isClientSide);
            }

            @Override
            public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
                com.kingodogo.buildscape.item.BuildersPouchItem.appendBuildersPouchTooltip(stack, tooltip::add);
                super.appendHoverText(stack, context, tooltip, flag);
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
            public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
                appendHammerTooltip(tooltip::add);
                super.appendHoverText(stack, context, tooltip, flag);
            }

            @Override
            public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
                ItemStack stack = player.getItemInHand(hand);
                if (hand == InteractionHand.MAIN_HAND) {
                    ItemStack offHand = player.getOffhandItem();
                    if (!offHand.isEmpty() && offHand.getItem() instanceof BlockItem) {
                        return InteractionResultHolder.success(stack);
                    }
                }
                return InteractionResultHolder.pass(stack);
            }
        };
    }

    @Override
    public void playNoteBlockChime(Level level, BlockPos pos, float pitch) {
        level.playSound(null, pos, SoundEvents.NOTE_BLOCK_CHIME.value(), SoundSource.PLAYERS, 1.0f, pitch);
    }

    private static final java.util.Map<java.util.UUID, CompoundTag> V121X_ENTITY_DATA = new java.util.concurrent.ConcurrentHashMap<>();
    private static final java.util.Map<net.minecraft.world.level.block.entity.BlockEntity, CompoundTag> V121X_BLOCK_ENTITY_DATA = new java.util.concurrent.ConcurrentHashMap<>();

    @Override
    public CompoundTag getEntityData(Entity entity) {
        if (entity == null) return new CompoundTag();
        return V121X_ENTITY_DATA.computeIfAbsent(entity.getUUID(), k -> new CompoundTag());
    }

    @Override
    public CompoundTag getBlockEntityData(net.minecraft.world.level.block.entity.BlockEntity be) {
        if (be == null) return new CompoundTag();
        return V121X_BLOCK_ENTITY_DATA.computeIfAbsent(be, k -> new CompoundTag());
    }

    @Override
    public long getTagLong(CompoundTag tag, String key, long defaultValue) {
        return tag != null && tag.contains(key) ? tag.getLong(key) : defaultValue;
    }

    @Override
    public boolean hasTagUUID(CompoundTag tag, String key) {
        return tag != null && tag.hasUUID(key);
    }

    @Override
    public java.util.UUID getTagUUID(CompoundTag tag, String key) {
        return tag != null ? tag.getUUID(key) : null;
    }

    @Override
    public void putTagUUID(CompoundTag tag, String key, java.util.UUID uuid) {
        if (tag != null && uuid != null) tag.putUUID(key, uuid);
    }

    @Override
    public int getMaxBuildHeight(Level level) {
        return level != null ? level.getMaxBuildHeight() : 320;
    }

    @Override
    public BlockPos getSharedSpawnPos(Level level) {
        return level != null ? level.getSharedSpawnPos() : BlockPos.ZERO;
    }

    @Override
    public Item createConfettiItem(Item.Properties properties) {
        return new com.kingodogo.buildscape.item.ConfettiItem(properties) {
            private static final int USE_COOLDOWN_TICKS = 10;

            @Override
            public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
                ItemStack itemstack = player.getItemInHand(hand);
                if (!level.isClientSide()) {
                    level.playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.PLAYERS, 0.8F, 1.4F);
                    level.playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.FIREWORK_ROCKET_TWINKLE, SoundSource.PLAYERS, 0.6F, 1.6F);
                    if (!player.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    player.getCooldowns().addCooldown(this, USE_COOLDOWN_TICKS);
                }
                return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
            }

            @Override
            public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
                com.kingodogo.buildscape.item.ConfettiItem.appendConfettiTooltip(stack, tooltip::add);
                super.appendHoverText(stack, context, tooltip, flag);
            }
        };
    }

    @Override
    public Item createBottleOfMistItem(Item.Properties properties) {
        return new com.kingodogo.buildscape.item.BottleOfMistItem(properties) {
            @Override
            public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
                ItemStack stack = player.getItemInHand(hand);
                useBuildscape(level, player, stack);
                return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
            }

            @Override
            public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
                appendBuildscapeTooltip(tooltip::add);
                super.appendHoverText(stack, context, tooltip, flag);
            }
        };
    }

    @Override
    public Item createWrenchItem(Item.Properties properties) {
        return new com.kingodogo.buildscape.item.WrenchItem(properties) {
            @Override public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
                appendBuildscapeTooltip(tooltip::add);
                super.appendHoverText(stack, context, tooltip, flag);
            }
        };
    }

    @Override
    public Item createFestiveGlintShardItem(Item.Properties properties) {
        return new com.kingodogo.buildscape.item.FestiveGlintShardItem(properties) {
            @Override public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
                appendBuildscapeTooltip(tooltip::add);
            }
        };
    }

    @Override
    public Item createBigOrnamentTemplateItem(Item.Properties properties) {
        return new com.kingodogo.buildscape.item.BigOrnamentTemplateItem(properties) {
            @Override public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
                super.appendHoverText(stack, context, tooltip, flag);
                appendBuildscapeTooltip(tooltip::add);
            }
        };
    }

    @Override
    public Item createStringlightFrameItem(Item.Properties properties) {
        return new com.kingodogo.buildscape.item.StringlightFrameItem(properties) {
            @Override public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
                appendBuildscapeTooltip(tooltip::add);
                super.appendHoverText(stack, context, tooltip, flag);
            }
        };
    }

    @Override
    public boolean hasEntityTag(Entity entity, String tag) {
        return entity != null && entity.getTags().contains(tag);
    }

    @Override
    public boolean addEntityTag(Entity entity, String tag) {
        return entity != null && entity.addTag(tag);
    }

    @Override
    public boolean removeEntityTag(Entity entity, String tag) {
        return entity != null && entity.removeTag(tag);
    }

    @Override
    public void loadAllItems(CompoundTag tag, NonNullList<ItemStack> items) {
        net.minecraft.world.ContainerHelper.loadAllItems(tag, items, net.minecraft.core.RegistryAccess.fromRegistryOfRegistries(net.minecraft.core.registries.BuiltInRegistries.REGISTRY));
    }

    @Override
    public void saveAllItems(CompoundTag tag, NonNullList<ItemStack> items) {
        net.minecraft.world.ContainerHelper.saveAllItems(tag, items, net.minecraft.core.RegistryAccess.fromRegistryOfRegistries(net.minecraft.core.registries.BuiltInRegistries.REGISTRY));
    }

    @Override
    public ItemStack loadSingleItemStack(CompoundTag tag) {
        if (tag == null || tag.isEmpty()) return ItemStack.EMPTY;
        return ItemStack.parseOptional(net.minecraft.core.RegistryAccess.fromRegistryOfRegistries(net.minecraft.core.registries.BuiltInRegistries.REGISTRY), tag);
    }

    @Override
    public CompoundTag saveSingleItemStack(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return new CompoundTag();
        return (CompoundTag) stack.saveOptional(net.minecraft.core.RegistryAccess.fromRegistryOfRegistries(net.minecraft.core.registries.BuiltInRegistries.REGISTRY));
    }

    @Override
    public Item createPatternItem(Item.Properties properties, String tooltipKey) {
        return new com.kingodogo.buildscape.item.PatternItem(properties, tooltipKey) {
            @Override
            public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
                super.appendHoverText(stack, context, tooltip, flag);
                appendPatternTooltip(stack, tooltip::add);
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
            public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
                super.appendHoverText(stack, context, tooltip, flag);
                com.kingodogo.buildscape.item.GoldenJarItem.addAdvancementTooltip(stack, tooltip::add);
            }
        };
    }

    @Override
    public BlockItem createFestiveStarItem(Block block, Item.Properties properties) {
        return new com.kingodogo.buildscape.item.FestiveStarItem(block, properties) {
            @Override
            public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
                super.appendHoverText(stack, context, tooltip, flag);
                com.kingodogo.buildscape.item.FestiveStarItem.appendFestiveStarTooltip(stack, tooltip::add);
            }
        };
    }

    @Override
    public BlockItem createMuffBlockItem(Block block, Item.Properties properties) {
        return new com.kingodogo.buildscape.item.MuffBlockItem(block, properties) {
            @Override
            public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
                com.kingodogo.buildscape.item.MuffBlockItem.appendMuffBlockTooltip(tooltip::add);
                super.appendHoverText(stack, context, tooltip, flag);
            }
        };
    }

    @Override
    public BlockItem createMistBlockItem(Block block, Item.Properties properties) {
        return new com.kingodogo.buildscape.item.MistBlockItem(block, properties) {
            @Override
            public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
                appendMistBlockTooltip(tooltip::add);
                super.appendHoverText(stack, context, tooltip, flag);
            }
        };
    }

    @Override
    public BlockItem createFestiveStockingItem(Block block, Item.Properties properties, String colorVariant) {
        return new com.kingodogo.buildscape.item.FestiveStockingItem(block, properties, colorVariant);
    }

@Override
    public Item createInfinitePhoenixFireworkStarItem(Item.Properties properties) {
        return new com.kingodogo.buildscape.item.InfinitePhoenixFireworkStarItem(properties) {
            @Override
            public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
                super.appendHoverText(stack, context, tooltip, flag);
                com.kingodogo.buildscape.item.InfinitePhoenixFireworkStarItem.appendInfiniteUsesTooltip(tooltip::add);
            }
        };
    }

    @Override
    public Item createColoredItemFrameItem(Item.Properties properties, String colorVariant) {
        return new com.kingodogo.buildscape.item.ColoredItemFrameItem(properties, colorVariant) {
            @Override
            public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
                appendColoredItemFrameTooltip(stack, tooltip::add);
                super.appendHoverText(stack, context, tooltip, flag);
            }
        };
    }

    @Override
    public Item createExperienceBucketItem(Item.Properties properties) {
        return new com.kingodogo.buildscape.item.ExperienceBucketItem(properties) {
            @Override
            public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
                player.startUsingItem(hand);
                return InteractionResultHolder.consume(player.getItemInHand(hand));
            }

            @Override
            public ItemStack finishUsingItem(ItemStack stack, Level level, net.minecraft.world.entity.LivingEntity entity) {
                if (entity instanceof Player player) {
                    if (!level.isClientSide) {
                        int xp = 25 + level.random.nextInt(6);
                        player.giveExperiencePoints(xp);
                    }

                    if (entity instanceof ServerPlayer serverPlayer) {
                        net.minecraft.advancements.CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, stack);
                        serverPlayer.awardStat(net.minecraft.stats.Stats.ITEM_USED.get(this));
                    }

                    if (!player.getAbilities().instabuild) {
                        return new ItemStack(Items.BUCKET);
                    }
                }
                return stack;
            }

            @Override
            public int getUseDuration(ItemStack stack, net.minecraft.world.entity.LivingEntity entity) {
                return 32;
            }

            @Override
            public net.minecraft.world.item.UseAnim getUseAnimation(ItemStack stack) {
                return net.minecraft.world.item.UseAnim.DRINK;
            }

            @Override
            public SoundEvent getDrinkingSound() {
                return SoundEvents.GENERIC_DRINK;
            }
        };
    }

@Override
    public Item createPoplarBoatItem(Item.Properties properties) {
        return new Item(properties.stacksTo(1)) {
            @Override
            public net.minecraft.world.InteractionResultHolder<ItemStack> use(Level level, net.minecraft.world.entity.player.Player player, net.minecraft.world.InteractionHand hand) {
                ItemStack stack = player.getItemInHand(hand);
                return switch (Services.PLATFORM.placePoplarBoat(this, level, player, hand)) {
                    case FAIL -> net.minecraft.world.InteractionResultHolder.fail(stack);
                    case SUCCESS -> net.minecraft.world.InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
                    case PASS -> net.minecraft.world.InteractionResultHolder.pass(stack);
                };
            }
        };
    }

    @Override
    public ItemStack removeItem(List<ItemStack> items, int slot, int amount) {
        return ContainerHelper.removeItem(items, slot, amount);
    }

    @Override
    public ItemStack takeItem(List<ItemStack> items, int slot) {
        return ContainerHelper.takeItem(items, slot);
    }

    @Override
    public int applyBiomeToArea(ServerLevel serverLevel, BlockPos pos1, BlockPos pos2, Holder<Biome> biomeHolder, ItemStack stack) {
        int minX = Math.min(pos1.getX(), pos2.getX());
        int maxX = Math.max(pos1.getX(), pos2.getX());
        int minY = Math.min(pos1.getY(), pos2.getY());
        int maxY = Math.max(pos1.getY(), pos2.getY());
        int minZ = Math.min(pos1.getZ(), pos2.getZ());
        int maxZ = Math.max(pos1.getZ(), pos2.getZ());

        int unbreakingLevel = getUnbreakingLevel(stack);
        int affectedBlocks = 0;
        Set<LevelChunk> modifiedChunks = new HashSet<>();

        LevelChunk lastChunk = null;
        int lastChunkX = Integer.MIN_VALUE;
        int lastChunkZ = Integer.MIN_VALUE;

        LevelChunkSection lastSection = null;
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

                LevelChunk chunk;
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

                    LevelChunkSection section;
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

        for (LevelChunk chunk : modifiedChunks) {
            markChunkUnsaved(chunk);
            syncChunk(serverLevel, chunk);
        }

        return affectedBlocks;
    }

    @Override
    public void scanChunkForBlock(ServerLevel level, LevelChunk chunk, Predicate<BlockState> predicate, BiConsumer<BlockPos, BlockState> action) {
        LevelChunkSection[] sections = chunk.getSections();
        for (int sectionIndex = 0; sectionIndex < sections.length; sectionIndex++) {
            LevelChunkSection section = sections[sectionIndex];
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
    public void playEvokerPrepareSummon(ServerLevel level, BlockPos pos) {
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

    @Override
    public void playButtonClick() {
        ClientAccess.playButtonClick();
    }

    @Override
    public void playNoteBlockBell() {
        ClientAccess.playNoteBlockBell();
    }

    @Override
    public void playNoteBlockDidgeridoo() {
        ClientAccess.playNoteBlockDidgeridoo();
    }

    @Override
    public void playVegetationStepSound(Level level, BlockPos pos, Entity entity, SoundType sounds) {
        if (entity instanceof Player player && player.onGround() && level.isClientSide()) {
            float stepInterval = 2.0f;
            int currentStep = (int) (player.walkDist / stepInterval);
            int lastStep = (int) (player.walkDistO / stepInterval);

            if (currentStep != lastStep && player.walkDist > player.walkDistO) {
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
            return serverPlayer.hasPermissions(permissionLevel);
        }
        return false;
    }

    @Override
    public boolean hasCommandPermission(net.minecraft.commands.CommandSourceStack source, int level) {
        return source.hasPermission(level);
    }

    @Override
    public net.minecraft.server.MinecraftServer getServer(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            return serverPlayer.getServer();
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void updateGameRule(Player player, String ruleName, boolean value) {
        if (!(player instanceof ServerPlayer serverPlayer) || !hasPermission(serverPlayer, 2)) {
            return;
        }
        net.minecraft.server.MinecraftServer server = serverPlayer.getServer();
        if (server == null) return;
        net.minecraft.world.level.GameRules rules = server.getGameRules();
        if (rules == null) return;

        if (ruleName.equals("fastLeafDecay") && com.kingodogo.buildscape.world.ModGameRules.FAST_LEAF_DECAY instanceof net.minecraft.world.level.GameRules.Key<?> key) {
            rules.getRule((net.minecraft.world.level.GameRules.Key<net.minecraft.world.level.GameRules.BooleanValue>) key).set(value, server);
        } else if (ruleName.equals("disableEndermanGriefing") && com.kingodogo.buildscape.world.ModGameRules.DISABLE_ENDERMAN_GRIEFING instanceof net.minecraft.world.level.GameRules.Key<?> key) {
            rules.getRule((net.minecraft.world.level.GameRules.Key<net.minecraft.world.level.GameRules.BooleanValue>) key).set(value, server);
        } else if (ruleName.equals("disableCreeperGriefing") && com.kingodogo.buildscape.world.ModGameRules.DISABLE_CREEPER_GRIEFING instanceof net.minecraft.world.level.GameRules.Key<?> key) {
            rules.getRule((net.minecraft.world.level.GameRules.Key<net.minecraft.world.level.GameRules.BooleanValue>) key).set(value, server);
        } else if (ruleName.equals("disableGhastGriefing") && com.kingodogo.buildscape.world.ModGameRules.DISABLE_GHAST_GRIEFING instanceof net.minecraft.world.level.GameRules.Key<?> key) {
            rules.getRule((net.minecraft.world.level.GameRules.Key<net.minecraft.world.level.GameRules.BooleanValue>) key).set(value, server);
        } else if (ruleName.equals("isCakeStack") && com.kingodogo.buildscape.world.ModGameRules.IS_CAKE_STACK instanceof net.minecraft.world.level.GameRules.Key<?> key) {
            rules.getRule((net.minecraft.world.level.GameRules.Key<net.minecraft.world.level.GameRules.BooleanValue>) key).set(value, server);
        } else if (ruleName.equals("isWaterbottleStack") && com.kingodogo.buildscape.world.ModGameRules.IS_WATER_BOTTLE_STACK instanceof net.minecraft.world.level.GameRules.Key<?> key) {
            rules.getRule((net.minecraft.world.level.GameRules.Key<net.minecraft.world.level.GameRules.BooleanValue>) key).set(value, server);
        }
    }

    @Override
    public float[] getDyeDiffuseColors(net.minecraft.world.item.DyeColor dyeColor) {
        int color = dyeColor.getTextureDiffuseColor();
        return new float[]{((color >> 16) & 0xFF) / 255.0F, ((color >> 8) & 0xFF) / 255.0F, (color & 0xFF) / 255.0F};
    }

    @Override
    public float getWalkDist(Player player) {
        return player.walkDist;
    }

    @Override
    public float getWalkDistO(Player player) {
        return player.walkDistO;
    }

    @Override
    public void registerCommonLifecycleInteractions() {
        registerFlowerPotPlants();
        registerCompostables();
        registerCauldronInteractions();
    }

    private void registerFlowerPotPlants() {
        registerPot("red_bush", "potted_red_bush");
        registerPot("frost_rose", "potted_frost_rose");
        registerPot("red_monets", "potted_red_monets");
        registerPot("blue_monets", "potted_blue_monets");
        registerPot("purple_monets", "potted_purple_monets");
        registerPot("light_blue_monets", "potted_light_blue_monets");
        registerPot("pink_monets", "potted_pink_monets");
        registerPot("yellow_monets", "potted_yellow_monets");
        registerPot("poplar_sapling", "potted_poplar_sapling");
        registerPot("clover", "potted_clover");
        registerPot("snowy_bush", "potted_snowy_bush");
        registerPot("snowy_short_grass", "potted_snowy_short_grass");
        registerPot("snowy_tall_grass", "potted_snowy_tall_grass");
        registerPot("snowy_fern", "potted_snowy_fern");
        registerPot("snowy_large_fern", "potted_snowy_large_fern");
    }

    private void registerPot(String flowerId, String pottedId) {
        Block flower = getBlock(new CommonId("buildscape", flowerId));
        Block potted = getBlock(new CommonId("buildscape", pottedId));
        if (flower != null && flower != Blocks.AIR && potted != null && potted != Blocks.AIR) {
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
            } catch (Throwable ignored) {}
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
        if (item != null && item != Items.AIR) {
            net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(item, chance);
        }
    }

    private void registerCauldronInteractions() {
        try {
            Item xpBucket = getItem(new CommonId("buildscape", "experience_bucket"));
            Block xpCauldron = getBlock(new CommonId("buildscape", "experience_cauldron"));
            if (xpBucket != null && xpCauldron != null) {
                net.minecraft.core.cauldron.CauldronInteraction.EMPTY.map().put(xpBucket, (state, level, pos, player, hand, stack) -> {
                    if (!level.isClientSide()) {
                        player.awardStat(net.minecraft.stats.Stats.FILL_CAULDRON);
                        level.setBlockAndUpdate(pos, xpCauldron.defaultBlockState().setValue(net.minecraft.world.level.block.LayeredCauldronBlock.LEVEL, 3));
                        level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                        if (!player.getAbilities().instabuild) {
                            player.setItemInHand(hand, new ItemStack(Items.BUCKET));
                        }
                    }
                    return net.minecraft.world.ItemInteractionResult.sidedSuccess(level.isClientSide());
                });

                net.minecraft.core.cauldron.CauldronInteraction.EMPTY.map().put(Items.EXPERIENCE_BOTTLE, (state, level, pos, player, hand, stack) -> {
                    if (!level.isClientSide()) {
                        player.awardStat(net.minecraft.stats.Stats.FILL_CAULDRON);
                        level.setBlockAndUpdate(pos, xpCauldron.defaultBlockState().setValue(net.minecraft.world.level.block.LayeredCauldronBlock.LEVEL, 1));
                        level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                        if (!player.getAbilities().instabuild) {
                            stack.shrink(1);
                            ItemStack returnStack = new ItemStack(Items.GLASS_BOTTLE);
                            if (!player.getInventory().add(returnStack)) {
                                player.drop(returnStack, false);
                            }
                        }
                    }
                    return net.minecraft.world.ItemInteractionResult.sidedSuccess(level.isClientSide());
                });
            }
        } catch (Throwable ignored) {
        }
    }

    @Override
    public void renderModelPart(net.minecraft.client.model.geom.ModelPart part, com.mojang.blaze3d.vertex.PoseStack poseStack, com.mojang.blaze3d.vertex.VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        ClientAccess.renderModelPart(part, poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public int getRenderDistanceChunks() {
        return ClientAccess.getRenderDistanceChunks();
    }

    @Override
    public void renderLineBox(com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float r, float g, float b, float a) {
        ClientAccess.renderLineBox(poseStack, bufferSource, minX, minY, minZ, maxX, maxY, maxZ, r, g, b, a);
    }

    @Override
    public boolean isScreenOpen() {
        return ClientAccess.isScreenOpen();
    }

    @Override
    public net.minecraft.world.phys.Vec3 getCameraPosition(net.minecraft.client.Camera camera) {
        return ClientAccess.getCameraPosition(camera);
    }

    @Override
    public void renderFallingIcicleBlock(net.minecraft.world.level.Level level, net.minecraft.world.level.block.state.BlockState blockState, net.minecraft.core.BlockPos blockPos, net.minecraft.core.BlockPos startPos, com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource) {
        ClientAccess.renderFallingIcicleBlock(level, blockState, blockPos, startPos, poseStack, bufferSource);
    }

    @Override
    public int getLightColor(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos) {
        return ClientAccess.getLightColor(level, pos);
    }

    @Override
    public boolean isMapItemWithData(ItemStack stack, net.minecraft.world.level.Level level) {
        return stack != null && !stack.isEmpty() && net.minecraft.world.item.MapItem.getSavedData(stack, level) != null;
    }

@Override
    public void renderColoredFrame(com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource, int packedLight, Object backTexture, boolean hasMap) {
        ClientAccess.renderColoredFrame(poseStack, bufferSource, packedLight, backTexture, hasMap);
    }

    @Override
    public void renderColoredFrameItem(com.kingodogo.buildscape.entity.ColoredItemFrameEntity entity, ItemStack itemStack, com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource, int packedLight, boolean isMap, boolean isInvisible) {
        ClientAccess.renderColoredFrameItem(entity, itemStack, poseStack, bufferSource, packedLight, isMap, isInvisible);
    }

    @Override
    public void renderStockingQuad(com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource, int packedLight, Object texture, boolean flipped) {
        ClientAccess.renderStockingQuad(poseStack, bufferSource, packedLight, texture, flipped);
    }

    @Override
    public void renderBlockModel(net.minecraft.world.level.block.state.BlockState blockState, com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource, int light, int overlay) {
        ClientAccess.renderBlockModel(blockState, poseStack, bufferSource, light, overlay);
    }

    @Override
    public void renderBlockModelWithTint(net.minecraft.world.level.block.state.BlockState state, net.minecraft.core.BlockPos pos, net.minecraft.world.level.Level level, com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource, int light, int overlay) {
        ClientAccess.renderBlockModelWithTint(state, pos, level, poseStack, bufferSource, light, overlay);
    }

    @Override
    public void renderColoredQuad(com.mojang.blaze3d.vertex.PoseStack poseStack, Object buffer,
                                  float x0, float y0, float z0, float u0, float v0,
                                  float x1, float y1, float z1, float u1, float v1,
                                  float x2, float y2, float z2, float u2, float v2,
                                  float x3, float y3, float z3, float u3, float v3,
                                  float r, float g, float b, float a,
                                  int light, int overlay,
                                  float nx, float ny, float nz) {
        if (!(buffer instanceof com.mojang.blaze3d.vertex.VertexConsumer vc)) return;
        org.joml.Matrix4f matrix = poseStack.last().pose();
        com.mojang.blaze3d.vertex.PoseStack.Pose lastPose = poseStack.last();
        vc.addVertex(matrix, x0, y0, z0).setColor(r, g, b, a).setUv(u0, v0).setOverlay(overlay).setLight(light).setNormal(lastPose, nx, ny, nz);
        vc.addVertex(matrix, x1, y1, z1).setColor(r, g, b, a).setUv(u1, v1).setOverlay(overlay).setLight(light).setNormal(lastPose, nx, ny, nz);
        vc.addVertex(matrix, x2, y2, z2).setColor(r, g, b, a).setUv(u2, v2).setOverlay(overlay).setLight(light).setNormal(lastPose, nx, ny, nz);
        vc.addVertex(matrix, x3, y3, z3).setColor(r, g, b, a).setUv(u3, v3).setOverlay(overlay).setLight(light).setNormal(lastPose, nx, ny, nz);
        if (ny > 0) {
            vc.addVertex(matrix, x3, y3, z3).setColor(r, g, b, a).setUv(u3, v3).setOverlay(overlay).setLight(light).setNormal(lastPose, -nx, -ny, -nz);
            vc.addVertex(matrix, x2, y2, z2).setColor(r, g, b, a).setUv(u2, v2).setOverlay(overlay).setLight(light).setNormal(lastPose, -nx, -ny, -nz);
            vc.addVertex(matrix, x1, y1, z1).setColor(r, g, b, a).setUv(u1, v1).setOverlay(overlay).setLight(light).setNormal(lastPose, -nx, -ny, -nz);
            vc.addVertex(matrix, x0, y0, z0).setColor(r, g, b, a).setUv(u0, v0).setOverlay(overlay).setLight(light).setNormal(lastPose, -nx, -ny, -nz);
        }
    }

    @Override
    public void renderJarFluid(com.kingodogo.buildscape.block.GlassJarBlockEntity blockEntity, com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource, int light, int overlay) {
        ClientAccess.renderJarFluid(blockEntity, poseStack, bufferSource, light, overlay);
    }

    @Override
    public void renderGlassJar(com.kingodogo.buildscape.block.GlassJarBlockEntity blockEntity, float partialTicks, com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource, int combinedLight, int combinedOverlay) {
        ClientAccess.renderGlassJar(blockEntity, partialTicks, poseStack, bufferSource, combinedLight, combinedOverlay);
    }

    @Override
    public void renderWobblyBlock(net.minecraft.world.level.block.state.BlockState state, long currentTick, long wobbleStartTick, boolean hasWobble, float partialTicks, com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource, int light, int overlay) {
        poseStack.pushPose();
        if (hasWobble && wobbleStartTick > 0) {
            float ticksSinceWobble = (float) (currentTick - wobbleStartTick) + partialTicks;
            if (ticksSinceWobble < 10.0F) {
                float wobbleProgress = ticksSinceWobble / 10.0F;
                float dampening = 1.0F - wobbleProgress;
                float oscillation = (float) Math.sin(wobbleProgress * Math.PI * 6);
                float rotationAngle = 8.0F * dampening * oscillation;

                poseStack.translate(0.5D, 0.0D, 0.5D);
                poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(rotationAngle));
                poseStack.translate(-0.5D, 0.0D, -0.5D);
            }
        }
        renderBlockModel(state, poseStack, bufferSource, light, overlay);
        poseStack.popPose();
    }

@Override
    public abstract void registerMenuScreens();

    @Override
    public void registerRenderers() {
        if (!isClient()) return;
        RenderFactory.registerRenderers();
    }

    @Override
    public void registerLayerDefinitions(java.util.function.BiConsumer<Object, java.util.function.Supplier<Object>> registrar) {
        if (!isClient()) return;
        RenderFactory.registerLayerDefinitions(registrar);
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

    @Override
    public void renderEntity(net.minecraft.world.entity.Entity entity, double x, double y, double z, float yaw, float partialTicks, com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource, int packedLight) {
        RenderFactory.renderEntity(entity, x, y, z, yaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    @Override
    public com.mojang.blaze3d.vertex.VertexConsumer getTranslucentBuffer(Object bufferSource) {
        return RenderFactory.getTranslucentBuffer(bufferSource);
    }

    @Override
    public boolean isTranslucent(net.minecraft.world.level.block.state.BlockState state) {
        return ClientAccess.isTranslucent(state);
    }

@Override
    public BlockColorSample sampleBlockColor(net.minecraft.world.level.block.state.BlockState state) {
        return ClientAccess.sampleBlockColor(v121xSpriteCache, this::getSpriteImage, state);
    }

public static class ColoredItemFrameEntityImpl extends HangingEntity implements ColoredItemFrameEntity {

        private static final EntityDataAccessor<ItemStack> DATA_ITEM = SynchedEntityData
                .defineId(ColoredItemFrameEntityImpl.class, EntityDataSerializers.ITEM_STACK);
        private static final EntityDataAccessor<Integer> DATA_ROTATION = SynchedEntityData
                .defineId(ColoredItemFrameEntityImpl.class, EntityDataSerializers.INT);
        private static final EntityDataAccessor<String> DATA_COLOR = SynchedEntityData
                .defineId(ColoredItemFrameEntityImpl.class, EntityDataSerializers.STRING);
        private static final EntityDataAccessor<String> DATA_PARTICLE_PATTERN = SynchedEntityData
                .defineId(ColoredItemFrameEntityImpl.class, EntityDataSerializers.STRING);
        private static final EntityDataAccessor<String> DATA_PARTICLE_COLORS = SynchedEntityData
                .defineId(ColoredItemFrameEntityImpl.class, EntityDataSerializers.STRING);

        private float dropChance = 1.0F;
        private boolean fixed = false;

        public ColoredItemFrameEntityImpl(EntityType<? extends HangingEntity> entityType, Level level) {
            super(entityType, level);
        }

        public ColoredItemFrameEntityImpl(EntityType<? extends HangingEntity> entityType, Level level, BlockPos pos, Direction direction) {
            super(entityType, level, pos);
            this.setDirection(direction);
        }

        public ColoredItemFrameEntityImpl(Level level, BlockPos pos, Direction direction, String color) {
            super(EntityType.ITEM_FRAME, level, pos);
            this.setDirection(direction);
            this.setColorVariant(color);
            if ("invisible".equals(color)) {
                this.setInvisible(true);
            }
        }

        @Override
        protected void defineSynchedData(SynchedEntityData.Builder builder) {
            builder.define(DATA_ITEM, ItemStack.EMPTY);
            builder.define(DATA_ROTATION, 0);
            builder.define(DATA_COLOR, "white");
            builder.define(DATA_PARTICLE_PATTERN, "none");
            builder.define(DATA_PARTICLE_COLORS, "");
        }

        @Override
        protected void setDirection(Direction direction) {
            Objects.requireNonNull(direction);
            this.direction = direction;
            if (direction.getAxis().isHorizontal()) {
                this.setXRot(0.0F);
                this.setYRot((float) (this.direction.get2DDataValue() * 90));
            } else {
                this.setXRot((float) (-90 * direction.getAxisDirection().getStep()));
                this.setYRot(0.0F);
            }
            this.xRotO = this.getXRot();
            this.yRotO = this.getYRot();
            this.recalculateBoundingBox();
        }

        @Override
        protected AABB calculateBoundingBox(BlockPos pos, Direction direction) {
            if (pos == null || direction == null) {
                return new AABB(this.getX(), this.getY(), this.getZ(), this.getX(), this.getY(), this.getZ());
            }

            double x = (double) pos.getX() + 0.5D - (double) direction.getStepX() * 0.46875D;
            double y = (double) pos.getY() + 0.5D - (double) direction.getStepY() * 0.46875D;
            double z = (double) pos.getZ() + 0.5D - (double) direction.getStepZ() * 0.46875D;

            double w = 12 / 32.0D;
            double h = 12 / 32.0D;
            double d = 12 / 32.0D;

            Direction.Axis axis = direction.getAxis();
            switch (axis) {
                case X -> w = 1.0D / 32.0D;
                case Y -> h = 1.0D / 32.0D;
                case Z -> d = 1.0D / 32.0D;
            }

            return new AABB(x - w, y - h, z - d, x + w, y + h, z + d);
        }

        public int getWidth() {
            return 12;
        }

        public int getHeight() {
            return 12;
        }

        @Override
        public String getColorVariant() {
            return this.getEntityData().get(DATA_COLOR);
        }

        @Override
        public void setColorVariant(String color) {
            this.getEntityData().set(DATA_COLOR, color);
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
        public ItemStack getItem() {
            return this.getEntityData().get(DATA_ITEM);
        }

        @Override
        public void setItem(ItemStack stack) {
            this.setItem(stack, true);
        }

        public void setItem(ItemStack stack, boolean playSound) {
            if (!stack.isEmpty()) {
                stack = stack.copy();
                stack.setCount(1);
                stack.setEntityRepresentation(this);
            }
            this.getEntityData().set(DATA_ITEM, stack);
            if (!stack.isEmpty() && playSound) {
                this.playSound(SoundEvents.ITEM_FRAME_ADD_ITEM, 1.0F, 1.0F);
            }
            if (this.pos != null) {
                this.level().updateNeighbourForOutputSignal(this.pos, Blocks.AIR);
            }
        }

        @Override
        public int getRotation() {
            return this.getEntityData().get(DATA_ROTATION);
        }

        @Override
        public void setRotation(int rotation) {
            this.setRotation(rotation, true);
        }

        public void setRotation(int rotation, boolean playSound) {
            this.getEntityData().set(DATA_ROTATION, rotation % 8);
            if (playSound && !this.getItem().isEmpty()) {
                this.playSound(SoundEvents.ITEM_FRAME_ROTATE_ITEM, 1.0F, 1.0F);
            }
            if (this.pos != null) {
                this.level().updateNeighbourForOutputSignal(this.pos, Blocks.AIR);
            }
        }

        @Override
        public InteractionResult interact(Player player, InteractionHand hand) {
            ItemStack heldItem = player.getItemInHand(hand);
            ItemStack frameItem = this.getItem();
            boolean hasItemInFrame = !frameItem.isEmpty();
            boolean hasItemInHand = !heldItem.isEmpty();

            if (this.fixed) {
                return InteractionResult.PASS;
            } else if (!this.level().isClientSide()) {
                if (!hasItemInFrame) {
                    if (hasItemInHand && !this.isRemoved()) {
                        if (heldItem.is(Items.FILLED_MAP)) {
                            MapItemSavedData mapData = MapItem.getSavedData(heldItem, this.level());
                            if (mapData != null && mapData.isTrackedCountOverLimit(256)) {
                                return InteractionResult.FAIL;
                            }
                        }
                        this.setItem(heldItem);
                        if (!player.getAbilities().instabuild) {
                            heldItem.shrink(1);
                        }
                    }
                } else {
                    this.playSound(SoundEvents.ITEM_FRAME_ROTATE_ITEM, 1.0F, 1.0F);
                    this.setRotation(this.getRotation() + 1);
                }
                return InteractionResult.CONSUME;
            } else {
                return !hasItemInFrame && !hasItemInHand ? InteractionResult.PASS : InteractionResult.SUCCESS;
            }
        }

        @Override
        public boolean hurt(DamageSource source, float amount) {
            if (this.fixed) {
                return (source == this.damageSources().fellOutOfWorld() || (source.getEntity() instanceof Player p && p.getAbilities().instabuild)) && super.hurt(source, amount);
            } else if (this.isInvulnerableTo(source)) {
                return false;
            } else if (!source.is(net.minecraft.tags.DamageTypeTags.IS_EXPLOSION) && !this.getItem().isEmpty()) {
                if (!this.level().isClientSide()) {
                    this.dropItem(source.getEntity(), false);
                    this.playSound(SoundEvents.ITEM_FRAME_REMOVE_ITEM, 1.0F, 1.0F);
                    this.gameEvent(net.minecraft.world.level.gameevent.GameEvent.BLOCK_CHANGE, source.getEntity());
                }
                return true;
            } else {
                return super.hurt(source, amount);
            }
        }

        @Override
        public boolean isPickable() {
            return true;
        }

        @Override
        public boolean skipAttackInteraction(Entity entity) {
            if (entity instanceof Player player) {
                return !this.level().mayInteract(player, this.pos);
            }
            return false;
        }

        @Override
        public void dropItem(@Nullable Entity entity) {
            this.playSound(SoundEvents.ITEM_FRAME_BREAK, 1.0F, 1.0F);
            this.dropItem(entity, true);
        }

        private void dropItem(@Nullable Entity entity, boolean dropSelf) {
            if (!this.fixed) {
                ItemStack itemstack = this.getItem();
                this.setItem(ItemStack.EMPTY);

                if (!this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                    if (entity == null) {
                        this.removeFramedMap(itemstack);
                    }
                } else {
                    if (entity instanceof Player player) {
                        if (player.getAbilities().instabuild) {
                            this.removeFramedMap(itemstack);
                            return;
                        }
                    }

                    if (dropSelf) {
                        this.spawnAtLocation(this.getFrameItemForColor(this.getColorVariant()));
                    }

                    if (!itemstack.isEmpty()) {
                        itemstack = itemstack.copy();
                        this.removeFramedMap(itemstack);
                        if (this.random.nextFloat() < this.dropChance) {
                            this.spawnAtLocation(itemstack);
                        }
                    }
                }
            }
        }

        private void removeFramedMap(ItemStack stack) {
            if (stack.is(Items.FILLED_MAP)) {
                MapItemSavedData mapData = MapItem.getSavedData(stack, this.level());
                if (mapData != null) {
                    mapData.removedFromFrame(this.pos, this.getId());
                    mapData.setDirty(true);
                }
            }
            stack.setEntityRepresentation(null);
        }

        @Override
        public void playPlacementSound() {
            this.playSound(SoundEvents.ITEM_FRAME_PLACE, 1.0F, 1.0F);
        }

        @Override
        public boolean survives() {
            if (this.fixed) {
                return true;
            } else if (!this.level().noCollision(this)) {
                return false;
            } else {
                BlockState blockstate = this.level().getBlockState(this.pos.relative(this.direction.getOpposite()));
                return (blockstate.isSolid() || (this.direction.getAxis().isHorizontal() && DiodeBlock.isDiode(blockstate)))
                        && this.level().getEntities(this, this.getBoundingBox(), HANGING_ENTITY).isEmpty();
            }
        }

        @Override
        public void addAdditionalSaveData(CompoundTag tag) {
            super.addAdditionalSaveData(tag);
            if (!this.getItem().isEmpty()) {
                CompoundTag itemTag = Services.PLATFORM.saveSingleItemStack(this.getItem());
                tag.put("Item", itemTag);
                tag.putByte("ItemRotation", (byte) this.getRotation());
                tag.putFloat("ItemDropChance", this.dropChance);
            }
            tag.putString("ColorVariant", this.getColorVariant());
            tag.putByte("Facing", (byte) this.direction.get3DDataValue());
            tag.putBoolean("Invisible", this.isInvisible());
            tag.putBoolean("Fixed", this.fixed);

            tag.putString("BuildScapeParticlePattern", this.getParticlePattern());
            tag.putString("BuildScapeParticleColorsRaw", this.getParticleColorsRaw());

            CompoundTag persistentData = Services.PLATFORM.getEntityData(this);
            if (persistentData != null && persistentData.contains("BuildScapeFrameId")) {
                tag.putString("BuildScapeFrameId", persistentData.getString("BuildScapeFrameId"));
            }
        }

        @Override
        public void readAdditionalSaveData(CompoundTag tag) {
            super.readAdditionalSaveData(tag);

            CompoundTag itemTag = tag.getCompound("Item");
            if (itemTag != null && !itemTag.isEmpty()) {
                ItemStack itemstack = Services.PLATFORM.loadSingleItemStack(itemTag);
                if (itemstack.isEmpty()) {
                    this.setItem(ItemStack.EMPTY, false);
                } else {
                    this.setItem(itemstack, false);
                }
                this.setRotation(tag.getByte("ItemRotation"), false);
                if (tag.contains("ItemDropChance", 99)) {
                    this.dropChance = tag.getFloat("ItemDropChance");
                }
            }

            if (tag.contains("BuildScapeParticlePattern", 8)) {
                this.setParticlePattern(tag.getString("BuildScapeParticlePattern"));
            }
            if (tag.contains("BuildScapeParticleColorsRaw", 8)) {
                this.setParticleColorsRaw(tag.getString("BuildScapeParticleColorsRaw"));
            }

            CompoundTag persistentData = Services.PLATFORM.getEntityData(this);
            if (persistentData != null && tag.contains("BuildScapeFrameId", 8)) {
                persistentData.putString("BuildScapeFrameId", tag.getString("BuildScapeFrameId"));
            }

            if (tag.contains("PATTERN", 8)) {
                this.setParticlePattern(tag.getString("PATTERN"));
            }

            if (tag.contains("COLORS", 9)) {
                ListTag colorList = tag.getList("COLORS", 8);
                if (colorList.size() > 0) {
                    List<String> colors = new ArrayList<>();
                    for (int i = 0; i < colorList.size(); i++) {
                        colors.add(colorList.getString(i));
                    }
                    this.setParticleColorsRaw(String.join(";", colors));
                }
            }

            if (tag.contains("ColorVariant")) {
                this.setColorVariant(tag.getString("ColorVariant"));
            }

            this.setDirection(Direction.from3DDataValue(tag.getByte("Facing")));
            this.setInvisible(tag.getBoolean("Invisible"));
            this.fixed = tag.getBoolean("Fixed");
        }

        @Override
        public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity serverEntity) {
            return new ClientboundAddEntityPacket(this, serverEntity, this.direction.get3DDataValue());
        }

        @Override
        public void recreateFromPacket(ClientboundAddEntityPacket packet) {
            super.recreateFromPacket(packet);
            this.setDirection(Direction.from3DDataValue(packet.getData()));
        }

        @Override
        public ItemStack getPickResult() {
            ItemStack frameItem = this.getFrameItemForColor(this.getColorVariant());
            ItemStack displayedItem = this.getItem();

            Services.PLATFORM.updateCustomData(frameItem, tag -> {
                if (!displayedItem.isEmpty()) {
                    CommonId itemId = Services.PLATFORM.getItemId(displayedItem.getItem());
                    if (itemId != null) {
                        tag.putString("ITEM", itemId.toString());
                    }
                }

                CompoundTag persistentData = Services.PLATFORM.getEntityData(this);
                if (persistentData != null && persistentData.contains("BuildScapeParticlePattern", 8)) {
                    tag.putString("PATTERN", persistentData.getString("BuildScapeParticlePattern"));
                }

                if (persistentData != null && persistentData.contains("BuildScapeParticleColors", 9)) {
                    ListTag colorList = persistentData.getList("BuildScapeParticleColors", 8);
                    if (colorList.size() > 0) {
                        tag.put("COLORS", colorList.copy());
                    }
                }
            });

            return frameItem;
        }

        public int getAnalogOutput() {
            return this.getItem().isEmpty() ? 0 : this.getRotation() % 8 + 1;
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
            this(EntityType.FALLING_BLOCK, level);
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
        protected void defineSynchedData(SynchedEntityData.Builder builder) {}

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

            if (fallTime > 600 || this.getY() < this.level().getMinBuildHeight() - 64) {
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

                boolean damaged = entity.hurt(this.level().damageSources().fallingStalactite(this), damage);
                if (damaged) {
                    entity.setDeltaMovement(entity.getDeltaMovement().add(0, -0.3, 0));
                }

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
        protected void readAdditionalSaveData(CompoundTag tag) {
            this.blockState = NbtUtils.readBlockState(this.level().holderLookup(Registries.BLOCK), tag.getCompound("BlockState"));
            this.fallTime = tag.getInt("FallTime");
            if (tag.contains("StartX")) {
                this.startPos = new BlockPos(tag.getInt("StartX"), tag.getInt("StartY"), tag.getInt("StartZ"));
            }
            if (this.blockState.isAir()) {
                this.blockState = Blocks.ICE.defaultBlockState();
            }
        }

        @Override
        protected void addAdditionalSaveData(CompoundTag tag) {
            tag.put("BlockState", NbtUtils.writeBlockState(this.blockState));
            tag.putInt("FallTime", this.fallTime);
            if (this.startPos != null) {
                tag.putInt("StartX", this.startPos.getX());
                tag.putInt("StartY", this.startPos.getY());
                tag.putInt("StartZ", this.startPos.getZ());
            }
        }

        @Override
        public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity serverEntity) {
            return new ClientboundAddEntityPacket(this, serverEntity, Block.getId(this.blockState));
        }

        @Override
        public void recreateFromPacket(ClientboundAddEntityPacket packet) {
            super.recreateFromPacket(packet);
            this.blockState = Block.stateById(packet.getData());
        }

        @Override
        public boolean isAttackable() {
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
            super(EntityType.ITEM_FRAME, level, pos);
            this.colorVariant = color != null ? color : "festive";
            if (direction != null) {
                this.setDirection(direction);
                if (this.pos != null) {
                    this.recalculateBoundingBox();
                }
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
            if (direction != null) {
                this.direction = direction;
                this.recalculateBoundingBox();
            }
        }

        @Override
        protected void defineSynchedData(SynchedEntityData.Builder builder) {
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
        public void dropItem(@Nullable Entity entity) {
            this.playSound(SoundEvents.ITEM_FRAME_REMOVE_ITEM, 1.0F, 1.0F);
            if (!this.level().isClientSide()) {
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

                this.spawnAtLocation(stockingItem);

                if (!hasSilkTouch && !storedItem.isEmpty()) {
                    this.spawnAtLocation(storedItem);
                }
            }
        }

        @Override
        public void playPlacementSound() {
            this.playSound(SoundEvents.ITEM_FRAME_ADD_ITEM, 1.0F, 1.0F);
        }

        @Override
        public InteractionResult interact(Player player, InteractionHand hand) {
            ItemStack heldItem = player.getItemInHand(hand);
            ItemStack storedItem = this.getItem();

            if (heldItem.isEmpty() && player.isShiftKeyDown() && !storedItem.isEmpty()) {
                if (!this.level().isClientSide()) {
                    ItemStack toGive = storedItem.copy();
                    this.setItem(ItemStack.EMPTY);

                    if (!player.getInventory().add(toGive)) {
                        ItemEntity itemEntity = new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), toGive);
                        itemEntity.setDefaultPickUpDelay();
                        this.level().addFreshEntity(itemEntity);
                    }

                    this.playSound(SoundEvents.ITEM_FRAME_REMOVE_ITEM, 1.0F, 1.0F);
                }
                return InteractionResult.sidedSuccess(this.level().isClientSide());
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
                    return InteractionResult.sidedSuccess(this.level().isClientSide());
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
                    return InteractionResult.sidedSuccess(this.level().isClientSide());
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
            if (!stack.isEmpty()) {
                stack = stack.copy();
                stack.setEntityRepresentation(this);
            }
            this.getEntityData().set(DATA_ITEM, stack.isEmpty() ? ItemStack.EMPTY : stack);
            if (!stack.isEmpty() && update) {
                this.playSound(SoundEvents.ITEM_FRAME_ADD_ITEM, 1.0F, 1.0F);
            }
            if (update && this.pos != null) {
                this.level().updateNeighbourForOutputSignal(this.pos, Blocks.AIR);
            }
        }

        @Override
        public void addAdditionalSaveData(CompoundTag tag) {
            super.addAdditionalSaveData(tag);
            ItemStack storedItem = this.getItem();
            if (!storedItem.isEmpty()) {
                CompoundTag itemTag = Services.PLATFORM.saveSingleItemStack(storedItem);
                tag.put("Item", itemTag);
            }
            tag.putString("ColorVariant", colorVariant);
        }

        @Override
        public void readAdditionalSaveData(CompoundTag tag) {
            super.readAdditionalSaveData(tag);
            CompoundTag itemTag = tag.getCompound("Item");
            if (itemTag != null && !itemTag.isEmpty()) {
                ItemStack item = Services.PLATFORM.loadSingleItemStack(itemTag);
                if (item.isEmpty()) {
                    this.setItem(ItemStack.EMPTY, false);
                } else {
                    this.setItem(item, false);
                }
            } else {
                this.setItem(ItemStack.EMPTY, false);
            }
            if (tag.contains("ColorVariant")) {
                this.colorVariant = tag.getString("ColorVariant");
            }
        }

        @Override
        public boolean survives() {
            if (!this.level().noCollision(this)) {
                return false;
            } else {
                BlockPos blockpos = this.pos.relative(this.direction.getOpposite());
                if (!this.level().getBlockState(blockpos).isSolidRender(this.level(), blockpos)) {
                    return false;
                } else {
                    List<Entity> entities = this.level().getEntities(this, this.getBoundingBox());
                    for (Entity entity : entities) {
                        if (entity instanceof HangingEntity && entity != this) {
                            return false;
                        }
                    }
                    return true;
                }
            }
        }

        @Override
        public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity serverEntity) {
            return new ClientboundAddEntityPacket(this, serverEntity);
        }

        @Override
        public void recreateFromPacket(ClientboundAddEntityPacket packet) {
            super.recreateFromPacket(packet);
        }

        @Override
        public boolean hurt(DamageSource source, float amount) {
            if (this.isInvulnerableTo(source)) {
                return false;
            } else if (!source.is(net.minecraft.tags.DamageTypeTags.IS_EXPLOSION) && !this.getItem().isEmpty()) {
                if (!this.level().isClientSide()) {
                    this.dropItem(source.getEntity());
                    this.playSound(SoundEvents.ITEM_FRAME_REMOVE_ITEM, 1.0F, 1.0F);
                    this.setItem(ItemStack.EMPTY);
                }
                return true;
            } else {
                return super.hurt(source, amount);
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

    private static void removeUnavailableTrades(List<MerchantOffer> trades) {
        int before = trades.size();
        trades.removeIf(trade -> trade == null || trade.getResult().isEmpty());
        if (trades.size() != before) {
            com.kingodogo.buildscape.BuildscapeCommon.LOGGER.warn(
                    "Skipped {} unavailable v121x homemaker trades", before - trades.size());
        }
    }

    public static class FestiveWanderingHomemakerEntityImpl extends WanderingTrader implements FestiveWanderingHomemakerEntity {
        private int despawnDelay = 48000;

        public FestiveWanderingHomemakerEntityImpl(EntityType<? extends WanderingTrader> type, Level level) {
            super(type, level);
        }

        public FestiveWanderingHomemakerEntityImpl(Level level) {
            this(EntityType.WANDERING_TRADER, level);
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
        public void addAdditionalSaveData(CompoundTag compound) {
            super.addAdditionalSaveData(compound);
            compound.putInt("DespawnDelay", this.despawnDelay);
        }

        @Override
        public void readAdditionalSaveData(CompoundTag compound) {
            super.readAdditionalSaveData(compound);
            if (compound.contains("DespawnDelay")) {
                this.despawnDelay = compound.getInt("DespawnDelay");
            }
        }

        @Override
        protected void updateTrades() {
            MerchantOffers offers = this.getOffers();
            offers.clear();

            java.util.Random rand = new java.util.Random(this.random.nextLong());
            List<MerchantOffer> list = WanderingHomemakerTrades.getFestiveTrades(rand);
            removeUnavailableTrades(list);
            Collections.shuffle(list, rand);
            for (int j = 0; j < Math.min(6, list.size()); j++) {
                offers.add(list.get(j));
            }

            LocalDate today = LocalDate.now();
            int month = today.getMonthValue();
            int day = today.getDayOfMonth();
            int rareChance = (month == 12 && day == 25) ? 15 : 250;
            if (this.random.nextInt(rareChance) == 0) {
                MerchantOffer rare = Services.PLATFORM.createMerchantOffer(
                        new ItemStack(Items.DIAMOND, 5),
                        new ItemStack(Services.PLATFORM.getItem(new CommonId("buildscape", "frost_rose")), 1),
                        5, 1, 0.05f);
                if (!rare.getResult().isEmpty()) offers.add(rare);
                else com.kingodogo.buildscape.BuildscapeCommon.LOGGER.warn("Skipping unavailable v121x festive homemaker rare trade");
            }
        }
    }

public static class PoplarBoatEntityImpl extends Boat implements PoplarBoatEntity {

        public PoplarBoatEntityImpl(EntityType<? extends Boat> entityType, Level level) {
            super(entityType, level);
            this.setVariant(Boat.Type.OAK);
        }

        public PoplarBoatEntityImpl(Level level, double x, double y, double z) {
            this(EntityType.BOAT, level);
            this.setPos(x, y, z);
            this.xo = x;
            this.yo = y;
            this.zo = z;
            this.setVariant(Boat.Type.OAK);
        }

        @Override
        public Item getDropItem() {
            return Services.PLATFORM.getItem(new CommonId("buildscape", "poplar_boat"));
        }
    }

    public static class SeatEntityImpl extends Entity implements SeatEntity {

        public SeatEntityImpl(EntityType<?> type, Level level) {
            super(type, level);
            this.noPhysics = true;
        }

        public SeatEntityImpl(Level level, double x, double y, double z) {
            this(EntityType.MARKER, level);
            this.setPos(x, y, z);
        }

        @Override
        protected void defineSynchedData(SynchedEntityData.Builder builder) {}

        @Override
        public boolean hurt(DamageSource source, float amount) {
            return false;
        }

        @Override
        protected void readAdditionalSaveData(CompoundTag tag) {}

        @Override
        protected void addAdditionalSaveData(CompoundTag tag) {}

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
            this(EntityType.WANDERING_TRADER, level);
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
        public void addAdditionalSaveData(CompoundTag compound) {
            super.addAdditionalSaveData(compound);
            compound.putInt("DespawnDelay", this.despawnDelay);
        }

        @Override
        public void readAdditionalSaveData(CompoundTag compound) {
            super.readAdditionalSaveData(compound);
            if (compound.contains("DespawnDelay")) {
                this.despawnDelay = compound.getInt("DespawnDelay");
            }
        }

        @Override
        protected void updateTrades() {
            MerchantOffers offers = this.getOffers();
            offers.clear();

            List<MerchantOffer> list = WanderingHomemakerTrades.getStandardTrades();
            removeUnavailableTrades(list);
            java.util.Random rand = new java.util.Random(this.random.nextLong());
            Collections.shuffle(list, rand);
            for (int j = 0; j < Math.min(5, list.size()); j++) {
                offers.add(list.get(j));
            }

            if (this.random.nextInt(5000) == 0) {
                MerchantOffer rare = Services.PLATFORM.createMerchantOffer(
                        new ItemStack(Items.DIAMOND, 12),
                        new ItemStack(Services.PLATFORM.getItem(new CommonId("buildscape", "ancient_ashen_scroll")), 1),
                        1, 1, 0.0f);
                if (!rare.getResult().isEmpty()) offers.add(rare);
                else com.kingodogo.buildscape.BuildscapeCommon.LOGGER.warn("Skipping unavailable v121x homemaker rare trade");
            }
        }
    }

    @Override
    public net.minecraft.client.gui.screens.Screen createConfigScreen(net.minecraft.client.gui.screens.Screen parent) {
        return ClientAccess.createConfigScreen(parent);
    }

    @Override
    public net.minecraft.client.gui.components.Button createButton(int x, int y, int width, int height, net.minecraft.network.chat.Component message, net.minecraft.client.gui.components.Button.OnPress onPress) {
        return ClientAccess.createButton(x, y, width, height, message, onPress);
    }

    @Override
    public net.minecraft.client.gui.components.Button createCustomButton(int x, int y, int width, int height, net.minecraft.network.chat.Component message, net.minecraft.client.gui.components.Button.OnPress onPress, com.kingodogo.buildscape.client.screen.widget.CustomButtonRenderer renderer) {
        return ClientAccess.createCustomButton(x, y, width, height, message, onPress, renderer);
    }

    @Override
    public net.minecraft.client.gui.screens.Screen wrapScreen(net.minecraft.network.chat.Component title, net.minecraft.client.gui.screens.Screen parent, com.kingodogo.buildscape.client.screen.IScreenDelegate delegate) {
        return ClientAccess.wrapScreen(title, parent, delegate);
    }

    @Override
    public net.minecraft.client.gui.screens.Screen createGuiEditorScreen(net.minecraft.client.gui.screens.Screen parent, String tabName, com.kingodogo.buildscape.client.screen.AbstractConfigTab sourceTab) {
        return ClientAccess.createGuiEditorScreen(parent, tabName, sourceTab);
    }

    @Override
    public net.minecraft.client.gui.screens.Screen createInventoryItemSelectorScreen(net.minecraft.client.gui.screens.Screen parent, com.kingodogo.buildscape.client.screen.PillarItemsConfigTab configTab) {
        return ClientAccess.createInventoryItemSelectorScreen(parent, configTab);
    }

    @Override
    public void renderGuiItem(Object poseStackOrGraphics, net.minecraft.world.item.ItemStack stack, int x, int y) {
        com.kingodogo.buildscape.adapter.v121x.client.GuiProvider.renderGuiItem(poseStackOrGraphics, stack, x, y);
    }

    @Override
    public void renderGuiItemDecorations(Object poseStackOrGraphics, net.minecraft.client.gui.Font font, net.minecraft.world.item.ItemStack stack, int x, int y) {
        ClientAccess.renderGuiItemDecorations(poseStackOrGraphics, font, stack, x, y);
    }

    @Override
    public java.util.List<net.minecraft.network.chat.Component> getTooltipFromItem(net.minecraft.world.item.ItemStack stack) {
        return com.kingodogo.buildscape.adapter.v121x.client.GuiProvider.getTooltipFromItem(stack);
    }

    @Override
    public net.minecraft.client.gui.components.AbstractWidget wrapCustomWidget(int x, int y, int width, int height, net.minecraft.network.chat.Component message, com.kingodogo.buildscape.client.screen.widget.ICustomWidget customWidget) {
        return ClientAccess.wrapCustomWidget(x, y, width, height, message, customWidget);
    }
}
