package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
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

import javax.annotation.Nullable;

public class GlowLightsBlock
        extends net.minecraft.world.level.block.VineBlock
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
                        .setValue(net.minecraft.world.level.block.VineBlock.NORTH, false)
                        .setValue(net.minecraft.world.level.block.VineBlock.EAST, false)
                        .setValue(net.minecraft.world.level.block.VineBlock.SOUTH, false)
                        .setValue(net.minecraft.world.level.block.VineBlock.WEST, false)
                        .setValue(net.minecraft.world.level.block.VineBlock.UP, false)
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
        if (state.getValue(net.minecraft.world.level.block.VineBlock.NORTH)) shape =
                Shapes.or(shape, SHAPE_NORTH);
        if (state.getValue(net.minecraft.world.level.block.VineBlock.SOUTH)) shape =
                Shapes.or(shape, SHAPE_SOUTH);
        if (state.getValue(net.minecraft.world.level.block.VineBlock.EAST)) shape =
                Shapes.or(shape, SHAPE_EAST);
        if (state.getValue(net.minecraft.world.level.block.VineBlock.WEST)) shape =
                Shapes.or(shape, SHAPE_WEST);
        if (state.getValue(net.minecraft.world.level.block.VineBlock.UP)) shape =
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
                        net.minecraft.world.level.block.VineBlock.getPropertyForFace(
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
                        : net.minecraft.world.level.block.VineBlock.getPropertyForFace(
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
                BlockPos placePos = context
                        .getClickedPos()
                        .relative(context.getClickedFace());
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
                            blockState.hasProperty(net.minecraft.world.level.block.VineBlock.UP)
                    ) state = state.setValue(
                            net.minecraft.world.level.block.VineBlock.UP,
                            blockState.getValue(net.minecraft.world.level.block.VineBlock.UP)
                    );
                    if (blockState.hasProperty(DOWN)) state = state.setValue(
                            DOWN,
                            blockState.getValue(DOWN)
                    );
                    if (
                            blockState.hasProperty(
                                    net.minecraft.world.level.block.VineBlock.NORTH
                            )
                    ) state = state.setValue(
                            net.minecraft.world.level.block.VineBlock.NORTH,
                            blockState.getValue(net.minecraft.world.level.block.VineBlock.NORTH)
                    );
                    if (
                            blockState.hasProperty(
                                    net.minecraft.world.level.block.VineBlock.SOUTH
                            )
                    ) state = state.setValue(
                            net.minecraft.world.level.block.VineBlock.SOUTH,
                            blockState.getValue(net.minecraft.world.level.block.VineBlock.SOUTH)
                    );
                    if (
                            blockState.hasProperty(
                                    net.minecraft.world.level.block.VineBlock.EAST
                            )
                    ) state = state.setValue(
                            net.minecraft.world.level.block.VineBlock.EAST,
                            blockState.getValue(net.minecraft.world.level.block.VineBlock.EAST)
                    );
                    if (
                            blockState.hasProperty(
                                    net.minecraft.world.level.block.VineBlock.WEST
                            )
                    ) state = state.setValue(
                            net.minecraft.world.level.block.VineBlock.WEST,
                            blockState.getValue(net.minecraft.world.level.block.VineBlock.WEST)
                    );
                }
            } else {
                state = this.defaultBlockState();
            }

            state = state.setValue(DOWN, true);
            BlockPos placePos = context
                    .getClickedPos()
                    .relative(context.getClickedFace());
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
                            blockState.hasProperty(net.minecraft.world.level.block.VineBlock.UP)
                    ) state = state.setValue(
                            net.minecraft.world.level.block.VineBlock.UP,
                            blockState.getValue(net.minecraft.world.level.block.VineBlock.UP)
                    );
                    if (blockState.hasProperty(DOWN)) state = state.setValue(
                            DOWN,
                            blockState.getValue(DOWN)
                    );
                    if (
                            blockState.hasProperty(
                                    net.minecraft.world.level.block.VineBlock.NORTH
                            )
                    ) state = state.setValue(
                            net.minecraft.world.level.block.VineBlock.NORTH,
                            blockState.getValue(net.minecraft.world.level.block.VineBlock.NORTH)
                    );
                    if (
                            blockState.hasProperty(
                                    net.minecraft.world.level.block.VineBlock.SOUTH
                            )
                    ) state = state.setValue(
                            net.minecraft.world.level.block.VineBlock.SOUTH,
                            blockState.getValue(net.minecraft.world.level.block.VineBlock.SOUTH)
                    );
                    if (
                            blockState.hasProperty(
                                    net.minecraft.world.level.block.VineBlock.EAST
                            )
                    ) state = state.setValue(
                            net.minecraft.world.level.block.VineBlock.EAST,
                            blockState.getValue(net.minecraft.world.level.block.VineBlock.EAST)
                    );
                    if (
                            blockState.hasProperty(
                                    net.minecraft.world.level.block.VineBlock.WEST
                            )
                    ) state = state.setValue(
                            net.minecraft.world.level.block.VineBlock.WEST,
                            blockState.getValue(net.minecraft.world.level.block.VineBlock.WEST)
                    );
                }
            } else {
                state = this.defaultBlockState();
            }

            state = state.setValue(
                    net.minecraft.world.level.block.VineBlock.UP,
                    true
            );
            BlockPos placePos = context
                    .getClickedPos()
                    .relative(context.getClickedFace());
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
                        : net.minecraft.world.level.block.VineBlock.getPropertyForFace(
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
                        blockState.hasProperty(net.minecraft.world.level.block.VineBlock.UP)
                ) state = state.setValue(
                        net.minecraft.world.level.block.VineBlock.UP,
                        blockState.getValue(net.minecraft.world.level.block.VineBlock.UP)
                );
                if (blockState.hasProperty(DOWN)) state = state.setValue(
                        DOWN,
                        blockState.getValue(DOWN)
                );
                if (
                        blockState.hasProperty(
                                net.minecraft.world.level.block.VineBlock.NORTH
                        )
                ) state = state.setValue(
                        net.minecraft.world.level.block.VineBlock.NORTH,
                        blockState.getValue(net.minecraft.world.level.block.VineBlock.NORTH)
                );
                if (
                        blockState.hasProperty(
                                net.minecraft.world.level.block.VineBlock.SOUTH
                        )
                ) state = state.setValue(
                        net.minecraft.world.level.block.VineBlock.SOUTH,
                        blockState.getValue(net.minecraft.world.level.block.VineBlock.SOUTH)
                );
                if (
                        blockState.hasProperty(net.minecraft.world.level.block.VineBlock.EAST)
                ) state = state.setValue(
                        net.minecraft.world.level.block.VineBlock.EAST,
                        blockState.getValue(net.minecraft.world.level.block.VineBlock.EAST)
                );
                if (
                        blockState.hasProperty(net.minecraft.world.level.block.VineBlock.WEST)
                ) state = state.setValue(
                        net.minecraft.world.level.block.VineBlock.WEST,
                        blockState.getValue(net.minecraft.world.level.block.VineBlock.WEST)
                );
            }

            BooleanProperty property = attachDirection == Direction.DOWN
                    ? DOWN
                    : net.minecraft.world.level.block.VineBlock.getPropertyForFace(
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

        BlockPos placePos = context
                .getClickedPos()
                .relative(context.getClickedFace());
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
                net.minecraft.world.level.block.VineBlock.getPropertyForFace(direction);
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
            return net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();
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
            net.minecraft.world.level.LevelReader level,
            BlockPos pos
    ) {
        return hasAnyFace(state);
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        net.minecraft.world.item.Item heldItem = context.getItemInHand().getItem();
        if (heldItem instanceof net.minecraft.world.item.BlockItem) {
            net.minecraft.world.level.block.Block heldBlock =
                    ((net.minecraft.world.item.BlockItem) heldItem).getBlock();

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
                    : net.minecraft.world.level.block.VineBlock.getPropertyForFace(
                    attachDirection
            );

            if (property != null && state.getBlock() == heldBlock) {
                return !state.hasProperty(property) || !state.getValue(property);
            }

            return true;
        }

        return false;
    }

    private boolean hasAnyFace(BlockState state) {
        return (
                state.getValue(net.minecraft.world.level.block.VineBlock.NORTH) ||
                        state.getValue(net.minecraft.world.level.block.VineBlock.EAST) ||
                        state.getValue(net.minecraft.world.level.block.VineBlock.SOUTH) ||
                        state.getValue(net.minecraft.world.level.block.VineBlock.WEST) ||
                        state.getValue(net.minecraft.world.level.block.VineBlock.UP) ||
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
                        net.minecraft.sounds.SoundEvents.STONE_BUTTON_CLICK_OFF,
                        net.minecraft.sounds.SoundSource.BLOCKS,
                        0.3f,
                        1.0f
                );
            } else {
                level.playSound(
                        null,
                        pos,
                        net.minecraft.sounds.SoundEvents.STONE_BUTTON_CLICK_ON,
                        net.minecraft.sounds.SoundSource.BLOCKS,
                        0.3f,
                        1.0f
                );
            }

            return InteractionResult.SUCCESS;
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof GlowLightsBlockEntity glowBE) {
            java.util.Map.Entry<String, String> dyeInfo = getDyeColorAndName(
                    heldItem
            );
            if (dyeInfo != null) {
                if (!glowBE.canAddMoreColors()) {
                    net.minecraft.network.chat.TextComponent message =
                            new net.minecraft.network.chat.TextComponent(
                                    "Glow Lights already has " +
                                            GlowLightsBlockEntity.MAX_DYE_COLORS +
                                            " colors! Break and replace to reset."
                            );
                    message.withStyle(net.minecraft.ChatFormatting.RED);
                    if (player instanceof net.minecraft.server.level.ServerPlayer) {
                        com.kingodogo.buildscape.network.ModMessages.INSTANCE.send(
                                net.minecraftforge.network.PacketDistributor.PLAYER.with(() ->
                                        (net.minecraft.server.level.ServerPlayer) player
                                ),
                                new com.kingodogo.buildscape.network.ActionBarMessagePacket(
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
                            net.minecraft.sounds.SoundEvents.DYE_USE,
                            net.minecraft.sounds.SoundSource.BLOCKS,
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
                    net.minecraft.network.chat.TextComponent message =
                            new net.minecraft.network.chat.TextComponent(
                                    "Glow Lights color added: " + dyeName + progressText
                            );
                    message.withStyle(net.minecraft.ChatFormatting.GREEN);
                    if (player instanceof net.minecraft.server.level.ServerPlayer) {
                        com.kingodogo.buildscape.network.ModMessages.INSTANCE.send(
                                net.minecraftforge.network.PacketDistributor.PLAYER.with(() ->
                                        (net.minecraft.server.level.ServerPlayer) player
                                ),
                                new com.kingodogo.buildscape.network.ActionBarMessagePacket(
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
    private java.util.Map.Entry<String, String> getDyeColorAndName(
            ItemStack stack
    ) {
        if (stack == null || stack.isEmpty()) {
            return null;
        }

        net.minecraft.world.item.Item item = stack.getItem();

        if (item == net.minecraft.world.item.Items.WHITE_DYE) {
            return new java.util.AbstractMap.SimpleEntry<>("#FFFFFF", "White");
        } else if (item == net.minecraft.world.item.Items.ORANGE_DYE) {
            return new java.util.AbstractMap.SimpleEntry<>("#FF9800", "Orange");
        } else if (item == net.minecraft.world.item.Items.MAGENTA_DYE) {
            return new java.util.AbstractMap.SimpleEntry<>("#C74EBD", "Magenta");
        } else if (item == net.minecraft.world.item.Items.LIGHT_BLUE_DYE) {
            return new java.util.AbstractMap.SimpleEntry<>("#3AB3DA", "Light Blue");
        } else if (item == net.minecraft.world.item.Items.YELLOW_DYE) {
            return new java.util.AbstractMap.SimpleEntry<>("#FED83D", "Yellow");
        } else if (item == net.minecraft.world.item.Items.LIME_DYE) {
            return new java.util.AbstractMap.SimpleEntry<>("#80C71F", "Lime");
        } else if (item == net.minecraft.world.item.Items.PINK_DYE) {
            return new java.util.AbstractMap.SimpleEntry<>("#F38BAA", "Pink");
        } else if (item == net.minecraft.world.item.Items.GRAY_DYE) {
            return new java.util.AbstractMap.SimpleEntry<>("#474F52", "Gray");
        } else if (item == net.minecraft.world.item.Items.LIGHT_GRAY_DYE) {
            return new java.util.AbstractMap.SimpleEntry<>("#9D9D97", "Light Gray");
        } else if (item == net.minecraft.world.item.Items.CYAN_DYE) {
            return new java.util.AbstractMap.SimpleEntry<>("#169C9C", "Cyan");
        } else if (item == net.minecraft.world.item.Items.PURPLE_DYE) {
            return new java.util.AbstractMap.SimpleEntry<>("#8932B8", "Purple");
        } else if (item == net.minecraft.world.item.Items.BLUE_DYE) {
            return new java.util.AbstractMap.SimpleEntry<>("#3C44AA", "Blue");
        } else if (item == net.minecraft.world.item.Items.BROWN_DYE) {
            return new java.util.AbstractMap.SimpleEntry<>("#835432", "Brown");
        } else if (item == net.minecraft.world.item.Items.GREEN_DYE) {
            return new java.util.AbstractMap.SimpleEntry<>("#5E7C16", "Green");
        } else if (item == net.minecraft.world.item.Items.RED_DYE) {
            return new java.util.AbstractMap.SimpleEntry<>("#B02E26", "Red");
        } else if (item == net.minecraft.world.item.Items.BLACK_DYE) {
            return new java.util.AbstractMap.SimpleEntry<>("#1D1D21", "Black");
        }

        return null;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GlowLightsBlockEntity(pos, state);
    }
}
