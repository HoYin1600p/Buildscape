package com.kingodogo.buildscape.client.particle;

import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class HazeAlphaCoverageTest {
    @Test
    public void cutoffNeverRejectsAnAlphaThatCanPassTheStandardParticleProgram() {
        for (int step = 1; step <= 1024; step++) {
            float alpha = step / 1024F;
            int cutoff = HazeShaderSupport.cutoffForAlpha(alpha);
            float vertexAlpha = (int) (alpha * 255) / 255F;
            for (int textureAlpha = 0; textureAlpha <= 255; textureAlpha++) {
                if (vertexAlpha * (textureAlpha / 255F) >= 0.1F) {
                    assertTrue(textureAlpha > cutoff);
                }
            }
            // A filtered sample outside the padded support is bounded by the cutoff.
            assertTrue(alpha * (cutoff / 255F) < 0.1F);
        }
    }

    @Test
    public void bundledTextureCoverageIncludesEveryVisibleTexelThroughoutFade() throws IOException {
        try (InputStream stream = getClass().getResourceAsStream("/assets/buildscape/textures/particle/haze.png")) {
            BufferedImage image = ImageIO.read(stream);
            int width = image.getWidth();
            int height = image.getHeight();
            for (int step = 1; step <= 44; step++) {
                float alpha = step / 100F;
                int cutoff = HazeShaderSupport.cutoffForAlpha(alpha);
                HazeTextureBounds bounds = HazeTextureBounds.compute(width, height,
                        index -> (image.getRGB(index % width, index / width) >>> 24) <= cutoff);
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int textureAlpha = image.getRGB(x, y) >>> 24;
                        if (textureAlpha / 255F * alpha >= 0.1F) {
                            assertFalse(bounds.isEmpty());
                            assertTrue((x + 0.5F) / width > bounds.minU());
                            assertTrue((x + 0.5F) / width < bounds.maxU());
                            assertTrue((y + 0.5F) / height > bounds.minV());
                            assertTrue((y + 0.5F) / height < bounds.maxV());
                        }
                    }
                }
                if (alpha == 0.44F) {
                    assertTrue((bounds.maxU() - bounds.minU()) * (bounds.maxV() - bounds.minV()) < 0.35F);
                }
            }
            HazeTextureBounds invisible = HazeTextureBounds.compute(width, height,
                    index -> (image.getRGB(index % width, index / width) >>> 24) <= HazeShaderSupport
                            .cutoffForAlpha(0.1F));
            assertTrue(invisible.isEmpty());
        }
    }
}
