package com.kingodogo.buildscape.copper;

import com.kingodogo.buildscape.block.copper.CopperTransitions;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

public class CopperRegistryMappingTest {
    private static final Path BLOCK_SOURCE = Path.of("src/main/java/com/kingodogo/buildscape/block");
    private static final String[] AGES = {"", "exposed_", "weathered_", "oxidized_"};

    @Test
    public void everyRegisteredCopperStageResolvesInWeatheringTable() throws IOException {
        Map<String, String> registry = registrations();
        CopperTransitions<String> table = new CopperTransitions<>();
        Set<String> mapped = new HashSet<>();
        String handler = Files.readString(BLOCK_SOURCE.resolve("CopperOxidationHandler.java"));
        Matcher chains = Pattern.compile("registerChain\\(ModBlocks\\.(\\w+),\\s*ModBlocks\\.(\\w+),"
                + "\\s*ModBlocks\\.(\\w+),\\s*ModBlocks\\.(\\w+)\\);").matcher(handler);
        int families = 0;
        while (chains.find()) {
            String[] stages = new String[4];
            for (int i = 0; i < stages.length; i++) {
                stages[i] = registry.get(chains.group(i + 1));
                assertNotNull("unregistered weathering target: " + chains.group(i + 1), stages[i]);
                assertTrue("stage belongs to multiple families: " + stages[i], mapped.add(stages[i]));
            }
            table.addChain(stages[0], stages[1], stages[2], stages[3]);
            families++;
        }
        assertTrue("weathering families were found", families > 0);

        Map<String, String> ids = new HashMap<>();
        for (String id : registry.values()) ids.put(id, id);
        int copperBlocks = 0;
        for (String id : registry.values()) {
            if (!id.contains("copper")) continue;
            copperBlocks++;
            // Copper pillars are BuildScape pillars (registry entry, colours, displayed item); swapping their block
            // would run pillar removal, so they keep their stage like in 4.0.5.
            if (id.startsWith("waxed_") || id.equals("copper_torch") || id.equals("copper_wall_torch")
                    || id.endsWith("copper_pillar")) {
                assertNull("waxed or non-weathering block: " + id, table.next(id));
                assertFalse("waxed or non-weathering block in a chain: " + id, mapped.contains(id));
                continue;
            }
            assertTrue("missing copper family: " + id, mapped.contains(id));
            String prefix = id.startsWith("bit_") ? "bit_" : id.startsWith("large_") ? "large_" : "";
            String base = id.substring(prefix.length());
            int age = 0;
            for (int i = 1; i < AGES.length; i++) {
                if (base.startsWith(AGES[i])) {
                    age = i;
                    base = base.substring(AGES[i].length());
                    break;
                }
            }
            if (age == AGES.length - 1) {
                assertNull("oxidized block has no next stage: " + id, table.next(id));
            } else {
                String nextId = prefix + AGES[age + 1] + base;
                assertTrue("next stage must exist in registrations: " + nextId, ids.containsKey(nextId));
                assertSame("next stage stays in its copper family: " + id, ids.get(nextId), table.next(id));
            }
        }
        assertTrue("registered copper blocks were inspected", copperBlocks > 100);
    }

    @Test
    public void mistAndRandomWeatheringShareTheNextStageLookup() throws IOException {
        String handler = Files.readString(BLOCK_SOURCE.resolve("CopperOxidationHandler.java"));
        assertTrue(Pattern.compile("getNextOxidationState\\(BlockState state\\)\\s*\\{\\s*"
                + "return getNext\\(state\\)\\.orElse\\(null\\);\\s*}").matcher(handler).find());
        String weathering = Files.readString(BLOCK_SOURCE.resolve("copper/BuildscapeWeatheringCopper.java"));
        assertTrue(Pattern.compile("getNext\\(BlockState state\\)\\s*\\{\\s*"
                + "return CopperOxidationHandler\\.getNext\\(state\\);\\s*}").matcher(weathering).find());
    }

    private static Map<String, String> registrations() throws IOException {
        String source = Files.readString(BLOCK_SOURCE.resolve("ModBlocks.java"));
        Matcher entries = Pattern.compile("RegistryObject<Block>\\s+(\\w+)\\s*=\\s*"
                + "BLOCKS\\s*\\.register\\(\\s*\"([^\"]+)\"").matcher(source);
        Map<String, String> registry = new HashMap<>();
        while (entries.find()) {
            assertNull("duplicate block field: " + entries.group(1),
                    registry.put(entries.group(1), entries.group(2)));
        }
        assertFalse("block registrations were found", registry.isEmpty());
        return registry;
    }
}
