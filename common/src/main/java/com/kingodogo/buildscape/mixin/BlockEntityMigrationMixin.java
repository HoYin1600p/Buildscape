package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.mixinsupport.RemovedBlockEntityMigration;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockEntity.class)
public abstract class BlockEntityMigrationMixin {
    @Dynamic("26.2 saved block entity migration")
    @Inject(method = "loadStatic(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/nbt/CompoundTag;Lnet/minecraft/core/HolderLookup$Provider;)Lnet/minecraft/world/level/block/entity/BlockEntity;",
            at = @At("HEAD"), cancellable = true, require = 0)
    private static void buildscape$migrateRemovedType(BlockPos pos, BlockState state, CompoundTag tag,
            @Coerce Object lookup, CallbackInfoReturnable<BlockEntity> callback) {
        RemovedBlockEntityMigration.load(pos, state, tag, lookup, callback::setReturnValue);
    }
}
