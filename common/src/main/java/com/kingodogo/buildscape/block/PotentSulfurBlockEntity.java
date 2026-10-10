package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.particle.ModParticles;
import com.kingodogo.buildscape.particle.GeyserParticleOptions;
import com.kingodogo.buildscape.sound.ModSounds;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import com.kingodogo.buildscape.block.entity.IBlockEntityReadData;
import com.kingodogo.buildscape.block.entity.IBlockEntityWriteData;
import com.kingodogo.buildscape.block.entity.IDataSerializable;
import com.kingodogo.buildscape.block.entity.DataBlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;

import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
public class PotentSulfurBlockEntity extends DataBlockEntity implements IDataSerializable {

    private static final Predicate<Entity> EFFECT_PREDICATE = EntitySelector.NO_SPECTATORS.and(EntitySelector.ENTITY_STILL_ALIVE);
    private static final CommonId NOT_AFFECTED_BY_GEYSERS = CommonId.of("minecraft", "not_affected_by_geysers");

    public int waitingCountdown = -1;
    public long eruptionTick = -1L;

    public static final BlockEntityTicker<PotentSulfurBlockEntity> SERVER_NAUSEA_EFFECT_TICKER = (level, pos, state, potentSulfur) -> {
        if (level.getGameTime() % 10L == 0L) {
            BlockPos sourceBlock = findNoxiousGasSourceBlock(level, pos);
            if (sourceBlock != null) {
                for (LivingEntity entity : getNearbyLivingEntities(level, sourceBlock)) {
                    if (canBeReachedByNoxiousGas(level, sourceBlock, entity.getEyePosition())) {
                        com.kingodogo.buildscape.platform.Services.PLATFORM.applyNauseaEffect(entity, 80, 0);
                    }
                }
            }
        }
    };

    public static final BlockEntityTicker<PotentSulfurBlockEntity> CLIENT_NOXIOUS_GAS_TICKER = (level, pos, state, entity) -> {
        if (level.getGameTime() % 20L == 0L) {
            BlockPos sourceBlock = findNoxiousGasSourceBlock(level, pos);
            if (sourceBlock != null) {
                spawnNoxiousGasCloudParticle(level, Vec3.atCenterOf(sourceBlock));
            }
        }
    };

    public static final BlockEntityTicker<PotentSulfurBlockEntity> CLIENT_GEYSER_PLUME_TICKER_ERUPTION = (level, pos, state, entity) -> {
        tickClientPlume(level, pos, state, entity, ModSounds.GEYSER_ERUPTION_ACTIVE.get());
    };

    public static final BlockEntityTicker<PotentSulfurBlockEntity> CLIENT_GEYSER_PLUME_TICKER_CONTINUOUS = (level, pos, state, entity) -> {
        tickClientPlume(level, pos, state, entity, ModSounds.GEYSER_CONTINUOUS_ACTIVE.get());
    };

    private static void tickClientPlume(Level level, BlockPos pos, BlockState state, PotentSulfurBlockEntity entity, SoundEvent sound) {
        BlockPos sourceBlock = findNoxiousGasSourceBlock(level, pos);
        if (sourceBlock != null) {
            long eruptionTime = level.getGameTime() - entity.eruptionTick;
            if (eruptionTime % 20L == 0L) {
                spawnGeyserParticle(level, pos, sourceBlock);
            }
            if (eruptionTime % 40L == 0L) {
                level.playLocalSound((double) sourceBlock.getX() + 0.5D, (double) sourceBlock.getY() + 0.5D, (double) sourceBlock.getZ() + 0.5D, sound, SoundSource.BLOCKS, 1.0F, 1.0F, false);
            }
        }
    }

