package com.kingodogo.buildscape.block.plant;

import com.kingodogo.buildscape.registry.items.WoodItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public class LeafLayersBlock
        extends
            SnowLayerBlock
        implements
            SimpleWaterloggedBlock {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private final String leafType;

    public LeafLayersBlock(Properties properties, String leafType) {
        super(properties);
        this.leafType = leafType;
        this.registerDefaultState(
                this.stateDefinition.any().setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(
            StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WATERLOGGED);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState blockState = level.getBlockState(pos.below());
        if (!blockState.is(Blocks.ICE) &&
                !blockState.is(Blocks.PACKED_ICE) &&
                !blockState.is(Blocks.BARRIER)) {
            if (!blockState.is(Blocks.HONEY_BLOCK) && !blockState.is(Blocks.SOUL_SAND)) {
                return (Block.isFaceFull(
                        blockState.getCollisionShape(level, pos.below()),
                        Direction.UP) ||
                        (blockState.is(this) && blockState.getValue(LAYERS) == 8));
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        int i = state.getValue(LAYERS);
        boolean isWaterlogged = state.getValue(WATERLOGGED);

        if (context.getItemInHand().is(this.asItem()) && i < 8) {
            if (context.replacingClickedOnBlock()) {
                return context.getClickedFace() == Direction.UP;
            } else {
                return true;
            }
        }

        Item heldItem = context.getItemInHand().getItem();
        if (heldItem instanceof BlockItem blockItem) {
            Block heldBlock = blockItem.getBlock();

            if (heldBlock instanceof LeafLayersBlock && heldBlock != this) {
                return false;
            }
            if (heldBlock instanceof SnowLayerBlock &&
                    !(heldBlock instanceof LeafLayersBlock)) {
                return false;
            }
            if (!(heldBlock instanceof LeafLayersBlock ||
                    heldBlock instanceof SnowLayerBlock)) {
                return false;
            }
        } else {
            return false;
        }

        return false;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidstate = context
                .getLevel()
                .getFluidState(context.getClickedPos());
        BlockState blockState = context
                .getLevel()
                .getBlockState(context.getClickedPos());
        if (blockState.is(this)) {
            int i = blockState.getValue(LAYERS);
            return blockState
                    .setValue(LAYERS, Math.min(8, i + 1))
                    .setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
        } else {
            BlockState state = super.getStateForPlacement(context);
            return state != null
                    ? state.setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER)
                    : null;
        }
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
        return super.updateShape(
                state,
                direction,
                neighborState,
                level,
                pos,
                neighborPos);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED)
                ? Fluids.WATER.getSource(false)
                : super.getFluidState(state);
    }

    @Override
    public VoxelShape getShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context) {
        return getCollisionShape(state, level, pos, context);
    }

    @Override
    public VoxelShape getCollisionShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context) {
        int i = state.getValue(LAYERS);
        if (i == 8) {
            return Shapes.block();
        } else {
            return Shapes.box(0.0, 0.0, 0.0, 1.0, (double) (i * 2) / 16.0, 1.0);
        }
    }

    @Override
    public VoxelShape getVisualShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return false;
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return 0;
    }

    @Override
    public boolean propagatesSkylightDown(
            BlockState state,
            BlockGetter reader,
            BlockPos pos) {
        return true;
    }

    @Override
    public float getShadeBrightness(
            BlockState state,
            BlockGetter level,
            BlockPos pos) {
        return 1.0F;
    }

    public boolean isPathfindable(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            PathComputationType type) {
        if (type == PathComputationType.LAND) {
            return state.getValue(LAYERS) < 5;
        }
        return false;
    }

    @Override
    public List<ItemStack> getDrops(
            BlockState state,
            LootContext.Builder builder) {
        int layerCount = state.getValue(LAYERS);
        return switch (leafType) {
            case "oak" -> List.of(
                    new ItemStack(WoodItems.OAK_LEAF_LAYERS.get(), layerCount));
            case "spruce" -> List.of(
                    new ItemStack(WoodItems.SPRUCE_LEAF_LAYERS.get(), layerCount));
            case "birch" -> List.of(
                    new ItemStack(WoodItems.BIRCH_LEAF_LAYERS.get(), layerCount));
            case "jungle" -> List.of(
                    new ItemStack(WoodItems.JUNGLE_LEAF_LAYERS.get(), layerCount));
            case "acacia" -> List.of(
                    new ItemStack(WoodItems.ACACIA_LEAF_LAYERS.get(), layerCount));
            case "dark_oak" -> List.of(
                    new ItemStack(WoodItems.DARK_OAK_LEAF_LAYERS.get(), layerCount));
            case "azalea" -> List.of(
                    new ItemStack(WoodItems.AZALEA_LEAF_LAYERS.get(), layerCount));
            case "flowering_azalea" -> List.of(
                    new ItemStack(WoodItems.FLOWERING_AZALEA_LEAF_LAYERS.get(), layerCount));
            case "snowy_oak" -> List.of(
                    new ItemStack(WoodItems.SNOWY_OAK_LEAF_LAYERS.get(), layerCount));
            case "snowy_spruce" -> List.of(
                    new ItemStack(WoodItems.SNOWY_SPRUCE_LEAF_LAYERS.get(), layerCount));
            case "snowy_birch" -> List.of(
                    new ItemStack(WoodItems.SNOWY_BIRCH_LEAF_LAYERS.get(), layerCount));
            case "snowy_jungle" -> List.of(
                    new ItemStack(WoodItems.SNOWY_JUNGLE_LEAF_LAYERS.get(), layerCount));
            case "snowy_acacia" -> List.of(
                    new ItemStack(WoodItems.SNOWY_ACACIA_LEAF_LAYERS.get(), layerCount));
            case "snowy_dark_oak" -> List.of(
                    new ItemStack(WoodItems.SNOWY_DARK_OAK_LEAF_LAYERS.get(), layerCount));
            case "snowy_mangrove" -> List.of(
                    new ItemStack(WoodItems.SNOWY_MANGROVE_LEAF_LAYERS.get(), layerCount));
            case "snowy_azalea" -> List.of(
                    new ItemStack(WoodItems.SNOWY_AZALEA_LEAF_LAYERS.get(), layerCount));
            case "snowy_flowering_azalea" -> List.of(
                    new ItemStack(
                            WoodItems.SNOWY_FLOWERING_AZALEA_LEAF_LAYERS.get(),
                            layerCount));
            case "mangrove" -> List.of(
                    new ItemStack(WoodItems.MANGROVE_LEAF_LAYERS.get(), layerCount));
            case "orange_poplar" -> List.of(
                    new ItemStack(WoodItems.ORANGE_POPLAR_LEAF_LAYERS.get(), layerCount));
            case "red_poplar" -> List.of(
                    new ItemStack(WoodItems.RED_POPLAR_LEAF_LAYERS.get(), layerCount));
            case "yellow_poplar" -> List.of(
                    new ItemStack(WoodItems.YELLOW_POPLAR_LEAF_LAYERS.get(), layerCount));
            case "cherry" -> List.of(
                    new ItemStack(WoodItems.CHERRY_LEAF_LAYERS.get(), layerCount));
            case "pale_oak" -> List.of(
                    new ItemStack(WoodItems.PALE_OAK_LEAF_LAYERS.get(), layerCount));
            case "snowy_cherry" -> List.of(
                    new ItemStack(WoodItems.SNOWY_CHERRY_LEAF_LAYERS.get(), layerCount));
            case "snowy_pale_oak" -> List.of(
                    new ItemStack(WoodItems.SNOWY_PALE_OAK_LEAF_LAYERS.get(), layerCount));
            case "snowy_orange_poplar" -> List.of(
                    new ItemStack(WoodItems.SNOWY_ORANGE_POPLAR_LEAF_LAYERS.get(), layerCount));
            case "snowy_red_poplar" -> List.of(
                    new ItemStack(WoodItems.SNOWY_RED_POPLAR_LEAF_LAYERS.get(), layerCount));
            case "snowy_yellow_poplar" -> List.of(
                    new ItemStack(WoodItems.SNOWY_YELLOW_POPLAR_LEAF_LAYERS.get(), layerCount));
            default -> List.of(
                    new ItemStack(WoodItems.OAK_LEAF_LAYERS.get(), layerCount));
        };
    }

}
