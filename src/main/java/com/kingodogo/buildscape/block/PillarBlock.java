package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

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
            net.minecraft.world.entity.player.Player player = context.getPlayer();
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

        // Only stack with regular PillarBlocks, NOT AshenKingPillars (they're independent)
        boolean hasAbove = above.getBlock() instanceof PillarBlock && !(above.getBlock() instanceof AshenKingPillarBlock);
        boolean hasBelow = below.getBlock() instanceof PillarBlock && !(below.getBlock() instanceof AshenKingPillarBlock);

        if (!level.isClientSide) {
            // Only transfer items from stacked regular pillars, NOT AshenKingPillars
            BlockState belowState = level.getBlockState(pos.below());
            BlockState aboveState = level.getBlockState(pos.above());

            if (hasBelow && !hasAbove && !(belowState.getBlock() instanceof AshenKingPillarBlock)) {
                if (
                        level.getBlockEntity(pos.below()) instanceof PillarBlockEntity be &&
                                be.hasDisplayItem()
                ) {
                    ItemStack item = be.getDisplayedItem().copy();
                    be.setDisplayedItem(ItemStack.EMPTY);
                    net.minecraft.world.entity.item.ItemEntity itemEntity =
                            new net.minecraft.world.entity.item.ItemEntity(
                                    level,
                                    pos.getX() + 0.5,
                                    pos.getY() + 0.5,
                                    pos.getZ() + 0.5,
                                    item
                            );
                    itemEntity.setDefaultPickUpDelay();
                    level.addFreshEntity(itemEntity);
                }
            } else if (hasAbove && !hasBelow && !(aboveState.getBlock() instanceof AshenKingPillarBlock)) {
                if (
                        level.getBlockEntity(pos.above()) instanceof PillarBlockEntity be &&
                                be.hasDisplayItem()
                ) {
                    ItemStack item = be.getDisplayedItem().copy();
                    be.setDisplayedItem(ItemStack.EMPTY);
                    net.minecraft.world.entity.item.ItemEntity itemEntity =
                            new net.minecraft.world.entity.item.ItemEntity(
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
        }

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
            // Only stack with regular PillarBlocks, NOT AshenKingPillars (they're independent)
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
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof PillarBlockEntity pillarBE) {
                Float storedYaw = PLACING_PLAYER_YAW.get();
                if (storedYaw != null) {
                    pillarBE.setFacingYaw(storedYaw);
                    PLACING_PLAYER_YAW.set(null);
                }
            }

            enforceSingleItemPerStack(level, pos);

            syncNewPillarWithStack(level, pos);

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

    private void syncNewPillarWithStack(Level level, BlockPos pos) {
        if (
                !level.isClientSide &&
                        !com.kingodogo.buildscape.BuildScape.isServerFullyInitialized()
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
                    net.minecraft.world.level.chunk.ChunkAccess chunk = level.getChunk(
                            above
                    );
                    if (
                            chunk instanceof net.minecraft.world.level.chunk.LevelChunk &&
                                    chunk
                                            .getStatus()
                                            .isOrAfter(net.minecraft.world.level.chunk.ChunkStatus.FULL)
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
                    net.minecraft.world.level.chunk.ChunkAccess chunk = level.getChunk(
                            below
                    );
                    if (
                            chunk instanceof net.minecraft.world.level.chunk.LevelChunk &&
                                    chunk
                                            .getStatus()
                                            .isOrAfter(net.minecraft.world.level.chunk.ChunkStatus.FULL)
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
            java.util.List<String> stackColors = existingPillar.getParticleColors();

            if (stackId != null && stackColors != null && !stackColors.isEmpty()) {
                if (
                        !level.isClientSide &&
                                com.kingodogo.buildscape.BuildScape.isServerFullyInitialized()
                ) {
                    com.kingodogo.buildscape.config.PillarIdManager manager =
                            com.kingodogo.buildscape.config.PillarIdManager.get();
                    com.kingodogo.buildscape.config.PillarIdManager.PillarData data =
                            manager.getPillarData(stackId);

                    if (data != null) {
                        BlockPos bottom = findBottomBlock(level, pos);
                        data.x = bottom.getX();
                        data.y = bottom.getY();
                        data.z = bottom.getZ();
                        manager.saveImmediate();
                    }
                }

                newPillarBE.forceSetColors(stackColors, stackId);
            }
        }
    }

    @Override
    public List<ItemStack> getDrops(
            BlockState state,
            LootContext.Builder builder
    ) {
        List<ItemStack> drops = super.getDrops(state, builder);

        LootContext lootContext = builder
                .withParameter(
                        net.minecraft.world.level.storage.loot.parameters.LootContextParams.BLOCK_STATE,
                        state
                )
                .create(
                        net.minecraft.world.level.storage.loot.parameters.LootContextParamSets.BLOCK
                );

        net.minecraft.world.phys.Vec3 origin = lootContext.getParamOrNull(
                net.minecraft.world.level.storage.loot.parameters.LootContextParams.ORIGIN
        );
        if (origin != null) {
            BlockPos pos = new BlockPos(origin);
            net.minecraft.world.level.Level level = lootContext.getLevel();
            if (level != null) {
                BlockEntity be = level.getBlockEntity(pos);
                if (
                        be instanceof PillarBlockEntity pillarBE && pillarBE.hasDisplayItem()
                ) {
                    ItemStack displayedItem = pillarBE.getDisplayedItem().copy();
                    if (!displayedItem.isEmpty()) {
                        drops.add(displayedItem);
                    }
                }
            }
        }

        return drops;
    }

    @Override
    public void onRemove(
            BlockState state,
            Level level,
            BlockPos pos,
            BlockState newState,
            boolean isMoving
    ) {
        if (
                !level.isClientSide &&
                        !state.is(newState.getBlock()) &&
                        com.kingodogo.buildscape.BuildScape.isServerFullyInitialized()
        ) {
            BlockEntity be = level.getBlockEntity(pos);
            String pillarIdToPreserve = null;
            java.util.List<String> colorsToPreserve = null;

            if (be instanceof PillarBlockEntity pillarBE) {
                pillarIdToPreserve = pillarBE.getPillarId();
                colorsToPreserve = pillarBE.getParticleColors();

                if (pillarBE.hasDisplayItem()) {
                    ItemStack displayedItem = pillarBE.getDisplayedItem().copy();
                    if (!displayedItem.isEmpty()) {
                        net.minecraft.world.entity.item.ItemEntity itemEntity =
                                new net.minecraft.world.entity.item.ItemEntity(
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
                if (current.equals(top)) break;
                current = current.above();
            }

            if (hasOtherPillars && firstRemainingPillar != null) {
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

                        com.kingodogo.buildscape.config.PillarIdManager manager =
                                com.kingodogo.buildscape.config.PillarIdManager.get();
                        com.kingodogo.buildscape.config.PillarIdManager.PillarData data =
                                manager.getPillarData(pillarIdToPreserve);
                        if (data != null) {
                            data.x = firstRemainingPillar.getX();
                            data.y = firstRemainingPillar.getY();
                            data.z = firstRemainingPillar.getZ();
                            manager.saveImmediate();
                        }

                        current = bottom;
                        while (
                                level.getBlockState(current).getBlock() instanceof PillarBlock
                        ) {
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

                com.kingodogo.buildscape.config.PillarIdManager manager =
                        com.kingodogo.buildscape.config.PillarIdManager.get();
                manager.removePillarByPosition(level, pos);
            } else {
                com.kingodogo.buildscape.config.PillarIdManager manager =
                        com.kingodogo.buildscape.config.PillarIdManager.get();
                if (pillarIdToPreserve != null && !pillarIdToPreserve.isEmpty()) {
                    manager.removePillar(pillarIdToPreserve);
                }
                manager.removePillarByPosition(level, pos);
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
                // Only stack with regular PillarBlocks, NOT AshenKingPillars (they're independent)
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
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @javax.annotation.Nullable net.minecraft.world.entity.LivingEntity placer, net.minecraft.world.item.ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide) {
            com.kingodogo.buildscape.config.PillarIdManager.get().getOrCreatePillarData(level, pos);
        }
    }

    @Override
    public <
            T extends BlockEntity
            > net.minecraft.world.level.block.entity.BlockEntityTicker<T> getTicker(
            Level level,
            BlockState state,
            net.minecraft.world.level.block.entity.BlockEntityType<T> type
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
            net.minecraft.world.phys.BlockHitResult hit
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
            java.util.Map.Entry<String, String> dyeInfo = getDyeColorAndName(
                    heldItem
            );
            if (dyeInfo != null) {
                if (stackHasAnyItem(level, pos)) {
                    String dyeColor = dyeInfo.getKey();
                    String dyeName = dyeInfo.getValue();

                    if (!pillarBE.canAddMoreColors()) {
                        net.minecraft.network.chat.TextComponent message =
                                new net.minecraft.network.chat.TextComponent(
                                        "Pillar already has " +
                                                PillarBlockEntity.MAX_DYE_COLORS +
                                                " colors! Break and replace to reset."
                                );
                        message.withStyle(net.minecraft.ChatFormatting.RED);
                        if (player instanceof net.minecraft.server.level.ServerPlayer) {
                            com.kingodogo.buildscape.network.ModMessages.INSTANCE.send(
                                    net.minecraftforge.network.PacketDistributor.PLAYER.with(() ->
                                            (net.minecraft.server.level.ServerPlayer) player
                                    ),
                                    new com.kingodogo.buildscape.network.ActionBarMessagePacket(
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
                            net.minecraft.sounds.SoundEvents.DYE_USE,
                            net.minecraft.sounds.SoundSource.BLOCKS,
                            1.0f,
                            1.0f
                    );

                    String pillarId = pillarBE.getPillarId();
                    int colorCount = pillarBE.getDyeColorCount();

                    String progressText =
                            " (" + colorCount + "/" + PillarBlockEntity.MAX_DYE_COLORS + ")";
                    net.minecraft.network.chat.TextComponent message;
                    if (pillarId != null) {
                        message = new net.minecraft.network.chat.TextComponent(
                                "[" + pillarId + "] Dyed " + dyeName + progressText
                        );
                    } else {
                        message = new net.minecraft.network.chat.TextComponent(
                                "Pillar Dyed " + dyeName + progressText
                        );
                    }

                    if (player instanceof net.minecraft.server.level.ServerPlayer) {
                        com.kingodogo.buildscape.network.ModMessages.INSTANCE.send(
                                net.minecraftforge.network.PacketDistributor.PLAYER.with(() ->
                                        (net.minecraft.server.level.ServerPlayer) player
                                ),
                                new com.kingodogo.buildscape.network.ActionBarMessagePacket(
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
                                    net.minecraft.world.item.SpawnEggItem;

            if (isSpawnEgg) {
                pillarBE.rotateFacing();
                float facingYaw = pillarBE.getFacingYaw();

                String direction = getDirectionName(facingYaw);
                net.minecraft.network.chat.TextComponent message =
                        new net.minecraft.network.chat.TextComponent(
                                "Mob facing: " + direction
                        );
                message.withStyle(net.minecraft.ChatFormatting.GREEN);
                player.displayClientMessage(message, true);

                level.playSound(
                        null,
                        pos,
                        net.minecraft.sounds.SoundEvents.UI_BUTTON_CLICK,
                        net.minecraft.sounds.SoundSource.BLOCKS,
                        0.5f,
                        1.0f
                );
                return InteractionResult.SUCCESS;
            } else {
                pillarBE.cycleParticlePattern();
                String pattern = pillarBE.getParticlePattern();
                if (pattern == null) {
                    pattern = com.kingodogo.buildscape.config.PillarParticleConfig.get()
                            .pattern;
                }

                net.minecraft.ChatFormatting color = getPatternColor(pattern);

                net.minecraft.network.chat.TextComponent patternText =
                        (net.minecraft.network.chat.TextComponent) new net.minecraft.network.chat.TextComponent(
                                pattern
                        ).withStyle(color);
                player.displayClientMessage(patternText, true);

                level.playSound(
                        null,
                        pos,
                        net.minecraft.sounds.SoundEvents.UI_BUTTON_CLICK,
                        net.minecraft.sounds.SoundSource.BLOCKS,
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
                        net.minecraft.world.entity.item.ItemEntity itemEntity =
                                new net.minecraft.world.entity.item.ItemEntity(
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
                                net.minecraft.sounds.SoundEvents.ITEM_FRAME_REMOVE_ITEM,
                                net.minecraft.sounds.SoundSource.BLOCKS,
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
                net.minecraft.world.entity.item.ItemEntity itemEntity =
                        new net.minecraft.world.entity.item.ItemEntity(
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
                        net.minecraft.sounds.SoundEvents.ITEM_FRAME_REMOVE_ITEM,
                        net.minecraft.sounds.SoundSource.BLOCKS,
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

            if (blockEntity.getParticlePattern() == null) {
                com.kingodogo.buildscape.config.PillarParticleConfig cfg =
                        com.kingodogo.buildscape.config.PillarParticleConfig.get();
                if (cfg.use_pattern && cfg.pattern != null) {
                    // Intentionally removed: blockEntity.setParticlePattern(cfg.pattern);
                    // This allows untouched pillars to naturally inherit future global pattern changes.
                }
            }

            if (!player.getAbilities().instabuild) {
                heldItem.shrink(1);
            }

            enforceSingleItemPerStack(level, pos);

            level.playSound(
                    null,
                    pos,
                    net.minecraft.sounds.SoundEvents.ITEM_FRAME_ADD_ITEM,
                    net.minecraft.sounds.SoundSource.BLOCKS,
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
        BlockPos top = findTopBlock(level, anyPosInStack);
        BlockPos bottom = findBottomBlock(level, anyPosInStack);

        boolean topHasItem = false;
        ItemStack firstItem = ItemStack.EMPTY;
        BlockPos current = bottom;

        // Only iterate through regular PillarBlocks, NOT AshenKingPillars
        while (level.getBlockState(current).getBlock() instanceof PillarBlock &&
               !(level.getBlockState(current).getBlock() instanceof AshenKingPillarBlock)) {
            BlockEntity be = level.getBlockEntity(current);
            if (
                    be instanceof PillarBlockEntity pillarBE && pillarBE.hasDisplayItem()
            ) {
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
                level.sendBlockUpdated(
                        top,
                        level.getBlockState(top),
                        level.getBlockState(top),
                        3
                );
            }
        }

        current = bottom;
        // Only iterate through regular PillarBlocks, NOT AshenKingPillars
        while (level.getBlockState(current).getBlock() instanceof PillarBlock &&
               !(level.getBlockState(current).getBlock() instanceof AshenKingPillarBlock)) {
            if (!current.equals(top)) {
                BlockEntity be = level.getBlockEntity(current);
                if (
                        be instanceof PillarBlockEntity pillarBE && pillarBE.hasDisplayItem()
                ) {
                    pillarBE.setDisplayedItem(ItemStack.EMPTY);
                    level.sendBlockUpdated(
                            current,
                            level.getBlockState(current),
                            level.getBlockState(current),
                            3
                    );
                }
            }
            current = current.above();
        }
    }

    private BlockPos findBottomBlock(Level level, BlockPos pos) {
        BlockPos current = pos;
        // Stop at AshenKingPillars - they're not part of regular pillar stacks
        while (level.getBlockState(current.below()).getBlock() instanceof PillarBlock &&
               !(level.getBlockState(current.below()).getBlock() instanceof AshenKingPillarBlock)) {
            current = current.below();
        }
        return current;
    }

    private BlockPos findTopBlock(Level level, BlockPos pos) {
        BlockPos current = pos;
        // Stop at AshenKingPillars - they're not part of regular pillar stacks
        while (level.getBlockState(current.above()).getBlock() instanceof PillarBlock &&
               !(level.getBlockState(current.above()).getBlock() instanceof AshenKingPillarBlock)) {
            current = current.above();
        }
        return current;
    }

    private java.util.Map.Entry<String, String> getDyeColorAndName(
            ItemStack stack
    ) {
        if (stack == null || stack.isEmpty()) {
            return null;
        }

        net.minecraft.world.item.Item item = stack.getItem();

        if (item == net.minecraft.world.item.Items.WHITE_DYE) {
            return new java.util.AbstractMap.SimpleEntry<>("#FFFFFF", "White");
        } else if (item == net.minecraft.world.item.Items.ORANGE_DYE) {
            return new java.util.AbstractMap.SimpleEntry<>("#FF9800", "Orange");
        } else if (item == net.minecraft.world.item.Items.MAGENTA_DYE) {
            return new java.util.AbstractMap.SimpleEntry<>("#C74EBD", "Magenta");
        } else if (item == net.minecraft.world.item.Items.LIGHT_BLUE_DYE) {
            return new java.util.AbstractMap.SimpleEntry<>("#3AB3DA", "Light Blue");
        } else if (item == net.minecraft.world.item.Items.YELLOW_DYE) {
            return new java.util.AbstractMap.SimpleEntry<>("#FED83D", "Yellow");
        } else if (item == net.minecraft.world.item.Items.LIME_DYE) {
            return new java.util.AbstractMap.SimpleEntry<>("#80C71F", "Lime");
        } else if (item == net.minecraft.world.item.Items.PINK_DYE) {
            return new java.util.AbstractMap.SimpleEntry<>("#F38BAA", "Pink");
        } else if (item == net.minecraft.world.item.Items.GRAY_DYE) {
            return new java.util.AbstractMap.SimpleEntry<>("#474F52", "Gray");
        } else if (item == net.minecraft.world.item.Items.LIGHT_GRAY_DYE) {
            return new java.util.AbstractMap.SimpleEntry<>("#9D9D97", "Light Gray");
        } else if (item == net.minecraft.world.item.Items.CYAN_DYE) {
            return new java.util.AbstractMap.SimpleEntry<>("#169C9C", "Cyan");
        } else if (item == net.minecraft.world.item.Items.PURPLE_DYE) {
            return new java.util.AbstractMap.SimpleEntry<>("#8932B8", "Purple");
        } else if (item == net.minecraft.world.item.Items.BLUE_DYE) {
            return new java.util.AbstractMap.SimpleEntry<>("#3C44AA", "Blue");
        } else if (item == net.minecraft.world.item.Items.BROWN_DYE) {
            return new java.util.AbstractMap.SimpleEntry<>("#835432", "Brown");
        } else if (item == net.minecraft.world.item.Items.GREEN_DYE) {
            return new java.util.AbstractMap.SimpleEntry<>("#5E7C16", "Green");
        } else if (item == net.minecraft.world.item.Items.RED_DYE) {
            return new java.util.AbstractMap.SimpleEntry<>("#B02E26", "Red");
        } else if (item == net.minecraft.world.item.Items.BLACK_DYE) {
            return new java.util.AbstractMap.SimpleEntry<>("#1D1D21", "Black");
        }

        return null;
    }

    private String getDyeColor(ItemStack stack) {
        java.util.Map.Entry<String, String> result = getDyeColorAndName(stack);
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

    private net.minecraft.ChatFormatting getPatternColor(String pattern) {
        if (pattern == null) {
            return net.minecraft.ChatFormatting.WHITE;
        }

        switch (pattern) {
            case "default":
                return net.minecraft.ChatFormatting.WHITE;
            case "beam":
                return net.minecraft.ChatFormatting.AQUA;
            case "spiral":
                return net.minecraft.ChatFormatting.LIGHT_PURPLE;
            case "fountain":
                return net.minecraft.ChatFormatting.BLUE;
            case "pulse":
                return net.minecraft.ChatFormatting.RED;
            case "ring":
                return net.minecraft.ChatFormatting.GOLD;
            case "burst":
                return net.minecraft.ChatFormatting.YELLOW;
            default:
                return net.minecraft.ChatFormatting.GRAY;
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
