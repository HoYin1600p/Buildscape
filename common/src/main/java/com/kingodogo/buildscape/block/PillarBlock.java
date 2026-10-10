package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.config.PillarIdManager;
import com.kingodogo.buildscape.config.PillarParticleConfig;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import com.kingodogo.buildscape.util.ComponentHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
public class PillarBlock extends Block implements EntityBlock, SimpleWaterloggedBlock, ICommonInteractable, ICommonNeighborAware, ICommonRemoval, ICommonShapeUpdate {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final EnumProperty<PillarPart> PART = EnumProperty.create("part", PillarPart.class);

    private static final ThreadLocal<Float> PLACING_PLAYER_YAW = new ThreadLocal<>();

    private static final VoxelShape SHAPE_SINGLE = Shapes.or(
            Block.box(2, 0, 2, 14, 2, 14),
            Block.box(3, 2, 3, 13, 14, 13),
            Block.box(0, 14, 0, 16, 16, 16)
    );
    private static final VoxelShape SHAPE_BOTTOM = Shapes.or(
            Block.box(2, 0, 2, 14, 2, 14),
            Block.box(3, 2, 3, 13, 16, 13)
    );
    private static final VoxelShape SHAPE_MIDDLE = Block.box(3, 0, 3, 13, 16, 13);
    private static final VoxelShape SHAPE_TOP = Shapes.or(
            Block.box(3, 0, 3, 13, 14, 13),
            Block.box(0, 14, 0, 16, 16, 16)
    );

    public PillarBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(PART, PillarPart.SINGLE)
                .setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PART, WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState above = level.getBlockState(pos.above());
        BlockState below = level.getBlockState(pos.below());

        if (!level.isClientSide()) {
            Player player = context.getPlayer();
            if (player != null) {
                float playerYaw = player.getYRot();
                float facingYaw = (playerYaw + 180.0f) % 360.0f;
                if (facingYaw < 0.0f) {
                    facingYaw += 360.0f;
                }
                PLACING_PLAYER_YAW.set(facingYaw);
            } else {
                PLACING_PLAYER_YAW.set(null);
            }
        }

        boolean hasAbove = above.getBlock() instanceof PillarBlock && !(above.getBlock() instanceof AshenKingPillarBlock);
        boolean hasBelow = below.getBlock() instanceof PillarBlock && !(below.getBlock() instanceof AshenKingPillarBlock);

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

