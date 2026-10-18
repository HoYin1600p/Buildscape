package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.block.copper.BuildscapeWeatheringCopper;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Random;

public class WeatheringTrapdoorBlock extends TrapDoorBlock implements BuildscapeWeatheringCopper {
    public WeatheringTrapdoorBlock(Properties properties) {
        super(properties.randomTicks());
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return CopperOxidationHandler.canOxidize(state);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
        this.onRandomTick(state, level, pos, random);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        state = state.cycle(OPEN);
        level.setBlock(pos, state, 2);
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        level.playSound(player, pos, state.getValue(OPEN) ? SoundEvents.WOODEN_TRAPDOOR_OPEN : SoundEvents.WOODEN_TRAPDOOR_CLOSE, SoundSource.BLOCKS, 1.0F, 1.0F);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
