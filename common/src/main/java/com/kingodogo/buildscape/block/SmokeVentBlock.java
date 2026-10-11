package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.ComponentHelper;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
public abstract class SmokeVentBlock extends BaseEntityBlock implements ICommonNeighborAware, ICommonAnalogOutput, ICommonInteractable, ICommonShapeUpdate {
    public static final EnumProperty<PillarPart> PART = EnumProperty.create("part", PillarPart.class);
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public static final double SMOKE_SPAWN_BASE = 9.0 / 16.0;

    private static final VoxelShape SHAPE_SINGLE = Shapes.or(
            Block.box(4, 0, 4, 12, 7, 12),
            Block.box(3, 7, 3, 13, 9, 13)
    );
    private static final VoxelShape SHAPE_TOP = Shapes.or(
            Block.box(4, 0, 4, 12, 7, 12),
            Block.box(3, 7, 3, 13, 9, 13)
    );
    private static final VoxelShape SHAPE_MIDDLE = Block.box(4, 0, 4, 12, 16, 12);
    private static final VoxelShape SHAPE_BOTTOM = Block.box(4, 0, 4, 12, 16, 12);

    public SmokeVentBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(PART, PillarPart.SINGLE).setValue(POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PART, POWERED);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SmokeVentBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        boolean hasAbove = level.getBlockState(pos.above()).getBlock() instanceof SmokeVentBlock;
        boolean hasBelow = level.getBlockState(pos.below()).getBlock() instanceof SmokeVentBlock;

        PillarPart part;
        if (hasAbove && hasBelow) {
            part = PillarPart.MIDDLE;
        } else if (hasBelow) {
            part = PillarPart.TOP;
        } else if (hasAbove) {
            part = PillarPart.BOTTOM;
        } else {
            part = PillarPart.SINGLE;
        }

