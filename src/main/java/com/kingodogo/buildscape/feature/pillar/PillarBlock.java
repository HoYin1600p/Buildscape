package com.kingodogo.buildscape.feature.pillar;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.advancement.AdvancementEvents;
import com.kingodogo.buildscape.config.PillarParticleConfig;
import com.kingodogo.buildscape.core.DyeColors;
import com.kingodogo.buildscape.core.NbtKeys;
import com.kingodogo.buildscape.core.ParticlePatterns;
import com.kingodogo.buildscape.network.ActionBarMessagePacket;
import com.kingodogo.buildscape.network.ModMessages;
import com.kingodogo.buildscape.registry.ModBlockEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public class PillarBlock
        extends Block
        implements EntityBlock, SimpleWaterloggedBlock {

    public static final BooleanProperty WATERLOGGED =
            BlockStateProperties.WATERLOGGED;
    private static final ThreadLocal<Float> PLACING_PLAYER_YAW =
            new ThreadLocal<>();

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
    public static final EnumProperty<PillarPart> PART = EnumProperty.create(
            "part",
            PillarPart.class
    );

    public PillarBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(PART, PillarPart.SINGLE)
                        .setValue(WATERLOGGED, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(
            StateDefinition.Builder<Block, BlockState> builder
    ) {
        builder.add(PART, WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState above = level.getBlockState(pos.above());
        BlockState below = level.getBlockState(pos.below());

        if (!level.isClientSide) {
            Player player = context.getPlayer();
            if (player != null) {
                float playerYaw = player.getYRot();
                float facingYaw = (playerYaw + 180.0f) % 360.0f;
                if (facingYaw < 0) {
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

        FluidState fluidstate = context
                .getLevel()
                .getFluidState(context.getClickedPos());
        return this.defaultBlockState()
                .setValue(PART, part)
                .setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
    }

    @Override
    public BlockState updateShape(
            BlockState state,
            Direction direction,
            BlockState neighborState,
            LevelAccessor level,
            BlockPos pos,
            BlockPos neighborPos
    ) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        if (direction == Direction.UP || direction == Direction.DOWN) {
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

            return state.setValue(PART, newPart);
        }
        return super.updateShape(
                state,
                direction,
                neighborState,
                level,
                pos,
                neighborPos
        );
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return super.canSurvive(state, level, pos);
    }

    @Override
    public void onPlace(
            BlockState state,
            Level level,
            BlockPos pos,
            BlockState oldState,
            boolean isMoving
    ) {

        if (!level.isClientSide) {
            if (!oldState.is(state.getBlock())) {
                ejectNeighborDisplayItem(level, pos, state);
            }
            enforceSingleItemPerStack(level, pos);
            syncNewPillarWithStack(level, pos, !oldState.is(state.getBlock()));

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
                level.sendBlockUpdated(
                        above,
                        level.getBlockState(above),
                        level.getBlockState(above),
                        3
                );
            }
            if (level.getBlockState(below).getBlock() instanceof PillarBlock) {
                level.sendBlockUpdated(
                        below,
                        level.getBlockState(below),
                        level.getBlockState(below),
                        3
                );
            }
        }
    }

    private void syncNewPillarWithStack(Level level, BlockPos pos, boolean newlyPlaced) {
        if (
                !level.isClientSide &&
                        !BuildScape.isServerFullyInitialized()
        ) {
            return;
        }

        if (
                !level.isClientSide &&
                        (level.getServer() == null ||
                                !level.getServer().isRunning() ||
                                level.getServer().getPlayerList().getPlayerCount() == 0)
        ) {
            return;
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof PillarBlockEntity newPillarBE)) return;

        BlockPos above = pos.above();
        BlockPos below = pos.below();
        PillarBlockEntity existingPillar = null;

        try {
            if (!level.isClientSide) {
                if (level.hasChunkAt(above)) {
                    ChunkAccess chunk = level.getChunk(
                            above
                    );
                    if (
                            chunk instanceof LevelChunk &&
                                    chunk
                                            .getStatus()
                                            .isOrAfter(ChunkStatus.FULL)
                    ) {
                        if (level.getBlockState(above).getBlock() instanceof PillarBlock) {
                            BlockEntity aboveBE = level.getBlockEntity(above);
                            if (
                                    aboveBE instanceof PillarBlockEntity pillarBE &&
                                            pillarBE.hasCustomColors()
                            ) {
                                existingPillar = pillarBE;
                            }
                        }
                    }
                }

                if (existingPillar == null && level.hasChunkAt(below)) {
                    ChunkAccess chunk = level.getChunk(
                            below
                    );
                    if (
                            chunk instanceof LevelChunk &&
                                    chunk
                                            .getStatus()
                                            .isOrAfter(ChunkStatus.FULL)
                    ) {
                        if (level.getBlockState(below).getBlock() instanceof PillarBlock) {
                            BlockEntity belowBE = level.getBlockEntity(below);
                            if (
                                    belowBE instanceof PillarBlockEntity pillarBE &&
                                            pillarBE.hasCustomColors()
                            ) {
                                existingPillar = pillarBE;
                            }
                        }
                    }
                }
            } else {
                if (level.getBlockState(above).getBlock() instanceof PillarBlock) {
                    BlockEntity aboveBE = level.getBlockEntity(above);
                    if (
                            aboveBE instanceof PillarBlockEntity pillarBE &&
                                    pillarBE.hasCustomColors()
                    ) {
                        existingPillar = pillarBE;
                    }
                }
                if (
                        existingPillar == null &&
                                level.getBlockState(below).getBlock() instanceof PillarBlock
                ) {
                    BlockEntity belowBE = level.getBlockEntity(below);
                    if (
                            belowBE instanceof PillarBlockEntity pillarBE &&
                                    pillarBE.hasCustomColors()
                    ) {
                        existingPillar = pillarBE;
                    }
                }
            }
        } catch (Exception e) {
            return;
        }

        if (existingPillar != null) {
            String stackId = existingPillar.getPillarId();
            List<String> stackColors = existingPillar.getParticleColors();

            if (stackId != null && stackColors != null && !stackColors.isEmpty()) {
                if (
                        !level.isClientSide &&
                                BuildScape.isServerFullyInitialized()
                ) {
                    PillarIdManager manager =
                            PillarIdManager.get(level);
                    PillarIdManager.PillarData data =
                            manager.getPillarData(stackId);

                    if (data != null) {
                        BlockPos bottom = findBottomBlock(level, pos);
                        if (newlyPlaced) {
                            BlockPos top = findTopBlock(level, pos);
                            manager.mergePillarStack(stackId, bottom, top);
                            for (BlockPos current = bottom; current.getY() <= top.getY(); current = current.above()) {
                                if (level.getBlockEntity(current) instanceof PillarBlockEntity stackPillarBE) {
                                    stackPillarBE.syncFromData(data);
                                    stackPillarBE.forceSetColors(stackColors, stackId);
                                }
                            }
                        } else {
                            manager.updatePillarPosition(stackId, bottom);
                        }
                    }
                }

                newPillarBE.forceSetColors(stackColors, stackId);
            }
        }
    }

    @Override
    public void onRemove(
            BlockState state,
            Level level,
            BlockPos pos,
            BlockState newState,
            boolean isMoving
    ) {
        if (!level.isClientSide && !state.is(newState.getBlock())
                && level.getBlockEntity(pos) instanceof PillarBlockEntity droppingBE
                && droppingBE.hasDisplayItem()) {
            ItemStack displayedItem = droppingBE.getDisplayedItem().copy();
            if (!displayedItem.isEmpty()) {
                ItemEntity itemEntity =
                        new ItemEntity(
                                level,
                                pos.getX() + 0.5,
                                pos.getY() + 0.5,
                                pos.getZ() + 0.5,
                                displayedItem
                        );
                itemEntity.setDefaultPickUpDelay();
                level.addFreshEntity(itemEntity);
            }
        }

        if (
                !level.isClientSide &&
                        !state.is(newState.getBlock()) &&
                        BuildScape.isServerFullyInitialized()
        ) {
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
            PillarIdManager manager =
                    PillarIdManager.get(level);
            BlockPos firstRemainingPillar = manager.removePillarBlock(
                    level, pillarIdToPreserve, pos, bottom, top);

            if (firstRemainingPillar != null) {
                BlockEntity remainingBe = level.getBlockEntity(firstRemainingPillar);
                if (remainingBe instanceof PillarBlockEntity remainingPillarBE) {
                    if (
                            colorsToPreserve != null &&
                                    !colorsToPreserve.isEmpty() &&
                                    pillarIdToPreserve != null
                    ) {
                        remainingPillarBE.forceSetColors(
                                colorsToPreserve,
                                pillarIdToPreserve
                        );

                        BlockPos current = bottom;
                        while (current.getY() <= top.getY()) {
                            if (!current.equals(pos)) {
                                BlockEntity currentBe = level.getBlockEntity(current);
                                if (
                                        currentBe instanceof PillarBlockEntity stackPillarBE &&
                                                !current.equals(firstRemainingPillar)
                                ) {
                                    stackPillarBE.forceSetColors(
                                            colorsToPreserve,
                                            pillarIdToPreserve
                                    );
                                }
                            }
                            if (current.equals(top)) break;
                            current = current.above();
                        }
                    }
                }

                manager.removeStalePillarPositionIndex(level, pos);
            }
        }

        super.onRemove(state, level, pos, newState, isMoving);

        if (!level.isClientSide) {
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
    }

    @Override
    public void neighborChanged(
            BlockState state,
            Level level,
            BlockPos pos,
            Block block,
            BlockPos neighborPos,
            boolean isMoving
    ) {
        if (neighborPos.equals(pos.above()) || neighborPos.equals(pos.below())) {
            if (!level.isClientSide) {
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
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PillarBlockEntity(pos, state);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide) {
            PillarIdManager.get(level).getOrCreatePillarData(level, pos);

            if (stack.hasTag()) {
                CompoundTag tag = stack.getTag();
                CompoundTag placementTag = null;
                if (tag.contains(NbtKeys.PILLAR_ITEM, 8) || tag.contains(NbtKeys.PILLAR_PATTERN, 8)) {
                    placementTag = tag;
                } else if (tag.contains(NbtKeys.BLOCK_ENTITY_TAG, 10)) {
                    CompoundTag beTag = tag.getCompound(NbtKeys.BLOCK_ENTITY_TAG);
                    if (beTag.contains(NbtKeys.PILLAR_ITEM, 8) || beTag.contains(NbtKeys.PILLAR_PATTERN, 8)) {
                        placementTag = beTag;
                    }
                }
                if (placementTag != null && level.getBlockEntity(pos) instanceof PillarBlockEntity pillarBE) {
                    applyPlacementNbt(pillarBE, placementTag);
                    level.sendBlockUpdated(pos, state, state, 3);
                }
            }
        }
    }

    private static void applyPlacementNbt(PillarBlockEntity pillarBE, CompoundTag tag) {
        if (tag.contains(NbtKeys.PILLAR_ITEM, 8)) {
            ResourceLocation itemLocation =
                    ResourceLocation.tryParse(tag.getString(NbtKeys.PILLAR_ITEM));
            Item item = itemLocation == null ? null
                    : ForgeRegistries.ITEMS.getValue(itemLocation);
            if (item != null && item != Items.AIR) {
                pillarBE.setDisplayedItem(new ItemStack(item));
            }
        }
        if (tag.contains(NbtKeys.PILLAR_PATTERN, 8)) {
            pillarBE.setParticlePattern(tag.getString(NbtKeys.PILLAR_PATTERN));
        }
        pillarBE.setChanged();
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
            ItemEntity itemEntity =
                    new ItemEntity(
                            level,
                            pos.getX() + 0.5,
                            pos.getY() + 0.5,
                            pos.getZ() + 0.5,
                            item
                    );
            itemEntity.setDefaultPickUpDelay();
            level.addFreshEntity(itemEntity);
        }
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        ItemStack stack = super.getCloneItemStack(state, target, level, pos, player);

        if (player != null && player.isShiftKeyDown()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof PillarBlockEntity pillarBE) {
                boolean hasCustomData = false;
                CompoundTag beTag = new CompoundTag();

                if (pillarBE.hasDisplayItem()) {
                    ItemStack displayedItem = pillarBE.getDisplayedItem();
                    if (!displayedItem.isEmpty()) {
                        ResourceLocation itemId =
                                ForgeRegistries.ITEMS.getKey(displayedItem.getItem());
                        if (itemId != null) {
                            beTag.putString(NbtKeys.PILLAR_ITEM, itemId.toString());
                            hasCustomData = true;
                        }
                    }
                }

                String pattern = pillarBE.getParticlePattern();
                if (pattern != null && !pattern.isEmpty()) {
                    beTag.putString(NbtKeys.PILLAR_PATTERN, pattern);
                    hasCustomData = true;
                }

                if (hasCustomData) {
                    CompoundTag tag = stack.getOrCreateTag();
                    tag.put(NbtKeys.BLOCK_ENTITY_TAG, beTag);
                    stack.setTag(tag);
                }
            }
        }

        return stack;
    }

    @Override
    public <
            T extends BlockEntity
            > BlockEntityTicker<T> getTicker(
            Level level,
            BlockState state,
            BlockEntityType<T> type
    ) {
        if (type == ModBlockEntities.PILLAR_BLOCK_ENTITY.get()) {
            return level.isClientSide
                    ? (lvl, p, s, t) ->
                    PillarBlockEntity.clientTick(lvl, p, s, (PillarBlockEntity) t)
                    : (lvl, p, s, t) ->
                    PillarBlockEntity.serverTick(lvl, p, s, (PillarBlockEntity) t);
        }
        return null;
    }

    @Override
    public InteractionResult use(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hit
    ) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof PillarBlockEntity pillarBE)) {
            return InteractionResult.PASS;
        }

        ItemStack heldItem = player.getItemInHand(hand);

        if (!heldItem.isEmpty()) {
            Map.Entry<String, String> dyeInfo = getDyeColorAndName(
                    heldItem
            );
            if (dyeInfo != null) {
                if (stackHasAnyItem(level, pos)) {
                    String dyeColor = dyeInfo.getKey();
                    String dyeName = dyeInfo.getValue();

                    if (!pillarBE.canAddMoreColors()) {
                        TextComponent message =
                                new TextComponent(
                                        "Pillar already has " +
                                                PillarBlockEntity.MAX_DYE_COLORS +
                                                " colors! Break and replace to reset."
                                );
                        message.withStyle(ChatFormatting.RED);
                        if (player instanceof ServerPlayer) {
                            ModMessages.INSTANCE.send(
                                    PacketDistributor.PLAYER.with(() ->
                                            (ServerPlayer) player
                                    ),
                                    new ActionBarMessagePacket(
                                            message
                                    )
                            );
                        }
                        return InteractionResult.CONSUME;
                    }

                    boolean added = pillarBE.addParticleColor(dyeColor);
                    if (!added) {
                        return InteractionResult.PASS;
                    }

                    if (!player.getAbilities().instabuild) {
                        heldItem.shrink(1);
                    }

                    level.playSound(
                            null,
                            pos,
                            SoundEvents.DYE_USE,
                            SoundSource.BLOCKS,
                            1.0f,
                            1.0f
                    );

                    String pillarId = pillarBE.getPillarId();
                    int colorCount = pillarBE.getDyeColorCount();

                    String progressText =
                            " (" + colorCount + "/" + PillarBlockEntity.MAX_DYE_COLORS + ")";
                    TextComponent message;
                    if (pillarId != null) {
                        message = new TextComponent(
                                "[" + pillarId + "] Dyed " + dyeName + progressText
                        );
                    } else {
                        message = new TextComponent(
                                "Pillar Dyed " + dyeName + progressText
                        );
                    }

                    if (player instanceof ServerPlayer) {
                        ModMessages.INSTANCE.send(
                                PacketDistributor.PLAYER.with(() ->
                                        (ServerPlayer) player
                                ),
                                new ActionBarMessagePacket(
                                        message
                                )
                        );
                    }

                    return InteractionResult.SUCCESS;
                }
            }
        }

        if (player.isShiftKeyDown()) {
            ItemStack displayedItem = pillarBE.getDisplayedItem();
            boolean isSpawnEgg =
                    !displayedItem.isEmpty() &&
                            displayedItem.getItem() instanceof
                                    SpawnEggItem;

            if (isSpawnEgg) {
                pillarBE.rotateFacing();
                float facingYaw = pillarBE.getFacingYaw();

                String direction = getDirectionName(facingYaw);
                TextComponent message =
                        new TextComponent(
                                "Mob facing: " + direction
                        );
                message.withStyle(ChatFormatting.GREEN);
                player.displayClientMessage(message, true);

                level.playSound(
                        null,
                        pos,
                        SoundEvents.UI_BUTTON_CLICK,
                        SoundSource.BLOCKS,
                        0.5f,
                        1.0f
                );
                return InteractionResult.SUCCESS;
            } else {
                pillarBE.cycleParticlePattern();
                String pattern = pillarBE.getParticlePattern();
                if (pattern == null) {
                    pattern = PillarParticleConfig.get()
                            .pattern;
                }

                ChatFormatting color = getPatternColor(pattern);

                TextComponent patternText =
                        (TextComponent) new TextComponent(
                                pattern
                        ).withStyle(color);
                player.displayClientMessage(patternText, true);

                level.playSound(
                        null,
                        pos,
                        SoundEvents.UI_BUTTON_CLICK,
                        SoundSource.BLOCKS,
                        0.5f,
                        1.0f
                );
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
                        level.sendBlockUpdated(
                                topBlockPos,
                                level.getBlockState(topBlockPos),
                                level.getBlockState(topBlockPos),
                                3
                        );
                        ItemEntity itemEntity =
                                new ItemEntity(
                                        level,
                                        pos.getX() + 0.5,
                                        pos.getY() + 1.0,
                                        pos.getZ() + 0.5,
                                        displayedItem.copy()
                                );
                        itemEntity.setDefaultPickUpDelay();
                        level.addFreshEntity(itemEntity);
                        level.playSound(
                                null,
                                pos,
                                SoundEvents.ITEM_FRAME_REMOVE_ITEM,
                                SoundSource.BLOCKS,
                                1.0f,
                                1.0f
                        );
                        return InteractionResult.SUCCESS;
                    }
                }
                return handleItemInteraction(
                        topPillarBE,
                        level,
                        topBlockPos,
                        player,
                        hand
                );
            }
            return InteractionResult.PASS;
        }

        if (heldItem.isEmpty() && pillarBE.hasDisplayItem()) {
            ItemStack displayedItem = pillarBE.getDisplayedItem();
            if (!displayedItem.isEmpty()) {
                pillarBE.setDisplayedItem(ItemStack.EMPTY);
                level.sendBlockUpdated(
                        pos,
                        level.getBlockState(pos),
                        level.getBlockState(pos),
                        3
                );
                ItemEntity itemEntity =
                        new ItemEntity(
                                level,
                                pos.getX() + 0.5,
                                pos.getY() + 1.0,
                                pos.getZ() + 0.5,
                                displayedItem.copy()
                        );
                itemEntity.setDefaultPickUpDelay();
                level.addFreshEntity(itemEntity);
                level.playSound(
                        null,
                        pos,
                        SoundEvents.ITEM_FRAME_REMOVE_ITEM,
                        SoundSource.BLOCKS,
                        1.0f,
                        1.0f
                );
                return InteractionResult.SUCCESS;
            }
        }

        return handleItemInteraction(pillarBE, level, pos, player, hand);
    }

    private InteractionResult handleItemInteraction(
            PillarBlockEntity blockEntity,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand
    ) {
        ItemStack heldItem = player.getItemInHand(hand);

        if (!heldItem.isEmpty() && stackHasAnyItem(level, pos)) {
            return InteractionResult.PASS;
        }

        if (!heldItem.isEmpty()) {
            ItemStack displayItem = heldItem.copy();
            displayItem.setCount(1);

            float playerYaw = player.getYRot();
            float facingYaw = (playerYaw + 180.0f) % 360.0f;
            if (facingYaw < 0) {
                facingYaw += 360.0f;
            }
            blockEntity.setDisplayedItem(displayItem, facingYaw);

            if (player instanceof ServerPlayer serverPlayer) {
                AdvancementEvents.onPillarItemInserted(serverPlayer);
            }

            if (blockEntity.getParticlePattern() == null) {
                PillarParticleConfig cfg =
                        PillarParticleConfig.get();
                if (cfg.use_pattern && cfg.pattern != null) {
                }
            }

            if (!player.getAbilities().instabuild) {
                heldItem.shrink(1);
            }

            enforceSingleItemPerStack(level, pos);

            level.playSound(
                    null,
                    pos,
                    SoundEvents.ITEM_FRAME_ADD_ITEM,
                    SoundSource.BLOCKS,
                    1.0f,
                    1.0f
            );
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    private boolean stackHasAnyItem(Level level, BlockPos pos) {
        BlockPos bottom = findBottomBlock(level, pos);
        BlockPos current = bottom;
        while (level.getBlockState(current).getBlock() instanceof PillarBlock) {
            BlockEntity be = level.getBlockEntity(current);
            if (
                    be instanceof PillarBlockEntity pillarBE && pillarBE.hasDisplayItem()
            ) {
                return true;
            }
            current = current.above();
        }
        return false;
    }

    private void enforceSingleItemPerStack(Level level, BlockPos anyPosInStack) {
        if (level.isClientSide) return;
        BlockPos top = findTopBlock(level, anyPosInStack);
        BlockPos bottom = findBottomBlock(level, anyPosInStack);
        if (!(level.getBlockEntity(top) instanceof PillarBlockEntity topBE)) return;

        ItemStack retained = topBE.getDisplayedItem().copy();
        BlockPos current = bottom;
        while (level.getBlockState(current).getBlock() instanceof PillarBlock &&
               !(level.getBlockState(current).getBlock() instanceof AshenKingPillarBlock)) {
            if (!current.equals(top)
                    && level.getBlockEntity(current) instanceof PillarBlockEntity pillarBE
                    && pillarBE.hasDisplayItem()) {
                ItemStack displaced = pillarBE.getDisplayedItem().copy();
                pillarBE.setDisplayedItem(ItemStack.EMPTY);
                if (retained.isEmpty()) {
                    retained = displaced;
                } else {
                    Block.popResource(level, current, displaced);
                }
            }
            current = current.above();
        }
        topBE.setDisplayedItem(retained);
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

    private Map.Entry<String, String> getDyeColorAndName(
            ItemStack stack
    ) {
        if (stack == null || stack.isEmpty()) {
            return null;
        }

        DyeColors dye = DyeColors.fromDyeItem(stack.getItem());
        if (dye != null) {
            return new AbstractMap.SimpleEntry<>(dye.hex(), dye.displayName());
        }

        return null;
    }

    private String getDyeColor(ItemStack stack) {
        Map.Entry<String, String> result = getDyeColorAndName(stack);
        return result != null ? result.getKey() : null;
    }

    private String getDirectionName(float yaw) {
        yaw = yaw % 360.0f;
        if (yaw < 0) {
            yaw += 360.0f;
        }

        if (yaw >= 315 || yaw < 45) {
            return "South";
        } else if (yaw >= 45 && yaw < 135) {
            return "West";
        } else if (yaw >= 135 && yaw < 225) {
            return "North";
        } else {
            return "East";
        }
    }

    private ChatFormatting getPatternColor(String pattern) {
        if (pattern == null) {
            return ChatFormatting.WHITE;
        }

        switch (pattern) {
            case ParticlePatterns.DEFAULT:
                return ChatFormatting.WHITE;
            case ParticlePatterns.BEAM:
                return ChatFormatting.AQUA;
            case ParticlePatterns.SPIRAL:
                return ChatFormatting.LIGHT_PURPLE;
            case ParticlePatterns.FOUNTAIN:
                return ChatFormatting.BLUE;
            case ParticlePatterns.PULSE:
                return ChatFormatting.RED;
            case ParticlePatterns.RING:
                return ChatFormatting.GOLD;
            case ParticlePatterns.BURST:
                return ChatFormatting.YELLOW;
            default:
                return ChatFormatting.GRAY;
        }
    }

    @Override
    public VoxelShape getShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        return switch (state.getValue(PART)) {
            case SINGLE -> SHAPE_SINGLE;
            case BOTTOM -> SHAPE_BOTTOM;
            case MIDDLE -> SHAPE_MIDDLE;
            case TOP -> SHAPE_TOP;
        };
    }

    @Override
    public VoxelShape getCollisionShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        return getShape(state, level, pos, context);
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return 0;
    }

    @Override
    public boolean propagatesSkylightDown(
            BlockState state,
            BlockGetter reader,
            BlockPos pos
    ) {
        return true;
    }

    @Override
    public float getShadeBrightness(
            BlockState state,
            BlockGetter level,
            BlockPos pos
    ) {
        return 1.0F;
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return false;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED)
                ? Fluids.WATER.getSource(false)
                : super.getFluidState(state);
    }
}
