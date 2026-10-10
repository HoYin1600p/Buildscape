package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.config.PillarParticleConfig;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import com.kingodogo.buildscape.util.ComponentHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;
public class AshenKingPillarBlock extends PillarBlock {

    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final VoxelShape[][] SHAPES = new VoxelShape[4][4];

    static {
        for (Direction facing : Direction.Plane.HORIZONTAL) {
            int fIdx = facing.get2DDataValue();
            for (PillarPart part : PillarPart.values()) {
                int pIdx = part.ordinal();
                SHAPES[fIdx][pIdx] = createShape(facing, part);
            }
        }
    }

    private static VoxelShape createShape(Direction facing, PillarPart part) {
        VoxelShape result = Shapes.empty();
        result = Shapes.or(result, rotateBox(0, 0, 3, 16, 5.5, 13, facing));
        result = Shapes.or(result, rotateBox(4, 5, 4.75, 12, 6.5, 11.25, facing));
        result = Shapes.or(result, rotateBox(5, 6.5, 5.75, 11, 7.5, 10.25, facing));
        result = Shapes.or(result, rotateBox(4, 7.5, 4.75, 12, 8.5, 11.25, facing));

        if (part == PillarPart.SINGLE || part == PillarPart.BOTTOM) {
            result = Shapes.or(result, rotateBox(2.1, 1.5, 2.1, 5.1, 4.5, 3.1, facing));
            result = Shapes.or(result, rotateBox(6.1, 1.5, 2.1, 9.1, 4.5, 3.1, facing));
            result = Shapes.or(result, rotateBox(10.1, 1.5, 2.1, 13.1, 4.5, 3.1, facing));
        }

        if (part == PillarPart.SINGLE || part == PillarPart.TOP) {
            result = Shapes.or(result, rotateBox(9.6, 1.5, 12.95, 12.6, 4.5, 13.95, facing));
        }

        return result.optimize();
    }

    private static VoxelShape rotateBox(double x1, double y1, double z1, double x2, double y2, double z2, Direction facing) {
        if (facing == Direction.EAST) {
            return Block.box(16 - z2, y1, x1, 16 - z1, y2, x2);
        } else if (facing == Direction.SOUTH) {
            return Block.box(16 - x2, y1, 16 - z2, 16 - x1, y2, 16 - z1);
        } else if (facing == Direction.WEST) {
            return Block.box(z1, y1, 16 - x2, z2, y2, 16 - x1);
        } else {
            return Block.box(x1, y1, z1, x2, y2, z2);
        }
    }

    public AshenKingPillarBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(PART, PillarPart.SINGLE)
                .setValue(WATERLOGGED, false)
                .setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PART, WATERLOGGED, FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction facing = context.getHorizontalDirection().getOpposite();
        FluidState fluidState = level.getFluidState(pos);

