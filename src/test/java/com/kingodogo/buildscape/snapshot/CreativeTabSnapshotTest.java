package com.kingodogo.buildscape.snapshot;

import com.kingodogo.buildscape.item.ModItems;
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
 * and the tab sorts live ItemStacks). This snapshot therefore records the order of the hard-coded
 * {@code items.add(new ItemStack(ModItems.X.get()))} list in ModCreativeModeTab, resolved to registry names, before
 * the tab's own sorting. The class is located by file name, so moving it between packages does not break the test.
 */
public class CreativeTabSnapshotTest {
    private static final Pattern ADD = Pattern.compile("new ItemStack\\(\\s*ModItems\\.(\\w+)\\.get\\(\\)");

    @BeforeClass
    public static void bootstrap() {
        RegistryTestSupport.bootstrap();
    }

    private static Path findSource(String fileName) throws IOException {
        try (Stream<Path> files = Files.walk(Path.of("src", "main", "java"))) {
            return files.filter(path -> path.getFileName().toString().equals(fileName)).findFirst().orElseThrow();
        }
    }

    @Test
    public void hardCodedCreativeTabOrder() throws IOException {
        String source = Files.readString(findSource("ModCreativeModeTab.java"), StandardCharsets.UTF_8);
        Map<String, String> paths = RegistryTestSupport.pathsByField(ModItems.class);
        List<String> lines = new ArrayList<>();
        Matcher matcher = ADD.matcher(source);
        while (matcher.find()) {
            String path = paths.get(matcher.group(1));
            lines.add(path != null ? path : "UNRESOLVED:" + matcher.group(1));
        }
        assertFalse(lines.isEmpty());
        SnapshotSupport.assertSnapshot("creative-tab-hardcoded-order.txt", lines);
    }
}
