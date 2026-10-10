package com.kingodogo.buildscape.event;

import com.kingodogo.buildscape.platform.Services;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public final class SculkExperienceLogic {
    private SculkExperienceLogic() {}

    /** -1 means this block keeps its ordinary experience behavior. */
    public static int experience(Player player, BlockState state, ItemStack tool) {
        var id = Services.PLATFORM.getBlockId(state.getBlock());
        if (id == null || !"buildscape".equals(id.getNamespace())) return -1;
        int amount = switch (id.getPath()) {
            case "sculk", "sculk_vein", "sculk_slab", "sculk_stairs", "sculk_wall", "sculk_vertical_slab" -> 1;
            case "sculk_catalyst", "sculk_shrieker", "sculk_sensor" -> 5;
            default -> -1;
        };
        if (amount < 0 || player == null) return -1;
        return player.getAbilities().instabuild || Services.PLATFORM.hasSilkTouch(tool) ? 0 : amount;
    }
}
