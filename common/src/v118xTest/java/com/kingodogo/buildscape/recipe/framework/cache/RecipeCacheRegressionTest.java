package com.kingodogo.buildscape.recipe.framework.cache;

import com.kingodogo.buildscape.recipe.framework.compiler.FamilyExpander;
import com.kingodogo.buildscape.recipe.framework.compiler.RecipeCacheGenerator;
import com.kingodogo.buildscape.recipe.framework.compiler.TemplateEngine;
import com.kingodogo.buildscape.recipe.framework.parser.RecipeIR;
import com.kingodogo.buildscape.recipe.framework.util.RecipePackFormatter;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;

import com.kingodogo.buildscape.recipe.framework.validation.RecipeValidator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class RecipeCacheRegressionTest {
    @BeforeAll
    public static void bootstrap() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    public void hashIgnoresCrLfAndMatchesRuntime() {
        String[] categories = {"crafting"};
        Map<String, byte[]> lf = Map.of("crafting", "{\n}\n".getBytes(StandardCharsets.UTF_8));
        Map<String, byte[]> crlf = Map.of("crafting", "{\r\n}\r\n".getBytes(StandardCharsets.UTF_8));
        String expected = RecipeCacheGenerator.computeSourceHash(categories, lf);
        assertEquals(expected, RecipeCacheGenerator.computeSourceHash(categories, crlf));
        assertEquals(expected, BinaryRecipeCache.computeSourceHash(categories, crlf));
        assertNotEquals(expected, RecipeCacheGenerator.computeSourceHash(categories,
                Map.of("crafting", "{\n }\n".getBytes(StandardCharsets.UTF_8))));
    }

    @Test
    public void duplicateKeysAreRejectedAtEveryDepth() {
        for (String source : List.of("{\"recipes\":[],\"recipes\":[]}",
                "{\"recipes\":[{\"result\":{\"item\":\"a\",\"item\":\"b\"}}]}")) {
            IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                    () -> RecipePackFormatter.format(source));
            assertTrue(error.getMessage().contains("Duplicate JSON key"));
        }
        assertEquals("{\n  \"recipes\": [\n  ]\n}\n", RecipePackFormatter.format("{\"recipes\":[]}"));
    }

    @Test
    public void duplicateIdsProduceLoudWarning() throws Exception {
        Path directory = Files.createTempDirectory("recipe-duplicate-test");
        PrintStream original = System.err;
        ByteArrayOutputStream warnings = new ByteArrayOutputStream();
        try {
            Files.writeString(directory.resolve("crafting.json"), "{\"recipes\":["
                    + "{\"id\":\"same\",\"type\":\"stonecutting\",\"input\":\"minecraft:stone\","
                    + "\"result\":{\"item\":\"minecraft:stone\"}},"
                    + "{\"id\":\"same\",\"type\":\"stonecutting\",\"input\":\"minecraft:stone\","
                    + "\"result\":{\"item\":\"minecraft:stone\"}}]}");
            System.setErr(new PrintStream(warnings, true, StandardCharsets.UTF_8));
            RecipeCacheGenerator.generateCache(directory);
            assertTrue(warnings.toString(StandardCharsets.UTF_8).contains("WARNING: Duplicate recipe ID"));
        } finally {
            System.setErr(original);
            Files.deleteIfExists(directory.resolve("crafting.json"));
            Files.deleteIfExists(directory);
        }
    }

    @Test
    public void formatCheckRejectsCrLfWithoutWriting() throws Exception {
        Path source = Files.createTempFile("recipe-format-test", ".json");
        try {
            String canonical = RecipePackFormatter.format("{\"recipes\":[]}");
            Files.writeString(source, canonical);
            RecipePackFormatter.checkFile(source);
            String crlf = canonical.replace("\n", "\r\n");
            Files.writeString(source, crlf);
            assertThrows(IllegalStateException.class, () -> RecipePackFormatter.checkFile(source));
            assertEquals(crlf, Files.readString(source));
        } finally {
            Files.deleteIfExists(source);
        }
    }

    @Test
    public void verificationRejectsTamperedPayloadWithValidHeader() throws Exception {
        Path directory = Files.createTempDirectory("recipe-verify-test");
        Path cache = directory.resolve("recipes.bscb");
        try {
            Files.writeString(directory.resolve("crafting.json"), "{\"recipes\":[]}");
            byte[] bytes = RecipeCacheGenerator.generateCache(directory);
            String[] args = {directory.toString(), cache.toString()};
            Files.write(cache, bytes);
            com.kingodogo.buildscape.recipe.framework.compiler.BundledRecipeCacheVerify.main(args);
            bytes[bytes.length - 1] ^= 1;
            Files.write(cache, bytes);
            assertThrows(IllegalStateException.class,
                    () -> com.kingodogo.buildscape.recipe.framework.compiler.BundledRecipeCacheVerify.main(args));
        } finally {
            Files.deleteIfExists(cache);
            Files.deleteIfExists(directory.resolve("crafting.json"));
            Files.deleteIfExists(directory);
        }
    }

    @Test
    public void defaultWoodTargetsExist() {
        FamilyExpander expander = new FamilyExpander(new TemplateEngine());
        for (String wood : List.of("ashpen_black", "cherry", "mangrove", "pale_oak", "poplar")) {
            List<RecipeIR.RecipeSpec> recipes = expander.expandFamily(new RecipeIR.FamilySpec(
                    "wood", "BS:" + wood + "_planks", "", List.of(), List.of(), false));
            assertEquals(wood.startsWith("ashpen") ? 6 : 8, recipes.size());
            assertTrue(recipes.stream().noneMatch(r -> r.result().item().endsWith("_stonecutter")
                    || (r.result().item().endsWith("_gate") && !r.result().item().endsWith("_fence_gate"))));
        }
    }

    @Test
    public void invalidRecordsAreDroppedWithoutLosingFollowingRecipe() throws Exception {
        // Validate shared records with known items, without a loader registration lifecycle.
        String[] pool = {"buildscape:test", "", "minecraft:stone", "{}", "not JSON", "minecraft:air",
                "{\"item\":\"minecraft:stone\"}", "{\"tag\":\"forge:stone\"}"};
        RecipeValidator validator = new RecipeValidator(id -> id.toString().equals("minecraft:stone"));
        for (int type : List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)) {
            for (int invalid : List.of(3, 4)) {
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                try (DataOutputStream out = new DataOutputStream(bytes)) {
                    writeRecord(out, type, 2, invalid);
                    writeRecord(out, 3, 2, 6);
                }
                try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
                    assertNull(BinaryRecipeCache.readRecipe(in, pool, validator));
                    assertNotNull(BinaryRecipeCache.readRecipe(in, pool, validator));
                    assertEquals(0, in.available());
                }
            }
        }
        for (int type : List.of(1, 3, 9)) {
            assertNull(readSingle(pool, validator, type, 5, 6));
        }
        assertNull(readSingle(pool, validator, 1, 2, 1));
        assertNull(readSingle(pool, validator, 9, 2, 1));
        // Unbound tags are syntactically valid; they must survive pre-tag-binding loads.
        assertNotNull(readSingle(pool, validator, 3, 2, 7));
        assertNotNull(readSingle(pool, validator, 1, 2, 7));
    }

    private static RecipeIR.CompiledRecipe readSingle(String[] pool, RecipeValidator validator,
            int type, int result, int ingredient) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (DataOutputStream out = new DataOutputStream(bytes)) {
            writeRecord(out, type, result, ingredient);
        }
        try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            return BinaryRecipeCache.readRecipe(in, pool, validator);
        }
    }

    private static void writeRecord(DataOutputStream out, int type, int result, int ingredient) throws Exception {
        for (int value : new int[]{0, 1, result, 1, 1}) out.writeInt(value);
        out.writeByte(type);
        if (type == 1 || type == 9) {
            out.writeInt(1);
            out.writeInt(1);
            out.writeInt(1);
        } else if (type == 2 || type == 10) {
            out.writeInt(1);
        }
        out.writeInt(ingredient);
        if (type == 8) out.writeInt(6);
        if (type >= 4 && type <= 7) {
            out.writeFloat(0.1f);
            out.writeInt(200);
        }
    }
}
