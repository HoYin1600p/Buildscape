package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.util.ForgeSoundType;

import java.util.List;

/**
 * Trapped Decorated Pot – identical feature-set to {@link DecoratedPotBlock}
 * PLUS a spawn-egg trap mechanic:
 * <ul>
 *   <li>If the stored item is a {@link SpawnEggItem}, breaking the pot or
 *       right-clicking empty-handed (retrieve gesture) will SPAWN the mob
 *       instead of dropping the egg, then clear the storage.</li>
 *   <li>All other items behave exactly like normal decorated pots.</li>
 * </ul>
 */
public class TrappedDecoratedPotBlock
        extends Block
        implements EntityBlock, SimpleWaterloggedBlock {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final VoxelShape SHAPE = Block.box(1, 0, 1, 15, 16, 15);

    public TrappedDecoratedPotBlock() {
        super(
                Properties.of(Material.STONE)
                        .strength(0.0f)
                        .sound(createPotSoundType())
                        .noOcclusion()
        );
        this.registerDefaultState(
                this.stateDefinition.any().setValue(WATERLOGGED, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluid = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState().setValue(WATERLOGGED, fluid.getType() == Fluids.WATER);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                  LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TrappedDecoratedPotBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> net.minecraft.world.level.block.entity.BlockEntityTicker<T> getTicker(
            Level level, BlockState state, net.minecraft.world.level.block.entity.BlockEntityType<T> type) {
        return null;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof TrappedDecoratedPotBlockEntity potEntity) {
            return potEntity.getComparatorOutput();
        }
        return 0;
    }

    // =========================================================================
    //  Interaction
    // =========================================================================

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {

        if (!(level.getBlockEntity(pos) instanceof TrappedDecoratedPotBlockEntity be)) {
            return InteractionResult.PASS;
        }

        ItemStack heldItem = player.getItemInHand(hand);

        // ---- Shift + item in hand → store item (same logic as normal pot) ----
        if (!heldItem.isEmpty() && player.isShiftKeyDown()) {
            // Prevent storing trapped pots inside themselves
            if (heldItem.getItem() instanceof net.minecraft.world.item.BlockItem blockItem &&
                    blockItem.getBlock() instanceof TrappedDecoratedPotBlock) {
                return InteractionResult.PASS;
            }

            ItemStack stored = be.getStoredItem();

            if (stored.isEmpty()) {
                if (!level.isClientSide) {
                    ItemStack toStore = heldItem.copy();
                    int toTake = Math.min(heldItem.getCount(), toStore.getMaxStackSize());
                    toStore.setCount(toTake);
                    be.setStoredItem(toStore, false);
                    heldItem.shrink(toTake);
                    if (heldItem.isEmpty()) player.setItemInHand(hand, ItemStack.EMPTY);
                    playInsertSound(level, pos);
                    spawnInsertParticles(level, pos);
                    be.triggerWobble(TrappedDecoratedPotBlockEntity.WobbleStyle.POSITIVE);
                    level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                }
                return InteractionResult.sidedSuccess(level.isClientSide);

            } else if (stored.sameItem(heldItem) && stored.getCount() < stored.getMaxStackSize()) {
                if (!level.isClientSide) {
                    int spaceAvailable = stored.getMaxStackSize() - stored.getCount();
                    int canAdd = Math.min(heldItem.getCount(), spaceAvailable);
                    stored.grow(canAdd);
                    be.setStoredItem(stored, true);
                    heldItem.shrink(canAdd);
                    if (heldItem.isEmpty()) player.setItemInHand(hand, ItemStack.EMPTY);
                    playInsertSound(level, pos);
                    spawnInsertParticles(level, pos);
                    be.triggerWobble(TrappedDecoratedPotBlockEntity.WobbleStyle.POSITIVE);
                    level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }

            // Full or incompatible
            if (!level.isClientSide) {
                playFailSound(level, pos);
                be.triggerWobble(TrappedDecoratedPotBlockEntity.WobbleStyle.NEGATIVE);
                level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            }
            return InteractionResult.PASS;
        }

        // ---- Empty hand, no shift → "poke" wobble ----
        if (heldItem.isEmpty() && !player.isShiftKeyDown()) {
            if (!level.isClientSide) {
                level.playSound(null, pos, ModSounds.DECORATED_POT_HIT.get(), SoundSource.BLOCKS, 0.5f, 1.0f);
                be.triggerWobble(TrappedDecoratedPotBlockEntity.WobbleStyle.POSITIVE);
                level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        // Prevent storing trapped pots
        if (!heldItem.isEmpty() && heldItem.getItem() instanceof net.minecraft.world.item.BlockItem blockItem &&
                blockItem.getBlock() instanceof TrappedDecoratedPotBlock) {
            return InteractionResult.PASS;
        }

        // ---- Item in hand, no shift → store 1 at a time ----
        if (!heldItem.isEmpty() && !player.isShiftKeyDown()) {
            ItemStack stored = be.getStoredItem();

            if (stored.isEmpty()) {
                if (!level.isClientSide) {
                    ItemStack toStore = heldItem.copy();
                    toStore.setCount(1);
                    be.setStoredItem(toStore, true);
                    heldItem.shrink(1);
                    if (heldItem.isEmpty()) player.setItemInHand(hand, ItemStack.EMPTY);
                    playInsertSound(level, pos);
                    spawnInsertParticles(level, pos);
                    be.triggerWobble(TrappedDecoratedPotBlockEntity.WobbleStyle.POSITIVE);
                    level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                }
                return InteractionResult.sidedSuccess(level.isClientSide);

            } else if (stored.sameItem(heldItem) && stored.getCount() < stored.getMaxStackSize()) {
                if (!level.isClientSide) {
                    stored.grow(1);
                    be.setStoredItem(stored, true);
                    heldItem.shrink(1);
                    if (heldItem.isEmpty()) player.setItemInHand(hand, ItemStack.EMPTY);
                    playInsertSound(level, pos);
                    spawnInsertParticles(level, pos);
                    be.triggerWobble(TrappedDecoratedPotBlockEntity.WobbleStyle.POSITIVE);
                    level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            } else {
                if (!level.isClientSide) {
                    playFailSound(level, pos);
                    be.triggerWobble(TrappedDecoratedPotBlockEntity.WobbleStyle.NEGATIVE);
                    level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                }
            }
        }

        // ---- Empty hand + shift → retrieve (spawn if egg, else give back) ----
        if (heldItem.isEmpty() && player.isShiftKeyDown() && !be.isEmpty()) {
            if (!level.isClientSide) {
                ItemStack stored = be.getStoredItem().copy();
                be.setStoredItem(ItemStack.EMPTY, true);

                if (stored.getItem() instanceof SpawnEggItem spawnEgg) {
                    // Trap fires: spawn the mob at the top of the pot
                    spawnMobFromEgg(level, pos, spawnEgg);
                    // No egg returned – the trap is consumed
                } else {
                    // Normal retrieval: give item back
                    if (!player.getInventory().add(stored)) {
                        net.minecraft.world.entity.item.ItemEntity itemEntity =
                                new net.minecraft.world.entity.item.ItemEntity(
                                        level,
                                        pos.getX() + 0.5,
                                        pos.getY() + 0.5,
                                        pos.getZ() + 0.5,
                                        stored
                                );
                        itemEntity.setDefaultPickUpDelay();
                        level.addFreshEntity(itemEntity);
                    }
                }
                level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return InteractionResult.PASS;
    }

    // =========================================================================
    //  Breaking – spawn egg trap fires on break too
    // =========================================================================

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof TrappedDecoratedPotBlockEntity potEntity && !potEntity.isEmpty()) {
            ItemStack stored = potEntity.getStoredItem().copy();

            if (!level.isClientSide) {
                if (stored.getItem() instanceof SpawnEggItem spawnEgg) {
                    // Trap fires: spawn mob, don't drop egg
                    spawnMobFromEgg(level, pos, spawnEgg);
                } else {
                    // Drop non-egg items normally
                    net.minecraft.world.entity.item.ItemEntity itemEntity =
                            new net.minecraft.world.entity.item.ItemEntity(
                                    level,
                                    pos.getX() + 0.5,
                                    pos.getY() + 0.5,
                                    pos.getZ() + 0.5,
                                    stored
                            );
                    itemEntity.setDefaultPickUpDelay();
                    level.addFreshEntity(itemEntity);
                }
            }
        }
        super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state,
                               BlockEntity be, ItemStack tool) {
        level.playSound(null, pos, ModSounds.DECORATED_POT_BREAK.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
        super.playerDestroy(level, player, pos, state, be, tool);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        LootContext ctx = builder
                .withParameter(net.minecraft.world.level.storage.loot.parameters.LootContextParams.BLOCK_STATE, state)
                .create(net.minecraft.world.level.storage.loot.parameters.LootContextParamSets.BLOCK);
        ItemStack tool = ctx.getParamOrNull(net.minecraft.world.level.storage.loot.parameters.LootContextParams.TOOL);
        if (tool != null && !tool.isEmpty()) {
            return java.util.Collections.singletonList(new ItemStack(Items.BRICK, 4));
        }
        return java.util.Collections.singletonList(new ItemStack(this));
    }

    // =========================================================================
    //  Helpers
    // =========================================================================

    /**
     * Spawns the mob associated with the given spawn egg directly above the pot.
     */
    private static void spawnMobFromEgg(Level level, BlockPos pos, SpawnEggItem spawnEgg) {
        if (level.isClientSide) return;
        EntityType<?> entityType = spawnEgg.getType(null);
        if (entityType != null) {
            // Spawn in the center, just above the top of the pot
            Vec3 spawnPos = Vec3.atCenterOf(pos).add(0, 0.5, 0);
            entityType.spawn(
                    (net.minecraft.server.level.ServerLevel) level,
                    null,
                    null,
                    null,
                    new BlockPos(spawnPos.x, spawnPos.y, spawnPos.z),
                    MobSpawnType.SPAWN_EGG,
                    true,
                    false
            );
            // Poof particles
            if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                serverLevel.sendParticles(
                        ParticleTypes.POOF,
                        spawnPos.x, spawnPos.y + 0.5, spawnPos.z,
                        8, 0.3, 0.3, 0.3, 0.05
                );
            }
        }
    }

    private static void playInsertSound(Level level, BlockPos pos) {
        level.playSound(null, pos, ModSounds.DECORATED_POT_INSERT_ITEM.get(), SoundSource.BLOCKS, 0.5f, 1.0f);
    }

    private static void playFailSound(Level level, BlockPos pos) {
        level.playSound(null, pos, ModSounds.DECORATED_POT_INSERT_FAIL.get(), SoundSource.BLOCKS, 0.5f, 1.0f);
    }

    private static void spawnInsertParticles(Level level, BlockPos pos) {
        double cx = pos.getX() + 0.5, cy = pos.getY() + 1.0, cz = pos.getZ() + 0.5;
        if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, cx, cy, cz, 0, 0.0, 0.1, 0.0, 1.0);
        } else if (level.isClientSide) {
            level.addParticle(ParticleTypes.LARGE_SMOKE, cx, cy, cz, 0.0, 0.1, 0.0);
        }
    }

    private static SoundType createPotSoundType() {
        return new ForgeSoundType(
                1.0f, 1.0f,
                () -> ModSounds.DECORATED_POT_BREAK.get(),
                () -> ModSounds.DECORATED_POT_STEP.get(),
                () -> ModSounds.DECORATED_POT_PLACE.get(),
                () -> ModSounds.DECORATED_POT_HIT.get(),
                () -> ModSounds.DECORATED_POT_FALL.get()
        );
    }
}
