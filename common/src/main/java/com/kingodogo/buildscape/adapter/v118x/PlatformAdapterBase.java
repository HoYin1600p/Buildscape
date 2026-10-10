package com.kingodogo.buildscape.adapter.v118x;
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
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionResult;
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
import com.mojang.math.Vector3f;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
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
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
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
public abstract class PlatformAdapterBase implements IPlatformAdapter {
    @Override public boolean isReplaceable(net.minecraft.world.level.block.state.BlockState state) { return state.getMaterial().isReplaceable(); }
    @Override public boolean isGlassBlock(net.minecraft.world.level.block.Block block) { return block instanceof net.minecraft.world.level.block.AbstractGlassBlock; }
    @Override
    public net.minecraft.world.level.block.RenderShape getEntityBlockRenderShape() { return net.minecraft.world.level.block.RenderShape.ENTITYBLOCK_ANIMATED; }
    @Override
    public int getClientParticleSetting() {
        return net.minecraft.client.Minecraft.getInstance().options.particles.ordinal();
    }

    @Override
    public Player getClientPlayer() {
        return net.minecraft.client.Minecraft.getInstance().player;
    }

    @Override
    public boolean hasShiftDown() {
        return net.minecraft.client.gui.screens.Screen.hasShiftDown();
    }

    @Override
    public boolean hasControlDown() {
        return net.minecraft.client.gui.screens.Screen.hasControlDown();
    }

