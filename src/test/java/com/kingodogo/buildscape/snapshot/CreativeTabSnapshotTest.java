package com.kingodogo.buildscape.snapshot;

import com.kingodogo.buildscape.registry.ModItems;
import com.kingodogo.buildscape.testutil.RegistryTestSupport;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.Assert.assertFalse;

/**
 * The creative tab fills itself from registered items, which a unit test cannot create (the item registry is frozen
 * and the tab sorts live ItemStacks). This snapshot therefore records the order of the hard-coded tab list, resolved
 * to registry names, before the tab's own sorting. The list lives in the
 * {@code TabOrder1..3} data classes as {@code ModItems.X,} entries; the icon in ModCreativeModeTab and the entries
 * between the firework blocks are read from their own files in tab order. Classes are located by file name, so moving
 * them between packages does not break the test.
 */
public class CreativeTabSnapshotTest {
    private static final Pattern ADD = Pattern.compile("new ItemStack\\(\\s*ModItems\\.(\\w+)\\.get\\(\\)");
    private static final Pattern ENTRY = Pattern.compile("^\\s*ModItems\\.(\\w+),?\\s*$", Pattern.MULTILINE);

    @BeforeClass
    public static void bootstrap() {
        RegistryTestSupport.bootstrap();
    }

    private static Path findSource(String fileName) throws IOException {
        try (Stream<Path> files = Files.walk(Path.of("src", "main", "java"))) {
            return files.filter(path -> path.getFileName().toString().equals(fileName)).findFirst().orElseThrow();
        }
    }

    private static void collect(Pattern pattern, Path file, Map<String, String> paths, List<String> lines) throws IOException {
        Matcher matcher = pattern.matcher(Files.readString(file, StandardCharsets.UTF_8));
        while (matcher.find()) {
            String path = paths.get(matcher.group(1));
            lines.add(path != null ? path : "UNRESOLVED:" + matcher.group(1));
        }
    }

    @Test
    public void hardCodedCreativeTabOrder() throws IOException {
        Map<String, String> paths = RegistryTestSupport.pathsByField(ModItems.class);
        List<String> lines = new ArrayList<>();
        collect(ADD, findSource("ModCreativeModeTab.java"), paths, lines);
        for (String order : List.of("TabOrder1.java", "TabOrder2.java", "TabOrder3.java")) {
            collect(ENTRY, findSource(order), paths, lines);
        }
        assertFalse(lines.isEmpty());
        SnapshotSupport.assertSnapshot("creative-tab-hardcoded-order.txt", lines);
    }
}
