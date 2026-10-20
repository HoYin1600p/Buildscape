package com.kingodogo.buildscape.feature.geyser;

import com.kingodogo.buildscape.core.NbtKeys;
import com.kingodogo.buildscape.core.SyncedBlockEntity;
import com.kingodogo.buildscape.registry.ModBlockEntities;
import com.kingodogo.buildscape.registry.ModParticles;
import com.kingodogo.buildscape.registry.ModSounds;
import com.kingodogo.buildscape.registry.blocks.ShapedStoneBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

public class PotentSulfurBlockEntity extends SyncedBlockEntity {
    private static final Predicate<Entity> EFFECT_PREDICATE = EntitySelector.NO_SPECTATORS
            .and(EntitySelector.ENTITY_STILL_ALIVE);

    public int waitingCountdown = -1;
    public long eruptionTick = -1L;

    public static final TagKey<EntityType<?>> NOT_AFFECTED_BY_GEYSERS = TagKey.create(Registry.ENTITY_TYPE_REGISTRY,
            new ResourceLocation("minecraft", "not_affected_by_geysers"));

    public static final BlockEntityTicker<PotentSulfurBlockEntity> SERVER_NAUSEA_EFFECT_TICKER = (level, pos, state,
            potentSulfur) -> {
        if (level.getGameTime() % 10L == 0L) {
            BlockPos sourceBlock = findNoxiousGasSourceBlock(level, pos);
            if (sourceBlock != null) {
                for (LivingEntity entity : getNearbyLivingEntities(level, sourceBlock)) {
                    if (canBeReachedByNoxiousGas(level, sourceBlock, entity.getEyePosition())) {
                        applyNauseaEffect(entity);
                    }
                }
            }
        }
    };

    public static final BlockEntityTicker<PotentSulfurBlockEntity> CLIENT_NOXIOUS_GAS_TICKER = (level, pos, state,
            entity) -> {
        if (level.getGameTime() % 20L == 0L) {
            BlockPos sourceBlock = findNoxiousGasSourceBlock(level, pos);
            if (sourceBlock != null) {
                spawnNoxiousGasCloudParticle(level, Vec3.atCenterOf(sourceBlock));
            }
        }
    };

    public static final BlockEntityTicker<PotentSulfurBlockEntity> CLIENT_GEYSER_PLUME_TICKER_ERUPTION = (level, pos,
            state, entity) -> {
        tickClientPlume(level, pos, state, entity, ModSounds.GEYSER_ERUPTION_ACTIVE.get());
    };

    public static final BlockEntityTicker<PotentSulfurBlockEntity> CLIENT_GEYSER_PLUME_TICKER_CONTINUOUS = (level, pos,
            state, entity) -> {
        tickClientPlume(level, pos, state, entity, ModSounds.GEYSER_CONTINUOUS_ACTIVE.get());
    };

    private static void tickClientPlume(Level level, BlockPos pos, BlockState state, PotentSulfurBlockEntity entity,
            SoundEvent sound) {
        BlockPos sourceBlock = findNoxiousGasSourceBlock(level, pos);
        if (sourceBlock != null) {
            long eruptionTime = level.getGameTime() - entity.eruptionTick;
            if (eruptionTime % 20L == 0L) {
                spawnGeyserParticle(level, pos, sourceBlock);
            }
            if (eruptionTime % 40L == 0L) {
                level.playLocalSound((double) sourceBlock.getX() + 0.5, (double) sourceBlock.getY() + 0.5,
                        (double) sourceBlock.getZ() + 0.5, sound, SoundSource.BLOCKS, 1.0F, 1.0F, false);
            }
        }
    }

    public static void tickClientPlumeDistant(Level level, BlockPos pos, BlockState state,
            PotentSulfurBlockEntity entity) {
        if (entity.eruptionTick == -1L) {
            entity.eruptionTick = level.getGameTime();
        }
        BlockPos sourceBlock = findNoxiousGasSourceBlock(level, pos);
        if (sourceBlock != null) {
            long eruptionTime = level.getGameTime() - entity.eruptionTick;
            if (eruptionTime % 20L == 0L) {
                spawnGeyserParticle(level, pos, sourceBlock);
            }
        }
    }

