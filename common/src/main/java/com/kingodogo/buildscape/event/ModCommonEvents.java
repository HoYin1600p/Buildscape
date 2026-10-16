package com.kingodogo.buildscape.event;

import com.kingodogo.buildscape.config.PillarIdManager;
import com.kingodogo.buildscape.config.PillarParticleConfig;
import com.kingodogo.buildscape.cosmetic.sign.SignFrameInteractionHandler;
import com.kingodogo.buildscape.network.PacketFactory;
import com.kingodogo.buildscape.network.SyncConfigPacket;
import com.kingodogo.buildscape.network.SyncGameRulesPacket;
import com.kingodogo.buildscape.network.SyncHomemakerCooldownPacket;
import com.kingodogo.buildscape.network.SyncPillarIdsPacket;
import com.kingodogo.buildscape.network.TreeChopJobManager;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.world.ModGameRules;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
public final class ModCommonEvents {

    private static final PillarMaintenanceSchedule PILLAR_MAINTENANCE = new PillarMaintenanceSchedule();

    private ModCommonEvents() {}

    public static void onBlockPlaced(Level level, BlockPos pos, BlockState state, @Nullable Player player) {
        SafeEventHooks.run("onBlockPlaced", () -> onBlockPlacedUnchecked(level, pos, state, player));
    }

    private static void onBlockPlacedUnchecked(Level level, BlockPos pos, BlockState state, @Nullable Player player) {
        MudToClayHandler.onBlockPlace(level, pos, state);

        if (player != null) {
            WanderingHomemakerSpawningHandler.onBlockPlaced(level, pos, state, player);

            if (player instanceof ServerPlayer serverPlayer) {
                AdvancementMilestoneLogic.onBlockPlaced(serverPlayer, level, pos, state);
            }
        }
    }

    public static InteractionResult onRightClickBlock(Player player, Level level, InteractionHand hand, BlockPos pos) {
        return onRightClickBlock(player, level, hand, pos, null);
    }

    public static InteractionResult onRightClickBlock(Player player, Level level, InteractionHand hand, BlockPos pos, net.minecraft.core.Direction face) {
        return SafeEventHooks.call("onRightClickBlock", () -> onRightClickBlockUnchecked(player, level, hand, pos, face), InteractionResult.PASS);
    }

