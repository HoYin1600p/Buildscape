package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.block.OrnamentBlock;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FenceBlock.class)
public class FenceBlockMixin {

    private static final TagKey<Block> FENCES_TAG = TagKey.create(
            Registry.BLOCK_REGISTRY,
            new ResourceLocation("minecraft:fences")
    );

    @Inject(method = "connectsTo", at = @At("HEAD"), cancellable = true)
    private void connectsTo(
            BlockState state,
            boolean sideSolidFullSquare,
            Direction direction,
            CallbackInfoReturnable<Boolean> cir
    ) {
        Block block = state.getBlock();
        if (block instanceof FenceBlock) {
            cir.setReturnValue(true);
            return;
        }
        if (block instanceof FenceGateBlock) {
            cir.setReturnValue(true);
            return;
        }
        if (block instanceof OrnamentBlock) {
            cir.setReturnValue(true);
            return;
        }
        if (state.is(FENCES_TAG)) {
            cir.setReturnValue(true);
            return;
        }
    }
}
