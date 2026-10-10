package com.kingodogo.buildscape.client;

import com.google.gson.JsonParser;
import com.kingodogo.buildscape.adapter.v26x.client.ConfettiParticle;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.RandomSource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConfettiAppearanceTest {
    @Test void aBurstCanSelectEveryPackagedShapeWithoutAgeBasedSelection() throws Exception {
        Path assets = Path.of(System.getProperty("buildscape.mainResources")).resolve("assets/buildscape");
        var textures = JsonParser.parseString(Files.readString(assets.resolve("particles/confetti.json")))
                .getAsJsonObject().getAsJsonArray("textures");
        assertEquals(7, textures.size());
        for (var texture : textures) {
            String id = texture.getAsString();
            assertTrue(id.startsWith("buildscape:"));
            assertTrue(Files.isRegularFile(assets.resolve("textures/particle/"
                    + id.substring("buildscape:".length()) + ".png")), id);
        }

        RandomSource burstRandom = RandomSource.create(12345L);
        Set<Integer> shapes = new HashSet<>();
        SpriteSet sprites = new SpriteSet() {
            @Override public TextureAtlasSprite get(RandomSource random) {
                assertSame(burstRandom, random);
                shapes.add(random.nextInt(textures.size()));
                return null; // No atlas or graphics context is needed to observe the selection.
            }
            @Override public TextureAtlasSprite get(int age, int lifetime) {
                throw new AssertionError("Confetti must not choose a shape by age");
            }
            @Override public TextureAtlasSprite first() {
                throw new AssertionError("Confetti must not always choose the first shape");
            }
        };
        for (int particle = 0; particle < 75; particle++) {
            ConfettiParticle.pickSprite(sprites, burstRandom);
        }
        assertEquals(Set.of(0, 1, 2, 3, 4, 5, 6), shapes);
    }

    @Test void aBurstCanSelectAllElevenReferenceColours() {
        RandomSource random = RandomSource.create(54321L);
        Set<Integer> colors = new HashSet<>();
        for (int particle = 0; particle < 120; particle++) {
            colors.add(ConfettiParticle.pickColor(random));
        }
        assertEquals(Set.of(0xFF0000, 0x00FFFF, 0x1919EA, 0x3CDFFF, 0xFFFF00,
                0xFF5C00, 0xBFFE00, 0x39FF14, 0xF686B7, 0xAB87FF, 0xFF00FF), colors);
    }
}