    private static InteractionResult onRightClickBlockUnchecked(Player player, Level level, InteractionHand hand, BlockPos pos, net.minecraft.core.Direction face) {
        if (player == null || level == null || pos == null) return InteractionResult.PASS;
        ItemStack potHeld = player.getItemInHand(hand);
        if (level.getBlockState(pos).is(net.minecraft.world.level.block.Blocks.FLOWER_POT)
                && !player.isSecondaryUseActive()
                && potHeld.getItem() instanceof com.kingodogo.buildscape.item.HazeBushItem item
                && com.kingodogo.buildscape.item.HazeBushItem.isDrained(potHeld)
                && item.getBlock() instanceof com.kingodogo.buildscape.block.HazeBushBlock bush) {
            Block pot = com.kingodogo.buildscape.block.ModBlocks.get(
                    com.kingodogo.buildscape.block.ModBlocks.POTTED_COLORED_HAZE_BUSHES.get(bush.getColor()));
            if (!level.isClientSide()) {
                level.setBlock(pos, pot.defaultBlockState().setValue(com.kingodogo.buildscape.block.HazeBushBlock.HAS_HAZE, false), 3);
                player.awardStat(net.minecraft.stats.Stats.POT_FLOWER);
                if (!player.getAbilities().instabuild) potHeld.shrink(1);
            }
            return Services.PLATFORM.sidedSuccess(level.isClientSide());
        }
        InteractionResult bonemeal = BackportBonemealHandler.use(player, level, hand, pos, face);
        if (bonemeal != InteractionResult.PASS) return bonemeal;
        BlockState state = level.getBlockState(pos);
        ItemStack held = player.getItemInHand(hand);
        if (state.getBlock() instanceof com.kingodogo.buildscape.block.SmokeVentBlock vent
                && Services.PLATFORM.getDyeColor(held) != null) {
            InteractionResult result = vent.onInteract(level, pos, state, player, hand,
                    new net.minecraft.world.phys.BlockHitResult(net.minecraft.world.phys.Vec3.atCenterOf(pos),
                            face == null ? net.minecraft.core.Direction.UP : face, pos, false));
            if (result == InteractionResult.SUCCESS && player instanceof ServerPlayer serverPlayer) {
                AdvancementEvents.incrementStat(serverPlayer, "smoke_vents_dyed", 1);
                AdvancementEvents.grant(serverPlayer, "colorful_smoke");
            }
            return result;
        }
        InteractionResult signResult = SignFrameInteractionHandler.handleRightClick(player, level, hand, pos);
        if (signResult != InteractionResult.PASS) {
            return signResult;
        }
        InteractionResult copperResult = Services.PLATFORM.handleCopperRightClick(level, pos, state, held, player, hand);
        if (copperResult != InteractionResult.PASS) {
            return copperResult;
        }
        if (state.getBlock() instanceof VineBlock && held.is(net.minecraft.world.item.Items.SHEARS)) {
            if (state.hasProperty(com.kingodogo.buildscape.block.ModBlockProperties.SHEARED)
                    && !state.getValue(com.kingodogo.buildscape.block.ModBlockProperties.SHEARED)) {
                if (!level.isClientSide()) {
                    level.setBlockAndUpdate(pos, state.setValue(com.kingodogo.buildscape.block.ModBlockProperties.SHEARED, true));
                    level.playSound(null, pos, SoundEvents.GROWING_PLANT_CROP, SoundSource.BLOCKS, 1.0F, 1.0F);
                    Services.PLATFORM.hurtAndBreak(held, 1, player, hand);
                }
                Services.PLATFORM.swingPlayerHand(player, hand);
                return Services.PLATFORM.sidedSuccess(level.isClientSide());
            }
        }
        if (state.is(net.minecraft.world.level.block.Blocks.DIRT) && Services.PLATFORM.isWaterPotion(held)) {
            if (!level.isClientSide()) {
                Block mudBlock = Services.PLATFORM.getBlock(new com.kingodogo.buildscape.util.CommonId("buildscape", "mud"));
                if (mudBlock != null && mudBlock != net.minecraft.world.level.block.Blocks.AIR) {
                    level.setBlock(pos, mudBlock.defaultBlockState(), 3);
                    level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                    if (!player.getAbilities().instabuild) {
                        held.shrink(1);
                        ItemStack bottle = new ItemStack(net.minecraft.world.item.Items.GLASS_BOTTLE);
                        if (held.isEmpty()) {
                            player.setItemInHand(hand, bottle);
                        } else if (!player.getInventory().add(bottle)) {
                            Services.PLATFORM.dropPlayerItem(player, bottle, false);
                        }
                    }
                }
            }
            Services.PLATFORM.swingPlayerHand(player, hand);
            return Services.PLATFORM.sidedSuccess(level.isClientSide());
        }
        return LogStrippingLogic.handleAxeStrip(player, level, hand, pos);
    }

    public static InteractionResult onLeftClickBlock(Player player, Level level, BlockPos pos) {
        return SafeEventHooks.call("onLeftClickBlock", () -> onLeftClickBlockUnchecked(player, level, pos), InteractionResult.PASS);
    }

