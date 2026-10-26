package com.kingodogo.buildscape.event;

import com.kingodogo.buildscape.platform.Services;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public final class SculkExperienceLogic {
    private SculkExperienceLogic() {}

    /** -1 leaves the loader event's existing experience unchanged. */
    public static int experience(Player player, BlockState state, ItemStack tool) {
        // The Forge loader applies non-negative results to its block break event.
        if (player == null || player.getAbilities().instabuild) return -1;
        var id = Services.PLATFORM.getBlockId(state.getBlock());
        if (id == null || !"buildscape".equals(id.getNamespace())) return -1;
        return withTool(forBlock(id.getPath()), Services.PLATFORM.hasSilkTouch(tool));
    }

    /** Vanilla 1.19 experience for breaking a sculk-family block, or -1 for any other block. */
    public static int forBlock(String path) {
        if (path == null) return -1;
        return switch (path) {
            case "sculk", "sculk_slab", "sculk_stairs", "sculk_wall", "sculk_vertical_slab", "sculk_pillar" -> 1;
            case "sculk_vein" -> 0;
            case "sculk_catalyst", "sculk_shrieker", "sculk_sensor" -> 5;
            default -> -1;
        };
    }

    /** Silk Touch removes the experience of sculk blocks only; other blocks stay untouched. */
    public static int withTool(int experience, boolean silkTouch) {
        return experience > 0 && silkTouch ? 0 : experience;
    }
}
