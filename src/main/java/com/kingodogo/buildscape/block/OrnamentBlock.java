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
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
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

public class OrnamentBlock
        extends AbstractGlassBlock
        implements SimpleWaterloggedBlock {

    public static final BooleanProperty WATERLOGGED =
            BlockStateProperties.WATERLOGGED;
    public static final EnumProperty<AttachmentType> ATTACHMENT =
            EnumProperty.create("attachment", AttachmentType.class);
    public static final DirectionProperty FACING =
            BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<StringColor> STRING_COLOR =
            EnumProperty.create("string_color", StringColor.class);
    public static final net.minecraft.world.level.block.state.properties.BooleanProperty LIT =
            net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT;

    private static final VoxelShape SHAPE_FLOOR = Block.box(5, 0, 5, 11, 6, 11);

    private static final VoxelShape SHAPE_CEILING = Block.box(
            5,
            5,
            5,
            11,
            11,
            11
    );

    private static final VoxelShape SHAPE_WALL_NORTH = Block.box(
            5,
            4,
            9,
            11,
            10,
            15
    );
    private static final VoxelShape SHAPE_WALL_SOUTH = Block.box(
            5,
            4,
            1,
            11,
            10,
            7
    );
    private static final VoxelShape SHAPE_WALL_EAST = Block.box(
            1,
            4,
            5,
            7,
            10,
            11
    );
    private static final VoxelShape SHAPE_WALL_WEST = Block.box(
            9,
            4,
            5,
            15,
            10,
            11
    );

    public enum AttachmentType implements StringRepresentable {
        FLOOR("floor"),
        CEILING("ceiling"),
        WALL("wall");

        private final String name;

        AttachmentType(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }

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

    public OrnamentBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(ATTACHMENT, AttachmentType.FLOOR)
                        .setValue(FACING, Direction.NORTH)
                        .setValue(STRING_COLOR, StringColor.WHITE)
                        .setValue(LIT, true)
                        .setValue(WATERLOGGED, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(
            StateDefinition.Builder<Block, BlockState> builder
    ) {
        builder.add(ATTACHMENT, FACING, STRING_COLOR, LIT, WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction clickedFace = context.getClickedFace();
        BlockPos clickedPos = context.getClickedPos();
        LevelReader level = context.getLevel();

        Direction playerFacing = context.getHorizontalDirection().getOpposite();

        BlockPos placePos;
        AttachmentType attachment;
        Direction facing;

        if (clickedFace == Direction.UP) {
            placePos = clickedPos.relative(clickedFace);
            attachment = AttachmentType.FLOOR;
            facing = playerFacing;
        } else if (clickedFace == Direction.DOWN) {
            placePos = clickedPos.relative(clickedFace);
            attachment = AttachmentType.CEILING;
            facing = playerFacing;
        } else {
            placePos = clickedPos.relative(clickedFace);
            attachment = AttachmentType.WALL;
            facing = playerFacing;
        }

        BlockState placeState = level.getBlockState(placePos);
        if (
                placeState.isAir() ||
                        placeState.canBeReplaced(context) ||
                        placeState.is(this)
        ) {
            FluidState fluidstate = context.getLevel().getFluidState(placePos);
            return this.defaultBlockState()
                    .setValue(ATTACHMENT, attachment)
                    .setValue(FACING, facing)
                    .setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
        }

        return null;
    }

    private boolean canAttachTo(
            LevelReader level,
            BlockPos pos,
            Direction direction
    ) {
        BlockState state = level.getBlockState(pos);
        if (state.is(this)) {
            return true;
        }
        if (
                state.getBlock() instanceof FenceBlock ||
                        state.getBlock() instanceof LeafHedgeBlock
        ) {
            return true;
        }
        if (
                state.getMaterial().isSolid() &&
                        !state.isAir() &&
                        !state.is(Blocks.BARRIER)
        ) {
            return true;
        }
        return state.isFaceSturdy(level, pos, direction);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        AttachmentType attachment = state.getValue(ATTACHMENT);

        switch (attachment) {
            case FLOOR:
                return canAttachTo(level, pos.below(), Direction.UP);
            case CEILING:
                return canAttachTo(level, pos.above(), Direction.DOWN);
            case WALL:
                Direction facing = state.getValue(FACING);
                BlockPos wallPos = pos.relative(facing.getOpposite());
                Direction wallFaceDirection = facing;
                return canAttachTo(level, wallPos, wallFaceDirection);
            default:
                return false;
        }
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

        if (!canSurvive(state, level, pos)) {
            if (level instanceof net.minecraft.server.level.ServerLevel) {
                level.scheduleTick(
                        pos,
                        this,
                        1
                );
            }
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
        if (state.getValue(ATTACHMENT) == AttachmentType.WALL) {
            return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
        }
        return state;
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        if (state.getValue(ATTACHMENT) == AttachmentType.WALL) {
            return state.rotate(mirror.getRotation(state.getValue(FACING)));
        }
        return state;
    }

    @Override
    public VoxelShape getShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        AttachmentType attachment = state.getValue(ATTACHMENT);

        if (attachment == AttachmentType.CEILING) {
            return SHAPE_CEILING;
        } else if (attachment == AttachmentType.WALL) {
            Direction facing = state.getValue(FACING);
            switch (facing) {
                case NORTH:
                    return SHAPE_WALL_NORTH;
                case SOUTH:
                    return SHAPE_WALL_SOUTH;
                case EAST:
                    return SHAPE_WALL_EAST;
                case WEST:
                    return SHAPE_WALL_WEST;
                default:
                    return SHAPE_FLOOR;
            }
        } else {
            return SHAPE_FLOOR;
        }
    }

    @Override
    public VoxelShape getCollisionShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        return getShape(state, level, pos, context);
    }

    @Override
    public int getLightBlock(
            BlockState state,
            BlockGetter level,
            BlockPos pos
    ) {
        if (
                state.getBlock() ==
                        com.kingodogo.buildscape.block.ModBlocks.TINTED_GLASS_ORNAMENT.get()
        ) {
            return 15;
        }
        return super.getLightBlock(state, level, pos);
    }

    @Override
    public float[] getBeaconColorMultiplier(
            BlockState state,
            LevelReader level,
            BlockPos pos,
            BlockPos beaconPos
    ) {
        if (
                state.getBlock() ==
                        com.kingodogo.buildscape.block.ModBlocks.TINTED_GLASS_ORNAMENT.get()
        ) {
            return null; // Tinted glass blocks the beam via opacity (getLightBlock)
        }

        // For other ornaments, use their map color to tint the beacon beam
        int color = state.getMapColor(level, pos).col;
        return new float[]{
                ((color >> 16) & 0xFF) / 255.0f,
                ((color >> 8) & 0xFF) / 255.0f,
                (color & 0xFF) / 255.0f
        };
    }

    @Override
    public int getLightEmission(
            BlockState state,
            BlockGetter level,
            BlockPos pos
    ) {
        if (!state.getValue(LIT)) {
            return 0;
        }

        if (
                state.getBlock() ==
                        com.kingodogo.buildscape.block.ModBlocks.TINTED_GLASS_ORNAMENT.get()
        ) {
            return 4;
        }

        return 15;
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
        BlockState newState = state.setValue(LIT, !currentLit);
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
