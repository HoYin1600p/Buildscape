package com.kingodogo.buildscape.adapter.v121x;

import com.google.common.collect.ImmutableSet;
import com.kingodogo.buildscape.BuildscapeCommon;
import com.kingodogo.buildscape.mixin.PoiTypeAccessor;
import com.kingodogo.buildscape.mixin.PoiTypesAccessor;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;
import java.util.Set;

/** Registers the retained copper rod block states with the vanilla lightning rod point of interest. */
public final class CopperRodPoi {
    private static final String[] ROD_IDS = {
            "waxed_copper_rod", "exposed_copper_rod", "waxed_exposed_copper_rod", "weathered_copper_rod",
            "waxed_weathered_copper_rod", "oxidized_copper_rod", "waxed_oxidized_copper_rod"
    };
    private static boolean registered;

    private CopperRodPoi() {}

    public static synchronized void ensureRegistered() {
        if (registered) return;
        registered = true;
        Holder<PoiType> holder = BuiltInRegistries.POINT_OF_INTEREST_TYPE.getHolderOrThrow(PoiTypes.LIGHTNING_ROD);
        PoiTypeAccessor accessor = (PoiTypeAccessor) (Object) holder.value();
        Set<BlockState> states = new HashSet<>(accessor.getMatchingStates());
        var byState = PoiTypesAccessor.getTypeByState();
        for (String id : ROD_IDS) {
            Block rod = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath("buildscape", id));
            if (rod == net.minecraft.world.level.block.Blocks.AIR) {
                BuildscapeCommon.LOGGER.warn("Copper rod {} is not registered; it will not join the lightning rod POI", id);
                continue;
            }
            for (BlockState state : rod.getStateDefinition().getPossibleStates()) {
                byState.put(state, holder);
                states.add(state);
            }
        }
        accessor.setMatchingStates(ImmutableSet.copyOf(states));
    }
}
