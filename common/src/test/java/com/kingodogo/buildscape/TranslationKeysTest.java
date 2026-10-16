package com.kingodogo.buildscape;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kingodogo.buildscape.adapter.v26x.BlockFactory;
import com.kingodogo.buildscape.block.BlockDefinition;
import com.kingodogo.buildscape.block.ModBlocks;
import com.kingodogo.buildscape.entity.EntityDefinition;
import com.kingodogo.buildscape.entity.ModEntities;
import com.kingodogo.buildscape.item.ItemDefinition;
import com.kingodogo.buildscape.item.ItemFactory;
import com.kingodogo.buildscape.item.ModCreativeTabs;
import com.kingodogo.buildscape.item.ModItems;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.registry.RegistrySupplier;
import com.kingodogo.buildscape.sound.ModSounds;
import com.kingodogo.buildscape.trophy.Trophies;
import com.kingodogo.buildscape.trophy.TrophyDefinition;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.MappedRegistry;

import net.minecraft.locale.Language;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Every name the mod shows must have an entry in assets/buildscape/lang/en_us.json (or be a vanilla key): blocks,
 * items (the block items use the block.* key, as vanilla's do), entity types, the creative tab and the subtitle
 * keys of the mod's sounds. The mod registers no enchantments and no mob effects, so those have nothing to check.
 */
class TranslationKeysTest {
    private static final Path MAIN_RESOURCES = Path.of(System.getProperty("buildscape.mainResources"));
    private static final Path LANG = MAIN_RESOURCES.resolve("assets/buildscape/lang/en_us.json");
    private static final Path SOUNDS = MAIN_RESOURCES.resolve("assets/buildscape/sounds.json");

    /** Blocks that cannot be built without a running game (see BlockstatePropertiesTest). */
    private static final java.util.Set<String> UNBUILDABLE_BLOCKS = java.util.Set.of("experience_liquid");

    @BeforeAll
    static void bootstrap() throws Exception {
        TestBootstrap.initialize();
        setField("unregisteredIntrusiveHolders", new java.util.IdentityHashMap<>());
        setField("frozen", false);
    }

    @AfterAll
    static void restoreRegistry() throws Exception {
        setField("frozen", true);
        setField("unregisteredIntrusiveHolders", null);
    }

    private static void setField(String name, Object value) throws Exception {
        var field = MappedRegistry.class.getDeclaredField(name);
        field.setAccessible(true);
        // Blocks and items are built headless, so both registries must hand out intrusive holders.
        field.set(net.minecraft.core.registries.BuiltInRegistries.BLOCK, value);
        field.set(net.minecraft.core.registries.BuiltInRegistries.ITEM, value instanceof java.util.IdentityHashMap<?, ?> ? new java.util.IdentityHashMap<>() : value);
    }

    private static boolean known(JsonObject lang, String key) {
        return lang.has(key) || Language.getInstance().has(key);
    }

    @Test
    void hazeBushesAndIceCrystalHaveEnglishEntries() throws Exception {
        JsonObject lang = JsonParser.parseString(Files.readString(LANG, StandardCharsets.UTF_8)).getAsJsonObject();
        for (String colour : List.of("white", "orange", "magenta", "light_blue", "yellow", "lime", "pink",
                "gray", "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black")) {
            String bush = colour + "_haze_bush";
            for (String key : List.of("block.buildscape." + bush, "block.buildscape.potted_" + bush,
                    "block.buildscape." + bush + ".drained")) {
                assertTrue(lang.has(key), key);
            }
        }
        for (String key : List.of("item.buildscape.bottle_of_mist", "tooltip.buildscape.bottle_of_mist.collect",
                "tooltip.buildscape.haze_bush.drained", "block.buildscape.ice_crystal")) {
            assertTrue(lang.has(key), key);
        }
    }

    @Test
    void everyNameTheModShowsHasAnEnglishEntry() throws Exception {
        JsonObject lang = JsonParser.parseString(Files.readString(LANG, StandardCharsets.UTF_8)).getAsJsonObject();
        // key -> what needs it
        Map<String, String> missing = new LinkedHashMap<>();

        ModBlocks.init();
        ModItems.init();
        ModEntities.init();
        ModSounds.init();

        // Blocks
        Map<String, Block> blocks = new LinkedHashMap<>();
        int blockCount = 0;
        for (RegistrySupplier<?> supplier : new ArrayList<>(Services.REGISTERED_BLOCKS)) {
            if (!(supplier.get() instanceof BlockDefinition def) || UNBUILDABLE_BLOCKS.contains(def.getId())) continue;
            Block block = new BlockFactory().createBlock(def);
            blocks.put(def.getId(), block);
            blockCount++;
            String key = block.getDescriptionId();
            if (!known(lang, key)) missing.put(key, "block " + def.getId());
        }
        assertTrue(blockCount > 2900, "expected the retained block list, got " + blockCount);

        // Items: block items through the real factory, the rest through createItem
        int itemCount = 0;
        int blockItems = 0;
        int addedItems = 0;
        int addedBlockItems = 0;
        List<String> wrongPrefix = new ArrayList<>();
        for (RegistrySupplier<?> supplier : new ArrayList<>(Services.REGISTERED_ITEMS)) {
            if (!(supplier.get() instanceof ItemDefinition def)) continue;
            Block matching = blocks.get(def.getId());
            Item item = matching != null
                    ? ItemFactory.createBlockItem(def.getId(), matching, def.getProperties())
                    : ItemFactory.createItem(def);
            itemCount++;
            boolean addedContent = def.getId().equals("ice_crystal") || def.getId().endsWith("_haze_bush");
            if (addedContent) addedItems++;
            if (matching != null) {
                blockItems++;
                if (addedContent) addedBlockItems++;
                assertInstanceOf(BlockItem.class, item, def.getId());
                if (!matching.getDescriptionId().equals(item.getDescriptionId())) {
                    wrongPrefix.add(def.getId() + ": item key " + item.getDescriptionId() + " but block key " + matching.getDescriptionId());
                }
            }
            String key = item.getDescriptionId();
            if (!known(lang, key)) missing.put(key, "item " + def.getId());

        }
        // Retained inventory after the two vanilla replacement passes. Exact counts
        // catch accidental omissions while allowing the intentionally removed items.
        assertEquals(2939 + addedItems, itemCount, "expected the retained item list plus haze bushes and ice crystal");
        assertEquals(2893 + addedBlockItems, blockItems, "expected the retained block item list plus new content");

        // Trophy blocks and their items use the block key (their block is built by the loader adapter, so check the key by id)
        for (TrophyDefinition trophy : Trophies.getAll()) {
            String key = "block.buildscape." + trophy.getId();
            if (!known(lang, key)) missing.put(key, "trophy " + trophy.getId());
        }

        // Entity types
        for (RegistrySupplier<?> supplier : new ArrayList<>(Services.REGISTERED_ENTITIES)) {
            if (!(supplier.get() instanceof EntityDefinition def)) continue;
            String key = "entity.buildscape." + def.getId();
            if (!known(lang, key)) missing.put(key, "entity " + def.getId());
        }

        // Creative tab
        if (!known(lang, ModCreativeTabs.TAB_TRANSLATION_KEY)) missing.put(ModCreativeTabs.TAB_TRANSLATION_KEY, "creative tab");

        // Sound subtitles
        JsonObject sounds = JsonParser.parseString(Files.readString(SOUNDS, StandardCharsets.UTF_8)).getAsJsonObject();
        for (var entry : sounds.entrySet()) {
            JsonObject sound = entry.getValue().getAsJsonObject();
            if (!sound.has("subtitle")) continue;
            String key = sound.get("subtitle").getAsString();
            if (!known(lang, key)) missing.put(key, "sound " + entry.getKey());
        }

        List<String> lines = new ArrayList<>();
        missing.forEach((key, owner) -> lines.add(key + "  <- " + owner));
        assertTrue(wrongPrefix.isEmpty() && lines.isEmpty(),
                wrongPrefix.size() + " block items do not use their block's name key (Item.Properties.useBlockDescriptionPrefix):\n"
                + String.join("\n", wrongPrefix) + "\n"
                + lines.size() + " translation keys are missing from en_us.json:\n" + String.join("\n", lines));
    }
}
