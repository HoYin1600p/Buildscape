package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.core.DyeColors;
import com.kingodogo.buildscape.event.AdvancementEvents;
import com.kingodogo.buildscape.network.ActionBarMessagePacket;
import com.kingodogo.buildscape.network.ModMessages;
import com.kingodogo.buildscape.registry.ModBlockEntities;
import com.kingodogo.buildscape.registry.ModStats;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.PacketDistributor;

import java.util.Map;

public class SmokeVentBlock extends Block implements EntityBlock {

    public static final EnumProperty<PillarPart> PART = EnumProperty.create("part", PillarPart.class);
    public static final BooleanProperty POWERED = BooleanProperty.create("powered");

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

    public SmokeVentBlock(Properties properties) {
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
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        PillarPart part = state.getValue(PART);
        if (!level.isClientSide || type != ModBlockEntities.SMOKE_VENT_BLOCK_ENTITY.get()
                || (part != PillarPart.TOP && part != PillarPart.SINGLE)) {
            return null;
        }
        return (tickLevel, pos, tickState, blockEntity) ->
                SmokeVentBlockEntity.particleTick(tickLevel, pos, tickState, (SmokeVentBlockEntity) blockEntity);
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

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                  LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (direction == Direction.UP || direction == Direction.DOWN) {
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
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!level.isClientSide) {
            boolean powered = hasRedstonePowerInStack(level, pos);
            if (powered != state.getValue(POWERED)) {
                setPoweredAndSync(level, pos, powered);
            }
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos,
                                Block block, BlockPos fromPos, boolean isMoving) {
        if (!level.isClientSide) {
            if (block instanceof SmokeVentBlock) return;

            boolean powered = hasRedstonePowerInStack(level, pos);
            boolean wasPowered = state.getValue(POWERED);
            if (powered == wasPowered) return;

            setPoweredAndSync(level, pos, powered);
            applyActiveToStack(level, pos, powered);

            BlockPos current = findBottomBlock(level, pos);
            while (level.getBlockState(current).getBlock() instanceof SmokeVentBlock) {
                level.updateNeighbourForOutputSignal(current, this);
                current = current.above();
            }

            level.playSound(null, pos, SoundEvents.UI_BUTTON_CLICK,
                    SoundSource.BLOCKS, 0.5f, powered ? 1.2f : 0.8f);
        }
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
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
        switch (state.getValue(PART)) {
            case TOP:
                return SHAPE_TOP;
            case MIDDLE:
                return SHAPE_MIDDLE;
            case BOTTOM:
                return SHAPE_BOTTOM;
            default:
                return SHAPE_SINGLE;
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        ItemStack heldItem = player.getItemInHand(hand);

        if (!heldItem.isEmpty()) {
            Map.Entry<String, String> dyeInfo = getDyeColorAndName(heldItem);
            if (dyeInfo != null) {
                String dyeColor = dyeInfo.getKey();
                String dyeName = dyeInfo.getValue();

                applyColorToStack(level, pos, dyeColor);

                if (!player.getAbilities().instabuild) {
                    heldItem.shrink(1);
                }

                level.playSound(null, pos, SoundEvents.DYE_USE,
                        SoundSource.BLOCKS, 1.0f, 1.0f);

                TextComponent message =
                        new TextComponent("Smoke color: " + dyeName);
                message.withStyle(ChatFormatting.GRAY);
                if (player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.awardStat(ModStats.SMOKE_VENTS_DYED);
                    AdvancementEvents.grant(serverPlayer, "colorful_smoke");
                    ModMessages.INSTANCE.send(
                            PacketDistributor.PLAYER.with(() -> serverPlayer),
                            new ActionBarMessagePacket(message));
                }

                return InteractionResult.SUCCESS;
            }
        }

        if (heldItem.isEmpty()) {
            boolean newActive = !getStackActive(level, pos);
            applyActiveToStack(level, pos, newActive);

            level.playSound(null, pos, SoundEvents.UI_BUTTON_CLICK,
                    SoundSource.BLOCKS, 0.5f, newActive ? 1.2f : 0.8f);

            BlockPos current = findBottomBlock(level, pos);
            while (level.getBlockState(current).getBlock() instanceof SmokeVentBlock) {
                level.updateNeighbourForOutputSignal(current, this);
                level.updateNeighborsAt(current, this);
                current = current.above();
            }

            TextComponent message =
                    new TextComponent(newActive ? "Smoke Vent: On" : "Smoke Vent: Off");
            message.withStyle(newActive ? ChatFormatting.GREEN : ChatFormatting.RED);
            if (player instanceof ServerPlayer) {
                ModMessages.INSTANCE.send(
                        PacketDistributor.PLAYER.with(() ->
                                (ServerPlayer) player),
                        new ActionBarMessagePacket(message));
            }

            return InteractionResult.SUCCESS;
        }

        if (player.isShiftKeyDown() && !heldItem.isEmpty() &&
                heldItem.getItem() == Items.WATER_BUCKET) {
            applyColorToStack(level, pos, null);

            level.playSound(null, pos, SoundEvents.BUCKET_EMPTY,
                    SoundSource.BLOCKS, 1.0f, 1.0f);

            TextComponent message =
                    new TextComponent("Smoke color cleared");
            message.withStyle(ChatFormatting.GRAY);
            if (player instanceof ServerPlayer) {
                ModMessages.INSTANCE.send(
                        PacketDistributor.PLAYER.with(() ->
                                (ServerPlayer) player),
                        new ActionBarMessagePacket(message));
            }

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
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

        DyeColors dye = DyeColors.fromDyeItem(stack.getItem());
        if (dye != null) {
            return Map.entry(dye.hex(), dye.displayName());
        }

        return null;
    }
}
