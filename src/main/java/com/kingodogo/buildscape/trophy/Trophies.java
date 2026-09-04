package com.kingodogo.buildscape.trophy;

import com.kingodogo.buildscape.block.ModBlocks;
import com.kingodogo.buildscape.item.ModCreativeModeTab;
import com.kingodogo.buildscape.item.ModItems;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.RegistryObject;

import java.util.*;

public class Trophies {
    private static final Map<String, TrophyDefinition> REGISTRY = new LinkedHashMap<>();
    private static final Map<String, TrophyDefinition> BY_ADVANCEMENT = new HashMap<>();

    // Single clean rectangular hitboxes per trophy type
    // Tall pillar / jar / froglight / hammer trophies (exceeding 1-block height by ~2 pixels)
    private static final Map<Direction, VoxelShape> PEDESTAL_SHAPES = TrophyDefinition.createDirectionalShapes(
        new double[]{4.0D, 0.0D, 4.0D, 12.0D, 1.0D, 12.0D},
        new double[]{7.0D, 0.0D, 3.0D, 9.0D, 2.0D, 5.0D},
        new double[]{3.0D, 0.0D, 7.0D, 5.0D, 2.0D, 9.0D},
        new double[]{7.0D, 0.0D, 11.0D, 9.0D, 2.0D, 13.0D},
        new double[]{11.0D, 0.0D, 7.0D, 13.0D, 2.0D, 9.0D},
        new double[]{5.0D, 1.0D, 5.0D, 11.0D, 10.0D, 11.0D},
        new double[]{4.375D, 9.85D, 4.125D, 11.625D, 11.85D, 11.875D},
        new double[]{4.875D, 11.85D, 5.0D, 10.875D, 17.85D, 11.0D}
    );

    // Wide Christmas stand trophies (Ornament, Stocking, Star, String Light)
    private static final Map<Direction, VoxelShape> STAND_SHAPES = TrophyDefinition.createDirectionalShapes(
        1.0D, 0.0D, 3.0D, 15.0D, 16.0D, 13.0D
    );

    // Buildscape Statuette Trophies (Gold, Emerald, Diamond, Netherite)
    private static final Map<Direction, VoxelShape> BUILDSCAPE_TROPHY_SHAPES = TrophyDefinition.createDirectionalShapes(
        1.0D, 0.0D, 1.0D, 15.0D, 30.0D, 15.0D
    );

    // 1. Pillar Trophies (Stone, Gold, Diamond, Netherite, Emerald)
    public static final TrophyDefinition STONE_PILLAR = register(
            TrophyDefinition.builder("stone_pillar_trophy")
                    .tier(TrophyTier.STONE)
                    .soundType(SoundType.METAL)
                    .hardness(1.5F)
                    .resistance(6.0F)
                    .shape(PEDESTAL_SHAPES)
                    .advancement("put_it_on_display")
                    .build()
    );

    public static final TrophyDefinition GOLD_PILLAR = register(
            TrophyDefinition.builder("gold_pillar_trophy")
                    .tier(TrophyTier.GOLD)
                    .soundType(SoundType.METAL)
                    .hardness(2.0F)
                    .resistance(6.0F)
                    .shape(PEDESTAL_SHAPES)
                    .advancement("columnist")
                    .build()
    );

    public static final TrophyDefinition DIAMOND_PILLAR = register(
            TrophyDefinition.builder("diamond_pillar_trophy")
                    .tier(TrophyTier.DIAMOND)
                    .soundType(SoundType.METAL)
                    .hardness(3.0F)
                    .resistance(8.0F)
                    .shape(PEDESTAL_SHAPES)
                    .advancement("art_collector")
                    .build()
    );

    public static final TrophyDefinition NETHERITE_PILLAR = register(
            TrophyDefinition.builder("netherite_pillar_trophy")
                    .tier(TrophyTier.NETHERITE)
                    .soundType(SoundType.METAL)
                    .hardness(4.0F)
                    .resistance(12.0F)
                    .shape(PEDESTAL_SHAPES)
                    .advancement("buildscape_museum")
                    .build()
    );

    public static final TrophyDefinition EMERALD_PILLAR = register(
            TrophyDefinition.builder("emerald_pillar_trophy")
                    .tier(TrophyTier.EMERALD)
                    .soundType(SoundType.METAL)
                    .hardness(2.5F)
                    .resistance(6.0F)
                    .shape(PEDESTAL_SHAPES)
                    .build()
    );

    // 2. Thematic / Item Trophies
    public static final TrophyDefinition GOLDEN_JAR = register(
            TrophyDefinition.builder("golden_jar_trophy")
                    .tier(TrophyTier.GOLD)
                    .soundType(SoundType.METAL)
                    .hardness(1.5F)
                    .resistance(6.0F)
                    .shape(PEDESTAL_SHAPES)
                    .advancement("jar_ring_display")
                    .build()
    );

