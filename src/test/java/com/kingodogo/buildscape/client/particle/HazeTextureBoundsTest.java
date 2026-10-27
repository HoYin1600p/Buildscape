package com.kingodogo.buildscape.client.particle;

import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class HazeTextureBoundsTest {
    @Test
    public void bundledTextureTrimsOnlyTransparentFilteringFootprints() throws IOException {
        try (InputStream stream = getClass().getResourceAsStream("/assets/buildscape/textures/particle/haze.png")) {
            BufferedImage image = ImageIO.read(stream);
            HazeTextureBounds bounds = HazeTextureBounds.compute(image.getWidth(), image.getHeight(),
                    index -> (image.getRGB(index % image.getWidth(), index / image.getWidth()) >>> 24) == 0);
            assertEquals(7 / 64F, bounds.minU(), 0);
            assertEquals(57 / 64F, bounds.maxU(), 0);
            assertEquals(0, bounds.minV(), 0);
            assertEquals(1, bounds.maxV(), 0);
            // Bilinear sampling can reach half a texel beyond the geometry boundary.
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    if ((x + 0.5F) / image.getWidth() <= bounds.minU() + 0.5F / image.getWidth()
                            || (x + 0.5F) / image.getWidth() >= bounds.maxU() - 0.5F / image.getWidth()) {
                        assertEquals(0, image.getRGB(x, y) >>> 24);
                    }
                }
            }
        }
    }

    @Test
    public void alphaIsTheHighestAcrossFrames() {
        int[] alpha = HazeTextureBounds.readAlpha(2, 1, new int[]{0, 1},
                (frame, x, y) -> (frame == x ? 0xC0 : 0x10) << 24);
        assertArrayEquals(new int[]{0xC0, 0xC0}, alpha);
    }

    @Test
    public void imageFreedByResourceReloadIsNotRead() {
        assertNull(HazeTextureBounds.readAlpha(4, 4, new int[]{0}, (frame, x, y) -> {
            throw new IllegalStateException("Image is not allocated.");
        }));
    }

    @Test
    public void replacementTexturesKeepTheirOwnCoverage() {
        assertEquals(HazeTextureBounds.FULL, HazeTextureBounds.compute(16, 16, index -> false));
        assertEquals(HazeTextureBounds.EMPTY, HazeTextureBounds.compute(16, 16, index -> true));
        HazeTextureBounds bounds = HazeTextureBounds.compute(16, 16, index -> index != 8 * 16 + 8);
        assertEquals(new HazeTextureBounds(7 / 16F, 10 / 16F, 7 / 16F, 10 / 16F), bounds);
        // Animated coverage is the union of all frames, including content touching opposite edges.
        assertEquals(HazeTextureBounds.FULL,
                HazeTextureBounds.compute(16, 16, index -> index != 0 && index != 255));
    }

    @Test
    public void croppingReducesPixelCoverageWithoutStretchingTextureAndInvalidatesCache() {
        HazeGeometry geometry = new HazeGeometry(List.of(new HazeMask.Rectangle(0, 0, 32, 32)), -8, 0.5F);
        geometry.update(0, 0, 2, 0);
        HazeTextureBounds bounds = new HazeTextureBounds(7 / 64F, 57 / 64F, 0, 1);
        geometry.setTextureBounds(bounds);
        assertTrue(geometry.update(0, 0, 2, 0));
        assertEquals(8, geometry.vertexCount());
        float[] vertices = geometry.vertices();
        assertEquals(-1.5625F, vertices[0], 0);
        assertEquals(1.5625F, vertices[4], 0);
        assertEquals(bounds.minU(), vertices[2], 0);
        assertEquals(bounds.maxU(), vertices[6], 0);
        assertEquals(0.78125F, (vertices[4] - vertices[0]) / 4, 0);
        geometry.setTextureBounds(bounds);
        assertFalse(geometry.update(0, 0, 2, 0));
        geometry.setTextureBounds(HazeTextureBounds.FULL);
        assertTrue(geometry.update(0, 0, 2, 0));
        assertEquals(-2, geometry.vertices()[0], 0);
    }
}
