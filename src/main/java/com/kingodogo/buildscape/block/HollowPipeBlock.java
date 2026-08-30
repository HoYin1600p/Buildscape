package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.fluid.ModFluids;
import com.kingodogo.buildscape.item.ModItems;
import com.kingodogo.buildscape.pipe.transport.BubbleColumnHandler;
import com.kingodogo.buildscape.pipe.transport.HollowPipeTransportManager;
import com.kingodogo.buildscape.pipe.transport.PipeFlowState;
import com.kingodogo.buildscape.pipe.transport.WaterPipeTransport;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.Set;

public class HollowPipeBlock extends RotatedPillarBlock implements SimpleWaterloggedBlock, EntityBlock {
    public static final ThreadLocal<Fluid> PLACED_FLUID = new ThreadLocal<>();

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty LAVA_LOGGED = BooleanProperty.create("lava_logged");
    /**
     * Highest water surface that fits beneath the pipe's two-pixel ceiling.
     * This is the source level for the internal channel; flowing levels below
     * it use vanilla's normal 7/9 through 1/9 sequence.
     */
    public static final float WATER_SOURCE_VISUAL_HEIGHT = 14.0F / 16.0F;
    /**
     * Set by the BFS transport manager to represent the flowing water level inside this pipe.
     * This is internal channel state for the block-entity renderer and transport logic, not a
     * vanilla FluidState for the whole block volume.
     */
    public static final IntegerProperty WATER_LEVEL = IntegerProperty.create("water_level", 0, 7);

    public static final BooleanProperty DOWN  = BlockStateProperties.DOWN;
    public static final BooleanProperty UP    = BlockStateProperties.UP;
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty WEST  = BlockStateProperties.WEST;
    public static final BooleanProperty EAST  = BlockStateProperties.EAST;

    private static final VoxelShape BOX_NORTH = Block.box(0, 0, 0, 16, 16, 2);
    private static final VoxelShape BOX_SOUTH = Block.box(0, 0, 14, 16, 16, 16);
    private static final VoxelShape BOX_WEST  = Block.box(0, 0, 0, 2, 16, 16);
    private static final VoxelShape BOX_EAST  = Block.box(14, 0, 0, 16, 16, 16);
    private static final VoxelShape BOX_DOWN  = Block.box(0, 0, 0, 16, 2, 16);
    private static final VoxelShape BOX_UP    = Block.box(0, 14, 0, 16, 16, 16);

    private static final VoxelShape Y_SHAPE = Shapes.join(
        Shapes.block(),
        Block.box(2, 0, 2, 14, 16, 14),
        BooleanOp.ONLY_FIRST
    ).optimize();

    private static final VoxelShape X_SHAPE = Shapes.join(
        Shapes.block(),
        Block.box(0, 2, 2, 16, 14, 14),
        BooleanOp.ONLY_FIRST
    ).optimize();

    private static final VoxelShape Z_SHAPE = Shapes.join(
        Shapes.block(),
        Block.box(2, 2, 0, 14, 14, 16),
        BooleanOp.ONLY_FIRST
    ).optimize();

    private static final VoxelShape X_SHAPE_SNEAK = Shapes.or(BOX_NORTH, BOX_SOUTH, BOX_DOWN);
    private static final VoxelShape Z_SHAPE_SNEAK = Shapes.or(BOX_WEST, BOX_EAST, BOX_DOWN);

    // Fast bitmask lookup table for selection/outline shapes (0..63)
    private static final VoxelShape[] SHAPES_BY_MASK = new VoxelShape[64];

