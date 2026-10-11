package com.kingodogo.buildscape.event;

import com.kingodogo.buildscape.platform.Services;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public final class SculkExperienceLogic {
    private SculkExperienceLogic() {}

    /** -1 leaves the loader event's existing experience unchanged. */
    public static int experience(Player player, BlockState state, ItemStack tool) {
        // TODO S7a: apply non-negative results in Forge's block break event.
        if (player == null || player.getAbilities().instabuild) return -1;
        // The reference suppresses experience for any Silk Touch break handled by this event.
        if (Services.PLATFORM.hasSilkTouch(tool)) return 0;
        var id = Services.PLATFORM.getBlockId(state.getBlock());
        if (id == null || !"buildscape".equals(id.getNamespace())) return -1;
        return switch (id.getPath()) {
            case "sculk", "sculk_vein", "sculk_slab", "sculk_stairs", "sculk_wall", "sculk_vertical_slab" -> 1;
            case "sculk_catalyst", "sculk_shrieker", "sculk_sensor" -> 5;
            default -> -1;
        };
    }
}
