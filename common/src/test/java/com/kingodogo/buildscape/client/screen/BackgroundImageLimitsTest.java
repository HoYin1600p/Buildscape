package com.kingodogo.buildscape.client.screen;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.DataOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("PMD.AvoidAccessibilityAlteration")
class BackgroundImageLimitsTest {
    @Test
    void rejectsEitherDimensionAboveTheLimitIncludingUnsignedPngValues() {
        assertFalse(BackgroundImageLimits.oversized(4096, 4096));
        assertTrue(BackgroundImageLimits.oversized(4097, 1));
        assertTrue(BackgroundImageLimits.oversized(1, 4097));
        assertTrue(BackgroundImageLimits.oversized(0xffffffffL, 1));
    }

    @Test
    void checksPngDimensionsBeforeDecoding(@TempDir Path directory) throws Exception {
        Path image = directory.resolve("large.png");
        try (var output = new DataOutputStream(Files.newOutputStream(image))) {
            output.writeLong(0x89504E470D0A1A0AL);
            output.writeInt(13);
            output.writeInt(0x49484452);
            output.writeInt(4097);
            output.writeInt(1);
        }
        var check = ConfigBackgrounds.class.getDeclaredMethod("isOversizedPng", Path.class);
        check.setAccessible(true);
        assertEquals(true, check.invoke(null, image));
    }
}