        boolean powered = level.hasNeighborSignal(pos);
        return this.defaultBlockState().setValue(PART, part).setValue(POWERED, powered);
    }
    public static BlockState calculatePart(BlockState state, BlockGetter level, BlockPos pos) {
        boolean hasAbove = level.getBlockState(pos.above()).getBlock() instanceof SmokeVentBlock;
        boolean hasBelow = level.getBlockState(pos.below()).getBlock() instanceof SmokeVentBlock;

        PillarPart newPart;
        if (hasAbove && hasBelow) {
            newPart = PillarPart.MIDDLE;
        } else if (hasBelow) {
            newPart = PillarPart.TOP;
        } else if (hasAbove) {
            newPart = PillarPart.BOTTOM;
        } else {
            newPart = PillarPart.SINGLE;
        }

        return state.setValue(PART, newPart);
    }

    @Override
    public BlockState onUpdateShape(BlockState state, Direction direction, BlockState neighborState, net.minecraft.world.level.LevelReader level, BlockPos pos, BlockPos neighborPos) {
        if (direction == Direction.UP || direction == Direction.DOWN) {
            return calculatePart(state, level, pos);
        }
        return state;
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!level.isClientSide()) {
            boolean powered = hasRedstonePowerInStack(level, pos);
            if (powered != state.getValue(POWERED)) {
                setPoweredAndSync(level, pos, powered);
            }
        }
    }

    @Override
    public void onNeighborUpdate(Level level, BlockPos pos, BlockState state, Block neighborBlock, BlockPos fromPos, boolean isMoving) {
        BlockState updated = calculatePart(state, level, pos);
        if (updated != state) {
            level.setBlock(pos, updated, 2);
            state = updated;
        }
        if (!level.isClientSide()) {
            if (neighborBlock instanceof SmokeVentBlock) return;

            boolean powered = hasRedstonePowerInStack(level, pos);
            boolean wasPowered = state.getValue(POWERED);
            if (powered == wasPowered) return;

            setPoweredAndSync(level, pos, powered);
            applyActiveToStack(level, pos, !powered);

            BlockPos current = findBottomBlock(level, pos);
            while (level.getBlockState(current).getBlock() instanceof SmokeVentBlock) {
                level.updateNeighbourForOutputSignal(current, this);
                current = current.above();
            }

            Services.PLATFORM.playButtonClick(level, pos, powered ? 1.2f : 0.8f);
        }
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutput(BlockState state, Level level, BlockPos pos) {
        BlockPos top = findTopBlock(level, pos);
        BlockEntity be = level.getBlockEntity(top);
        if (be instanceof SmokeVentBlockEntity ventBE) {
            return ventBE.isActive() ? 15 : 0;
        }
        return 0;
    }
    private void setPoweredAndSync(Level level, BlockPos pos, boolean powered) {
        BlockPos current = findBottomBlock(level, pos);
        while (level.getBlockState(current).getBlock() instanceof SmokeVentBlock) {
            BlockState bs = level.getBlockState(current);
            if (bs.getValue(POWERED) != powered) {
                level.setBlock(current, bs.setValue(POWERED, powered), 3);
            }
            current = current.above();
        }
    }
    private boolean hasRedstonePowerInStack(Level level, BlockPos pos) {
        BlockPos current = findBottomBlock(level, pos);
        while (level.getBlockState(current).getBlock() instanceof SmokeVentBlock) {
            if (level.hasNeighborSignal(current)) return true;
            current = current.above();
        }
        return false;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(PART)) {
            case TOP -> SHAPE_TOP;
            case MIDDLE -> SHAPE_MIDDLE;
            case BOTTOM -> SHAPE_BOTTOM;
            default -> SHAPE_SINGLE;
        };
    }

    @Override
    public InteractionResult onInteract(Level level, BlockPos pos, BlockState state, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);
        return handleVentInteraction(held, state, level, pos, player, hand, hit);
    }
    private InteractionResult handleVentInteraction(ItemStack handStack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        ItemStack heldItem = handStack.isEmpty() ? player.getItemInHand(hand) : handStack;
        if (!heldItem.isEmpty()) {
            Map.Entry<String, String> dyeInfo = getDyeColorAndName(heldItem);
            if (dyeInfo != null) {
                String dyeColor = dyeInfo.getKey();
                String dyeName = dyeInfo.getValue();

                applyColorToStack(level, pos, dyeColor);

                if (!player.getAbilities().instabuild) {
                    heldItem.shrink(1);
                }

                Services.PLATFORM.playDyeUse(level, pos);
                Services.PLATFORM.sendActionBarMessage(player, ComponentHelper.literal("Smoke color: " + dyeName).withStyle(ChatFormatting.GRAY));

                return InteractionResult.SUCCESS;
            }
        }
        if (heldItem.isEmpty()) {
            boolean newActive = !getStackActive(level, pos);
            applyActiveToStack(level, pos, newActive);

            Services.PLATFORM.playButtonClick(level, pos, newActive ? 1.2f : 0.8f);

            BlockPos current = findBottomBlock(level, pos);
            while (level.getBlockState(current).getBlock() instanceof SmokeVentBlock) {
                level.updateNeighbourForOutputSignal(current, this);
                level.updateNeighborsAt(current, this);
                current = current.above();
            }

            Services.PLATFORM.sendActionBarMessage(player, ComponentHelper.literal(newActive ? "Smoke Vent: On" : "Smoke Vent: Off")
                    .withStyle(newActive ? ChatFormatting.GREEN : ChatFormatting.RED));

            return InteractionResult.SUCCESS;
        }
        if (player.isShiftKeyDown() && !heldItem.isEmpty() && heldItem.is(Items.WATER_BUCKET)) {
            applyColorToStack(level, pos, null);

            Services.PLATFORM.playBucketEmpty(level, pos);
            Services.PLATFORM.sendActionBarMessage(player, ComponentHelper.literal("Smoke color cleared").withStyle(ChatFormatting.GRAY));

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    public void onAnimateTick(BlockState state, Level level, BlockPos pos) {
        PillarPart part = state.getValue(PART);
        if (part != PillarPart.TOP && part != PillarPart.SINGLE) {
            return;
        }

        BlockEntity activeBe = level.getBlockEntity(pos);
        if (activeBe instanceof SmokeVentBlockEntity ventBE && !ventBE.isActive()) {
            return;
        }

        java.util.concurrent.ThreadLocalRandom random = java.util.concurrent.ThreadLocalRandom.current();
        if (random.nextFloat() < 0.1F) {
            return;
        }

        double x = (double) pos.getX() + 0.5 + random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1);
        double y = (double) pos.getY() + SMOKE_SPAWN_BASE + random.nextDouble() * (2.0 - SMOKE_SPAWN_BASE);
        double z = (double) pos.getZ() + 0.5 + random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1);

        level.addAlwaysVisibleParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, true, x, y, z, 0.0, 0.07, 0.0);
    }

    private boolean getStackActive(Level level, BlockPos pos) {
        BlockPos top = findTopBlock(level, pos);
        BlockEntity be = level.getBlockEntity(top);
        if (be instanceof SmokeVentBlockEntity ventBE) {
            return ventBE.isActive();
        }
        return true;
    }

    private void applyActiveToStack(Level level, BlockPos pos, boolean active) {
        BlockPos bottom = findBottomBlock(level, pos);
        BlockPos current = bottom;
        while (level.getBlockState(current).getBlock() instanceof SmokeVentBlock) {
            BlockEntity be = level.getBlockEntity(current);
            if (be instanceof SmokeVentBlockEntity ventBE) {
                ventBE.setActive(active);
            }
            current = current.above();
        }
    }

    private void applyColorToStack(Level level, BlockPos pos, String color) {
        BlockPos bottom = findBottomBlock(level, pos);
        BlockPos current = bottom;
        while (level.getBlockState(current).getBlock() instanceof SmokeVentBlock) {
            BlockEntity be = level.getBlockEntity(current);
            if (be instanceof SmokeVentBlockEntity ventBE) {
                ventBE.setSmokeColor(color);
            }
            current = current.above();
        }
    }

    private BlockPos findTopBlock(Level level, BlockPos pos) {
        BlockPos current = pos;
        while (level.getBlockState(current.above()).getBlock() instanceof SmokeVentBlock) {
            current = current.above();
        }
        return current;
    }

    private BlockPos findBottomBlock(Level level, BlockPos pos) {
        BlockPos current = pos;
        while (level.getBlockState(current.below()).getBlock() instanceof SmokeVentBlock) {
            current = current.below();
        }
        return current;
    }

    private Map.Entry<String, String> getDyeColorAndName(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return null;

        DyeColor color = com.kingodogo.buildscape.platform.Services.PLATFORM.getDyeColor(stack);
        if (color != null) {
            String hex = String.format("#%06X", (0xFFFFFF & com.kingodogo.buildscape.platform.Services.PLATFORM.getDyeColorValue(color)));
            String name = color.getName().substring(0, 1).toUpperCase() + color.getName().substring(1);
            return Map.entry(hex, name);
        }

        return null;
    }
}
