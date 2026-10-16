package com.kingodogo.buildscape.event;

import com.kingodogo.buildscape.dispenser.DispenserEffects;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.DispenserBlock;

public final class LoaderDispenserSetup {
    private LoaderDispenserSetup() {}

    public static void register() {
        Services.PLATFORM.wrapRegistryAction(() -> {
            var mist = Services.PLATFORM.getItem(new CommonId("buildscape", "bottle_of_mist"));
            var confetti = Services.PLATFORM.getItem(new CommonId("buildscape", "confetti"));
            if (mist != null && mist != Items.AIR) DispenserBlock.registerBehavior(mist, new DefaultDispenseItemBehavior() {
                @Override protected ItemStack execute(BlockSource source, ItemStack stack) {
                    var facing = source.getBlockState().getValue(DispenserBlock.FACING);
                    return DispenserEffects.mist(source.getLevel(), source.getPos().relative(facing), facing, stack);
                }
            });
            if (confetti != null && confetti != Items.AIR) DispenserBlock.registerBehavior(confetti, new DefaultDispenseItemBehavior() {
                @Override protected ItemStack execute(BlockSource source, ItemStack stack) {
                    var facing = source.getBlockState().getValue(DispenserBlock.FACING);
                    net.minecraft.world.level.block.entity.DispenserBlockEntity dispenser = source.getEntity();
                    int slot = 4;
                    for (int i = 0; i < dispenser.getContainerSize(); i++) {
                        if (dispenser.getItem(i) == stack) { slot = i; break; }
                    }
                    return DispenserEffects.confetti(source.getLevel(), source.getPos().relative(facing), facing, stack, slot);
                }
            });
        });
    }
}
