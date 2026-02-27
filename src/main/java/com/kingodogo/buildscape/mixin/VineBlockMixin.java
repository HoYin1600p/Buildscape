package com.kingodogo.buildscape.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(VineBlock.class)
public abstract class VineBlockMixin extends Block {

    @Unique
    private static final BooleanProperty BUILDSCAPE_SHEARED = com.kingodogo.buildscape.block.ModBlockProperties.SHEARED;

    protected VineBlockMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "createBlockStateDefinition", at = @At("TAIL"))
    private void buildscape$addShearedProperty(StateDefinition.Builder<Block, BlockState> builder, CallbackInfo ci) {
        builder.add(BUILDSCAPE_SHEARED);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void buildscape$setDefaultSheared(Properties properties, CallbackInfo ci) {
        this.registerDefaultState(this.defaultBlockState().setValue(BUILDSCAPE_SHEARED, false));
    }

    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    private void buildscape$preventGrowthWhenSheared(BlockState state, ServerLevel level, BlockPos pos,
                                                     Random random, CallbackInfo ci) {
        if (state.hasProperty(BUILDSCAPE_SHEARED) && state.getValue(BUILDSCAPE_SHEARED)) {
            ci.cancel();
        }
    }
}
