package com.kingodogo.buildscape.snapshot;

import com.kingodogo.buildscape.testutil.BootstrappedGameTest;
import com.kingodogo.buildscape.registry.ModCreativeModeTab;
import com.kingodogo.buildscape.registry.tab.TabOrder;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.RegistryObject;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.Assert.assertFalse;

/**
 * The creative tab fills itself from registered items, which a unit test cannot create (the item registry is frozen
 * and the tab sorts live ItemStacks). This snapshot therefore records the order of the hard-coded tab lists, resolved
 * to registry names, before the tab's own sorting: the icon, then the {@code TabOrder} data lists (before the fireworks, after them, after the trophies) in the order the
 * tab adds them. Entries are read through the lists' public API; vanilla items (plain lambdas) are not mod
 * registrations and are not part of the snapshot.
 */
public class CreativeTabSnapshotTest extends BootstrappedGameTest {

    private static void collect(List<Supplier<? extends ItemLike>> order, List<String> lines) {
        for (Supplier<? extends ItemLike> entry : order) {
            if (entry instanceof RegistryObject<?> registered) {
                lines.add(registered.getId().getPath());
            }
        }
    }

    @Test
    public void hardCodedCreativeTabOrder() throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add(ModCreativeModeTab.iconItem().getId().getPath());
        collect(TabOrder.BEFORE_FIREWORKS, lines);
        collect(TabOrder.AFTER_FIREWORKS, lines);
        collect(TabOrder.AFTER_TROPHIES, lines);
        assertFalse(lines.isEmpty());
        SnapshotSupport.assertSnapshot("creative-tab-hardcoded-order.txt", lines);
    }
}
