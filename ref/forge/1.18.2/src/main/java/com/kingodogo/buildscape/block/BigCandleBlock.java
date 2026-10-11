package com.kingodogo.buildscape.block;

import java.util.List;
import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.ForgeRegistries;

public class BigCandleBlock extends Block implements SimpleWaterloggedBlock {

    public static final BooleanProperty WATERLOGGED =
            BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty LIT = BooleanProperty.create("lit");

    private final int lightLevel;

    private static final VoxelShape SHAPE = Shapes.or(
            Block.box(5.5, 0, 5.5, 10.5, 7, 10.5),
            Block.box(5.25, 7, 5.25, 10.75, 7.5, 10.75),
            Block.box(7.625, 7, 8, 8.375, 10, 8)
    );

    private static final VoxelShape SHAPE_ON_CAKE = Shapes.or(
            Block.box(5.5, 0, 5.5, 10.5, 7, 10.5),
            Block.box(5.25, 7, 5.25, 10.75, 7.5, 10.75),
            Block.box(7.625, 7, 8, 8.375, 10, 8)
    );

    public BigCandleBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.lightLevel = 12;
        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(LIT, false)
                        .setValue(WATERLOGGED, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(
            StateDefinition.Builder<Block, BlockState> builder
    ) {
        builder.add(LIT, WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidstate = context
                .getLevel()
                .getFluidState(context.getClickedPos());
        BlockPos clickedPos = context.getClickedPos();
        BlockState clickedState = context.getLevel().getBlockState(clickedPos);

        if (
                clickedState.is(Blocks.CAKE) && context.getClickedFace() == Direction.UP
        ) {
            BlockPos placePos = clickedPos.above();
            if (context.getLevel().getBlockState(placePos).canBeReplaced(context)) {
                return this.defaultBlockState()
                        .setValue(LIT, false)
                        .setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
            }
        }

        BlockPos pos = context.getClickedPos();
        return this.defaultBlockState()
                .setValue(LIT, false)
                .setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return true;
    }

    @Override
    public BlockState updateShape(
            BlockState state,
            Direction direction,
            BlockState neighborState,
            LevelAccessor level,
            BlockPos pos,
            BlockPos neighborPos
    ) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(
                state,
                direction,
                neighborState,
                level,
                pos,
                neighborPos
        );
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED)
                ? Fluids.WATER.getSource(false)
                : super.getFluidState(state);
    }

    @Override
    public int getLightEmission(
            BlockState state,
            BlockGetter level,
            BlockPos pos
    ) {
        return state.getValue(LIT) ? lightLevel : 0;
    }

    @Override
    public InteractionResult use(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hit
    ) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        ItemStack heldItem = player.getItemInHand(hand);
        boolean currentlyLit = state.getValue(LIT);
        boolean isWaterlogged = state.getValue(WATERLOGGED);

        if (
                heldItem.getItem() == Items.FLINT_AND_STEEL ||
                        heldItem.getItem() == Items.FIRE_CHARGE
        ) {
            if (!currentlyLit && !isWaterlogged) {
                level.setBlock(pos, state.setValue(LIT, true), 3);
                level.playSound(
                        null,
                        pos,
                        net.minecraft.sounds.SoundEvents.FLINTANDSTEEL_USE,
                        net.minecraft.sounds.SoundSource.BLOCKS,
                        1.0f,
                        1.0f
                );
                return InteractionResult.SUCCESS;
            }
        } else if (heldItem.isEmpty() || heldItem.getItem() == Items.WATER_BUCKET) {
            if (currentlyLit) {
                level.setBlock(pos, state.setValue(LIT, false), 3);
                level.playSound(
                        null,
                        pos,
                        SoundEvents.CANDLE_EXTINGUISH,
                        net.minecraft.sounds.SoundSource.BLOCKS,
                        1.0f,
                        1.0f
                );
                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    public void animateTick(
            BlockState state,
            Level level,
            BlockPos pos,
            Random random
    ) {
        if (state.getValue(LIT)) {
            BlockState belowState = level.getBlockState(pos.below());
            boolean onCake = belowState.is(Blocks.CAKE);

            double wickX = pos.getX() + 0.5;
            double wickY = pos.getY() + (onCake ? 0.125 : 0.625);
            double wickZ = pos.getZ() + 0.5;

            level.addParticle(
                    ParticleTypes.FLAME,
                    wickX + (random.nextDouble() - 0.5) * 0.05,
                    wickY,
                    wickZ + (random.nextDouble() - 0.5) * 0.05,
                    0.0,
                    0.0,
                    0.0
            );

            if (random.nextInt(10) == 0) {
                level.addParticle(
                        ParticleTypes.SMOKE,
                        wickX + (random.nextDouble() - 0.5) * 0.1,
                        wickY,
                        wickZ + (random.nextDouble() - 0.5) * 0.1,
                        0.0,
                        0.05,
                        0.0
                );
            }

            if (level.isClientSide && random.nextInt(35) == 0) {
                ResourceLocation soundLocation = new ResourceLocation(
                        "minecraft",
                        "block.candle.ambient"
                );
                SoundEvent candleAmbient = ForgeRegistries.SOUND_EVENTS.getValue(
                        soundLocation
                );
                if (candleAmbient == null) {
                    candleAmbient = new SoundEvent(soundLocation);
                }

                level.playLocalSound(
                        pos.getX() + 0.5,
                        pos.getY() + 0.5,
                        pos.getZ() + 0.5,
                        candleAmbient,
                        net.minecraft.sounds.SoundSource.BLOCKS,
                        0.8f + random.nextFloat() * 0.2f,
                        0.8f + random.nextFloat() * 0.4f,
                        false
                );
            }
        }
    }

    @Override
    public VoxelShape getShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        BlockState belowState = level.getBlockState(pos.below());
        if (belowState.is(Blocks.CAKE)) {
            return SHAPE_ON_CAKE;
        }
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        BlockState belowState = level.getBlockState(pos.below());
        if (belowState.is(Blocks.CAKE)) {
            return SHAPE_ON_CAKE;
        }
        return SHAPE;
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
    public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return 0;
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
    public float getShadeBrightness(
            BlockState state,
            BlockGetter level,
            BlockPos pos
    ) {
        return 1.0F;
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return false;
    }

    @Override
    public boolean skipRendering(
            BlockState state,
            BlockState adjacentBlockState,
            Direction side
    ) {
        return (
                adjacentBlockState.is(this) ||
                        super.skipRendering(state, adjacentBlockState, side)
        );
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return state.getValue(LIT);
    }

    @Override
    public void randomTick(
            BlockState state,
            ServerLevel level,
            BlockPos pos,
            Random random
    ) {
        if (state.getValue(LIT)) {
            if (random.nextInt(35) == 0) {
                ResourceLocation soundLocation = new ResourceLocation(
                        "minecraft",
                        "block.candle.ambient"
                );
                SoundEvent candleAmbient = ForgeRegistries.SOUND_EVENTS.getValue(
                        soundLocation
                );
                if (candleAmbient == null) {
                    candleAmbient = new SoundEvent(soundLocation);
                }

                level.playSound(
                        null,
                        pos,
                        candleAmbient,
                        net.minecraft.sounds.SoundSource.BLOCKS,
                        0.8f + random.nextFloat() * 0.2f,
                        0.8f + random.nextFloat() * 0.4f
                );
            }
        }
    }

    @Override
    public List<ItemStack> getDrops(
            BlockState state,
            LootContext.Builder builder
    ) {
        return List.of(new ItemStack(this));
    }
}
