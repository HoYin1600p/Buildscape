package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.event.LogStrippingLogic;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
public class LogStrippingHandler {

    private static final Map<Supplier<Block>, Supplier<Block>> STRIP_MAP = new HashMap<>();

    public static void init() {
    }

    public static void registerPair(Supplier<Block> unstripped, Supplier<Block> stripped) {
        if (unstripped != null && stripped != null) {
            STRIP_MAP.put(unstripped, stripped);
        }
    }

    public static InteractionResult handleAxeStrip(Player player, Level level, InteractionHand hand, BlockPos pos) {
        return LogStrippingLogic.handleAxeStrip(player, level, hand, pos);
    }

    public static BlockState copyStateProperties(BlockState from, BlockState to) {
        return LogStrippingLogic.copyStateProperties(from, to);
    }
}