    static {
        for (int mask = 0; mask < 64; mask++) {
            boolean down  = (mask & (1 << Direction.DOWN.get3DDataValue())) != 0;
            boolean up    = (mask & (1 << Direction.UP.get3DDataValue())) != 0;
            boolean north = (mask & (1 << Direction.NORTH.get3DDataValue())) != 0;
            boolean south = (mask & (1 << Direction.SOUTH.get3DDataValue())) != 0;
            boolean west  = (mask & (1 << Direction.WEST.get3DDataValue())) != 0;
            boolean east  = (mask & (1 << Direction.EAST.get3DDataValue())) != 0;

            int count = (down ? 1 : 0) + (up ? 1 : 0) + (north ? 1 : 0) + (south ? 1 : 0) + (west ? 1 : 0) + (east ? 1 : 0);

            if (count == 0) {
                SHAPES_BY_MASK[mask] = Y_SHAPE;
            } else if (!north && !south && !west && !east && (down || up)) {
                SHAPES_BY_MASK[mask] = Y_SHAPE;
            } else if (!down && !up && !west && !east && (north || south)) {
                SHAPES_BY_MASK[mask] = Z_SHAPE;
            } else if (!down && !up && !north && !south && (west || east)) {
                SHAPES_BY_MASK[mask] = X_SHAPE;
            } else {
                VoxelShape outer = Shapes.block();
                VoxelShape interior = Block.box(2, 2, 2, 14, 14, 14);

                if (down)  interior = Shapes.or(interior, Block.box(2, 0, 2, 14, 2, 14));
                if (up)    interior = Shapes.or(interior, Block.box(2, 14, 2, 14, 16, 14));
                if (north) interior = Shapes.or(interior, Block.box(2, 2, 0, 14, 14, 2));
                if (south) interior = Shapes.or(interior, Block.box(2, 2, 14, 14, 14, 16));
                if (west)  interior = Shapes.or(interior, Block.box(0, 2, 2, 2, 14, 14));
                if (east)  interior = Shapes.or(interior, Block.box(14, 2, 2, 16, 14, 14));

                SHAPES_BY_MASK[mask] = Shapes.join(outer, interior, BooleanOp.ONLY_FIRST).optimize();
            }
        }
    }

