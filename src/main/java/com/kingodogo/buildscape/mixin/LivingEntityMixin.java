package com.kingodogo.buildscape.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(method = "onClimbable", at = @At("RETURN"), cancellable = true)
    private void onOnClimbable(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;

        if (self instanceof Player) {
            return;
        }

        if (cir.getReturnValue()) {
            BlockPos pos = self.blockPosition();
            BlockState state = self.level.getBlockState(pos);
            Block block = state.getBlock();

            if (block instanceof ChainBlock || isBuildscapeChain(block)) {
                cir.setReturnValue(false);
            }
        }
    }

    private static boolean isBuildscapeChain(Block block) {
        ResourceLocation key = ForgeRegistries.BLOCKS.getKey(block);
        return key != null && key.getNamespace().equals("buildscape") && key.getPath().contains("chain");
    }
}