    public static void tickClientPlumeDistant(Level level, BlockPos pos, BlockState state, PotentSulfurBlockEntity entity) {
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

    public static final BlockEntityTicker<PotentSulfurBlockEntity> SERVER_WAITING_COUNTDOWN_TICKER = (level, pos, state, entity) -> {
        if (level.getGameTime() % 20L == 0L) {
            BlockPos sourceBlock = findNoxiousGasSourceBlock(level, pos);
            if (sourceBlock != null) {
                if (entity.waitingCountdown <= 0) {
                    int waterBlocks = sourceBlock.getY() - pos.getY() - 1;
                    Random rand = geyserPositional(level, pos);
                    if (state.getValue(PotentSulfurBlock.STATE) == PotentSulfurState.DORMANT) {
                        entity.waitingCountdown = 10 * (waterBlocks - 1) + rand.nextInt(16) + 15;
                    } else {
                        rand.nextInt();
                        entity.waitingCountdown = waterBlocks - 1 + rand.nextInt(2) + 1;
                    }
                }

                if (entity.waitingCountdown > 0) {
                    --entity.waitingCountdown;
                }
                entity.setChanged();

                if (entity.waitingCountdown == 0) {
                    PotentSulfurState stateToSet = state.getValue(PotentSulfurBlock.STATE) == PotentSulfurState.DORMANT ? PotentSulfurState.ERUPTING : PotentSulfurState.DORMANT;
                    level.setBlockAndUpdate(pos, state.setValue(PotentSulfurBlock.STATE, stateToSet));
                }
            }
        }
    };

    public static final BlockEntityTicker<PotentSulfurBlockEntity> LAUNCH_ENTITY_TICKER = (level, pos, state, entity) -> {
        BlockPos sourceBlock = findNoxiousGasSourceBlock(level, pos);
        if (sourceBlock != null) {
            int rawWaterBlocks = sourceBlock.getY() - pos.getY() - 1;
            int waterBlocks = Math.max(1, rawWaterBlocks);
            int geyserForceHeight = getUnobstructedBlockCount(level, pos.above(), waterBlocks);

            double plumeTipY = (double) sourceBlock.getY() + (double) geyserForceHeight - 1.0D;
            double particleSizeOffset = 1.18D + (double) (waterBlocks - 1) * 0.05D;
            AABB aabb = (new AABB(pos.above())).expandTowards(0.0D, (double) (geyserForceHeight + 3), 0.0D).inflate(0.45D, 0.0D, 0.45D);

            for (Entity entityToBeLaunched : level.getEntitiesOfClass(Entity.class, aabb, EFFECT_PREDICATE)) {
                Vec3 entityVelocity = entityToBeLaunched.getDeltaMovement();
                entityToBeLaunched.resetFallDistance();

                if (!entityToBeLaunched.isPassenger()
                        && !com.kingodogo.buildscape.platform.Services.PLATFORM.isEntityTypeInTag(entityToBeLaunched, NOT_AFFECTED_BY_GEYSERS)) {
                    if (entityToBeLaunched instanceof Player player && player.getAbilities().flying) {
                        continue;
                    }

                    boolean isPlayer = entityToBeLaunched instanceof Player;
                    double entityY = entityToBeLaunched.getY();
                    double bobbingTime = (double) (level.getGameTime() + (long) entityToBeLaunched.getId() * 7L) * 0.4D;
                    double bobbingOffset = Math.sin(bobbingTime) * 0.10D;
                    double bobbingVel = Math.cos(bobbingTime) * 0.07D;
                    double targetY = plumeTipY + particleSizeOffset + bobbingOffset;

                    Vec3 newVelocity;
                    if (entityY < targetY - 1.0D) {
                        double targetSpeed = Math.min(0.65D, 0.4D + (double) waterBlocks * 0.06D);
                        double newY = Math.min(targetSpeed, Math.max(entityVelocity.y + 0.18D, 0.4D));
                        double horizDamping = isPlayer ? 1.0D : 0.9D;
                        newVelocity = new Vec3(entityVelocity.x * horizDamping, newY, entityVelocity.z * horizDamping);
                    } else if (entityY <= targetY + 1.8D) {
                        double hoverY = (targetY - entityY) * 0.45D + bobbingVel;
                        double finalY = (isPlayer && entityVelocity.y > 0.2D) ? Math.max(hoverY, entityVelocity.y) : hoverY;
                        double horizDamping = isPlayer ? 1.0D : 0.85D;
                        newVelocity = new Vec3(entityVelocity.x * horizDamping, finalY, entityVelocity.z * horizDamping);
                    } else {
                        continue;
                    }

                    entityToBeLaunched.setDeltaMovement(newVelocity);
                    com.kingodogo.buildscape.platform.Services.PLATFORM.markEntityVelocityChanged(entityToBeLaunched);

                    if (entityToBeLaunched instanceof ServerPlayer serverPlayer) {
                        serverPlayer.hurtMarked = true;
                        if (entityY < targetY - 1.0D && entityVelocity.y < 0.2D) {
                            serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(serverPlayer));
                        }
                    }
                }
            }
        }
    };

    public PotentSulfurBlockEntity(final BlockPos worldPosition, final BlockState blockState) {
        super(ModBlockEntities.POTENT_SULFUR_TYPE, worldPosition, blockState);
    }

    @Override
    public void readData(IBlockEntityReadData data) {
        this.waitingCountdown = data.getIntOr("countdown", -1);
        this.eruptionTick = data.getLongOr("eruption_tick", -1L);
    }

