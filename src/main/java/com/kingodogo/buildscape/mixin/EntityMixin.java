package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.block.ClimbableChainBlock;
import com.kingodogo.buildscape.block.LargeChainBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow
    public net.minecraft.world.level.Level level;
    @Shadow
    public boolean onGround;

    @Shadow
    public abstract BlockPos blockPosition();

    @Inject(method = "move", at = @At("TAIL"))
    private void onMove(
            net.minecraft.world.entity.MoverType type,
            Vec3 movement,
            CallbackInfo ci
    ) {
        Entity self = (Entity) (Object) this;

        if (self instanceof Player) {
            return;
        }

        BlockPos pos = blockPosition();
        if (level != null && isChainBlock(level.getBlockState(pos))) {
            this.onGround = false;
        }
    }

    private static boolean isChainBlock(BlockState state) {
        return (
                state.getBlock() instanceof ChainBlock ||
                        state.getBlock() instanceof ClimbableChainBlock ||
                        state.getBlock() instanceof LargeChainBlock
        );
    }
}
