package com.kingodogo.buildscape.adapter.v118x;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/** Headless regression checks following the project's standalone test-runner convention. */
public final class CopperRodLogicTest {
    private static int checks;

    private CopperRodLogicTest() {
    }

    public static void main(String[] args) throws Exception {
        net.minecraft.SharedConstants.tryDetectVersion();
        net.minecraft.server.Bootstrap.bootStrap();
        checkStages();
        checkToolTag();
        System.out.println("Copper rod logic tests passed: " + checks + " checks");
    }

    private static void checkStages() {
        // Plain identities exercise the production mapping helpers without loading Forge registries.
        Object fresh = new Object();
        Object exposed = new Object();
        Object weathered = new Object();
        Object oxidized = new Object();
        Object waxed = new Object();
        Object stone = new Object();
        Supplier<Object> b0 = () -> fresh;
        Supplier<Object> b1 = () -> exposed;
        Supplier<Object> b2 = () -> weathered;
        Supplier<Object> b3 = () -> oxidized;
        Map<Supplier<Object>, Supplier<Object>> next = new HashMap<>();
        Map<Supplier<Object>, Supplier<Object>> previous = new HashMap<>();
        RestoredCopperOxidation.registerChain(next, previous, b0, b1, b2, b3);

        Object[] stages = {fresh, exposed, weathered, oxidized};
        for (int i = 0; i < stages.length; i++) {
            check(RestoredCopperOxidation.isWeatheringStage(stages[i], next, previous),
                    "every unwaxed stage participates in lightning walks: " + i);
            check(RestoredCopperOxidation.getFirstStage(stages[i], next, previous) == fresh,
                    "every stage resolves to fresh copper, including fresh itself: " + i);
            check(RestoredCopperOxidation.getMappedStage(stages[i], previous)
                            == (i == 0 ? null : stages[i - 1]),
                    "one walk step removes exactly one stage: " + i);
            check(RestoredCopperOxidation.getMappedStage(stages[i], next)
                            == (i == stages.length - 1 ? null : stages[i + 1]),
                    "oxidation advances exactly one stage: " + i);
        }
        for (Object other : List.of(waxed, stone)) {
            check(!RestoredCopperOxidation.isWeatheringStage(other, next, previous),
                    "waxed and non-copper bases cannot start or continue lightning walks");
            check(RestoredCopperOxidation.getFirstStage(other, next, previous) == null,
                    "unmapped blocks have no first copper stage");
            check(RestoredCopperOxidation.getMappedStage(other, previous) == null,
                    "unmapped blocks cannot be scraped");
        }
        Object secondFresh = new Object();
        Object secondExposed = new Object();
        Object secondWeathered = new Object();
        Object secondOxidized = new Object();
        RestoredCopperOxidation.registerChain(next, previous, () -> secondFresh, () -> secondExposed,
                () -> secondWeathered, () -> secondOxidized);
        check(RestoredCopperOxidation.getFirstStage(secondOxidized, next, previous) == secondFresh,
                "independent copper families retain their own first stage");
        check(RestoredCopperOxidation.getFirstStage(oxidized, next, previous) == fresh,
                "adding another copper family preserves existing mappings");
    }

    private static void checkToolTag() throws Exception {
        Path path = Path.of(System.getProperty("buildscape.mainResources"), "data/minecraft/tags/blocks/needs_stone_tool.json");
        JsonObject tag;
        try (Reader reader = Files.newBufferedReader(path)) {
            tag = JsonParser.parseReader(reader).getAsJsonObject();
        }
        check(!tag.get("replace").getAsBoolean(), "tool tag retains vanilla entries");
        Set<String> values = new HashSet<>();
        for (JsonElement value : tag.getAsJsonArray("values")) {
            values.add(value.getAsString());
        }
        List<String> rodIds = List.of("copper_rod", "exposed_copper_rod", "weathered_copper_rod",
                "oxidized_copper_rod", "waxed_copper_rod", "waxed_exposed_copper_rod",
                "waxed_weathered_copper_rod", "waxed_oxidized_copper_rod");
        for (String rodId : rodIds) {
            check(values.contains("buildscape:" + rodId), "stone tool requirement for " + rodId);
        }
        check(values.stream().filter(value -> value.startsWith("buildscape:") && value.endsWith("copper_rod"))
                        .count() == 8,
                "tool tag includes exactly the eight copper rod variants");
    }

    private static void check(boolean condition, String description) {
        if (!condition) {
            throw new AssertionError(description);
        }
        checks++;
    }
}
