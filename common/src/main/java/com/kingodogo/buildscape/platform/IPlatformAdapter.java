package com.kingodogo.buildscape.platform;

import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
public interface IPlatformAdapter {
    enum BoatPlacementOutcome { PASS, FAIL, SUCCESS }

    default BoatPlacementOutcome placePoplarBoat(net.minecraft.world.item.Item boatItem, Level level,
                                                  net.minecraft.world.entity.player.Player player,
                                                  net.minecraft.world.InteractionHand hand) {
        net.minecraft.world.item.ItemStack stack = player.getItemInHand(hand);
        net.minecraft.world.phys.Vec3 eye = player.getEyePosition();
        net.minecraft.world.phys.Vec3 view = player.getViewVector(1.0F);
        net.minecraft.world.phys.HitResult hit = level.clip(new net.minecraft.world.level.ClipContext(
                eye, eye.add(view.scale(5.0D)), net.minecraft.world.level.ClipContext.Block.OUTLINE,
                net.minecraft.world.level.ClipContext.Fluid.ANY, player));
        if (hit.getType() == net.minecraft.world.phys.HitResult.Type.MISS) return BoatPlacementOutcome.PASS;

        java.util.List<Entity> entities = level.getEntities(player,
                player.getBoundingBox().expandTowards(view.scale(5.0D)).inflate(1.0D),
                net.minecraft.world.entity.EntitySelector.NO_SPECTATORS.and(Entity::isPickable));
        for (Entity entity : entities) {
            if (entity.getBoundingBox().inflate(entity.getPickRadius()).contains(eye)) return BoatPlacementOutcome.PASS;
        }
        if (hit.getType() != net.minecraft.world.phys.HitResult.Type.BLOCK) return BoatPlacementOutcome.PASS;

        Entity boat = createBoatEntity(level, hit.getLocation().x, hit.getLocation().y, hit.getLocation().z, true);
        if (boat == null) return BoatPlacementOutcome.PASS;
        boat.setYRot(player.getYRot());
        if (!level.noCollision(boat, boat.getBoundingBox())) return BoatPlacementOutcome.FAIL;
        if (!level.isClientSide()) {
            level.addFreshEntity(boat);
            net.minecraft.world.phys.Vec3 location = hit.getLocation();
            level.gameEvent(player, net.minecraft.world.level.gameevent.GameEvent.ENTITY_PLACE,
                    new net.minecraft.core.BlockPos((int)Math.floor(location.x), (int)Math.floor(location.y), (int)Math.floor(location.z)));
            if (!player.getAbilities().instabuild) stack.shrink(1);
        }
        player.awardStat(net.minecraft.stats.Stats.ITEM_USED.get(boatItem));
        return BoatPlacementOutcome.SUCCESS;
    }
    String getPlatformName();
    boolean isModLoaded(String modId);
    boolean isClient();
    boolean hasCurrentUser();
    String getCurrentUserUuid();
    int getClientParticleSetting();
    boolean hasShiftDown();
    boolean hasControlDown();
    boolean widgetMouseClicked(net.minecraft.client.gui.components.AbstractWidget widget, double mouseX, double mouseY, int button);
    boolean widgetKeyPressed(net.minecraft.client.gui.components.AbstractWidget widget, int keyCode, int scanCode, int modifiers);
    boolean widgetCharTyped(net.minecraft.client.gui.components.AbstractWidget widget, char codePoint, int modifiers);
    void setEditBoxFilter(net.minecraft.client.gui.components.EditBox editBox, java.util.function.Predicate<String> filter);
    void enableScissor(Object graphics, int x, int y, int width, int height);
    void disableScissor(Object graphics);
    CommonId registerDynamicTexture(String name, net.minecraft.client.renderer.texture.DynamicTexture texture);
    net.minecraft.client.renderer.texture.DynamicTexture createDynamicTexture(String name, int width, int height, boolean clear);
    boolean getGameRuleBoolean(net.minecraft.world.level.Level level, Object ruleKey, boolean fallback);
    Iterable<net.minecraft.world.item.ItemStack> getInventoryItems(net.minecraft.world.entity.player.Inventory inventory);
    boolean hasPlayerPermissions(net.minecraft.world.entity.player.Player player, int level);
    void openScreen(net.minecraft.client.gui.screens.Screen screen);
    void openUri(java.net.URI uri);
    void setNativeImagePixel(com.mojang.blaze3d.platform.NativeImage image, int x, int y, int abgr);
    void beginGuiOverlayRender();
    void endGuiOverlayRender();
    void resetShaderColor();
    void renderPillarMarkers(com.mojang.blaze3d.vertex.PoseStack poseStack, net.minecraft.client.Camera camera);
    float getWalkDistance(net.minecraft.world.entity.player.Player player);
    boolean isWalkAnimationMoving(net.minecraft.world.entity.player.Player player);
    net.minecraft.network.chat.Component parseComponentJson(String json);
    net.minecraft.client.KeyMapping createKeyMapping(String translationKey, int keyCode, String categoryTranslationKey);
    net.minecraft.world.level.block.RenderShape getEntityBlockRenderShape();
    net.minecraft.world.entity.player.Player getClientPlayer();

