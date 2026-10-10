package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.particle.ModParticles;
import com.kingodogo.buildscape.sound.ModSounds;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
public class PotentSulfurBlock extends Block implements EntityBlock, ICommonNeighborAware {

    public static final EnumProperty<PotentSulfurState> STATE = EnumProperty.create("state", PotentSulfurState.class);
    private static final CommonId CAUSES_CONTINUOUS_GEYSER_ERUPTIONS = CommonId.of("minecraft", "causes_continuous_geyser_eruptions");
    private static final CommonId CAUSES_PERIODIC_GEYSER_ERUPTIONS = CommonId.of("minecraft", "causes_periodic_geyser_eruptions");

    public PotentSulfurBlock(final BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(STATE, PotentSulfurState.DRY));
    }

    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STATE);
    }

    @Override
    public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
        return new PotentSulfurBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public void onNeighborUpdate(Level level, BlockPos pos, BlockState state, Block block, BlockPos neighborPos, boolean isMoving) {
        if (!level.isClientSide()) {
            BlockState updated = validBlockState(state, level, pos);
            if (updated != state) {
                level.setBlock(pos, updated, 3);
            }
        }
    }

    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext context) {
        return validBlockState(this.defaultBlockState(), context.getLevel(), context.getClickedPos());
    }
    public static BlockState validBlockState(final BlockState state, final LevelReader level, final BlockPos pos) {
        if (!level.getFluidState(pos.above()).isSourceOfType(Fluids.WATER)) {
            return state.setValue(STATE, PotentSulfurState.DRY);
        } else {
            BlockState belowState = level.getBlockState(pos.below());
            boolean isContinuous = (belowState.is(Blocks.LAVA)
                    || Services.PLATFORM.isBlockInTag(belowState.getBlock(), CAUSES_CONTINUOUS_GEYSER_ERUPTIONS))
                    && isSourceIfFluid(belowState);
            boolean isPeriodic = (belowState.is(Blocks.MAGMA_BLOCK)
                    || Services.PLATFORM.isBlockInTag(belowState.getBlock(), CAUSES_PERIODIC_GEYSER_ERUPTIONS))
                    && isSourceIfFluid(belowState);

            if (isContinuous) {
                return state.setValue(STATE, PotentSulfurState.CONTINUOUS);
            } else if (isPeriodic) {
                boolean isGeyser = state.getValue(STATE) == PotentSulfurState.ERUPTING || state.getValue(STATE) == PotentSulfurState.DORMANT;
                if (!isGeyser) {
                    BlockEntity be = level.getBlockEntity(pos);
                    if (be instanceof PotentSulfurBlockEntity potentSulfurEntity) {
                        potentSulfurEntity.resetCountdown();
                    }
                }
                return state.getValue(STATE) == PotentSulfurState.ERUPTING ? state : state.setValue(STATE, PotentSulfurState.DORMANT);
            } else {
                return state.setValue(STATE, PotentSulfurState.WET);
            }
        }
    }

    public BlockState onUpdateShape(final BlockState state, final LevelReader level, final BlockPos pos) {
        return validBlockState(state, level, pos);
    }

    private static boolean isSourceIfFluid(final BlockState belowState) {
        FluidState fluidState = belowState.getFluidState();
        return fluidState.isEmpty() || fluidState.isSource();
    }

    public void onPlaced(final BlockState state, final Level level, final BlockPos pos) {
        if (state.getValue(STATE) == PotentSulfurState.ERUPTING || state.getValue(STATE) == PotentSulfurState.CONTINUOUS) {
            level.blockEvent(pos, this, 0, 0);
            level.playSound(null, pos,
                    state.getValue(STATE) == PotentSulfurState.CONTINUOUS
                            ? ModSounds.GEYSER_CONTINUOUS_START.get()
                            : ModSounds.GEYSER_ERUPTION_START.get(),
                    SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }

    public void onAnimateTick(final BlockState state, final Level level, final BlockPos pos,
                              final java.util.function.DoubleSupplier random) {
        if (state.getValue(STATE) != PotentSulfurState.DRY) {
            if (level.getFluidState(pos.above()).isSourceOfType(Fluids.WATER)) {
                spawnBubbleParticlesAt(level, random, pos.getX(), pos.getY() + 1, pos.getZ());
                spawnBubbleParticlesAt(level, random, pos.getX(), pos.getY() + 1, pos.getZ());
            }
        }
    }

    private static void spawnBubbleParticlesAt(final Level level, final java.util.function.DoubleSupplier random,
                                               final double x, final double y, final double z) {
        level.addAlwaysVisibleParticle(ModParticles.SULFUR_BUBBLES.get(),
                x + random.getAsDouble(), y + random.getAsDouble(), z + random.getAsDouble(), 0.0D, 0.0D, 0.0D);
    }

    public boolean onTriggerEvent(final Level level, final BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof PotentSulfurBlockEntity entity) {
            entity.eruptionTick = level.getGameTime();
        }
        return true;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(final Level level, final BlockState blockState, final BlockEntityType<T> type) {
        if (type != ModBlockEntities.POTENT_SULFUR_TYPE) return null;
        boolean client = level.isClientSide();
        BlockEntityTicker<PotentSulfurBlockEntity> ticker;

        switch (blockState.getValue(STATE)) {
            case DRY -> ticker = null;
            case WET -> ticker = client ? PotentSulfurBlockEntity.CLIENT_NOXIOUS_GAS_TICKER : PotentSulfurBlockEntity.SERVER_NAUSEA_EFFECT_TICKER;
            case DORMANT -> ticker = client ? PotentSulfurBlockEntity.CLIENT_NOXIOUS_GAS_TICKER : (l, p, s, e) -> {
                PotentSulfurBlockEntity.SERVER_WAITING_COUNTDOWN_TICKER.tick(l, p, s, e);
                PotentSulfurBlockEntity.SERVER_NAUSEA_EFFECT_TICKER.tick(l, p, s, e);
            };
            case ERUPTING -> ticker = client ? (l, p, s, e) -> {
                PotentSulfurBlockEntity.CLIENT_GEYSER_PLUME_TICKER_ERUPTION.tick(l, p, s, e);
                PotentSulfurBlockEntity.LAUNCH_ENTITY_TICKER.tick(l, p, s, e);
            } : (l, p, s, e) -> {
                PotentSulfurBlockEntity.LAUNCH_ENTITY_TICKER.tick(l, p, s, e);
                PotentSulfurBlockEntity.SERVER_WAITING_COUNTDOWN_TICKER.tick(l, p, s, e);
            };
            case CONTINUOUS -> ticker = client ? (l, p, s, e) -> {
                PotentSulfurBlockEntity.CLIENT_GEYSER_PLUME_TICKER_CONTINUOUS.tick(l, p, s, e);
                PotentSulfurBlockEntity.LAUNCH_ENTITY_TICKER.tick(l, p, s, e);
            } : PotentSulfurBlockEntity.LAUNCH_ENTITY_TICKER;
            default -> ticker = null;
        }

        return (BlockEntityTicker<T>) ticker;
    }
}
