package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AshenKingPillarBlock extends PillarBlock {

    public static final DirectionProperty FACING =
            BlockStateProperties.HORIZONTAL_FACING;

    // Cache shapes: [Facing Index (0:N, 1:E, 2:S, 3:W)][Part Index (0:SINGLE, 1:BOTTOM, 2:MIDDLE, 3:TOP)]
    private static final VoxelShape[][] SHAPES = new VoxelShape[4][4];

    static {
        for (Direction facing : Direction.Plane.HORIZONTAL) {
            int fIdx = facing.get2DDataValue();
            for (PillarPart part : PillarPart.values()) {
                int pIdx = part.ordinal();
                SHAPES[fIdx][pIdx] = createShape(facing, part);
            }
        }
    }

    private static VoxelShape createShape(Direction facing, PillarPart part) {
        VoxelShape result = Shapes.empty();
        
        // 1. Main Central Log (always present)
        result = Shapes.or(result, rotateBox(0, 0, 3.5, 16, 5, 12.5, facing));
        
        // 2. Red Cushion / Center Decoration (always present)
        result = Shapes.or(result, rotateBox(5.5, 5, 6, 10.5, 7.5, 10, facing));
        
        // 3. Handle base - only on SINGLE or BOTTOM (the "start" side)
        if (part == PillarPart.SINGLE || part == PillarPart.BOTTOM) {
            result = Shapes.or(result, rotateBox(-3, 1.5, 4, 0, 5, 12, facing));
        }

        // 4. Anchor base - only on SINGLE or TOP (the "end" side)
        if (part == PillarPart.SINGLE || part == PillarPart.TOP) {
            result = Shapes.or(result, rotateBox(16, 0.5, 4, 20, 4, 12, facing));
        }
        
        return result.optimize();
    }

    private static VoxelShape rotateBox(double x1, double y1, double z1, double x2, double y2, double z2, Direction facing) {
        switch (facing) {
            case EAST:  return Block.box(16 - z2, y1, x1, 16 - z1, y2, x2);
            case SOUTH: return Block.box(16 - x2, y1, 16 - z2, 16 - x1, y2, 16 - z1);
            case WEST:  return Block.box(z1, y1, 16 - x2, z2, y2, 16 - x1);
            default:    return Block.box(x1, y1, z1, x2, y2, z2); // NORTH
        }
    }

    public AshenKingPillarBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(PART, PillarPart.SINGLE)
                        .setValue(WATERLOGGED, false)
                        .setValue(FACING, Direction.NORTH)
        );
    }


    @Override
    protected void createBlockStateDefinition(
            StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PART, WATERLOGGED, FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction facing = context.getHorizontalDirection().getOpposite();
        FluidState fluidstate = level.getFluidState(pos);

        BlockState state = this.defaultBlockState().setValue(FACING, facing).setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);

        // Don't interact with neighbors for item transfer - AshenKingPillars are independent
        // Only return SINGLE state, never connect to stacks
        return state.setValue(PART, PillarPart.SINGLE);
    }

    private BlockState updatePart(BlockState state, LevelAccessor level, BlockPos pos, Direction facing) {
        // Ashen King Pillars always render as SINGLE - no connection to neighbors
        return state.setValue(PART, PillarPart.SINGLE);
    }

    @Override
    public BlockState updateShape(
            BlockState state,
            Direction direction,
            BlockState neighborState,
            LevelAccessor level,
            BlockPos pos,
            BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        
        Direction facing = state.getValue(FACING);
        if (direction == facing.getClockWise() || direction == facing.getCounterClockWise()) {
            return updatePart(state, level, pos, facing);
        }
        
        return state;
    }

    @Override
    public VoxelShape getShape(
            BlockState state, BlockGetter level, BlockPos pos,
            CollisionContext context) {
        int fIdx = state.getValue(FACING).get2DDataValue();
        int pIdx = state.getValue(PART).ordinal();
        return SHAPES[fIdx][pIdx];
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    /**
     * Override to prevent item transfer behavior (Ashen King Pillars don't connect/stack)
     * Each pillar is independent - no item stacking enforcement
     */
    @Override
    public void onPlace(BlockState state, net.minecraft.world.level.Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        // AshenKingPillar overrides onPlace to skip the enforceSingleItemPerStack() call from parent
        // Each pillar keeps its own item when stacked
        if (!level.isClientSide) {
            // Don't call parent's onPlace which enforces single item transfer
            // Just notify neighbors of the block state change
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

    /**
     * Override to prevent any connection or item transfer behavior
     * Ashen King Pillars always render as SINGLE and do NOT transfer items
     */
    @Override
    public void neighborChanged(BlockState state, net.minecraft.world.level.Level level, BlockPos pos, net.minecraft.world.level.block.Block block, BlockPos neighborPos, boolean isMoving) {
        // Ashen King Pillars never connect to neighbors, always remain SINGLE
        // Don't call parent's neighborChanged which enforces item transfer
    }

    /**
     * Override to prevent item interaction with stacked pillars
     * Each AshenKingPillar is independent - no cross-pillar item logic
     */
    @Override
    public net.minecraft.world.InteractionResult use(
            BlockState state,
            net.minecraft.world.level.Level level,
            BlockPos pos,
            net.minecraft.world.entity.player.Player player,
            net.minecraft.world.InteractionHand hand,
            net.minecraft.world.phys.BlockHitResult hit) {

        if (level.isClientSide) {
            return net.minecraft.world.InteractionResult.SUCCESS;
        }

        net.minecraft.world.level.block.entity.BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof PillarBlockEntity pillarBE)) {
            return net.minecraft.world.InteractionResult.PASS;
        }

        net.minecraft.world.item.ItemStack heldItem = player.getItemInHand(hand);

        // Check for dye items
        if (!heldItem.isEmpty()) {
            java.util.Map.Entry<String, String> dyeInfo = getDyeColorAndName(heldItem);
            if (dyeInfo != null) {
                if (pillarBE.hasDisplayItem()) {
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
                                    new com.kingodogo.buildscape.network.ActionBarMessagePacket(message)
                            );
                        }
                        return net.minecraft.world.InteractionResult.CONSUME;
                    }

                    boolean added = pillarBE.addParticleColor(dyeColor);
                    if (!added) {
                        return net.minecraft.world.InteractionResult.PASS;
                    }

                    if (!player.getAbilities().instabuild) {
                        heldItem.shrink(1);
                    }

                    level.playSound(null, pos, net.minecraft.sounds.SoundEvents.DYE_USE,
                            net.minecraft.sounds.SoundSource.BLOCKS, 1.0f, 1.0f);

                    String pillarId = pillarBE.getPillarId();
                    int colorCount = pillarBE.getDyeColorCount();
                    String progressText = " (" + colorCount + "/" + PillarBlockEntity.MAX_DYE_COLORS + ")";
                    net.minecraft.network.chat.TextComponent message;
                    if (pillarId != null) {
                        message = new net.minecraft.network.chat.TextComponent(
                                "[" + pillarId + "] Dyed " + dyeName + progressText);
                    } else {
                        message = new net.minecraft.network.chat.TextComponent(
                                "Pillar Dyed " + dyeName + progressText);
                    }

                    if (player instanceof net.minecraft.server.level.ServerPlayer) {
                        com.kingodogo.buildscape.network.ModMessages.INSTANCE.send(
                                net.minecraftforge.network.PacketDistributor.PLAYER.with(() ->
                                        (net.minecraft.server.level.ServerPlayer) player
                                ),
                                new com.kingodogo.buildscape.network.ActionBarMessagePacket(message)
                        );
                    }

                    return net.minecraft.world.InteractionResult.SUCCESS;
                }
            }
        }

        // Handle shift-click for particle pattern cycling
        if (player.isShiftKeyDown()) {
            net.minecraft.world.item.ItemStack displayedItem = pillarBE.getDisplayedItem();
            boolean isSpawnEgg = !displayedItem.isEmpty() &&
                    displayedItem.getItem() instanceof net.minecraft.world.item.SpawnEggItem;

            if (isSpawnEgg) {
                pillarBE.rotateFacing();
                float facingYaw = pillarBE.getFacingYaw();
                String direction = getDirectionName(facingYaw);
                net.minecraft.network.chat.TextComponent message =
                        new net.minecraft.network.chat.TextComponent("Mob facing: " + direction);
                message.withStyle(net.minecraft.ChatFormatting.GREEN);
                player.displayClientMessage(message, true);

                level.playSound(null, pos, net.minecraft.sounds.SoundEvents.UI_BUTTON_CLICK,
                        net.minecraft.sounds.SoundSource.BLOCKS, 0.5f, 1.0f);
                return net.minecraft.world.InteractionResult.SUCCESS;
            } else {
                pillarBE.cycleParticlePattern();
                String pattern = pillarBE.getParticlePattern();
                if (pattern == null) {
                    pattern = com.kingodogo.buildscape.config.PillarParticleConfig.get().pattern;
                }

                net.minecraft.ChatFormatting color = getPatternColor(pattern);
                net.minecraft.network.chat.TextComponent patternText =
                        (net.minecraft.network.chat.TextComponent) new net.minecraft.network.chat.TextComponent(pattern)
                                .withStyle(color);
                player.displayClientMessage(patternText, true);

                level.playSound(null, pos, net.minecraft.sounds.SoundEvents.UI_BUTTON_CLICK,
                        net.minecraft.sounds.SoundSource.BLOCKS, 0.5f, 1.0f);
                return net.minecraft.world.InteractionResult.SUCCESS;
            }
        }

        // Handle item removal - ONLY from current pillar, not from stack
        if (heldItem.isEmpty() && pillarBE.hasDisplayItem()) {
            net.minecraft.world.item.ItemStack displayedItem = pillarBE.getDisplayedItem();
            if (!displayedItem.isEmpty()) {
                pillarBE.setDisplayedItem(net.minecraft.world.item.ItemStack.EMPTY);
                level.sendBlockUpdated(pos, level.getBlockState(pos), level.getBlockState(pos), 3);

                net.minecraft.world.entity.item.ItemEntity itemEntity =
                        new net.minecraft.world.entity.item.ItemEntity(
                                level, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                                displayedItem.copy());
                itemEntity.setDefaultPickUpDelay();
                level.addFreshEntity(itemEntity);

                level.playSound(null, pos, net.minecraft.sounds.SoundEvents.ITEM_FRAME_REMOVE_ITEM,
                        net.minecraft.sounds.SoundSource.BLOCKS, 1.0f, 1.0f);
                return net.minecraft.world.InteractionResult.SUCCESS;
            }
        }

        // Handle item placement - ONLY on current pillar
        if (!heldItem.isEmpty()) {
            net.minecraft.world.item.ItemStack displayItem = heldItem.copy();
            displayItem.setCount(1);

            float playerYaw = player.getYRot();
            float facingYaw = (playerYaw + 180.0f) % 360.0f;
            if (facingYaw < 0) {
                facingYaw += 360.0f;
            }
            pillarBE.setDisplayedItem(displayItem, facingYaw);

            if (pillarBE.getParticlePattern() == null) {
                com.kingodogo.buildscape.config.PillarParticleConfig cfg =
                        com.kingodogo.buildscape.config.PillarParticleConfig.get();
                if (cfg.use_pattern && cfg.pattern != null) {
                    pillarBE.setParticlePattern(cfg.pattern);
                }
            }

            if (!player.getAbilities().instabuild) {
                heldItem.shrink(1);
            }

            level.playSound(null, pos, net.minecraft.sounds.SoundEvents.ITEM_FRAME_ADD_ITEM,
                    net.minecraft.sounds.SoundSource.BLOCKS, 1.0f, 1.0f);
            return net.minecraft.world.InteractionResult.SUCCESS;
        }

        return net.minecraft.world.InteractionResult.PASS;
    }

    /**
     * Get dye color and name from ItemStack
     */
    private java.util.Map.Entry<String, String> getDyeColorAndName(net.minecraft.world.item.ItemStack stack) {
        if (stack == null || stack.isEmpty()) return null;

        net.minecraft.world.item.Item item = stack.getItem();
        if (item == net.minecraft.world.item.Items.WHITE_DYE)
            return new java.util.AbstractMap.SimpleEntry<>("#FFFFFF", "White");
        else if (item == net.minecraft.world.item.Items.ORANGE_DYE)
            return new java.util.AbstractMap.SimpleEntry<>("#FF9800", "Orange");
        else if (item == net.minecraft.world.item.Items.MAGENTA_DYE)
            return new java.util.AbstractMap.SimpleEntry<>("#C74EBD", "Magenta");
        else if (item == net.minecraft.world.item.Items.LIGHT_BLUE_DYE)
            return new java.util.AbstractMap.SimpleEntry<>("#3AB3DA", "Light Blue");
        else if (item == net.minecraft.world.item.Items.YELLOW_DYE)
            return new java.util.AbstractMap.SimpleEntry<>("#FED83D", "Yellow");
        else if (item == net.minecraft.world.item.Items.LIME_DYE)
            return new java.util.AbstractMap.SimpleEntry<>("#80C71F", "Lime");
        else if (item == net.minecraft.world.item.Items.PINK_DYE)
            return new java.util.AbstractMap.SimpleEntry<>("#F38BAA", "Pink");
        else if (item == net.minecraft.world.item.Items.GRAY_DYE)
            return new java.util.AbstractMap.SimpleEntry<>("#474F52", "Gray");
        else if (item == net.minecraft.world.item.Items.LIGHT_GRAY_DYE)
            return new java.util.AbstractMap.SimpleEntry<>("#9D9D97", "Light Gray");
        else if (item == net.minecraft.world.item.Items.CYAN_DYE)
            return new java.util.AbstractMap.SimpleEntry<>("#169C9C", "Cyan");
        else if (item == net.minecraft.world.item.Items.PURPLE_DYE)
            return new java.util.AbstractMap.SimpleEntry<>("#8932B8", "Purple");
        else if (item == net.minecraft.world.item.Items.BLUE_DYE)
            return new java.util.AbstractMap.SimpleEntry<>("#3C44AA", "Blue");
        else if (item == net.minecraft.world.item.Items.BROWN_DYE)
            return new java.util.AbstractMap.SimpleEntry<>("#835432", "Brown");
        else if (item == net.minecraft.world.item.Items.GREEN_DYE)
            return new java.util.AbstractMap.SimpleEntry<>("#5E7C16", "Green");
        else if (item == net.minecraft.world.item.Items.RED_DYE)
            return new java.util.AbstractMap.SimpleEntry<>("#B02E26", "Red");
        else if (item == net.minecraft.world.item.Items.BLACK_DYE)
            return new java.util.AbstractMap.SimpleEntry<>("#1D1D21", "Black");

        return null;
    }

    /**
     * Get direction name from yaw
     */
    private String getDirectionName(float yaw) {
        yaw = yaw % 360.0f;
        if (yaw < 0) yaw += 360.0f;

        if (yaw >= 315 || yaw < 45) return "South";
        else if (yaw >= 45 && yaw < 135) return "West";
        else if (yaw >= 135 && yaw < 225) return "North";
        else return "East";
    }

    /**
     * Get pattern color
     */
    private net.minecraft.ChatFormatting getPatternColor(String pattern) {
        if (pattern == null) return net.minecraft.ChatFormatting.WHITE;

        switch (pattern) {
            case "default": return net.minecraft.ChatFormatting.WHITE;
            case "beam": return net.minecraft.ChatFormatting.AQUA;
            case "spiral": return net.minecraft.ChatFormatting.LIGHT_PURPLE;
            case "fountain": return net.minecraft.ChatFormatting.BLUE;
            case "pulse": return net.minecraft.ChatFormatting.RED;
            case "ring": return net.minecraft.ChatFormatting.GOLD;
            case "burst": return net.minecraft.ChatFormatting.YELLOW;
            default: return net.minecraft.ChatFormatting.GRAY;
        }
    }
}