    Block getBlock(CommonId id);
    CommonId getBlockId(Block block);

    Item getItem(CommonId id);
    CommonId getItemId(Item item);
    default Item.Properties prepareItemProperties(CommonId id, Item.Properties properties) {
        return properties;
    }
    int getItemRawId(Item item);
    Item getItemByRawId(int rawId);

    Fluid getFluid(CommonId id);
    CommonId getFluidId(Fluid fluid);
    Fluid getBucketFluid(Item item);

    Entity createSeatEntity(Level level, double x, double y, double z);
    Entity createFallingIcicleEntity(Level level, double x, double y, double z, net.minecraft.world.level.block.state.BlockState state);
    Entity createWanderingHomemakerEntity(Level level, boolean festive);
    Entity createBoatEntity(Level level, double x, double y, double z, boolean poplar);
    Entity createColoredItemFrameEntity(Level level, net.minecraft.core.BlockPos pos, net.minecraft.core.Direction direction, String color);

    Entity createFestiveStockingEntity(Level level, net.minecraft.core.BlockPos pos, net.minecraft.core.Direction direction, String color);

    default Entity createArmorStandEntity(Level level, double x, double y, double z) {
        return null;
    }


    net.minecraft.world.entity.EntityType<?> getFallingIcicleEntityType();

    net.minecraft.world.entity.EntityType<?> getFestiveStockingEntityType();
    net.minecraft.world.entity.EntityType<?> getMangroveBoatEntityType();
    net.minecraft.world.entity.EntityType<?> getColoredItemFrameEntityType();
    net.minecraft.world.entity.EntityType<?> getSeatEntityType();
    net.minecraft.world.entity.EntityType<?> getPoplarBoatEntityType();
    net.minecraft.world.entity.EntityType<?> getWanderingHomemakerEntityType();
    net.minecraft.world.entity.EntityType<?> getFestiveWanderingHomemakerEntityType();

    Level getEntityLevel(Entity entity);

    Iterable<Item> getAllItems();
    net.minecraft.resources.ResourceKey<net.minecraft.core.Registry<Item>> getItemRegistryKey();

    <V> void register(net.minecraft.core.Registry<V> registry, CommonId id, V value);
    default void wrapRegistryAction(Runnable action) { action.run(); }

    void registerWorldGen();
    void registerParticleType(CommonId id, net.minecraft.core.particles.ParticleType<?> type);
    void registerCopperFireFlameParticle();
    void registerParticleProviders();
    void rotateX(com.mojang.blaze3d.vertex.PoseStack poseStack, float degrees);
    void rotateY(com.mojang.blaze3d.vertex.PoseStack poseStack, float degrees);
    void rotateZ(com.mojang.blaze3d.vertex.PoseStack poseStack, float degrees);
    void fill(Object poseStackOrGraphics, int minX, int minY, int maxX, int maxY, int color);
    void drawShadow(Object poseStackOrGraphics, net.minecraft.client.gui.Font font, String text, int x, int y, int color);
    void draw(Object poseStackOrGraphics, net.minecraft.client.gui.Font font, String text, float x, float y, int color);
    void drawCenteredString(Object poseStackOrGraphics, net.minecraft.client.gui.Font font, net.minecraft.network.chat.Component component, int x, int y, int color);
    void drawCenteredString(Object poseStackOrGraphics, net.minecraft.client.gui.Font font, String text, int x, int y, int color);
    void bindTexture(CommonId id);
    void blit(Object poseStackOrGraphics, CommonId texture, int x, int y, float u, float v, int width, int height, int sheetW, int sheetH);
    void blit(Object poseStackOrGraphics, CommonId texture, int x, int y, int width, int height, float u, float v, int uWidth, int vHeight, int sheetW, int sheetH);
    void renderComponentTooltip(Object poseStackOrGraphics, net.minecraft.client.gui.Font font, java.util.List<net.minecraft.network.chat.Component> components, int mouseX, int mouseY);
    java.util.List<net.minecraft.network.chat.Component> getTooltipFromItem(net.minecraft.world.item.ItemStack stack);
    com.mojang.blaze3d.vertex.PoseStack toPoseStack(Object poseStackOrGraphics);
    void pushGuiPose(Object poseStackOrGraphics);
    void popGuiPose(Object poseStackOrGraphics);
    void translateGuiPose(Object poseStackOrGraphics, float x, float y, float z);
    void scaleGuiPose(Object poseStackOrGraphics, float x, float y);
    void renderClientOverlay(Object poseStackOrGraphics, int width, int height);
    void renderWidget(Object poseStackOrGraphics, net.minecraft.client.gui.components.AbstractWidget widget, int mouseX, int mouseY, float partialTick);
    net.minecraft.client.gui.components.Button createButton(int x, int y, int width, int height, net.minecraft.network.chat.Component message, net.minecraft.client.gui.components.Button.OnPress onPress);
    net.minecraft.client.gui.components.Button createCustomButton(int x, int y, int width, int height, net.minecraft.network.chat.Component message, net.minecraft.client.gui.components.Button.OnPress onPress, com.kingodogo.buildscape.client.screen.widget.CustomButtonRenderer renderer);
    net.minecraft.client.gui.components.AbstractWidget wrapCustomWidget(int x, int y, int width, int height, net.minecraft.network.chat.Component message, com.kingodogo.buildscape.client.screen.widget.ICustomWidget customWidget);
    CommonId getEntityTypeId(net.minecraft.world.entity.EntityType<?> entityType);
    boolean hasCustomHoverName(net.minecraft.world.item.ItemStack stack);
    net.minecraft.world.entity.EquipmentSlot getEquipmentSlot(net.minecraft.world.item.ItemStack stack);
    net.minecraft.world.phys.AABB getModelBounds(Object model);
    void renderItemFixed(net.minecraft.world.item.ItemStack stack, com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource, int combinedLight, int combinedOverlay, Object model);
    void renderItemStatic(net.minecraft.world.item.ItemStack stack, com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource, int combinedLight, int combinedOverlay, int seed);
    void registerRecipeSerializers();
    byte[] readResourceBytes(net.minecraft.server.packs.resources.ResourceManager resourceManager, CommonId location);
    net.minecraft.server.packs.resources.PreparableReloadListener createRecipeReloadListener();
    void injectRecipes(net.minecraft.world.item.crafting.RecipeManager recipeManager, java.util.List<com.kingodogo.buildscape.recipe.framework.parser.RecipeIR.CompiledRecipe> recipes);

