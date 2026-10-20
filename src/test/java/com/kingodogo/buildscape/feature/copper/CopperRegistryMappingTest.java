package com.kingodogo.buildscape.feature.copper;

import com.kingodogo.buildscape.registry.ModBlocks;
import com.kingodogo.buildscape.testutil.RegistryTestSupport;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Runs the real {@link CopperOxidationHandler} against the mod's copper blocks. The registry objects of the copper
 * blocks are bound to freshly built blocks, so the handler registers its chains exactly as in game and the test
 * reads the result through its public lookups instead of parsing source files.
 */
public class CopperRegistryMappingTest {
    private static final String[] AGES = {"", "exposed_", "weathered_", "oxidized_"};
    private static Map<String, Block> copperBlocks;

    @BeforeClass
    public static void bootstrap() {
        RegistryTestSupport.bootstrap();
        copperBlocks = RegistryTestSupport.bind(ModBlocks.BLOCKS, path -> path.contains("copper"));
    }

    @AfterClass
    public static void unbind() {
        RegistryTestSupport.unbind(ModBlocks.BLOCKS, path -> path.contains("copper"));
    }

    private static boolean isOutsideWeatheringChains(String id) {
        // Copper pillars are BuildScape pillars (registry entry, colours, displayed item); swapping their block
        // would run pillar removal, so they keep their stage like in 4.0.5.
        return id.startsWith("waxed_") || id.equals("copper_torch") || id.equals("copper_wall_torch")
                || id.endsWith("copper_pillar");
    }

    @Test
    public void everyRegisteredCopperStageResolvesInWeatheringTable() {
        assertTrue("registered copper blocks were bound", copperBlocks.size() > 100);
        CopperOxidationHandler.init();
        for (Map.Entry<String, Block> entry : copperBlocks.entrySet()) {
            String id = entry.getKey();
            BlockState state = entry.getValue().defaultBlockState();
            Optional<BlockState> next = CopperOxidationHandler.getNext(state);
            if (isOutsideWeatheringChains(id)) {
                assertTrue("waxed or non-weathering block: " + id, next.isEmpty());
                assertNull("waxed or non-weathering block has no age: " + id,
                        CopperOxidationHandler.getAge(entry.getValue()));
                continue;
            }
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
            assertSame("age of " + id, WeatheringCopper.WeatherState.values()[age],
                    CopperOxidationHandler.getAge(entry.getValue()));
            if (age == AGES.length - 1) {
                assertTrue("oxidized block has no next stage: " + id, next.isEmpty());
            } else {
                String nextId = prefix + AGES[age + 1] + base;
                assertTrue("missing copper family: " + id, next.isPresent());
                assertSame("next stage stays in its copper family: " + id,
                        copperBlocks.get(nextId), next.get().getBlock());
            }
        }
    }

    @Test
    public void mistAndRandomWeatheringShareTheNextStageLookup() {
        CopperOxidationHandler.init();
        int checked = 0;
        for (Block block : copperBlocks.values()) {
            BlockState state = block.defaultBlockState();
            Optional<BlockState> expected = CopperOxidationHandler.getNext(state);
            assertEquals(expected.orElse(null), CopperOxidationHandler.getNextOxidationState(state));
            if (block instanceof BuildscapeWeatheringCopper weathering) {
                assertEquals(expected, weathering.getNext(state));
                checked++;
            }
        }
        assertTrue("weathering copper blocks were checked", checked > 50);
    }
}