    public static final BlockEntityTicker<PotentSulfurBlockEntity> SERVER_WAITING_COUNTDOWN_TICKER = (level, pos, state,
            entity) -> {
        if (level.getGameTime() % 20L == 0L) {
            BlockPos sourceBlock = findNoxiousGasSourceBlock(level, pos);
            if (sourceBlock != null) {
                if (entity.waitingCountdown <= 0) {
                    int waterBlocks = sourceBlock.getY() - pos.getY() - 1;
                    Random geyserPositional = geyserPositional(level, pos);
                    if (state.getValue(PotentSulfurBlock.STATE) == PotentSulfurState.DORMANT) {
                        entity.waitingCountdown = 10 * (waterBlocks - 1) + geyserPositional.nextInt(16) + 15;
                    } else {
                        geyserPositional.nextInt();
                        entity.waitingCountdown = waterBlocks - 1 + geyserPositional.nextInt(2) + 1;
                    }
                    entity.setChanged();
                }

                if (entity.waitingCountdown > 0) {
                    --entity.waitingCountdown;
                }

                if (entity.waitingCountdown == 0) {
                    PotentSulfurState stateToSet = state.getValue(PotentSulfurBlock.STATE) == PotentSulfurState.DORMANT
                            ? PotentSulfurState.ERUPTING
                            : PotentSulfurState.DORMANT;
                    level.setBlockAndUpdate(pos, state.setValue(PotentSulfurBlock.STATE, stateToSet));
                    entity.setChanged();
                }
            }
        }
    };

