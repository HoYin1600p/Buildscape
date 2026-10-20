package com.kingodogo.buildscape.feature.pillar;

import com.kingodogo.buildscape.advancement.AdvancementGrants;
import com.kingodogo.buildscape.config.PillarParticleConfig;
import com.kingodogo.buildscape.core.DyeColors;
import com.kingodogo.buildscape.core.ParticlePatterns;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.PacketDistributor;

import java.util.AbstractMap;
import java.util.Map;

public class AshenKingPillarBlock extends PillarBlock {

    public static final DirectionProperty FACING =
            BlockStateProperties.HORIZONTAL_FACING;

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
        switch (facing) {
            case EAST:  return Block.box(16 - z2, y1, x1, 16 - z1, y2, x2);
            case SOUTH: return Block.box(16 - x2, y1, 16 - z2, 16 - x1, y2, 16 - z1);
            case WEST:  return Block.box(z1, y1, 16 - x2, z2, y2, 16 - x1);
            default:    return Block.box(x1, y1, z1, x2, y2, z2);
        }
    }

    public AshenKingPillarBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(PART, PillarPart.SINGLE)
                        .setValue(WATERLOGGED, false)
                        .setValue(FACING, Direction.NORTH)
        );
    }


    @Override
    protected void createBlockStateDefinition(
            StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PART, WATERLOGGED, FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction facing = context.getHorizontalDirection().getOpposite();
        FluidState fluidstate = level.getFluidState(pos);

        BlockState state = this.defaultBlockState().setValue(FACING, facing).setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);

        return state.setValue(PART, PillarPart.SINGLE);
    }

    private BlockState updatePart(BlockState state, LevelAccessor level, BlockPos pos, Direction facing) {
        return state.setValue(PART, PillarPart.SINGLE);
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

        Direction facing = state.getValue(FACING);
        if (direction == facing.getClockWise() || direction == facing.getCounterClockWise()) {
            return updatePart(state, level, pos, facing);
        }

        return state;
    }

    @Override
    public VoxelShape getShape(
            BlockState state, BlockGetter level, BlockPos pos,
            CollisionContext context) {
        int fIdx = state.getValue(FACING).get2DDataValue();
        int pIdx = state.getValue(PART).ordinal();
        return SHAPES[fIdx][pIdx];
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
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!level.isClientSide) {
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
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos neighborPos, boolean isMoving) {
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

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof PillarBlockEntity pillarBE)) {
            return InteractionResult.PASS;
        }

        ItemStack heldItem = player.getItemInHand(hand);

        if (!heldItem.isEmpty()) {
            Map.Entry<String, String> dyeInfo = getDyeColorAndName(heldItem);
            if (dyeInfo != null) {
                if (pillarBE.hasDisplayItem()) {
                    String dyeColor = dyeInfo.getKey();
                    String dyeName = dyeInfo.getValue();

                    if (!pillarBE.canAddMoreColors()) {
                        TextComponent message =
                                new TextComponent(
                                        "Pillar already has " +
                                                PillarBlockEntity.MAX_DYE_COLORS +
                                                " colors! Break and replace to reset."
                                );
                        message.withStyle(ChatFormatting.RED);
                        if (player instanceof ServerPlayer) {
                            ModMessages.INSTANCE.send(
                                    PacketDistributor.PLAYER.with(() ->
                                            (ServerPlayer) player
                                    ),
                                    new ActionBarMessagePacket(message)
                            );
                        }
                        return InteractionResult.CONSUME;
                    }

                    boolean added = pillarBE.addParticleColor(dyeColor);
                    if (!added) {
                        return InteractionResult.PASS;
                    }

                    if (!player.getAbilities().instabuild) {
                        heldItem.shrink(1);
                    }

                    level.playSound(null, pos, SoundEvents.DYE_USE,
                            SoundSource.BLOCKS, 1.0f, 1.0f);

                    String pillarId = pillarBE.getPillarId();
                    int colorCount = pillarBE.getDyeColorCount();
                    String progressText = " (" + colorCount + "/" + PillarBlockEntity.MAX_DYE_COLORS + ")";
                    TextComponent message;
                    if (pillarId != null) {
                        message = new TextComponent(
                                "[" + pillarId + "] Dyed " + dyeName + progressText);
                    } else {
                        message = new TextComponent(
                                "Pillar Dyed " + dyeName + progressText);
                    }

                    if (player instanceof ServerPlayer) {
                        ModMessages.INSTANCE.send(
                                PacketDistributor.PLAYER.with(() ->
                                        (ServerPlayer) player
                                ),
                                new ActionBarMessagePacket(message)
                        );
                    }

                    return InteractionResult.SUCCESS;
                }
            }
        }

        if (player.isShiftKeyDown()) {
            ItemStack displayedItem = pillarBE.getDisplayedItem();
            boolean isSpawnEgg = !displayedItem.isEmpty() &&
                    displayedItem.getItem() instanceof SpawnEggItem;

            if (isSpawnEgg) {
                pillarBE.rotateFacing();
                float facingYaw = pillarBE.getFacingYaw();
                String direction = getDirectionName(facingYaw);
                TextComponent message =
                        new TextComponent("Mob facing: " + direction);
                message.withStyle(ChatFormatting.GREEN);
                player.displayClientMessage(message, true);

                level.playSound(null, pos, SoundEvents.UI_BUTTON_CLICK,
                        SoundSource.BLOCKS, 0.5f, 1.0f);
                return InteractionResult.SUCCESS;
            } else {
                pillarBE.cycleParticlePattern();
                String pattern = pillarBE.getParticlePattern();
                if (pattern == null) {
                    pattern = PillarParticleConfig.get().pattern;
                }

                ChatFormatting color = getPatternColor(pattern);
                TextComponent patternText =
                        (TextComponent) new TextComponent(pattern)
                                .withStyle(color);
                player.displayClientMessage(patternText, true);

                level.playSound(null, pos, SoundEvents.UI_BUTTON_CLICK,
                        SoundSource.BLOCKS, 0.5f, 1.0f);
                return InteractionResult.SUCCESS;
            }
        }

        if (heldItem.isEmpty() && pillarBE.hasDisplayItem()) {
            ItemStack displayedItem = pillarBE.getDisplayedItem();
            if (!displayedItem.isEmpty()) {
                pillarBE.setDisplayedItem(ItemStack.EMPTY);
                level.sendBlockUpdated(pos, level.getBlockState(pos), level.getBlockState(pos), 3);

                ItemEntity itemEntity =
                        new ItemEntity(
                                level, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                                displayedItem.copy());
                itemEntity.setDefaultPickUpDelay();
                level.addFreshEntity(itemEntity);

                level.playSound(null, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM,
                        SoundSource.BLOCKS, 1.0f, 1.0f);
                return InteractionResult.SUCCESS;
            }
        }

        if (!heldItem.isEmpty() && !pillarBE.hasDisplayItem()) {
            ItemStack displayItem = heldItem.copy();
            displayItem.setCount(1);

            float playerYaw = player.getYRot();
            float facingYaw = (playerYaw + 180.0f) % 360.0f;
            if (facingYaw < 0) {
                facingYaw += 360.0f;
            }
            pillarBE.setDisplayedItem(displayItem, facingYaw);

            if (player instanceof ServerPlayer serverPlayer) {
                AdvancementGrants.onPillarItemInserted(serverPlayer);
            }

            if (pillarBE.getParticlePattern() == null) {
                PillarParticleConfig cfg =
                        PillarParticleConfig.get();
                if (cfg.use_pattern && cfg.pattern != null) {
                }
            }

            if (!player.getAbilities().instabuild) {
                heldItem.shrink(1);
            }

            level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM,
                    SoundSource.BLOCKS, 1.0f, 1.0f);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    private Map.Entry<String, String> getDyeColorAndName(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return null;
        }

        DyeColors dye = DyeColors.fromDyeItem(stack.getItem());
        if (dye != null) {
            return new AbstractMap.SimpleEntry<>(dye.hex(), dye.displayName());
        }

        return null;
    }

    private String getDirectionName(float yaw) {
        yaw = yaw % 360.0f;
        if (yaw < 0) {
            yaw += 360.0f;
        }

        if (yaw >= 315 || yaw < 45) {
            return "South";
        }
        else if (yaw >= 45 && yaw < 135) {
            return "West";
        }
        else if (yaw >= 135 && yaw < 225) {
            return "North";
        }
        else {
            return "East";
        }
    }

    private ChatFormatting getPatternColor(String pattern) {
        if (pattern == null) {
            return ChatFormatting.WHITE;
        }

        switch (pattern) {
            case ParticlePatterns.DEFAULT: return ChatFormatting.WHITE;
            case ParticlePatterns.BEAM: return ChatFormatting.AQUA;
            case ParticlePatterns.SPIRAL: return ChatFormatting.LIGHT_PURPLE;
            case ParticlePatterns.FOUNTAIN: return ChatFormatting.BLUE;
            case ParticlePatterns.PULSE: return ChatFormatting.RED;
            case ParticlePatterns.RING: return ChatFormatting.GOLD;
            case ParticlePatterns.BURST: return ChatFormatting.YELLOW;
            default: return ChatFormatting.GRAY;
        }
    }
}
