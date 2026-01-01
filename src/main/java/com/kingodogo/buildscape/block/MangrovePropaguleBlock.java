package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.block.ModBlocks;

import java.util.List;
import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MangrovePropaguleBlock
        extends BushBlock
        implements BonemealableBlock, SimpleWaterloggedBlock {

    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 4);
    public static final IntegerProperty STAGE = IntegerProperty.create(
            "stage",
            0,
            1
    );
    public static final BooleanProperty HANGING = BooleanProperty.create(
            "hanging"
    );
    public static final BooleanProperty WATERLOGGED =
            BlockStateProperties.WATERLOGGED;

    private static final VoxelShape SHAPE = Block.box(
            6.0D,
            0.0D,
            6.0D,
            10.0D,
            12.0D,
            10.0D
    );
    private static final VoxelShape HANGING_SHAPE_0 = Block.box(
            7.0D,
            13.0D,
            7.0D,
            9.0D,
            16.0D,
            9.0D
    );
    private static final VoxelShape HANGING_SHAPE_1 = Block.box(
            7.0D,
            10.0D,
            7.0D,
            9.0D,
            16.0D,
            9.0D
    );
    private static final VoxelShape HANGING_SHAPE_2 = Block.box(
            7.0D,
            7.0D,
            7.0D,
            9.0D,
            16.0D,
            9.0D
    );
    private static final VoxelShape HANGING_SHAPE_3 = Block.box(
            7.0D,
            3.0D,
            7.0D,
            9.0D,
            16.0D,
            9.0D
    );
    private static final int GROWTH_RADIUS = 32;

    public MangrovePropaguleBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(AGE, 0)
                        .setValue(STAGE, 0)
                        .setValue(HANGING, false)
                        .setValue(WATERLOGGED, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(
            StateDefinition.Builder<Block, BlockState> builder
    ) {
        builder.add(AGE, STAGE, HANGING, WATERLOGGED);
    }

    @Override
    public VoxelShape getShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        if (state.getValue(HANGING)) {
            int age = state.getValue(AGE);
            switch (age) {
                case 0:
                    return HANGING_SHAPE_0;
                case 1:
                    return HANGING_SHAPE_1;
                case 2:
                    return HANGING_SHAPE_2;
                case 3:
                    return HANGING_SHAPE_3;
                default:
                    return HANGING_SHAPE_0;
            }
        }
        return SHAPE;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        BlockState aboveState = level.getBlockState(pos.above());
        FluidState fluidstate = level.getFluidState(pos);

        boolean hanging =
                aboveState.is(BlockTags.LEAVES) ||
                        aboveState.is(ModBlocks.MANGROVE_LEAVES.get());

        return this.defaultBlockState()
                .setValue(HANGING, hanging)
                .setValue(AGE, 0)
                .setValue(STAGE, 0)
                .setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        if (state.getValue(HANGING)) {
            BlockState aboveState = level.getBlockState(pos.above());
            return (
                    aboveState.is(BlockTags.LEAVES) ||
                            aboveState.is(ModBlocks.MANGROVE_LEAVES.get())
            );
        } else {
            BlockPos belowPos = pos.below();
            BlockState belowState = level.getBlockState(belowPos);
            return (
                    (belowState.is(BlockTags.DIRT) && !belowState.is(Blocks.DIRT_PATH)) ||
                            belowState.is(Blocks.MOSS_BLOCK) ||
                            belowState.is(ModBlocks.MUD.get()) ||
                            belowState.is(Blocks.CLAY) ||
                            belowState.is(ModBlocks.MUDDY_MANGROVE_ROOTS.get())
            );
        }
    }

    @Override
    public BlockState updateShape(
            BlockState state,
            Direction direction,
            BlockState adjacentState,
            LevelAccessor level,
            BlockPos pos,
            BlockPos adjacentPos
    ) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        if (state.getValue(HANGING)) {
            if (direction == Direction.UP) {
                BlockState aboveState = level.getBlockState(pos.above());
                if (
                        !aboveState.is(BlockTags.LEAVES) &&
                                !aboveState.is(ModBlocks.MANGROVE_LEAVES.get())
                ) {
                    return Blocks.AIR.defaultBlockState();
                }
            }
        } else {
            if (direction == Direction.DOWN && !this.canSurvive(state, level, pos)) {
                return Blocks.AIR.defaultBlockState();
            }
        }

        return super.updateShape(
                state,
                direction,
                adjacentState,
                level,
                pos,
                adjacentPos
        );
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED)
                ? Fluids.WATER.getSource(false)
                : super.getFluidState(state);
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return !state.getValue(HANGING);
    }

    @Override
    public void randomTick(
            BlockState state,
            ServerLevel level,
            BlockPos pos,
            Random random
    ) {
        if (state.getValue(HANGING)) {
            return;
        }
        if (!hasPlayerNearby(level, pos)) {
            return;
        }
        if (random.nextInt(7) == 0) {
            this.advanceTree(level, pos, state, random);
        }
    }

    private boolean hasPlayerNearby(ServerLevel level, BlockPos pos) {
        AABB searchBox = new AABB(pos).inflate(GROWTH_RADIUS);
        List<Player> players = level.getEntitiesOfClass(Player.class, searchBox);
        return !players.isEmpty();
    }

    @Override
    public boolean isValidBonemealTarget(
            BlockGetter level,
            BlockPos pos,
            BlockState state,
            boolean isClient
    ) {
        if (state.getValue(HANGING)) {
            int age = state.getValue(AGE);
            return age < 3;
        }
        return true;
    }

    @Override
    public boolean isBonemealSuccess(
            Level level,
            Random random,
            BlockPos pos,
            BlockState state
    ) {
        return true;
    }

    @Override
    public void performBonemeal(
            ServerLevel level,
            Random random,
            BlockPos pos,
            BlockState state
    ) {
        if (state.getValue(HANGING)) {
            int currentAge = state.getValue(AGE);
            if (currentAge < 3) {
                level.setBlock(pos, state.setValue(AGE, currentAge + 1), 4);
            }
            return;
        }
        if (state.getValue(STAGE) < 1) {
            level.setBlock(pos, state.setValue(STAGE, state.getValue(STAGE) + 1), 4);
        } else {
            this.advanceTree(level, pos, state, random, true);
        }
    }

    private void advanceTree(
            ServerLevel level,
            BlockPos pos,
            BlockState state,
            Random random
    ) {
        advanceTree(level, pos, state, random, false);
    }

    private void advanceTree(
            ServerLevel level,
            BlockPos pos,
            BlockState state,
            Random random,
            boolean bonemeal
    ) {
        if (state.getValue(HANGING)) {
            return;
        }
        if (state.getValue(STAGE) < 1) {
            level.setBlock(pos, state.setValue(STAGE, state.getValue(STAGE) + 1), 4);
            return;
        }
        if (!bonemeal) {
            if (!canGrowTree(level, pos, state)) {
                return;
            }
        }
        String variant =
                com.kingodogo.buildscape.worldgen.ModConfiguredFeatures.getRandomMangroveTreeVariant(
                        random
                );
        net.minecraft.resources.ResourceLocation featureLocation =
                com.kingodogo.buildscape.worldgen.ModConfiguredFeatures.getMangroveTreeResourceLocation(
                        variant
                );
        java.util.Optional<
                ? extends net.minecraft.core.Holder<
                        ? extends net.minecraft.world.level.levelgen.feature.ConfiguredFeature<
                                ?,
                                ?
                                >
                        >
                > featureHolder = level
                .registryAccess()
                .registry(net.minecraft.core.Registry.CONFIGURED_FEATURE_REGISTRY)
                .flatMap(registry ->
                        registry.getHolder(
                                net.minecraft.resources.ResourceKey.create(
                                        net.minecraft.core.Registry.CONFIGURED_FEATURE_REGISTRY,
                                        featureLocation
                                )
                        )
                );

        if (featureHolder.isPresent()) {
            net.minecraft.world.level.levelgen.feature.ConfiguredFeature<
                    ?,
                    ?
                    > feature = featureHolder.get().value();

            if (
                    feature.feature() ==
                            net.minecraft.world.level.levelgen.feature.Feature.TREE
            ) {
                BlockPos belowPos = pos.below();
                BlockState belowState = level.getBlockState(belowPos);
                if (belowState.is(Blocks.GRASS_BLOCK)) {
                    level.setBlock(belowPos, Blocks.DIRT.defaultBlockState(), 3);
                }
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 4);
                @SuppressWarnings("unchecked")
                net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration treeConfig =
                        (net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration) feature.config();
                net.minecraft.world.level.levelgen.feature.FeaturePlaceContext<
                        net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration
                        > placeContext =
                        new net.minecraft.world.level.levelgen.feature.FeaturePlaceContext<>(
                                java.util.Optional.empty(),
                                level,
                                level.getChunkSource().getGenerator(),
                                random,
                                pos,
                                treeConfig
                        );
                boolean success =
                        net.minecraft.world.level.levelgen.feature.Feature.TREE.place(
                                placeContext
                        );
                if (!success) {
                    level.setBlock(pos, state, 4);
                }
            } else {
                com.kingodogo.buildscape.BuildScape.getLogger()
                        .warn(
                                "Mangrove tree feature {} is not a tree feature!",
                                featureLocation
                        );
                level.setBlock(pos, state, 4);
            }
        } else {
            com.kingodogo.buildscape.BuildScape.getLogger()
                    .warn(
                            "Mangrove tree feature {} not found in registry! Cannot grow tree.",
                            featureLocation
                    );
            level.setBlock(pos, state, 4);
        }
    }

    private boolean canGrowTree(
            LevelReader level,
            BlockPos pos,
            BlockState state
    ) {
        if (level.getMaxLocalRawBrightness(pos) < 9) {
            return false;
        }
        for (int y = 1; y <= 6; y++) {
            BlockPos checkPos = pos.above(y);
            BlockState checkState = level.getBlockState(checkPos);
            if (
                    !checkState.isAir() &&
                            !checkState.is(BlockTags.DIRT) &&
                            !isLogOrWood(checkState)
            ) {
                return false;
            }
        }
        boolean hasSolidBlockBelow = false;
        for (int x = -4; x <= 4; x++) {
            for (int z = -4; z <= 4; z++) {
                if (x == 0 && z == 0) continue;
                for (int y = -4; y <= 4; y++) {
                    BlockPos checkPos = pos.offset(x, y, z);
                    BlockState checkState = level.getBlockState(checkPos);
                    if (isSolidBlock(level, checkPos, checkState)) {
                        hasSolidBlockBelow = true;
                        break;
                    }
                }
                if (hasSolidBlockBelow) break;
            }
            if (hasSolidBlockBelow) break;
        }
        if (!hasSolidBlockBelow) {
            return false;
        }
        boolean hasRootLandingSpot = false;
        for (int x = -4; x <= 4; x++) {
            for (int z = -4; z <= 4; z++) {
                for (int y = 1; y <= 11; y++) {
                    BlockPos checkPos = pos.offset(x, -y, z);
                    BlockState checkState = level.getBlockState(checkPos);

                    if (isSolidBlock(level, checkPos, checkState)) {
                        hasRootLandingSpot = true;
                        break;
                    }
                }
                if (hasRootLandingSpot) break;
            }
            if (hasRootLandingSpot) break;
        }

        return hasRootLandingSpot;
    }

    private boolean isLogOrWood(BlockState state) {
        return (
                state.is(BlockTags.LOGS) ||
                        state.is(BlockTags.PLANKS) ||
                        state.getBlock() == ModBlocks.MANGROVE_LOG.get() ||
                        state.getBlock() == ModBlocks.MANGROVE_WOOD.get() ||
                        state.getBlock() == ModBlocks.STRIPPED_MANGROVE_LOG.get() ||
                        state.getBlock() == ModBlocks.STRIPPED_MANGROVE_WOOD.get()
        );
    }

    private boolean isSolidBlock(
            BlockGetter level,
            BlockPos pos,
            BlockState state
    ) {
        if (state.is(ModBlocks.MUD.get())) {
            return false;
        }
        return (
                state.isFaceSturdy(level, pos, Direction.UP) &&
                        state.getMaterial().isSolid() &&
                        !state.getMaterial().isReplaceable()
        );
    }

    @Override
    public VoxelShape getVisualShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        return Shapes.empty();
    }

    @Override
    public float getShadeBrightness(
            BlockState state,
            BlockGetter level,
            BlockPos pos
    ) {
        return 1.0F;
    }

    @Override
    public boolean propagatesSkylightDown(
            BlockState state,
            BlockGetter reader,
            BlockPos pos
    ) {
        return true;
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return 0;
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return false;
    }

    @Override
    public boolean skipRendering(
            BlockState state,
            BlockState adjacentState,
            Direction side
    ) {
        return (
                adjacentState.is(this) || super.skipRendering(state, adjacentState, side)
        );
    }
}
