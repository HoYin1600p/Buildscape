package com.kingodogo.buildscape.mixin.fabric;

import com.kingodogo.buildscape.item.WrenchItem;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Fabric has no per-item sneak-bypass hook. Forge and NeoForge ask each held stack
 * (ItemStack.doesSneakBypassUse) before a sneaking player skips the block's use handler; this applies the same
 * rule so a sneaking player with the wrench still reaches the hollow pipe's use handler.
 */
@Mixin(ServerPlayerGameMode.class)
public abstract class SneakBypassUseMixin {
    @ModifyVariable(method = "useItemOn", at = @At("STORE"), ordinal = 1)
    private boolean buildscape$sneakBypassUse(boolean skipBlockUse, ServerPlayer player, Level level, ItemStack stack,
                                              InteractionHand hand, BlockHitResult hit) {
        if (!skipBlockUse) {
            return false;
        }
        BlockPos pos = hit.getBlockPos();
        return !(buildscape$bypasses(player.getMainHandItem(), level, pos, player)
                && buildscape$bypasses(player.getOffhandItem(), level, pos, player));
    }

    private static boolean buildscape$bypasses(ItemStack held, Level level, BlockPos pos, ServerPlayer player) {
        return held.isEmpty()
                || held.getItem() instanceof WrenchItem wrench && wrench.doesSneakBypassUse(held, level, pos, player);
    }
}