    net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider createRandomStateProvider(java.util.List<net.minecraft.world.level.block.state.BlockState> states);
    net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator getCreakingHeartTreeDecorator();
    net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator createMangroveLeaveVineDecorator(float probability);
    net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator createMangroveMossCarpetDecorator(float probability);
    net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator createMangrovePropaguleDecorator(float probability, net.minecraft.util.valueproviders.IntProvider p1, net.minecraft.util.valueproviders.IntProvider p2, int requiredEmptyBlocks);
    net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator createMangroveRootDecorator(net.minecraft.util.valueproviders.IntProvider p1, net.minecraft.util.valueproviders.IntProvider p2, float probability, net.minecraft.util.valueproviders.IntProvider p3);
    net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer createMangroveUpwardsBranchingTrunkPlacer(int baseHeight, int heightRandA, int heightRandB, net.minecraft.util.valueproviders.IntProvider extraBranchSteps, float placeBranchPerLogProbability, net.minecraft.util.valueproviders.IntProvider extraBranchLength);
    net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer createMangroveRandomSpreadFoliagePlacer(net.minecraft.util.valueproviders.IntProvider radius, net.minecraft.util.valueproviders.IntProvider offset, net.minecraft.util.valueproviders.IntProvider foliageHeight, int leafPlacementAttempts);
    net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration createTreeConfiguration(
            net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider trunkProvider,
            net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer trunkPlacer,
            net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider foliageProvider,
            net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer foliagePlacer,
            net.minecraft.world.level.levelgen.feature.featuresize.FeatureSize featureSize,
            java.util.List<net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator> decorators,
            boolean ignoreVines
    );
    net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider createWeightedStateProvider(
            java.util.List<net.minecraft.world.level.block.state.BlockState> states,
            java.util.List<java.lang.Integer> weights
    );
    net.minecraft.world.level.levelgen.feature.ConfiguredFeature<?, ?> createPatchFeature(
            net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider stateProvider,
            int tries,
            int xzSpread,
            int ySpread
    );
    void registerConfiguredFeature(CommonId id, Object configuredFeature);
    net.minecraft.world.item.CreativeModeTab createCreativeTab(CommonId tabId, String titleKey, java.util.function.Supplier<net.minecraft.world.item.ItemStack> iconSupplier, java.util.List<String> orderedItemIds, java.util.Map<String, Item> items);
    Item createBiomeBrushItem(com.kingodogo.buildscape.item.BiomeBrushItem.BiomeBrushTier tier, Item.Properties properties);
    boolean supportsBiomeBrushEnchantment(net.minecraft.world.item.ItemStack stack, Object enchantment);
    net.minecraft.world.item.BlockItem createTrophyBlockItem(com.kingodogo.buildscape.trophy.TrophyBlock block, com.kingodogo.buildscape.trophy.TrophyDefinition definition, Item.Properties properties);
    com.kingodogo.buildscape.trophy.TrophyBlock createTrophyBlock(com.kingodogo.buildscape.trophy.TrophyDefinition definition);

    net.minecraft.sounds.SoundEvent createSoundEvent(CommonId id);
    void playBlockSound(Level level, net.minecraft.core.BlockPos pos, CommonId soundId);
    void neighborChanged(Level level, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state, Block fromBlock, net.minecraft.core.BlockPos fromPos);

