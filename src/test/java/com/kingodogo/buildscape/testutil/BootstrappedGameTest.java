package com.kingodogo.buildscape.testutil;

import org.junit.BeforeClass;

/**
 * Base class for tests that need the vanilla game bootstrapped (registries, tags, event listener lists). JUnit runs
 * this class's {@code @BeforeClass} method before the subclass's own, so a test only has to extend it.
 */
public abstract class BootstrappedGameTest {
    @BeforeClass
    public static void bootstrap() {
        RegistryTestSupport.bootstrap();
    }
}