    public static final TrophyDefinition GOLD_ORNAMENT = register(
            TrophyDefinition.builder("gold_ornament_trophy")
                    .tier(TrophyTier.GOLD)
                    .soundType(SoundType.METAL)
                    .hardness(1.5F)
                    .resistance(6.0F)
                    .shape(STAND_SHAPES)
                    .advancement("ornamental")
                    .build()
    );

    public static final TrophyDefinition EMERALD_STOCKING = register(
            TrophyDefinition.builder("emerald_stocking_trophy")
                    .tier(TrophyTier.EMERALD)
                    .soundType(SoundType.METAL)
                    .hardness(1.0F)
                    .resistance(4.0F)
                    .shape(STAND_SHAPES)
                    .advancement("christmas_every_day")
                    .build()
    );

    public static final TrophyDefinition STAR = register(
            TrophyDefinition.builder("star_trophy")
                    .tier(TrophyTier.SPECIAL)
                    .soundType(SoundType.METAL)
                    .lightEmission(15)
                    .hardness(2.0F)
                    .resistance(6.0F)
                    .shape(STAND_SHAPES)
                    .advancement("santas_little_helper")
                    .build()
    );

    public static final TrophyDefinition STRING_LIGHT = register(
            TrophyDefinition.builder("string_light_trophy")
                    .tier(TrophyTier.SPECIAL)
                    .soundType(SoundType.METAL)
                    .lightEmission(15)
                    .hardness(1.5F)
                    .resistance(6.0F)
                    .shape(STAND_SHAPES)
                    .advancement("light_em_up")
                    .build()
    );

    public static final TrophyDefinition FIRELIGHT = register(
            TrophyDefinition.builder("firelight_trophy")
                    .tier(TrophyTier.SPECIAL)
                    .soundType(SoundType.METAL)
                    .lightEmission(15)
                    .hardness(2.5F)
                    .resistance(8.0F)
                    .shape(PEDESTAL_SHAPES)
                    .advancement("reach_for_the_sky")
                    .build()
    );

    // 3. Hammer & Buildscape Milestone Trophies
    public static final TrophyDefinition DIAMOND_HAMMER = register(
            TrophyDefinition.builder("diamond_hammer_trophy")
                    .tier(TrophyTier.DIAMOND)
                    .soundType(SoundType.METAL)
                    .hardness(3.0F)
                    .resistance(8.0F)
                    .shape(PEDESTAL_SHAPES)
                    .advancement("hammer_time")
                    .build()
    );

    public static final TrophyDefinition GOLD_BUILDSCAPE = register(
            TrophyDefinition.builder("gold_buildscape_trophy")
                    .tier(TrophyTier.GOLD)
                    .soundType(SoundType.METAL)
                    .hardness(2.0F)
                    .resistance(6.0F)
                    .shape(BUILDSCAPE_TROPHY_SHAPES)
                    .advancement("one_more_block")
                    .build()
    );

    public static final TrophyDefinition EMERALD_BUILDSCAPE = register(
            TrophyDefinition.builder("emerald_buildscape_trophy")
                    .tier(TrophyTier.EMERALD)
                    .soundType(SoundType.METAL)
                    .hardness(2.5F)
                    .resistance(6.0F)
                    .shape(BUILDSCAPE_TROPHY_SHAPES)
                    .advancement("okay_one_more")
                    .build()
    );

    public static final TrophyDefinition DIAMOND_BUILDSCAPE = register(
            TrophyDefinition.builder("diamond_buildscape_trophy")
                    .tier(TrophyTier.DIAMOND)
                    .soundType(SoundType.METAL)
                    .hardness(3.0F)
                    .resistance(8.0F)
                    .shape(BUILDSCAPE_TROPHY_SHAPES)
                    .advancement("actually_one_last")
                    .build()
    );

    public static final TrophyDefinition NETHERITE_BUILDSCAPE = register(
            TrophyDefinition.builder("netherite_buildscape_trophy")
                    .tier(TrophyTier.NETHERITE)
                    .soundType(SoundType.METAL)
                    .hardness(4.0F)
                    .resistance(12.0F)
                    .shape(BUILDSCAPE_TROPHY_SHAPES)
                    .advancement("one_last_one_i_promise")
                    .build()
    );


    private static TrophyDefinition register(TrophyDefinition definition) {
        REGISTRY.put(definition.getId(), definition);
        if (definition.getAssociatedAdvancement() != null) {
            BY_ADVANCEMENT.put(definition.getAssociatedAdvancement(), definition);
        }

        // Register Block
        RegistryObject<TrophyBlock> blockObj = ModBlocks.BLOCKS.register(
                definition.getId(),
                () -> new TrophyBlock(definition)
        );
        definition.setBlockRegistryObject(blockObj);

        // Register Item
        RegistryObject<Item> itemObj = ModItems.ITEMS.register(
                definition.getId(),
                () -> new TrophyBlockItem(blockObj.get(), definition, new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB).stacksTo(1))
        );
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

    public static void init() {
    }
}
