package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.network.ActionBarMessagePacket;
import com.kingodogo.buildscape.network.ModMessages;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.PacketDistributor;

import java.util.AbstractMap;
import java.util.Map;
import javax.annotation.Nullable;

public class GlowLightsBlock
        extends VineBlock
        implements EntityBlock, SimpleWaterloggedBlock {

    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final BooleanProperty WATERLOGGED =
            BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;

    private static final VoxelShape SHAPE_NORTH = Block.box(
            0.0D,
            0.0D,
            0.0D,
            16.0D,
            16.0D,
            1.0D
    );
    private static final VoxelShape SHAPE_SOUTH = Block.box(
            0.0D,
            0.0D,
            15.0D,
            16.0D,
            16.0D,
            16.0D
    );
    private static final VoxelShape SHAPE_EAST = Block.box(
            15.0D,
            0.0D,
            0.0D,
            16.0D,
            16.0D,
            16.0D
    );
    private static final VoxelShape SHAPE_WEST = Block.box(
            0.0D,
            0.0D,
            0.0D,
            1.0D,
            16.0D,
            16.0D
    );
    private static final VoxelShape SHAPE_UP = Block.box(
            0.0D,
            15.0D,
            0.0D,
            16.0D,
            16.0D,
            16.0D
    );
    private static final VoxelShape SHAPE_DOWN = Block.box(
            0.0D,
            0.0D,
            0.0D,
            16.0D,
            1.0D,
            16.0D
    );

    public GlowLightsBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(VineBlock.NORTH, false)
                        .setValue(VineBlock.EAST, false)
                        .setValue(VineBlock.SOUTH, false)
                        .setValue(VineBlock.WEST, false)
                        .setValue(VineBlock.UP, false)
                        .setValue(DOWN, false)
                        .setValue(WATERLOGGED, false)
                        .setValue(LIT, true)
        );
    }

    @Override
    protected void createBlockStateDefinition(
            StateDefinition.Builder<Block, BlockState> builder
    ) {
        super.createBlockStateDefinition(builder);
        builder.add(DOWN, WATERLOGGED, LIT);
    }

    @Override
    public int getLightEmission(
            BlockState state,
            BlockGetter level,
            BlockPos pos
    ) {
        return state.getValue(LIT) ? 15 : 0;
    }

    @Override
    public VoxelShape getShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        VoxelShape shape = Shapes.empty();
        if (state.getValue(VineBlock.NORTH)) shape =
                Shapes.or(shape, SHAPE_NORTH);
        if (state.getValue(VineBlock.SOUTH)) shape =
                Shapes.or(shape, SHAPE_SOUTH);
        if (state.getValue(VineBlock.EAST)) shape =
                Shapes.or(shape, SHAPE_EAST);
        if (state.getValue(VineBlock.WEST)) shape =
                Shapes.or(shape, SHAPE_WEST);
        if (state.getValue(VineBlock.UP)) shape =
                Shapes.or(shape, SHAPE_UP);
        if (state.getValue(DOWN)) shape = Shapes.or(shape, SHAPE_DOWN);
        return shape.isEmpty() ? Shapes.block() : shape;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos clickedPos = context.getClickedPos();
        BlockState blockState = context.getLevel().getBlockState(clickedPos);
        boolean isExistingGlowLight = blockState.getBlock() instanceof
                GlowLightsBlock;

        Direction clickedFace = context.getClickedFace();
        Direction attachDirection = clickedFace.getOpposite();

        if (
                isExistingGlowLight &&
                        context.getPlayer() != null &&
                        context.getPlayer().isShiftKeyDown()
        ) {
            return null;
        }

        if (
                isExistingGlowLight &&
                        !(context.getPlayer() != null && context.getPlayer().isShiftKeyDown())
        ) {
            if (blockState.getBlock() == this) {
                BooleanProperty property =
                        VineBlock.getPropertyForFace(
                                attachDirection
                        );
                if (
                        property != null &&
                                blockState.hasProperty(property) &&
                                blockState.getValue(property)
                ) {
                    return null;
                }
                if (
                        attachDirection == Direction.DOWN &&
                                blockState.hasProperty(DOWN) &&
                                blockState.getValue(DOWN)
                ) {
                    return null;
                }
            }
        }

        if (!isExistingGlowLight) {
            BlockPos originalPos = clickedPos.relative(clickedFace.getOpposite());
            BlockState originalState = context.getLevel().getBlockState(originalPos);
            if (originalState.getBlock() instanceof GlowLightsBlock) {
                Direction attachToGlow = clickedFace.getOpposite();
                BooleanProperty attachProperty = attachToGlow == Direction.DOWN
                        ? DOWN
                        : VineBlock.getPropertyForFace(
                        attachToGlow
                );

                if (
                        attachProperty != null &&
                                originalState.hasProperty(attachProperty) &&
                                originalState.getValue(attachProperty)
                ) {
                    return null;
                }

                BlockState state = this.defaultBlockState();
                if (state.hasProperty(attachProperty)) {
                    state = state.setValue(attachProperty, true);
                }
                BlockPos placePos = context.getClickedPos();
                FluidState fluidState = context.getLevel().getFluidState(placePos);
                return state
                        .setValue(LIT, true)
                        .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
            }
        }

        if (clickedFace == Direction.UP) {
            BlockState state;
            if (isExistingGlowLight) {
                if (blockState.getBlock() == this) {
                    state = blockState;
                } else {
                    state = this.defaultBlockState();
                    if (
                            blockState.hasProperty(VineBlock.UP)
                    ) state = state.setValue(
                            VineBlock.UP,
                            blockState.getValue(VineBlock.UP)
                    );
                    if (blockState.hasProperty(DOWN)) state = state.setValue(
                            DOWN,
                            blockState.getValue(DOWN)
                    );
                    if (
                            blockState.hasProperty(
                                    VineBlock.NORTH
                            )
                    ) state = state.setValue(
                            VineBlock.NORTH,
                            blockState.getValue(VineBlock.NORTH)
                    );
                    if (
                            blockState.hasProperty(
                                    VineBlock.SOUTH
                            )
                    ) state = state.setValue(
                            VineBlock.SOUTH,
                            blockState.getValue(VineBlock.SOUTH)
                    );
                    if (
                            blockState.hasProperty(
                                    VineBlock.EAST
                            )
                    ) state = state.setValue(
                            VineBlock.EAST,
                            blockState.getValue(VineBlock.EAST)
                    );
                    if (
                            blockState.hasProperty(
                                    VineBlock.WEST
                            )
                    ) state = state.setValue(
                            VineBlock.WEST,
                            blockState.getValue(VineBlock.WEST)
                    );
                }
            } else {
                state = this.defaultBlockState();
            }

            state = state.setValue(DOWN, true);
            BlockPos placePos = context.getClickedPos();
            FluidState fluidState = context.getLevel().getFluidState(placePos);
            return state
                    .setValue(LIT, true)
                    .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
        }

        if (clickedFace == Direction.DOWN) {
            BlockState state;
            if (isExistingGlowLight) {
                if (blockState.getBlock() == this) {
                    state = blockState;
                } else {
                    state = this.defaultBlockState();
                    if (
                            blockState.hasProperty(VineBlock.UP)
                    ) state = state.setValue(
                            VineBlock.UP,
                            blockState.getValue(VineBlock.UP)
                    );
                    if (blockState.hasProperty(DOWN)) state = state.setValue(
                            DOWN,
                            blockState.getValue(DOWN)
                    );
                    if (
                            blockState.hasProperty(
                                    VineBlock.NORTH
                            )
                    ) state = state.setValue(
                            VineBlock.NORTH,
                            blockState.getValue(VineBlock.NORTH)
                    );
                    if (
                            blockState.hasProperty(
                                    VineBlock.SOUTH
                            )
                    ) state = state.setValue(
                            VineBlock.SOUTH,
                            blockState.getValue(VineBlock.SOUTH)
                    );
                    if (
                            blockState.hasProperty(
                                    VineBlock.EAST
                            )
                    ) state = state.setValue(
                            VineBlock.EAST,
                            blockState.getValue(VineBlock.EAST)
                    );
                    if (
                            blockState.hasProperty(
                                    VineBlock.WEST
                            )
                    ) state = state.setValue(
                            VineBlock.WEST,
                            blockState.getValue(VineBlock.WEST)
                    );
                }
            } else {
                state = this.defaultBlockState();
            }

            state = state.setValue(
                    VineBlock.UP,
                    true
            );
            BlockPos placePos = context.getClickedPos();
            FluidState fluidState = context.getLevel().getFluidState(placePos);
            return state
                    .setValue(LIT, true)
                    .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
        }

        BlockState state;
        if (isExistingGlowLight) {
            if (blockState.getBlock() == this) {
                BooleanProperty property = attachDirection == Direction.DOWN
                        ? DOWN
                        : VineBlock.getPropertyForFace(
                        attachDirection
                );
                if (
                        property != null &&
                                blockState.hasProperty(property) &&
                                blockState.getValue(property)
                ) {
                    return null;
                }
            }

            if (blockState.getBlock() == this) {
                state = blockState;
            } else {
                state = this.defaultBlockState();
                if (
                        blockState.hasProperty(VineBlock.UP)
                ) state = state.setValue(
                        VineBlock.UP,
                        blockState.getValue(VineBlock.UP)
                );
                if (blockState.hasProperty(DOWN)) state = state.setValue(
                        DOWN,
                        blockState.getValue(DOWN)
                );
                if (
                        blockState.hasProperty(
                                VineBlock.NORTH
                        )
                ) state = state.setValue(
                        VineBlock.NORTH,
                        blockState.getValue(VineBlock.NORTH)
                );
                if (
                        blockState.hasProperty(
                                VineBlock.SOUTH
                        )
                ) state = state.setValue(
                        VineBlock.SOUTH,
                        blockState.getValue(VineBlock.SOUTH)
                );
                if (
                        blockState.hasProperty(VineBlock.EAST)
                ) state = state.setValue(
                        VineBlock.EAST,
                        blockState.getValue(VineBlock.EAST)
                );
                if (
                        blockState.hasProperty(VineBlock.WEST)
                ) state = state.setValue(
                        VineBlock.WEST,
                        blockState.getValue(VineBlock.WEST)
                );
            }

            BooleanProperty property = attachDirection == Direction.DOWN
                    ? DOWN
                    : VineBlock.getPropertyForFace(
                    attachDirection
            );
            if (property != null && state.hasProperty(property)) {
                if (!state.getValue(property)) {
                    state = state.setValue(property, true);
                } else {
                    return null;
                }
            }

            if (!this.hasAnyFace(state)) {
                return null;
            }
        } else {
            state = this.defaultBlockState();
            BlockState parentState = super.getStateForPlacement(context);
            if (parentState != null && parentState.is(this)) {
                state = parentState;
            }
        }

        BlockPos placePos = context.getClickedPos();
        FluidState fluidState = context.getLevel().getFluidState(placePos);
        return state
                .setValue(LIT, true)
                .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
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

        BooleanProperty faceProperty =
                VineBlock.getPropertyForFace(direction);
        if (
                faceProperty != null &&
                        state.hasProperty(faceProperty) &&
                        state.getValue(faceProperty)
        ) {
            if (
                    !canAttachTo(level, neighborState, neighborPos, direction.getOpposite())
            ) {
                state = state.setValue(faceProperty, false);
            }
        }
        if (direction == Direction.DOWN && state.getValue(DOWN)) {
            if (
                    !canAttachTo(level, neighborState, neighborPos, direction.getOpposite())
            ) {
                state = state.setValue(DOWN, false);
            }
        }

        if (!hasAnyFace(state)) {
            return Blocks.AIR.defaultBlockState();
        }

        return state;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED)
                ? Fluids.WATER.getSource(false)
                : super.getFluidState(state);
    }

    @Override
    public boolean canSurvive(
            BlockState state,
            LevelReader level,
            BlockPos pos
    ) {
        return hasAnyFace(state);
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        Item heldItem = context.getItemInHand().getItem();
        if (heldItem instanceof BlockItem) {
            Block heldBlock =
                    ((BlockItem) heldItem).getBlock();

            if (!(heldBlock instanceof GlowLightsBlock)) {
                return false;
            }

            if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) {
                return false;
            }

            Direction clickedFace = context.getClickedFace();
            Direction attachDirection = clickedFace.getOpposite();
            BooleanProperty property = attachDirection == Direction.DOWN
                    ? DOWN
                    : VineBlock.getPropertyForFace(
                    attachDirection
            );

            if (property != null && state.getBlock() == heldBlock) {
                return !state.hasProperty(property) || !state.getValue(property);
            }

            return true;
        }

        return false;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!level.isClientSide && newState.is(this)) {
            int removedFaces = countFaces(state) - countFaces(newState);
            if (removedFaces > 0) {
                Block.popResource(level, pos, new ItemStack(this, removedFaces));
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    private int countFaces(BlockState state) {
        int count = 0;
        for (Direction direction : Direction.values()) {
            BooleanProperty property = direction == Direction.DOWN
                    ? DOWN : VineBlock.getPropertyForFace(direction);
            if (state.getValue(property)) count++;
        }
        return count;
    }

    private boolean hasAnyFace(BlockState state) {
        return (
                state.getValue(VineBlock.NORTH) ||
                        state.getValue(VineBlock.EAST) ||
                        state.getValue(VineBlock.SOUTH) ||
                        state.getValue(VineBlock.WEST) ||
                        state.getValue(VineBlock.UP) ||
                        state.getValue(DOWN)
        );
    }

    private boolean canAttachTo(
            BlockGetter level,
            BlockState state,
            BlockPos pos,
            Direction direction
    ) {
        if (state.isAir()) {
            return false;
        }
        if (state.isFaceSturdy(level, pos, direction)) {
            return true;
        }
        if (Block.isFaceFull(state.getCollisionShape(level, pos), direction)) {
            return true;
        }
        return !state.getMaterial().isReplaceable();
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

        if (heldItem.isEmpty()) {
            boolean currentLit = state.getValue(LIT);
            BlockState newState = state.setValue(LIT, !currentLit);
            level.setBlock(pos, newState, 3);

            if (currentLit) {
                level.playSound(
                        null,
                        pos,
                        SoundEvents.STONE_BUTTON_CLICK_OFF,
                        SoundSource.BLOCKS,
                        0.3f,
                        1.0f
                );
            } else {
                level.playSound(
                        null,
                        pos,
                        SoundEvents.STONE_BUTTON_CLICK_ON,
                        SoundSource.BLOCKS,
                        0.3f,
                        1.0f
                );
            }

            return InteractionResult.SUCCESS;
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof GlowLightsBlockEntity glowBE) {
            Map.Entry<String, String> dyeInfo = getDyeColorAndName(
                    heldItem
            );
            if (dyeInfo != null) {
                if (!glowBE.canAddMoreColors()) {
                    TextComponent message =
                            new TextComponent(
                                    "Glow Lights already has " +
                                            GlowLightsBlockEntity.MAX_DYE_COLORS +
                                            " colors! Break and replace to reset."
                            );
                    message.withStyle(ChatFormatting.RED);
                    if (player instanceof ServerPlayer) {
                        ModMessages.INSTANCE.send(
                                PacketDistributor.PLAYER.with(() ->
                                        (ServerPlayer) player
                                ),
                                new ActionBarMessagePacket(
                                        message
                                )
                        );
                    }
                    return InteractionResult.CONSUME;
                }

                String dyeColor = dyeInfo.getKey();
                String dyeName = dyeInfo.getValue();
                boolean added = glowBE.addDyeColor(dyeColor);

                if (added) {
                    if (!player.getAbilities().instabuild) {
                        heldItem.shrink(1);
                    }

                    level.playSound(
                            null,
                            pos,
                            SoundEvents.DYE_USE,
                            SoundSource.BLOCKS,
                            1.0f,
                            1.0f
                    );

                    int colorCount = glowBE.getDyeColorCount();
                    String progressText =
                            " (" +
                                    colorCount +
                                    "/" +
                                    GlowLightsBlockEntity.MAX_DYE_COLORS +
                                    ")";
                    TextComponent message =
                            new TextComponent(
                                    "Glow Lights color added: " + dyeName + progressText
                            );
                    message.withStyle(ChatFormatting.GREEN);
                    if (player instanceof ServerPlayer) {
                        ModMessages.INSTANCE.send(
                                PacketDistributor.PLAYER.with(() ->
                                        (ServerPlayer) player
                                ),
                                new ActionBarMessagePacket(
                                        message
                                )
                        );
                    }

                    return InteractionResult.SUCCESS;
                }
            }
        }

        return InteractionResult.PASS;
    }

    @Nullable
    private Map.Entry<String, String> getDyeColorAndName(
            ItemStack stack
    ) {
        if (stack == null || stack.isEmpty()) {
            return null;
        }

        Item item = stack.getItem();

        if (item == Items.WHITE_DYE) {
            return new AbstractMap.SimpleEntry<>("#E8FEFD", "White");
        } else if (item == Items.ORANGE_DYE) {
            return new AbstractMap.SimpleEntry<>("#FF5C00", "Orange");
        } else if (item == Items.MAGENTA_DYE) {
            return new AbstractMap.SimpleEntry<>("#FF00FF", "Magenta");
        } else if (item == Items.LIGHT_BLUE_DYE) {
            return new AbstractMap.SimpleEntry<>("#3CDFFF", "Light Blue");
        } else if (item == Items.YELLOW_DYE) {
            return new AbstractMap.SimpleEntry<>("#FFFF00", "Yellow");
        } else if (item == Items.LIME_DYE) {
            return new AbstractMap.SimpleEntry<>("#BFFE00", "Lime");
        } else if (item == Items.PINK_DYE) {
            return new AbstractMap.SimpleEntry<>("#F686B7", "Pink");
        } else if (item == Items.GRAY_DYE) {
            return new AbstractMap.SimpleEntry<>("#232526", "Gray");
        } else if (item == Items.LIGHT_GRAY_DYE) {
            return new AbstractMap.SimpleEntry<>("#B1B8C5", "Light Gray");
        } else if (item == Items.CYAN_DYE) {
            return new AbstractMap.SimpleEntry<>("#00FFFF", "Cyan");
        } else if (item == Items.PURPLE_DYE) {
            return new AbstractMap.SimpleEntry<>("#AB87FF", "Purple");
        } else if (item == Items.BLUE_DYE) {
            return new AbstractMap.SimpleEntry<>("#1919EA", "Blue");
        } else if (item == Items.BROWN_DYE) {
            return new AbstractMap.SimpleEntry<>("#411900", "Brown");
        } else if (item == Items.GREEN_DYE) {
            return new AbstractMap.SimpleEntry<>("#39FF14", "Green");
        } else if (item == Items.RED_DYE) {
            return new AbstractMap.SimpleEntry<>("#FF0000", "Red");
        } else if (item == Items.BLACK_DYE) {
            return new AbstractMap.SimpleEntry<>("#07010C", "Black");
        }

        return null;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GlowLightsBlockEntity(pos, state);
    }
}
