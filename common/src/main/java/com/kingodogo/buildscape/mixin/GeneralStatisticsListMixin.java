package com.kingodogo.buildscape.mixin;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.stats.Stat;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Comparator;

@Mixin(targets = "net.minecraft.client.gui.screens.achievement.StatsScreen$GeneralStatisticsList")
public abstract class GeneralStatisticsListMixin {

    @Dynamic
    @Redirect(
            method = "<init>",
            at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/ObjectArrayList;sort(Ljava/util/Comparator;)V", remap = false),
            require = 0
    )
    private void redirectSort(ObjectArrayList<Stat<?>> list, Comparator<Stat<?>> originalComparator) {
        MixinFactory.sortGeneralStatsList(list, originalComparator);
    }
}
