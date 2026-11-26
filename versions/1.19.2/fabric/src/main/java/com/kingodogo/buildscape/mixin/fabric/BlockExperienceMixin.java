package com.kingodogo.buildscape.mixin.fabric;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public abstract class BlockExperienceMixin {
    @Inject(method = "playerDestroy", at = @At("TAIL"))
    private void buildscape$experience(Level level, Player player, BlockPos pos, BlockState state,
            BlockEntity entity, ItemStack tool, CallbackInfo ci) {
        int experience = com.kingodogo.buildscape.event.SculkExperienceLogic.experience(player, state, tool);
        if (!level.isClientSide && experience > 0) net.minecraft.world.entity.ExperienceOrb.award(
                (net.minecraft.server.level.ServerLevel)level, net.minecraft.world.phys.Vec3.atCenterOf(pos), experience);
    }
}
