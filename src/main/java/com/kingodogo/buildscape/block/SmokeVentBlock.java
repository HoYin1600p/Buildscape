package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.particle.ModParticles;
import com.kingodogo.buildscape.particle.SmokeColorRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;
import java.util.Random;

public class SmokeVentBlock extends Block implements EntityBlock {

    public static final EnumProperty<PillarPart> PART = EnumProperty.create("part", PillarPart.class);

    private static final VoxelShape SHAPE_SINGLE = Shapes.or(
            Block.box(4, 0, 4, 12, 7, 12),
            Block.box(3, 7, 3, 13, 9, 13)
    );
    private static final VoxelShape SHAPE_TOP = Shapes.or(
            Block.box(4, 0, 4, 12, 7, 12),
            Block.box(3, 7, 3, 13, 9, 13)
    );
    private static final VoxelShape SHAPE_MIDDLE = Block.box(4, 0, 4, 12, 16, 12);
    private static final VoxelShape SHAPE_BOTTOM = Block.box(4, 0, 4, 12, 16, 12);

    public SmokeVentBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(PART, PillarPart.SINGLE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PART);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SmokeVentBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        boolean hasAbove = level.getBlockState(pos.above()).getBlock() instanceof SmokeVentBlock;
        boolean hasBelow = level.getBlockState(pos.below()).getBlock() instanceof SmokeVentBlock;

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

        return this.defaultBlockState().setValue(PART, part);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                  LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (direction == Direction.UP || direction == Direction.DOWN) {
            boolean hasAbove = level.getBlockState(pos.above()).getBlock() instanceof SmokeVentBlock;
            boolean hasBelow = level.getBlockState(pos.below()).getBlock() instanceof SmokeVentBlock;

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
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        switch (state.getValue(PART)) {
            case TOP:
                return SHAPE_TOP;
            case MIDDLE:
                return SHAPE_MIDDLE;
            case BOTTOM:
                return SHAPE_BOTTOM;
            default:
                return SHAPE_SINGLE;
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        ItemStack heldItem = player.getItemInHand(hand);

        // Dye interaction - changes smoke color for the whole stack
        if (!heldItem.isEmpty()) {
            Map.Entry<String, String> dyeInfo = getDyeColorAndName(heldItem);
            if (dyeInfo != null) {
                String dyeColor = dyeInfo.getKey();
                String dyeName = dyeInfo.getValue();

                applyColorToStack(level, pos, dyeColor);

                if (!player.getAbilities().instabuild) {
                    heldItem.shrink(1);
                }

                level.playSound(null, pos, net.minecraft.sounds.SoundEvents.DYE_USE,
                        net.minecraft.sounds.SoundSource.BLOCKS, 1.0f, 1.0f);

                net.minecraft.network.chat.TextComponent message =
                        new net.minecraft.network.chat.TextComponent("Smoke color: " + dyeName);
                message.withStyle(net.minecraft.ChatFormatting.GRAY);
                if (player instanceof net.minecraft.server.level.ServerPlayer) {
                    com.kingodogo.buildscape.network.ModMessages.INSTANCE.send(
                            net.minecraftforge.network.PacketDistributor.PLAYER.with(() ->
                                    (net.minecraft.server.level.ServerPlayer) player),
                            new com.kingodogo.buildscape.network.ActionBarMessagePacket(message));
                }

                return InteractionResult.SUCCESS;
            }
        }

        // Empty hand right-click to toggle smoke on/off
        if (heldItem.isEmpty()) {
            boolean newActive = !getStackActive(level, pos);
            applyActiveToStack(level, pos, newActive);

            level.playSound(null, pos, net.minecraft.sounds.SoundEvents.UI_BUTTON_CLICK,
                    net.minecraft.sounds.SoundSource.BLOCKS, 0.5f, newActive ? 1.2f : 0.8f);

            net.minecraft.network.chat.TextComponent message =
                    new net.minecraft.network.chat.TextComponent(newActive ? "Smoke Vent: On" : "Smoke Vent: Off");
            message.withStyle(newActive ? net.minecraft.ChatFormatting.GREEN : net.minecraft.ChatFormatting.RED);
            if (player instanceof net.minecraft.server.level.ServerPlayer) {
                com.kingodogo.buildscape.network.ModMessages.INSTANCE.send(
                        net.minecraftforge.network.PacketDistributor.PLAYER.with(() ->
                                (net.minecraft.server.level.ServerPlayer) player),
                        new com.kingodogo.buildscape.network.ActionBarMessagePacket(message));
            }

            return InteractionResult.SUCCESS;
        }

        // Shift + click with water bucket to clear color
        if (player.isShiftKeyDown() && !heldItem.isEmpty() &&
                heldItem.getItem() == Items.WATER_BUCKET) {
            applyColorToStack(level, pos, null);

            level.playSound(null, pos, net.minecraft.sounds.SoundEvents.BUCKET_EMPTY,
                    net.minecraft.sounds.SoundSource.BLOCKS, 1.0f, 1.0f);

            net.minecraft.network.chat.TextComponent message =
                    new net.minecraft.network.chat.TextComponent("Smoke color cleared");
            message.withStyle(net.minecraft.ChatFormatting.GRAY);
            if (player instanceof net.minecraft.server.level.ServerPlayer) {
                com.kingodogo.buildscape.network.ModMessages.INSTANCE.send(
                        net.minecraftforge.network.PacketDistributor.PLAYER.with(() ->
                                (net.minecraft.server.level.ServerPlayer) player),
                        new com.kingodogo.buildscape.network.ActionBarMessagePacket(message));
            }

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, Random random) {
        PillarPart part = state.getValue(PART);
        if (part != PillarPart.TOP && part != PillarPart.SINGLE) {
            return;
        }

        BlockEntity activeBe = level.getBlockEntity(pos);
        if (activeBe instanceof SmokeVentBlockEntity ventBE && !ventBE.isActive()) {
            return;
        }

        if (random.nextFloat() < 0.1F) {
            return;
        }

        double x = (double) pos.getX() + 0.5 + random.nextDouble() / 3.0 * (double) (random.nextBoolean() ? 1 : -1);
        double y = (double) pos.getY() + random.nextDouble() + random.nextDouble();
        double z = (double) pos.getZ() + 0.5 + random.nextDouble() / 3.0 * (double) (random.nextBoolean() ? 1 : -1);

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof SmokeVentBlockEntity ventBE && ventBE.getSmokeColor() != null) {
            // Colored smoke - queue color then spawn custom particle
            SmokeColorRegistry.registerColorForPosition(x, y, z, ventBE.getSmokeColor());
            level.addAlwaysVisibleParticle(ModParticles.COLORED_SMOKE.get(), true, x, y, z, 0.0, 0.07, 0.0);
        } else {
            // Default campfire smoke
            level.addAlwaysVisibleParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, true, x, y, z, 0.0, 0.07, 0.0);
        }
    }

