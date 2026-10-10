package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.pipe.transport.HollowPipeTransportManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
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
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.StainedGlassBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
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
import java.util.Collections;
import java.util.List;
public class HollowLogBlock extends RotatedPillarBlock implements EntityBlock, SimpleWaterloggedBlock, ICommonNeighborAware, ICommonInteractable, ICommonPlayerDestroy {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    /** Shared with HollowPipeBlock: property lookups are by instance, and the shared fluid code reads this one on logs too. */
    public static final BooleanProperty LAVA_LOGGED = HollowPipeBlock.LAVA_LOGGED;
    public static final BooleanProperty HAS_GLASS_NEG = BooleanProperty.create("glass_neg");
    public static final BooleanProperty HAS_GLASS_POS = BooleanProperty.create("glass_pos");
    public static final BooleanProperty HAS_DECORATION = BooleanProperty.create("decoration");

    private static final VoxelShape Y_SHAPE = Shapes.join(
            Shapes.block(),
            Block.box(2, 0, 2, 14, 16, 14),
            BooleanOp.ONLY_FIRST
    );
    private static final VoxelShape X_SHAPE = Shapes.join(
            Shapes.block(),
            Block.box(0, 2, 2, 16, 14, 14),
            BooleanOp.ONLY_FIRST
    );
    private static final VoxelShape Z_SHAPE = Shapes.join(
            Shapes.block(),
            Block.box(2, 2, 0, 14, 14, 16),
            BooleanOp.ONLY_FIRST
    );