    void hurtAndBreak(net.minecraft.world.item.ItemStack stack, int amount, net.minecraft.world.entity.LivingEntity entity, net.minecraft.world.InteractionHand hand);

    void sendActionBarMessage(net.minecraft.world.entity.player.Player player, net.minecraft.network.chat.Component message);
    void sendSystemMessage(net.minecraft.world.entity.player.Player player, net.minecraft.network.chat.Component message);
    void sendCommandSuccess(net.minecraft.commands.CommandSourceStack source, String message, boolean broadcastToOps);
    void sendCommandFailure(net.minecraft.commands.CommandSourceStack source, String message);
    boolean hasCommandPermission(net.minecraft.commands.CommandSourceStack source, int level);

    net.minecraft.network.chat.MutableComponent literal(String text);
    net.minecraft.network.chat.MutableComponent translatable(String key);
    net.minecraft.network.chat.MutableComponent translatable(String key, Object... args);
    void setItemCustomName(net.minecraft.world.item.ItemStack stack, net.minecraft.network.chat.Component name);
    String getItemCustomName(net.minecraft.world.item.ItemStack stack);

    void playButtonClick(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos);
    void playButtonClick(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos, float pitch);
    void playNoteHarp(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos, float pitch);
    void playEatSound(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos);
    void playDrinkSound(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos);
    void playHoneyDrinkSound(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos);
    void playFlintAndSteelSound(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos);
    void playCandleExtinguishSound(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos);
    void playCandleAmbientSound(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos, float pitch);
    void playCandleAmbientLocalSound(net.minecraft.world.level.Level level, double x, double y, double z, float volume, float pitch);
    void playVegetationStepSound(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos, net.minecraft.world.entity.Entity entity, net.minecraft.world.level.block.SoundType sounds);

    void spawnDustParticles(net.minecraft.server.level.ServerLevel level, double x, double y, double z, int color, float scale);

    net.minecraft.world.item.DyeColor getDyeColor(net.minecraft.world.item.ItemStack stack);
    int getDyeColorValue(net.minecraft.world.item.DyeColor color);

    void syncChunk(net.minecraft.server.level.ServerLevel level, net.minecraft.world.level.chunk.LevelChunk chunk);

    void markChunkUnsaved(net.minecraft.world.level.chunk.LevelChunk chunk);

    void setSectionBiome(net.minecraft.world.level.chunk.LevelChunkSection section, int localQx, int localQy, int localQz, net.minecraft.core.Holder<net.minecraft.world.level.biome.Biome> biome);

    CommonId getBiomeId(net.minecraft.world.level.Level level, net.minecraft.core.Holder<net.minecraft.world.level.biome.Biome> biomeHolder);

    net.minecraft.core.Holder<net.minecraft.world.level.biome.Biome> getBiomeHolder(net.minecraft.server.level.ServerLevel level, CommonId id);

    int getUnbreakingLevel(net.minecraft.world.item.ItemStack stack);
    boolean isArmorItem(net.minecraft.world.item.ItemStack stack);
    net.minecraft.world.item.ItemStack getCraftingRemainingItem(net.minecraft.world.item.ItemStack stack);
    int getEfficiencyLevel(net.minecraft.world.item.ItemStack stack);
    float getHoeMiningSpeed(net.minecraft.world.item.ItemStack stack);
    boolean hasSilkTouch(net.minecraft.world.item.ItemStack stack);
    boolean isReplaceable(net.minecraft.world.level.block.state.BlockState state);
    boolean isGlassBlock(net.minecraft.world.level.block.Block block);

    void awardAdvancement(net.minecraft.server.level.ServerPlayer player, CommonId advancementId, String criterion);
    void awardStat(net.minecraft.server.level.ServerPlayer player, CommonId statId);

    net.minecraft.stats.Stat<?> registerCustomStat(String registryName, CommonId statId);

    net.minecraft.nbt.CompoundTag getCustomData(net.minecraft.world.item.ItemStack stack, boolean create);

    void updateCustomData(net.minecraft.world.item.ItemStack stack, java.util.function.Consumer<net.minecraft.nbt.CompoundTag> updater);

    void configureFireworkStar(net.minecraft.world.item.ItemStack stack, byte shapeId, int[] colors, boolean flicker, boolean trail);

    void configureFireworkRocket(net.minecraft.world.item.ItemStack stack, int flight, byte shapeId, int[] colors, boolean flicker, boolean trail);

    int[] getFireworkStarColors(net.minecraft.world.item.ItemStack stack);

    int getDyeFireworkColor(net.minecraft.world.item.ItemStack stack);
    CommonId getDimensionId(net.minecraft.world.level.Level level);
    boolean isNight(net.minecraft.world.level.Level level);
    long packChunkPos(int chunkX, int chunkZ);
    long packChunkPos(net.minecraft.world.level.ChunkPos chunkPos);
    long packChunkPos(net.minecraft.core.BlockPos pos);
    boolean isItemInTag(net.minecraft.world.item.ItemStack stack, CommonId tagId);

