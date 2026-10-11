package com.kingodogo.buildscape.adapter.v118x;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NullEntityCollisionContextTest {
    private static final Pattern CLIP = Pattern.compile(
            "\\bnew\\s+(?:net\\.minecraft\\.world\\.level\\.)?ClipContext\\s*\\(");
    private static final Pattern COLLISION = Pattern.compile(
            "\\bCollisionContext\\s*\\.\\s*of\\s*\\(\\s*null\\s*\\)");
    private static final Pattern NON_CODE = Pattern.compile(
            "//[^\\r\\n]*|/\\*.*?\\*/|\"(?:\\\\.|[^\"\\\\])*\"|'(?:\\\\.|[^'\\\\])*'", Pattern.DOTALL);

    private static boolean hasNullEntityContext(String source) {
        String code = NON_CODE.matcher(source).replaceAll(" ");
        if (COLLISION.matcher(code).find()) return true;
        var calls = CLIP.matcher(code);
        while (calls.find()) {
            int depth = 0;
            int argument = 0;
            int start = calls.end();
            for (int i = start; i < code.length(); i++) {
                char c = code.charAt(i);
                if (c == ')' && depth == 0) {
                    if (argument == 4 && code.substring(start, i).trim().equals("null")) return true;
                    break;
                }
                if (c == ',' && depth == 0) {
                    argument++;
                    start = i + 1;
                } else if (c == '(' || c == '[' || c == '{') {
                    depth++;
                } else if (c == ')' || c == ']' || c == '}') {
                    depth--;
                }
            }
        }
        return false;
    }

    @Test
    void production1182SourcesNeverPassLiteralNullToEntityContexts() throws IOException {
        Path common = RemapSafeReflectionTest.commonRoot();
        List<String> failures = new ArrayList<>();
        for (String root : List.of("src/main/java/com/kingodogo/buildscape/adapter/v118x", "src/v118x")) {
            Path directory = common.resolve(root);
            assertTrue(Files.isDirectory(directory), "Missing scan root: " + directory);
            try (var paths = Files.walk(directory)) {
                for (Path path : paths.filter(p -> p.toString().endsWith(".java")).toList()) {
                    if (hasNullEntityContext(Files.readString(path))) {
                        failures.add(common.relativize(path).toString());
                    }
                }
            }
        }
        assertTrue(failures.isEmpty(), "Null entity contexts crash on vanilla/Fabric 1.18.2:\n"
                + String.join("\n", failures));
    }

    @Test
    void detectorRecognizesNestedArgumentsQualifiedNamesAndWhitespace() {
        assertTrue(hasNullEntityContext("new ClipContext(a, b, Block.COLLIDER, Fluid.NONE, null)"));
        assertTrue(hasNullEntityContext("new net.minecraft.world.level.ClipContext(\n"
                + "new Vec3(1, 2, 3), position.offset(0, 1, 0), Block.COLLIDER, Fluid.NONE, /* no entity */ null\n)"));
        assertTrue(hasNullEntityContext("CollisionContext.of(null)"));
        assertTrue(hasNullEntityContext("net.minecraft.world.phys.shapes.CollisionContext . of (\nnull )"));
        assertFalse(hasNullEntityContext("new ClipContext(a, b, Block.COLLIDER, Fluid.NONE, player)"));
        assertFalse(hasNullEntityContext("new ClipContext(a, b, Block.COLLIDER, Fluid.NONE, CollisionContext.empty())"));
        assertFalse(hasNullEntityContext("CollisionContext.of(player)"));
        assertFalse(hasNullEntityContext("// CollisionContext.of(null)\nCollisionContext.empty()"));
        assertFalse(hasNullEntityContext("String example = \"CollisionContext.of(null)\";"));
    }
}
