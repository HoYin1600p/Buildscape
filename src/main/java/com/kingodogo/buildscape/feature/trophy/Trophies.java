package com.kingodogo.buildscape.feature.trophy;

import com.kingodogo.buildscape.registry.blocks.BlocksRegister;
import com.kingodogo.buildscape.registry.ModCreativeModeTab;
import com.kingodogo.buildscape.registry.items.ItemsRegister;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.RegistryObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

public class Trophies {
    private static final Map<String, TrophyDefinition> REGISTRY = new LinkedHashMap<>();
    private static final Map<String, TrophyDefinition> BY_ADVANCEMENT = new HashMap<>();

    private static final Map<Direction, VoxelShape> PEDESTAL_SHAPES = TrophyDefinition.createDirectionalShapes(
            new double[]{4.0, 0.0, 4.0, 12.0, 1.0, 12.0},
            new double[]{7.0, 0.0, 3.0, 9.0, 2.0, 5.0},
            new double[]{3.0, 0.0, 7.0, 5.0, 2.0, 9.0},
            new double[]{7.0, 0.0, 11.0, 9.0, 2.0, 13.0},
            new double[]{11.0, 0.0, 7.0, 13.0, 2.0, 9.0},
            new double[]{5.0, 1.0, 5.0, 11.0, 10.0, 11.0},
            new double[]{4.375, 9.85, 4.125, 11.625, 11.85, 11.875},
            new double[]{4.875, 11.85, 5.0, 10.875, 17.85, 11.0});

    private static final Map<Direction, VoxelShape> STAND_SHAPES = TrophyDefinition.createDirectionalShapes(
            1.0, 0.0, 3.0, 15.0, 16.0, 13.0);

    private static final Map<Direction, VoxelShape> BUILDSCAPE_TROPHY_SHAPES = TrophyDefinition.createDirectionalShapes(
            1.0, 0.0, 1.0, 15.0, 30.0, 15.0);

    public static final TrophyDefinition STONE_PILLAR = register(
            TrophyDefinition.builder("stone_pillar_trophy")
                    .tier(TrophyTier.STONE)
                    .soundType(SoundType.METAL)
                    .hardness(1.5F)
                    .resistance(6.0F)
                    .shape(PEDESTAL_SHAPES)
                    .advancement("reach_for_the_sky")
                    .build());

    public static final TrophyDefinition GOLD_PILLAR = register(
            TrophyDefinition.builder("gold_pillar_trophy")
                    .tier(TrophyTier.GOLD)
                    .soundType(SoundType.METAL)
                    .hardness(2.0F)
                    .resistance(6.0F)
                    .shape(PEDESTAL_SHAPES)
                    .advancement("put_it_on_display")
                    .build());

    public static final TrophyDefinition DIAMOND_PILLAR = register(
            TrophyDefinition.builder("diamond_pillar_trophy")
                    .tier(TrophyTier.DIAMOND)
                    .soundType(SoundType.METAL)
                    .hardness(3.0F)
                    .resistance(8.0F)
                    .shape(PEDESTAL_SHAPES)
                    .advancement("art_collector")
                    .build());

    public static final TrophyDefinition NETHERITE_PILLAR = register(
            TrophyDefinition.builder("netherite_pillar_trophy")
                    .tier(TrophyTier.NETHERITE)
                    .soundType(SoundType.METAL)
                    .hardness(4.0F)
                    .resistance(12.0F)
                    .shape(PEDESTAL_SHAPES)
                    .advancement("buildscape_museum")
                    .build());

    public static final TrophyDefinition EMERALD_PILLAR = register(
            TrophyDefinition.builder("emerald_pillar_trophy")
                    .tier(TrophyTier.EMERALD)
                    .soundType(SoundType.METAL)
                    .hardness(2.5F)
                    .resistance(6.0F)
                    .shape(PEDESTAL_SHAPES)
                    .advancement("columnist")
                    .build());

    public static final TrophyDefinition GOLDEN_JAR = register(
            TrophyDefinition.builder("golden_jar_trophy")
                    .tier(TrophyTier.GOLD)
                    .soundType(SoundType.METAL)
                    .hardness(1.5F)
                    .resistance(6.0F)
                    .shape(PEDESTAL_SHAPES)
                    .advancement("jar_ring_display")
                    .build());

    public static final TrophyDefinition GOLD_ORNAMENT = register(
            TrophyDefinition.builder("gold_ornament_trophy")
                    .tier(TrophyTier.GOLD)
                    .soundType(SoundType.METAL)
                    .hardness(1.5F)
                    .resistance(6.0F)
                    .shape(STAND_SHAPES)
                    .advancement("ornamental")
                    .build());