    boolean isBlockInTag(net.minecraft.world.level.block.Block block, CommonId tagId);

    boolean isEntityTypeInTag(net.minecraft.world.entity.Entity entity, CommonId tagId);

    void markEntityVelocityChanged(net.minecraft.world.entity.Entity entity);

    net.minecraft.world.InteractionResult sidedSuccess(boolean clientSide);
    boolean doesBedExplode(net.minecraft.world.level.Level level);
    net.minecraft.world.item.trading.MerchantOffer createMerchantOffer(net.minecraft.world.item.ItemStack cost, net.minecraft.world.item.ItemStack result, int maxUses, int xp, float priceMultiplier);
    net.minecraft.world.item.crafting.Ingredient createTagIngredient(CommonId id);
    net.minecraft.tags.TagKey<net.minecraft.world.item.Item> createItemTagKey(CommonId id);

    boolean isGlassBaseBlock(net.minecraft.world.level.block.Block block);

    boolean isEnchantingTableBlock(net.minecraft.world.level.block.Block block);
    boolean isFood(net.minecraft.world.item.ItemStack stack);
    boolean isWaterPotion(net.minecraft.world.item.ItemStack stack);

    boolean isEnchantedItem(net.minecraft.world.item.ItemStack stack);
    net.minecraft.world.item.ItemStack createWaterPotion();
    void applyFoodEffects(net.minecraft.world.entity.player.Player player, net.minecraft.world.level.Level level, net.minecraft.world.item.ItemStack foodStack);
    void applyPotionEffects(net.minecraft.world.entity.LivingEntity entity, net.minecraft.world.item.ItemStack potionStack);
    String getTagString(net.minecraft.nbt.CompoundTag tag, String key, String defaultValue);
    int getTagInt(net.minecraft.nbt.CompoundTag tag, String key, int defaultValue);
    boolean getTagBoolean(net.minecraft.nbt.CompoundTag tag, String key, boolean defaultValue);
    long getTagLong(net.minecraft.nbt.CompoundTag tag, String key, long defaultValue);
    boolean hasTagUUID(net.minecraft.nbt.CompoundTag tag, String key);
    java.util.UUID getTagUUID(net.minecraft.nbt.CompoundTag tag, String key);
    void putTagUUID(net.minecraft.nbt.CompoundTag tag, String key, java.util.UUID uuid);
    int getMaxBuildHeight(net.minecraft.world.level.Level level);
    net.minecraft.core.BlockPos getSharedSpawnPos(net.minecraft.world.level.Level level);
    net.minecraft.world.item.Item createConfettiItem(net.minecraft.world.item.Item.Properties properties);
    net.minecraft.world.item.Item createBottleOfMistItem(net.minecraft.world.item.Item.Properties properties);
    net.minecraft.world.item.Item createWrenchItem(net.minecraft.world.item.Item.Properties properties);
    net.minecraft.world.item.Item createFestiveGlintShardItem(net.minecraft.world.item.Item.Properties properties);
    net.minecraft.world.item.Item createBigOrnamentTemplateItem(net.minecraft.world.item.Item.Properties properties);
    net.minecraft.world.item.Item createStringlightFrameItem(net.minecraft.world.item.Item.Properties properties);
    net.minecraft.nbt.CompoundTag getTagCompound(net.minecraft.nbt.CompoundTag tag, String key);
    java.util.Set<String> getTagKeys(net.minecraft.nbt.CompoundTag tag);
    net.minecraft.nbt.CompoundTag readCompressedTag(java.io.File file) throws java.io.IOException;
    void writeCompressedTag(java.io.File file, net.minecraft.nbt.CompoundTag tag) throws java.io.IOException;
    java.util.List<String> getTagStringList(net.minecraft.nbt.CompoundTag tag, String key);
    void putTagStringList(net.minecraft.nbt.CompoundTag tag, String key, java.util.List<String> list);
    void spawnMob(net.minecraft.server.level.ServerLevel level, net.minecraft.world.entity.EntityType<?> type, net.minecraft.world.item.ItemStack stack, net.minecraft.core.BlockPos pos);
    boolean isSameItemSameComponents(net.minecraft.world.item.ItemStack a, net.minecraft.world.item.ItemStack b);
    void applyNauseaEffect(net.minecraft.world.entity.LivingEntity entity, int durationTicks, int amplifier);
    void hurtFreezeDamage(net.minecraft.world.entity.LivingEntity entity, float amount);
    int getSelectedSlot(net.minecraft.world.entity.player.Inventory inventory);
    <T extends net.minecraft.world.level.block.entity.BlockEntity> net.minecraft.world.level.block.entity.BlockEntityType<T> createBlockEntityType(BlockEntityFactory<T> factory, java.util.function.Predicate<net.minecraft.world.level.block.state.BlockState> isValid);
    boolean isOnGround(net.minecraft.world.entity.Entity entity);
    void spawnMobFromEgg(net.minecraft.server.level.ServerLevel level, net.minecraft.core.BlockPos pos, net.minecraft.world.item.ItemStack eggStack);
    net.minecraft.world.item.Item createBuildersPouchItem(net.minecraft.world.item.Item.Properties properties);
    net.minecraft.world.item.Item createHammerItem(com.kingodogo.buildscape.item.HammerItem.HammerTier tier, net.minecraft.world.item.Item.Properties properties);
    boolean supportsHammerEnchantment(net.minecraft.world.item.ItemStack stack, Object enchantment);
    void playNoteBlockChime(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos, float pitch);
    net.minecraft.nbt.CompoundTag getEntityData(net.minecraft.world.entity.Entity entity);
    net.minecraft.nbt.CompoundTag getBlockEntityData(net.minecraft.world.level.block.entity.BlockEntity be);

