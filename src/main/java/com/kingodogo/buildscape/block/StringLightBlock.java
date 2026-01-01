package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.AbstractGlassBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class StringLightBlock
        extends AbstractGlassBlock
        implements SimpleWaterloggedBlock {

    public static final BooleanProperty WATERLOGGED =
            BlockStateProperties.WATERLOGGED;
    public static final EnumProperty<StringColor> STRING_COLOR =
            EnumProperty.create("string_color", StringColor.class);
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final DirectionProperty FACING =
            BlockStateProperties.HORIZONTAL_FACING;

    private static final VoxelShape SHAPE_NORTH = Block.box(0, 12, 0, 16, 15, 2);
    private static final VoxelShape SHAPE_SOUTH = Block.box(
            0,
            12,
            14,
            16,
            15,
            16
    );
    private static final VoxelShape SHAPE_EAST = Block.box(14, 12, 0, 16, 15, 16);
    private static final VoxelShape SHAPE_WEST = Block.box(0, 12, 0, 2, 15, 16);

    public enum StringColor implements StringRepresentable {
        WHITE("white", 0xFFFFFF, "White"),
        ORANGE("orange", 0xFF9800, "Orange"),
        MAGENTA("magenta", 0xC74EBD, "Magenta"),
        LIGHT_BLUE("light_blue", 0x3AB3DA, "Light Blue"),
        YELLOW("yellow", 0xFED83D, "Yellow"),
        LIME("lime", 0x80C71F, "Lime"),
        PINK("pink", 0xF38BAA, "Pink"),
        GRAY("gray", 0x474F52, "Gray"),
        LIGHT_GRAY("light_gray", 0x9D9D97, "Light Gray"),
        CYAN("cyan", 0x169C9C, "Cyan"),
        PURPLE("purple", 0x8932B8, "Purple"),
        BLUE("blue", 0x3C44AA, "Blue"),
        BROWN("brown", 0x835432, "Brown"),
        GREEN("green", 0x5E7C16, "Green"),
        RED("red", 0xB02E26, "Red"),
        BLACK("black", 0x1D1D21, "Black");

        private final String name;
        private final int color;
        private final String displayName;

        StringColor(String name, int color, String displayName) {
            this.name = name;
            this.color = color;
            this.displayName = displayName;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        public int getColor() {
            return color;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public StringLightBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(STRING_COLOR, StringColor.BLACK)
                        .setValue(LIT, false)
                        .setValue(FACING, Direction.NORTH)
                        .setValue(WATERLOGGED, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(
            StateDefinition.Builder<Block, BlockState> builder
    ) {
        builder.add(STRING_COLOR, LIT, FACING, WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidstate = context
                .getLevel()
                .getFluidState(context.getClickedPos());
        Direction clickedFace = context.getClickedFace();

        if (clickedFace.getAxis().isHorizontal()) {
            return this.defaultBlockState()
                    .setValue(STRING_COLOR, StringColor.BLACK)
                    .setValue(LIT, false)
                    .setValue(FACING, clickedFace.getOpposite())
                    .setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
        }

        return this.defaultBlockState()
                .setValue(STRING_COLOR, StringColor.BLACK)
                .setValue(LIT, false)
                .setValue(FACING, context.getHorizontalDirection())
                .setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
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
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
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
    public boolean useShapeForLightOcclusion(BlockState state) {
        return false;
    }

    @Override
    public boolean skipRendering(
            BlockState state,
            BlockState adjacentBlockState,
            net.minecraft.core.Direction side
    ) {
        return (
                adjacentBlockState.is(this) ||
                        super.skipRendering(state, adjacentBlockState, side)
        );
    }

    @Override
    public VoxelShape getShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        Direction facing = state.getValue(FACING);
        return switch (facing) {
            case NORTH -> SHAPE_NORTH;
            case SOUTH -> SHAPE_SOUTH;
            case EAST -> SHAPE_EAST;
            case WEST -> SHAPE_WEST;
            default -> SHAPE_NORTH;
        };
    }

    @Override
    public VoxelShape getCollisionShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        Direction facing = state.getValue(FACING);
        return switch (facing) {
            case NORTH -> SHAPE_NORTH;
            case SOUTH -> SHAPE_SOUTH;
            case EAST -> SHAPE_EAST;
            case WEST -> SHAPE_WEST;
            default -> SHAPE_NORTH;
        };
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
        if (!heldItem.isEmpty()) {
            StringColor dyeColor = getDyeColor(heldItem);
            if (dyeColor != null) {
                BlockState newState = state.setValue(STRING_COLOR, dyeColor);
                level.setBlock(pos, newState, 3);
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
                net.minecraft.network.chat.TextComponent message =
                        new net.minecraft.network.chat.TextComponent(
                                "String colored: " + dyeColor.getDisplayName()
                        );
                message.withStyle(net.minecraft.ChatFormatting.GREEN);
                if (player instanceof net.minecraft.server.level.ServerPlayer) {
                    com.kingodogo.buildscape.network.ModMessages.INSTANCE.send(
                            net.minecraftforge.network.PacketDistributor.PLAYER.with(() ->
                                    (net.minecraft.server.level.ServerPlayer) player
                            ),
                            new com.kingodogo.buildscape.network.ActionBarMessagePacket(message)
                    );
                }
                return InteractionResult.SUCCESS;
            }
        }
        boolean currentLit = state.getValue(LIT);
        boolean newLit = !currentLit;
        BlockState newState = state.setValue(LIT, newLit);
        level.setBlock(pos, newState, 3);
        level.playSound(
                null,
                pos,
                currentLit
                        ? net.minecraft.sounds.SoundEvents.STONE_BUTTON_CLICK_OFF
                        : net.minecraft.sounds.SoundEvents.STONE_BUTTON_CLICK_ON,
                net.minecraft.sounds.SoundSource.BLOCKS,
                0.3f,
                1.0f
        );
        net.minecraft.network.chat.TextComponent message =
                new net.minecraft.network.chat.TextComponent(
                        newLit ? "Turned On" : "Turned Off"
                );
        message.withStyle(
                newLit
                        ? net.minecraft.ChatFormatting.GREEN
                        : net.minecraft.ChatFormatting.RED
        );
        player.displayClientMessage(message, true);
        return InteractionResult.SUCCESS;
    }

    private StringColor getDyeColor(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return null;
        }
        net.minecraft.world.item.Item item = stack.getItem();
        if (item == net.minecraft.world.item.Items.WHITE_DYE) {
            return StringColor.WHITE;
        } else if (item == net.minecraft.world.item.Items.ORANGE_DYE) {
            return StringColor.ORANGE;
        } else if (item == net.minecraft.world.item.Items.MAGENTA_DYE) {
            return StringColor.MAGENTA;
        } else if (item == net.minecraft.world.item.Items.LIGHT_BLUE_DYE) {
            return StringColor.LIGHT_BLUE;
        } else if (item == net.minecraft.world.item.Items.YELLOW_DYE) {
            return StringColor.YELLOW;
        } else if (item == net.minecraft.world.item.Items.LIME_DYE) {
            return StringColor.LIME;
        } else if (item == net.minecraft.world.item.Items.PINK_DYE) {
            return StringColor.PINK;
        } else if (item == net.minecraft.world.item.Items.GRAY_DYE) {
            return StringColor.GRAY;
        } else if (item == net.minecraft.world.item.Items.LIGHT_GRAY_DYE) {
            return StringColor.LIGHT_GRAY;
        } else if (item == net.minecraft.world.item.Items.CYAN_DYE) {
            return StringColor.CYAN;
        } else if (item == net.minecraft.world.item.Items.PURPLE_DYE) {
            return StringColor.PURPLE;
        } else if (item == net.minecraft.world.item.Items.BLUE_DYE) {
            return StringColor.BLUE;
        } else if (item == net.minecraft.world.item.Items.BROWN_DYE) {
            return StringColor.BROWN;
        } else if (item == net.minecraft.world.item.Items.GREEN_DYE) {
            return StringColor.GREEN;
        } else if (item == net.minecraft.world.item.Items.RED_DYE) {
            return StringColor.RED;
        } else if (item == net.minecraft.world.item.Items.BLACK_DYE) {
            return StringColor.BLACK;
        }
        return null;
    }
}
