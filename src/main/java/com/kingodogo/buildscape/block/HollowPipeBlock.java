package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.fluid.ModFluids;
import com.kingodogo.buildscape.item.ModItems;
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
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Random;

public class HollowPipeBlock extends RotatedPillarBlock implements SimpleWaterloggedBlock, EntityBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty LAVA_LOGGED = BooleanProperty.create("lava_logged");

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

    private static final VoxelShape Y_SHAPE = Shapes.or(BOX_NORTH, BOX_SOUTH, BOX_WEST, BOX_EAST);
    private static final VoxelShape X_SHAPE = Shapes.or(BOX_NORTH, BOX_SOUTH, BOX_DOWN, BOX_UP);
    private static final VoxelShape Z_SHAPE = Shapes.or(BOX_WEST, BOX_EAST, BOX_DOWN, BOX_UP);

    private static final VoxelShape X_SHAPE_SNEAK = Shapes.or(BOX_NORTH, BOX_SOUTH, BOX_DOWN);
    private static final VoxelShape Z_SHAPE_SNEAK = Shapes.or(BOX_WEST, BOX_EAST, BOX_DOWN);

    private static final VoxelShape FRAME_Z1 = Block.box(0, 0, 0, 2, 2, 16);
    private static final VoxelShape FRAME_Z2 = Block.box(14, 0, 0, 16, 2, 16);
    private static final VoxelShape FRAME_Z3 = Block.box(0, 14, 0, 2, 16, 16);
    private static final VoxelShape FRAME_Z4 = Block.box(14, 14, 0, 16, 16, 16);

    private static final VoxelShape FRAME_Y1 = Block.box(0, 2, 0, 2, 14, 2);
    private static final VoxelShape FRAME_Y2 = Block.box(14, 2, 0, 16, 14, 2);
    private static final VoxelShape FRAME_Y3 = Block.box(0, 2, 14, 2, 14, 16);
    private static final VoxelShape FRAME_Y4 = Block.box(14, 2, 14, 16, 14, 16);

    private static final VoxelShape FRAME_X1 = Block.box(2, 0, 0, 14, 2, 2);
    private static final VoxelShape FRAME_X2 = Block.box(2, 0, 14, 14, 2, 16);
    private static final VoxelShape FRAME_X3 = Block.box(2, 14, 0, 14, 16, 2);
    private static final VoxelShape FRAME_X4 = Block.box(2, 14, 14, 14, 16, 16);

    private static final VoxelShape SIX_WAY_SHAPE = Shapes.or(
            FRAME_Z1, FRAME_Z2, FRAME_Z3, FRAME_Z4,
            FRAME_Y1, FRAME_Y2, FRAME_Y3, FRAME_Y4,
            FRAME_X1, FRAME_X2, FRAME_X3, FRAME_X4
    );

    public HollowPipeBlock(Properties properties) {
        super(properties.noOcclusion());
        this.registerDefaultState(this.defaultBlockState()
                .setValue(AXIS, Direction.Axis.Y)
                .setValue(DOWN, false)
                .setValue(UP, false)
                .setValue(NORTH, false)
                .setValue(SOUTH, false)
                .setValue(WEST, false)
                .setValue(EAST, false)
                .setValue(WATERLOGGED, false)
                .setValue(LAVA_LOGGED, false));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HollowLogBlockEntity(pos, state);
    }

    public static Fluid getContainedFluid(BlockState state, @Nullable BlockEntity be) {
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
                ResourceLocation rl = ResourceLocation.tryParse(ft);
                if (rl != null && ForgeRegistries.FLUIDS.containsKey(rl)) {
                    return ForgeRegistries.FLUIDS.getValue(rl);
                }
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
        if (state.getValue(getPropertyForDirection(dir))) {
            return false;
        }
        int count = (state.getValue(DOWN) ? 1 : 0) + (state.getValue(UP) ? 1 : 0)
                  + (state.getValue(NORTH) ? 1 : 0) + (state.getValue(SOUTH) ? 1 : 0)
                  + (state.getValue(WEST) ? 1 : 0) + (state.getValue(EAST) ? 1 : 0);
        if (count == 0) {
            return dir.getAxis() == state.getValue(AXIS);
        }
        if (count == 1) {
            return dir.getAxis() == getPrimaryAxis(state);
        }
        return false;
    }

    public static final ThreadLocal<Fluid> PLACED_FLUID = new ThreadLocal<>();

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);
        BlockEntity be = level.getBlockEntity(pos);
        HollowLogBlockEntity hollowBe = be instanceof HollowLogBlockEntity h ? h : null;
        Fluid containedFluid = getContainedFluid(state, be);

        // 1. Empty Bucket interaction to retrieve any fluid
        if (held.is(Items.BUCKET) && containedFluid != Fluids.EMPTY) {
            if (!level.isClientSide) {
                state = state.setValue(WATERLOGGED, false).setValue(LAVA_LOGGED, false);
                level.setBlock(pos, state, 3);
                if (hollowBe != null) {
                    hollowBe.setFluidType("none");
                    hollowBe.setLavaTicks(0);
                    hollowBe.setChanged();
                }

                ItemStack filledBucket = getFilledBucketForFluid(containedFluid);
                if (!player.getAbilities().instabuild) {
                    held.shrink(1);
                    if (held.isEmpty()) {
                        player.setItemInHand(hand, filledBucket);
                    } else if (!player.getInventory().add(filledBucket)) {
                        player.drop(filledBucket, false);
                    }
                }

                SoundEvent sound = containedFluid.getAttributes().getFillSound();
                if (sound == null) sound = (containedFluid == Fluids.LAVA) ? SoundEvents.BUCKET_FILL_LAVA : SoundEvents.BUCKET_FILL;
                level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        // 2. Filled Fluid Bucket / Container interaction to deposit fluid
        Fluid fluidInBucket = getFluidFromItem(held);
        if (fluidInBucket != Fluids.EMPTY) {
            if (containedFluid != Fluids.EMPTY) {
                // NEVER allow replacing an existing fluid in a pipe! Only one fluid type per blockspace.
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
            if (!level.isClientSide) {
                boolean isWater = (fluidInBucket == Fluids.WATER);
                boolean isLava = (fluidInBucket == Fluids.LAVA);

                state = state.setValue(WATERLOGGED, isWater).setValue(LAVA_LOGGED, isLava);
                level.setBlock(pos, state, 3);

                if (hollowBe != null) {
                    String fKey = isWater ? "water" : (isLava ? "lava" : ForgeRegistries.FLUIDS.getKey(fluidInBucket).toString());
                    hollowBe.setFluidType(fKey);
                    hollowBe.setLavaTicks(0);
                    hollowBe.setChanged();
                }

                tryFlowOut(level, pos, state, fluidInBucket);

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
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return InteractionResult.PASS;
    }

    public static void tryFlowOut(Level level, BlockPos pos, BlockState state, Fluid fluid) {
        if (fluid == null || fluid == Fluids.EMPTY || level.isClientSide) return;
        for (Direction dir : Direction.values()) {
            tryFlowOutTo(level, pos, state, dir, fluid);
        }
    }

    public static void tryFlowOutTo(Level level, BlockPos pos, BlockState state, Direction dir, Fluid fluid) {
        if (fluid == null || fluid == Fluids.EMPTY || level.isClientSide || dir == Direction.UP) return;
        if (isOpenEndpoint(state, dir)) {
            BlockPos neighborPos = pos.relative(dir);
            BlockState neighborState = level.getBlockState(neighborPos);

            // Flow out of open endpoint into the world
            if (neighborState.isAir() || neighborState.canBeReplaced(fluid) || (neighborState.getBlock() instanceof LiquidBlock && !neighborState.getFluidState().isSource())) {
                BlockState fluidBlock = fluid.defaultFluidState().createLegacyBlock();
                if (!fluidBlock.isAir()) {
                    level.setBlock(neighborPos, fluidBlock, 3);
                    level.scheduleTick(neighborPos, fluid, fluid.getTickDelay(level));
                }
            }
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
        super.tick(state, level, pos, random);
        BlockEntity be = level.getBlockEntity(pos);
        Fluid fluid = getContainedFluid(state, be);
        tryFlowOut(level, pos, state, fluid);
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
        boolean down  = state.getValue(DOWN);
        boolean up    = state.getValue(UP);
        boolean north = state.getValue(NORTH);
        boolean south = state.getValue(SOUTH);
        boolean west  = state.getValue(WEST);
        boolean east  = state.getValue(EAST);

        int count = (down ? 1 : 0) + (up ? 1 : 0) + (north ? 1 : 0)
                  + (south ? 1 : 0) + (west ? 1 : 0) + (east ? 1 : 0);

        if (count == 0) {
            Direction.Axis axis = state.getValue(AXIS);
            return switch (axis) {
                case X -> X_SHAPE;
                case Z -> Z_SHAPE;
                default -> Y_SHAPE;
            };
        }

        if (count == 1) {
            Direction.Axis axis = getPrimaryAxis(state);
            return switch (axis) {
                case X -> X_SHAPE;
                case Z -> Z_SHAPE;
                default -> Y_SHAPE;
            };
        }

        VoxelShape shape = Shapes.empty();
        if (!north) shape = Shapes.or(shape, BOX_NORTH);
        if (!south) shape = Shapes.or(shape, BOX_SOUTH);
        if (!west)  shape = Shapes.or(shape, BOX_WEST);
        if (!east)  shape = Shapes.or(shape, BOX_EAST);
        if (!down)  shape = Shapes.or(shape, BOX_DOWN);
        if (!up)    shape = Shapes.or(shape, BOX_UP);
        return shape.isEmpty() ? SIX_WAY_SHAPE : shape;
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
        if (entity instanceof Player player) {
            if (hasVerticalChannel(state)) {
                // Vertical pipes and intersections with vertical channels act as ladders, NOT crawling
                if (player.getForcedPose() == Pose.SWIMMING) {
                    player.setForcedPose(null);
                }
            } else {
                // Horizontal-only pipe: force crawling when sneaking, already crawling, or entering the cavity
                if (player.isShiftKeyDown() || player.getPose() == Pose.SWIMMING || isEnteringHorizontalCavity(player, pos)) {
                    player.setPose(Pose.SWIMMING);
                    player.setForcedPose(Pose.SWIMMING);
                }
            }
        }
    }

    private boolean isEnteringHorizontalCavity(Player player, BlockPos pos) {
        double px = player.getX();
        double py = player.getY();
        double pz = player.getZ();
        return px >= pos.getX() - 0.1 && px <= pos.getX() + 1.1 &&
               py >= pos.getY() - 0.1 && py <= pos.getY() + 1.1 &&
               pz >= pos.getZ() - 0.1 && pz <= pos.getZ() + 1.1;
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
                    if (worldFluid == Fluids.WATER) {
                        hollowBe.setFluidType("water");
                    } else if (worldFluid == Fluids.LAVA) {
                        hollowBe.setFluidType("lava");
                        hollowBe.setLavaTicks(0);
                    } else {
                        String fKey = ForgeRegistries.FLUIDS.getKey(worldFluid).toString();
                        hollowBe.setFluidType(fKey);
                        hollowBe.setLavaTicks(0);
                    }
                    hollowBe.setChanged();
                } else if (state.getValue(LAVA_LOGGED)) {
                    hollowBe.setFluidType("lava");
                    hollowBe.setLavaTicks(0);
                    hollowBe.setChanged();
                } else if (state.getValue(WATERLOGGED)) {
                    hollowBe.setFluidType("water");
                    hollowBe.setChanged();
                }
            }
            Fluid fluid = getContainedFluid(state, be);
            tryFlowOut(level, pos, state, fluid);
            notifyAndRecalculateNeighbors(level, pos);
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            Fluid loggedFluid = getContainedFluid(state, be);

            super.onRemove(state, level, pos, newState, isMoving);
            if (!level.isClientSide) {
                notifyAndRecalculateNeighbors(level, pos);

                // When broken, drop pipe and place fluid in world in flowing/source state
                if (loggedFluid != null && loggedFluid != Fluids.EMPTY) {
                    BlockState fluidBlock = loggedFluid.defaultFluidState().createLegacyBlock();
                    if (!fluidBlock.isAir() && level.getBlockState(pos).isAir()) {
                        level.setBlock(pos, fluidBlock, 3);
                        level.scheduleTick(pos, loggedFluid, loggedFluid.getTickDelay(level));
                    }
                }
            }
        } else {
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        BlockState updated = super.updateShape(state, direction, neighborState, level, pos, neighborPos);
        updated = updateConnections(level, pos, updated);
        if (!level.isClientSide() && direction != Direction.UP && level instanceof Level lvl) {
            if (isOpenEndpoint(updated, direction) && (neighborState.isAir() || neighborState.canBeReplaced(Fluids.WATER))) {
                Fluid fluid = getContainedFluid(updated, lvl.getBlockEntity(pos));
                if (fluid != Fluids.EMPTY) {
                    tryFlowOutTo(lvl, pos, updated, direction, fluid);
                }
            }
        }
        return updated;
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

        // 1. Compatible HollowPipeBlock
        if (neighborBlock instanceof HollowPipeBlock) {
            Direction.Axis neighborAxis = neighborState.hasProperty(AXIS) ? neighborState.getValue(AXIS) : Direction.Axis.Y;

            // This block's internal channel points in direction dir
            boolean myChannelPointsToNeighbor = (myAxis == dirAxis);

            // Neighbor's internal channel points back toward this block
            boolean neighborChannelPointsToMe = (neighborAxis == dirAxis);

            // Neighbor is already an active branch/junction facing this block
            BooleanProperty neighborOppositeProp = getPropertyForDirection(dir.getOpposite());
            boolean neighborAlreadyOpenToMe = neighborState.hasProperty(neighborOppositeProp) && neighborState.getValue(neighborOppositeProp);

            // This block already has an active branch in direction dir
            BooleanProperty myProp = getPropertyForDirection(dir);
            boolean myAlreadyOpenToNeighbor = state.hasProperty(myProp) && state.getValue(myProp);

            return myChannelPointsToNeighbor || neighborChannelPointsToMe || neighborAlreadyOpenToMe || myAlreadyOpenToNeighbor;
        }

        // 2. Solid PipeBlock along its configured AXIS
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
        return false;
    }

    @Override
    public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
        return false;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return Fluids.EMPTY.defaultFluidState();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(DOWN, UP, NORTH, SOUTH, WEST, EAST, WATERLOGGED, LAVA_LOGGED);
    }
}