    boolean hasEntityTag(net.minecraft.world.entity.Entity entity, String tag);

    boolean addEntityTag(net.minecraft.world.entity.Entity entity, String tag);

    boolean removeEntityTag(net.minecraft.world.entity.Entity entity, String tag);
    void loadAllItems(net.minecraft.nbt.CompoundTag tag, net.minecraft.core.NonNullList<net.minecraft.world.item.ItemStack> items);
    void saveAllItems(net.minecraft.nbt.CompoundTag tag, net.minecraft.core.NonNullList<net.minecraft.world.item.ItemStack> items);
    net.minecraft.world.item.ItemStack loadSingleItemStack(net.minecraft.nbt.CompoundTag tag);
    net.minecraft.nbt.CompoundTag saveSingleItemStack(net.minecraft.world.item.ItemStack stack);
    net.minecraft.world.item.Item createPatternItem(net.minecraft.world.item.Item.Properties properties, String tooltipKey);
    net.minecraft.world.item.BlockItem createGlassJarItem(net.minecraft.world.level.block.Block block, net.minecraft.world.item.Item.Properties properties);
    net.minecraft.world.item.BlockItem createGoldenJarItem(net.minecraft.world.level.block.Block block, net.minecraft.world.item.Item.Properties properties);
    net.minecraft.world.item.BlockItem createFestiveStarItem(net.minecraft.world.level.block.Block block, net.minecraft.world.item.Item.Properties properties);
    net.minecraft.world.item.BlockItem createMuffBlockItem(net.minecraft.world.level.block.Block block, net.minecraft.world.item.Item.Properties properties);
    net.minecraft.world.item.BlockItem createMistBlockItem(net.minecraft.world.level.block.Block block, net.minecraft.world.item.Item.Properties properties);
    net.minecraft.world.item.BlockItem createFestiveStockingItem(net.minecraft.world.level.block.Block block, net.minecraft.world.item.Item.Properties properties, String colorVariant);
    net.minecraft.world.item.BlockItem createCopperChestItem(net.minecraft.world.level.block.Block block, net.minecraft.world.item.Item.Properties properties);
    net.minecraft.world.item.Item createInfinitePhoenixFireworkStarItem(net.minecraft.world.item.Item.Properties properties);
    net.minecraft.world.item.Item createColoredItemFrameItem(net.minecraft.world.item.Item.Properties properties, String colorVariant);
    net.minecraft.world.item.Item createExperienceBucketItem(net.minecraft.world.item.Item.Properties properties);
    net.minecraft.world.item.Item createMangroveBoatItem(net.minecraft.world.item.Item.Properties properties);
    net.minecraft.world.item.Item createPoplarBoatItem(net.minecraft.world.item.Item.Properties properties);
    net.minecraft.world.item.ItemStack removeItem(java.util.List<net.minecraft.world.item.ItemStack> items, int slot, int amount);
    net.minecraft.world.item.ItemStack takeItem(java.util.List<net.minecraft.world.item.ItemStack> items, int slot);
    int applyBiomeToArea(net.minecraft.server.level.ServerLevel level, net.minecraft.core.BlockPos pos1, net.minecraft.core.BlockPos pos2, net.minecraft.core.Holder<net.minecraft.world.level.biome.Biome> biomeHolder, net.minecraft.world.item.ItemStack stack);
    void scanChunkForBlock(net.minecraft.server.level.ServerLevel level, net.minecraft.world.level.chunk.LevelChunk chunk, java.util.function.Predicate<net.minecraft.world.level.block.state.BlockState> predicate, java.util.function.BiConsumer<net.minecraft.core.BlockPos, net.minecraft.world.level.block.state.BlockState> action);
    void markChunkUnsaved(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos);
    void playWoolBreak(net.minecraft.world.level.Level level, double x, double y, double z);
    void playExperienceOrbPickup(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos);
    void playExperienceOrbPickup(net.minecraft.world.level.Level level, double x, double y, double z);
    void playExperienceOrbPickup(net.minecraft.world.level.Level level, double x, double y, double z, float volume, float pitch);
    void playDispenserFail(net.minecraft.world.level.Level level, double x, double y, double z);
    void playBoneMealUse(net.minecraft.world.level.Level level, double x, double y, double z);
    void playDragonBreathFill(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos);
    void playFireExtinguish(net.minecraft.world.level.Level level, net.minecraft.world.entity.player.Player player, net.minecraft.core.BlockPos pos);
    void playFireExtinguish(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos);
    void playFireExtinguish(net.minecraft.world.level.Level level, double x, double y, double z);
    void playWaterAmbient(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos);
    void playPowderSnowStep(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos);
    void playWaxOn(net.minecraft.world.level.Level level, net.minecraft.world.entity.player.Player player, net.minecraft.core.BlockPos pos);
    default void playWaxOn(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos, net.minecraft.world.entity.player.Player player) {
        playWaxOn(level, player, pos);
    }
    void playWaxOff(net.minecraft.world.level.Level level, net.minecraft.world.entity.player.Player player, net.minecraft.core.BlockPos pos);
    default void playWaxOff(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos, net.minecraft.world.entity.player.Player player) {
        playWaxOff(level, player, pos);
    }
    void playAxeScrape(net.minecraft.world.level.Level level, net.minecraft.world.entity.player.Player player, net.minecraft.core.BlockPos pos);
    default void playAxeScrape(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos, net.minecraft.world.entity.player.Player player) {
        playAxeScrape(level, player, pos);
    }
    void playAxeStrip(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos);
    void playBottleFill(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos);
    void playBottleEmpty(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos);
    void playBucketFill(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos);
    void playBucketEmpty(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos);
    void playBucketFillFluid(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos, boolean isLava);
    void playBucketEmptyFluid(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos, boolean isLava);
    void playItemPickup(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos, float pitch);
    void playItemPickup(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos, float volume, float pitch);
    void playLeverClick(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos, float pitch);
    void playStoneButtonClick(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos, boolean on);
    void playStoneButtonClick(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos, boolean on, float volume, float pitch);
    void playEvokerPrepareSummon(net.minecraft.server.level.ServerLevel level, net.minecraft.core.BlockPos pos);
    void playDyeUse(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos);
    void playItemFrameAdd(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos);
    void playItemFrameRemove(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos);
    void playItemFrameRotate(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos);
    void playEyeblossomTransition(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos, boolean night);
    void playBoneDiceSet(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos);
    void playBoneDiceRoll(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos);
    void playShear(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos);
    void playGlassPlace(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos);
    void playGrassPlace(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos);
    void playStonePlace(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos);
    void playGlassBreak(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos);
    void playGrassBreak(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos);
    void playAnvilUse(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos);
    void playButtonClick();
    void playNoteBlockBell();
    void playNoteBlockDidgeridoo();