        FluidState fluidState = context.getLevel().getFluidState(pos);
        return this.defaultBlockState()
                .setValue(PART, part)
                .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);

        if (!level.isClientSide()) {
            if (!oldState.is(state.getBlock())) {
                ejectNeighborDisplayItem(level, pos, state);
            }
            enforceSingleItemPerStack(level, pos);
            syncNewPillarWithStack(level, pos);

            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof PillarBlockEntity pillarBE) {
                Float storedYaw = PLACING_PLAYER_YAW.get();
                if (storedYaw != null) {
                    pillarBE.setFacingYaw(storedYaw);
                    PLACING_PLAYER_YAW.set(null);
                }
            }

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

    private void syncNewPillarWithStack(Level level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof PillarBlockEntity newPillarBE)) {
            return;
        }

        BlockPos above = pos.above();
        BlockPos below = pos.below();
        PillarBlockEntity existingPillar = null;

        if (level.getBlockState(above).getBlock() instanceof PillarBlock) {
            BlockEntity aboveBE = level.getBlockEntity(above);
            if (aboveBE instanceof PillarBlockEntity pBE && pBE.hasCustomColors()) {
                existingPillar = pBE;
            }
        }
        if (existingPillar == null && level.getBlockState(below).getBlock() instanceof PillarBlock) {
            BlockEntity belowBE = level.getBlockEntity(below);
            if (belowBE instanceof PillarBlockEntity pBE && pBE.hasCustomColors()) {
                existingPillar = pBE;
            }
        }

        if (existingPillar != null) {
            String stackId = existingPillar.getPillarId();
            List<String> stackColors = existingPillar.getParticleColors();
            if (stackId != null && stackColors != null && !stackColors.isEmpty()) {
                if (!level.isClientSide()) {
                    PillarIdManager manager = PillarIdManager.get(level);
                    PillarIdManager.PillarData data = manager.getPillarData(stackId);
                    if (data != null) {
                        BlockPos bottom = findBottomBlock(level, pos);
                        data.x = bottom.getX();
                        data.y = bottom.getY();
                        data.z = bottom.getZ();
                        manager.saveImmediate(level.getServer());
                    }
                }
                newPillarBE.forceSetColors(stackColors, stackId);
            }
        }
    }

    @Override
    public void onBlockRemoved(Level level, BlockPos pos, BlockState state) {
        if (level.getBlockEntity(pos) instanceof PillarBlockEntity droppingBE && droppingBE.hasDisplayItem()) {
            ItemStack displayed = droppingBE.getDisplayedItem().copy();
            if (!displayed.isEmpty()) {
                ItemEntity itemEntity = new ItemEntity(level, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, displayed);
                itemEntity.setDefaultPickUpDelay();
                level.addFreshEntity(itemEntity);
            }
        }

        BlockEntity be = level.getBlockEntity(pos);
        String pillarIdToPreserve = null;
        List<String> colorsToPreserve = null;
        if (be instanceof PillarBlockEntity pillarBE) {
            pillarIdToPreserve = pillarBE.getPillarId();
            colorsToPreserve = pillarBE.getParticleColors();
            pillarBE.clearLocalStateOnly();
        }

        BlockPos bottom = findBottomBlock(level, pos);
        BlockPos top = findTopBlock(level, pos);
        boolean hasOtherPillars = false;
        BlockPos firstRemainingPillar = null;

        BlockPos current = bottom;
        while (level.getBlockState(current).getBlock() instanceof PillarBlock) {
            if (!current.equals(pos)) {
                hasOtherPillars = true;
                if (firstRemainingPillar == null) {
                    firstRemainingPillar = current;
                }
            }
            if (current.equals(top)) {
                break;
            }
            current = current.above();
        }

        PillarIdManager manager = PillarIdManager.get(level);
        if (hasOtherPillars && firstRemainingPillar != null) {
            BlockEntity remainingBe = level.getBlockEntity(firstRemainingPillar);
            if (remainingBe instanceof PillarBlockEntity remainingPillarBE) {
                if (colorsToPreserve != null && !colorsToPreserve.isEmpty() && pillarIdToPreserve != null) {
                    remainingPillarBE.forceSetColors(colorsToPreserve, pillarIdToPreserve);

                    PillarIdManager.PillarData data = manager.getPillarData(pillarIdToPreserve);
                    if (data != null) {
                        data.x = firstRemainingPillar.getX();
                        data.y = firstRemainingPillar.getY();
                        data.z = firstRemainingPillar.getZ();
                        manager.saveImmediate(level.getServer());
                    }

                    current = bottom;
                    while (level.getBlockState(current).getBlock() instanceof PillarBlock) {
                        if (!current.equals(pos)) {
                            BlockEntity currentBe = level.getBlockEntity(current);
                            if (currentBe instanceof PillarBlockEntity stackPillarBE && !current.equals(firstRemainingPillar)) {
                                stackPillarBE.forceSetColors(colorsToPreserve, pillarIdToPreserve);
                            }
                        }
                        if (current.equals(top)) {
                            break;
                        }
                        current = current.above();
                    }
                }
            }
            manager.removePillarByPosition(level, pos);
        } else {
            if (pillarIdToPreserve != null && !pillarIdToPreserve.isEmpty()) {
                manager.removePillar(pillarIdToPreserve);
            }
            manager.removePillarByPosition(level, pos);
        }

        BlockPos above = pos.above();
        BlockPos below = pos.below();
        BlockState aboveState = level.getBlockState(above);
        BlockState belowState = level.getBlockState(below);
        if (aboveState.getBlock() instanceof PillarBlock) {
            level.sendBlockUpdated(above, aboveState, aboveState, 3);
        }
        if (belowState.getBlock() instanceof PillarBlock) {
            level.sendBlockUpdated(below, belowState, belowState, 3);
        }
    }

    @Override
    public void onNeighborUpdate(Level level, BlockPos pos, BlockState state, Block block, BlockPos fromPos, boolean isMoving) {
        if (!level.isClientSide()) {
            BlockState below = level.getBlockState(pos.below());
            BlockState above = level.getBlockState(pos.above());
            boolean hasAbove = above.getBlock() instanceof PillarBlock && !(above.getBlock() instanceof AshenKingPillarBlock);
            boolean hasBelow = below.getBlock() instanceof PillarBlock && !(below.getBlock() instanceof AshenKingPillarBlock);

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

            if (state.getValue(PART) != newPart) {
                level.setBlock(pos, state.setValue(PART, newPart), 3);
            }

            enforceSingleItemPerStack(level, pos);
        }
    }

    @Override
    public BlockState onUpdateShape(BlockState state, Direction direction, BlockState neighborState, LevelReader level, BlockPos pos, BlockPos neighborPos) {
        if (direction == Direction.UP || direction == Direction.DOWN) {
            // The shape-update argument is authoritative, including during replacement/removal.
            BlockState below = direction == Direction.DOWN ? neighborState : level.getBlockState(pos.below());
            BlockState above = direction == Direction.UP ? neighborState : level.getBlockState(pos.above());
            boolean hasAbove = above.getBlock() instanceof PillarBlock && !(above.getBlock() instanceof AshenKingPillarBlock);
            boolean hasBelow = below.getBlock() instanceof PillarBlock && !(below.getBlock() instanceof AshenKingPillarBlock);

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
        return state;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide()) {
            PillarIdManager.get(level).getOrCreatePillarData(level, pos);

            CompoundTag customTag = Services.PLATFORM.getCustomData(stack, false);
            if (customTag != null && level.getBlockEntity(pos) instanceof PillarBlockEntity pillarBE) {
                String itemName = Services.PLATFORM.getTagString(customTag, "ITEM", null);
                if (itemName != null && !itemName.isEmpty()) {
                    Item item = Services.PLATFORM.getItem(CommonId.parse(itemName));
                    if (item != null && item != Items.AIR) {
                        pillarBE.setDisplayedItem(new ItemStack(item));
                    }
                }
                String pattern = Services.PLATFORM.getTagString(customTag, "PATTERN", null);
                if (pattern != null && !pattern.isEmpty()) {
                    pillarBE.setParticlePattern(pattern);
                }
                pillarBE.setChanged();
                level.sendBlockUpdated(pos, state, state, 3);
            }
        }
    }

    private static void ejectNeighborDisplayItem(Level level, BlockPos pos, BlockState state) {
        PillarPart part = state.getValue(PART);
        BlockPos neighborPos;
        if (part == PillarPart.TOP) {
            neighborPos = pos.below();
        } else if (part == PillarPart.BOTTOM) {
            neighborPos = pos.above();
        } else {
            return;
        }

        if (level.getBlockState(neighborPos).getBlock() instanceof AshenKingPillarBlock) {
            return;
        }

        if (level.getBlockEntity(neighborPos) instanceof PillarBlockEntity be && be.hasDisplayItem()) {
            ItemStack item = be.getDisplayedItem().copy();
            be.setDisplayedItem(ItemStack.EMPTY);
            ItemEntity itemEntity = new ItemEntity(level, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, item);
            itemEntity.setDefaultPickUpDelay();
            level.addFreshEntity(itemEntity);
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(PART)) {
            case SINGLE -> SHAPE_SINGLE;
            case BOTTOM -> SHAPE_BOTTOM;
            case MIDDLE -> SHAPE_MIDDLE;
            case TOP -> SHAPE_TOP;
        };
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getShape(state, level, pos, context);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PillarBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return type == ModBlockEntities.PILLAR_TYPE
                    ? (lvl, pos, st, be) -> PillarBlockEntity.clientTick(lvl, pos, st, (PillarBlockEntity) be)
                    : null;
        }
        return null;
    }

    @Override
    public InteractionResult onInteract(Level level, BlockPos pos, BlockState state, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack heldItem = player.getItemInHand(hand);
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof PillarBlockEntity pillarBE)) {
            return InteractionResult.PASS;
        }

        if (!heldItem.isEmpty()) {
            Map.Entry<String, String> dyeInfo = getDyeColorAndName(heldItem);
            if (dyeInfo != null) {
                if (stackHasAnyItem(level, pos)) {
                    String dyeColor = dyeInfo.getKey();
                    String dyeName = dyeInfo.getValue();

                    if (!pillarBE.canAddMoreColors()) {
                        Services.PLATFORM.sendActionBarMessage(
                                player,
                                ComponentHelper.literal("Pillar already has " + PillarBlockEntity.MAX_DYE_COLORS + " colors! Break and replace to reset.")
                                        .withStyle(ChatFormatting.RED)
                        );
                        return InteractionResult.CONSUME;
                    }

                    boolean added = pillarBE.addParticleColor(dyeColor);
                    if (!added) {
                        return InteractionResult.PASS;
                    }

                    if (!player.getAbilities().instabuild) {
                        heldItem.shrink(1);
                    }

                    Services.PLATFORM.playDyeUse(level, pos);

                    String pillarId = pillarBE.getPillarId();
                    int colorCount = pillarBE.getDyeColorCount();
                    String progressText = " (" + colorCount + "/" + PillarBlockEntity.MAX_DYE_COLORS + ")";
                    Component message;
                    if (pillarId != null) {
                        message = ComponentHelper.literal("[" + pillarId + "] Dyed " + dyeName + progressText);
                    } else {
                        message = ComponentHelper.literal("Pillar Dyed " + dyeName + progressText);
                    }

                    Services.PLATFORM.sendActionBarMessage(player, message);
                    return InteractionResult.SUCCESS;
                }
            }
        }

        if (player.isShiftKeyDown()) {
            ItemStack displayed = pillarBE.getDisplayedItem();
            boolean isSpawnEgg = !displayed.isEmpty() && displayed.getItem() instanceof SpawnEggItem;

            if (isSpawnEgg) {
                pillarBE.rotateFacing();
                float facingYaw = pillarBE.getFacingYaw();
                String direction = getDirectionName(facingYaw);
                Services.PLATFORM.sendActionBarMessage(
                        player,
                        ComponentHelper.literal("Mob facing: " + direction).withStyle(ChatFormatting.GREEN)
                );
                Services.PLATFORM.playButtonClick(level, pos);
                return InteractionResult.SUCCESS;
            } else {
                pillarBE.cycleParticlePattern();
                String pattern = pillarBE.getParticlePattern();
                if (pattern == null) {
                    pattern = "default";
                }
                ChatFormatting color = getPatternColor(pattern);
                Services.PLATFORM.sendActionBarMessage(
                        player,
                        ComponentHelper.literal(pattern).withStyle(color)
                );
                Services.PLATFORM.playButtonClick(level, pos);
                return InteractionResult.SUCCESS;
            }
        }

        BlockPos topBlockPos = findTopBlock(level, pos);
        if (!topBlockPos.equals(pos)) {
            BlockEntity topEntity = level.getBlockEntity(topBlockPos);
            if (topEntity instanceof PillarBlockEntity topPillarBE) {
                if (heldItem.isEmpty() && topPillarBE.hasDisplayItem()) {
                    ItemStack displayedItem = topPillarBE.getDisplayedItem();
                    if (!displayedItem.isEmpty()) {
                        topPillarBE.setDisplayedItem(ItemStack.EMPTY);
                        level.sendBlockUpdated(topBlockPos, level.getBlockState(topBlockPos), level.getBlockState(topBlockPos), 3);
                        ItemEntity itemEntity = new ItemEntity(level, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, displayedItem.copy());
                        itemEntity.setDefaultPickUpDelay();
                        level.addFreshEntity(itemEntity);
                        Services.PLATFORM.playItemFrameRemove(level, pos);
                        return InteractionResult.SUCCESS;
                    }
                }
                return handleItemInteraction(topPillarBE, level, topBlockPos, player, hand);
            }
            return InteractionResult.PASS;
        }

        if (heldItem.isEmpty() && pillarBE.hasDisplayItem()) {
            ItemStack displayedItem = pillarBE.getDisplayedItem();
            if (!displayedItem.isEmpty()) {
                pillarBE.setDisplayedItem(ItemStack.EMPTY);
                level.sendBlockUpdated(pos, level.getBlockState(pos), level.getBlockState(pos), 3);
                ItemEntity itemEntity = new ItemEntity(level, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, displayedItem.copy());
                itemEntity.setDefaultPickUpDelay();
                level.addFreshEntity(itemEntity);
                Services.PLATFORM.playItemFrameRemove(level, pos);
                return InteractionResult.SUCCESS;
            }
        }

        return handleItemInteraction(pillarBE, level, pos, player, hand);
    }

    private InteractionResult handleItemInteraction(PillarBlockEntity be, Level level, BlockPos pos, Player player, InteractionHand hand) {
        ItemStack heldItem = player.getItemInHand(hand);

        if (!heldItem.isEmpty() && stackHasAnyItem(level, pos)) {
            return InteractionResult.PASS;
        }

        if (!heldItem.isEmpty()) {
            ItemStack displayItem = heldItem.copy();
            displayItem.setCount(1);

            float playerYaw = player.getYRot();
            float facingYaw = (playerYaw + 180.0f) % 360.0f;
            if (facingYaw < 0.0f) {
                facingYaw += 360.0f;
            }
            be.setDisplayedItem(displayItem, facingYaw);

            if (player instanceof ServerPlayer serverPlayer) {
                Services.PLATFORM.awardAdvancement(serverPlayer, new CommonId("buildscape", "pedestal"), "insert_item");
                com.kingodogo.buildscape.event.AdvancementMilestoneLogic.onPillarItemInserted(serverPlayer);
            }

            if (!player.getAbilities().instabuild) {
                heldItem.shrink(1);
            }

            enforceSingleItemPerStack(level, pos);
            Services.PLATFORM.playItemFrameAdd(level, pos);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    private boolean stackHasAnyItem(Level level, BlockPos pos) {
        BlockPos bottom = findBottomBlock(level, pos);
        BlockPos current = bottom;
        while (level.getBlockState(current).getBlock() instanceof PillarBlock) {
            BlockEntity be = level.getBlockEntity(current);
            if (be instanceof PillarBlockEntity pillarBE && pillarBE.hasDisplayItem()) {
                return true;
            }
            current = current.above();
        }
        return false;
    }

    private void enforceSingleItemPerStack(Level level, BlockPos anyPosInStack) {
        BlockPos top = findTopBlock(level, anyPosInStack);
        BlockPos bottom = findBottomBlock(level, anyPosInStack);

        boolean topHasItem = false;
        ItemStack firstItem = ItemStack.EMPTY;
        BlockPos current = bottom;

        while (level.getBlockState(current).getBlock() instanceof PillarBlock &&
                !(level.getBlockState(current).getBlock() instanceof AshenKingPillarBlock)) {
            BlockEntity be = level.getBlockEntity(current);
            if (be instanceof PillarBlockEntity pillarBE && pillarBE.hasDisplayItem()) {
                if (current.equals(top)) {
                    topHasItem = true;
                }
                if (firstItem.isEmpty()) {
                    firstItem = pillarBE.getDisplayedItem().copy();
                }
            }
            current = current.above();
        }

        if (!topHasItem && !firstItem.isEmpty()) {
            BlockEntity beTop = level.getBlockEntity(top);
            if (beTop instanceof PillarBlockEntity topBE) {
                topBE.setDisplayedItem(firstItem);
                level.sendBlockUpdated(top, level.getBlockState(top), level.getBlockState(top), 3);
            }
        }

        current = bottom;
        while (level.getBlockState(current).getBlock() instanceof PillarBlock &&
                !(level.getBlockState(current).getBlock() instanceof AshenKingPillarBlock)) {
            if (!current.equals(top)) {
                BlockEntity be = level.getBlockEntity(current);
                if (be instanceof PillarBlockEntity pillarBE && pillarBE.hasDisplayItem()) {
                    pillarBE.setDisplayedItem(ItemStack.EMPTY);
                    level.sendBlockUpdated(current, level.getBlockState(current), level.getBlockState(current), 3);
                }
            }
            current = current.above();
        }
    }

    private BlockPos findBottomBlock(Level level, BlockPos pos) {
        BlockPos current = pos;
        while (level.getBlockState(current.below()).getBlock() instanceof PillarBlock &&
                !(level.getBlockState(current.below()).getBlock() instanceof AshenKingPillarBlock)) {
            current = current.below();
        }
        return current;
    }

    private BlockPos findTopBlock(Level level, BlockPos pos) {
        BlockPos current = pos;
        while (level.getBlockState(current.above()).getBlock() instanceof PillarBlock &&
                !(level.getBlockState(current.above()).getBlock() instanceof AshenKingPillarBlock)) {
            current = current.above();
        }
        return current;
    }
    public static Map.Entry<String, String> getDyeColorAndName(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return null;
        }

        net.minecraft.world.item.DyeColor dye = Services.PLATFORM.getDyeColor(stack);
        if (dye == null) {
            return null;
        }

        return switch (dye) {
            case WHITE -> new AbstractMap.SimpleEntry<>("#E8FEFD", "White");
            case ORANGE -> new AbstractMap.SimpleEntry<>("#FF5C00", "Orange");
            case MAGENTA -> new AbstractMap.SimpleEntry<>("#FF00FF", "Magenta");
            case LIGHT_BLUE -> new AbstractMap.SimpleEntry<>("#3CDFFF", "Light Blue");
            case YELLOW -> new AbstractMap.SimpleEntry<>("#FFFF00", "Yellow");
            case LIME -> new AbstractMap.SimpleEntry<>("#BFFE00", "Lime");
            case PINK -> new AbstractMap.SimpleEntry<>("#F686B7", "Pink");
            case GRAY -> new AbstractMap.SimpleEntry<>("#232526", "Gray");
            case LIGHT_GRAY -> new AbstractMap.SimpleEntry<>("#B1B8C5", "Light Gray");
            case CYAN -> new AbstractMap.SimpleEntry<>("#00FFFF", "Cyan");
            case PURPLE -> new AbstractMap.SimpleEntry<>("#AB87FF", "Purple");
            case BLUE -> new AbstractMap.SimpleEntry<>("#1919EA", "Blue");
            case BROWN -> new AbstractMap.SimpleEntry<>("#411900", "Brown");
            case GREEN -> new AbstractMap.SimpleEntry<>("#39FF14", "Green");
            case RED -> new AbstractMap.SimpleEntry<>("#FF0000", "Red");
            case BLACK -> new AbstractMap.SimpleEntry<>("#07010C", "Black");
        };
    }

    private static String getDirectionName(float yaw) {
        yaw = yaw % 360.0f;
        if (yaw < 0.0f) {
            yaw += 360.0f;
        }

        if (yaw >= 315.0f || yaw < 45.0f) {
            return "South";
        } else if (yaw >= 45.0f && yaw < 135.0f) {
            return "West";
        } else if (yaw >= 135.0f && yaw < 225.0f) {
            return "North";
        } else {
            return "East";
        }
    }

    private static ChatFormatting getPatternColor(String pattern) {
        if (pattern == null) {
            return ChatFormatting.WHITE;
        }

        return switch (pattern) {
            case "default" -> ChatFormatting.WHITE;
            case "beam" -> ChatFormatting.AQUA;
            case "spiral" -> ChatFormatting.LIGHT_PURPLE;
            case "fountain" -> ChatFormatting.BLUE;
            case "pulse" -> ChatFormatting.RED;
            case "ring" -> ChatFormatting.GOLD;
            case "burst" -> ChatFormatting.YELLOW;
            default -> ChatFormatting.GRAY;
        };
    }


    @Override
    public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 1.0F;
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return false;
    }
}
