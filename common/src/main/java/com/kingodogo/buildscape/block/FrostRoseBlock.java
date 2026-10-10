package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.particle.ModParticles;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.function.DoubleSupplier;
import java.util.function.IntUnaryOperator;
public abstract class FrostRoseBlock extends BushBlock implements SinksOnFarmland, ICommonShapeUpdate, ICommonEntityInside {

    protected static final VoxelShape SHAPE = Block.box(
            5.0D,
            0.0D,
            5.0D,
            11.0D,
            10.0D,
            11.0D
    );

    private static final String CONTACT_POSITION_KEY = "BuildscapeFrostRosePosition";
    private static final String CONTACT_DIMENSION_KEY = "BuildscapeFrostRoseDimension";
    private static final String CONTACT_START_KEY = "BuildscapeFrostRoseStart";
    private static final String CONTACT_LAST_DAMAGE_KEY = "BuildscapeFrostRoseLastDamage";
    private static final String CONTACT_LAST_TICK_KEY = "BuildscapeFrostRoseLastContact";

    public FrostRoseBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(ON_FARMLAND, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ON_FARMLAND);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        VoxelShape shape = SHAPE;
        if (state.getValue(ON_FARMLAND)) {
            return shape.move(0, -0.0625D, 0);
        }
        return shape;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        Block snowBricks = Services.PLATFORM.getBlock(new CommonId("buildscape", "snow_bricks"));
        return (
                state.is(BlockTags.DIRT) ||
                        state.is(Blocks.GRASS_BLOCK) ||
                        state.is(Blocks.FARMLAND) ||
                        state.is(Blocks.ICE) ||
                        state.is(Blocks.PACKED_ICE) ||
                        state.is(Blocks.BLUE_ICE) ||
                        state.is(Blocks.SNOW) ||
                        state.is(Blocks.SNOW_BLOCK) ||
                        state.is(Blocks.POWDER_SNOW) ||
                        (snowBricks != null && snowBricks != Blocks.AIR && state.is(snowBricks))
        );
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos belowPos = pos.below();
        BlockState belowState = level.getBlockState(belowPos);
        return this.mayPlaceOn(belowState, level, belowPos);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state != null) {
            return state.setValue(ON_FARMLAND, shouldSink(context.getLevel(), context.getClickedPos()));
        }
        return null;
    }

    @Override
    public BlockState onUpdateShape(BlockState state, Direction direction, BlockState neighborState, LevelReader level, BlockPos pos, BlockPos neighborPos) {
        if (direction == Direction.DOWN) {
            return state.setValue(ON_FARMLAND, shouldSink(level, pos));
        }
        return state;
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (!level.isClientSide() && entity instanceof LivingEntity livingEntity) {
            handleContact(level, pos, livingEntity);
        }
        super.stepOn(level, pos, state, entity);
    }

    @Override
    public void onEntityInside(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (!level.isClientSide() && entity instanceof LivingEntity livingEntity) {
            handleContact(level, pos, livingEntity);
        }
    }

    private static void handleContact(Level level, BlockPos pos, LivingEntity entity) {
        net.minecraft.nbt.CompoundTag data = Services.PLATFORM.getEntityData(entity);
        long currentTick = level.getGameTime();
        String dimension = Services.PLATFORM.getDimensionId(level).toString();

        boolean continuing = data.contains(CONTACT_START_KEY)
                && Services.PLATFORM.getTagLong(data, CONTACT_POSITION_KEY, 0L) == pos.asLong()
                && dimension.equals(Services.PLATFORM.getTagString(data, CONTACT_DIMENSION_KEY, ""))
                && currentTick - Services.PLATFORM.getTagLong(data, CONTACT_LAST_TICK_KEY, 0L) <= 1L;

        if (!continuing) {
            if (!data.contains(CONTACT_START_KEY)
                    && data.contains(CONTACT_LAST_TICK_KEY)
                    && Services.PLATFORM.getTagLong(data, CONTACT_LAST_TICK_KEY, 0L) == currentTick) {
                return;
            }
            data.putLong(CONTACT_POSITION_KEY, pos.asLong());
            data.putString(CONTACT_DIMENSION_KEY, dimension);
            data.putLong(CONTACT_START_KEY, currentTick);
            data.putLong(CONTACT_LAST_DAMAGE_KEY, currentTick);
            data.putLong(CONTACT_LAST_TICK_KEY, currentTick);
            applyFreezeDamage(level, pos, entity);
            return;
        }

        data.putLong(CONTACT_LAST_TICK_KEY, currentTick);
        long elapsedTicks = currentTick - Services.PLATFORM.getTagLong(data, CONTACT_START_KEY, 0L);
        if (elapsedTicks >= 40L) {
            clearContact(data);
            data.putLong(CONTACT_LAST_TICK_KEY, currentTick);
            return;
        }

        if (currentTick - Services.PLATFORM.getTagLong(data, CONTACT_LAST_DAMAGE_KEY, 0L) >= 20L) {
            applyFreezeDamage(level, pos, entity);
            data.putLong(CONTACT_LAST_DAMAGE_KEY, currentTick);
        }
    }

    private static void clearContact(net.minecraft.nbt.CompoundTag data) {
        data.remove(CONTACT_POSITION_KEY);
        data.remove(CONTACT_DIMENSION_KEY);
        data.remove(CONTACT_START_KEY);
        data.remove(CONTACT_LAST_DAMAGE_KEY);
        data.remove(CONTACT_LAST_TICK_KEY);
    }

    private static void applyFreezeDamage(Level level, BlockPos pos, LivingEntity entity) {
        com.kingodogo.buildscape.platform.Services.PLATFORM.hurtFreezeDamage(entity, 1.0F);
        Services.PLATFORM.playPowderSnowStep(level, pos);
        if (entity.canFreeze()) {
            entity.setTicksFrozen(Math.min(entity.getTicksFrozen() + 140, 300));
        }
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 1.0F;
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return false;
    }

    @Override
    public boolean skipRendering(BlockState state, BlockState adjacentState, Direction side) {
        return adjacentState.is(this) || super.skipRendering(state, adjacentState, side);
    }

    public void onAnimateTick(BlockState state, Level level, BlockPos pos, DoubleSupplier nextDouble, IntUnaryOperator nextInt) {
        int radius = 3;
        int count = 0;

        for (BlockPos p : BlockPos.betweenClosed(pos.offset(-radius, -radius, -radius), pos.offset(radius, radius, radius))) {
            if (level.getBlockState(p).is(this)) {
                count++;
            }
        }

        double normalizedProbability = (49.0 / 15.0) / count;
        if (count >= 5 && nextDouble.getAsDouble() < normalizedProbability) {
            int particlesToSpawn = 15 + nextInt.applyAsInt(6);
            if (nextDouble.getAsDouble() < 0.20) {
                particlesToSpawn = 21 + nextInt.applyAsInt(30);
            }

            double maxRadius = 40.0;
            for (int i = 0; i < particlesToSpawn; i++) {
                double r = maxRadius * Math.sqrt(nextDouble.getAsDouble());
                double angle = nextDouble.getAsDouble() * Math.PI * 2;

                double spawnX = pos.getX() + 0.5 + Math.cos(angle) * r;
                double spawnZ = pos.getZ() + 0.5 + Math.sin(angle) * r;
                double spawnY = pos.getY() + 10.0 + nextDouble.getAsDouble() * 15.0;

                double xSpeed = (nextDouble.getAsDouble() - 0.5) * 0.1;
                double ySpeed = -0.05 - nextDouble.getAsDouble() * 0.05;
                double zSpeed = (nextDouble.getAsDouble() - 0.5) * 0.1;

                level.addParticle(
                        ModParticles.SNOWFLAKE.get(),
                        spawnX, spawnY, spawnZ,
                        xSpeed, ySpeed, zSpeed
                );
            }
        }
    }
}
