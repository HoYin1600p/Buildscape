package com.kingodogo.buildscape.registry.tab;

import com.kingodogo.buildscape.registry.ModItems;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.world.level.ItemLike;

/**
 * The hard-coded Buildscape creative tab order, in three stretches around the firework stars and the trophies.
 * Each stretch is the runs of the family-named {@code *TabOrder} classes joined in order. The entries are read
 * when the tab fills itself.
 */
public final class TabOrder {

    private TabOrder() {}

    /** Everything before the firework stars and rockets. */
    public static final List<Supplier<? extends ItemLike>> BEFORE_FIREWORKS = join(
            CopperTabOrder.ITEMS,
            SteelTabOrder.ITEMS,
            StoneTabOrder.ITEMS,
            WoolTabOrder.ITEMS,
            GlassTabOrder.ITEMS,
            DecorTabOrder.ITEMS);

    /** Entries that follow the firework stars and rockets and precede the trophies. */
    public static final List<Supplier<? extends ItemLike>> AFTER_FIREWORKS = List.<Supplier<? extends ItemLike>>of(
            ModItems.EXPERIENCE_BUCKET,
            ModItems.WANDERING_HOMEMAKER_SPAWN_EGG,
            ModItems.FESTIVE_WANDERING_HOMEMAKER_SPAWN_EGG);

    /** Everything after the trophies. */
    public static final List<Supplier<? extends ItemLike>> AFTER_TROPHIES = join(
            WoodTabOrder.ITEMS,
            CardboardTabOrder.ITEMS,
            NatureTabOrder.ITEMS);

    @SafeVarargs
    private static List<Supplier<? extends ItemLike>> join(List<Supplier<? extends ItemLike>>... runs) {
        List<Supplier<? extends ItemLike>> all = new ArrayList<>();
        for (List<Supplier<? extends ItemLike>> run : runs) {
            all.addAll(run);
        }
        return List.copyOf(all);
    }
}