    public HollowPipeBlock(Properties properties) {
        super(properties.noOcclusion());
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(AXIS, Direction.Axis.Y)
                .setValue(DOWN, false)
                .setValue(UP, false)
                .setValue(NORTH, false)
                .setValue(SOUTH, false)
                .setValue(WEST, false)
                .setValue(EAST, false)
                .setValue(WATERLOGGED, false)
                .setValue(LAVA_LOGGED, false)
                .setValue(WATER_LEVEL, 0));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HollowLogBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) return null;
        return type == ModBlockEntities.HOLLOW_LOG_BLOCK_ENTITY.get()
                ? (lvl, pos, st, be) -> HollowLogBlockEntity.serverTick(lvl, pos, st, (HollowLogBlockEntity) be)
                : null;
    }

    public static Fluid getSourceFluid(BlockState state, @Nullable BlockEntity be) {
        if (state.getValue(WATERLOGGED)) {
            return Fluids.WATER;
        }
        if (state.getValue(LAVA_LOGGED)) {
            return Fluids.LAVA;
        }
        if (be instanceof HollowLogBlockEntity hollowBe) {
            String ft = hollowBe.getFluidType();
            if ("water".equals(ft)) return Fluids.WATER;
            if ("lava".equals(ft)) return Fluids.LAVA;
            if ("experience".equals(ft) || "buildscape:experience_still".equals(ft) || "buildscape:experience".equals(ft)) {
                return ModFluids.EXPERIENCE_STILL.get();
            }
            if (!"none".equals(ft) && !ft.isEmpty()) {
                ResourceLocation loc = ResourceLocation.tryParse(ft);
                if (loc != null && ForgeRegistries.FLUIDS.containsKey(loc)) {
                    Fluid f = ForgeRegistries.FLUIDS.getValue(loc);
                    if (f != null) return f;
                }
            }
        }
        return Fluids.EMPTY;
    }

    public static Fluid getContainedFluid(BlockState state, @Nullable BlockEntity be) {
        Fluid source = getSourceFluid(state, be);
        if (source != Fluids.EMPTY) {
            return source;
        }
        if (be instanceof HollowLogBlockEntity hollowBe) {
            PipeFlowState flow = hollowBe.getPipeFlowState();
            if (flow != null && flow.hasWater()) {
                return Fluids.WATER;
            }
        }
        return Fluids.EMPTY;
    }

    public static Fluid getFluidFromItem(ItemStack stack) {
        if (stack.isEmpty()) return Fluids.EMPTY;
        if (stack.getItem() instanceof BucketItem bucketItem) {
            return bucketItem.getFluid();
        }
        FluidStack fs = FluidUtil.getFluidContained(stack).orElse(FluidStack.EMPTY);
        if (!fs.isEmpty()) {
            return fs.getFluid();
        }
        return Fluids.EMPTY;
    }

    public static ItemStack getFilledBucketForFluid(Fluid fluid) {
        if (fluid == Fluids.WATER) return new ItemStack(Items.WATER_BUCKET);
        if (fluid == Fluids.LAVA) return new ItemStack(Items.LAVA_BUCKET);
        if (fluid == ModFluids.EXPERIENCE_STILL.get() || fluid == ModFluids.EXPERIENCE_FLOWING.get()) {
            return new ItemStack(ModItems.EXPERIENCE_BUCKET.get());
        }
        Item bucket = fluid.getBucket();
        if (bucket != null && bucket != Items.AIR) {
            return new ItemStack(bucket);
        }
        return FluidUtil.getFilledBucket(new FluidStack(fluid, 1000));
    }

    public static boolean isOpenEndpoint(BlockState state, Direction dir) {
        if (dir == null || !(state.getBlock() instanceof HollowPipeBlock)) {
            return false;
        }
        BooleanProperty prop = getPropertyForDirection(dir);
        if (prop != null && state.hasProperty(prop) && state.getValue(prop)) {
            // A set directional property denotes a seamless connection to another
            // pipe, never an opening into the world.
            return false;
        }
        int connections = getConnectCount(state);
        if (connections == 0) {
            return dir.getAxis() == state.getValue(AXIS);
        }
        // A pipe with one attached segment has one exposed end along its primary
        // axis. Junctions have no implicit world-facing endpoint.
        return connections == 1 && dir.getAxis() == getPrimaryAxis(state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);
        BlockEntity be = level.getBlockEntity(pos);
        HollowLogBlockEntity hollowBe = be instanceof HollowLogBlockEntity h ? h : null;
        Fluid containedFluid = getContainedFluid(state, be);
        Direction hitFace = hit.getDirection();

        // 1. Wrench configuration: sneak-click rotates axis, normal click toggles open side (min 2 openings)
        if (held.is(ModItems.WRENCH.get())) {
            if (!level.isClientSide) {
                if (player.isShiftKeyDown()) {
                    Direction.Axis currentAxis = state.getValue(AXIS);
                    Direction.Axis nextAxis = switch (currentAxis) {
                        case Y -> Direction.Axis.Z;
                        case Z -> Direction.Axis.X;
                        case X -> Direction.Axis.Y;
                    };
                    level.setBlock(pos, state.setValue(AXIS, nextAxis), 3);
                    level.playSound(null, pos, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 0.5F, 1.5F);
                    HollowPipeTransportManager.markDirty(level, pos);
                    return InteractionResult.SUCCESS;
                }

                BooleanProperty prop = getPropertyForDirection(hitFace);
                if (prop != null) {
                    boolean currentVal = state.getValue(prop);
                    if (currentVal) {
                        // Attempting to close this face: ensure at least 2 open faces remain
                        int openCount = (state.getValue(DOWN) ? 1 : 0) + (state.getValue(UP) ? 1 : 0)
                                + (state.getValue(NORTH) ? 1 : 0) + (state.getValue(SOUTH) ? 1 : 0)
                                + (state.getValue(WEST) ? 1 : 0) + (state.getValue(EAST) ? 1 : 0);
                        if (openCount <= 2) {
                            level.playSound(null, pos, SoundEvents.DISPENSER_FAIL, SoundSource.BLOCKS, 0.8F, 1.2F);
                            player.displayClientMessage(new net.minecraft.network.chat.TextComponent("Pipes must have at least 2 open ends!"), true);
                            return InteractionResult.SUCCESS;
                        }
                    }

                    BlockState newState = state.setValue(prop, !currentVal);
                    level.setBlock(pos, newState, 3);
                    level.playSound(null, pos, SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.6F, !currentVal ? 1.2F : 0.8F);
                    HollowPipeTransportManager.markDirty(level, pos);
                    return InteractionResult.SUCCESS;
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        boolean isEmptyBucket = held.is(Items.BUCKET)
                || (held.getItem() instanceof BucketItem bi && bi.getFluid() == Fluids.EMPTY)
                || (FluidUtil.getFluidHandler(held).isPresent() && FluidUtil.getFluidContained(held).orElse(FluidStack.EMPTY).isEmpty());

        // 2. Empty Bucket interaction to retrieve fluid (SOURCE ONLY)
        Fluid sourceFluid = getSourceFluid(state, be);
        if (isEmptyBucket && sourceFluid != Fluids.EMPTY) {
            if (!level.isClientSide) {
                if (hollowBe != null) {
                    hollowBe.setFluidType("none");
                    hollowBe.setLavaTicks(0);
                    hollowBe.setChanged();
                }
                state = state.setValue(WATERLOGGED, false).setValue(LAVA_LOGGED, false);
                level.setBlock(pos, state, 3);

                ItemStack filledBucket = getFilledBucketForFluid(sourceFluid);
                if (!player.getAbilities().instabuild) {
                    held.shrink(1);
                    if (held.isEmpty()) {
                        player.setItemInHand(hand, filledBucket);
                    } else if (!player.getInventory().add(filledBucket)) {
                        player.drop(filledBucket, false);
                    }
                }

                SoundEvent sound = sourceFluid.getAttributes().getFillSound();
                if (sound == null) sound = (sourceFluid == Fluids.LAVA) ? SoundEvents.BUCKET_FILL_LAVA : SoundEvents.BUCKET_FILL;
                level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);

                HollowPipeTransportManager.onBucketUsed(level, pos, state);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        // 3. Filled Fluid Bucket interaction to deposit fluid
        Fluid fluidInBucket = getFluidFromItem(held);
        if (fluidInBucket != Fluids.EMPTY) {
            if (sourceFluid != Fluids.EMPTY) {
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
            if (!level.isClientSide) {
                boolean isWater = (fluidInBucket == Fluids.WATER);
                boolean isLava = (fluidInBucket == Fluids.LAVA);

                state = state.setValue(WATERLOGGED, isWater).setValue(LAVA_LOGGED, isLava);
                level.setBlock(pos, state, 3);

                if (hollowBe != null) {
                    if (isWater) {
                        hollowBe.setLavaTicks(0);
                    } else if (isLava) {
                        hollowBe.setFluidType("lava");
                        hollowBe.setLavaTicks(0);
                    } else {
                        ResourceLocation key = ForgeRegistries.FLUIDS.getKey(fluidInBucket);
                        if (key != null) hollowBe.setFluidType(key.toString());
                        hollowBe.setLavaTicks(0);
                    }
                    hollowBe.setChanged();
                }

                if (!player.getAbilities().instabuild) {
                    ItemStack emptyContainer = held.hasContainerItem() ? held.getContainerItem() : new ItemStack(Items.BUCKET);
                    held.shrink(1);
                    if (held.isEmpty()) {
                        player.setItemInHand(hand, emptyContainer);
                    } else if (!player.getInventory().add(emptyContainer)) {
                        player.drop(emptyContainer, false);
                    }
                }

                SoundEvent sound = fluidInBucket.getAttributes().getEmptySound();
                if (sound == null) sound = (fluidInBucket == Fluids.LAVA) ? SoundEvents.BUCKET_EMPTY_LAVA : SoundEvents.BUCKET_EMPTY;
                level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);

                // The transport manager chooses the downstream endpoint.  Do not
                // spread from every physical opening here, because the inlet side
                // must remain an inlet rather than creating water beside the pipe.
                HollowPipeTransportManager.onBucketUsed(level, pos, state);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        // 4. Modded Forge Fluid Handler interaction (Tanks, Universal Buckets)
        if (hollowBe != null && FluidUtil.getFluidHandler(held).isPresent()) {
            boolean interactSuccess = FluidUtil.interactWithFluidHandler(player, hand, level, pos, hitFace);
            if (interactSuccess) {
                if (!level.isClientSide) {
                    hollowBe.setChanged();
                    level.sendBlockUpdated(pos, state, state, 3);
                    HollowPipeTransportManager.onBucketUsed(level, pos, state);
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    public ItemStack pickupBlock(LevelAccessor level, BlockPos pos, BlockState state) {
        BlockEntity be = level.getBlockEntity(pos);
        HollowLogBlockEntity hollowBe = be instanceof HollowLogBlockEntity h ? h : null;
        Fluid sourceFluid = getSourceFluid(state, be);
        if (sourceFluid != Fluids.EMPTY) {
            if (hollowBe != null) {
                hollowBe.setFluidType("none");
                hollowBe.setLavaTicks(0);
                hollowBe.setChanged();
            }
            level.setBlock(pos, state.setValue(WATERLOGGED, false).setValue(LAVA_LOGGED, false), 3);
            if (level instanceof Level lvl) {
                HollowPipeTransportManager.onBucketUsed(lvl, pos, state);
            }
            return getFilledBucketForFluid(sourceFluid);
        }
        return ItemStack.EMPTY;
    }

    /**
     * Spreads fluid to the world from all open endpoints of this pipe.
     *
     * @param dist The BFS horizontal distance of this pipe from the source. Used to calculate
     *             the outflow flow level so the water continues at the correct vanilla level
     *             rather than restarting at full strength. At dist=7 (MAX_HORIZONTAL_FLOW),
     *             outflow amount = 0, so no water is placed — matching vanilla's 7-block limit.
     */
    public static void trySpreadToWorld(Level level, BlockPos pos, BlockState state, Fluid fluid, int dist) {
        trySpreadToWorld(level, pos, state, fluid, dist, null);
    }

    /**
     * Spreads only through transport-approved exit faces. A null set preserves
     * the legacy behaviour for non-pipe callers; steel-pipe transport always
     * supplies its downstream directions.
     */
    public static void trySpreadToWorld(Level level, BlockPos pos, BlockState state, Fluid fluid, int dist,
                                        @Nullable Set<Direction> allowedDirections) {
        if (fluid == null || fluid == Fluids.EMPTY || level.isClientSide) return;
        int outflowAmount = WaterPipeTransport.MAX_HORIZONTAL_FLOW - dist; // 7-dist
        for (Direction dir : Direction.values()) {
            if (dir == Direction.UP) continue;
            if (allowedDirections != null && !allowedDirections.contains(dir)) continue;
            if (isOpenEndpoint(state, dir)) {
                // Downward exits (waterfalls) always use full strength because the BFS
                // resets the distance counter on a vertical drop, just like vanilla does.
                int amount = (dir == Direction.DOWN) ? WaterPipeTransport.MAX_HORIZONTAL_FLOW : outflowAmount;
                if (amount <= 0) continue; // pipe has used up all 7 horizontal blocks
                spreadToWorldBlock(level, pos.relative(dir), fluid, amount);
            }
        }
    }

    /**
     * Places a flowing fluid block at a single neighbor position.
     *
     * @param amount The flow amount (1–7). Amount 7 = strongest flow (adjacent to source),
     *               amount 1 = weakest flow. This continues the vanilla flow chain rather
     *               than restarting it at full strength from the pipe exit.
     */
    public static void spreadToWorldBlock(Level level, BlockPos neighborPos, Fluid fluid, int amount) {
        if (amount <= 0) return;
        BlockState neighborState = level.getBlockState(neighborPos);
        if (neighborState.getBlock() instanceof HollowLogBlock || neighborState.getBlock() instanceof HollowPipeBlock) {
            return;
        }

        if (fluid instanceof FlowingFluid flowing) {
            BlockState fluidBlock = flowing.getFlowing(amount, false).createLegacyBlock();
            if (!fluidBlock.isAir()) {
                if (neighborState.isAir() || neighborState.canBeReplaced(fluid)
                        || (neighborState.getBlock() instanceof LiquidBlock && !neighborState.getFluidState().isSource())) {
                    if (!neighborState.equals(fluidBlock)) {
                        level.setBlock(neighborPos, fluidBlock, 3);
                    }
                    // Always reschedule so vanilla fluid tick keeps the block alive and spreads it further
                    level.scheduleTick(neighborPos, flowing, flowing.getTickDelay(level));
                }
            }
        } else {
            if (neighborState.isAir() || neighborState.canBeReplaced(fluid)
                    || (neighborState.getBlock() instanceof LiquidBlock && !neighborState.getFluidState().isSource())) {
                BlockState fluidBlock = fluid.defaultFluidState().createLegacyBlock();
                if (!fluidBlock.isAir() && !neighborState.equals(fluidBlock)) {
                    level.setBlock(neighborPos, fluidBlock, 3);
                    level.scheduleTick(neighborPos, fluid, fluid.getTickDelay(level));
                }
            }
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
        super.tick(state, level, pos, random);
        boolean hasWater = state.getValue(WATERLOGGED) || state.getValue(WATER_LEVEL) > 0;
        if (hasWater) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof HollowLogBlockEntity hollowBe) {
                PipeFlowState flow = hollowBe.getPipeFlowState();
                if (flow != null && flow.hasWater()) {
                    // Refresh only the endpoint selected by the directed transport
                    // graph. This prevents water from escaping through side walls
                    // or back through the inlet.
                    trySpreadToWorld(level, pos, state, Fluids.WATER, flow.getDistance(), flow.getFlowDirections());
                }
            }
            level.scheduleTick(pos, this, Fluids.WATER.getTickDelay(level));
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (context instanceof EntityCollisionContext entityContext) {
            Entity entity = entityContext.getEntity();
            if (entity instanceof LivingEntity living) {
                if (isHollowLogHolding(living.getMainHandItem()) || isHollowLogHolding(living.getOffhandItem())) {
                    return Shapes.block();
                }
            }
        }
        return getActualShape(state);
    }

    public static boolean hasVerticalChannel(BlockState state) {
        if (state.hasProperty(UP) && state.getValue(UP)) return true;
        if (state.hasProperty(DOWN) && state.getValue(DOWN)) return true;
        if (state.hasProperty(AXIS) && state.getValue(AXIS) == Direction.Axis.Y) {
            boolean north = state.hasProperty(NORTH) && state.getValue(NORTH);
            boolean south = state.hasProperty(SOUTH) && state.getValue(SOUTH);
            boolean west  = state.hasProperty(WEST) && state.getValue(WEST);
            boolean east  = state.hasProperty(EAST) && state.getValue(EAST);
            int count = (north ? 1 : 0) + (south ? 1 : 0) + (west ? 1 : 0) + (east ? 1 : 0);
            return count == 0;
        }
        return false;
    }

    @Override
    public boolean isLadder(BlockState state, net.minecraft.world.level.LevelReader level, BlockPos pos, LivingEntity entity) {
        return hasVerticalChannel(state);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (context instanceof EntityCollisionContext entityContext) {
            Entity entity = entityContext.getEntity();
            if (entity instanceof LivingEntity living && living.isShiftKeyDown()) {
                if (!hasVerticalChannel(state)) {
                    Direction.Axis axis = getPrimaryAxis(state);
                    return switch (axis) {
                        case X -> X_SHAPE_SNEAK;
                        case Z -> Z_SHAPE_SNEAK;
                        default -> Y_SHAPE;
                    };
                }
            }
        }
        return getActualShape(state);
    }

    public static int getConnectCount(BlockState state) {
        return (state.getValue(DOWN) ? 1 : 0) + (state.getValue(UP) ? 1 : 0)
                + (state.getValue(NORTH) ? 1 : 0) + (state.getValue(SOUTH) ? 1 : 0)
                + (state.getValue(WEST) ? 1 : 0) + (state.getValue(EAST) ? 1 : 0);
    }

    public static Direction.Axis getPrimaryAxis(BlockState state) {
        if (state.getValue(DOWN) || state.getValue(UP)) return Direction.Axis.Y;
        if (state.getValue(NORTH) || state.getValue(SOUTH)) return Direction.Axis.Z;
        if (state.getValue(WEST) || state.getValue(EAST)) return Direction.Axis.X;
        return state.getValue(AXIS);
    }

    public VoxelShape getActualShape(BlockState state) {
        int mask = 0;
        if (state.getValue(DOWN))  mask |= (1 << Direction.DOWN.get3DDataValue());
        if (state.getValue(UP))    mask |= (1 << Direction.UP.get3DDataValue());
        if (state.getValue(NORTH)) mask |= (1 << Direction.NORTH.get3DDataValue());
        if (state.getValue(SOUTH)) mask |= (1 << Direction.SOUTH.get3DDataValue());
        if (state.getValue(WEST))  mask |= (1 << Direction.WEST.get3DDataValue());
        if (state.getValue(EAST))  mask |= (1 << Direction.EAST.get3DDataValue());

        if (mask >= 0 && mask < 64) {
            return SHAPES_BY_MASK[mask];
        }
        return Y_SHAPE;
    }

    private static boolean isHollowLogHolding(ItemStack stack) {
        if (stack != null && !stack.isEmpty()) {
            return stack.getItem() instanceof BlockItem || stack.getItem() instanceof BucketItem;
        }
        return false;
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        super.entityInside(state, level, pos, entity);

        // Apply contained water / bubble column physics directly to players and in-world ItemEntity objects
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof HollowLogBlockEntity hollowBe) {
            PipeFlowState flowState = hollowBe.getPipeFlowState();
            if (flowState != null && flowState.hasWater()) {
                BubbleColumnHandler.handleEntityInside(level, pos, state, entity, flowState);
            }
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, Random random) {
        super.animateTick(state, level, pos, random);
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof HollowLogBlockEntity hollowBe) {
            PipeFlowState flowState = hollowBe.getPipeFlowState();
            if (flowState != null && flowState.hasWater()) {
                BubbleColumnHandler.spawnFlowParticles(level, pos, random, flowState);
            }
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        Direction.Axis axis = context.getClickedFace().getAxis();

        Fluid fluidInWorld = (fluidstate != null && fluidstate.isSource()) ? fluidstate.getType() : Fluids.EMPTY;
        if (fluidInWorld != Fluids.EMPTY) {
            PLACED_FLUID.set(fluidInWorld);
        } else {
            PLACED_FLUID.remove();
        }

        boolean isWater = (fluidInWorld == Fluids.WATER);
        boolean isLava = (fluidInWorld == Fluids.LAVA);

        BlockState state = this.defaultBlockState()
                .setValue(AXIS, axis)
                .setValue(WATERLOGGED, isWater)
                .setValue(LAVA_LOGGED, isLava);
        return updateConnections(context.getLevel(), context.getClickedPos(), state);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            Fluid worldFluid = PLACED_FLUID.get();
            PLACED_FLUID.remove();

            if (be instanceof HollowLogBlockEntity hollowBe) {
                if (worldFluid != null && worldFluid != Fluids.EMPTY) {
                    if (worldFluid == Fluids.LAVA) {
                        hollowBe.setFluidType("lava");
                        hollowBe.setLavaTicks(0);
                    } else if (worldFluid != Fluids.WATER) {
                        ResourceLocation key = ForgeRegistries.FLUIDS.getKey(worldFluid);
                        if (key != null) hollowBe.setFluidType(key.toString());
                        hollowBe.setLavaTicks(0);
                    }
                    hollowBe.setChanged();
                } else if (state.getValue(LAVA_LOGGED)) {
                    hollowBe.setFluidType("lava");
                    hollowBe.setLavaTicks(0);
                    hollowBe.setChanged();
                }
            }
            notifyAndRecalculateNeighbors(level, pos);
            HollowPipeTransportManager.onBlockPlaced(level, pos, state);
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            Fluid sourceFluid = getSourceFluid(state, be);

            super.onRemove(state, level, pos, newState, isMoving);
            if (!level.isClientSide) {
                notifyAndRecalculateNeighbors(level, pos);
                HollowPipeTransportManager.onBlockRemoved(level, pos, state);

                if (sourceFluid != null && sourceFluid != Fluids.EMPTY) {
                    BlockState fluidBlock = sourceFluid.defaultFluidState().createLegacyBlock();
                    if (!fluidBlock.isAir() && level.getBlockState(pos).isAir()) {
                        level.setBlock(pos, fluidBlock, 3);
                        level.scheduleTick(pos, sourceFluid, sourceFluid.getTickDelay(level));
                    }
                }
            }
        } else {
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        if (state.getValue(LAVA_LOGGED)) {
            level.scheduleTick(pos, Fluids.LAVA, Fluids.LAVA.getTickDelay(level));
        }
        // Schedule a block tick when carrying water so outflow endpoints stay refreshed
        if (state.getValue(WATER_LEVEL) > 0) {
            level.scheduleTick(pos, this, Fluids.WATER.getTickDelay(level));
        }
        BlockState updated = super.updateShape(state, direction, neighborState, level, pos, neighborPos);
        // Preserve WATER_LEVEL across the updateConnections recalculation
        if (state.getValue(WATER_LEVEL) > 0 && updated.getValue(WATER_LEVEL) == 0) {
            updated = updated.setValue(WATER_LEVEL, state.getValue(WATER_LEVEL));
        }
        updated = updateConnections(level, pos, updated);
        if (!level.isClientSide() && level instanceof Level lvl) {
            HollowPipeTransportManager.onNeighborChanged(lvl, pos, updated, neighborPos);
        }
        return updated;
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
        if (!level.isClientSide) {
            HollowPipeTransportManager.onNeighborChanged(level, pos, state, fromPos);
        }
    }

    public static BooleanProperty getPropertyForDirection(Direction dir) {
        return switch (dir) {
            case DOWN -> DOWN;
            case UP -> UP;
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case WEST -> WEST;
            case EAST -> EAST;
        };
    }

    public BlockState updateConnections(BlockGetter level, BlockPos pos, BlockState state) {
        boolean down  = canConnectTo(level, pos, state, Direction.DOWN);
        boolean up    = canConnectTo(level, pos, state, Direction.UP);
        boolean north = canConnectTo(level, pos, state, Direction.NORTH);
        boolean south = canConnectTo(level, pos, state, Direction.SOUTH);
        boolean west  = canConnectTo(level, pos, state, Direction.WEST);
        boolean east  = canConnectTo(level, pos, state, Direction.EAST);

        return state
                .setValue(DOWN, down)
                .setValue(UP, up)
                .setValue(NORTH, north)
                .setValue(SOUTH, south)
                .setValue(WEST, west)
                .setValue(EAST, east);
    }

    public boolean canConnectTo(BlockGetter level, BlockPos pos, BlockState state, Direction dir) {
        BlockPos neighborPos = pos.relative(dir);
        BlockState neighborState = level.getBlockState(neighborPos);
        Block neighborBlock = neighborState.getBlock();

        Direction.Axis dirAxis = dir.getAxis();
        Direction.Axis myAxis = state.hasProperty(AXIS) ? state.getValue(AXIS) : Direction.Axis.Y;

        if (neighborBlock instanceof HollowPipeBlock) {
            Direction.Axis neighborAxis = neighborState.hasProperty(AXIS) ? neighborState.getValue(AXIS) : Direction.Axis.Y;

            boolean myChannelPointsToNeighbor = (myAxis == dirAxis);
            boolean neighborChannelPointsToMe = (neighborAxis == dirAxis);

            BooleanProperty neighborOppositeProp = getPropertyForDirection(dir.getOpposite());
            boolean neighborAlreadyOpenToMe = neighborState.hasProperty(neighborOppositeProp) && neighborState.getValue(neighborOppositeProp);

            BooleanProperty myProp = getPropertyForDirection(dir);
            boolean myAlreadyOpenToNeighbor = state.hasProperty(myProp) && state.getValue(myProp);

            return myChannelPointsToNeighbor || neighborChannelPointsToMe || neighborAlreadyOpenToMe || myAlreadyOpenToNeighbor;
        }

        if (neighborBlock instanceof PipeBlock) {
            return neighborState.getValue(PipeBlock.AXIS) == dirAxis && (myAxis == dirAxis || (state.hasProperty(getPropertyForDirection(dir)) && state.getValue(getPropertyForDirection(dir))));
        }

        return false;
    }

    private void notifyAndRecalculateNeighbors(Level level, BlockPos pos) {
        for (Direction dir : Direction.values()) {
            BlockPos neighborPos = pos.relative(dir);
            BlockState neighborState = level.getBlockState(neighborPos);
            if (neighborState.getBlock() instanceof HollowPipeBlock pipeBlock) {
                BlockState updated = pipeBlock.updateConnections(level, neighborPos, neighborState);
                if (updated != neighborState) {
                    level.setBlock(neighborPos, updated, 3);
                }
            }
        }
    }

    @Override
    public boolean canPlaceLiquid(BlockGetter level, BlockPos pos, BlockState state, Fluid fluid) {
        // Water in steel pipes is handled as channel transport through the hollow gap.
        // Letting vanilla place water into the block would fill the full block volume
        // and make outside water visually attach to the pipe shell.
        return false;
    }

    @Override
    public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
        return false;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        if (state.getValue(WATERLOGGED) || state.getValue(WATER_LEVEL) > 0) {
            return Fluids.EMPTY.defaultFluidState();
        }
        if (state.getValue(LAVA_LOGGED)) {
            return Fluids.LAVA.getSource(false);
        }
        return super.getFluidState(state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(DOWN, UP, NORTH, SOUTH, WEST, EAST, WATERLOGGED, LAVA_LOGGED, WATER_LEVEL);
    }
}