    public HollowLogBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(AXIS, Direction.Axis.Y)
                .setValue(WATERLOGGED, false)
                .setValue(LAVA_LOGGED, false)
                .setValue(HAS_GLASS_NEG, false)
                .setValue(HAS_GLASS_POS, false)
                .setValue(HAS_DECORATION, false));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HollowLogBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        return type == ModBlockEntities.HOLLOW_LOG_TYPE
                ? (lvl, pos, st, be) -> HollowLogBlockEntity.serverTick(lvl, pos, st, (HollowLogBlockEntity) be)
                : null;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (context instanceof EntityCollisionContext entityContext) {
            Entity entity = entityContext.getEntity();
            if (entity instanceof LivingEntity living) {
                if (isHollowLogHolding(living.getMainHandItem()) || isHollowLogHolding(living.getOffhandItem())
                        || isValidDecorationHolding(living.getMainHandItem(), state.getValue(AXIS), state)
                        || isValidDecorationHolding(living.getOffhandItem(), state.getValue(AXIS), state)) {
                    return Shapes.block();
                }
            }
        }
        return getActualShape(state);
    }

    public static boolean isOpenEnd(BlockState state, Direction dir) {
        if (dir == null || !(state.getBlock() instanceof HollowLogBlock)) return false;
        Direction.Axis axis = state.getValue(AXIS);
        if (dir.getAxis() != axis) return false;
        boolean isNeg = (dir == Direction.WEST || dir == Direction.NORTH || dir == Direction.DOWN);
        return isNeg ? !state.getValue(HAS_GLASS_NEG) : !state.getValue(HAS_GLASS_POS);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getActualShape(state);
    }

    private VoxelShape getActualShape(BlockState state) {
        Direction.Axis axis = state.getValue(AXIS);
        VoxelShape base = switch (axis) {
            case X -> X_SHAPE;
            case Z -> Z_SHAPE;
            default -> Y_SHAPE;
        };
        if (state.getValue(HAS_GLASS_NEG)) {
            base = Shapes.or(base, getGlassShape(axis, false));
        }
        if (state.getValue(HAS_GLASS_POS)) {
            base = Shapes.or(base, getGlassShape(axis, true));
        }
        if (state.getValue(HAS_DECORATION)) {
            base = Shapes.or(base, Block.box(2, 2, 2, 14, 14, 14));
        }
        return base;
    }

    private VoxelShape getGlassShape(Direction.Axis axis, boolean isPos) {
        return switch (axis) {
            case X -> isPos ? Block.box(14, 0, 0, 16, 16, 16) : Block.box(0, 0, 0, 2, 16, 16);
            case Z -> isPos ? Block.box(0, 0, 14, 16, 16, 16) : Block.box(0, 0, 0, 16, 16, 2);
            default -> isPos ? Block.box(0, 14, 0, 16, 16, 16) : Block.box(0, 0, 0, 16, 2, 16);
        };
    }

    private static boolean isHollowLogHolding(ItemStack stack) {
        if (stack != null && !stack.isEmpty() && stack.getItem() instanceof BlockItem blockItem) {
            Block block = blockItem.getBlock();
            return block instanceof HollowLogBlock || block instanceof HollowPipeBlock;
        }
        return false;
    }

    private static boolean isValidDecorationHolding(ItemStack stack, Direction.Axis axis, BlockState logState) {
        if (stack == null || stack.isEmpty()) return false;
        Item item = stack.getItem();

        boolean hasGlassNeg = logState.getValue(HAS_GLASS_NEG);
        boolean isPotAllowed = (axis != Direction.Axis.Y) || hasGlassNeg;
        boolean hasDecoration = logState.getValue(HAS_DECORATION);

        if (item == Items.FLOWER_POT && isPotAllowed && !hasDecoration) return true;

        if (hasDecoration) {
            return getPottedBlockState(item) != null;
        }

        if (item instanceof BlockItem blockItem) {
            Block block = blockItem.getBlock();
            return isValidFullBlockDecoration(block);
        }
        return false;
    }

    @Override
    public InteractionResult onInteract(Level level, BlockPos pos, BlockState state, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);
        return handleInteraction(state, level, pos, player, hand, hit, held);
    }

    private InteractionResult handleInteraction(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, ItemStack held) {
        BlockEntity be = level.getBlockEntity(pos);
        HollowLogBlockEntity hollowBe = (be instanceof HollowLogBlockEntity) ? (HollowLogBlockEntity) be : null;

        Direction hitFace = hit.getDirection();
        Direction.Axis axis = state.getValue(AXIS);

        boolean targetPos;
        if (hitFace.getAxis() == axis) {
            targetPos = (hitFace.getAxisDirection() == Direction.AxisDirection.POSITIVE);
        } else {
            double hitCoord = switch (axis) {
                case X -> hit.getLocation().x - pos.getX();
                case Z -> hit.getLocation().z - pos.getZ();
                default -> hit.getLocation().y - pos.getY();
            };
            targetPos = (hitCoord >= 0.5);
        }

        boolean hasFluid = HollowPipeBlock.getContainedFluid(state, hollowBe) != Fluids.EMPTY;
        boolean hasDecoration = state.getValue(HAS_DECORATION);

        if (hasFluid && isFullGlassBlock(held)) {
            BooleanProperty glassProp = targetPos ? HAS_GLASS_POS : HAS_GLASS_NEG;
            if (!state.getValue(glassProp)) {
                if (!level.isClientSide()) {
                    state = state.setValue(glassProp, true);
                    level.setBlock(pos, state, 3);
                    if (hollowBe != null && held.getItem() instanceof BlockItem blockItem) {
                        BlockState glassState = blockItem.getBlock().defaultBlockState();
                        if (targetPos) hollowBe.setGlassCoverPos(glassState);
                        else hollowBe.setGlassCoverNeg(glassState);
                        hollowBe.setGlassPlacedByPlayer(player.getUUID());
                        hollowBe.setChanged();
                    }
                    if (!player.getAbilities().instabuild) {
                        held.shrink(1);
                    }
                    Services.PLATFORM.playGlassPlace(level, pos);
                    HollowPipeTransportManager.markDirty(level, pos);
                }
                return InteractionResult.SUCCESS;
            }
        }

        if (!hasDecoration) {
            Fluid sourceFluid = HollowPipeBlock.getSourceFluid(state, hollowBe);
            Fluid containedFluid = HollowPipeBlock.getContainedFluid(state, hollowBe);

            boolean isEmptyBucket = held.is(Items.BUCKET)
                    || (held.getItem() instanceof BucketItem && Services.PLATFORM.getBucketFluid(held.getItem()) == Fluids.EMPTY);

            if (isEmptyBucket && sourceFluid != Fluids.EMPTY) {
                if (!level.isClientSide()) {
                    if (hollowBe != null) {
                        hollowBe.setFluidType("none");
                        hollowBe.setLavaTicks(0);
                        hollowBe.setChanged();
                    }
                    state = state.setValue(WATERLOGGED, false).setValue(LAVA_LOGGED, false);
                    level.setBlock(pos, state, 3);

                    ItemStack filledBucket = HollowPipeBlock.getFilledBucketForFluid(sourceFluid);
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

            Fluid fluidInBucket = HollowPipeBlock.getFluidFromItem(held);
            if (fluidInBucket != Fluids.EMPTY) {
                if (containedFluid != Fluids.EMPTY) {
                    return InteractionResult.SUCCESS;
                }
                if (!level.isClientSide()) {
                    boolean isWater = (fluidInBucket == Fluids.WATER);
                    boolean isLava = (fluidInBucket == Fluids.LAVA);
                    if (hollowBe != null) {
                        String fKey = isWater ? "water" : (isLava ? "lava" : String.valueOf(Services.PLATFORM.getFluidId(fluidInBucket)));
                        hollowBe.setFluidType(fKey);
                        if (isLava) {
                            hollowBe.setLavaTicks(100 + level.getRandom().nextInt(71901));
                            hollowBe.setLavaPlacedByPlayer(player.getUUID());
                        } else {
                            hollowBe.setLavaTicks(0);
                        }
                        hollowBe.setChanged();
                    }
                    state = state.setValue(WATERLOGGED, isWater).setValue(LAVA_LOGGED, isLava);
                    level.setBlock(pos, state, 3);
                    HollowPipeTransportManager.onBucketUsed(level, pos, state);

                    if (!player.getAbilities().instabuild) {
                        held.shrink(1);
                        ItemStack emptyContainer = new ItemStack(Items.BUCKET);
                        if (held.isEmpty()) {
                            player.setItemInHand(hand, emptyContainer);
                        } else if (!player.getInventory().add(emptyContainer)) {
                            player.drop(emptyContainer, false);
                        }
                    }
                    Services.PLATFORM.playBucketEmptyFluid(level, pos, fluidInBucket == Fluids.LAVA);
                }
                return InteractionResult.SUCCESS;
            }
        }

        if (hasDecoration && hollowBe != null && hollowBe.getDecorationState().is(Blocks.FLOWER_POT)) {
            BlockState pottedState = getPottedBlockState(held.getItem());
            if (pottedState != null) {
                if (!level.isClientSide()) {
                    hollowBe.setDecorationState(pottedState);
                    hollowBe.setChanged();
                    if (!player.getAbilities().instabuild) {
                        held.shrink(1);
                    }
                    Services.PLATFORM.playGrassPlace(level, pos);
                }
                return InteractionResult.SUCCESS;
            }
        }

        if (!hasFluid && !hasDecoration) {
            boolean isPotAllowed = (axis != Direction.Axis.Y) || state.getValue(HAS_GLASS_NEG);

            if (held.is(Items.FLOWER_POT) && isPotAllowed) {
                if (!level.isClientSide()) {
                    state = state.setValue(HAS_DECORATION, true);
                    level.setBlock(pos, state, 3);
                    if (hollowBe != null) {
                        hollowBe.setDecorationState(Blocks.FLOWER_POT.defaultBlockState());
                        hollowBe.setChanged();
                    }
                    if (!player.getAbilities().instabuild) {
                        held.shrink(1);
                    }
                    Services.PLATFORM.playStonePlace(level, pos);
                }
                return InteractionResult.SUCCESS;
            }

            if (held.getItem() instanceof BlockItem blockItem) {
                Block block = blockItem.getBlock();
                boolean isFullBlock = isValidFullBlockDecoration(block);

                if (isFullBlock) {
                    if (!level.isClientSide()) {
                        BlockState stateToStore = block.defaultBlockState();
                        state = state.setValue(HAS_DECORATION, true);
                        level.setBlock(pos, state, 3);
                        if (hollowBe != null) {
                            hollowBe.setDecorationState(stateToStore);
                            hollowBe.setChanged();
                        }
                        if (!player.getAbilities().instabuild) {
                            held.shrink(1);
                        }
                        Services.PLATFORM.playStonePlace(level, pos);
                    }
                    return InteractionResult.SUCCESS;
                }
            }
        }

        if (held.isEmpty() || player.isShiftKeyDown()) {
            BooleanProperty glassProp = targetPos ? HAS_GLASS_POS : HAS_GLASS_NEG;
            boolean hasTargetGlass = state.getValue(glassProp) || (hollowBe != null && (targetPos ? !hollowBe.getGlassCoverPos().isAir() : !hollowBe.getGlassCoverNeg().isAir()));
            boolean hasAnyDecoration = state.getValue(HAS_DECORATION) || (hollowBe != null && !hollowBe.getDecorationState().isAir());

            if (hasTargetGlass) {
                if (!level.isClientSide()) {
                    state = state.setValue(glassProp, false);
                    level.setBlock(pos, state, 3);
                    if (hollowBe != null) {
                        BlockState glassState = targetPos ? hollowBe.getGlassCoverPos() : hollowBe.getGlassCoverNeg();
                        if (!glassState.isAir()) {
                            popResource(level, pos, new ItemStack(glassState.getBlock()));
                        } else {
                            popResource(level, pos, new ItemStack(Items.GLASS));
                        }
                        if (targetPos) hollowBe.setGlassCoverPos(Blocks.AIR.defaultBlockState());
                        else hollowBe.setGlassCoverNeg(Blocks.AIR.defaultBlockState());
                        hollowBe.setChanged();
                    }
                    Services.PLATFORM.playGlassBreak(level, pos);
                    HollowPipeTransportManager.markDirty(level, pos);
                }
                return InteractionResult.SUCCESS;
            } else if (hasAnyDecoration) {
                if (!level.isClientSide()) {
                    state = state.setValue(HAS_DECORATION, false);
                    level.setBlock(pos, state, 3);
                    if (hollowBe != null) {
                        BlockState decState = hollowBe.getDecorationState();
                        for (ItemStack drop : getDecorationDrops(decState)) {
                            popResource(level, pos, drop);
                        }
                        hollowBe.setDecorationState(Blocks.AIR.defaultBlockState());
                        hollowBe.setChanged();
                    }
                    Services.PLATFORM.playGrassBreak(level, pos);
                }
                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.PASS;
    }

    private static List<ItemStack> getDecorationDrops(BlockState decState) {
        if (decState == null || decState.isAir()) return Collections.emptyList();
        if (decState.getBlock() instanceof FlowerPotBlock potBlock) {
            if (potBlock == Blocks.FLOWER_POT) {
                return List.of(new ItemStack(Items.FLOWER_POT));
            } else {
                return List.of(new ItemStack(potBlock.asItem()));
            }
        }
        return List.of(new ItemStack(decState.getBlock()));
    }

    private static boolean isFullGlassBlock(ItemStack stack) {
        if (stack != null && !stack.isEmpty() && stack.getItem() instanceof BlockItem blockItem) {
            Block block = blockItem.getBlock();

            if (block.getClass().getSimpleName().contains("Glass") || block.getClass().getSimpleName().contains("Transparent")) {
                return true;
            }

            CommonId rl = Services.PLATFORM.getBlockId(block);
            if (rl != null && rl.getPath().contains("glass")) {
                String path = rl.getPath();
                if (!path.contains("pane") && !path.contains("fence") && !path.contains("slab")
                        && !path.contains("stair") && !path.contains("wall") && !path.contains("jar")) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isValidFullBlockDecoration(Block block) {
        if (block == null || block instanceof EntityBlock || block instanceof HollowLogBlock || block instanceof HollowPipeBlock) {
            return false;
        }

        CommonId rl = Services.PLATFORM.getBlockId(block);
        if (rl != null) {
            String path = rl.getPath();
            if (path.contains("glass") || path.contains("chain") || path.contains("shelf") || path.contains("jar")
                    || path.contains("slab") || path.contains("stair") || path.contains("wall")
                    || path.contains("fence") || path.contains("door") || path.contains("torch")
                    || path.contains("ladder") || path.contains("lantern") || path.contains("carpet")) {
                return false;
            }
        }

        try {
            BlockState defaultState = block.defaultBlockState();
            if (!Block.isShapeFullBlock(defaultState.getShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO))) {
                return false;
            }
        } catch (Exception ignored) {
            // The reference rejects blocks whose shape cannot be queried without a world.
            return false;
        }

        return true;
    }

    public static BlockState getPottedBlockState(Item item) {
        if (item == Items.POPPY) return Blocks.POTTED_POPPY.defaultBlockState();
        if (item == Items.DANDELION) return Blocks.POTTED_DANDELION.defaultBlockState();
        if (item == Items.BLUE_ORCHID) return Blocks.POTTED_BLUE_ORCHID.defaultBlockState();
        if (item == Items.ALLIUM) return Blocks.POTTED_ALLIUM.defaultBlockState();
        if (item == Items.AZURE_BLUET) return Blocks.POTTED_AZURE_BLUET.defaultBlockState();
        if (item == Items.RED_TULIP) return Blocks.POTTED_RED_TULIP.defaultBlockState();
        if (item == Items.ORANGE_TULIP) return Blocks.POTTED_ORANGE_TULIP.defaultBlockState();
        if (item == Items.WHITE_TULIP) return Blocks.POTTED_WHITE_TULIP.defaultBlockState();
        if (item == Items.PINK_TULIP) return Blocks.POTTED_PINK_TULIP.defaultBlockState();
        if (item == Items.OXEYE_DAISY) return Blocks.POTTED_OXEYE_DAISY.defaultBlockState();
        if (item == Items.CORNFLOWER) return Blocks.POTTED_CORNFLOWER.defaultBlockState();
        if (item == Items.LILY_OF_THE_VALLEY) return Blocks.POTTED_LILY_OF_THE_VALLEY.defaultBlockState();
        if (item == Items.WITHER_ROSE) return Blocks.POTTED_WITHER_ROSE.defaultBlockState();
        if (item == Items.RED_MUSHROOM) return Blocks.POTTED_RED_MUSHROOM.defaultBlockState();
        if (item == Items.BROWN_MUSHROOM) return Blocks.POTTED_BROWN_MUSHROOM.defaultBlockState();
        if (item == Items.DEAD_BUSH) return Blocks.POTTED_DEAD_BUSH.defaultBlockState();
        if (item == Items.CACTUS) return Blocks.POTTED_CACTUS.defaultBlockState();
        if (item == Items.BAMBOO) return Blocks.POTTED_BAMBOO.defaultBlockState();
        if (item == Items.OAK_SAPLING) return Blocks.POTTED_OAK_SAPLING.defaultBlockState();
        if (item == Items.SPRUCE_SAPLING) return Blocks.POTTED_SPRUCE_SAPLING.defaultBlockState();
        if (item == Items.BIRCH_SAPLING) return Blocks.POTTED_BIRCH_SAPLING.defaultBlockState();
        if (item == Items.JUNGLE_SAPLING) return Blocks.POTTED_JUNGLE_SAPLING.defaultBlockState();
        if (item == Items.ACACIA_SAPLING) return Blocks.POTTED_ACACIA_SAPLING.defaultBlockState();
        if (item == Items.DARK_OAK_SAPLING) return Blocks.POTTED_DARK_OAK_SAPLING.defaultBlockState();
        if (item == Items.CRIMSON_FUNGUS) return Blocks.POTTED_CRIMSON_FUNGUS.defaultBlockState();
        if (item == Items.WARPED_FUNGUS) return Blocks.POTTED_WARPED_FUNGUS.defaultBlockState();
        if (item == Items.CRIMSON_ROOTS) return Blocks.POTTED_CRIMSON_ROOTS.defaultBlockState();
        if (item == Items.WARPED_ROOTS) return Blocks.POTTED_WARPED_ROOTS.defaultBlockState();
        if (item == Items.AZALEA) return Blocks.POTTED_AZALEA.defaultBlockState();
        if (item == Items.FLOWERING_AZALEA) return Blocks.POTTED_FLOWERING_AZALEA.defaultBlockState();
        if (item == Items.FERN) return Blocks.POTTED_FERN.defaultBlockState();
        return null;
    }

    @Override
    public void onPlayerDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        BlockEntity be = level.getBlockEntity(pos);
        Fluid sourceFluid = HollowPipeBlock.getSourceFluid(state, be);
        if (be instanceof HollowLogBlockEntity hollowBe) {
            if (!hollowBe.getGlassCoverNeg().isAir()) {
                popResource(level, pos, new ItemStack(hollowBe.getGlassCoverNeg().getBlock()));
            }
            if (!hollowBe.getGlassCoverPos().isAir()) {
                popResource(level, pos, new ItemStack(hollowBe.getGlassCoverPos().getBlock()));
            }
            if (!hollowBe.getDecorationState().isAir()) {
                for (ItemStack drop : getDecorationDrops(hollowBe.getDecorationState())) {
                    popResource(level, pos, drop);
                }
            }
        }
        if (!level.isClientSide()) {
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
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        Direction.Axis axis = context.getClickedFace().getAxis();

        Fluid fluidInWorld = (fluidstate != null && fluidstate.isSource()) ? fluidstate.getType() : Fluids.EMPTY;
        if (fluidInWorld != Fluids.EMPTY) {
            HollowPipeBlock.PLACED_FLUID.set(fluidInWorld);
        } else {
            HollowPipeBlock.PLACED_FLUID.remove();
        }

        boolean isWater = (fluidInWorld == Fluids.WATER);
        boolean isLava = (fluidInWorld == Fluids.LAVA);

        return this.defaultBlockState()
                .setValue(AXIS, axis)
                .setValue(WATERLOGGED, isWater)
                .setValue(LAVA_LOGGED, isLava)
                .setValue(HAS_GLASS_NEG, false)
                .setValue(HAS_GLASS_POS, false)
                .setValue(HAS_DECORATION, false);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide()) {
            BlockEntity be = level.getBlockEntity(pos);
            Fluid worldFluid = HollowPipeBlock.PLACED_FLUID.get();
            HollowPipeBlock.PLACED_FLUID.remove();

            if (be instanceof HollowLogBlockEntity hollowBe) {
                if (worldFluid != null && worldFluid != Fluids.EMPTY) {
                    if (worldFluid == Fluids.WATER) {
                        hollowBe.setFluidType("water");
                    } else if (worldFluid == Fluids.LAVA) {
                        hollowBe.setFluidType("lava");
                        if (placer instanceof Player player) {
                            hollowBe.setLavaPlacedByPlayer(player.getUUID());
                        }
                        hollowBe.setLavaTicks(100 + level.getRandom().nextInt(71901));
                    } else {
                        String fKey = String.valueOf(Services.PLATFORM.getFluidId(worldFluid));
                        hollowBe.setFluidType(fKey);
                        hollowBe.setLavaTicks(0);
                    }
                    hollowBe.setChanged();
                } else if (state.getValue(LAVA_LOGGED)) {
                    hollowBe.setFluidType("lava");
                    if (placer instanceof Player player) {
                        hollowBe.setLavaPlacedByPlayer(player.getUUID());
                    }
                    hollowBe.setLavaTicks(100 + level.getRandom().nextInt(71901));
                    hollowBe.setChanged();
                } else if (state.getValue(WATERLOGGED)) {
                    hollowBe.setFluidType("water");
                    hollowBe.setChanged();
                }

                boolean hasDec = !hollowBe.getDecorationState().isAir();
                boolean hasNeg = !hollowBe.getGlassCoverNeg().isAir();
                boolean hasPos = !hollowBe.getGlassCoverPos().isAir();

                if (hasDec != state.getValue(HAS_DECORATION) || hasNeg != state.getValue(HAS_GLASS_NEG) || hasPos != state.getValue(HAS_GLASS_POS)) {
                    BlockState updated = state
                            .setValue(HAS_DECORATION, hasDec)
                            .setValue(HAS_GLASS_NEG, hasNeg)
                            .setValue(HAS_GLASS_POS, hasPos);
                    level.setBlock(pos, updated, 3);
                }
            }
            HollowPipeTransportManager.onBlockPlaced(level, pos, state);
        }
    }

    @Override
    public void onNeighborUpdate(Level level, BlockPos pos, BlockState state, Block neighborBlock, BlockPos neighborPos, boolean isMoving) {
        if (!level.isClientSide()) {
            HollowPipeTransportManager.onNeighborChanged(level, pos, state, pos);
        }
    }

    public boolean canPlaceLiquid(BlockGetter level, BlockPos pos, BlockState state, Fluid fluid) {
        return !state.getValue(WATERLOGGED) && !state.getValue(LAVA_LOGGED) && fluid == Fluids.WATER;
    }

    @Override
    public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
        if (!state.getValue(WATERLOGGED) && !state.getValue(LAVA_LOGGED) && fluidState.getType() == Fluids.WATER) {
            if (!level.isClientSide()) {
                BlockState newState = state.setValue(WATERLOGGED, true);
                level.setBlock(pos, newState, 3);
                level.scheduleTick(pos, fluidState.getType(), fluidState.getType().getTickDelay(level));
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof HollowLogBlockEntity hollowBe) {
                    hollowBe.setFluidType("water");
                    hollowBe.setChanged();
                }
                if (level instanceof Level lvl) {
                    HollowPipeTransportManager.onBucketUsed(lvl, pos, newState);
                }
            }
            return true;
        }
        return false;
    }

    public ItemStack pickupBlock(LevelAccessor level, BlockPos pos, BlockState state) {
        BlockEntity be = level.getBlockEntity(pos);
        HollowLogBlockEntity hollowBe = be instanceof HollowLogBlockEntity h ? h : null;
        Fluid sourceFluid = HollowPipeBlock.getSourceFluid(state, be);
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
            return HollowPipeBlock.getFilledBucketForFluid(sourceFluid);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return Fluids.EMPTY.defaultFluidState();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WATERLOGGED, LAVA_LOGGED, HAS_GLASS_NEG, HAS_GLASS_POS, HAS_DECORATION);
    }
}