    public static final TrophyDefinition EMERALD_STOCKING = register(
            TrophyDefinition.builder("emerald_stocking_trophy")
                    .tier(TrophyTier.EMERALD)
                    .soundType(SoundType.METAL)
                    .hardness(1.0F)
                    .resistance(4.0F)
                    .shape(STAND_SHAPES)
                    .advancement("christmas_every_day")
                    .build());

    public static final TrophyDefinition STAR = register(
            TrophyDefinition.builder("star_trophy")
                    .tier(TrophyTier.SPECIAL)
                    .soundType(SoundType.METAL)
                    .lightEmission(15)
                    .hardness(2.0F)
                    .resistance(6.0F)
                    .shape(STAND_SHAPES)
                    .advancement("santas_little_helper")
                    .build());

    public static final TrophyDefinition STRING_LIGHT = register(
            TrophyDefinition.builder("string_light_trophy")
                    .tier(TrophyTier.SPECIAL)
                    .soundType(SoundType.METAL)
                    .lightEmission(15)
                    .hardness(1.5F)
                    .resistance(6.0F)
                    .shape(STAND_SHAPES)
                    .advancement("light_em_up")
                    .build());

    public static final TrophyDefinition FIRELIGHT = register(
            TrophyDefinition.builder("firelight_trophy")
                    .tier(TrophyTier.SPECIAL)
                    .soundType(SoundType.METAL)
                    .lightEmission(15)
                    .hardness(2.5F)
                    .resistance(8.0F)
                    .shape(PEDESTAL_SHAPES)
                    .advancement("rainbow_mood_light")
                    .build());

    public static final TrophyDefinition DIAMOND_HAMMER = register(
            TrophyDefinition.builder("diamond_hammer_trophy")
                    .tier(TrophyTier.DIAMOND)
                    .soundType(SoundType.METAL)
                    .hardness(3.0F)
                    .resistance(8.0F)
                    .shape(PEDESTAL_SHAPES)
                    .advancement("hammer_time")
                    .build());

    public static final TrophyDefinition GOLD_BUILDSCAPE = register(
            TrophyDefinition.builder("gold_buildscape_trophy")
                    .tier(TrophyTier.GOLD)
                    .soundType(SoundType.METAL)
                    .hardness(2.0F)
                    .resistance(6.0F)
                    .shape(BUILDSCAPE_TROPHY_SHAPES)
                    .advancement("one_more_block")
                    .build());

    public static final TrophyDefinition EMERALD_BUILDSCAPE = register(
            TrophyDefinition.builder("emerald_buildscape_trophy")
                    .tier(TrophyTier.EMERALD)
                    .soundType(SoundType.METAL)
                    .hardness(2.5F)
                    .resistance(6.0F)
                    .shape(BUILDSCAPE_TROPHY_SHAPES)
                    .advancement("okay_one_more")
                    .build());

    public static final TrophyDefinition DIAMOND_BUILDSCAPE = register(
            TrophyDefinition.builder("diamond_buildscape_trophy")
                    .tier(TrophyTier.DIAMOND)
                    .soundType(SoundType.METAL)
                    .hardness(3.0F)
                    .resistance(8.0F)
                    .shape(BUILDSCAPE_TROPHY_SHAPES)
                    .advancement("actually_one_last")
                    .build());

    public static final TrophyDefinition NETHERITE_BUILDSCAPE = register(
            TrophyDefinition.builder("netherite_buildscape_trophy")
                    .tier(TrophyTier.NETHERITE)
                    .soundType(SoundType.METAL)
                    .hardness(4.0F)
                    .resistance(12.0F)
                    .shape(BUILDSCAPE_TROPHY_SHAPES)
                    .advancement("one_last_one_i_promise")
                    .build());

    private static TrophyDefinition register(TrophyDefinition definition) {
        REGISTRY.put(definition.getId(), definition);
        if (definition.getAssociatedAdvancement() != null) {
            BY_ADVANCEMENT.put(definition.getAssociatedAdvancement(), definition);
        }

        RegistryObject<TrophyBlock> blockObj = BlocksRegister.blocks().register(
                definition.getId(),
                () -> new TrophyBlock(definition));
        definition.setBlockRegistryObject(blockObj);

        RegistryObject<Item> itemObj = ItemsRegister.items().register(
                definition.getId(),
                () -> new TrophyBlockItem(blockObj.get(), definition,
                        new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB).stacksTo(1)));
        definition.setItemRegistryObject(itemObj);

        return definition;
    }

    public static Collection<TrophyDefinition> getAll() {
        return REGISTRY.values();
    }

    public static TrophyDefinition get(String id) {
        return REGISTRY.get(id);
    }

    public static Item getRewardForAdvancement(String advancementId) {
        TrophyDefinition def = BY_ADVANCEMENT.get(advancementId);
        if (def != null && def.getItem() != null) {
            return def.getItem();
        }
        return null;
    }

    public static void init() {}
}