    private boolean getStackActive(Level level, BlockPos pos) {
        BlockPos top = findTopBlock(level, pos);
        BlockEntity be = level.getBlockEntity(top);
        if (be instanceof SmokeVentBlockEntity ventBE) {
            return ventBE.isActive();
        }
        return true;
    }

    private void applyActiveToStack(Level level, BlockPos pos, boolean active) {
        BlockPos bottom = findBottomBlock(level, pos);
        BlockPos current = bottom;
        while (level.getBlockState(current).getBlock() instanceof SmokeVentBlock) {
            BlockEntity be = level.getBlockEntity(current);
            if (be instanceof SmokeVentBlockEntity ventBE) {
                ventBE.setActive(active);
            }
            current = current.above();
        }
    }

    private void applyColorToStack(Level level, BlockPos pos, String color) {
        BlockPos bottom = findBottomBlock(level, pos);
        BlockPos current = bottom;
        while (level.getBlockState(current).getBlock() instanceof SmokeVentBlock) {
            BlockEntity be = level.getBlockEntity(current);
            if (be instanceof SmokeVentBlockEntity ventBE) {
                ventBE.setSmokeColor(color);
            }
            current = current.above();
        }
    }

    private BlockPos findTopBlock(Level level, BlockPos pos) {
        BlockPos current = pos;
        while (level.getBlockState(current.above()).getBlock() instanceof SmokeVentBlock) {
            current = current.above();
        }
        return current;
    }

    private BlockPos findBottomBlock(Level level, BlockPos pos) {
        BlockPos current = pos;
        while (level.getBlockState(current.below()).getBlock() instanceof SmokeVentBlock) {
            current = current.below();
        }
        return current;
    }

    private Map.Entry<String, String> getDyeColorAndName(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return null;

        net.minecraft.world.item.Item item = stack.getItem();

        if (item == Items.WHITE_DYE) return Map.entry("#FFFFFF", "White");
        else if (item == Items.ORANGE_DYE) return Map.entry("#FF9800", "Orange");
        else if (item == Items.MAGENTA_DYE) return Map.entry("#C74EBD", "Magenta");
        else if (item == Items.LIGHT_BLUE_DYE) return Map.entry("#3AB3DA", "Light Blue");
        else if (item == Items.YELLOW_DYE) return Map.entry("#FED83D", "Yellow");
        else if (item == Items.LIME_DYE) return Map.entry("#80C71F", "Lime");
        else if (item == Items.PINK_DYE) return Map.entry("#F38BAA", "Pink");
        else if (item == Items.GRAY_DYE) return Map.entry("#474F52", "Gray");
        else if (item == Items.LIGHT_GRAY_DYE) return Map.entry("#9D9D97", "Light Gray");
        else if (item == Items.CYAN_DYE) return Map.entry("#169C9C", "Cyan");
        else if (item == Items.PURPLE_DYE) return Map.entry("#8932B8", "Purple");
        else if (item == Items.BLUE_DYE) return Map.entry("#3C44AA", "Blue");
        else if (item == Items.BROWN_DYE) return Map.entry("#835432", "Brown");
        else if (item == Items.GREEN_DYE) return Map.entry("#5E7C16", "Green");
        else if (item == Items.RED_DYE) return Map.entry("#B02E26", "Red");
        else if (item == Items.BLACK_DYE) return Map.entry("#1D1D21", "Black");

        return null;
    }
}