    boolean hasPermission(net.minecraft.world.entity.player.Player player, int permissionLevel);
    net.minecraft.server.MinecraftServer getServer(net.minecraft.world.entity.player.Player player);
    void updateGameRule(net.minecraft.world.entity.player.Player player, String ruleName, boolean value);
    float[] getDyeDiffuseColors(net.minecraft.world.item.DyeColor dyeColor);
    float getWalkDist(net.minecraft.world.entity.player.Player player);
    float getWalkDistO(net.minecraft.world.entity.player.Player player);
    void registerCommonLifecycleInteractions();
    void renderModelPart(net.minecraft.client.model.geom.ModelPart part, com.mojang.blaze3d.vertex.PoseStack poseStack, com.mojang.blaze3d.vertex.VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha);
    int getRenderDistanceChunks();
    void renderLineBox(com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float r, float g, float b, float a);
    boolean isScreenOpen();
    net.minecraft.world.phys.Vec3 getCameraPosition(net.minecraft.client.Camera camera);
    void renderFallingIcicleBlock(net.minecraft.world.level.Level level, net.minecraft.world.level.block.state.BlockState blockState, net.minecraft.core.BlockPos blockPos, net.minecraft.core.BlockPos startPos, com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource);
    int getLightColor(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos);
    boolean isMapItemWithData(net.minecraft.world.item.ItemStack stack, net.minecraft.world.level.Level level);
    void renderColoredFrame(com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource, int packedLight, Object backTexture, boolean hasMap);
    void renderColoredFrameItem(com.kingodogo.buildscape.entity.ColoredItemFrameEntity entity, net.minecraft.world.item.ItemStack itemStack, com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource, int packedLight, boolean isMap, boolean isInvisible);
    void renderStockingQuad(com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource, int packedLight, Object texture, boolean flipped);
    void renderBlockModel(net.minecraft.world.level.block.state.BlockState blockState, com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource, int light, int overlay);
    void renderColoredQuad(com.mojang.blaze3d.vertex.PoseStack poseStack, Object buffer,
                           float x0, float y0, float z0, float u0, float v0,
                           float x1, float y1, float z1, float u1, float v1,
                           float x2, float y2, float z2, float u2, float v2,
                           float x3, float y3, float z3, float u3, float v3,
                           float r, float g, float b, float a,
                           int light, int overlay,
                           float nx, float ny, float nz);
    void renderJarFluid(com.kingodogo.buildscape.block.GlassJarBlockEntity blockEntity, com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource, int light, int overlay);
    void renderBlockModelWithTint(net.minecraft.world.level.block.state.BlockState state, net.minecraft.core.BlockPos pos, net.minecraft.world.level.Level level, com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource, int light, int overlay);
    void renderGlassJar(com.kingodogo.buildscape.block.GlassJarBlockEntity blockEntity, float partialTicks, com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource, int combinedLight, int combinedOverlay);
    void renderWobblyBlock(net.minecraft.world.level.block.state.BlockState state, long currentTick, long wobbleStartTick, boolean hasWobble, float partialTicks, com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource, int light, int overlay);
    void renderCopperChest(com.kingodogo.buildscape.block.CopperChestBlockEntity blockEntity, float partialTicks, com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource, int combinedLight, int combinedOverlay);
    void registerMenuScreens();

