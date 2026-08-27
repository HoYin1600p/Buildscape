package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.event.AdvancementEvents;
import com.kingodogo.buildscape.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.AbstractGlassBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.CarpetBlock;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.EndRodBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.GlassBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.LightningRodBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.MushroomBlock;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StainedGlassBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.WallBlock;
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

import java.util.Random;

public class HollowLogBlock extends RotatedPillarBlock implements EntityBlock, SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty LAVA_LOGGED = BooleanProperty.create("lava_logged");
    public static final BooleanProperty HAS_GLASS_NEG = BooleanProperty.create("has_glass_neg");
    public static final BooleanProperty HAS_GLASS_POS = BooleanProperty.create("has_glass_pos");
    public static final BooleanProperty HAS_DECORATION = BooleanProperty.create("has_decoration");

    private static final VoxelShape Y_NORTH = Block.box(0, 0, 0, 16, 16, 2);
    private static final VoxelShape Y_SOUTH = Block.box(0, 0, 14, 16, 16, 16);
    private static final VoxelShape Y_WEST  = Block.box(0, 0, 2, 2, 16, 14);
    private static final VoxelShape Y_EAST  = Block.box(14, 0, 2, 16, 16, 14);
    private static final VoxelShape Y_SHAPE = Shapes.or(Y_NORTH, Y_SOUTH, Y_WEST, Y_EAST);

    private static final VoxelShape X_NORTH = Block.box(0, 0, 0, 16, 16, 2);
    private static final VoxelShape X_SOUTH = Block.box(0, 0, 14, 16, 16, 16);
    private static final VoxelShape X_DOWN  = Block.box(0, 0, 2, 16, 2, 14);
    private static final VoxelShape X_UP    = Block.box(0, 14, 2, 16, 16, 14);
    private static final VoxelShape X_SHAPE = Shapes.or(X_NORTH, X_SOUTH, X_DOWN, X_UP);

    private static final VoxelShape Z_WEST  = Block.box(0, 0, 0, 2, 16, 16);
    private static final VoxelShape Z_EAST  = Block.box(14, 0, 0, 16, 16, 16);
    private static final VoxelShape Z_DOWN  = Block.box(2, 0, 0, 14, 2, 16);
    private static final VoxelShape Z_UP    = Block.box(2, 14, 0, 14, 16, 16);
    private static final VoxelShape Z_SHAPE = Shapes.or(Z_WEST, Z_EAST, Z_DOWN, Z_UP);

    public HollowLogBlock(Properties properties) {
        super(properties.noOcclusion());
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
        if (level.isClientSide) return null;
        if (!state.getValue(LAVA_LOGGED)) return null;
        return type == ModBlockEntities.HOLLOW_LOG_BLOCK_ENTITY.get()
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

        // If holding empty flower pot, allowed only if log has no decoration and pot is allowed
        if (item == Items.FLOWER_POT && isPotAllowed && !hasDecoration) return true;

        // If log already has decoration, plant/flower can be inserted into an existing flower pot
        if (hasDecoration) {
            return getPottedBlockState(item) != null;
        }

        // Empty log: only full blocks are valid interior decoration
        if (item instanceof BlockItem blockItem) {
            Block block = blockItem.getBlock();
            return isValidFullBlockDecoration(block);
        }
        return false;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);
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

        boolean hasFluid = state.getValue(WATERLOGGED) || state.getValue(LAVA_LOGGED)
                || (hollowBe != null && !"none".equals(hollowBe.getFluidType()) && !hollowBe.getFluidType().isEmpty());
        boolean hasDecoration = state.getValue(HAS_DECORATION);

        // 1. Placing Glass Cover — ONLY IF FLUID IS PRESENT
        if (hasFluid && isFullGlassBlock(held)) {
            BooleanProperty glassProp = targetPos ? HAS_GLASS_POS : HAS_GLASS_NEG;
            if (!state.getValue(glassProp)) {
                if (!level.isClientSide) {
                    state = state.setValue(glassProp, true);
                    level.setBlock(pos, state, 3);
                    if (hollowBe != null && held.getItem() instanceof BlockItem blockItem) {
                        BlockState glassState = blockItem.getBlock().defaultBlockState();
                        if (targetPos) hollowBe.setGlassCoverPos(glassState);
                        else hollowBe.setGlassCoverNeg(glassState);
                        hollowBe.setGlassPlacedByPlayer(player.getUUID());
                    }
                    if (!player.getAbilities().instabuild) {
                        held.shrink(1);
                    }
                    level.playSound(null, pos, SoundEvents.GLASS_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);

                    if (player instanceof ServerPlayer serverPlayer) {
                        if (state.getValue(LAVA_LOGGED) || (hollowBe != null && "lava".equals(hollowBe.getFluidType()))) {
                            if (hollowBe != null && serverPlayer.getUUID().equals(hollowBe.getLavaPlacedByPlayer())) {
                                AdvancementEvents.grant(serverPlayer, "finally_you_can_walk_on_lava");
                            }
                        }
                        if (state.getValue(WATERLOGGED) || (hollowBe != null && "water".equals(hollowBe.getFluidType()))) {
                            AdvancementEvents.grant(serverPlayer, "i_feel_like_jesus");
                        }
                    }
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }

        // 2. Fluid interactions (Lava, Water, Experience, or Modded Fluids) — REJECT IF DECORATION IS PRESENT
        if (!hasDecoration) {
            Fluid containedFluid = HollowPipeBlock.getContainedFluid(state, hollowBe);

            boolean isEmptyBucket = held.is(Items.BUCKET)
                    || (held.getItem() instanceof BucketItem bi && bi.getFluid() == Fluids.EMPTY)
                    || (FluidUtil.getFluidHandler(held).isPresent() && FluidUtil.getFluidContained(held).orElse(FluidStack.EMPTY).isEmpty());

            // Empty Bucket interaction to retrieve fluid
            if (isEmptyBucket && containedFluid != Fluids.EMPTY) {
                if (!level.isClientSide) {
                    if (hollowBe != null) {
                        hollowBe.setFluidType("none");
                        hollowBe.setLavaTicks(0);
                        hollowBe.setChanged();
                    }
                    state = state.setValue(WATERLOGGED, false).setValue(LAVA_LOGGED, false);
                    level.setBlock(pos, state, 3);

                    ItemStack filledBucket = HollowPipeBlock.getFilledBucketForFluid(containedFluid);
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

            // Filled Fluid Bucket interaction to deposit fluid
            Fluid fluidInBucket = HollowPipeBlock.getFluidFromItem(held);
            if (fluidInBucket != Fluids.EMPTY) {
                if (containedFluid != Fluids.EMPTY) {
                    // NEVER allow replacing an existing fluid in a log! Only one fluid type per blockspace.
                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
                if (!level.isClientSide) {
                    boolean isWater = (fluidInBucket == Fluids.WATER);
                    boolean isLava = (fluidInBucket == Fluids.LAVA);
                    if (hollowBe != null) {
                        String fKey = isWater ? "water" : (isLava ? "lava" : ForgeRegistries.FLUIDS.getKey(fluidInBucket).toString());
                        hollowBe.setFluidType(fKey);
                        if (isLava) {
                            hollowBe.setLavaTicks(100 + level.random.nextInt(71901));
                            hollowBe.setLavaPlacedByPlayer(player.getUUID());
                        } else {
                            hollowBe.setLavaTicks(0);
                        }
                        hollowBe.setChanged();
                    }
                    state = state.setValue(WATERLOGGED, isWater).setValue(LAVA_LOGGED, isLava);
                    level.setBlock(pos, state, 3);
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

                    if (player instanceof ServerPlayer serverPlayer) {
                        if (isLava && (state.getValue(HAS_GLASS_NEG) || state.getValue(HAS_GLASS_POS))) {
                            if (hollowBe != null && serverPlayer.getUUID().equals(hollowBe.getGlassPlacedByPlayer())) {
                                AdvancementEvents.grant(serverPlayer, "finally_you_can_walk_on_lava");
                            }
                        }
                    }
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }

        // 3. Inserting Flower/Plant/Foliage into an existing Empty Flower Pot inside Hollow Log
        if (hasDecoration && hollowBe != null && hollowBe.getDecorationState().is(Blocks.FLOWER_POT)) {
            BlockState pottedState = getPottedBlockState(held.getItem());
            if (pottedState != null) {
                if (!level.isClientSide) {
                    hollowBe.setDecorationState(pottedState);
                    if (!player.getAbilities().instabuild) {
                        held.shrink(1);
                    }
                    level.playSound(null, pos, SoundEvents.GRASS_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }

        // 4. Hollow Log Interior Decoration — REJECT IF FLUID IS PRESENT
        // Foliage / flowers / plants can ONLY be placed if an empty flower pot is already placed inside the log (Section 3).
        if (!hasFluid && !hasDecoration) {
            boolean isPotAllowed = (axis != Direction.Axis.Y) || state.getValue(HAS_GLASS_NEG);

            // A. Placing an empty Flower Pot
            if (held.is(Items.FLOWER_POT) && isPotAllowed) {
                if (!level.isClientSide) {
                    state = state.setValue(HAS_DECORATION, true);
                    level.setBlock(pos, state, 3);
                    if (hollowBe != null) {
                        hollowBe.setDecorationState(Blocks.FLOWER_POT.defaultBlockState());
                    }
                    if (!player.getAbilities().instabuild) {
                        held.shrink(1);
                    }
                    level.playSound(null, pos, SoundEvents.STONE_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }

            // B. Placing a Full Block Decoration
            if (held.getItem() instanceof BlockItem blockItem) {
                Block block = blockItem.getBlock();
                boolean isFullBlock = isValidFullBlockDecoration(block);

                if (isFullBlock) {
                    if (!level.isClientSide) {
                        BlockState stateToStore = block.defaultBlockState();
                        state = state.setValue(HAS_DECORATION, true);
                        level.setBlock(pos, state, 3);
                        if (hollowBe != null) {
                            hollowBe.setDecorationState(stateToStore);
                        }
                        if (!player.getAbilities().instabuild) {
                            held.shrink(1);
                        }
                        level.playSound(null, pos, SoundEvents.STONE_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
                    }
                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
            }
        }

        // 5. Removing glass cover or decoration with empty hand or sneaking
        if (held.isEmpty() || player.isShiftKeyDown()) {
            BooleanProperty glassProp = targetPos ? HAS_GLASS_POS : HAS_GLASS_NEG;
            boolean hasTargetGlass = state.getValue(glassProp) || (hollowBe != null && (targetPos ? !hollowBe.getGlassCoverPos().isAir() : !hollowBe.getGlassCoverNeg().isAir()));
            boolean hasAnyDecoration = state.getValue(HAS_DECORATION) || (hollowBe != null && !hollowBe.getDecorationState().isAir());

            if (hasTargetGlass) {
                if (!level.isClientSide) {
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
                    }
                    level.playSound(null, pos, SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            } else if (hasAnyDecoration) {
                if (!level.isClientSide) {
                    state = state.setValue(HAS_DECORATION, false);
                    level.setBlock(pos, state, 3);
                    if (hollowBe != null) {
                        BlockState decState = hollowBe.getDecorationState();
                        for (ItemStack drop : getDecorationDrops(decState)) {
                            popResource(level, pos, drop);
                        }
                        hollowBe.setDecorationState(Blocks.AIR.defaultBlockState());
                    }
                    level.playSound(null, pos, SoundEvents.GRASS_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }

        return InteractionResult.PASS;
    }

    public static java.util.List<ItemStack> getDecorationDrops(BlockState decorationState) {
        java.util.List<ItemStack> drops = new java.util.ArrayList<>();
        if (decorationState != null && !decorationState.isAir()) {
            Block block = decorationState.getBlock();
            if (block instanceof FlowerPotBlock potBlock) {
                drops.add(new ItemStack(Items.FLOWER_POT));
                Block content = potBlock.getContent();
                if (content != null && content != Blocks.AIR) {
                    Item plantItem = content.asItem();
                    if (plantItem != null && plantItem != Items.AIR) {
                        drops.add(new ItemStack(plantItem));
                    }
                }
            } else {
                Item item = block.asItem();
                if (item != null && item != Items.AIR) {
                    drops.add(new ItemStack(item));
                }
            }
        }
        return drops;
    }

    private static boolean isFullGlassBlock(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        Item item = stack.getItem();
        if (item == Items.GLASS || item == Items.TINTED_GLASS) return true;
        if (item instanceof BlockItem blockItem) {
            Block block = blockItem.getBlock();

            // Exclude Non-Full Glass Shapes (Panes, Fences, Gates, Walls, Slabs, Stairs, Jars, etc.)
            if (block instanceof IronBarsBlock || block instanceof FenceBlock || block instanceof FenceGateBlock
                    || block instanceof WallBlock || block instanceof SlabBlock || block instanceof StairBlock
                    || block instanceof GlassJarBlock) {
                return false;
            }

            // Standard Minecraft Glass Classes
            if (block instanceof GlassBlock || block instanceof StainedGlassBlock || block instanceof AbstractGlassBlock) {
                return true;
            }

            // Check Tags (forge:glass/colorless, forge:glass, etc.)
            if (stack.is(net.minecraftforge.common.Tags.Items.GLASS) || stack.is(net.minecraftforge.common.Tags.Items.GLASS_COLORLESS) || stack.is(net.minecraftforge.common.Tags.Items.STAINED_GLASS)) {
                return true;
            }

            // Buildscape & Modded Glass Blocks: check registry name for "glass"
            net.minecraft.resources.ResourceLocation rl = ForgeRegistries.BLOCKS.getKey(block);
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
        if (block == null) return false;
        if (block instanceof EntityBlock) return false;
        if (block instanceof HollowLogBlock || block instanceof HollowPipeBlock) return false;

        // Reject non-full-cube / partial / utility blocks (including Fences, Gates, Panes, Walls, Slabs, Stairs, Chains, etc.)
        if (block instanceof SlabBlock || block instanceof StairBlock || block instanceof WallBlock
                || block instanceof FenceBlock || block instanceof FenceGateBlock || block instanceof IronBarsBlock
                || block instanceof ChainBlock || block instanceof ClimbableChainBlock || block instanceof LargeChainBlock
                || block instanceof SideChainPartBlock || block instanceof WeatheringClimbableChainBlock || block instanceof WeatheringLargeChainBlock
                || block instanceof TrapDoorBlock || block instanceof DoorBlock
                || block instanceof LadderBlock || block instanceof LanternBlock || block instanceof TorchBlock
                || block instanceof BushBlock || block instanceof FlowerBlock || block instanceof MushroomBlock
                || block instanceof SaplingBlock || block instanceof FlowerPotBlock || block instanceof LiquidBlock
                || block instanceof CarpetBlock || block instanceof ButtonBlock || block instanceof LeverBlock
                || block instanceof PressurePlateBlock || block instanceof EndRodBlock || block instanceof LightningRodBlock
                || block instanceof PointedDripstoneBlock || block instanceof PointedIcicleBlock || block instanceof SulfurSpikeBlock
                || block instanceof ShelfBlock || block instanceof GlassJarBlock) {
            return false;
        }

        // Reject ALL glass blocks and glass items from being placed as interior block decorations
        if (block instanceof GlassBlock || block instanceof StainedGlassBlock || block instanceof AbstractGlassBlock) {
            return false;
        }
        net.minecraft.resources.ResourceLocation rl = ForgeRegistries.BLOCKS.getKey(block);
        if (rl != null) {
            String path = rl.getPath();
            if (path.contains("glass") || path.contains("chain") || path.contains("shelf") || path.contains("jar")) {
                return false;
            }
        }

        // Shape check: must be a full 1x1x1 cube block
        try {
            BlockState defaultState = block.defaultBlockState();
            if (!Block.isShapeFullBlock(defaultState.getShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO))) {
                return false;
            }
        } catch (Exception ignored) {
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
        if (item instanceof BlockItem bi) {
            Block b = bi.getBlock();
            if (b instanceof FlowerPotBlock pot && pot != Blocks.FLOWER_POT) {
                return pot.defaultBlockState();
            }
            if (Blocks.FLOWER_POT instanceof FlowerPotBlock emptyPot) {
                net.minecraft.resources.ResourceLocation rl = b.getRegistryName();
                if (rl != null) {
                    java.util.function.Supplier<? extends Block> potSupplier = emptyPot.getFullPotsView().get(rl);
                    if (potSupplier != null && potSupplier.get() != null && potSupplier.get() != Blocks.AIR) {
                        return potSupplier.get().defaultBlockState();
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            Fluid loggedFluid = HollowPipeBlock.getContainedFluid(state, be);
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
            super.onRemove(state, level, pos, newState, isMoving);
            if (!level.isClientSide && loggedFluid != null && loggedFluid != Fluids.EMPTY) {
                BlockState fluidBlock = loggedFluid.defaultFluidState().createLegacyBlock();
                if (!fluidBlock.isAir() && level.getBlockState(pos).isAir()) {
                    level.setBlock(pos, fluidBlock, 3);
                    level.scheduleTick(pos, loggedFluid, loggedFluid.getTickDelay(level));
                }
            }
        } else {
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
        super.tick(state, level, pos, random);
        BlockEntity be = level.getBlockEntity(pos);
        Fluid fluid = HollowPipeBlock.getContainedFluid(state, be);
        tryFlowOut(level, pos, state, fluid);
    }

    public static void tryFlowOut(Level level, BlockPos pos, BlockState state, Fluid fluid) {
        if (fluid == null || fluid == Fluids.EMPTY || level.isClientSide) return;
        Direction.Axis axis = state.getValue(AXIS);
        Direction negDir = switch (axis) {
            case X -> Direction.WEST;
            case Z -> Direction.NORTH;
            default -> Direction.DOWN;
        };
        Direction posDir = switch (axis) {
            case X -> Direction.EAST;
            case Z -> Direction.SOUTH;
            default -> Direction.UP;
        };

        boolean hasGlassNeg = state.getValue(HAS_GLASS_NEG);
        boolean hasGlassPos = state.getValue(HAS_GLASS_POS);

        if (!hasGlassNeg && negDir != Direction.UP) {
            spreadFluidToNeighbor(level, pos.relative(negDir), fluid);
        }
        if (!hasGlassPos && posDir != Direction.UP) {
            spreadFluidToNeighbor(level, pos.relative(posDir), fluid);
        }
    }

    private static void spreadFluidToNeighbor(Level level, BlockPos neighborPos, Fluid fluid) {
        BlockState neighborState = level.getBlockState(neighborPos);
        if (neighborState.isAir() || neighborState.canBeReplaced(fluid) || (neighborState.getBlock() instanceof LiquidBlock && !neighborState.getFluidState().isSource())) {
            if (fluid instanceof FlowingFluid flowing) {
                BlockState fluidBlock = flowing.getFlowing(7, false).createLegacyBlock();
                if (!fluidBlock.isAir() && !neighborState.equals(fluidBlock)) {
                    level.setBlock(neighborPos, fluidBlock, 3);
                    level.scheduleTick(neighborPos, flowing, flowing.getTickDelay(level));
                }
            } else {
                BlockState fluidBlock = fluid.defaultFluidState().createLegacyBlock();
                if (!fluidBlock.isAir() && !neighborState.equals(fluidBlock)) {
                    level.setBlock(neighborPos, fluidBlock, 3);
                    level.scheduleTick(neighborPos, fluid, fluid.getTickDelay(level));
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
        if (!level.isClientSide) {
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
                        hollowBe.setLavaTicks(100 + level.random.nextInt(71901));
                    } else {
                        String fKey = ForgeRegistries.FLUIDS.getKey(worldFluid).toString();
                        hollowBe.setFluidType(fKey);
                        hollowBe.setLavaTicks(0);
                    }
                    hollowBe.setChanged();
                } else if (state.getValue(LAVA_LOGGED)) {
                    hollowBe.setFluidType("lava");
                    if (placer instanceof Player player) {
                        hollowBe.setLavaPlacedByPlayer(player.getUUID());
                    }
                    hollowBe.setLavaTicks(100 + level.random.nextInt(71901));
                    hollowBe.setChanged();
                } else if (state.getValue(WATERLOGGED)) {
                    hollowBe.setFluidType("water");
                    hollowBe.setChanged();
                }

                // Sync BlockState properties with NBT contents from copied BlockEntityTag
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
            Fluid fluid = HollowPipeBlock.getContainedFluid(state, be);
            tryFlowOut(level, pos, state, fluid);
        }
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (!level.isClientSide() && direction != Direction.UP && level instanceof Level lvl) {
            Direction.Axis axis = state.getValue(AXIS);
            boolean isEnd = (axis == Direction.Axis.X && (direction == Direction.WEST || direction == Direction.EAST))
                         || (axis == Direction.Axis.Z && (direction == Direction.NORTH || direction == Direction.SOUTH))
                         || (axis == Direction.Axis.Y && direction == Direction.DOWN);
            boolean hasGlass = (direction == Direction.WEST || direction == Direction.NORTH || direction == Direction.DOWN)
                    ? state.getValue(HAS_GLASS_NEG) : state.getValue(HAS_GLASS_POS);
            if (isEnd && !hasGlass && (neighborState.isAir() || neighborState.canBeReplaced(Fluids.WATER))) {
                Fluid fluid = HollowPipeBlock.getContainedFluid(state, lvl.getBlockEntity(pos));
                if (fluid != Fluids.EMPTY) {
                    spreadFluidToNeighbor(lvl, neighborPos, fluid);
                }
            }
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public boolean canPlaceLiquid(BlockGetter level, BlockPos pos, BlockState state, Fluid fluid) {
        return !state.getValue(WATERLOGGED) && !state.getValue(LAVA_LOGGED) && fluid == Fluids.WATER;
    }

    @Override
    public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
        if (!state.getValue(WATERLOGGED) && !state.getValue(LAVA_LOGGED) && fluidState.getType() == Fluids.WATER) {
            if (!level.isClientSide()) {
                level.setBlock(pos, state.setValue(WATERLOGGED, true), 3);
                level.scheduleTick(pos, fluidState.getType(), fluidState.getType().getTickDelay(level));
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof HollowLogBlockEntity hollowBe) {
                    hollowBe.setFluidType("water");
                    hollowBe.setChanged();
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public ItemStack pickupBlock(LevelAccessor level, BlockPos pos, BlockState state) {
        BlockEntity be = level.getBlockEntity(pos);
        HollowLogBlockEntity hollowBe = be instanceof HollowLogBlockEntity h ? h : null;
        Fluid containedFluid = HollowPipeBlock.getContainedFluid(state, be);
        if (containedFluid != Fluids.EMPTY) {
            if (hollowBe != null) {
                hollowBe.setFluidType("none");
                hollowBe.setLavaTicks(0);
                hollowBe.setChanged();
            }
            level.setBlock(pos, state.setValue(WATERLOGGED, false).setValue(LAVA_LOGGED, false), 3);
            return HollowPipeBlock.getFilledBucketForFluid(containedFluid);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        if (state.getValue(WATERLOGGED)) {
            return Fluids.WATER.getSource(false);
        }
        if (state.getValue(LAVA_LOGGED)) {
            return Fluids.LAVA.getSource(false);
        }
        return super.getFluidState(state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WATERLOGGED, LAVA_LOGGED, HAS_GLASS_NEG, HAS_GLASS_POS, HAS_DECORATION);
    }
}
