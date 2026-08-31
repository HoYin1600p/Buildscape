package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.stat.ModStats;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Comparator;

@Mixin(targets = "net.minecraft.client.gui.screens.achievement.StatsScreen$GeneralStatisticsList")
public abstract class GeneralStatisticsListMixin {

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/ObjectArrayList;sort(Ljava/util/Comparator;)V", remap = false))
    private void redirectSort(ObjectArrayList<Stat<ResourceLocation>> list, Comparator<Stat<ResourceLocation>> originalComparator) {
        boolean hasOtherMods = false;
        for (Stat<ResourceLocation> s : list) {
            String ns = s.getValue().getNamespace();
            if (!"minecraft".equals(ns) && !"buildscape".equals(ns)) {
                hasOtherMods = true;
                break;
            }
        }
        if (!hasOtherMods && ModStats.HEADER_OTHER_STAT != null) {
            list.remove(ModStats.HEADER_OTHER_STAT);
        }

        list.sort((a, b) -> {
            int pA = getPriority(a);
            int pB = getPriority(b);
            if (pA != pB) {
                return Integer.compare(pA, pB);
            }
            return originalComparator.compare(a, b);
        });
    }

    private static int getPriority(Stat<ResourceLocation> stat) {
        if (stat == ModStats.HEADER_MINECRAFT_STAT) return 0;
        ResourceLocation id = stat.getValue();
        if ("minecraft".equals(id.getNamespace())) return 1;
        if (stat == ModStats.HEADER_BUILDSCAPE_STAT) return 2;
        if ("buildscape".equals(id.getNamespace())) return 3;
        if (stat == ModStats.HEADER_OTHER_STAT) return 4;
        return 5;
    }
}
