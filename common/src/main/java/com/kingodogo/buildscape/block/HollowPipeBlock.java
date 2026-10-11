package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.pipe.transport.BubbleColumnHandler;
import com.kingodogo.buildscape.pipe.transport.HollowPipeTransportManager;
import com.kingodogo.buildscape.pipe.transport.PipeFlowState;
import com.kingodogo.buildscape.pipe.transport.WaterPipeTransport;
import net.minecraft.core.BlockPos;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LiquidBlock;
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

import javax.annotation.Nullable;
import java.util.Set;
public class HollowPipeBlock extends RotatedPillarBlock implements SimpleWaterloggedBlock, EntityBlock, ICommonNeighborAware, ICommonEntityInside, ICommonInteractable, ICommonPlayerDestroy {
    public static final ThreadLocal<Fluid> PLACED_FLUID = new ThreadLocal<>();

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty LAVA_LOGGED = BooleanProperty.create("lava_logged");
    public static final float WATER_SOURCE_VISUAL_HEIGHT = 14.0F / 16.0F;
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
        if (level.isClientSide()) return null;
        return type == ModBlockEntities.HOLLOW_LOG_TYPE
                ? (lvl, pos, st, be) -> HollowLogBlockEntity.serverTick(lvl, pos, st, (HollowLogBlockEntity) be)
                : null;
    }

    public static Fluid getSourceFluid(BlockState state, @Nullable BlockEntity be) {
        if (state.hasProperty(WATERLOGGED) && state.getValue(WATERLOGGED)) {
            return Fluids.WATER;
        }
        if (state.hasProperty(LAVA_LOGGED) && state.getValue(LAVA_LOGGED)) {
            return Fluids.LAVA;
        }
        if (be instanceof HollowLogBlockEntity hollowBe) {
            String ft = hollowBe.getFluidType();
            if ("water".equals(ft)) return Fluids.WATER;
            if ("lava".equals(ft)) return Fluids.LAVA;
            if (!"none".equals(ft) && !ft.isEmpty()) {
                Fluid found = Services.PLATFORM.getFluid(CommonId.tryParse(ft));
                if (found != null && found != Fluids.EMPTY) {
                    return found;
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
        if (state.getBlock() instanceof HollowPipeBlock && be instanceof HollowLogBlockEntity hollowBe) {
            PipeFlowState flow = hollowBe.getPipeFlowState();
            if (flow != null && flow.hasFluid()) {
                return com.kingodogo.buildscape.pipe.transport.WorldPipeTopologyAccess.fluidById(flow.getFluidId());
            }
        }
        return Fluids.EMPTY;
    }

    public static Fluid getFluidFromItem(ItemStack stack) {
        if (stack.isEmpty()) return Fluids.EMPTY;
        if (stack.getItem() instanceof BucketItem && !(stack.getItem() instanceof MobBucketItem)) {
            return Services.PLATFORM.getBucketFluid(stack.getItem());
        }
        return Fluids.EMPTY;
    }

    public static ItemStack getFilledBucketForFluid(Fluid fluid) {
        if (fluid == Fluids.WATER) return new ItemStack(Items.WATER_BUCKET);
        if (fluid == Fluids.LAVA) return new ItemStack(Items.LAVA_BUCKET);
        Item bucket = fluid.getBucket();
        if (bucket != null && bucket != Items.AIR) {
            return new ItemStack(bucket);
        }
        return new ItemStack(Items.BUCKET);
    }

    public static boolean isOpenEndpoint(BlockState state, Direction dir) {
        if (dir == null || !(state.getBlock() instanceof HollowPipeBlock)) {
            return false;
        }
        BooleanProperty prop = getPropertyForDirection(dir);
        if (prop != null && state.hasProperty(prop) && state.getValue(prop)) {
            return false;
        }
        int connections = getConnectCount(state);
        if (connections == 0) {
            return dir.getAxis() == state.getValue(AXIS);
        }
        return connections == 1 && dir.getAxis() == getPrimaryAxis(state);
    }

    @Override
    public InteractionResult onInteract(Level level, BlockPos pos, BlockState state, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);
        BlockEntity be = level.getBlockEntity(pos);
        HollowLogBlockEntity hollowBe = be instanceof HollowLogBlockEntity h ? h : null;
        Fluid containedFluid = getContainedFluid(state, be);

        CommonId heldId = Services.PLATFORM.getItemId(held.getItem());
        if (held.getItem().toString().contains("wrench") || (heldId != null && "buildscape:wrench".equals(heldId.toString()))) {
            if (!player.isShiftKeyDown()) {
                return InteractionResult.PASS;
            }
            if (!level.isClientSide()) {
                Direction.Axis currentAxis = state.getValue(AXIS);
                Direction.Axis nextAxis = switch (currentAxis) {
                    case Y -> Direction.Axis.Z;
                    case Z -> Direction.Axis.X;
                    case X -> Direction.Axis.Y;
                };
                setAxisAndRefresh(level, pos, state, nextAxis);
                Services.PLATFORM.playAnvilUse(level, pos);
            }
            return InteractionResult.SUCCESS;
        }

        boolean isEmptyBucket = held.is(Items.BUCKET)
                || (held.getItem() instanceof BucketItem && Services.PLATFORM.getBucketFluid(held.getItem()) == Fluids.EMPTY);

        Fluid sourceFluid = getSourceFluid(state, be);
        if (isEmptyBucket && sourceFluid != Fluids.EMPTY) {
            if (!level.isClientSide()) {
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

                Services.PLATFORM.playBucketFillFluid(level, pos, sourceFluid == Fluids.LAVA);

                HollowPipeTransportManager.onBucketUsed(level, pos, state);
            }
            return InteractionResult.SUCCESS;
        }

        Fluid fluidInBucket = getFluidFromItem(held);
        if (fluidInBucket != Fluids.EMPTY) {
            if (containedFluid != Fluids.EMPTY) {
                return InteractionResult.SUCCESS;
            }
            if (!level.isClientSide()) {
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
                        CommonId key = Services.PLATFORM.getFluidId(fluidInBucket);
                        if (key != null) hollowBe.setFluidType(key.toString());
                        hollowBe.setLavaTicks(0);
                    }
                    hollowBe.setChanged();
                }

                if (!player.getAbilities().instabuild) {
                    ItemStack emptyContainer = new ItemStack(Items.BUCKET);
                    held.shrink(1);
                    if (held.isEmpty()) {
                        player.setItemInHand(hand, emptyContainer);
                    } else if (!player.getInventory().add(emptyContainer)) {
                        player.drop(emptyContainer, false);
                    }
                }

                Services.PLATFORM.playBucketEmptyFluid(level, pos, fluidInBucket == Fluids.LAVA);

                HollowPipeTransportManager.onBucketUsed(level, pos, state);
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

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

    public static void trySpreadToWorld(Level level, BlockPos pos, BlockState state, Fluid fluid, int dist) {
        trySpreadToWorld(level, pos, state, fluid, dist, null);
    }

    public static void trySpreadToWorld(Level level, BlockPos pos, BlockState state, Fluid fluid, int dist,
                                        @Nullable Set<Direction> allowedDirections) {
        if (fluid == null || fluid == Fluids.EMPTY || level.isClientSide()) return;
        int outflowAmount = WaterPipeTransport.MAX_HORIZONTAL_FLOW - dist;
        for (Direction dir : Direction.values()) {
            if (dir == Direction.UP) continue;
            if (allowedDirections != null && !allowedDirections.contains(dir)) continue;
            boolean openEndpoint = state.getBlock() instanceof HollowLogBlock
                    ? HollowLogBlock.isOpenEnd(state, dir)
                    : isOpenEndpoint(state, dir);
            if (openEndpoint) {
                boolean falling = dir == Direction.DOWN;
                int amount = falling ? 8 : outflowAmount;
                if (amount <= 0) continue;
                spreadToWorldBlock(level, pos.relative(dir), fluid, amount, falling);
            }
        }
    }

    public static void spreadToWorldBlock(Level level, BlockPos neighborPos, Fluid fluid, int amount) {
        spreadToWorldBlock(level, neighborPos, fluid, amount, false);
    }

    private static void spreadToWorldBlock(Level level, BlockPos neighborPos, Fluid fluid, int amount, boolean falling) {
        if (amount <= 0) return;
        BlockState neighborState = level.getBlockState(neighborPos);
        if (neighborState.getBlock() instanceof HollowLogBlock || neighborState.getBlock() instanceof HollowPipeBlock) {
            return;
        }

        if (fluid instanceof FlowingFluid flowing) {
            BlockState fluidBlock = flowing.getFlowing(amount, falling).createLegacyBlock();
            if (!fluidBlock.isAir()) {
                if (neighborState.isAir() || neighborState.canBeReplaced(fluid)
                        || (neighborState.getBlock() instanceof LiquidBlock && !neighborState.getFluidState().isSource())) {
                    if (!neighborState.equals(fluidBlock)) {
                        level.setBlock(neighborPos, fluidBlock, 3);
                    }
                    Fluid placedFluid = fluidBlock.getFluidState().getType();
                    level.scheduleTick(neighborPos, placedFluid, placedFluid.getTickDelay(level));
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

    public void onBlockTick(ServerLevel level, BlockPos pos, BlockState state) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof HollowLogBlockEntity hollowBe) {
            PipeFlowState flow = hollowBe.getPipeFlowState();
            if (flow != null && flow.hasFluid()) {
                Fluid fluid = com.kingodogo.buildscape.pipe.transport.WorldPipeTopologyAccess.fluidById(flow.getFluidId());
                trySpreadToWorld(level, pos, state, fluid, flow.getDistance(), flow.getFlowDirections());
                level.scheduleTick(pos, this, fluid.getTickDelay(level));
            }
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
        int count = getConnectCount(state);
        if (count == 0) {
            Direction.Axis axis = state.hasProperty(AXIS) ? state.getValue(AXIS) : Direction.Axis.Y;
            return switch (axis) {
                case X -> X_SHAPE;
                case Z -> Z_SHAPE;
                default -> Y_SHAPE;
            };
        }

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
    public void onEntityInside(Level level, BlockPos pos, BlockState state, Entity entity) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof HollowLogBlockEntity hollowBe) {
            PipeFlowState flowState = hollowBe.getPipeFlowState();
            if (flowState != null && flowState.hasWater()) {
                BubbleColumnHandler.handleEntityInside(level, pos, state, entity, flowState);
            }
        }
    }

    public void onAnimateTick(Level level, BlockPos pos, BlockState state) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof HollowLogBlockEntity hollowBe) {
            PipeFlowState flowState = hollowBe.getPipeFlowState();
            if (flowState != null && flowState.hasWater()) {
                BubbleColumnHandler.spawnFlowParticles(level, pos, flowState);
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
        if (!level.isClientSide()) {
            BlockEntity be = level.getBlockEntity(pos);
            Fluid worldFluid = PLACED_FLUID.get();
            PLACED_FLUID.remove();

            if (be instanceof HollowLogBlockEntity hollowBe) {
                if (worldFluid != null && worldFluid != Fluids.EMPTY) {
                    if (worldFluid == Fluids.LAVA) {
                        hollowBe.setFluidType("lava");
                        hollowBe.setLavaTicks(0);
                    } else if (worldFluid != Fluids.WATER) {
                        CommonId key = Services.PLATFORM.getFluidId(worldFluid);
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
    public void onPlayerDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        BlockEntity be = level.getBlockEntity(pos);
        Fluid sourceFluid = getSourceFluid(state, be);

        if (!level.isClientSide()) {
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
    }

    @Override
    public void onNeighborUpdate(Level level, BlockPos pos, BlockState state, Block neighborBlock, BlockPos neighborPos, boolean isMoving) {
        if (!level.isClientSide()) {
            HollowPipeTransportManager.onNeighborChanged(level, pos, state, pos);
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

    public static BlockState updateConnections(BlockGetter level, BlockPos pos, BlockState state) {
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

    public static boolean canConnectTo(BlockGetter level, BlockPos pos, BlockState state, Direction dir) {
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

        if (neighborBlock instanceof HollowLogBlock) {
            return false;
        }

        if (neighborBlock instanceof PipeBlock) {
            return neighborState.getValue(PipeBlock.AXIS) == dirAxis && (myAxis == dirAxis || (state.hasProperty(getPropertyForDirection(dir)) && state.getValue(getPropertyForDirection(dir))));
        }

        return false;
    }

    public void setAxisAndRefresh(Level level, BlockPos pos, BlockState state, Direction.Axis axis) {
        BlockState rotated = updateConnections(level, pos, state.setValue(AXIS, axis));
        level.setBlock(pos, rotated, 3);
        notifyAndRecalculateNeighbors(level, pos);
        HollowPipeTransportManager.markDirty(level, pos);
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
            if (neighborState.getBlock() instanceof HollowLogBlock) {
                HollowPipeTransportManager.markDirty(level, neighborPos);
            }
        }
    }

    public boolean canPlaceLiquid(BlockGetter level, BlockPos pos, BlockState state, Fluid fluid) {
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