    @Override
    public void writeData(IBlockEntityWriteData data) {
        data.putInt("countdown", this.waitingCountdown);
        data.putLong("eruption_tick", this.eruptionTick);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
    public void resetCountdown() {
        this.waitingCountdown = -1;
    }

    @Override
    public void setLevel(final Level level) {
        super.setLevel(level);
        if (this.eruptionTick == -1L) {
            this.eruptionTick = level.getGameTime();
        }
    }

    private static List<LivingEntity> getNearbyLivingEntities(final Level level, final BlockPos pos) {
        AABB aabb = (new AABB(pos)).inflate(2.5D, 0.0D, 2.5D);
        return level.getEntitiesOfClass(LivingEntity.class, aabb, EFFECT_PREDICATE);
    }

    public static Random geyserPositional(final Level level, final BlockPos pos) {
        long seed = (level instanceof net.minecraft.server.level.ServerLevel serverLevel ? serverLevel.getSeed() : 0L)
                ^ -904011478L ^ pos.asLong();
        return new Random(seed);
    }

    private static void spawnGeyserParticle(final Level level, final BlockPos sulfurPos, final BlockPos sourcePos) {
        int waterBlocks = sourcePos.getY() - sulfurPos.getY() - 1;
        level.addAlwaysVisibleParticle(new GeyserParticleOptions(ModParticles.GEYSER.get(), waterBlocks), true,
                (double) sourcePos.getX() + 0.5D, (double) sourcePos.getY(), (double) sourcePos.getZ() + 0.5D,
                0.0D, 0.0D, 0.0D);
    }

    private static void spawnNoxiousGasCloudParticle(final Level level, final Vec3 pos) {
        level.addParticle(ModParticles.NOXIOUS_GAS.get(), pos.x, pos.y, pos.z, 0.0D, 0.0D, 0.0D);
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

    private static boolean isGeyserPassableBlock(final BlockState state, final Level level, final BlockPos pos, final CollisionContext context) {
        return state.isAir() || state.is(Blocks.WATER) || state.getCollisionShape(level, pos, context).isEmpty();
    }

    private static BlockPos findNoxiousGasSourceBlock(final Level level, final BlockPos origin) {
        int maxY = origin.getY() + 4 + 1;
        CollisionContext geyserPositionContext = CollisionContext.empty();
        BlockPos.MutableBlockPos pos = origin.above(1).mutable();

        while (pos.getY() <= maxY) {
            BlockState state = level.getBlockState(pos);
            boolean isWaterLogged = level.getFluidState(pos).isSourceOfType(Fluids.WATER);
            if (isWaterLogged && (state.is(Blocks.WATER) || isGeyserPassableBlock(state, level, pos, geyserPositionContext))) {
                pos.move(Direction.UP);
                continue;
            }

            if (state.isAir() || isGeyserPassableBlock(state, level, pos, geyserPositionContext)) {
                return pos.immutable();
            }
            break;
        }
        return null;
    }

    public static boolean canBeReachedByNoxiousGas(final Level level, final BlockPos sourceBlock, final Vec3 pos) {
        BlockPos blockPos = new BlockPos(net.minecraft.util.Mth.floor(pos.x), net.minecraft.util.Mth.floor(pos.y), net.minecraft.util.Mth.floor(pos.z));
        CollisionContext geyserPositionContext = CollisionContext.empty();
        if (!isGeyserPassableBlock(level.getBlockState(blockPos), level, blockPos, geyserPositionContext)) {
            return false;
        } else if (pos.distanceToSqr(Vec3.atCenterOf(sourceBlock)) > 9.0D) {
            return false;
        } else {
            Vec3 belowSource = Vec3.atCenterOf(sourceBlock.below());
            Vec3 belowPos = new Vec3(pos.x, pos.y - 1.0D, pos.z);
            BlockPos belowBlockPos = new BlockPos(net.minecraft.util.Mth.floor(belowPos.x), net.minecraft.util.Mth.floor(belowPos.y), net.minecraft.util.Mth.floor(belowPos.z));
            return level.getFluidState(belowBlockPos).isSourceOfType(Fluids.WATER) && haveLineOfSight(level, belowSource, belowPos);
        }
    }

    private static boolean haveLineOfSight(final Level level, final Vec3 a, final Vec3 b) {
        HitResult hitResult = level.clip(new ClipContext(a, b, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, (net.minecraft.world.entity.Entity) null));
        return hitResult.getType() != HitResult.Type.BLOCK;
    }
}
