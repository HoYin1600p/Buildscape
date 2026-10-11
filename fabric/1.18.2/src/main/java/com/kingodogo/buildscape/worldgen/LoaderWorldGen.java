package com.kingodogo.buildscape.worldgen;

import com.kingodogo.buildscape.registry.FabricRegistryAdapter;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

/** Register configured features after the Fabric block batch is ready. */
public final class LoaderWorldGen {
    private static final Set<String> IDS = new HashSet<>();
    private LoaderWorldGen() {}

    public static boolean contains(CommonId id) {
        return "buildscape".equals(id.getNamespace()) && IDS.contains(id.getPath());
    }
    private static void queue(FabricRegistryAdapter registry, String path, Supplier<ConfiguredFeature<?, ?>> factory) {
        if (!IDS.add(path)) throw new IllegalStateException("Duplicate configured feature: " + path);
        com.kingodogo.buildscape.platform.Services.PLATFORM.wrapRegistryAction(() -> com.kingodogo.buildscape.platform.Services.PLATFORM.registerConfiguredFeature(new CommonId("buildscape", path), factory.get()));
    }
    public static void register(FabricRegistryAdapter registry) {
        // 1.18.2 keeps its own mangrove trees (26.2 uses vanilla's), shaped by the v118x adapter.
        queue(registry, "mangrove", () -> com.kingodogo.buildscape.adapter.v118x.WorldGenFactory.createMangroveTreeFeature(false));
        queue(registry, "tall_mangrove", () -> com.kingodogo.buildscape.adapter.v118x.WorldGenFactory.createMangroveTreeFeature(true));
        queue(registry, "poplar", ModConfiguredFeatures.POPLAR_TREE::get);
        queue(registry, "pale_oak", ModConfiguredFeatures.PALE_OAK_TREE::get);
        queue(registry, "cherry", ModConfiguredFeatures.CHERRY_TREE::get);
        queue(registry, "red_monets", ModConfiguredFeatures.RED_MONETS::get);
        queue(registry, "blue_monets", ModConfiguredFeatures.BLUE_MONETS::get);
        queue(registry, "purple_monets", ModConfiguredFeatures.PURPLE_MONETS::get);
        queue(registry, "light_blue_monets", ModConfiguredFeatures.LIGHT_BLUE_MONETS::get);
        queue(registry, "pink_monets", ModConfiguredFeatures.PINK_MONETS::get);
        queue(registry, "yellow_monets", ModConfiguredFeatures.YELLOW_MONETS::get);
        queue(registry, "red_petals", ModConfiguredFeatures.RED_PETALS::get);
        queue(registry, "blue_petals", ModConfiguredFeatures.BLUE_PETALS::get);
        queue(registry, "orange_petals", ModConfiguredFeatures.ORANGE_PETALS::get);
        queue(registry, "pink_petals", ModConfiguredFeatures.PINK_PETALS::get);
        queue(registry, "purple_petals", ModConfiguredFeatures.PURPLE_PETALS::get);
        queue(registry, "all_petals", ModConfiguredFeatures.ALL_PETALS::get);
        queue(registry, "red_petal_1_n", ModConfiguredFeatures.RED_PETAL_1_N::get);
        queue(registry, "red_petal_1_s", ModConfiguredFeatures.RED_PETAL_1_S::get);
        queue(registry, "red_petal_1_e", ModConfiguredFeatures.RED_PETAL_1_E::get);
        queue(registry, "red_petal_1_w", ModConfiguredFeatures.RED_PETAL_1_W::get);
        queue(registry, "red_petal_2_n", ModConfiguredFeatures.RED_PETAL_2_N::get);
        queue(registry, "red_petal_2_s", ModConfiguredFeatures.RED_PETAL_2_S::get);
        queue(registry, "red_petal_2_e", ModConfiguredFeatures.RED_PETAL_2_E::get);
        queue(registry, "red_petal_2_w", ModConfiguredFeatures.RED_PETAL_2_W::get);
        queue(registry, "red_petal_3_n", ModConfiguredFeatures.RED_PETAL_3_N::get);
        queue(registry, "red_petal_3_s", ModConfiguredFeatures.RED_PETAL_3_S::get);
        queue(registry, "red_petal_3_e", ModConfiguredFeatures.RED_PETAL_3_E::get);
        queue(registry, "red_petal_3_w", ModConfiguredFeatures.RED_PETAL_3_W::get);
        queue(registry, "red_petal_4_n", ModConfiguredFeatures.RED_PETAL_4_N::get);
        queue(registry, "red_petal_4_s", ModConfiguredFeatures.RED_PETAL_4_S::get);
        queue(registry, "red_petal_4_e", ModConfiguredFeatures.RED_PETAL_4_E::get);
        queue(registry, "red_petal_4_w", ModConfiguredFeatures.RED_PETAL_4_W::get);
        queue(registry, "blue_petal_1_n", ModConfiguredFeatures.BLUE_PETAL_1_N::get);
        queue(registry, "blue_petal_1_s", ModConfiguredFeatures.BLUE_PETAL_1_S::get);
        queue(registry, "blue_petal_1_e", ModConfiguredFeatures.BLUE_PETAL_1_E::get);
        queue(registry, "blue_petal_1_w", ModConfiguredFeatures.BLUE_PETAL_1_W::get);
        queue(registry, "blue_petal_2_n", ModConfiguredFeatures.BLUE_PETAL_2_N::get);
        queue(registry, "blue_petal_2_s", ModConfiguredFeatures.BLUE_PETAL_2_S::get);
        queue(registry, "blue_petal_2_e", ModConfiguredFeatures.BLUE_PETAL_2_E::get);
        queue(registry, "blue_petal_2_w", ModConfiguredFeatures.BLUE_PETAL_2_W::get);
        queue(registry, "blue_petal_3_n", ModConfiguredFeatures.BLUE_PETAL_3_N::get);
        queue(registry, "blue_petal_3_s", ModConfiguredFeatures.BLUE_PETAL_3_S::get);
        queue(registry, "blue_petal_3_e", ModConfiguredFeatures.BLUE_PETAL_3_E::get);
        queue(registry, "blue_petal_3_w", ModConfiguredFeatures.BLUE_PETAL_3_W::get);
        queue(registry, "blue_petal_4_n", ModConfiguredFeatures.BLUE_PETAL_4_N::get);
        queue(registry, "blue_petal_4_s", ModConfiguredFeatures.BLUE_PETAL_4_S::get);
        queue(registry, "blue_petal_4_e", ModConfiguredFeatures.BLUE_PETAL_4_E::get);
        queue(registry, "blue_petal_4_w", ModConfiguredFeatures.BLUE_PETAL_4_W::get);
        queue(registry, "orange_petal_1_n", ModConfiguredFeatures.ORANGE_PETAL_1_N::get);
        queue(registry, "orange_petal_1_s", ModConfiguredFeatures.ORANGE_PETAL_1_S::get);
        queue(registry, "orange_petal_1_e", ModConfiguredFeatures.ORANGE_PETAL_1_E::get);
        queue(registry, "orange_petal_1_w", ModConfiguredFeatures.ORANGE_PETAL_1_W::get);
        queue(registry, "orange_petal_2_n", ModConfiguredFeatures.ORANGE_PETAL_2_N::get);
        queue(registry, "orange_petal_2_s", ModConfiguredFeatures.ORANGE_PETAL_2_S::get);
        queue(registry, "orange_petal_2_e", ModConfiguredFeatures.ORANGE_PETAL_2_E::get);
        queue(registry, "orange_petal_2_w", ModConfiguredFeatures.ORANGE_PETAL_2_W::get);
        queue(registry, "orange_petal_3_n", ModConfiguredFeatures.ORANGE_PETAL_3_N::get);
        queue(registry, "orange_petal_3_s", ModConfiguredFeatures.ORANGE_PETAL_3_S::get);
        queue(registry, "orange_petal_3_e", ModConfiguredFeatures.ORANGE_PETAL_3_E::get);
        queue(registry, "orange_petal_3_w", ModConfiguredFeatures.ORANGE_PETAL_3_W::get);
        queue(registry, "orange_petal_4_n", ModConfiguredFeatures.ORANGE_PETAL_4_N::get);
        queue(registry, "orange_petal_4_s", ModConfiguredFeatures.ORANGE_PETAL_4_S::get);
        queue(registry, "orange_petal_4_e", ModConfiguredFeatures.ORANGE_PETAL_4_E::get);
        queue(registry, "orange_petal_4_w", ModConfiguredFeatures.ORANGE_PETAL_4_W::get);
        queue(registry, "pink_petal_1_n", ModConfiguredFeatures.PINK_PETAL_1_N::get);
        queue(registry, "pink_petal_1_s", ModConfiguredFeatures.PINK_PETAL_1_S::get);
        queue(registry, "pink_petal_1_e", ModConfiguredFeatures.PINK_PETAL_1_E::get);
        queue(registry, "pink_petal_1_w", ModConfiguredFeatures.PINK_PETAL_1_W::get);
        queue(registry, "pink_petal_2_n", ModConfiguredFeatures.PINK_PETAL_2_N::get);
        queue(registry, "pink_petal_2_s", ModConfiguredFeatures.PINK_PETAL_2_S::get);
        queue(registry, "pink_petal_2_e", ModConfiguredFeatures.PINK_PETAL_2_E::get);
        queue(registry, "pink_petal_2_w", ModConfiguredFeatures.PINK_PETAL_2_W::get);
        queue(registry, "pink_petal_3_n", ModConfiguredFeatures.PINK_PETAL_3_N::get);
        queue(registry, "pink_petal_3_s", ModConfiguredFeatures.PINK_PETAL_3_S::get);
        queue(registry, "pink_petal_3_e", ModConfiguredFeatures.PINK_PETAL_3_E::get);
        queue(registry, "pink_petal_3_w", ModConfiguredFeatures.PINK_PETAL_3_W::get);
        queue(registry, "pink_petal_4_n", ModConfiguredFeatures.PINK_PETAL_4_N::get);
        queue(registry, "pink_petal_4_s", ModConfiguredFeatures.PINK_PETAL_4_S::get);
        queue(registry, "pink_petal_4_e", ModConfiguredFeatures.PINK_PETAL_4_E::get);
        queue(registry, "pink_petal_4_w", ModConfiguredFeatures.PINK_PETAL_4_W::get);
        queue(registry, "purple_petal_1_n", ModConfiguredFeatures.PURPLE_PETAL_1_N::get);
        queue(registry, "purple_petal_1_s", ModConfiguredFeatures.PURPLE_PETAL_1_S::get);
        queue(registry, "purple_petal_1_e", ModConfiguredFeatures.PURPLE_PETAL_1_E::get);
        queue(registry, "purple_petal_1_w", ModConfiguredFeatures.PURPLE_PETAL_1_W::get);
        queue(registry, "purple_petal_2_n", ModConfiguredFeatures.PURPLE_PETAL_2_N::get);
        queue(registry, "purple_petal_2_s", ModConfiguredFeatures.PURPLE_PETAL_2_S::get);
        queue(registry, "purple_petal_2_e", ModConfiguredFeatures.PURPLE_PETAL_2_E::get);
        queue(registry, "purple_petal_2_w", ModConfiguredFeatures.PURPLE_PETAL_2_W::get);
        queue(registry, "purple_petal_3_n", ModConfiguredFeatures.PURPLE_PETAL_3_N::get);
        queue(registry, "purple_petal_3_s", ModConfiguredFeatures.PURPLE_PETAL_3_S::get);
        queue(registry, "purple_petal_3_e", ModConfiguredFeatures.PURPLE_PETAL_3_E::get);
        queue(registry, "purple_petal_3_w", ModConfiguredFeatures.PURPLE_PETAL_3_W::get);
        queue(registry, "purple_petal_4_n", ModConfiguredFeatures.PURPLE_PETAL_4_N::get);
        queue(registry, "purple_petal_4_s", ModConfiguredFeatures.PURPLE_PETAL_4_S::get);
        queue(registry, "purple_petal_4_e", ModConfiguredFeatures.PURPLE_PETAL_4_E::get);
        queue(registry, "purple_petal_4_w", ModConfiguredFeatures.PURPLE_PETAL_4_W::get);
        queue(registry, "clover_1_n", ModConfiguredFeatures.CLOVER_1_N::get);
        queue(registry, "clover_1_s", ModConfiguredFeatures.CLOVER_1_S::get);
        queue(registry, "clover_1_e", ModConfiguredFeatures.CLOVER_1_E::get);
        queue(registry, "clover_1_w", ModConfiguredFeatures.CLOVER_1_W::get);
        queue(registry, "clover_2_n", ModConfiguredFeatures.CLOVER_2_N::get);
        queue(registry, "clover_2_s", ModConfiguredFeatures.CLOVER_2_S::get);
        queue(registry, "clover_2_e", ModConfiguredFeatures.CLOVER_2_E::get);
        queue(registry, "clover_2_w", ModConfiguredFeatures.CLOVER_2_W::get);
        queue(registry, "clover_3_n", ModConfiguredFeatures.CLOVER_3_N::get);
        queue(registry, "clover_3_s", ModConfiguredFeatures.CLOVER_3_S::get);
        queue(registry, "clover_3_e", ModConfiguredFeatures.CLOVER_3_E::get);
        queue(registry, "clover_3_w", ModConfiguredFeatures.CLOVER_3_W::get);
        queue(registry, "clover_4_n", ModConfiguredFeatures.CLOVER_4_N::get);
        queue(registry, "clover_4_s", ModConfiguredFeatures.CLOVER_4_S::get);
        queue(registry, "clover_4_e", ModConfiguredFeatures.CLOVER_4_E::get);
        queue(registry, "clover_4_w", ModConfiguredFeatures.CLOVER_4_W::get);
        queue(registry, "clover", ModConfiguredFeatures.CLOVER::get);
        queue(registry, "wildflowers", ModConfiguredFeatures.WILDFLOWERS::get);
        queue(registry, "leaf_litter", ModConfiguredFeatures.LEAF_LITTER::get);
        queue(registry, "colored_spore_blossom", ModConfiguredFeatures.COLORED_SPORE_BLOSSOM::get);
        queue(registry, "all_colored_spore_blossom", ModConfiguredFeatures.ALL_COLORED_SPORE_BLOSSOM::get);
        queue(registry, "mangrove_propagule", ModConfiguredFeatures.MANGROVE_PROPAGULE::get);
        queue(registry, "mangrove_propagule_patch_1", ModConfiguredFeatures.MANGROVE_PROPAGULE_PATCH_1::get);
        queue(registry, "mangrove_propagule_patch_2", ModConfiguredFeatures.MANGROVE_PROPAGULE_PATCH_2::get);
        queue(registry, "mangrove_propagule_patch_3", ModConfiguredFeatures.MANGROVE_PROPAGULE_PATCH_3::get);
        queue(registry, "mangrove_propagule_patch_4", ModConfiguredFeatures.MANGROVE_PROPAGULE_PATCH_4::get);
        queue(registry, "mangrove_propagule_patch_5", ModConfiguredFeatures.MANGROVE_PROPAGULE_PATCH_5::get);
        queue(registry, "mangrove_propagule_patch_6", ModConfiguredFeatures.MANGROVE_PROPAGULE_PATCH_6::get);
        queue(registry, "mangrove_propagule_patch_7", ModConfiguredFeatures.MANGROVE_PROPAGULE_PATCH_7::get);
        queue(registry, "mangrove_propagule_patch_8", ModConfiguredFeatures.MANGROVE_PROPAGULE_PATCH_8::get);
    }
}

