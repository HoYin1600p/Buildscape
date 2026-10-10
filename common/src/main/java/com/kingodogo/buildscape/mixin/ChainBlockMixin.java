package com.kingodogo.buildscape.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ChainBlock.class)
public abstract class ChainBlockMixin extends RotatedPillarBlock {

    protected ChainBlockMixin(Properties properties) {
        super(properties);
    }

    public boolean isLadder(
            BlockState state,
            LevelReader level,
            BlockPos pos,
            LivingEntity entity
    ) {
        if (entity == null) return false;
        return (
                entity instanceof Player &&
                        state.getValue(ChainBlock.AXIS) == Direction.Axis.Y
        );
    }
}
