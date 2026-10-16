package com.kingodogo.buildscape.mixin.fabric;

import com.kingodogo.buildscape.block.SnowyLargeFernBlock;
import com.kingodogo.buildscape.block.SnowyTallGrassBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DoublePlantBlock.class)
public abstract class DoublePlantPlacementMixin extends BushBlock {
    protected DoublePlantPlacementMixin(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Inject(method = "getStateForPlacement", at = @At("HEAD"), cancellable = true)
    private void buildscape$replaceableUpperHalf(BlockPlaceContext context, CallbackInfoReturnable<BlockState> cir) {
        Object self = this;
        if (!(self instanceof SnowyTallGrassBlock) && !(self instanceof SnowyLargeFernBlock)) return;
        BlockPos pos = context.getClickedPos();
        // Preserve the reference placement rule when the upper space contains replaceable vegetation.
        cir.setReturnValue(pos.getY() < context.getLevel().getMaxBuildHeight() - 1
                && context.getLevel().getBlockState(pos.above()).canBeReplaced(context)
                ? super.getStateForPlacement(context) : null);
    }
}
