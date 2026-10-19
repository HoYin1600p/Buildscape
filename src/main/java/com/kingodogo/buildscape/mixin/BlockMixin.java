package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.block.PlanterHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Block.class)
public class BlockMixin {

    @ModifyVariable(method = "registerDefaultState", at = @At("HEAD"), argsOnly = true)
    private BlockState buildscape$modifyDefaultState(BlockState state) {
        if ((Object) this instanceof LeavesBlock) {
            if (state.hasProperty(BlockStateProperties.WATERLOGGED)) {
                return state.setValue(BlockStateProperties.WATERLOGGED, false);
            }
        }
        if (state.hasProperty(PlanterHelper.PLANTER)) {
            return state.setValue(PlanterHelper.PLANTER, PlanterHelper.PlanterType.NONE);
        }
        return state;
    }
}
