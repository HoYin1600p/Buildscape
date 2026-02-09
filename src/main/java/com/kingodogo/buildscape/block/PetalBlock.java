package com.kingodogo.buildscape.block;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PetalBlock extends BushBlock implements BonemealableBlock {

    public static final IntegerProperty FLOWER_AMOUNT = IntegerProperty.create(
            "flower_amount",
            1,
            4
    );
    public static final DirectionProperty FACING =
            BlockStateProperties.HORIZONTAL_FACING;

    private static SoundType cachedSoundType = null;

    private static SoundType getFlowerBedSounds() {
        if (cachedSoundType == null) {
            cachedSoundType =
                    com.kingodogo.buildscape.sound.ModSounds.FLOWER_BED_SOUNDS();
        }
        return cachedSoundType;
    }

    private static final VoxelShape SHAPE_1_SOUTH = Block.box(
            0.0D,
            0.0D,
            0.0D,
            8.0D,
            3.0D,
            8.0D
    );
    private static final VoxelShape SHAPE_2_SOUTH = Block.box(
            0.0D,
            0.0D,
            0.0D,
            8.0D,
            3.0D,
            16.0D
    );
    private static final VoxelShape SHAPE_3_SOUTH = Block.box(
            0.0D,
            0.0D,
            0.0D,
            16.0D,
            3.0D,
            16.0D
    );
    private static final VoxelShape SHAPE_4_SOUTH = Block.box(
            0.0D,
            0.0D,
            0.0D,
            16.0D,
            3.0D,
            16.0D
    );

    private static final VoxelShape COLLISION_SHAPE = Block.box(
            0.0D,
            0.0D,
            0.0D,
            16.0D,
            3.0D,
            16.0D
    );
    private static final VoxelShape INSIDE_SHAPE = Block.box(
            0.0D,
            0.0D,
            0.0D,
            16.0D,
            16.0D,
            16.0D
    );

    private static final VoxelShape SHAPE_1_NORTH = Block.box(
            8.0D,
            0.0D,
            8.0D,
            16.0D,
            3.0D,
            16.0D
    );
    private static final VoxelShape SHAPE_2_NORTH = Block.box(
            8.0D,
            0.0D,
            0.0D,
            16.0D,
            3.0D,
            16.0D
    );
    private static final VoxelShape SHAPE_3_NORTH = Block.box(
            0.0D,
            0.0D,
            0.0D,
            16.0D,
            3.0D,
            16.0D
    );
    private static final VoxelShape SHAPE_4_NORTH = Block.box(
            0.0D,
            0.0D,
            0.0D,
            16.0D,
            3.0D,
            16.0D
    );

    private static final VoxelShape SHAPE_1_EAST = Block.box(
            0.0D,
            0.0D,
            8.0D,
            8.0D,
            3.0D,
            16.0D
    );
    private static final VoxelShape SHAPE_2_EAST = Block.box(
            0.0D,
            0.0D,
            8.0D,
            16.0D,
            3.0D,
            16.0D
    );
    private static final VoxelShape SHAPE_3_EAST = Block.box(
            0.0D,
            0.0D,
            0.0D,
            16.0D,
            3.0D,
            16.0D
    );
    private static final VoxelShape SHAPE_4_EAST = Block.box(
            0.0D,
            0.0D,
            0.0D,
            16.0D,
            3.0D,
            16.0D
    );

    private static final VoxelShape SHAPE_1_WEST = Block.box(
            8.0D,
            0.0D,
            0.0D,
            16.0D,
            3.0D,
            8.0D
    );
    private static final VoxelShape SHAPE_2_WEST = Block.box(
            0.0D,
            0.0D,
            0.0D,
            16.0D,
            3.0D,
            8.0D
    );
    private static final VoxelShape SHAPE_3_WEST = Block.box(
            0.0D,
            0.0D,
            0.0D,
            16.0D,
            3.0D,
            16.0D
    );
    private static final VoxelShape SHAPE_4_WEST = Block.box(
            0.0D,
            0.0D,
            0.0D,
            16.0D,
            3.0D,
            16.0D
    );

    public PetalBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(FLOWER_AMOUNT, 1)
                        .setValue(FACING, Direction.SOUTH)
        );
    }

    @Override
    public SoundType getSoundType(BlockState state) {
        return getFlowerBedSounds();
    }

    @Override
    protected void createBlockStateDefinition(
            StateDefinition.Builder<Block, BlockState> builder
    ) {
        builder.add(FLOWER_AMOUNT, FACING);
    }

    @Override
    public VoxelShape getShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        int stage = state.getValue(FLOWER_AMOUNT);
        Direction facing = state.getValue(FACING);

        return switch (facing) {
            case NORTH -> switch (stage) {
                case 1 -> SHAPE_1_NORTH;
                case 2 -> SHAPE_2_NORTH;
                case 3 -> SHAPE_3_NORTH;
                default -> SHAPE_4_NORTH;
            };
            case EAST -> switch (stage) {
                case 1 -> SHAPE_1_EAST;
                case 2 -> SHAPE_2_EAST;
                case 3 -> SHAPE_3_EAST;
                default -> SHAPE_4_EAST;
            };
            case WEST -> switch (stage) {
                case 1 -> SHAPE_1_WEST;
                case 2 -> SHAPE_2_WEST;
                case 3 -> SHAPE_3_WEST;
                default -> SHAPE_4_WEST;
            };
            default -> switch (stage) {
                case 1 -> SHAPE_1_SOUTH;
                case 2 -> SHAPE_2_SOUTH;
                case 3 -> SHAPE_3_SOUTH;
                default -> SHAPE_4_SOUTH;
            };
        };
    }

    @Override
    public VoxelShape getCollisionShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        return Shapes.empty();
    }

    @Override
    public void entityInside(
            BlockState state,
            Level level,
            BlockPos pos,
            Entity entity
    ) {
        if (
                entity instanceof Player player &&
                        player.isOnGround() &&
                        level.isClientSide
        ) {
            float stepInterval = 2.0f;
            int currentStep = (int) (player.walkDist / stepInterval);
            int lastStep = (int) (player.walkDistO / stepInterval);

            if (currentStep != lastStep && player.walkDist > player.walkDistO) {
                SoundType sounds = getFlowerBedSounds();
                float volume = 0.25f;
                float pitch = 1.2f;
                if (sounds instanceof CustomSoundType customSounds) {
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
        super.entityInside(state, level, pos, entity);
    }

    @Override
    protected boolean mayPlaceOn(
            BlockState state,
            BlockGetter level,
            BlockPos pos
    ) {
        return (
                state.isFaceSturdy(level, pos, Direction.UP) &&
                        state.getMaterial().isSolid() &&
                        !state.getMaterial().isReplaceable()
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
        if (
                state != null &&
                        this.canSurvive(state, context.getLevel(), context.getClickedPos())
        ) {
            Direction playerFacing = context.getHorizontalDirection();
            return state.setValue(FLOWER_AMOUNT, 1).setValue(FACING, playerFacing);
        }
        return null;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
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
        ItemStack itemStack = player.getItemInHand(hand);

        if (itemStack.getItem() instanceof BlockItem blockItem) {
            if (
                    blockItem.getBlock() instanceof PetalBlock &&
                            blockItem.getBlock() == this
            ) {
                int currentAmount = state.getValue(FLOWER_AMOUNT);
                if (currentAmount < 4) {
                    if (!level.isClientSide) {
                        level.setBlock(
                                pos,
                                state.setValue(FLOWER_AMOUNT, currentAmount + 1),
                                3
                        );
                        if (!player.getAbilities().instabuild) {
                            itemStack.shrink(1);
                        }
                    }
                    SoundType sounds = getFlowerBedSounds();
                    level.playSound(
                            player,
                            pos,
                            sounds.getPlaceSound(),
                            SoundSource.BLOCKS,
                            sounds.getVolume(),
                            sounds.getPitch()
                    );
                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    public boolean isValidBonemealTarget(
            BlockGetter level,
            BlockPos pos,
            BlockState state,
            boolean isClient
    ) {
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
        int currentStage = state.getValue(FLOWER_AMOUNT);

        if (currentStage < 4) {
            level.setBlock(pos, state.setValue(FLOWER_AMOUNT, currentStage + 1), 3);
        } else {
            ItemStack petalStack = new ItemStack(this, 1);
            ItemEntity itemEntity = new ItemEntity(
                    level,
                    pos.getX() + 0.5D,
                    pos.getY() + 0.5D,
                    pos.getZ() + 0.5D,
                    petalStack
            );
            itemEntity.setDefaultPickUpDelay();
            level.addFreshEntity(itemEntity);
        }
    }

    @Override
    public List<ItemStack> getDrops(
            BlockState state,
            LootContext.Builder builder
    ) {
        int stage = state.getValue(FLOWER_AMOUNT);
        return Collections.singletonList(new ItemStack(this, stage));
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
    public boolean useShapeForLightOcclusion(BlockState state) {
        return false;
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
}
