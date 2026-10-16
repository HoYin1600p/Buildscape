package com.kingodogo.buildscape.adapter.v26x;

import com.kingodogo.buildscape.TestBootstrap;
import com.kingodogo.buildscape.registry.VanillaReplacementAliases;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Blocks;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RemovedBlockEntityDataTest {
    @BeforeAll
    static void bootstrap() {
        TestBootstrap.initialize();
    }

    @Test
    void copperChestPreservesInheritedInventoryNameLockAndLootData() {
        CompoundTag saved = saved("buildscape:copper_chest");
        CompoundTag stack = stack("minecraft:diamond", 12);
        stack.putByte("Slot", (byte) 26);
        ListTag items = new ListTag();
        items.add(stack);
        saved.put("Items", items);
        CompoundTag name = new CompoundTag();
        name.putString("text", "Saved chest");
        saved.put("CustomName", name);
        CompoundTag lock = new CompoundTag();
        lock.putString("items", "minecraft:tripwire_hook");
        saved.put("lock", lock);

        var copperChest = BuiltInRegistries.BLOCK.getValue(Identifier.parse("minecraft:copper_chest"));
        var migrated = RemovedBlockEntityData.prepare(copperChest.defaultBlockState(), saved);
        assertNotNull(migrated);
        assertNotNull(migrated.tag());
        assertEquals("minecraft:chest", migrated.tag().getStringOr("id", ""));
        assertEquals(items, migrated.tag().get("Items"));
        assertEquals(name, migrated.tag().get("CustomName"));
        assertEquals(lock, migrated.tag().get("lock"));
        assertEquals(13, migrated.tag().getIntOr("x", -1));
        assertEquals("buildscape:copper_chest", saved.getStringOr("id", ""));
        migrated.tag().getListOrEmpty("Items").getCompoundOrEmpty(0).putInt("count", 1);
        assertEquals(12, stack.getIntOr("count", -1), "Conversion must not mutate the chunk's original tag");

        CompoundTag loot = saved("buildscape:copper_chest");
        loot.putString("LootTable", "minecraft:chests/simple_dungeon");
        loot.putLong("LootTableSeed", 91234L);
        CompoundTag converted = RemovedBlockEntityData.convertChest(loot, "minecraft:chest");
        assertEquals(loot.get("LootTable"), converted.get("LootTable"));
        assertEquals(91234L, converted.getLongOr("LootTableSeed", 0));
    }

    @Test
    void everyRetiredCopperVariantUsesTheVanillaChestType() {
        var aliases = VanillaReplacementAliases.ALL.stream()
                .filter(alias -> alias.block() && alias.source().endsWith("copper_chest")).toList();
        assertEquals(8, aliases.size());
        for (var alias : aliases) {
            var block = BuiltInRegistries.BLOCK.getValue(Identifier.parse(alias.target()));
            var migration = RemovedBlockEntityData.prepare(block.defaultBlockState(), saved("buildscape:copper_chest"));
            assertNotNull(migration);
            assertNotNull(migration.tag());
            assertEquals("minecraft:chest", migration.tag().getStringOr("id", ""), alias.target());
            assertNull(RemovedBlockEntityData.prepare(block.defaultBlockState(), migration.tag()),
                    "Rewritten tags must bypass migration on the nested load");
        }
    }

    @Test
    void inheritedSignDataKeepsBothSidesFilteringColorGlowAndWax() {
        for (String type : new String[] {"bamboo_sign_block_entity", "mangrove_sign_block_entity"}) {
            CompoundTag saved = saved("buildscape:" + type);
            saved.put("front_text", text("Hello", "blue", true));
            saved.put("back_text", text("Behind", "red", false));
            saved.putBoolean("is_waxed", true);
            CompoundTag converted = RemovedBlockEntityData.convertSign(saved, "minecraft:sign");
            assertEquals(saved.get("front_text"), converted.get("front_text"));
            assertEquals(saved.get("back_text"), converted.get("back_text"));
            assertTrue(converted.getBooleanOr("is_waxed", false));
            assertEquals("minecraft:sign", converted.getStringOr("id", ""));
            assertEquals("buildscape:" + type, saved.getStringOr("id", ""));
        }
    }

    @Test
    void signMigrationSelectsOrdinaryWallAndHangingVanillaTypes() {
        CompoundTag saved = saved("buildscape:bamboo_sign_block_entity");
        saved.put("front_text", text("Hanging text", "black", false));
        for (var block : new net.minecraft.world.level.block.Block[] {
                Blocks.BAMBOO_SIGN, Blocks.BAMBOO_WALL_SIGN, Blocks.BAMBOO_HANGING_SIGN,
                Blocks.BAMBOO_WALL_HANGING_SIGN, Blocks.MANGROVE_SIGN, Blocks.MANGROVE_HANGING_SIGN}) {
            var migration = RemovedBlockEntityData.prepare(block.defaultBlockState(), saved);
            assertNotNull(migration);
            assertNotNull(migration.tag());
            String expected = block == Blocks.BAMBOO_HANGING_SIGN || block == Blocks.BAMBOO_WALL_HANGING_SIGN
                    || block == Blocks.MANGROVE_HANGING_SIGN ? "minecraft:hanging_sign" : "minecraft:sign";
            assertEquals(expected, migration.tag().getStringOr("id", ""));
            assertEquals(saved.get("front_text"), migration.tag().get("front_text"));
        }
    }

    @Test
    void shelfFlattensTheExactValueOutputDataFormatWithoutMovingSparseSlots() {
        CompoundTag saved = saved("buildscape:shelf");
        ListTag items = new ListTag();
        items.add(shelfItem(2, stack("minecraft:emerald", 7)));
        items.add(shelfItem(0, stack("minecraft:diamond", 2)));
        saved.put("Items", items);
        saved.putBoolean("align_items_to_bottom", true);
        var migration = RemovedBlockEntityData.prepare(Blocks.PALE_OAK_SHELF.defaultBlockState(), saved);
        assertNotNull(migration);
        CompoundTag converted = migration.tag();
        assertNotNull(converted);
        assertEquals("minecraft:shelf", converted.getStringOr("id", ""));
        assertTrue(converted.getBooleanOr("align_items_to_bottom", false));
        ListTag migratedItems = converted.getListOrEmpty("Items");
        assertEquals(2, migratedItems.size());
        CompoundTag right = migratedItems.getCompoundOrEmpty(0);
        assertEquals((byte) 2, right.getByteOr("Slot", (byte) -1));
        assertEquals("minecraft:emerald", right.getStringOr("id", ""));
        assertEquals(7, right.getIntOr("count", 0));
        assertEquals(0, migratedItems.getCompoundOrEmpty(1).getByteOr("Slot", (byte) -1));
        assertFalse(right.contains("Item"));
        assertTrue(items.getCompoundOrEmpty(0).contains("Item"));
        assertEquals("buildscape:shelf", saved.getStringOr("id", ""));
    }

    @Test
    void shelfKeepsComponentsFlatItemsAndEmptySlotsAndRejectsInvalidSlots() {
        CompoundTag saved = saved("buildscape:shelf");
        ListTag items = new ListTag();
        CompoundTag stack = stack("minecraft:diamond", 1);
        CompoundTag components = new CompoundTag();
        components.putString("minecraft:custom_name", "Shelf treasure");
        stack.put("components", components);
        items.add(shelfItem(1, stack));
        CompoundTag flat = stack("minecraft:emerald", 3);
        flat.putByte("Slot", (byte) 2);
        items.add(flat);
        items.add(shelfItem(-1, stack));
        items.add(shelfItem(3, stack));
        saved.put("Items", items);
        ListTag converted = RemovedBlockEntityData.convertShelf(saved, "minecraft:shelf").getListOrEmpty("Items");
        assertEquals(2, converted.size());
        assertEquals(components, converted.getCompoundOrEmpty(0).get("components"));
        assertEquals(flat, converted.getCompoundOrEmpty(1));
        assertTrue(RemovedBlockEntityData.convertShelf(saved("buildscape:shelf"), "minecraft:shelf")
                .getListOrEmpty("Items").isEmpty());
    }

    @Test
    void keptBuildscapeShelfAndValidTypesBypassMigrationAndUnknownTypesDrop() {
        assertFalse(RemovedBlockEntityData.isCandidate(Identifier.parse("buildscape:shelf"),
                Identifier.parse("buildscape:stripped_bamboo_shelf"), false));
        assertFalse(RemovedBlockEntityData.isCandidate(Identifier.parse("buildscape:shelf"),
                Identifier.parse("minecraft:pale_oak_shelf"), true));
        assertNull(RemovedBlockEntityData.prepare(Blocks.PALE_OAK_SHELF.defaultBlockState(), saved("minecraft:shelf")));
        var unknown = RemovedBlockEntityData.prepare(Blocks.PALE_OAK_SHELF.defaultBlockState(),
                saved("buildscape:unknown_block_entity"));
        assertNotNull(unknown);
        assertNull(unknown.tag());
        var wrongFamily = RemovedBlockEntityData.prepare(Blocks.PALE_OAK_SHELF.defaultBlockState(),
                saved("buildscape:copper_chest"));
        assertNotNull(wrongFamily);
        assertNull(wrongFamily.tag());
    }

    private static CompoundTag saved(String type) {
        CompoundTag saved = new CompoundTag();
        saved.putString("id", type);
        saved.putInt("x", 13);
        saved.putInt("y", 64);
        saved.putInt("z", -2);
        return saved;
    }

    private static CompoundTag stack(String item, int count) {
        CompoundTag stack = new CompoundTag();
        stack.putString("id", item);
        stack.putInt("count", count);
        return stack;
    }

    private static CompoundTag shelfItem(int slot, CompoundTag stack) {
        CompoundTag child = new CompoundTag();
        child.putInt("Slot", slot);
        child.put("Item", stack);
        return child;
    }

    private static CompoundTag text(String message, String color, boolean glowing) {
        CompoundTag text = new CompoundTag();
        ListTag messages = new ListTag();
        ListTag filtered = new ListTag();
        for (int i = 0; i < 4; i++) {
            messages.add(StringTag.valueOf(i == 0 ? message : ""));
            filtered.add(StringTag.valueOf(i == 0 ? "Filtered " + message : ""));
        }
        text.put("messages", messages);
        text.put("filtered_messages", filtered);
        text.putString("color", color);
        text.putBoolean("has_glowing_text", glowing);
        return text;
    }
}
