package com.kingodogo.buildscape.event;

import com.kingodogo.buildscape.item.ModItems;
import com.kingodogo.buildscape.util.FestiveGlintHelper;
import net.minecraft.world.item.ItemStack;

public final class FestiveGlintAnvilLogic {

    private FestiveGlintAnvilLogic() {}

    public static class AnvilResult {
        public final ItemStack output;
        public final int cost;
        public final int materialCost;

        public AnvilResult(ItemStack output, int cost, int materialCost) {
            this.output = output;
            this.cost = cost;
            this.materialCost = materialCost;
        }
    }

    public static AnvilResult processAnvil(ItemStack left, ItemStack right, String renameText) {
        if (left.isEmpty() || right.isEmpty()) {
            return null;
        }

        net.minecraft.world.item.Item shard = com.kingodogo.buildscape.platform.Services.PLATFORM.getItem(new com.kingodogo.buildscape.util.CommonId("buildscape", "festive_glint_shard"));
        if (shard != null && right.is(shard)) {
            if (!FestiveGlintHelper.isEnchantedItem(left)) {
                return null;
            }

            ItemStack output = left.copy();
            output.setCount(1);
            FestiveGlintHelper.applyFestiveGlint(output);

            return new AnvilResult(output, 0, 0);
        }

        return null;
    }
}
