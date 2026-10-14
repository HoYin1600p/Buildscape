package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluids;
public class MulticolorGlowLightsBlock extends GlowLightsBlock {

    public MulticolorGlowLightsBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state == null) {
            return null;
        }
        // Main samples the fluid one block past the destination (clicked pos shifted by the clicked face).
        BlockPos fluidPos = context.getClickedPos().relative(context.getClickedFace());
        boolean water = context.getLevel().getFluidState(fluidPos).getType() == Fluids.WATER;
        return state.setValue(LIT, true).setValue(WATERLOGGED, water);
    }

    /** Main's attachment check: a face also holds on any non-replaceable neighbour, not only on full faces. */
    private static boolean canAttachToMulticolor(BlockGetter level, BlockState state, BlockPos pos, Direction direction) {
        return canAttachTo(level, state, pos, direction) || !Services.PLATFORM.isReplaceable(state);
    }

    public static BlockState updateMulticolorShape(BlockState state, Direction direction, BlockState neighborState, BlockGetter level, BlockPos neighborPos) {
        BooleanProperty faceProperty = direction == Direction.DOWN ? DOWN : (direction == Direction.UP ? UP : getPropertyForFace(direction));
        if (faceProperty != null && state.hasProperty(faceProperty) && state.getValue(faceProperty)) {
            if (!canAttachToMulticolor(level, neighborState, neighborPos, direction.getOpposite())) {
                state = state.setValue(faceProperty, false);
            }
        }
        if (!hasFaces(state)) {
            return net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();
        }
        return state;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return null;
    }
}