    private static InteractionResult onLeftClickBlockUnchecked(Player player, Level level, BlockPos pos) {
        if (player == null || level == null || pos == null || player.isSpectator()) return InteractionResult.PASS;
        ItemStack held = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (held.getItem() instanceof com.kingodogo.buildscape.item.BiomeBrushItem brush) {
            if (!level.isClientSide()) {
                if (held.getDamageValue() >= held.getMaxDamage()) {
                    level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 1.0f, 0.8f);
                    return InteractionResult.SUCCESS;
                }
                if (player.isShiftKeyDown()) {
                    brush.clearCapturedBiome(held, player);
                } else {
                    brush.setPos2(held, pos, player);
                }
            }
            return InteractionResult.SUCCESS;
        }

        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        if (block instanceof com.kingodogo.buildscape.block.PetalBlock
                || block instanceof com.kingodogo.buildscape.block.CloverBlock
                || block instanceof com.kingodogo.buildscape.block.RoseVinesBlock) {
            SoundType soundType = state.getSoundType();
            if (soundType instanceof com.kingodogo.buildscape.block.CustomSoundType customSound) {
                level.playSound(
                        null,
                        pos,
                        soundType.getHitSound(),
                        SoundSource.BLOCKS,
                        customSound.getHitVolume(),
                        customSound.getHitPitch());
            }
        }
        return InteractionResult.PASS;
    }

    public static void onLivingDeath(LivingEntity entity, DamageSource source) {
        FrostRoseDropHandler.onLivingDeath(entity, source);
    }

    public static void onLivingUpdate(LivingEntity entity) {
        ChainMobHandler.onLivingUpdate(entity);
        Level level = Services.PLATFORM.getEntityLevel(entity);
        if (com.kingodogo.buildscape.fluid.ModFluids.isExperience(level.getFluidState(entity.blockPosition()).getType())
                && (entity.isSwimming() || entity.isInWater())) {
            entity.setDeltaMovement(boostExperienceSwimming(entity.getDeltaMovement()));
        }

        if (entity.isPassenger() || Services.PLATFORM.isEntityTypeInTag(entity,
                new com.kingodogo.buildscape.util.CommonId("minecraft", "not_affected_by_geysers"))
                || entity instanceof Player player && player.getAbilities().flying) return;
        BlockPos.MutableBlockPos testPos = entity.blockPosition().mutable();
        for (int i = 0; i < 24; i++) {
            BlockState state = level.getBlockState(testPos);
            com.kingodogo.buildscape.util.CommonId blockId = Services.PLATFORM.getBlockId(state.getBlock());
            if (blockId != null && blockId.getPath().equals("potent_sulfur")
                    && (blockId.getNamespace().equals("buildscape") || blockId.getNamespace().equals("minecraft"))) {
                net.minecraft.world.level.block.state.properties.Property<?> geyserState =
                        state.getBlock().getStateDefinition().getProperty("state");
                boolean erupting = geyserState != null && state.hasProperty(geyserState)
                        && state.getValue(geyserState) instanceof net.minecraft.util.StringRepresentable value
                        && (value.getSerializedName().equals("erupting") || value.getSerializedName().equals("continuous"));
                if (erupting) {
                    BlockPos.MutableBlockPos waterTop = testPos.above().mutable();
                    while (level.getFluidState(waterTop).isSourceOfType(net.minecraft.world.level.material.Fluids.WATER)) {
                        waterTop.move(net.minecraft.core.Direction.UP);
                    }
                    int waterBlocks = Math.max(1, waterTop.getY() - testPos.getY() - 1);
                    var velocity = entity.getDeltaMovement();
                    if (velocity.y < 0.3D + waterBlocks * 0.1D) {
                        entity.resetFallDistance();
                        entity.setDeltaMovement(velocity.add(0.0D, 0.2D, 0.0D));
                        Services.PLATFORM.markGeyserVelocityChanged(entity);
                    }
                }
                break;
            }
            if (!state.isAir() && !state.is(net.minecraft.world.level.block.Blocks.WATER)
                    && !state.getCollisionShape(level, testPos).isEmpty()) break;
            testPos.move(net.minecraft.core.Direction.DOWN);
        }
    }

    static net.minecraft.world.phys.Vec3 boostExperienceSwimming(net.minecraft.world.phys.Vec3 velocity) {
        return velocity.multiply(1.175D, 1.06D, 1.175D);
    }

    public static void onItemCrafted(Player player, ItemStack stack) {
        if (player instanceof ServerPlayer serverPlayer) {
            AdvancementMilestoneLogic.onItemCrafted(serverPlayer, stack);
        }
    }

    public static void onItemCrafted(Player player, ItemStack stack, net.minecraft.world.Container ingredients) {
        onItemCrafted(player, stack, ingredients, stack.getCount());
    }

    public static void onItemCrafted(Player player, ItemStack stack, net.minecraft.world.Container ingredients, int count) {
        if (player instanceof ServerPlayer serverPlayer) {
            AdvancementMilestoneLogic.onItemCrafted(serverPlayer, stack, count);
        }
        prepareCraftedItem(player, stack, ingredients);
    }

    public static void prepareCraftedItem(Player player, ItemStack stack, net.minecraft.world.Container ingredients) {
        if (Services.PLATFORM.getEntityLevel(player).isClientSide()
                || !stack.is(net.minecraft.world.item.Items.SUSPICIOUS_STEW)) return;
        net.minecraft.world.item.Item frostRose = Services.PLATFORM.getItem(
                new com.kingodogo.buildscape.util.CommonId("buildscape", "frost_rose"));
        if (frostRose == null) return;
        for (int slot = 0; slot < ingredients.getContainerSize(); slot++) {
            if (ingredients.getItem(slot).is(frostRose)) {
                Services.PLATFORM.updateCustomData(stack, data -> data.putInt("FrostRoseStew", 1));
                return;
            }
        }
    }

    public static InteractionResult onRightClickItem(Player player, Level level, InteractionHand hand) {
        return SafeEventHooks.call("onRightClickItem", () -> onRightClickItemUnchecked(player, level, hand), InteractionResult.PASS);
    }

    private static InteractionResult onRightClickItemUnchecked(Player player, Level level, InteractionHand hand) {
        if (player == null || level == null) return InteractionResult.PASS;
        ItemStack held = player.getItemInHand(hand);
        if (held.getItem() instanceof com.kingodogo.buildscape.item.ConfettiItem) {
            if (!level.isClientSide()) {
                var look = player.getViewVector(1.0F);
                var start = player.getEyePosition().add(look.scale(.9D));
                var data = Services.PLATFORM.getCustomData(held, false);
                int burst = data == null ? 1 : Math.max(1, Math.min(5, Services.PLATFORM.getTagInt(data, "BurstLevel", 1)));
                PacketFactory.sendToTracking(level, player.blockPosition(), new com.kingodogo.buildscape.network.ConfettiBurstPacket(
                        start.x, start.y, start.z, (float) look.x, (float) look.y, (float) look.z, burst, level.getRandom().nextLong()));
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FIREWORK_ROCKET_BLAST,
                        SoundSource.PLAYERS, .8F, 1.4F);
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FIREWORK_ROCKET_TWINKLE,
                        SoundSource.PLAYERS, .6F, 1.6F);
                if (player instanceof ServerPlayer serverPlayer) AdvancementEvents.onConfettiUsed(serverPlayer);
                if (!player.getAbilities().instabuild) held.shrink(1);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    public static void onItemUseFinish(LivingEntity entity, ItemStack stack) {
        SafeEventHooks.run("onItemUseFinish", () -> onItemUseFinishUnchecked(entity, stack));
    }

    private static void onItemUseFinishUnchecked(LivingEntity entity, ItemStack stack) {
        if (entity instanceof Player player && !Services.PLATFORM.getEntityLevel(player).isClientSide()
                && stack.is(net.minecraft.world.item.Items.SUSPICIOUS_STEW)) {
            CompoundTag data = Services.PLATFORM.getCustomData(stack, false);
            if (data != null && Services.PLATFORM.getTagInt(data, "FrostRoseStew", 0) == 1) {
                CompoundTag playerData = Services.PLATFORM.getEntityData(player);
                playerData.putInt("FrostRoseStewDamageTicks", 120);
            }
        }
    }

    private static final java.util.Set<java.util.UUID> JOINED_PLAYERS = java.util.Collections.newSetFromMap(new java.util.concurrent.ConcurrentHashMap<>());

    public static void onPlayerTick(Player player) {
        if (player == null) return;
        HollowLogCrawlHandler.tickPlayer(player);

        Level level = Services.PLATFORM.getEntityLevel(player);
        if (level.isClientSide()) return;

        CompoundTag playerData = Services.PLATFORM.getEntityData(player);
        int damageTicks = Services.PLATFORM.getTagInt(playerData, "FrostRoseStewDamageTicks", 0);
        if (damageTicks > 0) {
            if (damageTicks % 20 == 0 && player.isAlive()) {
                Services.PLATFORM.hurtFreezeDamage(player, 1.0F);
                player.setTicksFrozen(Math.min(player.getTicksFrozen() + 140, 300));
            }
            damageTicks--;
            if (damageTicks > 0) {
                playerData.putInt("FrostRoseStewDamageTicks", damageTicks);
            } else {
                playerData.remove("FrostRoseStewDamageTicks");
            }
        }
    }

    public static void onPlayerJoin(ServerPlayer player) {
        if (player == null) return;
        JOINED_PLAYERS.add(player.getUUID());
        AdvancementEvents.checkFullCubeAdvancement(player);
        MinecraftServer server = Services.PLATFORM.getServer(player);
        if (server != null) {
            PillarIdManager.setCurrentServer(server);
        }
        PillarIdManager manager = PillarIdManager.get();
        if (!manager.hasLoaded() && !manager.isLoadInProgress()) {
            if (server != null) {
                manager.load(server);
            } else {
                manager.load();
            }
        }

        PacketFactory.sendToPlayer(player, new SyncConfigPacket(PillarParticleConfig.getServerConfig(), player));

        if (server != null && server.isRunning()) {
            PacketFactory.sendToPlayer(player, new SyncGameRulesPacket(
                    ModGameRules.isFastLeafDecayEnabled(),
                    ModGameRules.isEndermanGriefingDisabled(),
                    ModGameRules.isCreeperGriefingDisabled(),
                    ModGameRules.isGhastGriefingDisabled(),
                    ModGameRules.isCakeStackingEnabled(),
                    ModGameRules.isWaterBottleStackingEnabled()
            ));

            SyncPillarIdsPacket.sendToPlayer(player, manager.getAllPillarDataForSync());

            CompoundTag tag = Services.PLATFORM.getEntityData(player);
            long cooldown = Services.PLATFORM.getTagLong(tag, "WanderingHomemakerCooldownRealTime", 0L);
            PacketFactory.sendToPlayer(player, new SyncHomemakerCooldownPacket(cooldown));
        }
    }

    public static boolean isMobGriefingDisabled(Entity entity, Level level) {
        if (entity == null || level == null) return false;
        if (Services.PLATFORM.isEnderman(entity)) {
            return ModGameRules.isEndermanGriefingDisabled();
        } else if (entity instanceof net.minecraft.world.entity.monster.Creeper) {
            return ModGameRules.isCreeperGriefingDisabled();
        } else if (entity instanceof net.minecraft.world.entity.monster.Ghast
                || Services.PLATFORM.isLargeFireball(entity)) {
            return ModGameRules.isGhastGriefingDisabled();
        }
        return false;
    }


    public static void onServerStarted(MinecraftServer server) {
        if (server == null) {
            return;
        }

        // Reset cached data from the previous world.
        PillarIdManager.resetWorldCache();
        PILLAR_MAINTENANCE.reset();
        com.kingodogo.buildscape.BuildscapeCommon.setServerFullyInitialized(false);

        // Preserve explicit server registration from dev/multiversion.
        PillarIdManager.setCurrentServer(server);

        // Load pillar data for the active world.
        PillarIdManager.get().load(server);
    }


    public static void onServerTick(MinecraftServer server) {
        FrostRoseDropHandler.onServerTick(server);
        MudToClayHandler.onServerTick(server);
        TreeChopJobManager.onServerTick(server);

        if (server == null || !server.isRunning()) return;
        PillarIdManager manager = PillarIdManager.get();
        if (!manager.hasLoaded()) {
            if (!manager.isLoadInProgress()) manager.load(server);
            return;
        }
        com.kingodogo.buildscape.BuildscapeCommon.setServerFullyInitialized(true);
        PILLAR_MAINTENANCE.tick(() -> manager.savePeriodic(false), () -> manager.savePeriodic(true),
                manager::flushRequestedSave, server.getPlayerList().getPlayerCount() > 0,
                PillarIdManager::checkAndRunScheduledRecovery, () -> {
                    if (manager.needsWorldRecovery()) manager.scheduleRecoveryFromWorld(server, false);
                });
    }

    public static void onServerStopping() {
        FrostRoseDropHandler.onServerStopping();
        MudToClayHandler.onServerStopping();
        TreeChopJobManager.onServerStopping();
        com.kingodogo.buildscape.pipe.transport.HollowPipeTransportManager.onServerStopping();
        com.kingodogo.buildscape.block.CascadeWaterManager.onServerStopping();
        HollowLogCrawlHandler.onServerStopping();
        JOINED_PLAYERS.clear();
        PILLAR_MAINTENANCE.reset();
        try {
            PillarIdManager manager = PillarIdManager.get();
            if (manager != null && manager.hasLoaded()) {
                manager.savePeriodic(true);
            }
        } catch (Exception exception) {
            com.kingodogo.buildscape.BuildscapeCommon.LOGGER.error("Failed to save pillar data during server shutdown", exception);
        }
    }

    public static void onServerStopped() {
        try {
            PillarIdManager manager = PillarIdManager.get();
            if (manager.hasLoaded()) {
                manager.savePeriodic(true);
            }
        } catch (Exception exception) {
            com.kingodogo.buildscape.BuildscapeCommon.LOGGER.error("Failed to save pillar data after server shutdown", exception);
        } finally {
            com.kingodogo.buildscape.BuildscapeCommon.setServerFullyInitialized(false);
            PILLAR_MAINTENANCE.reset();
            PillarIdManager.fullReset();
            PillarIdManager.setCurrentServer(null);
        }
    }

    public static void onPlayerLeave(ServerPlayer player) {
        if (player == null) return;
        JOINED_PLAYERS.remove(player.getUUID());
        TreeChopJobManager.onPlayerLoggedOut(player);
        HollowLogCrawlHandler.onPlayerLoggedOut(player.getUUID());
    }

    public static void onLevelUnload(net.minecraft.server.level.ServerLevel level) {
        try {
            PillarIdManager manager = PillarIdManager.get();
            if (manager.hasLoaded()) manager.savePeriodic(true);
        } catch (Exception exception) {
            com.kingodogo.buildscape.BuildscapeCommon.LOGGER.error("Failed to save pillar data during world unload", exception);
        }
        com.kingodogo.buildscape.pipe.transport.HollowPipeTransportManager.onLevelUnload(level);
        com.kingodogo.buildscape.block.CascadeWaterManager.onLevelUnload(level);
        TreeChopJobManager.onLevelUnload(level);
        FrostRoseDropHandler.onLevelUnload(level);
    }

    public static void onPlayerChangedDimension(ServerPlayer player) {
        long cooldown = Services.PLATFORM.getTagLong(Services.PLATFORM.getEntityData(player),
                "WanderingHomemakerCooldownRealTime", 0L);
        PacketFactory.sendToPlayer(player, new SyncHomemakerCooldownPacket(cooldown));
    }

    public static InteractionResult onEntityInteract(Player player, Level level, InteractionHand hand, Entity target) {
        return SafeEventHooks.call("onEntityInteract", () -> onEntityInteractUnchecked(player, level, hand, target), InteractionResult.PASS);
    }

    private static InteractionResult onEntityInteractUnchecked(Player player, Level level, InteractionHand hand, Entity target) {
        if (player instanceof ServerPlayer serverPlayer) {
            if (target instanceof com.kingodogo.buildscape.entity.FestiveWanderingHomemakerEntity) {
                AdvancementEvents.grant(serverPlayer, "its_beginning_to_look_a_lot_like_christmas");
            } else if (target instanceof com.kingodogo.buildscape.entity.WanderingHomemakerEntity) {
                AdvancementEvents.grant(serverPlayer, "the_homemaker_cometh");
            }
        }
        InteractionResult frameResult = ItemFrameParticleHandler.onEntityInteract(player, level, hand, target);
        if (frameResult != InteractionResult.PASS) return frameResult;
        return InteractionResult.PASS;
    }
}
