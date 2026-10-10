package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.platform.Services;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import com.kingodogo.buildscape.util.BeaconScanContext;
public class OrnamentBlock extends Block implements SimpleWaterloggedBlock, ICommonInteractable {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final EnumProperty<AttachmentType> ATTACHMENT = EnumProperty.create("attachment", AttachmentType.class);
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<StringColor> STRING_COLOR = EnumProperty.create("string_color", StringColor.class);
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    private final boolean tinted;

    private static final VoxelShape SHAPE_FLOOR = Block.box(5, 0, 5, 11, 6, 11);
    private static final VoxelShape SHAPE_CEILING = Block.box(5, 5, 5, 11, 11, 11);
    private static final VoxelShape SHAPE_WALL_NORTH = Block.box(5, 4, 9, 11, 10, 15);
    private static final VoxelShape SHAPE_WALL_SOUTH = Block.box(5, 4, 1, 11, 10, 7);
    private static final VoxelShape SHAPE_WALL_EAST = Block.box(1, 4, 5, 7, 10, 11);
    private static final VoxelShape SHAPE_WALL_WEST = Block.box(9, 4, 5, 15, 10, 11);

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
        WHITE("white", 0xE8FEFD, "White"), ORANGE("orange", 0xFF5C00, "Orange"),
        MAGENTA("magenta", 0xFF00FF, "Magenta"), LIGHT_BLUE("light_blue", 0x3CDFFF, "Light Blue"),
        YELLOW("yellow", 0xFFFF00, "Yellow"), LIME("lime", 0xBFFE00, "Lime"),
        PINK("pink", 0xF686B7, "Pink"), GRAY("gray", 0x232526, "Gray"),
        LIGHT_GRAY("light_gray", 0xB1B8C5, "Light Gray"), CYAN("cyan", 0x00FFFF, "Cyan"),
        PURPLE("purple", 0xAB87FF, "Purple"), BLUE("blue", 0x1919EA, "Blue"),
        BROWN("brown", 0x411900, "Brown"), GREEN("green", 0x39FF14, "Green"),
        RED("red", 0xFF0000, "Red"), BLACK("black", 0x07010C, "Black");

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
        public int getColor() { return color; }
        public String getDisplayName() { return displayName; }
    }

    public OrnamentBlock(BlockBehaviour.Properties properties) {
        this(properties, false);
    }

    public OrnamentBlock(BlockBehaviour.Properties properties, boolean tinted) {
        super(properties);
        this.tinted = tinted;
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(ATTACHMENT, AttachmentType.FLOOR)
                .setValue(FACING, Direction.NORTH)
                .setValue(STRING_COLOR, StringColor.WHITE)
                .setValue(LIT, true)
                .setValue(WATERLOGGED, false));
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 1.0F;
    }

    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ATTACHMENT, FACING, STRING_COLOR, LIT, WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction clickedFace = context.getClickedFace();
        BlockPos clickedPos = context.getClickedPos();
        LevelReader level = context.getLevel();
        Direction playerFacing = context.getHorizontalDirection().getOpposite();

        AttachmentType attachment;
        Direction facing;

        if (clickedFace == Direction.UP) {
            attachment = AttachmentType.FLOOR;
            facing = playerFacing;
        } else if (clickedFace == Direction.DOWN) {
            attachment = AttachmentType.CEILING;
            facing = playerFacing;
        } else {
            attachment = AttachmentType.WALL;
            facing = playerFacing;
        }

        BlockPos placePos = clickedPos.relative(clickedFace);
        BlockState placeState = level.getBlockState(placePos);
        if (!placeState.isAir() && !placeState.canBeReplaced(context) && !placeState.is(this)) return null;
        FluidState fluidState = level.getFluidState(placePos);
        return this.defaultBlockState()
                .setValue(ATTACHMENT, attachment)
                .setValue(FACING, facing)
                .setValue(STRING_COLOR, StringColor.WHITE)
                .setValue(LIT, true)
                .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    private boolean canAttachTo(LevelReader level, BlockPos pos, Direction direction) {
        BlockState support = level.getBlockState(pos);
        com.kingodogo.buildscape.util.CommonId supportId = com.kingodogo.buildscape.platform.Services.PLATFORM.getBlockId(support.getBlock());
        boolean leafHedge = supportId != null && supportId.getPath().endsWith("_hedge");
        if (support.is(this) || support.getBlock() instanceof FenceBlock || leafHedge) return true;
        if (!support.isAir() && !support.is(Blocks.BARRIER) && support.isCollisionShapeFullBlock(level, pos)) return true;
        return support.isFaceSturdy(level, pos, direction);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return switch (state.getValue(ATTACHMENT)) {
            case FLOOR -> canAttachTo(level, pos.below(), Direction.UP);
            case CEILING -> canAttachTo(level, pos.above(), Direction.DOWN);
            case WALL -> {
                Direction facing = state.getValue(FACING);
                yield canAttachTo(level, pos.relative(facing.getOpposite()), facing);
            }
        };
    }

    public int getBeaconColor(BlockState state, LevelReader level, BlockPos pos, BlockPos beaconPos) {
        if (tinted) {
            BeaconScanContext.markBlocking(level, pos, beaconPos);
            return 0xFFFFFF;
        }
        return state.getMapColor(level, pos).col;
    }

    public boolean isTinted() { return tinted; }

    @Override
    public boolean skipRendering(BlockState state, BlockState adjacentState, Direction side) {
        return adjacentState.is(this) || super.skipRendering(state, adjacentState, side);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        AttachmentType attachment = state.getValue(ATTACHMENT);
        if (attachment == AttachmentType.FLOOR) {
            return SHAPE_FLOOR;
        }
        if (attachment == AttachmentType.CEILING) {
            return SHAPE_CEILING;
        }

        Direction facing = state.getValue(FACING);
        return switch (facing) {
            case SOUTH -> SHAPE_WALL_SOUTH;
            case EAST -> SHAPE_WALL_EAST;
            case WEST -> SHAPE_WALL_WEST;
            default -> SHAPE_WALL_NORTH;
        };
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
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
    public InteractionResult onInteract(Level level, BlockPos pos, BlockState state, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        ItemStack heldItem = player.getItemInHand(hand);
        if (!heldItem.isEmpty()) {
            DyeColor dyeColor = com.kingodogo.buildscape.platform.Services.PLATFORM.getDyeColor(heldItem);
            if (dyeColor != null) {
                StringColor color = StringColor.valueOf(dyeColor.getName().toUpperCase());
                level.setBlock(pos, state.setValue(STRING_COLOR, color), 3);
                if (!player.getAbilities().instabuild) {
                    heldItem.shrink(1);
                }
                Services.PLATFORM.playDyeUse(level, pos);
                Services.PLATFORM.sendActionBarMessage(player,
                        com.kingodogo.buildscape.util.ComponentHelper.literal("String colored: " + color.getDisplayName()).withStyle(ChatFormatting.GREEN));
                return InteractionResult.SUCCESS;
            }
        }

        return toggleLight(state, level, pos);
    }

    private InteractionResult toggleLight(BlockState state, Level level, BlockPos pos) {
        boolean currentLit = state.getValue(LIT);
        level.setBlock(pos, state.setValue(LIT, !currentLit), 3);
        Services.PLATFORM.playStoneButtonClick(level, pos, !currentLit);
        return InteractionResult.SUCCESS;
    }
}
