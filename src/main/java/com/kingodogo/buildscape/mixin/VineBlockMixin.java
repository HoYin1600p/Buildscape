package com.kingodogo.buildscape.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(VineBlock.class)
public abstract class VineBlockMixin extends Block {

    @Unique
    private static final BooleanProperty BUILDSCAPE_SHEARED = BooleanProperty.create("sheared");

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
        if (state.getValue(BUILDSCAPE_SHEARED)) {
            ci.cancel();
        }
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return !state.getValue(BUILDSCAPE_SHEARED);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        if (state.getValue(BUILDSCAPE_SHEARED)) {
            return InteractionResult.PASS;
        }

        ItemStack heldItem = player.getItemInHand(hand);
        if (heldItem.is(Items.SHEARS)) {
            level.setBlockAndUpdate(pos, state.setValue(BUILDSCAPE_SHEARED, true));
            level.playSound(null, pos, SoundEvents.GROWING_PLANT_CROP, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.gameEvent(player, GameEvent.SHEAR, pos);
            heldItem.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return InteractionResult.PASS;
    }
}
