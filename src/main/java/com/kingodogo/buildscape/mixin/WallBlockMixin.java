package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.block.OrnamentBlock;
import net.minecraft.tags.BlockTags;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WallBlock.class)
public class WallBlockMixin {

    private static final TagKey<Block> WALLS_TAG = BlockTags.WALLS;

    @Inject(method = "connectsTo", at = @At("HEAD"), cancellable = true)
    private void connectsTo(
            BlockState state,
            boolean sideSolidFullSquare,
            Direction direction,
            CallbackInfoReturnable<Boolean> cir) {
        Block block = state.getBlock();
        if (block instanceof WallBlock) {
            cir.setReturnValue(true);
            return;
        }
        if (block instanceof OrnamentBlock) {
            cir.setReturnValue(true);
            return;
        }
        if (state.is(WALLS_TAG)) {
            cir.setReturnValue(true);
        }
    }
}
