package com.kingodogo.buildscape.event;

import com.kingodogo.buildscape.TestBootstrap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HollowLogCrawlCleanupTest {
    @BeforeAll
    static void bootstrap() {
        TestBootstrap.initialize();
    }

    @AfterEach
    void clearSession() {
        HollowLogCrawlHandler.onServerStopping();
    }

    @Test
    void logoutClearsBothCrawlModesWithoutChangingOtherPlayers() throws Exception {
        UUID departing = UUID.randomUUID();
        UUID remaining = UUID.randomUUID();
        for (String mode : new String[]{"FORCED_PLAYERS", "CUSTOM_CRAWLING_PLAYERS"}) {
            players(mode).add(departing);
            players(mode).add(remaining);
        }

        HollowLogCrawlHandler.onPlayerLoggedOut(departing);

        for (String mode : new String[]{"FORCED_PLAYERS", "CUSTOM_CRAWLING_PLAYERS"}) {
            assertFalse(players(mode).contains(departing));
            assertTrue(players(mode).contains(remaining));
        }
    }

    @Test
    void serverStopClearsBothCrawlModesBeforeTheNextSession() throws Exception {
        players("FORCED_PLAYERS").add(UUID.randomUUID());
        players("CUSTOM_CRAWLING_PLAYERS").add(UUID.randomUUID());

        HollowLogCrawlHandler.onServerStopping();

        assertTrue(players("FORCED_PLAYERS").isEmpty());
        assertTrue(players("CUSTOM_CRAWLING_PLAYERS").isEmpty());
    }

    @SuppressWarnings("unchecked")
    private static Set<UUID> players(String mode) throws Exception {
        var field = HollowLogCrawlHandler.class.getDeclaredField(mode);
        field.setAccessible(true);
        return (Set<UUID>) field.get(null);
    }
}