        return this.defaultBlockState()
                .setValue(FACING, facing)
                .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER)
                .setValue(PART, PillarPart.SINGLE);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (!level.isClientSide()) {
            BlockPos above = pos.above();
            BlockPos below = pos.below();
            if (level.getBlockState(above).getBlock() instanceof PillarBlock) {
                level.sendBlockUpdated(above, level.getBlockState(above), level.getBlockState(above), 3);
            }
            if (level.getBlockState(below).getBlock() instanceof PillarBlock) {
                level.sendBlockUpdated(below, level.getBlockState(below), level.getBlockState(below), 3);
            }
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        PillarPart part = state.getValue(PART);
        int fIdx = facing.get2DDataValue();
        int pIdx = part.ordinal();
        if (fIdx >= 0 && fIdx < 4 && pIdx >= 0 && pIdx < 4) {
            return SHAPES[fIdx][pIdx];
        }
        return super.getShape(state, level, pos, context);
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
    public InteractionResult onInteract(Level level, BlockPos pos, BlockState state, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack heldItem = player.getItemInHand(hand);
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof PillarBlockEntity pillarBE)) {
            return InteractionResult.PASS;
        }

        if (!heldItem.isEmpty()) {
            Map.Entry<String, String> dyeInfo = getDyeColorAndName(heldItem);
            if (dyeInfo != null) {
                if (pillarBE.hasDisplayItem()) {
                    String dyeColor = dyeInfo.getKey();
                    String dyeName = dyeInfo.getValue();

                    if (!pillarBE.canAddMoreColors()) {
                        Services.PLATFORM.sendActionBarMessage(
                                player,
                                ComponentHelper.literal("Pillar already has " + PillarBlockEntity.MAX_DYE_COLORS + " colors! Break and replace to reset.")
                                        .withStyle(ChatFormatting.RED)
                        );
                        return InteractionResult.CONSUME;
                    }

                    boolean added = pillarBE.addParticleColor(dyeColor);
                    if (!added) {
                        return InteractionResult.PASS;
                    }

                    if (!player.getAbilities().instabuild) {
                        heldItem.shrink(1);
                    }

                    Services.PLATFORM.playDyeUse(level, pos);

                    String pillarId = pillarBE.getPillarId();
                    int colorCount = pillarBE.getDyeColorCount();
                    String progressText = " (" + colorCount + "/" + PillarBlockEntity.MAX_DYE_COLORS + ")";
                    Component message;
                    if (pillarId != null) {
                        message = ComponentHelper.literal("[" + pillarId + "] Dyed " + dyeName + progressText);
                    } else {
                        message = ComponentHelper.literal("Pillar Dyed " + dyeName + progressText);
                    }

                    Services.PLATFORM.sendActionBarMessage(player, message);
                    return InteractionResult.SUCCESS;
                }
            }
        }

        if (player.isShiftKeyDown()) {
            ItemStack displayedItem = pillarBE.getDisplayedItem();
            boolean isSpawnEgg = !displayedItem.isEmpty() && displayedItem.getItem() instanceof SpawnEggItem;

            if (isSpawnEgg) {
                pillarBE.rotateFacing();
                float facingYaw = pillarBE.getFacingYaw();
                String direction = getAshenKingDirectionName(facingYaw);
                Services.PLATFORM.sendActionBarMessage(
                        player,
                        ComponentHelper.literal("Mob facing: " + direction).withStyle(ChatFormatting.GREEN)
                );
                Services.PLATFORM.playButtonClick(level, pos);
                return InteractionResult.SUCCESS;
            } else {
                pillarBE.cycleParticlePattern();
                String pattern = pillarBE.getParticlePattern();
                if (pattern == null) {
                    pattern = PillarParticleConfig.get().pattern;
                }
                ChatFormatting color = getAshenKingPatternColor(pattern);
                Services.PLATFORM.sendActionBarMessage(
                        player,
                        ComponentHelper.literal(pattern).withStyle(color)
                );
                Services.PLATFORM.playButtonClick(level, pos);
                return InteractionResult.SUCCESS;
            }
        }

        if (heldItem.isEmpty() && pillarBE.hasDisplayItem()) {
            ItemStack displayedItem = pillarBE.getDisplayedItem();
            if (!displayedItem.isEmpty()) {
                pillarBE.setDisplayedItem(ItemStack.EMPTY);
                level.sendBlockUpdated(pos, level.getBlockState(pos), level.getBlockState(pos), 3);
                ItemEntity itemEntity = new ItemEntity(level, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, displayedItem.copy());
                itemEntity.setDefaultPickUpDelay();
                level.addFreshEntity(itemEntity);
                Services.PLATFORM.playItemFrameRemove(level, pos);
                return InteractionResult.SUCCESS;
            }
        }

        if (!heldItem.isEmpty() && !pillarBE.hasDisplayItem()) {
            ItemStack displayItem = heldItem.copy();
            displayItem.setCount(1);

            float playerYaw = player.getYRot();
            float facingYaw = (playerYaw + 180.0f) % 360.0f;
            if (facingYaw < 0.0f) {
                facingYaw += 360.0f;
            }
            pillarBE.setDisplayedItem(displayItem, facingYaw);

            if (player instanceof ServerPlayer serverPlayer) {
                Services.PLATFORM.awardAdvancement(serverPlayer, new CommonId("buildscape", "pedestal"), "insert_item");
            }

            if (!player.getAbilities().instabuild) {
                heldItem.shrink(1);
            }

            Services.PLATFORM.playItemFrameAdd(level, pos);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    private static String getAshenKingDirectionName(float yaw) {
        yaw = yaw % 360.0f;
        if (yaw < 0.0f) {
            yaw += 360.0f;
        }

        if (yaw >= 315.0f || yaw < 45.0f) {
            return "South";
        } else if (yaw >= 45.0f && yaw < 135.0f) {
            return "West";
        } else if (yaw >= 135.0f && yaw < 225.0f) {
            return "North";
        } else {
            return "East";
        }
    }

    private static ChatFormatting getAshenKingPatternColor(String pattern) {
        if (pattern == null) {
            return ChatFormatting.WHITE;
        }

        return switch (pattern) {
            case "default" -> ChatFormatting.WHITE;
            case "beam" -> ChatFormatting.AQUA;
            case "spiral" -> ChatFormatting.LIGHT_PURPLE;
            case "fountain" -> ChatFormatting.BLUE;
            case "pulse" -> ChatFormatting.RED;
            case "ring" -> ChatFormatting.GOLD;
            case "burst" -> ChatFormatting.YELLOW;
            default -> ChatFormatting.GRAY;
        };
    }
}