    public static final BlockEntityTicker<PotentSulfurBlockEntity> LAUNCH_ENTITY_TICKER = (level, pos, state,
            entity) -> {
        int topY = getLiftTop(level, pos);
        tickLivingLift(level, pos, topY);
        BlockPos sourceBlock = findNoxiousGasSourceBlock(level, pos);
        if (sourceBlock != null) {
            int rawWaterBlocks = sourceBlock.getY() - pos.getY() - 1;
            int waterBlocks = Math.max(1, rawWaterBlocks);
            int geyserForceHeight = getUnobstructedBlockCount(level, pos.above(), waterBlocks);

            double plumeTipY = (double) sourceBlock.getY() + (double) geyserForceHeight - 1.0;
            double particleSizeOffset = 1.18 + (double) (waterBlocks - 1) * 0.05;
            AABB aabb = (new AABB(pos.above())).expandTowards(0.0, (double) (geyserForceHeight + 3), 0.0)
                    .inflate(0.45, 0.0, 0.45);

            for (Entity entityToBeLaunched : level.getEntitiesOfClass(Entity.class, aabb, EFFECT_PREDICATE)) {
                if (entityToBeLaunched.blockPosition().getY() > topY) {
                    continue;
                }
                Vec3 entityVelocity = entityToBeLaunched.getDeltaMovement();

                if (!entityToBeLaunched.isPassenger() && !entityToBeLaunched.getType().is(NOT_AFFECTED_BY_GEYSERS)) {
                    if (entityToBeLaunched instanceof Player player) {
                        if (player.getAbilities().flying) {
                            continue;
                        }
                    }

                    entityToBeLaunched.resetFallDistance();
                    boolean isPlayer = entityToBeLaunched instanceof Player;

                    double entityY = entityToBeLaunched.getY();
                    double bobbingTime = (double) (level.getGameTime() + (long) entityToBeLaunched.getId() * 7L) * 0.4;
                    double bobbingOffset = Math.sin(bobbingTime) * 0.10;
                    double bobbingVel = Math.cos(bobbingTime) * 0.07;
                    double targetY = plumeTipY + particleSizeOffset + bobbingOffset;

                    Vec3 newVelocity;
                    if (entityY < targetY - 1.0) {
                        double targetSpeed = Math.min(0.65, 0.4 + (double) waterBlocks * 0.06);
                        double newY = Math.min(targetSpeed, Math.max(entityVelocity.y + 0.18, 0.4));
                        double horizDamping = isPlayer ? 1.0 : 0.9;
                        newVelocity = new Vec3(entityVelocity.x * horizDamping, newY, entityVelocity.z * horizDamping);
                    } else if (entityY <= targetY + 1.8) {
                        double hoverY = (targetY - entityY) * 0.45 + bobbingVel;
                        double finalY = (isPlayer && entityVelocity.y > 0.2)
                                ? Math.max(hoverY, entityVelocity.y)
                                : hoverY;
                        double horizDamping = isPlayer ? 1.0 : 0.85;
                        newVelocity = new Vec3(entityVelocity.x * horizDamping, finalY,
                                entityVelocity.z * horizDamping);
                    } else {
                        continue;
                    }

                    entityToBeLaunched.setDeltaMovement(newVelocity);
                    entityToBeLaunched.hasImpulse = true;

                    if (entityToBeLaunched instanceof ServerPlayer serverPlayer) {
                        serverPlayer.hurtMarked = true;
                        if (entityY < targetY - 1.0 && entityVelocity.y < 0.2) {
                            serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(serverPlayer));
                        }
                    }
                }
            }
        }
    };

    private static int getLiftTop(final Level level, final BlockPos pos) {
        BlockPos.MutableBlockPos testPos = pos.mutable();
        return GeyserLiftRules.unobstructedTop(pos.getY(), y -> {
            testPos.setY(y);
            BlockState testState = level.getBlockState(testPos);
            return !testState.is(ShapedStoneBlocks.POTENT_SULFUR.get())
                    && isGeyserPassableBlock(testState, level, testPos, CollisionContext.empty());
        });
    }

    private static void tickLivingLift(final Level level, final BlockPos pos, final int topY) {
        AABB column = new AABB(pos).expandTowards(0.0, 23.0, 0.0);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, column, living -> {
            BlockPos livingPos = living.blockPosition();
            return livingPos.getX() == pos.getX() && livingPos.getZ() == pos.getZ()
                    && livingPos.getY() >= pos.getY() && livingPos.getY() < pos.getY() + 24
                    && !living.isPassenger() && !living.getType().is(NOT_AFFECTED_BY_GEYSERS)
                    && !(living instanceof Player player && player.getAbilities().flying);
        });
        if (entities.isEmpty()) {
            return;
        }

        BlockPos.MutableBlockPos sourceSearch = pos.above().mutable();
        while (level.getFluidState(sourceSearch).isSourceOfType(Fluids.WATER)) {
            sourceSearch.move(Direction.UP);
        }
        int waterBlocks = Math.max(1, sourceSearch.getY() - pos.getY() - 1);

        for (LivingEntity living : entities) {
            if (living.blockPosition().getY() > topY) {
                continue;
            }
            Vec3 entityVelocity = living.getDeltaMovement();
            if (entityVelocity.y < 0.3 + (double) waterBlocks * 0.1) {
                living.resetFallDistance();
                living.setDeltaMovement(entityVelocity.add(0.0, 0.2, 0.0));
                living.hasImpulse = true;
                if (!level.isClientSide()) {
                    living.hurtMarked = true;
                }
            }
        }
    }

    public PotentSulfurBlockEntity(final BlockPos worldPosition, final BlockState blockState) {
        super(ModBlockEntities.POTENT_SULFUR.get(), worldPosition, blockState);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.waitingCountdown = tag.getInt("countdown");
        if (tag.contains(NbtKeys.ERUPTION_TICK)) {
            this.eruptionTick = tag.getLong(NbtKeys.ERUPTION_TICK);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("countdown", this.waitingCountdown);
        tag.putLong(NbtKeys.ERUPTION_TICK, this.eruptionTick);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        load(tag);
    }

    @Override
    public void setLevel(final Level level) {
        super.setLevel(level);
        if (this.eruptionTick == -1L) {
            this.eruptionTick = level.getGameTime();
        }
    }

    public void resetCountdown() {
        this.waitingCountdown = -1;
    }

    private static void applyNauseaEffect(final LivingEntity entity) {
        entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 80, 0, true, true));
    }

    private static List<LivingEntity> getNearbyLivingEntities(final Level level, final BlockPos pos) {
        AABB aabb = (new AABB(pos)).inflate(2.5, 0.0, 2.5);
        return level.getEntitiesOfClass(LivingEntity.class, aabb, EFFECT_PREDICATE);
    }

    public static Random geyserPositional(final Level level, final BlockPos pos) {
        long seed = (level instanceof ServerLevel ? ((ServerLevel) level).getSeed() : 0L) ^ -904011478L ^ pos.asLong();
        return new Random(seed);
    }

    private static void spawnGeyserParticle(final Level level, final BlockPos sulfurPos, final BlockPos sourcePos) {
        int waterBlocks = sourcePos.getY() - sulfurPos.getY() - 1;
        level.addAlwaysVisibleParticle(new GeyserParticleOptions(ModParticles.GEYSER.get(), waterBlocks), true,
                (double) sourcePos.getX() + 0.5, (double) sourcePos.getY(), (double) sourcePos.getZ() + 0.5, 0.0,
                0.0, 0.0);
    }

    private static void spawnNoxiousGasCloudParticle(final Level level, final Vec3 pos) {
        level.addParticle(ModParticles.NOXIOUS_GAS.get(), pos.x, pos.y, pos.z, 0.0, 0.0, 0.0);
    }

    private static int getUnobstructedBlockCount(final Level level, final BlockPos pos, final int waterBlocks) {
        int geyserForceHeight = 5 * waterBlocks;
        CollisionContext geyserPositionContext = CollisionContext.empty();

        for (int i = 0; i < geyserForceHeight; ++i) {
            BlockPos currentPos = pos.above(i);
            BlockState state = level.getBlockState(currentPos);
            if (!isGeyserPassableBlock(state, level, currentPos, geyserPositionContext)) {
                return i;
            }
        }

        return geyserForceHeight;
    }

    private static boolean isGeyserPassableBlock(final BlockState state, final Level level, final BlockPos pos,
            final CollisionContext context) {
        return !state.isAir() && !state.is(Blocks.WATER)
                ? state.getCollisionShape(level, pos, context).isEmpty()
                : true;
    }

    private static @Nullable BlockPos findNoxiousGasSourceBlock(final Level level, final BlockPos origin) {
        int maxY = origin.getY() + 4 + 1;
        CollisionContext geyserPositionContext = CollisionContext.empty();
        BlockPos.MutableBlockPos pos = origin.above(1).mutable();

        while (true) {
            if (pos.getY() <= maxY) {
                BlockState state = level.getBlockState(pos);
                boolean isWaterLogged = level.getFluidState(pos).isSourceOfType(Fluids.WATER);
                if (isWaterLogged && (state.is(Blocks.WATER)
                        || isGeyserPassableBlock(state, level, pos, geyserPositionContext))) {
                    pos.move(Direction.UP);
                    continue;
                }

                if (state.isAir() || isGeyserPassableBlock(state, level, pos, geyserPositionContext)) {
                    return pos.immutable();
                }
            }
            return null;
        }
    }

    public static boolean canBeReachedByNoxiousGas(final Level level, final BlockPos sourceBlock, final Vec3 pos) {
        BlockPos blockPos = new BlockPos(pos);
        CollisionContext geyserPositionContext = CollisionContext.empty();
        if (!isGeyserPassableBlock(level.getBlockState(blockPos), level, blockPos, geyserPositionContext)) {
            return false;
        } else if (pos.distanceToSqr(Vec3.atCenterOf(sourceBlock)) > 9.0) {
            return false;
        } else {
            Vec3 belowSource = Vec3.atCenterOf(sourceBlock.below());
            Vec3 belowPos = pos.with(Axis.Y, pos.y - 1.0);
            return isWater(level, belowPos) && haveLineOfSight(level, belowSource, belowPos);
        }
    }

    private static boolean isWater(final Level level, final Vec3 pos) {
        return level.getFluidState(new BlockPos(pos)).isSourceOfType(Fluids.WATER);
    }

    private static boolean haveLineOfSight(final Level level, final Vec3 a, final Vec3 b) {
        HitResult hitResult = level.clip(new ClipContext(a, b, Block.COLLIDER, Fluid.NONE, null));
        return hitResult.getType() != Type.BLOCK;
    }
}
