package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.mixinsupport.ShulkerGhostFilterCapture;
import com.kingodogo.buildscape.util.GhostFilterable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ShulkerBoxBlock.class)
public abstract class ShulkerBoxBlockV121xMixin {
    @Dynamic
    @Inject(method = "getDrops", at = @At("RETURN"), require = 0)
    private void buildscape$preserveGhostFilters(BlockState state, LootParams.Builder builder,
            CallbackInfoReturnable<List<ItemStack>> cir) {
        BlockEntity entity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (entity instanceof GhostFilterable filterable) {
            ShulkerGhostFilterCapture.capture(filterable);
            ShulkerGhostFilterCapture.preserveInDrops(cir.getReturnValue());
        }
    }
}
