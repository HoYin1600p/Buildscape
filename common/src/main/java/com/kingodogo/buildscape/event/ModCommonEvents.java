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

    private static int pillarSaveTickCounter = 0;
    private static int pillarBackupTickCounter = 0;
    private static final int PILLAR_SAVE_INTERVAL = 600;
    private static final int PILLAR_BACKUP_INTERVAL = 6000;

    private ModCommonEvents() {}

    public static void onBlockPlaced(Level level, BlockPos pos, BlockState state, @Nullable Player player) {
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
        if (player == null || level == null || pos == null) return InteractionResult.PASS;
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
        InteractionResult copperResult = com.kingodogo.buildscape.block.CopperOxidationHandler.handleRightClick(level, pos, state, held, player, hand);
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
                player.swing(hand);
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
                            player.drop(bottle, false);
                        }
                    }
                }
            }
            player.swing(hand);
            return Services.PLATFORM.sidedSuccess(level.isClientSide());
        }
        return LogStrippingLogic.handleAxeStrip(player, level, hand, pos);
    }

    public static void onLeftClickBlock(Player player, Level level, BlockPos pos) {
        if (player == null || level == null || pos == null) return;
        ItemStack held = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (held.getItem() instanceof com.kingodogo.buildscape.item.BiomeBrushItem brush) {
            if (!level.isClientSide()) {
                if (held.getDamageValue() >= held.getMaxDamage()) {
                    level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 1.0f, 0.8f);
                    return;
                }
                if (player.isShiftKeyDown()) {
                    brush.clearCapturedBiome(held, player);
                } else {
                    brush.setPos2(held, pos, player);
                }
            }
            return;
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
    }

    public static void onLivingDeath(LivingEntity entity, DamageSource source) {
        FrostRoseDropHandler.onLivingDeath(entity, source);
        Level level = Services.PLATFORM.getEntityLevel(entity);
        if (!level.isClientSide()) {
            BlockPos deathPos = entity.blockPosition();
            for (BlockPos pos : BlockPos.betweenClosed(deathPos.offset(-8, -8, -8), deathPos.offset(8, 8, 8))) {
                var id = Services.PLATFORM.getBlockId(level.getBlockState(pos).getBlock());
                if (id != null && "buildscape".equals(id.getNamespace()) && "sculk_catalyst".equals(id.getPath())) {
                    com.kingodogo.buildscape.block.SculkCatalystHandler.onMobKilledNearCatalyst(level, pos, deathPos, entity);
                    break;
                }
            }
        }
    }

    public static void onLivingUpdate(LivingEntity entity) {
        ChainMobHandler.onLivingUpdate(entity);
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
        if (player == null || level == null) return InteractionResult.PASS;
        ItemStack held = player.getItemInHand(hand);
        if (held.getItem() instanceof com.kingodogo.buildscape.item.ConfettiItem) {
            if (!level.isClientSide()) {
                var look = player.getViewVector(1.0F);
                var start = player.getEyePosition().add(look.scale(.9D));
                var data = Services.PLATFORM.getCustomData(held, false);
                int burst = data == null ? 1 : Math.clamp(Services.PLATFORM.getTagInt(data, "BurstLevel", 1), 1, 5);
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
        if (held.is(net.minecraft.world.item.Items.GLASS_BOTTLE)) {
            net.minecraft.world.phys.Vec3 eye = player.getEyePosition();
            net.minecraft.world.phys.Vec3 view = player.getViewVector(1.0F);
            net.minecraft.world.phys.HitResult hit = level.clip(new net.minecraft.world.level.ClipContext(
                    eye, eye.add(view.scale(5.0D)), net.minecraft.world.level.ClipContext.Block.OUTLINE,
                    net.minecraft.world.level.ClipContext.Fluid.SOURCE_ONLY, player));
            if (hit.getType() != net.minecraft.world.phys.HitResult.Type.MISS) return InteractionResult.PASS;
            net.minecraft.world.item.Item mistItem = Services.PLATFORM.getItem(new com.kingodogo.buildscape.util.CommonId("buildscape", "bottle_of_mist"));
            if (mistItem == null || mistItem == net.minecraft.world.item.Items.AIR) return InteractionResult.PASS;
            if (!level.isClientSide()) {
                if (!player.getAbilities().instabuild) {
                    held.shrink(1);
                }
                if (mistItem != null && mistItem != net.minecraft.world.item.Items.AIR) {
                    ItemStack mistBottle = new ItemStack(mistItem);
                    if (!player.getInventory().add(mistBottle.copy())) {
                        player.drop(mistBottle, false);
                    }
                    level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BOTTLE_FILL, SoundSource.PLAYERS, 1.0F, 1.0F);
                }
            }
            player.swing(hand);
            return Services.PLATFORM.sidedSuccess(level.isClientSide());
        }
        return InteractionResult.PASS;
    }

    public static void onItemUseFinish(LivingEntity entity, ItemStack stack) {
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
        PillarIdManager manager = PillarIdManager.get();
        if (!manager.hasLoaded() && !manager.isLoadInProgress()) {
            manager.load();
        }

        PacketFactory.sendToPlayer(player, new SyncConfigPacket(PillarParticleConfig.get()));

        MinecraftServer server = Services.PLATFORM.getServer(player);
        if (server != null && server.isRunning()) {
            PacketFactory.sendToPlayer(player, new SyncGameRulesPacket(
                    ModGameRules.isFastLeafDecayEnabled(),
                    ModGameRules.clientDisableEndermanGriefing,
                    ModGameRules.clientDisableCreeperGriefing,
                    ModGameRules.clientDisableGhastGriefing,
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
        if (entity instanceof net.minecraft.world.entity.monster.EnderMan) {
            return ModGameRules.clientDisableEndermanGriefing;
        } else if (entity instanceof net.minecraft.world.entity.monster.Creeper) {
            return ModGameRules.clientDisableCreeperGriefing;
        } else if (entity instanceof net.minecraft.world.entity.monster.Ghast
                || entity.getClass().getSimpleName().toLowerCase().contains("fireball")) {
            return ModGameRules.clientDisableGhastGriefing;
        }
        return false;
    }

    public static void onServerStarted(MinecraftServer server) {
        PillarIdManager.get().load();
    }

    public static void onServerTick(MinecraftServer server) {
        FrostRoseDropHandler.onServerTick(server);
        MudToClayHandler.onServerTick(server);
        TreeChopJobManager.onServerTick(server);

        if (server != null) {
            for (net.minecraft.server.level.ServerLevel level : server.getAllLevels()) {
                com.kingodogo.buildscape.block.EyeblossomTransitionHandler.onWorldTick(level);
            }
            pillarSaveTickCounter++;
            if (pillarSaveTickCounter >= PILLAR_SAVE_INTERVAL) {
                pillarSaveTickCounter = 0;
                PillarIdManager.get().saveImmediate(server);
            }
        }
    }

    public static void onServerStopping() {
        FrostRoseDropHandler.onServerStopping();
        MudToClayHandler.onServerStopping();
        JOINED_PLAYERS.clear();
        try {
            PillarIdManager.get().saveImmediate();
        } catch (Exception exception) {
            com.kingodogo.buildscape.BuildscapeCommon.LOGGER.error("Failed to save pillar data during server shutdown", exception);
        }
    }

    public static void onPlayerLeave(ServerPlayer player) {
        JOINED_PLAYERS.remove(player.getUUID());
    }

    public static void onPlayerChangedDimension(ServerPlayer player) {
        long cooldown = Services.PLATFORM.getTagLong(Services.PLATFORM.getEntityData(player),
                "WanderingHomemakerCooldownRealTime", 0L);
        PacketFactory.sendToPlayer(player, new SyncHomemakerCooldownPacket(cooldown));
    }

    public static InteractionResult onEntityInteract(Player player, Level level, InteractionHand hand, Entity target) {
        if (player instanceof ServerPlayer serverPlayer) {
            if (target instanceof com.kingodogo.buildscape.entity.FestiveWanderingHomemakerEntity) {
                AdvancementEvents.grant(serverPlayer, "its_beginning_to_look_a_lot_like_christmas");
            } else if (target instanceof com.kingodogo.buildscape.entity.WanderingHomemakerEntity) {
                AdvancementEvents.grant(serverPlayer, "the_homemaker_cometh");
            }
        }
        InteractionResult frameResult = ItemFrameParticleHandler.onEntityInteract(player, level, hand, target);
        if (frameResult != InteractionResult.PASS) return frameResult;

        if (target instanceof net.minecraft.world.entity.AgeableMob mob
                && !(target instanceof net.minecraft.world.entity.monster.Monster)) {
            ItemStack held = player.getItemInHand(hand);
            net.minecraft.world.item.Item flower = Services.PLATFORM.getItem(
                    new com.kingodogo.buildscape.util.CommonId("buildscape", "golden_dandelion"));
            if (flower != null && held.is(flower)) {
                boolean frozen = com.kingodogo.buildscape.util.GoldenDandelionGrowth.isFrozen(mob);
                if (!com.kingodogo.buildscape.util.GoldenDandelionGrowth.canToggle(mob.isBaby(), frozen)) {
                    return InteractionResult.PASS;
                }
                if (!level.isClientSide()) {
                    if (!com.kingodogo.buildscape.util.GoldenDandelionGrowth.setFrozen(mob, !frozen)) {
                        return InteractionResult.PASS;
                    }
                    if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                        serverLevel.sendParticles(frozen ? net.minecraft.core.particles.ParticleTypes.HAPPY_VILLAGER
                                        : net.minecraft.core.particles.ParticleTypes.WAX_OFF,
                                mob.getX(), mob.getY() + mob.getBbHeight() * 0.5D, mob.getZ(),
                                12, 0.3D, 0.3D, 0.3D, 0.05D);
                    }
                    level.playSound(null, mob.blockPosition(), frozen ? SoundEvents.VILLAGER_YES
                                    : SoundEvents.HONEYCOMB_WAX_ON, SoundSource.NEUTRAL, 1.0F, 1.0F);
                    if (!player.getAbilities().instabuild) held.shrink(1);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }
}
