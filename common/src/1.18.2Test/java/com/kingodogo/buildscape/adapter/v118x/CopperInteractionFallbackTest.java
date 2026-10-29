package com.kingodogo.buildscape.adapter.v118x;

import net.minecraft.world.InteractionResult;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** On 1.18.2 the shared copper table (bit copper among it) answers whatever the restored table passes on. */
class CopperInteractionFallbackTest {
    @Test
    void sharedHandlerRunsOnlyWhenTheRestoredOnePasses() {
        AtomicInteger sharedCalls = new AtomicInteger();
        assertEquals(InteractionResult.SUCCESS, RestoredCopperOxidation.firstHandled(
                () -> InteractionResult.PASS, () -> {
                    sharedCalls.incrementAndGet();
                    return InteractionResult.SUCCESS;
                }));
        assertEquals(1, sharedCalls.get());

        assertEquals(InteractionResult.CONSUME, RestoredCopperOxidation.firstHandled(
                () -> InteractionResult.CONSUME, () -> {
                    sharedCalls.incrementAndGet();
                    return InteractionResult.SUCCESS;
                }));
        assertEquals(1, sharedCalls.get(), "a handled restored interaction must not run twice");
        assertEquals(InteractionResult.PASS, RestoredCopperOxidation.firstHandled(
                () -> InteractionResult.PASS, () -> InteractionResult.PASS));
    }

    @Test
    void bothLoadersRouteCopperClicksThroughTheFallback() throws Exception {
        Path adapter = Path.of(System.getProperty("buildscape.v118xMixinConfig",
                        "src/1.18.2/resources/buildscape.mixins.json"))
                .toAbsolutePath().getParent().getParent().getParent()
                .resolve("1.18.2/java/com/kingodogo/buildscape/adapter/v118x/PlatformAdapterBase.java");
        String source = Files.readString(adapter);
        int method = source.indexOf("handleCopperRightClick(");
        String body = source.substring(method, source.indexOf("    @Override", method));
        assertTrue(body.contains("RestoredCopperOxidation.firstHandled("), "restored handler first");
        assertTrue(body.contains("CopperOxidationHandler.handleRightClick("), "shared handler as fallback");
    }
}