    @Override
    public boolean widgetMouseClicked(net.minecraft.client.gui.components.AbstractWidget widget, double mouseX, double mouseY, int button) {
        return widget != null && widget.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean widgetKeyPressed(net.minecraft.client.gui.components.AbstractWidget widget, int keyCode, int scanCode, int modifiers) {
        return widget != null && widget.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean widgetCharTyped(net.minecraft.client.gui.components.AbstractWidget widget, char codePoint, int modifiers) {
        return widget != null && widget.charTyped(codePoint, modifiers);
    }

    @Override
    public void setEditBoxFilter(net.minecraft.client.gui.components.EditBox editBox, java.util.function.Predicate<String> filter) {
        editBox.setFilter(filter);
    }

    @Override
    public void enableScissor(Object graphics, int x, int y, int width, int height) {
        com.mojang.blaze3d.systems.RenderSystem.enableScissor(x, y, width, height);
    }

    @Override
    public void disableScissor(Object graphics) {
        com.mojang.blaze3d.systems.RenderSystem.disableScissor();
    }

    @Override
    public CommonId registerDynamicTexture(String name, net.minecraft.client.renderer.texture.DynamicTexture texture) {
        net.minecraft.resources.ResourceLocation id = net.minecraft.client.Minecraft.getInstance().getTextureManager().register(name, texture);
        return CommonId.of(id.getNamespace(), id.getPath());
    }

    @Override
    public net.minecraft.client.renderer.texture.DynamicTexture createDynamicTexture(String name, int width, int height, boolean clear) {
        net.minecraft.client.renderer.texture.DynamicTexture texture = new net.minecraft.client.renderer.texture.DynamicTexture(width, height, clear);
        texture.setFilter(false, false);
        return texture;
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
    @Override public void openScreen(net.minecraft.client.gui.screens.Screen screen) { net.minecraft.client.Minecraft.getInstance().setScreen(screen); }
    @Override public void openUri(java.net.URI uri) { net.minecraft.Util.getPlatform().openUri(uri); }
    @Override public void setNativeImagePixel(com.mojang.blaze3d.platform.NativeImage image, int x, int y, int abgr) { image.setPixelRGBA(x, y, abgr); }
    @Override public void beginGuiOverlayRender() { com.mojang.blaze3d.systems.RenderSystem.disableDepthTest(); com.mojang.blaze3d.systems.RenderSystem.enableBlend(); com.mojang.blaze3d.systems.RenderSystem.defaultBlendFunc(); }
    @Override public void endGuiOverlayRender() { com.mojang.blaze3d.systems.RenderSystem.disableBlend(); com.mojang.blaze3d.systems.RenderSystem.enableDepthTest(); }
    @Override public void resetShaderColor() { com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F); }
    @Override public void renderPillarMarkers(com.mojang.blaze3d.vertex.PoseStack poseStack, net.minecraft.client.Camera camera) { }
    @Override public float getWalkDistance(net.minecraft.world.entity.player.Player player) { return player.walkDist; }
    @Override public boolean isWalkAnimationMoving(net.minecraft.world.entity.player.Player player) { return player.walkDist > player.walkDistO; }
    @Override public net.minecraft.network.chat.Component parseComponentJson(String json) { return net.minecraft.network.chat.Component.Serializer.fromJson(json); }
    @Override public net.minecraft.client.KeyMapping createKeyMapping(String translationKey, int keyCode, String categoryTranslationKey) { return new net.minecraft.client.KeyMapping(translationKey, com.mojang.blaze3d.platform.InputConstants.Type.KEYSYM, keyCode, categoryTranslationKey); }
    @Override
    public boolean hasCurrentUser() {
        return net.minecraft.client.Minecraft.getInstance().getUser() != null;
    }

    @Override
    public String getCurrentUserUuid() {
        net.minecraft.client.User user = net.minecraft.client.Minecraft.getInstance().getUser();
        return user != null ? user.getUuid() : null;
    }

    @Override
    public Block getBlock(CommonId id) {
        if (id == null) return null;
        ResourceLocation rl = new ResourceLocation(id.getNamespace(), id.getPath());
        return Registry.BLOCK.get(rl);
    }

    @Override
    public CommonId getBlockId(Block block) {
        if (block == null) return null;
        ResourceLocation rl = Registry.BLOCK.getKey(block);
        return rl != null ? new CommonId(rl.getNamespace(), rl.getPath()) : null;
    }

    @Override
    public Item getItem(CommonId id) {
        if (id == null) return null;
        ResourceLocation rl = new ResourceLocation(id.getNamespace(), id.getPath());
        return Registry.ITEM.get(rl);
    }

    @Override
    public CommonId getItemId(Item item) {
        if (item == null) return null;
        ResourceLocation rl = Registry.ITEM.getKey(item);
        return rl != null ? new CommonId(rl.getNamespace(), rl.getPath()) : null;
    }

    @Override
    public int getItemRawId(Item item) {
        return item != null ? Registry.ITEM.getId(item) : -1;
    }

    @Override
    public Item getItemByRawId(int rawId) {
        return Registry.ITEM.byId(rawId);
    }

    @Override
    public Fluid getFluid(CommonId id) {
        if (id == null) return null;
        ResourceLocation rl = new ResourceLocation(id.getNamespace(), id.getPath());
        return Registry.FLUID.get(rl);
    }

    @Override
    public CommonId getFluidId(Fluid fluid) {
        if (fluid == null) return null;
        ResourceLocation rl = Registry.FLUID.getKey(fluid);
        return rl != null ? new CommonId(rl.getNamespace(), rl.getPath()) : null;
    }

    @Override
    public Fluid getBucketFluid(Item item) {
        if (item == Items.WATER_BUCKET) return Fluids.WATER;
        if (item == Items.LAVA_BUCKET) return Fluids.LAVA;
        if (item instanceof BucketItem) {
            try {
                java.lang.reflect.Field field = BucketItem.class.getDeclaredField("content");
                field.setAccessible(true);
                return (Fluid) field.get(item);
            } catch (Exception ignored) {
                try {
                    java.lang.reflect.Method method = item.getClass().getMethod("getFluid");
                    return (Fluid) method.invoke(item);
                } catch (Exception ignored2) {}
            }
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
    public EntityType<?> getMangroveBoatEntityType() {
        if (mangroveBoatType == null) {
            mangroveBoatType = EntityType.Builder.<MangroveBoatEntityImpl>of(MangroveBoatEntityImpl::new, MobCategory.MISC)
                    .sized(1.375F, 0.5625F).clientTrackingRange(10).updateInterval(3).build("mangrove_boat");
        }
        return mangroveBoatType;
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
        return entity.level;
    }

    @Override
    public Iterable<Item> getAllItems() {
        return Registry.ITEM;
    }

    @Override
    public net.minecraft.resources.ResourceKey<Registry<Item>> getItemRegistryKey() {
        return Registry.ITEM_REGISTRY;
    }

    @Override
    public <V> void register(Registry<V> registry, CommonId id, V value) {
        ResourceLocation rl = new ResourceLocation(id.getNamespace(), id.getPath());
        Registry.register(registry, rl, value);
    }

    @Override
    public net.minecraft.stats.Stat<?> registerCustomStat(String registryName, CommonId statId) {
        ResourceLocation value = new ResourceLocation(statId.getNamespace(), statId.getPath());
        Registry.register(Registry.CUSTOM_STAT, registryName, value);
        return net.minecraft.stats.Stats.CUSTOM.get(value, net.minecraft.stats.StatFormatter.DEFAULT);
    }

    @Override
    public void registerParticleType(CommonId id, ParticleType<?> type) {
        ResourceLocation rl = new ResourceLocation(id.getNamespace(), id.getPath());
        Registry.register(Registry.PARTICLE_TYPE, rl, type);
    }


    @Override
    public void registerParticleProviders() {
        if (!isClient()) return;
        com.kingodogo.buildscape.adapter.v118x.ParticleFactory.registerProviders();
    }

    @Override
    public void registerCopperFireFlameParticle() {
        registerParticleProviders();
    }

    @Override
    public void rotateX(com.mojang.blaze3d.vertex.PoseStack poseStack, float degrees) {
        poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(degrees));
    }

    @Override
    public void rotateY(com.mojang.blaze3d.vertex.PoseStack poseStack, float degrees) {
        poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(degrees));
    }

    @Override
    public void rotateZ(com.mojang.blaze3d.vertex.PoseStack poseStack, float degrees) {
        poseStack.mulPose(com.mojang.math.Vector3f.ZP.rotationDegrees(degrees));
    }

    @Override
    public void fill(Object poseStackOrGraphics, int minX, int minY, int maxX, int maxY, int color) {
        if (poseStackOrGraphics instanceof com.mojang.blaze3d.vertex.PoseStack ps) {
            net.minecraft.client.gui.GuiComponent.fill(ps, minX, minY, maxX, maxY, color);
        }
    }

    @Override
    public void drawShadow(Object poseStackOrGraphics, net.minecraft.client.gui.Font font, String text, int x, int y, int color) {
        if (poseStackOrGraphics instanceof com.mojang.blaze3d.vertex.PoseStack ps) {
            font.drawShadow(ps, text, x, y, color);
        }
    }

    @Override
    public void draw(Object poseStackOrGraphics, net.minecraft.client.gui.Font font, String text, float x, float y, int color) {
        if (poseStackOrGraphics instanceof com.mojang.blaze3d.vertex.PoseStack ps) {
            font.draw(ps, text, x, y, color);
        }
    }

    @Override
    public void drawCenteredString(Object poseStackOrGraphics, net.minecraft.client.gui.Font font, net.minecraft.network.chat.Component component, int x, int y, int color) {
        if (poseStackOrGraphics instanceof com.mojang.blaze3d.vertex.PoseStack ps) {
            net.minecraft.client.gui.GuiComponent.drawCenteredString(ps, font, component, x, y, color);
        }
    }

    @Override
    public void drawCenteredString(Object poseStackOrGraphics, net.minecraft.client.gui.Font font, String text, int x, int y, int color) {
        if (poseStackOrGraphics instanceof com.mojang.blaze3d.vertex.PoseStack ps) {
            net.minecraft.client.gui.GuiComponent.drawCenteredString(ps, font, text, x, y, color);
        }
    }

    @Override
    public void bindTexture(CommonId id) {
        if (id != null) {
            com.mojang.blaze3d.systems.RenderSystem.setShaderTexture(0, new ResourceLocation(id.getNamespace(), id.getPath()));
        }
    }

    @Override
    public void blit(Object poseStackOrGraphics, CommonId texture, int x, int y, float u, float v, int width, int height, int sheetW, int sheetH) {
        if (poseStackOrGraphics instanceof com.mojang.blaze3d.vertex.PoseStack ps && texture != null) {
            bindTexture(texture);
            net.minecraft.client.gui.GuiComponent.blit(ps, x, y, u, v, width, height, sheetW, sheetH);
        }
    }

    @Override
    public void blit(Object poseStackOrGraphics, CommonId texture, int x, int y, int width, int height, float u, float v, int uWidth, int vHeight, int sheetW, int sheetH) {
        if (poseStackOrGraphics instanceof com.mojang.blaze3d.vertex.PoseStack ps && texture != null) {
            bindTexture(texture);
            net.minecraft.client.gui.GuiComponent.blit(ps, x, y, width, height, u, v, uWidth, vHeight, sheetW, sheetH);
        }
    }

    @Override
    public void renderComponentTooltip(Object poseStackOrGraphics, net.minecraft.client.gui.Font font, java.util.List<net.minecraft.network.chat.Component> components, int mouseX, int mouseY) {
        if (poseStackOrGraphics instanceof com.mojang.blaze3d.vertex.PoseStack ps) {
            net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
            if (mc.screen != null) {
                mc.screen.renderComponentTooltip(ps, components, mouseX, mouseY);
            }
        }
    }

    @Override
    public com.mojang.blaze3d.vertex.PoseStack toPoseStack(Object poseStackOrGraphics) {
        return com.kingodogo.buildscape.adapter.v118x.GuiProvider.toPoseStack(poseStackOrGraphics);
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
        try {
            net.minecraft.data.BuiltinRegistries.register(
                    net.minecraft.data.BuiltinRegistries.CONFIGURED_FEATURE,
                    new net.minecraft.resources.ResourceLocation(id.getNamespace(), id.getPath()),
                    (net.minecraft.world.level.levelgen.feature.ConfiguredFeature<?, ?>) configuredFeature
            );
        } catch (Throwable ignored) {}
    }

    @Override public void pushGuiPose(Object context) { if (context instanceof com.mojang.blaze3d.vertex.PoseStack pose) pose.pushPose(); }
    @Override public void popGuiPose(Object context) { if (context instanceof com.mojang.blaze3d.vertex.PoseStack pose) pose.popPose(); }
    @Override public void translateGuiPose(Object context, float x, float y, float z) { if (context instanceof com.mojang.blaze3d.vertex.PoseStack pose) pose.translate(x, y, z); }
    @Override public void scaleGuiPose(Object context, float x, float y) { if (context instanceof com.mojang.blaze3d.vertex.PoseStack pose) pose.scale(x, y, 1.0f); }
    @Override public void renderClientOverlay(Object context, int width, int height) { if (context instanceof com.mojang.blaze3d.vertex.PoseStack pose) com.kingodogo.buildscape.client.ClientEvents.renderOverlay(pose, width, height); }

    @Override
    public void renderWidget(Object poseStackOrGraphics, net.minecraft.client.gui.components.AbstractWidget widget, int mouseX, int mouseY, float partialTick) {
        com.kingodogo.buildscape.adapter.v118x.GuiProvider.renderWidget(poseStackOrGraphics, widget, mouseX, mouseY, partialTick);
    }

    @Override
    public CommonId getEntityTypeId(net.minecraft.world.entity.EntityType<?> entityType) {
        ResourceLocation key = net.minecraft.core.Registry.ENTITY_TYPE.getKey(entityType);
        return key != null ? new CommonId(key.getNamespace(), key.getPath()) : new CommonId("minecraft", "pig");
    }

    @Override
    public boolean hasCustomHoverName(ItemStack stack) {
        return stack != null && stack.hasCustomHoverName();
    }

    @Override
    public net.minecraft.world.entity.EquipmentSlot getEquipmentSlot(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return null;
        return net.minecraft.world.entity.LivingEntity.getEquipmentSlotForItem(stack);
    }

    private final java.util.Map<net.minecraft.client.resources.model.BakedModel, net.minecraft.world.phys.AABB> v118xModelBoundsCache = new java.util.WeakHashMap<>();

    @Override
    public net.minecraft.world.phys.AABB getModelBounds(Object model) {
        if (!(model instanceof net.minecraft.client.resources.model.BakedModel bakedModel)) return new net.minecraft.world.phys.AABB(0, 0, 0, 1, 1, 1);
        return v118xModelBoundsCache.computeIfAbsent(bakedModel, m -> {
            double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE, minZ = Double.MAX_VALUE;
            double maxX = -Double.MAX_VALUE, maxY = -Double.MAX_VALUE, maxZ = -Double.MAX_VALUE;
            java.util.Random rand = new java.util.Random(42L);
            for (net.minecraft.core.Direction dir : new net.minecraft.core.Direction[]{null, net.minecraft.core.Direction.DOWN, net.minecraft.core.Direction.UP, net.minecraft.core.Direction.NORTH, net.minecraft.core.Direction.SOUTH, net.minecraft.core.Direction.WEST, net.minecraft.core.Direction.EAST}) {
                java.util.List<net.minecraft.client.renderer.block.model.BakedQuad> quads = m.getQuads(null, dir, rand);
                for (net.minecraft.client.renderer.block.model.BakedQuad quad : quads) {
                    int[] vertices = quad.getVertices();
                    int step = vertices.length / 4;
                    for (int i = 0; i < 4; i++) {
                        float x = Float.intBitsToFloat(vertices[i * step]);
                        float y = Float.intBitsToFloat(vertices[i * step + 1]);
                        float z = Float.intBitsToFloat(vertices[i * step + 2]);
                        if (x < minX) minX = x;
                        if (y < minY) minY = y;
                        if (z < minZ) minZ = z;
                        if (x > maxX) maxX = x;
                        if (y > maxY) maxY = y;
                        if (z > maxZ) maxZ = z;
                    }
                }
            }
            if (minX == Double.MAX_VALUE) return new net.minecraft.world.phys.AABB(0, 0, 0, 1, 1, 1);
            return new net.minecraft.world.phys.AABB(minX, minY, minZ, maxX, maxY, maxZ);
        });
    }

    @Override
    public void renderItemFixed(ItemStack stack, com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource, int combinedLight, int combinedOverlay, Object model) {
        if (bufferSource instanceof net.minecraft.client.renderer.MultiBufferSource mbs && model instanceof net.minecraft.client.resources.model.BakedModel bm) {
            net.minecraft.client.Minecraft.getInstance().getItemRenderer().render(
                    stack, net.minecraft.client.renderer.block.model.ItemTransforms.TransformType.FIXED, stack.hasFoil(), poseStack, mbs, combinedLight, combinedOverlay, bm);
        }
    }

    @Override
    public void renderItemStatic(ItemStack stack, com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource, int combinedLight, int combinedOverlay, int seed) {
        if (bufferSource instanceof net.minecraft.client.renderer.MultiBufferSource mbs) {
            net.minecraft.client.Minecraft.getInstance().getItemRenderer().renderStatic(
                    stack, net.minecraft.client.renderer.block.model.ItemTransforms.TransformType.NONE, combinedLight, combinedOverlay, poseStack, mbs, seed);
        }
    }

    @Override
    public void registerRecipeSerializers() {
        RecipeFactory.register();
    }

    @Override
    public byte[] readResourceBytes(net.minecraft.server.packs.resources.ResourceManager resourceManager, CommonId location) {
        if (resourceManager == null || location == null) return null;
        try {
            net.minecraft.resources.ResourceLocation rl = new net.minecraft.resources.ResourceLocation(location.getNamespace(), location.getPath());
            if (resourceManager.hasResource(rl)) {
                net.minecraft.server.packs.resources.Resource res = resourceManager.getResource(rl);
                try (java.io.InputStream stream = res.getInputStream()) {
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
        return new CreativeModeTab(CreativeModeTab.TABS.length, titleKey) {
            @Override
            public ItemStack makeIcon() {
                return iconSupplier.get();
            }
        };
    }

    @Override
    public boolean supportsBiomeBrushEnchantment(ItemStack stack, Object enchantment) {
        if (enchantment instanceof net.minecraft.world.item.enchantment.Enchantment e) {
            return e == net.minecraft.world.item.enchantment.Enchantments.UNBREAKING
                    || e == net.minecraft.world.item.enchantment.Enchantments.MENDING;
        }
        return false;
    }

    @Override
    public Item createBiomeBrushItem(com.kingodogo.buildscape.item.BiomeBrushItem.BiomeBrushTier tier, Item.Properties properties) {
        return new com.kingodogo.buildscape.item.BiomeBrushItem(tier, properties) {
            public boolean canApplyAtEnchantingTable(ItemStack stack, net.minecraft.world.item.enchantment.Enchantment enchantment) {
                return supportsBiomeBrushEnchantment(stack, enchantment);
            }

            @Override
            public void appendHoverText(ItemStack stack, @org.jetbrains.annotations.Nullable Level level, List<Component> tooltip, net.minecraft.world.item.TooltipFlag flag) {
                super.appendHoverText(stack, level, tooltip, flag);
                addHoverText(stack, tooltip::add);
            }
        };
    }

    @Override
    public net.minecraft.world.item.BlockItem createTrophyBlockItem(com.kingodogo.buildscape.trophy.TrophyBlock block, com.kingodogo.buildscape.trophy.TrophyDefinition definition, Item.Properties properties) {
        return new com.kingodogo.buildscape.trophy.TrophyBlockItem(block, definition, properties) {
            @Override
            public void appendHoverText(ItemStack stack, @org.jetbrains.annotations.Nullable Level level, List<Component> tooltip, net.minecraft.world.item.TooltipFlag flag) {
                super.appendHoverText(stack, level, tooltip, flag);
                addHoverText(stack, tooltip::add);
            }
        };
    }

    @Override
    public com.kingodogo.buildscape.trophy.TrophyBlock createTrophyBlock(com.kingodogo.buildscape.trophy.TrophyDefinition definition) {
        return new com.kingodogo.buildscape.trophy.TrophyBlock(definition, BlockBehaviour.Properties.copy(Blocks.STONE)
                .strength(definition.getHardness(), definition.getResistance())
                .sound(definition.getSoundType())
                .lightLevel(state -> definition.getLightEmission())
                .emissiveRendering((state, getter, pos) -> true)
                .noOcclusion()) {
            @Override
            public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
                return createTrophyStack(level.getBlockEntity(pos));
            }

            @Override
            public List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootContext.Builder builder) {
                return java.util.Collections.singletonList(createTrophyStack(builder.getOptionalParameter(net.minecraft.world.level.storage.loot.parameters.LootContextParams.BLOCK_ENTITY)));
            }
        };
    }

    @Override
    public SoundEvent createSoundEvent(CommonId id) {
        ResourceLocation rl = new ResourceLocation(id.getNamespace(), id.getPath());
        return new SoundEvent(rl);
    }

    @Override
    public void playBlockSound(Level level, net.minecraft.core.BlockPos pos, CommonId soundId) {
        ResourceLocation rl = new ResourceLocation(soundId.getNamespace(), soundId.getPath());
        SoundEvent se = Registry.SOUND_EVENT.get(rl);
        if (se != null) {
            level.playSound(null, pos, se, net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }

    @Override
    public void neighborChanged(Level level, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state, Block fromBlock, net.minecraft.core.BlockPos fromPos) {
        level.neighborChanged(pos, fromBlock, fromPos);
    }

    @Override
    public void hurtAndBreak(ItemStack stack, int amount, LivingEntity entity, InteractionHand hand) {
        EquipmentSlot slot = hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
        stack.hurtAndBreak(amount, entity, e -> e.broadcastBreakEvent(slot));
    }

    @Override
    public void sendActionBarMessage(Player player, Component message) {
        player.displayClientMessage(message, true);
    }

    @Override
    public void sendSystemMessage(Player player, Component message) {
        player.sendMessage(message, net.minecraft.Util.NIL_UUID);
    }

    @Override
    public void sendCommandSuccess(net.minecraft.commands.CommandSourceStack source, String message, boolean broadcastToOps) {
        source.sendSuccess(new TextComponent(message), broadcastToOps);
    }

    @Override
    public void sendCommandFailure(net.minecraft.commands.CommandSourceStack source, String message) {
        source.sendFailure(new TextComponent(message));
    }

    @Override
    public MutableComponent literal(String text) {
        return new TextComponent(text);
    }

    @Override
    public MutableComponent translatable(String key) {
        return new TranslatableComponent(key);
    }

    @Override
    public void setItemCustomName(ItemStack stack, Component name) {
        stack.setHoverName(name);
    }

    @Override
    public String getItemCustomName(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return null;
        if (stack.hasCustomHoverName()) {
            return stack.getHoverName().getString();
        }
        return null;
    }

    @Override
    public MutableComponent translatable(String key, Object... args) {
        return new TranslatableComponent(key, args);
    }

    @Override
    public void playButtonClick(Level level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.UI_BUTTON_CLICK, SoundSource.BLOCKS, 0.5f, 1.0f);
    }

    @Override
    public void playButtonClick(Level level, BlockPos pos, float pitch) {
        level.playSound(null, pos, SoundEvents.UI_BUTTON_CLICK, SoundSource.BLOCKS, 0.5f, pitch);
    }

    @Override
    public void playNoteHarp(Level level, BlockPos pos, float pitch) {
        level.playSound(null, pos, SoundEvents.NOTE_BLOCK_HARP, SoundSource.RECORDS, 3.0F, pitch);
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
        return color != null ? color.getFireworkColor() : 0xFFFFFF;
    }

    @Override
    public void syncChunk(ServerLevel level, LevelChunk chunk) {
        ClientboundLevelChunkWithLightPacket packet =
                new ClientboundLevelChunkWithLightPacket(chunk, level.getLightEngine(), null, null, true);
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
    public void setSectionBiome(LevelChunkSection section, int localQx, int localQy, int localQz, Holder<Biome> biome) {
        PalettedContainer<Holder<Biome>> container = section.getBiomes();
        container.set(localQx, localQy, localQz, biome);
    }

    @Override
    public CommonId getBiomeId(Level level, Holder<Biome> biomeHolder) {
        if (biomeHolder == null) return null;
        ResourceLocation rl = level.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).getKey(biomeHolder.value());
        return rl != null ? new CommonId(rl.getNamespace(), rl.getPath()) : null;
    }

    @Override
    public Holder<Biome> getBiomeHolder(ServerLevel level, CommonId id) {
        if (id == null) return null;
        ResourceLocation rl = new ResourceLocation(id.getNamespace(), id.getPath());
        ResourceKey<Biome> key = ResourceKey.create(Registry.BIOME_REGISTRY, rl);
        return level.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).getHolder(key).orElse(null);
    }

    @Override
    public int getUnbreakingLevel(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return 0;
        return EnchantmentHelper.getItemEnchantmentLevel(Enchantments.UNBREAKING, stack);
    }

    @Override
    public int getEfficiencyLevel(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return 0;
        return EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY, stack);
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
        return EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, stack) > 0;
    }

    @Override
    public void awardAdvancement(ServerPlayer player, CommonId advancementId, String criterion) {
        ResourceLocation id = new ResourceLocation(advancementId.getNamespace(), advancementId.getPath());
        net.minecraft.advancements.Advancement adv = player.getServer().getAdvancements().getAdvancement(id);
        if (adv != null) {
            player.getAdvancements().award(adv, criterion);
        }
    }

    @Override
    public void awardStat(ServerPlayer player, CommonId statId) {
        if (player == null || statId == null) return;
        ResourceLocation id = new ResourceLocation(statId.getNamespace(), statId.getPath());
        net.minecraft.stats.Stat<ResourceLocation> stat = net.minecraft.stats.Stats.CUSTOM.get(id);
        if (stat != null) {
            player.awardStat(stat);
        }
    }

    @Override
    public CompoundTag getCustomData(ItemStack stack, boolean create) {
        return create ? stack.getOrCreateTag() : stack.getTag();
    }

    @Override
    public void updateCustomData(ItemStack stack, Consumer<CompoundTag> updater) {
        updater.accept(stack.getOrCreateTag());
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
        CompoundTag explosion = stack.getOrCreateTagElement("Explosion");
        explosion.putByte("Type", shapeId);
        explosion.putIntArray("Colors", colors);
        explosion.putBoolean("Flicker", flicker);
        explosion.putBoolean("Trail", trail);
    }

    @Override
    public void configureFireworkRocket(ItemStack stack, int flight, byte shapeId, int[] colors, boolean flicker, boolean trail) {
        CompoundTag fireworks = stack.getOrCreateTagElement("Fireworks");
        net.minecraft.nbt.ListTag explosions = new net.minecraft.nbt.ListTag();
        CompoundTag explosion = new CompoundTag();
        explosion.putByte("Type", shapeId);
        explosion.putIntArray("Colors", colors);
        explosion.putBoolean("Flicker", flicker);
        explosion.putBoolean("Trail", trail);
        explosions.add(explosion);
        fireworks.put("Explosions", explosions);
        fireworks.putByte("Flight", (byte) Math.max(0, Math.min(3, flight)));
    }

    @Override
    public int[] getFireworkStarColors(ItemStack stack) {
        CompoundTag explosion = stack.getTagElement("Explosion");
        return explosion != null && explosion.contains("Colors") ? explosion.getIntArray("Colors") : new int[0];
    }

    @Override
    public int getDyeFireworkColor(ItemStack stack) {
        return stack.getItem() instanceof net.minecraft.world.item.DyeItem dye ? dye.getDyeColor().getFireworkColor() : 0;
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
            ResourceLocation id = new ResourceLocation(tagId.getNamespace(), tagId.getPath());
            return stack.is(net.minecraft.tags.TagKey.create(Registry.ITEM_REGISTRY, id));
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isBlockInTag(Block block, CommonId tagId) {
        if (block == null || tagId == null) return false;
        try {
            ResourceLocation id = new ResourceLocation(tagId.getNamespace(), tagId.getPath());
            return block.defaultBlockState().is(net.minecraft.tags.TagKey.create(Registry.BLOCK_REGISTRY, id));
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isEntityTypeInTag(Entity entity, CommonId tagId) {
        if (entity == null || tagId == null) return false;
        try {
            ResourceLocation id = new ResourceLocation(tagId.getNamespace(), tagId.getPath());
            return entity.getType().is(net.minecraft.tags.TagKey.create(Registry.ENTITY_TYPE_REGISTRY, id));
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
        return new net.minecraft.world.item.trading.MerchantOffer(cost, result, maxUses, xp, priceMultiplier);
    }

    @Override
    public net.minecraft.world.item.crafting.Ingredient createTagIngredient(CommonId id) {
        return net.minecraft.world.item.crafting.Ingredient.of(net.minecraft.tags.TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(id.getNamespace(), id.getPath())));
    }

    @Override
    public net.minecraft.tags.TagKey<net.minecraft.world.item.Item> createItemTagKey(CommonId id) {
        return net.minecraft.tags.TagKey.create(net.minecraft.core.Registry.ITEM_REGISTRY,
                new net.minecraft.resources.ResourceLocation(id.getNamespace(), id.getPath()));
    }

    @Override
    public boolean isGlassBaseBlock(Block block) {
        return block instanceof net.minecraft.world.level.block.AbstractGlassBlock;
    }

    @Override
    public boolean isEnchantingTableBlock(Block block) {
        return block instanceof net.minecraft.world.level.block.EnchantmentTableBlock;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack != null && !stack.isEmpty() && stack.isEdible();
    }

    @Override
    public boolean isWaterPotion(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        if (stack.is(Items.WATER_BUCKET)) return true;
        return stack.getItem() instanceof net.minecraft.world.item.PotionItem && PotionUtils.getPotion(stack) == Potions.WATER;
    }

    @Override
    public boolean isEnchantedItem(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        if (stack.isEnchanted() || stack.hasFoil()) return true;
        if (!net.minecraft.world.item.enchantment.EnchantmentHelper.getEnchantments(stack).isEmpty()) return true;
        return stack.getItem() instanceof net.minecraft.world.item.EnchantedBookItem
                && !net.minecraft.world.item.EnchantedBookItem.getEnchantments(stack).isEmpty();
    }

    @Override
    public ItemStack createWaterPotion() {
        return PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER);
    }

    @Override
    public void applyFoodEffects(Player player, Level level, ItemStack foodStack) {
        if (player == null || foodStack == null || foodStack.isEmpty()) return;
        foodStack.getItem().finishUsingItem(foodStack, level, player);
    }

    @Override
    public void applyPotionEffects(LivingEntity entity, ItemStack potionStack) {
        if (entity == null || potionStack == null || potionStack.isEmpty()) return;
        PotionUtils.getMobEffects(potionStack).forEach(effect -> entity.addEffect(new MobEffectInstance(effect)));
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
        return net.minecraft.nbt.NbtIo.readCompressed(file);
    }

    @Override
    public void writeCompressedTag(java.io.File file, CompoundTag tag) throws java.io.IOException {
        net.minecraft.nbt.NbtIo.writeCompressed(tag, file);
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
            entity.hurt(DamageSource.FREEZE, amount);
        }
    }

    @Override
    public int getSelectedSlot(Inventory inventory) {
        return inventory != null ? inventory.selected : 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(BlockEntityFactory<T> factory, Predicate<BlockState> isValid) {
        try {
            Class<?> supplierClass = Class.forName("net.minecraft.world.level.block.entity.BlockEntityType$BlockEntitySupplier");
            Object proxy = java.lang.reflect.Proxy.newProxyInstance(
                    BlockEntityType.class.getClassLoader(),
                    new Class<?>[]{supplierClass},
                    (proxyObj, method, args) -> {
                        if ("create".equals(method.getName())) {
                            return factory.create((BlockPos) args[0], (BlockState) args[1]);
                        }
                        return null;
                    }
            );
            Set<Block> validBlocks = new java.util.AbstractSet<>() {
                @Override
                public boolean contains(Object o) {
                    if (o instanceof Block b) {
                        return isValid.test(b.defaultBlockState());
                    }
                    return false;
                }

                @Override
                public java.util.Iterator<Block> iterator() {
                    return java.util.Collections.emptyIterator();
                }

                @Override
                public int size() {
                    return 0;
                }
            };
            java.lang.reflect.Constructor<BlockEntityType> constructor = BlockEntityType.class.getDeclaredConstructor(supplierClass, Set.class, com.mojang.datafixers.types.Type.class);
            constructor.setAccessible(true);
            return (BlockEntityType<T>) constructor.newInstance(proxy, validBlocks, null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create BlockEntityType for 1.18.2", e);
        }
    }

    @Override
    public boolean isOnGround(Entity entity) {
        return entity.isOnGround();
    }

    @Override
    public void spawnMobFromEgg(ServerLevel level, BlockPos pos, ItemStack eggStack) {
        if (eggStack.getItem() instanceof SpawnEggItem spawnEgg) {
            EntityType<?> entityType = spawnEgg.getType(eggStack.getTag());
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
            public void appendHoverText(ItemStack stack, @org.jetbrains.annotations.Nullable Level level, List<Component> tooltip, net.minecraft.world.item.TooltipFlag flag) {
                com.kingodogo.buildscape.item.BuildersPouchItem.appendBuildersPouchTooltip(stack, tooltip::add);
                super.appendHoverText(stack, level, tooltip, flag);
            }
        };
    }

    @Override
    public boolean supportsHammerEnchantment(ItemStack stack, Object enchantment) {
        if (enchantment instanceof net.minecraft.world.item.enchantment.Enchantment e) {
            return e == net.minecraft.world.item.enchantment.Enchantments.UNBREAKING
                    || e == net.minecraft.world.item.enchantment.Enchantments.MENDING
                    || e == net.minecraft.world.item.enchantment.Enchantments.SILK_TOUCH;
        }
        return false;
    }

    @Override
    public Item createHammerItem(com.kingodogo.buildscape.item.HammerItem.HammerTier tier, Item.Properties properties) {
        return new com.kingodogo.buildscape.item.HammerItem(tier, properties) {
            public boolean canApplyAtEnchantingTable(ItemStack stack, net.minecraft.world.item.enchantment.Enchantment enchantment) {
                return supportsHammerEnchantment(stack, enchantment);
            }

            @Override
            public void appendHoverText(ItemStack stack, @org.jetbrains.annotations.Nullable Level level, List<Component> tooltip, net.minecraft.world.item.TooltipFlag flag) {
                appendHammerTooltip(tooltip::add);
                super.appendHoverText(stack, level, tooltip, flag);
            }

            @Override
            public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
                ItemStack stack = player.getItemInHand(hand);
                if (hand == InteractionHand.MAIN_HAND) {
                    ItemStack offHand = player.getOffhandItem();
                    if (!offHand.isEmpty() && offHand.getItem() instanceof net.minecraft.world.item.BlockItem) {
                        return InteractionResultHolder.success(stack);
                    }
                }
                return InteractionResultHolder.pass(stack);
            }
        };
    }

    @Override
    public void playNoteBlockChime(Level level, BlockPos pos, float pitch) {
        level.playSound(null, pos, SoundEvents.NOTE_BLOCK_CHIME, SoundSource.PLAYERS, 1.0f, pitch);
    }

    private static final java.util.Map<java.util.UUID, CompoundTag> V118X_ENTITY_DATA = new java.util.concurrent.ConcurrentHashMap<>();
    private static final java.util.Map<net.minecraft.world.level.block.entity.BlockEntity, CompoundTag> V118X_BLOCK_ENTITY_DATA = new java.util.concurrent.ConcurrentHashMap<>();

    @Override
    public CompoundTag getEntityData(Entity entity) {
        if (entity == null) return new CompoundTag();
        return V118X_ENTITY_DATA.computeIfAbsent(entity.getUUID(), k -> new CompoundTag());
    }

    @Override
    public CompoundTag getBlockEntityData(net.minecraft.world.level.block.entity.BlockEntity be) {
        if (be == null) return new CompoundTag();
        return V118X_BLOCK_ENTITY_DATA.computeIfAbsent(be, k -> new CompoundTag());
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
        if (level == null) return BlockPos.ZERO;
        return new BlockPos(level.getLevelData().getXSpawn(), level.getLevelData().getYSpawn(), level.getLevelData().getZSpawn());
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
            public void appendHoverText(ItemStack stack, @org.jetbrains.annotations.Nullable Level level, List<Component> tooltip, net.minecraft.world.item.TooltipFlag flag) {
                com.kingodogo.buildscape.item.ConfettiItem.appendConfettiTooltip(stack, tooltip::add);
                super.appendHoverText(stack, level, tooltip, flag);
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
            public void appendHoverText(ItemStack stack, @org.jetbrains.annotations.Nullable Level level, List<Component> tooltip, net.minecraft.world.item.TooltipFlag flag) {
                appendBuildscapeTooltip(tooltip::add);
                super.appendHoverText(stack, level, tooltip, flag);
            }
        };
    }

    @Override
    public Item createWrenchItem(Item.Properties properties) {
        return new com.kingodogo.buildscape.item.WrenchItem(properties) {
            @Override public void appendHoverText(ItemStack stack, @org.jetbrains.annotations.Nullable Level level, List<Component> tooltip, net.minecraft.world.item.TooltipFlag flag) {
                appendBuildscapeTooltip(tooltip::add);
                super.appendHoverText(stack, level, tooltip, flag);
            }
        };
    }

    @Override
    public Item createFestiveGlintShardItem(Item.Properties properties) {
        return new com.kingodogo.buildscape.item.FestiveGlintShardItem(properties) {
            @Override public void appendHoverText(ItemStack stack, @org.jetbrains.annotations.Nullable Level level, List<Component> tooltip, net.minecraft.world.item.TooltipFlag flag) {
                appendBuildscapeTooltip(tooltip::add);
            }
        };
    }

    @Override
    public Item createBigOrnamentTemplateItem(Item.Properties properties) {
        return new com.kingodogo.buildscape.item.BigOrnamentTemplateItem(properties) {
            @Override public void appendHoverText(ItemStack stack, @org.jetbrains.annotations.Nullable Level level, List<Component> tooltip, net.minecraft.world.item.TooltipFlag flag) {
                super.appendHoverText(stack, level, tooltip, flag);
                appendBuildscapeTooltip(tooltip::add);
            }
        };
    }

    @Override
    public Item createStringlightFrameItem(Item.Properties properties) {
        return new com.kingodogo.buildscape.item.StringlightFrameItem(properties) {
            @Override public void appendHoverText(ItemStack stack, @org.jetbrains.annotations.Nullable Level level, List<Component> tooltip, net.minecraft.world.item.TooltipFlag flag) {
                appendBuildscapeTooltip(tooltip::add);
                super.appendHoverText(stack, level, tooltip, flag);
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
        net.minecraft.world.ContainerHelper.loadAllItems(tag, items);
    }

    @Override
    public void saveAllItems(CompoundTag tag, NonNullList<ItemStack> items) {
        net.minecraft.world.ContainerHelper.saveAllItems(tag, items);
    }

    @Override
    public ItemStack loadSingleItemStack(CompoundTag tag) {
        if (tag == null || tag.isEmpty()) return ItemStack.EMPTY;
        return ItemStack.of(tag);
    }

    @Override
    public CompoundTag saveSingleItemStack(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return new CompoundTag();
        return stack.save(new CompoundTag());
    }

    @Override
    public Item createPatternItem(Item.Properties properties, String tooltipKey) {
        return new com.kingodogo.buildscape.item.PatternItem(properties, tooltipKey) {
            @Override
            public void appendHoverText(ItemStack stack, @org.jetbrains.annotations.Nullable Level level, List<Component> tooltip, net.minecraft.world.item.TooltipFlag flag) {
                super.appendHoverText(stack, level, tooltip, flag);
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
            public void appendHoverText(ItemStack stack, @org.jetbrains.annotations.Nullable Level level, List<Component> tooltip, net.minecraft.world.item.TooltipFlag flag) {
                super.appendHoverText(stack, level, tooltip, flag);
                com.kingodogo.buildscape.item.GoldenJarItem.addAdvancementTooltip(stack, tooltip::add);
            }
        };
    }

    @Override
    public BlockItem createFestiveStarItem(Block block, Item.Properties properties) {
        return new com.kingodogo.buildscape.item.FestiveStarItem(block, properties) {
            @Override
            public void appendHoverText(ItemStack stack, @org.jetbrains.annotations.Nullable Level level, List<Component> tooltip, net.minecraft.world.item.TooltipFlag flag) {
                super.appendHoverText(stack, level, tooltip, flag);
                com.kingodogo.buildscape.item.FestiveStarItem.appendFestiveStarTooltip(stack, tooltip::add);
            }
        };
    }

    @Override
    public BlockItem createMuffBlockItem(Block block, Item.Properties properties) {
        return new com.kingodogo.buildscape.item.MuffBlockItem(block, properties) {
            @Override
            public void appendHoverText(ItemStack stack, @org.jetbrains.annotations.Nullable Level level, List<Component> tooltip, net.minecraft.world.item.TooltipFlag flag) {
                com.kingodogo.buildscape.item.MuffBlockItem.appendMuffBlockTooltip(tooltip::add);
                super.appendHoverText(stack, level, tooltip, flag);
            }
        };
    }

    @Override
    public BlockItem createMistBlockItem(Block block, Item.Properties properties) {
        return new com.kingodogo.buildscape.item.MistBlockItem(block, properties) {
            @Override
            public void appendHoverText(ItemStack stack, @org.jetbrains.annotations.Nullable Level level, List<Component> tooltip, net.minecraft.world.item.TooltipFlag flag) {
                appendMistBlockTooltip(tooltip::add);
                super.appendHoverText(stack, level, tooltip, flag);
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
            public void appendHoverText(ItemStack stack, @org.jetbrains.annotations.Nullable Level level, List<Component> tooltip, net.minecraft.world.item.TooltipFlag flag) {
                super.appendHoverText(stack, level, tooltip, flag);
                com.kingodogo.buildscape.item.InfinitePhoenixFireworkStarItem.appendInfiniteUsesTooltip(tooltip::add);
            }
        };
    }

    @Override
    public Item createColoredItemFrameItem(Item.Properties properties, String colorVariant) {
        return new com.kingodogo.buildscape.item.ColoredItemFrameItem(properties, colorVariant) {
            @Override
            public void appendHoverText(ItemStack stack, @org.jetbrains.annotations.Nullable Level level, List<Component> tooltip, net.minecraft.world.item.TooltipFlag flag) {
                appendColoredItemFrameTooltip(stack, tooltip::add);
                super.appendHoverText(stack, level, tooltip, flag);
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
            public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
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
            public int getUseDuration(ItemStack stack) {
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
    public Item createMangroveBoatItem(Item.Properties properties) {
        return new com.kingodogo.buildscape.item.MangroveBoatItem(properties);
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
        net.minecraft.client.Minecraft.getInstance().getSoundManager().play(
                net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    @Override
    public void playNoteBlockBell() {
        net.minecraft.client.Minecraft.getInstance().getSoundManager().play(
                net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(SoundEvents.NOTE_BLOCK_BELL, 1.0F));
    }

    @Override
    public void playNoteBlockDidgeridoo() {
        net.minecraft.client.Minecraft.getInstance().getSoundManager().play(
                net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(SoundEvents.NOTE_BLOCK_DIDGERIDOO, 1.0F));
    }

    @Override
    public void playVegetationStepSound(Level level, BlockPos pos, Entity entity, SoundType sounds) {
        if (entity instanceof Player player && player.isOnGround() && level.isClientSide()) {
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
        return dyeColor.getTextureDiffuseColors();
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
        if (item != null && item != Items.AIR) {
            net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(item, chance);
        }
    }

    private void registerCauldronInteractions() {
        try {
            Item xpBucket = getItem(new CommonId("buildscape", "experience_bucket"));
            Block xpCauldron = getBlock(new CommonId("buildscape", "experience_cauldron"));
            if (xpBucket != null && xpCauldron != null) {
                net.minecraft.core.cauldron.CauldronInteraction.EMPTY.put(xpBucket, (state, level, pos, player, hand, stack) -> {
                    if (!level.isClientSide()) {
                        player.awardStat(net.minecraft.stats.Stats.FILL_CAULDRON);
                        level.setBlockAndUpdate(pos, xpCauldron.defaultBlockState().setValue(net.minecraft.world.level.block.LayeredCauldronBlock.LEVEL, 3));
                        level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                        if (!player.getAbilities().instabuild) {
                            player.setItemInHand(hand, new ItemStack(Items.BUCKET));
                        }
                    }
                    return net.minecraft.world.InteractionResult.sidedSuccess(level.isClientSide());
                });

                net.minecraft.core.cauldron.CauldronInteraction.EMPTY.put(Items.EXPERIENCE_BOTTLE, (state, level, pos, player, hand, stack) -> {
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
                    return net.minecraft.world.InteractionResult.sidedSuccess(level.isClientSide());
                });
            }
        } catch (Throwable ignored) {
        }
    }

    @Override
    public void renderModelPart(net.minecraft.client.model.geom.ModelPart part, com.mojang.blaze3d.vertex.PoseStack poseStack, com.mojang.blaze3d.vertex.VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (part != null) {
            part.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        }
    }

    @Override
    public int getRenderDistanceChunks() {
        return net.minecraft.client.Minecraft.getInstance().options.renderDistance;
    }

    @Override
    public void renderLineBox(com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float r, float g, float b, float a) {
        if (bufferSource instanceof net.minecraft.client.renderer.MultiBufferSource mbs) {
            com.mojang.blaze3d.vertex.VertexConsumer consumer = mbs.getBuffer(net.minecraft.client.renderer.RenderType.lines());
            net.minecraft.client.renderer.LevelRenderer.renderLineBox(poseStack, consumer, minX, minY, minZ, maxX, maxY, maxZ, r, g, b, a);
        }
    }

    @Override
    public boolean isScreenOpen() {
        return net.minecraft.client.Minecraft.getInstance().screen != null;
    }

    @Override
    public net.minecraft.world.phys.Vec3 getCameraPosition(net.minecraft.client.Camera camera) {
        return camera != null ? camera.getPosition() : net.minecraft.world.phys.Vec3.ZERO;
    }

    @Override
    public void renderFallingIcicleBlock(net.minecraft.world.level.Level level, net.minecraft.world.level.block.state.BlockState blockState, net.minecraft.core.BlockPos blockPos, net.minecraft.core.BlockPos startPos, com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource) {
        if (bufferSource instanceof net.minecraft.client.renderer.MultiBufferSource mbs) {
            net.minecraft.client.renderer.block.BlockRenderDispatcher blockRenderer = net.minecraft.client.Minecraft.getInstance().getBlockRenderer();
            net.minecraft.client.renderer.RenderType renderType = net.minecraft.client.renderer.RenderType.cutout();
            blockRenderer.getModelRenderer().tesselateBlock(
                    level,
                    blockRenderer.getBlockModel(blockState),
                    blockState,
                    blockPos,
                    poseStack,
                    mbs.getBuffer(renderType),
                    false,
                    level.random,
                    blockState.getSeed(startPos),
                    net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY
            );
        }
    }

    @Override
    public int getLightColor(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos) {
        return net.minecraft.client.renderer.LevelRenderer.getLightColor(level, pos);
    }

    @Override
    public boolean isMapItemWithData(ItemStack stack, net.minecraft.world.level.Level level) {
        return stack != null && !stack.isEmpty() && net.minecraft.world.item.MapItem.getSavedData(stack, level) != null;
    }

    private static final net.minecraft.resources.ResourceLocation BIRCH_PLANKS_118 = new net.minecraft.resources.ResourceLocation("minecraft", "textures/block/birch_planks.png");

    private void renderQuadWithUV118(com.mojang.blaze3d.vertex.VertexConsumer consumer, com.mojang.math.Matrix4f pose, com.mojang.math.Matrix3f normal, int packedLight,
                                     float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4,
                                     float u1, float v1, float u2, float v2, float nx, float ny, float nz) {
        consumer.vertex(pose, x1, y1, z1).color(255, 255, 255, 255).uv(u1, v1).overlayCoords(net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, nx, ny, nz).endVertex();
        consumer.vertex(pose, x2, y2, z2).color(255, 255, 255, 255).uv(u2, v1).overlayCoords(net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, nx, ny, nz).endVertex();
        consumer.vertex(pose, x3, y3, z3).color(255, 255, 255, 255).uv(u2, v2).overlayCoords(net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, nx, ny, nz).endVertex();
        consumer.vertex(pose, x4, y4, z4).color(255, 255, 255, 255).uv(u1, v2).overlayCoords(net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, nx, ny, nz).endVertex();
    }

    private void renderBoxFaces118(com.mojang.blaze3d.vertex.VertexConsumer consumer, com.mojang.math.Matrix4f pose, com.mojang.math.Matrix3f normal, int packedLight,
                                   float x1, float y1, float z1, float x2, float y2, float z2) {
        renderQuadWithUV118(consumer, pose, normal, packedLight, x2, y1, z1, x1, y1, z1, x1, y2, z1, x2, y2, z1, x1, 1F - y2, x2, 1F - y1, 0, 0, -1);
        renderQuadWithUV118(consumer, pose, normal, packedLight, x1, y1, z2, x2, y1, z2, x2, y2, z2, x1, y2, z2, x1, 1F - y2, x2, 1F - y1, 0, 0, 1);
        renderQuadWithUV118(consumer, pose, normal, packedLight, x1, y1, z2, x2, y1, z2, x2, y1, z1, x1, y1, z1, x1, z1, x2, z2, 0, -1, 0);
        renderQuadWithUV118(consumer, pose, normal, packedLight, x1, y2, z1, x2, y2, z1, x2, y2, z2, x1, y2, z2, x1, z1, x2, z2, 0, 1, 0);
        renderQuadWithUV118(consumer, pose, normal, packedLight, x1, y1, z1, x1, y1, z2, x1, y2, z2, x1, y2, z1, z1, 1F - y2, z2, 1F - y1, -1, 0, 0);
        renderQuadWithUV118(consumer, pose, normal, packedLight, x2, y1, z2, x2, y1, z1, x2, y2, z1, x2, y2, z2, z1, 1F - y2, z2, 1F - y1, 1, 0, 0);
    }

    @Override
    public void renderColoredFrame(com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource, int packedLight, Object backTexture, boolean hasMap) {
        if (!(bufferSource instanceof net.minecraft.client.renderer.MultiBufferSource buffer && backTexture instanceof net.minecraft.resources.ResourceLocation resLoc)) return;
        poseStack.pushPose();
        poseStack.translate(-0.5D, -0.5D, -0.5D);

        com.mojang.math.Matrix4f pose = poseStack.last().pose();
        com.mojang.math.Matrix3f normal = poseStack.last().normal();

        float backZ1 = hasMap ? 15.001F / 16F : 15.5F / 16F;
        float backZ2 = 1.0F;
        float frameZ1 = hasMap ? 15.001F / 16F : 15F / 16F;
        float frameZ2 = 1.0F;

        com.mojang.blaze3d.vertex.VertexConsumer backConsumer = buffer.getBuffer(net.minecraft.client.renderer.RenderType.entityCutoutNoCull(resLoc));

        float x1 = hasMap ? 1F / 16F : 3F / 16F;
        float x2 = hasMap ? 15F / 16F : 13F / 16F;
        float y1 = hasMap ? 1F / 16F : 3F / 16F;
        float y2 = hasMap ? 15F / 16F : 13F / 16F;

        renderQuadWithUV118(backConsumer, pose, normal, packedLight, x1, y1, backZ1, x2, y1, backZ1, x2, y2, backZ1, x1, y2, backZ1, x1, y2, x2, y1, 0, 0, -1);
        renderQuadWithUV118(backConsumer, pose, normal, packedLight, x2, y1, backZ2, x1, y1, backZ2, x1, y2, backZ2, x2, y2, backZ2, x1, y2, x2, y1, 0, 0, 1);

        com.mojang.blaze3d.vertex.VertexConsumer frameConsumer = buffer.getBuffer(net.minecraft.client.renderer.RenderType.entityCutoutNoCull(BIRCH_PLANKS_118));
        if (hasMap) {
            renderBoxFaces118(frameConsumer, pose, normal, packedLight, 1F / 16F, 0F, frameZ1, 15F / 16F, 1F / 16F, frameZ2);
            renderBoxFaces118(frameConsumer, pose, normal, packedLight, 1F / 16F, 15F / 16F, frameZ1, 15F / 16F, 1F, frameZ2);
            renderBoxFaces118(frameConsumer, pose, normal, packedLight, 0F, 0F, frameZ1, 1F / 16F, 1F, frameZ2);
            renderBoxFaces118(frameConsumer, pose, normal, packedLight, 15F / 16F, 0F, frameZ1, 1F, 1F, frameZ2);
        } else {
            renderBoxFaces118(frameConsumer, pose, normal, packedLight, 3F / 16F, 2F / 16F, frameZ1, 13F / 16F, 3F / 16F, frameZ2);
            renderBoxFaces118(frameConsumer, pose, normal, packedLight, 3F / 16F, 13F / 16F, frameZ1, 13F / 16F, 14F / 16F, frameZ2);
            renderBoxFaces118(frameConsumer, pose, normal, packedLight, 2F / 16F, 2F / 16F, frameZ1, 3F / 16F, 14F / 16F, frameZ2);
            renderBoxFaces118(frameConsumer, pose, normal, packedLight, 13F / 16F, 2F / 16F, frameZ1, 14F / 16F, 14F / 16F, frameZ2);
        }
        poseStack.popPose();
    }

    @Override
    public void renderColoredFrameItem(com.kingodogo.buildscape.entity.ColoredItemFrameEntity entity, ItemStack itemStack, com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource, int packedLight, boolean isMap, boolean isInvisible) {
        if (!(bufferSource instanceof net.minecraft.client.renderer.MultiBufferSource buffer)) return;
        net.minecraft.world.level.saveddata.maps.MapItemSavedData mapData = isMap ? net.minecraft.world.item.MapItem.getSavedData(itemStack, entity.asEntity().level) : null;
        poseStack.translate(0.0D, 0.0D, isInvisible ? 0.5D : 0.4375D);

        int rotation = (mapData != null) ? (entity.getRotation() % 4 * 2) : entity.getRotation();
        rotateZ(poseStack, (float) rotation * 360.0F / 8.0F);

        if (mapData != null) {
            rotateZ(poseStack, 180.0F);
            poseStack.scale(0.0078125F, 0.0078125F, 0.0078125F);
            poseStack.translate(-64.0D, -64.0D, 0.0D);
            poseStack.translate(0.0D, 0.0D, -1.0D);
            Integer mapId = net.minecraft.world.item.MapItem.getMapId(itemStack);
            if (mapId != null) {
                net.minecraft.client.Minecraft.getInstance().gameRenderer.getMapRenderer().render(poseStack, buffer, mapId, mapData, true, packedLight);
            }
        } else {
            poseStack.scale(0.5F, 0.5F, 0.5F);
            net.minecraft.client.Minecraft.getInstance().getItemRenderer().renderStatic(
                    itemStack,
                    net.minecraft.client.renderer.block.model.ItemTransforms.TransformType.FIXED,
                    packedLight,
                    net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY,
                    poseStack,
                    buffer,
                    entity.getId()
            );
        }
    }

    @Override
    public void renderStockingQuad(com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource, int packedLight, Object texture, boolean flipped) {
        if (!(bufferSource instanceof net.minecraft.client.renderer.MultiBufferSource buffer && texture instanceof net.minecraft.resources.ResourceLocation resLoc)) return;
        com.mojang.blaze3d.vertex.VertexConsumer vertexConsumer = buffer.getBuffer(net.minecraft.client.renderer.RenderType.entityCutoutNoCull(resLoc));
        float width = 0.5F;
        float height = 0.5F;
        float uMin = flipped ? 1.0F : 0.0F;
        float uMax = flipped ? 0.0F : 1.0F;
        float vMin = 0.0F;
        float vMax = 1.0F;

        com.mojang.math.Matrix4f pose = poseStack.last().pose();
        com.mojang.math.Matrix3f normal = poseStack.last().normal();

        vertexConsumer.vertex(pose, -width, -height, 0.0F).color(255, 255, 255, 255).uv(uMin, vMax).overlayCoords(net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0.0F, 0.0F, 1.0F).endVertex();
        vertexConsumer.vertex(pose, width, -height, 0.0F).color(255, 255, 255, 255).uv(uMax, vMax).overlayCoords(net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0.0F, 0.0F, 1.0F).endVertex();
        vertexConsumer.vertex(pose, width, height, 0.0F).color(255, 255, 255, 255).uv(uMax, vMin).overlayCoords(net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0.0F, 0.0F, 1.0F).endVertex();
        vertexConsumer.vertex(pose, -width, height, 0.0F).color(255, 255, 255, 255).uv(uMin, vMin).overlayCoords(net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0.0F, 0.0F, 1.0F).endVertex();
    }

    @Override
    public void renderBlockModel(net.minecraft.world.level.block.state.BlockState blockState, com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource, int light, int overlay) {
        if (!(bufferSource instanceof net.minecraft.client.renderer.MultiBufferSource buffer)) return;
        net.minecraft.client.renderer.block.BlockRenderDispatcher dispatcher = net.minecraft.client.Minecraft.getInstance().getBlockRenderer();
        net.minecraft.client.resources.model.BakedModel model = dispatcher.getBlockModel(blockState);
        net.minecraft.client.renderer.RenderType renderType = net.minecraft.client.renderer.ItemBlockRenderTypes.getRenderType(blockState, true);
        dispatcher.getModelRenderer().renderModel(poseStack.last(), buffer.getBuffer(renderType), blockState, model, 1.0F, 1.0F, 1.0F, light, overlay);
    }

    @Override
    public void renderBlockModelWithTint(net.minecraft.world.level.block.state.BlockState state, net.minecraft.core.BlockPos pos, net.minecraft.world.level.Level level, com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource, int light, int overlay) {
        if (!(bufferSource instanceof net.minecraft.client.renderer.MultiBufferSource buffer)) return;
        net.minecraft.client.renderer.block.BlockRenderDispatcher dispatcher = net.minecraft.client.Minecraft.getInstance().getBlockRenderer();
        net.minecraft.client.resources.model.BakedModel model = dispatcher.getBlockModel(state);
        net.minecraft.client.renderer.RenderType renderType = net.minecraft.client.renderer.ItemBlockRenderTypes.getRenderType(state, true);
        float r = 1.0F, g = 1.0F, b = 1.0F;
        int color = (level != null && pos != null)
                ? net.minecraft.client.Minecraft.getInstance().getBlockColors().getColor(state, level, pos, 0)
                : net.minecraft.client.Minecraft.getInstance().getBlockColors().getColor(state, null, null, 0);
        if (color != -1) {
            r = ((color >> 16) & 0xFF) / 255.0F;
            g = ((color >> 8) & 0xFF) / 255.0F;
            b = (color & 0xFF) / 255.0F;
        }
        dispatcher.getModelRenderer().renderModel(poseStack.last(), buffer.getBuffer(renderType), state, model, r, g, b, light, overlay);
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
        com.mojang.math.Matrix4f matrix = poseStack.last().pose();
        vc.vertex(matrix, x0, y0, z0).color(r, g, b, a).uv(u0, v0).overlayCoords(overlay).uv2(light).normal(nx, ny, nz).endVertex();
        vc.vertex(matrix, x1, y1, z1).color(r, g, b, a).uv(u1, v1).overlayCoords(overlay).uv2(light).normal(nx, ny, nz).endVertex();
        vc.vertex(matrix, x2, y2, z2).color(r, g, b, a).uv(u2, v2).overlayCoords(overlay).uv2(light).normal(nx, ny, nz).endVertex();
        vc.vertex(matrix, x3, y3, z3).color(r, g, b, a).uv(u3, v3).overlayCoords(overlay).uv2(light).normal(nx, ny, nz).endVertex();
        if (ny > 0) {
            vc.vertex(matrix, x3, y3, z3).color(r, g, b, a).uv(u3, v3).overlayCoords(overlay).uv2(light).normal(-nx, -ny, -nz).endVertex();
            vc.vertex(matrix, x2, y2, z2).color(r, g, b, a).uv(u2, v2).overlayCoords(overlay).uv2(light).normal(-nx, -ny, -nz).endVertex();
            vc.vertex(matrix, x1, y1, z1).color(r, g, b, a).uv(u1, v1).overlayCoords(overlay).uv2(light).normal(-nx, -ny, -nz).endVertex();
            vc.vertex(matrix, x0, y0, z0).color(r, g, b, a).uv(u0, v0).overlayCoords(overlay).uv2(light).normal(-nx, -ny, -nz).endVertex();
        }
    }

    @Override
    public void renderJarFluid(com.kingodogo.buildscape.block.GlassJarBlockEntity blockEntity, com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource, int light, int overlay) {
        if (!(bufferSource instanceof net.minecraft.client.renderer.MultiBufferSource mbs)) return;
        ItemStack liquidItem = blockEntity.getStoredLiquidItem();
        int level = blockEntity.getLiquidLevel();
        if (liquidItem == null || liquidItem.isEmpty() || level <= 0) return;

        net.minecraft.resources.ResourceLocation textureLoc = new net.minecraft.resources.ResourceLocation("minecraft", "block/water_still");
        int color = 0xFF3F76E4;

        if (com.kingodogo.buildscape.block.GlassJarBlockEntity.isXpLiquid(liquidItem)) {
            color = 0xFFFFFFFF;
            textureLoc = new net.minecraft.resources.ResourceLocation("buildscape", "fluid/experience_flow");
        } else if (liquidItem.getItem() instanceof net.minecraft.world.item.PotionItem) {
            color = net.minecraft.world.item.alchemy.PotionUtils.getColor(liquidItem);
            if ((color & 0xFF000000) == 0) color |= 0xFF000000;
        } else if (liquidItem.getItem() instanceof net.minecraft.world.item.HoneyBottleItem) {
            color = 0xFFFF9600;
            textureLoc = new net.minecraft.resources.ResourceLocation("minecraft", "block/honey_block_top");
        } else if (liquidItem.is(net.minecraft.world.item.Items.MILK_BUCKET)) {
            color = 0xFFFFFFFF;
            textureLoc = new net.minecraft.resources.ResourceLocation("minecraft", "block/white_concrete");
        } else if (liquidItem.is(net.minecraft.world.item.Items.LAVA_BUCKET)) {
            color = 0xFFFFFFFF;
            textureLoc = new net.minecraft.resources.ResourceLocation("minecraft", "block/lava_still");
        } else if (liquidItem.getItem() instanceof net.minecraft.world.item.BucketItem) {
            net.minecraft.world.level.material.Fluid fluid = getBucketFluid(liquidItem.getItem());
            if (fluid == net.minecraft.world.level.material.Fluids.LAVA || fluid == net.minecraft.world.level.material.Fluids.FLOWING_LAVA) {
                color = 0xFFFFFFFF;
                textureLoc = new net.minecraft.resources.ResourceLocation("minecraft", "block/lava_still");
            } else {
                color = 0xFF3F76E4;
                textureLoc = new net.minecraft.resources.ResourceLocation("minecraft", "block/water_still");
            }
        }

        net.minecraft.client.renderer.texture.TextureAtlasSprite sprite = net.minecraft.client.Minecraft.getInstance().getTextureAtlas(net.minecraft.world.inventory.InventoryMenu.BLOCK_ATLAS).apply(textureLoc);
        com.mojang.blaze3d.vertex.VertexConsumer vc = mbs.getBuffer(net.minecraft.client.renderer.RenderType.translucent());

        int maxLevel = com.kingodogo.buildscape.block.GlassJarBlockEntity.isXpLiquid(liquidItem) ? com.kingodogo.buildscape.block.GlassJarBlockEntity.XP_BOTTLE_MAX : 16;
        float fillRatio = Math.min(maxLevel, level) / (float) maxLevel;
        float y1 = 0.07F;
        float y2 = y1 + (fillRatio * 0.65F);
        float x1 = 0.27F, x2 = 0.73F, z1 = 0.27F, z2 = 0.73F;

        float r = ((color >> 16) & 0xFF) / 255.0F;
        float g = ((color >> 8) & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;
        float a = ((color >> 24) & 0xFF) / 255.0F;
        if (a == 0.0F) a = 0.88F;

        float u0 = sprite.getU0(), u1 = sprite.getU1(), v0 = sprite.getV0(), v1 = sprite.getV1();
        com.mojang.math.Matrix4f matrix = poseStack.last().pose();

        vc.vertex(matrix, x1, y2, z1).color(r, g, b, a).uv(u0, v0).overlayCoords(overlay).uv2(light).normal(0, 1, 0).endVertex();
        vc.vertex(matrix, x1, y2, z2).color(r, g, b, a).uv(u0, v1).overlayCoords(overlay).uv2(light).normal(0, 1, 0).endVertex();
        vc.vertex(matrix, x2, y2, z2).color(r, g, b, a).uv(u1, v1).overlayCoords(overlay).uv2(light).normal(0, 1, 0).endVertex();
        vc.vertex(matrix, x2, y2, z1).color(r, g, b, a).uv(u1, v0).overlayCoords(overlay).uv2(light).normal(0, 1, 0).endVertex();

        vc.vertex(matrix, x1, y2, z1).color(r, g, b, a).uv(u0, v0).overlayCoords(overlay).uv2(light).normal(0, 0, -1).endVertex();
        vc.vertex(matrix, x2, y2, z1).color(r, g, b, a).uv(u1, v0).overlayCoords(overlay).uv2(light).normal(0, 0, -1).endVertex();
        vc.vertex(matrix, x2, y1, z1).color(r, g, b, a).uv(u1, v1).overlayCoords(overlay).uv2(light).normal(0, 0, -1).endVertex();
        vc.vertex(matrix, x1, y1, z1).color(r, g, b, a).uv(u0, v1).overlayCoords(overlay).uv2(light).normal(0, 0, -1).endVertex();

        vc.vertex(matrix, x1, y1, z2).color(r, g, b, a).uv(u0, v1).overlayCoords(overlay).uv2(light).normal(0, 0, 1).endVertex();
        vc.vertex(matrix, x2, y1, z2).color(r, g, b, a).uv(u1, v1).overlayCoords(overlay).uv2(light).normal(0, 0, 1).endVertex();
        vc.vertex(matrix, x2, y2, z2).color(r, g, b, a).uv(u1, v0).overlayCoords(overlay).uv2(light).normal(0, 0, 1).endVertex();
        vc.vertex(matrix, x1, y2, z2).color(r, g, b, a).uv(u0, v0).overlayCoords(overlay).uv2(light).normal(0, 0, 1).endVertex();

        vc.vertex(matrix, x1, y1, z1).color(r, g, b, a).uv(u0, v1).overlayCoords(overlay).uv2(light).normal(-1, 0, 0).endVertex();
        vc.vertex(matrix, x1, y1, z2).color(r, g, b, a).uv(u1, v1).overlayCoords(overlay).uv2(light).normal(-1, 0, 0).endVertex();
        vc.vertex(matrix, x1, y2, z2).color(r, g, b, a).uv(u1, v0).overlayCoords(overlay).uv2(light).normal(-1, 0, 0).endVertex();
        vc.vertex(matrix, x1, y2, z1).color(r, g, b, a).uv(u0, v0).overlayCoords(overlay).uv2(light).normal(-1, 0, 0).endVertex();

        vc.vertex(matrix, x2, y2, z1).color(r, g, b, a).uv(u0, v0).overlayCoords(overlay).uv2(light).normal(1, 0, 0).endVertex();
        vc.vertex(matrix, x2, y2, z2).color(r, g, b, a).uv(u1, v0).overlayCoords(overlay).uv2(light).normal(1, 0, 0).endVertex();
        vc.vertex(matrix, x2, y1, z2).color(r, g, b, a).uv(u1, v1).overlayCoords(overlay).uv2(light).normal(1, 0, 0).endVertex();
        vc.vertex(matrix, x2, y1, z1).color(r, g, b, a).uv(u0, v1).overlayCoords(overlay).uv2(light).normal(1, 0, 0).endVertex();
    }

    @Override
    public void renderGlassJar(com.kingodogo.buildscape.block.GlassJarBlockEntity blockEntity, float partialTicks, com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource, int combinedLight, int combinedOverlay) {
        if (blockEntity == null || !(bufferSource instanceof net.minecraft.client.renderer.MultiBufferSource mbs)) return;

        Level level = blockEntity.getLevel();
        BlockPos pos = blockEntity.getBlockPos();
        BlockState blockState = blockEntity.getBlockState();
        int light = (level != null) ? net.minecraft.client.renderer.LevelRenderer.getLightColor(level, pos) : combinedLight;

        long currentTick = (level != null) ? level.getGameTime() : 0;
        long wobbleStartTick = blockEntity.getWobbleStartedAtTick();

        float wobbleProgress = 0.0F;
        float wobbleDuration = 10.0F;
        if (wobbleStartTick > 0) {
            float ticksSinceWobble = (float) (currentTick - wobbleStartTick) + partialTicks;
            if (ticksSinceWobble < wobbleDuration) {
                wobbleProgress = ticksSinceWobble / wobbleDuration;
            }
        }

        poseStack.pushPose();

        if (wobbleProgress > 0.0F && wobbleProgress < 1.0F) {
            float dampening = 1.0F - wobbleProgress;
            float oscillation = (float) Math.sin(wobbleProgress * Math.PI * 6);
            float rotationAngle = 8.0F * dampening * oscillation;

            poseStack.translate(0.5D, 0.0D, 0.5D);
            poseStack.mulPose(com.mojang.math.Vector3f.ZP.rotationDegrees(rotationAngle));
            poseStack.translate(-0.5D, 0.0D, -0.5D);
        }

        if (blockEntity.hasLiquid()) {
            renderJarFluid(blockEntity, poseStack, mbs, light, combinedOverlay);
        } else if (!blockEntity.isEmpty()) {
            ItemStack storedItem = blockEntity.getStoredItem();
            if (storedItem != null && !storedItem.isEmpty()) {
                int count = storedItem.getCount();
                int renderCount = Math.min(32, count);

                float startY = 0.07F;
                float yStep = 0.019F;
                net.minecraft.client.renderer.entity.ItemRenderer itemRenderer = net.minecraft.client.Minecraft.getInstance().getItemRenderer();
                net.minecraft.client.resources.model.BakedModel itemModel = itemRenderer.getModel(storedItem, level, null, 0);

                for (int i = 0; i < renderCount; i++) {
                    poseStack.pushPose();

                    long seed = (pos.getX() * 3129871L) ^ (pos.getZ() * 116129781L) ^ (pos.getY() * 9999991L) + i * 10007L;
                    long h1 = (seed ^ (seed >>> 16)) * 0x45d9f3bL;
                    h1 = (h1 ^ (h1 >>> 16)) * 0x45d9f3bL;
                    h1 = h1 ^ (h1 >>> 16);
                    float randomAngle = (float) (Math.abs(h1 % 360));

                    long seedX = seed + 17L;
                    long hx = (seedX ^ (seedX >>> 16)) * 0x45d9f3bL;
                    hx = (hx ^ (hx >>> 16)) * 0x45d9f3bL;
                    hx = hx ^ (hx >>> 16);
                    float offsetX = (((hx % 100) - 50) / 50.0F) * 0.025F;

                    long seedZ = seed + 34L;
                    long hz = (seedZ ^ (seedZ >>> 16)) * 0x45d9f3bL;
                    hz = (hz ^ (hz >>> 16)) * 0x45d9f3bL;
                    hz = hz ^ (hz >>> 16);
                    float offsetZ = (((hz % 100) - 50) / 50.0F) * 0.025F;

                    float currentY = startY + (i * yStep);

                    poseStack.translate(0.5D + offsetX, currentY, 0.5D + offsetZ);
                    poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(randomAngle));
                    poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(90.0F));
                    poseStack.scale(0.42F, 0.42F, 0.42F);

                    itemRenderer.render(storedItem, net.minecraft.client.renderer.block.model.ItemTransforms.TransformType.FIXED, false, poseStack, mbs, light, combinedOverlay, itemModel);
                    poseStack.popPose();
                }
            }
        }

        renderBlockModel(blockState, poseStack, mbs, light, combinedOverlay);
        poseStack.popPose();
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
                poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(rotationAngle));
                poseStack.translate(-0.5D, 0.0D, -0.5D);
            }
        }
        renderBlockModel(state, poseStack, bufferSource, light, overlay);
        poseStack.popPose();
    }

    private net.minecraft.client.model.geom.ModelPart copperChestLid;
    private net.minecraft.client.model.geom.ModelPart copperChestBottom;
    private net.minecraft.client.model.geom.ModelPart copperChestLock;
    private net.minecraft.client.model.geom.ModelPart copperChestDoubleLeftLid;
    private net.minecraft.client.model.geom.ModelPart copperChestDoubleLeftBottom;
    private net.minecraft.client.model.geom.ModelPart copperChestDoubleLeftLock;
    private net.minecraft.client.model.geom.ModelPart copperChestDoubleRightLid;
    private net.minecraft.client.model.geom.ModelPart copperChestDoubleRightBottom;
    private net.minecraft.client.model.geom.ModelPart copperChestDoubleRightLock;

    private synchronized void initCopperChestModels() {
        if (copperChestLid != null) return;
        net.minecraft.client.model.geom.EntityModelSet models = net.minecraft.client.Minecraft.getInstance().getEntityModels();
        net.minecraft.client.model.geom.ModelPart single = models.bakeLayer(net.minecraft.client.model.geom.ModelLayers.CHEST);
        this.copperChestBottom = single.getChild("bottom");
        this.copperChestLid = single.getChild("lid");
        this.copperChestLock = single.getChild("lock");

        net.minecraft.client.model.geom.ModelPart left = models.bakeLayer(net.minecraft.client.model.geom.ModelLayers.DOUBLE_CHEST_LEFT);
        this.copperChestDoubleLeftBottom = left.getChild("bottom");
        this.copperChestDoubleLeftLid = left.getChild("lid");
        this.copperChestDoubleLeftLock = left.getChild("lock");

        net.minecraft.client.model.geom.ModelPart right = models.bakeLayer(net.minecraft.client.model.geom.ModelLayers.DOUBLE_CHEST_RIGHT);
        this.copperChestDoubleRightBottom = right.getChild("bottom");
        this.copperChestDoubleRightLid = right.getChild("lid");
        this.copperChestDoubleRightLock = right.getChild("lock");
    }

    private net.minecraft.client.resources.model.Material getCopperChestMaterial(String path, net.minecraft.world.level.block.state.properties.ChestType chestType) {
        String baseName = "copper_chest";
        if (path.contains("oxidized")) baseName = "oxidized_copper_chest";
        else if (path.contains("weathered")) baseName = "weathered_copper_chest";
        else if (path.contains("exposed")) baseName = "exposed_copper_chest";

        String suffix = "";
        if (chestType == net.minecraft.world.level.block.state.properties.ChestType.LEFT) suffix = "_left";
        else if (chestType == net.minecraft.world.level.block.state.properties.ChestType.RIGHT) suffix = "_right";

        return new net.minecraft.client.resources.model.Material(
                net.minecraft.client.renderer.Sheets.CHEST_SHEET,
                new net.minecraft.resources.ResourceLocation("buildscape", "entity/chest/" + baseName + suffix)
        );
    }

    @Override
    public void renderCopperChest(com.kingodogo.buildscape.block.CopperChestBlockEntity blockEntity, float partialTicks, com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource, int combinedLight, int combinedOverlay) {
        if (blockEntity == null || !(bufferSource instanceof net.minecraft.client.renderer.MultiBufferSource mbs)) return;
        initCopperChestModels();

        Level level = blockEntity.getLevel();
        boolean hasLevel = level != null;
        BlockState blockState = hasLevel ? blockEntity.getBlockState() : net.minecraft.world.level.block.Blocks.CHEST.defaultBlockState().setValue(net.minecraft.world.level.block.ChestBlock.FACING, Direction.SOUTH);
        net.minecraft.world.level.block.state.properties.ChestType chestType = blockState.hasProperty(net.minecraft.world.level.block.ChestBlock.TYPE)
                ? blockState.getValue(net.minecraft.world.level.block.ChestBlock.TYPE)
                : net.minecraft.world.level.block.state.properties.ChestType.SINGLE;

        Block block = blockState.getBlock();
        CommonId blockId = getBlockId(block);
        String path = blockId == null ? "" : blockId.getPath();
        net.minecraft.client.resources.model.Material material = getCopperChestMaterial(path, chestType);
        com.mojang.blaze3d.vertex.VertexConsumer vertexConsumer = material.buffer(mbs, net.minecraft.client.renderer.RenderType::entityCutout);

        poseStack.pushPose();
        float yRot = blockState.hasProperty(net.minecraft.world.level.block.ChestBlock.FACING)
                ? blockState.getValue(net.minecraft.world.level.block.ChestBlock.FACING).toYRot()
                : 0.0F;
        poseStack.translate(0.5D, 0.5D, 0.5D);
        poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(-yRot));
        poseStack.translate(-0.5D, -0.5D, -0.5D);

        float openness = blockEntity.getOpenNess(partialTicks);
        openness = 1.0F - openness;
        openness = 1.0F - openness * openness * openness;

        net.minecraft.client.model.geom.ModelPart lidPart, bottomPart, lockPart;
        if (chestType == net.minecraft.world.level.block.state.properties.ChestType.LEFT) {
            lidPart = this.copperChestDoubleLeftLid;
            bottomPart = this.copperChestDoubleLeftBottom;
            lockPart = this.copperChestDoubleLeftLock;
        } else if (chestType == net.minecraft.world.level.block.state.properties.ChestType.RIGHT) {
            lidPart = this.copperChestDoubleRightLid;
            bottomPart = this.copperChestDoubleRightBottom;
            lockPart = this.copperChestDoubleRightLock;
        } else {
            lidPart = this.copperChestLid;
            bottomPart = this.copperChestBottom;
            lockPart = this.copperChestLock;
        }

        lidPart.xRot = -(openness * ((float) Math.PI / 2F));
        lockPart.xRot = lidPart.xRot;
        lidPart.render(poseStack, vertexConsumer, combinedLight, combinedOverlay);
        lockPart.render(poseStack, vertexConsumer, combinedLight, combinedOverlay);
        bottomPart.render(poseStack, vertexConsumer, combinedLight, combinedOverlay);

        poseStack.popPose();
    }

    @Override
    public void registerMenuScreens() {
        if (!isClient()) return;
        com.kingodogo.buildscape.adapter.v118x.GuiProvider.registerMenuScreens();
        registerRenderers();
    }

    @Override
    public void registerRenderers() {
        if (!isClient()) return;
        RenderFactory.registerRenderers();
    }

    @Override
    public void registerLayerDefinitions(java.util.function.BiConsumer<Object, java.util.function.Supplier<Object>> registrar) {
        if (!isClient()) return;
        registrar.accept(RenderFactory.HOMEMAKER_LAYER, RenderFactory::createBodyLayer);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void registerEntityAttributes(java.util.function.BiConsumer<net.minecraft.world.entity.EntityType<? extends net.minecraft.world.entity.LivingEntity>, net.minecraft.world.entity.ai.attributes.AttributeSupplier> consumer) {
        net.minecraft.world.entity.EntityType<? extends net.minecraft.world.entity.LivingEntity> homemaker = (net.minecraft.world.entity.EntityType<? extends net.minecraft.world.entity.LivingEntity>) getWanderingHomemakerEntityType();
        net.minecraft.world.entity.EntityType<? extends net.minecraft.world.entity.LivingEntity> festive = (net.minecraft.world.entity.EntityType<? extends net.minecraft.world.entity.LivingEntity>) getFestiveWanderingHomemakerEntityType();
        net.minecraft.world.entity.ai.attributes.AttributeSupplier supplier = WanderingHomemakerEntityImpl.createAttributes().build();
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
        try {
            return net.minecraft.client.renderer.ItemBlockRenderTypes.getChunkRenderType(state) == net.minecraft.client.renderer.RenderType.translucent();
        } catch (Throwable t) {
            return false;
        }
    }

    private static final int COLOR_MAX_SAMPLES = 32;
    private final Map<String, double[]> v118xSpriteCache = new java.util.concurrent.ConcurrentHashMap<>();

    private static com.mojang.blaze3d.platform.NativeImage getSpriteImage(net.minecraft.client.renderer.texture.TextureAtlasSprite sprite) {
        try {
            java.lang.reflect.Field field = net.minecraft.client.renderer.texture.TextureAtlasSprite.class.getDeclaredField("mainImage");
            field.setAccessible(true);
            com.mojang.blaze3d.platform.NativeImage[] images = (com.mojang.blaze3d.platform.NativeImage[]) field.get(sprite);
            if (images != null && images.length > 0) return images[0];
        } catch (Throwable ignored) {
            try {
                for (java.lang.reflect.Field f : net.minecraft.client.renderer.texture.TextureAtlasSprite.class.getDeclaredFields()) {
                    if (f.getType().isArray() && f.getType().getComponentType() == com.mojang.blaze3d.platform.NativeImage.class) {
                        f.setAccessible(true);
                        com.mojang.blaze3d.platform.NativeImage[] images = (com.mojang.blaze3d.platform.NativeImage[]) f.get(sprite);
                        if (images != null && images.length > 0) return images[0];
                    }
                }
            } catch (Throwable ignored2) {}
        }
        return null;
    }

    @Override
    public BlockColorSample sampleBlockColor(net.minecraft.world.level.block.state.BlockState state) {
        if (state == null) return null;
        try {
            net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
            if (mc == null || mc.getBlockRenderer() == null) return null;
            net.minecraft.client.resources.model.BakedModel model = mc.getBlockRenderer().getBlockModel(state);
            if (model == null) return null;

            net.minecraft.client.color.block.BlockColors blockColors = mc.getBlockColors();
            double red = 0, green = 0, blue = 0;
            int weight = 0;
            boolean transparent = false;
            java.util.Set<String> textures = new java.util.HashSet<>();

            for (Direction side : new Direction[]{Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST, null}) {
                List<net.minecraft.client.renderer.block.model.BakedQuad> quads;
                try {
                    quads = model.getQuads(state, side, new java.util.Random(42L));
                } catch (Throwable ignored) {
                    continue;
                }
                if (quads == null) continue;
                for (net.minecraft.client.renderer.block.model.BakedQuad quad : quads) {
                    net.minecraft.client.renderer.texture.TextureAtlasSprite sprite = quad.getSprite();
                    if (sprite == null) continue;
                    String spriteName = sprite.getName().toString();
                    if (spriteName.contains("missingno")) continue;
                    textures.add(spriteName);

                    int tint = 0xFFFFFF;
                    if (quad.isTinted() && blockColors != null) {
                        try {
                            int resolved = blockColors.getColor(state, null, null, quad.getTintIndex());
                            if (resolved != -1) tint = resolved & 0xFFFFFF;
                        } catch (Throwable ignored) {}
                    }

                    int tintR = (tint >> 16) & 255;
                    int tintG = (tint >> 8) & 255;
                    int tintB = tint & 255;

                    String cacheKey = spriteName + "#" + tint;
                    double[] sample = v118xSpriteCache.computeIfAbsent(cacheKey, k -> {
                        com.mojang.blaze3d.platform.NativeImage img = getSpriteImage(sprite);
                        if (img == null) return new double[]{0, 0, 0, 0, 0};
                        int w = sprite.getWidth();
                        int h = sprite.getHeight();
                        int stepX = Math.max(1, (w + COLOR_MAX_SAMPLES - 1) / COLOR_MAX_SAMPLES);
                        int stepY = Math.max(1, (h + COLOR_MAX_SAMPLES - 1) / COLOR_MAX_SAMPLES);
                        double rAcc = 0, gAcc = 0, bAcc = 0, wAcc = 0;
                        boolean trans = false;
                        for (int y = 0; y < h; y += stepY) {
                            for (int x = 0; x < w; x += stepX) {
                                int pixel = img.getPixelRGBA(x, y);
                                int a = com.mojang.blaze3d.platform.NativeImage.getA(pixel);
                                if (a < 250) trans = true;
                                if (a < 16) continue;
                                double aw = a / 255.0;
                                int pr = (com.mojang.blaze3d.platform.NativeImage.getR(pixel) * tintR) / 255;
                                int pg = (com.mojang.blaze3d.platform.NativeImage.getG(pixel) * tintG) / 255;
                                int pb = (com.mojang.blaze3d.platform.NativeImage.getB(pixel) * tintB) / 255;
                                rAcc += srgbToLinearColor(pr / 255.0) * aw;
                                gAcc += srgbToLinearColor(pg / 255.0) * aw;
                                bAcc += srgbToLinearColor(pb / 255.0) * aw;
                                wAcc += aw;
                            }
                        }
                        if (wAcc == 0) return new double[]{0, 0, 0, 0, trans ? 1 : 0};
                        return new double[]{rAcc / wAcc, gAcc / wAcc, bAcc / wAcc, 1.0, trans ? 1 : 0};
                    });

                    if (sample[3] > 0) {
                        red += sample[0];
                        green += sample[1];
                        blue += sample[2];
                        if (sample[4] > 0.5) transparent = true;
                        weight++;
                    }
                }
            }

            if (weight == 0) return null;
            int finalR = linearToSrgbColor(red / weight);
            int finalG = linearToSrgbColor(green / weight);
            int finalB = linearToSrgbColor(blue / weight);
            int rgb = (finalR << 16) | (finalG << 8) | finalB;
            return new BlockColorSample(rgb, transparent, textures.size() == 1);
        } catch (Throwable t) {
            return null;
        }
    }

    private static double srgbToLinearColor(double val) {
        return val <= 0.04045 ? val / 12.92 : Math.pow((val + 0.055) / 1.055, 2.4);
    }

    private static int linearToSrgbColor(double val) {
        double clamped = Math.max(0.0, Math.min(1.0, val));
        double srgb = clamped <= 0.0031308 ? clamped * 12.92 : 1.055 * Math.pow(clamped, 1.0 / 2.4) - 0.055;
        return Math.max(0, Math.min(255, (int) Math.round(srgb * 255.0)));
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
        protected void defineSynchedData() {
            this.getEntityData().define(DATA_ITEM, ItemStack.EMPTY);
            this.getEntityData().define(DATA_ROTATION, 0);
            this.getEntityData().define(DATA_COLOR, "white");
            this.getEntityData().define(DATA_PARTICLE_PATTERN, "none");
            this.getEntityData().define(DATA_PARTICLE_COLORS, "");
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
        protected void recalculateBoundingBox() {
            if (this.direction == null || this.pos == null) {
                return;
            }

            double x = (double) this.pos.getX() + 0.5D - (double) this.direction.getStepX() * 0.46875D;
            double y = (double) this.pos.getY() + 0.5D - (double) this.direction.getStepY() * 0.46875D;
            double z = (double) this.pos.getZ() + 0.5D - (double) this.direction.getStepZ() * 0.46875D;
            this.setPosRaw(x, y, z);

            double w = this.getWidth();
            double h = this.getHeight();
            double d = this.getWidth();

            Direction.Axis axis = this.direction.getAxis();
            switch (axis) {
                case X -> w = 1.0D;
                case Y -> h = 1.0D;
                case Z -> d = 1.0D;
            }

            w /= 32.0D;
            h /= 32.0D;
            d /= 32.0D;

            this.setBoundingBox(new AABB(x - w, y - h, z - d, x + w, y + h, z + d));
        }

        @Override
        public int getWidth() {
            return 12;
        }

        @Override
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
                this.level.updateNeighbourForOutputSignal(this.pos, Blocks.AIR);
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
                this.level.updateNeighbourForOutputSignal(this.pos, Blocks.AIR);
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
            } else if (!this.level.isClientSide) {
                if (!hasItemInFrame) {
                    if (hasItemInHand && !this.isRemoved()) {
                        if (heldItem.is(Items.FILLED_MAP)) {
                            MapItemSavedData mapData = MapItem.getSavedData(heldItem, this.level);
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
                return (source == DamageSource.OUT_OF_WORLD || source.isCreativePlayer()) && super.hurt(source, amount);
            } else if (this.isInvulnerableTo(source)) {
                return false;
            } else if (!source.isExplosion() && !this.getItem().isEmpty()) {
                if (!this.level.isClientSide) {
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
                return !this.level.mayInteract(player, this.pos);
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

                if (!this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
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
                MapItemSavedData mapData = MapItem.getSavedData(stack, this.level);
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
            } else if (!this.level.noCollision(this)) {
                return false;
            } else {
                BlockState blockstate = this.level.getBlockState(this.pos.relative(this.direction.getOpposite()));
                return (blockstate.getMaterial().isSolid()
                        || (this.direction.getAxis().isHorizontal() && DiodeBlock.isDiode(blockstate)))
                        && this.level.getEntities(this, this.getBoundingBox(), HANGING_ENTITY).isEmpty();
            }
        }

        @Override
        public void addAdditionalSaveData(CompoundTag tag) {
            super.addAdditionalSaveData(tag);
            if (!this.getItem().isEmpty()) {
                tag.put("Item", this.getItem().save(new CompoundTag()));
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
                ItemStack itemstack = ItemStack.of(itemTag);
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
        public Packet<?> getAddEntityPacket() {
            return new ClientboundAddEntityPacket(this, this.getType(), this.direction.get3DDataValue(), this.getPos());
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
            CompoundTag tag = frameItem.getOrCreateTag();
            boolean hasCustomData = false;

            if (!displayedItem.isEmpty()) {
                CommonId itemId = Services.PLATFORM.getItemId(displayedItem.getItem());
                if (itemId != null) {
                    tag.putString("ITEM", itemId.toString());
                    hasCustomData = true;
                }
            }

            CompoundTag persistentData = Services.PLATFORM.getEntityData(this);
            if (persistentData != null && persistentData.contains("BuildScapeParticlePattern", 8)) {
                tag.putString("PATTERN", persistentData.getString("BuildScapeParticlePattern"));
                hasCustomData = true;
            }

            if (persistentData != null && persistentData.contains("BuildScapeParticleColors", 9)) {
                ListTag colorList = persistentData.getList("BuildScapeParticleColors", 8);
                if (colorList.size() > 0) {
                    tag.put("COLORS", colorList.copy());
                    hasCustomData = true;
                }
            }

            if (hasCustomData) {
                frameItem.setTag(tag);
            }

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
        protected void defineSynchedData() {}

        @Override
        public void tick() {
            if (!this.isNoGravity()) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.04D, 0.0D));
            }

            this.move(MoverType.SELF, this.getDeltaMovement());

            if (!this.level.isClientSide && fallTime > 2) {
                checkEntityCollision();
            }

            this.setDeltaMovement(this.getDeltaMovement().scale(0.98D));
            fallTime++;

            if (this.onGround && !hasLanded) {
                hasLanded = true;
                if (!this.level.isClientSide && !blockState.isAir()) {
                    breakAndDrop();
                }
                this.discard();
                return;
            }

            if (fallTime > 600 || this.getY() < this.level.getMinBuildHeight() - 64) {
                this.discard();
            }
        }

        private void checkEntityCollision() {
            AABB boundingBox = this.getBoundingBox().inflate(0.25, 0.5, 0.25);
            Vec3 motion = this.getDeltaMovement();
            if (motion.y < 0) {
                boundingBox = boundingBox.expandTowards(0, motion.y, 0);
            }

            List<LivingEntity> entities = this.level.getEntitiesOfClass(LivingEntity.class, boundingBox);
            for (LivingEntity entity : entities) {
                float fallDistance = (float) (this.startPos != null ? this.startPos.getY() - this.getY() : fallTime * 0.04);
                float damage = Math.min(Math.max(2.0f, fallDistance * 2.0f), 40.0f);

                boolean damaged = entity.hurt(DamageSource.FALLING_STALACTITE, damage);
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
            ItemEntity itemEntity = new ItemEntity(this.level, this.getX(), this.getY(), this.getZ(), itemStack);
            itemEntity.setDefaultPickUpDelay();
            this.level.addFreshEntity(itemEntity);

            this.level.playSound(null, this.blockPosition(), blockState.getSoundType().getBreakSound(), net.minecraft.sounds.SoundSource.BLOCKS, 1.0f, 1.0f);
            this.level.levelEvent(2001, this.blockPosition(), Block.getId(blockState));
        }

        @Override
        protected void readAdditionalSaveData(CompoundTag tag) {
            this.blockState = NbtUtils.readBlockState(tag.getCompound("BlockState"));
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
        public Packet<?> getAddEntityPacket() {
            return new ClientboundAddEntityPacket(this, Block.getId(this.blockState));
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
                if (this.pos != null) {
                    this.recalculateBoundingBox();
                }
            }
        }

        @Override
        protected void defineSynchedData() {
            this.getEntityData().define(DATA_ITEM, ItemStack.EMPTY);
        }

        @Override
        public void setPos(double x, double y, double z) {
            super.setPos(x, y, z);
            this.recalculateBoundingBox();
        }

        @Override
        protected void recalculateBoundingBox() {
            if (this.direction != null && this.pos != null) {
                double d0 = (double) this.pos.getX() + 0.5D;
                double d1 = (double) this.pos.getY() + 0.5D;
                double d2 = (double) this.pos.getZ() + 0.5D;
                double d4 = this.offs(this.getWidth());
                double d5 = this.offs(this.getHeight());

                d0 -= (double) this.direction.getStepX() * 0.46875D;
                d1 -= (double) this.direction.getStepY() * 0.46875D;
                d2 -= (double) this.direction.getStepZ() * 0.46875D;

                Direction offsetDir;
                if (this.direction.getAxis() == Direction.Axis.Y) {
                    offsetDir = Direction.NORTH;
                } else {
                    offsetDir = switch (this.direction) {
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

                this.setPosRaw(d0, d1, d2);

                double d6, d7, d8;
                if (this.direction.getAxis() == Direction.Axis.Z) {
                    d6 = (double) this.getWidth();
                    d7 = (double) this.getHeight();
                    d8 = 2.0D;
                } else if (this.direction.getAxis() == Direction.Axis.X) {
                    d6 = 2.0D;
                    d7 = (double) this.getHeight();
                    d8 = (double) this.getWidth();
                } else {
                    d6 = (double) this.getWidth();
                    d7 = 2.0D;
                    d8 = (double) this.getHeight();
                }

                d6 /= 32.0D;
                d7 /= 32.0D;
                d8 /= 32.0D;

                this.setBoundingBox(new AABB(d0 - d6, d1 - d7, d2 - d8, d0 + d6, d1 + d7, d2 + d8));
            }
        }

        private double offs(int val) {
            return val % 32 == 0 ? 0.5D : 0.0D;
        }

        @Override
        public int getWidth() {
            return 12;
        }

        @Override
        public int getHeight() {
            return 16;
        }

        @Override
        public void dropItem(@Nullable Entity entity) {
            this.playSound(SoundEvents.ITEM_FRAME_REMOVE_ITEM, 1.0F, 1.0F);
            if (!this.level.isClientSide) {
                ItemStack storedItem = this.getItem();
                boolean hasSilkTouch = false;

                if (entity instanceof Player player) {
                    ItemStack tool = player.getMainHandItem();
                    if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, tool) > 0) {
                        hasSilkTouch = true;
                    }
                }

                ItemStack stockingItem = getStockingItemForColor(this.colorVariant);

                if (hasSilkTouch && !storedItem.isEmpty()) {
                    CompoundTag tag = stockingItem.getOrCreateTag();
                    CompoundTag storedTag = new CompoundTag();
                    storedItem.save(storedTag);
                    tag.put("StoredItem", storedTag);
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
                if (!this.level.isClientSide) {
                    ItemStack toGive = storedItem.copy();
                    this.setItem(ItemStack.EMPTY);

                    if (!player.getInventory().add(toGive)) {
                        ItemEntity itemEntity = new ItemEntity(this.level, this.getX(), this.getY(), this.getZ(), toGive);
                        itemEntity.setDefaultPickUpDelay();
                        this.level.addFreshEntity(itemEntity);
                    }

                    this.playSound(SoundEvents.ITEM_FRAME_REMOVE_ITEM, 1.0F, 1.0F);
                }
                return InteractionResult.sidedSuccess(this.level.isClientSide);
            }

            if (!heldItem.isEmpty()) {
                if (storedItem.isEmpty()) {
                    if (!this.level.isClientSide) {
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
                    return InteractionResult.sidedSuccess(this.level.isClientSide);
                } else if (ItemStack.isSameItemSameTags(storedItem, heldItem)
                        && storedItem.getCount() < storedItem.getMaxStackSize()) {
                    if (!this.level.isClientSide) {
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
                    return InteractionResult.sidedSuccess(this.level.isClientSide);
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
                this.level.updateNeighbourForOutputSignal(this.pos, Blocks.AIR);
            }
        }

        @Override
        public void addAdditionalSaveData(CompoundTag tag) {
            super.addAdditionalSaveData(tag);
            ItemStack storedItem = this.getItem();
            if (!storedItem.isEmpty()) {
                CompoundTag itemTag = new CompoundTag();
                storedItem.save(itemTag);
                tag.put("Item", itemTag);
            }
            tag.putString("ColorVariant", colorVariant);
        }

        @Override
        public void readAdditionalSaveData(CompoundTag tag) {
            super.readAdditionalSaveData(tag);
            CompoundTag itemTag = tag.getCompound("Item");
            if (itemTag != null && !itemTag.isEmpty()) {
                ItemStack item = ItemStack.of(itemTag);
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
            if (!this.level.noCollision(this)) {
                return false;
            } else {
                BlockPos blockpos = this.pos.relative(this.direction.getOpposite());
                if (!this.level.getBlockState(blockpos).isSolidRender(this.level, blockpos)) {
                    return false;
                } else {
                    List<Entity> entities = this.level.getEntities(this, this.getBoundingBox());
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
        public Packet<?> getAddEntityPacket() {
            return new ClientboundAddEntityPacket(this);
        }

        @Override
        public void recreateFromPacket(ClientboundAddEntityPacket packet) {
            super.recreateFromPacket(packet);
        }

        @Override
        public boolean hurt(DamageSource source, float amount) {
            if (this.isInvulnerableTo(source)) {
                return false;
            } else if (!source.isExplosion() && !this.getItem().isEmpty()) {
                if (!this.level.isClientSide) {
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
                CompoundTag tag = result.getOrCreateTag();
                CompoundTag storedTag = new CompoundTag();
                storedItem.save(storedTag);
                tag.put("StoredItem", storedTag);
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
            this(EntityType.WANDERING_TRADER, level);
        }

        public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder createAttributes() {
            return net.minecraft.world.entity.Mob.createMobAttributes()
                    .add(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED, 0.5D)
                    .add(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH, 20.0D)
                    .add(net.minecraft.world.entity.ai.attributes.Attributes.FOLLOW_RANGE, 16.0D);
        }

        @Override
        public void aiStep() {
            super.aiStep();
            if (!this.level.isClientSide) {
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

            List<MerchantOffer> list = WanderingHomemakerTrades.getFestiveTrades(this.random);
            Collections.shuffle(list, this.random);
            for (int j = 0; j < Math.min(6, list.size()); j++) {
                offers.add(list.get(j));
            }

            LocalDate today = LocalDate.now();
            int month = today.getMonthValue();
            int day = today.getDayOfMonth();
            int rareChance = (month == 12 && day == 25) ? 15 : 250;
            if (this.random.nextInt(rareChance) == 0) {
                offers.add(Services.PLATFORM.createMerchantOffer(
                        new ItemStack(Items.DIAMOND, 5),
                        new ItemStack(Services.PLATFORM.getItem(new CommonId("buildscape", "frost_rose")), 1),
                        5, 1, 0.05f));
            }
        }
    }

    public static class MangroveBoatEntityImpl extends Boat implements MangroveBoatEntity {

        public MangroveBoatEntityImpl(EntityType<? extends Boat> entityType, Level level) {
            super(entityType, level);
            this.setType(Boat.Type.OAK);
        }

        public MangroveBoatEntityImpl(Level level, double x, double y, double z) {
            this(EntityType.BOAT, level);
            this.setPos(x, y, z);
            this.xo = x;
            this.yo = y;
            this.zo = z;
            this.setType(Boat.Type.OAK);
        }

        @Override
        public Item getDropItem() {
            return Services.PLATFORM.getItem(new CommonId("buildscape", "mangrove_boat"));
        }
    }

    public static class PoplarBoatEntityImpl extends Boat implements PoplarBoatEntity {

        public PoplarBoatEntityImpl(EntityType<? extends Boat> entityType, Level level) {
            super(entityType, level);
            this.setType(Boat.Type.OAK);
        }

        public PoplarBoatEntityImpl(Level level, double x, double y, double z) {
            this(EntityType.BOAT, level);
            this.setPos(x, y, z);
            this.xo = x;
            this.yo = y;
            this.zo = z;
            this.setType(Boat.Type.OAK);
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
        protected void defineSynchedData() {}

        @Override
        public boolean hurt(DamageSource source, float amount) {
            return false;
        }

        @Override
        protected void readAdditionalSaveData(CompoundTag tag) {}

        @Override
        protected void addAdditionalSaveData(CompoundTag tag) {}

        @Override
        public Packet<?> getAddEntityPacket() {
            return new ClientboundAddEntityPacket(this);
        }

        @Override
        public void tick() {
            super.tick();
            if (!this.level.isClientSide()) {
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

        public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder createAttributes() {
            return net.minecraft.world.entity.Mob.createMobAttributes()
                    .add(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED, 0.5D)
                    .add(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH, 20.0D)
                    .add(net.minecraft.world.entity.ai.attributes.Attributes.FOLLOW_RANGE, 16.0D);
        }

        @Override
        public void aiStep() {
            super.aiStep();
            if (!this.level.isClientSide) {
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
            Collections.shuffle(list, this.random);
            for (int j = 0; j < Math.min(5, list.size()); j++) {
                offers.add(list.get(j));
            }

            if (this.random.nextInt(5000) == 0) {
                offers.add(Services.PLATFORM.createMerchantOffer(
                        new ItemStack(Items.DIAMOND, 12),
                        new ItemStack(Services.PLATFORM.getItem(new CommonId("buildscape", "ancient_ashen_scroll")), 1),
                        1, 1, 0.0f));
            }
        }
    }

    @Override
    public net.minecraft.client.gui.screens.Screen createConfigScreen(net.minecraft.client.gui.screens.Screen parent) {
        return com.kingodogo.buildscape.adapter.v118x.GuiProvider.createConfigScreen(parent);
    }

    @Override
    public net.minecraft.client.gui.components.Button createButton(int x, int y, int width, int height, net.minecraft.network.chat.Component message, net.minecraft.client.gui.components.Button.OnPress onPress) {
        return com.kingodogo.buildscape.adapter.v118x.GuiProvider.createButton(x, y, width, height, message, onPress);
    }

    @Override
    public net.minecraft.client.gui.components.Button createCustomButton(int x, int y, int width, int height, net.minecraft.network.chat.Component message, net.minecraft.client.gui.components.Button.OnPress onPress, com.kingodogo.buildscape.client.screen.widget.CustomButtonRenderer renderer) {
        return com.kingodogo.buildscape.adapter.v118x.GuiProvider.createCustomButton(x, y, width, height, message, onPress, renderer);
    }

    @Override
    public net.minecraft.client.gui.screens.Screen wrapScreen(net.minecraft.network.chat.Component title, net.minecraft.client.gui.screens.Screen parent, com.kingodogo.buildscape.client.screen.IScreenDelegate delegate) {
        return com.kingodogo.buildscape.adapter.v118x.GuiProvider.wrapScreen(title, parent, delegate);
    }

    @Override
    public net.minecraft.client.gui.screens.Screen createGuiEditorScreen(net.minecraft.client.gui.screens.Screen parent, String tabName, com.kingodogo.buildscape.client.screen.AbstractConfigTab sourceTab) {
        return com.kingodogo.buildscape.adapter.v118x.GuiProvider.createGuiEditorScreen(parent, tabName, sourceTab);
    }

    @Override
    public net.minecraft.client.gui.screens.Screen createInventoryItemSelectorScreen(net.minecraft.client.gui.screens.Screen parent, com.kingodogo.buildscape.client.screen.PillarItemsConfigTab configTab) {
        return com.kingodogo.buildscape.adapter.v118x.GuiProvider.createInventoryItemSelectorScreen(parent, configTab);
    }

    @Override
    public void renderGuiItem(Object poseStackOrGraphics, net.minecraft.world.item.ItemStack stack, int x, int y) {
        com.kingodogo.buildscape.adapter.v118x.GuiProvider.renderGuiItem(poseStackOrGraphics, stack, x, y);
    }

    @Override
    public void renderGuiItemDecorations(Object poseStackOrGraphics, net.minecraft.client.gui.Font font, net.minecraft.world.item.ItemStack stack, int x, int y) {
        com.kingodogo.buildscape.adapter.v118x.GuiProvider.renderGuiItemDecorations(poseStackOrGraphics, font, stack, x, y);
    }

    @Override
    public java.util.List<net.minecraft.network.chat.Component> getTooltipFromItem(net.minecraft.world.item.ItemStack stack) {
        return com.kingodogo.buildscape.adapter.v118x.GuiProvider.getTooltipFromItem(stack);
    }

    @Override
    public net.minecraft.client.gui.components.AbstractWidget wrapCustomWidget(int x, int y, int width, int height, net.minecraft.network.chat.Component message, com.kingodogo.buildscape.client.screen.widget.ICustomWidget customWidget) {
        return com.kingodogo.buildscape.adapter.v118x.GuiProvider.wrapCustomWidget(x, y, width, height, message, customWidget);
    }

    @Override
    public int getWaterColor(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos) {
        return (level != null && pos != null) ? net.minecraft.client.renderer.BiomeColors.getAverageWaterColor(level, pos) : 0x3F76E4;
    }

    @Override
    public boolean isArmor(net.minecraft.world.item.ItemStack stack) {
        return stack != null && stack.getItem() instanceof net.minecraft.world.item.ArmorItem;
    }

    @Override
    public boolean isSwordLike(net.minecraft.world.item.ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        net.minecraft.world.item.Item item = stack.getItem();
        return item instanceof net.minecraft.world.item.SwordItem ||
                item instanceof net.minecraft.world.item.TridentItem ||
                item instanceof net.minecraft.world.item.ShovelItem;
    }

    @Override
    public boolean isAxeLike(net.minecraft.world.item.ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        net.minecraft.world.item.Item item = stack.getItem();
        return item instanceof net.minecraft.world.item.AxeItem ||
                item instanceof net.minecraft.world.item.PickaxeItem ||
                item instanceof net.minecraft.world.item.HoeItem;
    }

    @Override
    public void setupArmorStand(net.minecraft.world.entity.Entity entity) {
        if (entity instanceof net.minecraft.world.entity.decoration.ArmorStand stand) {
            net.minecraft.nbt.CompoundTag nbt = new net.minecraft.nbt.CompoundTag();
            nbt.putBoolean("ShowArms", true);
            nbt.putBoolean("Small", false);
            nbt.putBoolean("NoGravity", true);
            nbt.putBoolean("NoBasePlate", true);
            nbt.putBoolean("Invisible", true);
            try {
                stand.readAdditionalSaveData(nbt);
            } catch (Exception ignored) {
            }
            stand.setUUID(java.util.UUID.randomUUID());
            stand.noPhysics = true;
        }
    }

    @Override
    public void updateArmorStand(net.minecraft.world.entity.Entity entity, net.minecraft.world.item.ItemStack stack, net.minecraft.world.entity.EquipmentSlot slot, boolean isStandItem) {
        if (!(entity instanceof net.minecraft.world.entity.decoration.ArmorStand armorStand)) return;
        for (net.minecraft.world.entity.EquipmentSlot s : net.minecraft.world.entity.EquipmentSlot.values()) {
            armorStand.setItemSlot(s, net.minecraft.world.item.ItemStack.EMPTY);
        }
        net.minecraft.nbt.CompoundTag nbt = new net.minecraft.nbt.CompoundTag();
        if (isStandItem) {
            nbt.putBoolean("Invisible", false);
            nbt.putBoolean("NoBasePlate", false);
        } else {
            nbt.putBoolean("Invisible", true);
            nbt.putBoolean("NoBasePlate", true);
            if (slot != null && !stack.isEmpty()) {
                armorStand.setItemSlot(slot, stack.copy());
            } else if (isElytra(stack)) {
                armorStand.setItemSlot(net.minecraft.world.entity.EquipmentSlot.CHEST, stack.copy());
            }
        }
        try {
            armorStand.readAdditionalSaveData(nbt);
        } catch (Exception ignored) {
        }
    }
}