    default void registerRenderers() {
    }

    default void registerLayerDefinitions(java.util.function.BiConsumer<Object, java.util.function.Supplier<Object>> registrar) {
    }

    default void registerEntityAttributes(java.util.function.BiConsumer<net.minecraft.world.entity.EntityType<? extends net.minecraft.world.entity.LivingEntity>, net.minecraft.world.entity.ai.attributes.AttributeSupplier> consumer) {
    }

    default void renderEntity(net.minecraft.world.entity.Entity entity, double x, double y, double z, float yaw, float partialTicks, com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource, int packedLight) {
    }

    default com.mojang.blaze3d.vertex.VertexConsumer getTranslucentBuffer(Object bufferSource) {
        return null;
    }

    default net.minecraft.client.renderer.texture.TextureAtlasSprite getBlockAtlasSprite(CommonId id) {
        return null;
    }

    default Object getItemModel(net.minecraft.world.item.ItemStack stack, net.minecraft.world.level.Level level, int seed) {
        return null;
    }

    default boolean isGui3dModel(Object model) {
        return false;
    }

    default void applyMobState(net.minecraft.world.entity.Entity entity, Object mobState) {
    }

    default int getWaterColor(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos) {
        return 0x3F76E4;
    }

    default boolean isArmor(net.minecraft.world.item.ItemStack stack) {
        return false;
    }

    default boolean isElytra(net.minecraft.world.item.ItemStack stack) {
        return stack != null && stack.is(net.minecraft.world.item.Items.ELYTRA);
    }

    default boolean isArmorStand(net.minecraft.world.item.ItemStack stack) {
        return stack != null && stack.is(net.minecraft.world.item.Items.ARMOR_STAND);
    }

    default boolean isSwordLike(net.minecraft.world.item.ItemStack stack) {
        return false;
    }

    default boolean isAxeLike(net.minecraft.world.item.ItemStack stack) {
        return false;
    }

    default boolean isWeaponOrTool(net.minecraft.world.item.ItemStack stack) {
        return isSwordLike(stack) || isAxeLike(stack);
    }

    default void setupArmorStand(net.minecraft.world.entity.Entity stand) {
    }

    default void updateArmorStand(net.minecraft.world.entity.Entity armorStand, net.minecraft.world.item.ItemStack stack, net.minecraft.world.entity.EquipmentSlot slot, boolean isStandItem) {
    }

    default net.minecraft.world.entity.Entity createMobPillarEntity(net.minecraft.world.item.ItemStack spawnEggStack, net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos, Object mobState) {
        return null;
    }

    record BlockColorSample(int rgb, boolean transparent, boolean singleTexture) {}
    default BlockColorSample sampleBlockColor(net.minecraft.world.level.block.state.BlockState state) { return null; }
    default boolean isTranslucent(net.minecraft.world.level.block.state.BlockState state) { return false; }
    default net.minecraft.client.gui.screens.Screen createConfigScreen(net.minecraft.client.gui.screens.Screen parent) { return null; }
    default net.minecraft.client.gui.screens.Screen wrapScreen(net.minecraft.network.chat.Component title, net.minecraft.client.gui.screens.Screen parent, com.kingodogo.buildscape.client.screen.IScreenDelegate delegate) { return null; }
    default net.minecraft.client.gui.screens.Screen createGuiEditorScreen(net.minecraft.client.gui.screens.Screen parent, String tabName, com.kingodogo.buildscape.client.screen.AbstractConfigTab sourceTab) { return null; }
    default net.minecraft.client.gui.screens.Screen createInventoryItemSelectorScreen(net.minecraft.client.gui.screens.Screen parent, com.kingodogo.buildscape.client.screen.PillarItemsConfigTab configTab) { return null; }
    void renderGuiItem(Object poseStackOrGraphics, net.minecraft.world.item.ItemStack stack, int x, int y);
    void renderGuiItemDecorations(Object poseStackOrGraphics, net.minecraft.client.gui.Font font, net.minecraft.world.item.ItemStack stack, int x, int y);
}
