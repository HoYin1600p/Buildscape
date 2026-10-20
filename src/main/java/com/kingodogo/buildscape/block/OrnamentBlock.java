package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.core.DyeColors;
import com.kingodogo.buildscape.core.WaterloggedGlassBlock;
import com.kingodogo.buildscape.network.ActionBarMessagePacket;
import com.kingodogo.buildscape.network.ModMessages;
import com.kingodogo.buildscape.util.BeaconScanContext;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
import net.minecraftforge.network.PacketDistributor;

import java.util.Random;

public class OrnamentBlock
        extends
            WaterloggedGlassBlock
        implements
            SimpleWaterloggedBlock {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final EnumProperty<AttachmentType> ATTACHMENT = EnumProperty.create("attachment",
            AttachmentType.class);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<StringColor> STRING_COLOR = EnumProperty.create("string_color", StringColor.class);
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    private final boolean isTinted;

    private static final VoxelShape SHAPE_FLOOR = Block.box(5, 0, 5, 11, 6, 11);

    private static final VoxelShape SHAPE_CEILING = Block.box(
            5,
            5,
            5,
            11,
            11,
            11);

    private static final VoxelShape SHAPE_WALL_NORTH = Block.box(
            5,
            4,
            9,
            11,
            10,
            15);
    private static final VoxelShape SHAPE_WALL_SOUTH = Block.box(
            5,
            4,
            1,
            11,
            10,
            7);
    private static final VoxelShape SHAPE_WALL_EAST = Block.box(
            1,
            4,
            5,
            7,
            10,
            11);
    private static final VoxelShape SHAPE_WALL_WEST = Block.box(
            9,
            4,
            5,
            15,
            10,
            11);

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
        WHITE(DyeColors.WHITE),
        ORANGE(DyeColors.ORANGE),
        MAGENTA(DyeColors.MAGENTA),
        LIGHT_BLUE(DyeColors.LIGHT_BLUE),
        YELLOW(DyeColors.YELLOW),
        LIME(DyeColors.LIME),
        PINK(DyeColors.PINK),
        GRAY(DyeColors.GRAY),
        LIGHT_GRAY(DyeColors.LIGHT_GRAY),
        CYAN(DyeColors.CYAN),
        PURPLE(DyeColors.PURPLE),
        BLUE(DyeColors.BLUE),
        BROWN(DyeColors.BROWN),
        GREEN(DyeColors.GREEN),
        RED(DyeColors.RED),
        BLACK(DyeColors.BLACK);

        private final DyeColors dye;

        StringColor(DyeColors dye) {
            this.dye = dye;
        }

        @Override
        public String getSerializedName() {
            return dye.serializedName();
        }

        public int getColor() {
            return dye.rgb();
        }

        public String getDisplayName() {
            return dye.displayName();
        }
    }

    public OrnamentBlock(BlockBehaviour.Properties properties) {
        this(properties, false);
    }

    public OrnamentBlock(BlockBehaviour.Properties properties, boolean isTinted) {
        super(properties);
        this.isTinted = isTinted;
        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(ATTACHMENT, AttachmentType.FLOOR)
                        .setValue(FACING, Direction.NORTH)
                        .setValue(STRING_COLOR, StringColor.WHITE)
                        .setValue(LIT, true)
                        .setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(
            StateDefinition.Builder<Block, BlockState> builder) {
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
            placePos = clickedPos;
            attachment = AttachmentType.FLOOR;
            facing = playerFacing;
        } else if (clickedFace == Direction.DOWN) {
            placePos = clickedPos;
            attachment = AttachmentType.CEILING;
            facing = playerFacing;
        } else {
            placePos = clickedPos;
            attachment = AttachmentType.WALL;
            facing = playerFacing;
        }

        BlockState placeState = level.getBlockState(placePos);
        if (placeState.isAir() ||
                placeState.canBeReplaced(context) ||
                placeState.is(this)) {
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
            Direction direction) {
        BlockState state = level.getBlockState(pos);
        if (state.is(this)) {
            return true;
        }
        if (state.getBlock() instanceof FenceBlock ||
                state.getBlock() instanceof LeafHedgeBlock) {
            return true;
        }
        if (state.getMaterial().isSolid() &&
                !state.isAir() &&
                !state.is(Blocks.BARRIER)) {
            return true;
        }
        return state.isFaceSturdy(level, pos, direction);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        AttachmentType attachment = state.getValue(ATTACHMENT);

        switch (attachment) {
            case FLOOR -> {
                return canAttachTo(level, pos.below(), Direction.UP);
            }
            case CEILING -> {
                return canAttachTo(level, pos.above(), Direction.DOWN);
            }
            case WALL -> {
                Direction facing = state.getValue(FACING);
                BlockPos wallPos = pos.relative(facing.getOpposite());
                Direction wallFaceDirection = facing;
                return canAttachTo(level, wallPos, wallFaceDirection);
            }
            default -> {
                return false;
            }
        }
    }

    @Override
    public BlockState updateShape(
            BlockState state,
            Direction direction,
            BlockState neighborState,
            LevelAccessor level,
            BlockPos pos,
            BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        if (!canSurvive(state, level, pos)) {
            if (level instanceof ServerLevel) {
                level.scheduleTick(
                        pos,
                        this,
                        1);
            }
        }
        return super.updateShape(
                state,
                direction,
                neighborState,
                level,
                pos,
                neighborPos);
    }

    @Override
    public void tick(BlockState state, ServerLevel level,
            BlockPos pos, Random random) {
        if (!canSurvive(state, level, pos)) {
            level.destroyBlock(pos, true);
        }
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
            CollisionContext context) {
        AttachmentType attachment = state.getValue(ATTACHMENT);

        if (attachment == AttachmentType.CEILING) {
            return SHAPE_CEILING;
        } else if (attachment == AttachmentType.WALL) {
            Direction facing = state.getValue(FACING);
            return switch (facing) {
                case NORTH -> SHAPE_WALL_NORTH;
                case SOUTH -> SHAPE_WALL_SOUTH;
                case EAST -> SHAPE_WALL_EAST;
                case WEST -> SHAPE_WALL_WEST;
                default -> SHAPE_FLOOR;
            };
        } else {
            return SHAPE_FLOOR;
        }
    }

    @Override
    public VoxelShape getCollisionShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context) {
        return getShape(state, level, pos, context);
    }

    @Override
    public float[] getBeaconColorMultiplier(
            BlockState state,
            LevelReader level,
            BlockPos pos,
            BlockPos beaconPos) {
        if (this.isTinted) {
            BeaconScanContext.markBlocking(level, pos, beaconPos);
            return new float[]{1.0F, 1.0F, 1.0F};
        }

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
            BlockPos pos) {
        if (!state.getValue(LIT)) {
            return 0;
        }

        if (this.isTinted) {
            return 4;
        }

        return 15;
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        if (this.isTinted) {
            return 0;
        }
        return super.getLightBlock(state, level, pos);
    }

    @Override
    public boolean skipRendering(
            BlockState state,
            BlockState adjacentBlockState,
            Direction side) {
        return (adjacentBlockState.is(this) ||
                super.skipRendering(state, adjacentBlockState, side));
    }

    @Override
    public InteractionResult use(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hit) {
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
                        SoundEvents.DYE_USE,
                        SoundSource.BLOCKS,
                        1.0f,
                        1.0f);

                TextComponent message = new TextComponent(
                        "String colored: " + dyeColor.getDisplayName());
                message.withStyle(ChatFormatting.GREEN);
                if (player instanceof ServerPlayer) {
                    ModMessages.INSTANCE.send(
                            PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
                            new ActionBarMessagePacket(message));
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
                        ? SoundEvents.STONE_BUTTON_CLICK_OFF
                        : SoundEvents.STONE_BUTTON_CLICK_ON,
                SoundSource.BLOCKS,
                0.3f,
                1.0f);

        return InteractionResult.SUCCESS;
    }

    private StringColor getDyeColor(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return null;
        }

        Item item = stack.getItem();

        if (item == Items.WHITE_DYE) {
            return StringColor.WHITE;
        } else if (item == Items.ORANGE_DYE) {
            return StringColor.ORANGE;
        } else if (item == Items.MAGENTA_DYE) {
            return StringColor.MAGENTA;
        } else if (item == Items.LIGHT_BLUE_DYE) {
            return StringColor.LIGHT_BLUE;
        } else if (item == Items.YELLOW_DYE) {
            return StringColor.YELLOW;
        } else if (item == Items.LIME_DYE) {
            return StringColor.LIME;
        } else if (item == Items.PINK_DYE) {
            return StringColor.PINK;
        } else if (item == Items.GRAY_DYE) {
            return StringColor.GRAY;
        } else if (item == Items.LIGHT_GRAY_DYE) {
            return StringColor.LIGHT_GRAY;
        } else if (item == Items.CYAN_DYE) {
            return StringColor.CYAN;
        } else if (item == Items.PURPLE_DYE) {
            return StringColor.PURPLE;
        } else if (item == Items.BLUE_DYE) {
            return StringColor.BLUE;
        } else if (item == Items.BROWN_DYE) {
            return StringColor.BROWN;
        } else if (item == Items.GREEN_DYE) {
            return StringColor.GREEN;
        } else if (item == Items.RED_DYE) {
            return StringColor.RED;
        } else if (item == Items.BLACK_DYE) {
            return StringColor.BLACK;
        }

        return null;
    }
}
