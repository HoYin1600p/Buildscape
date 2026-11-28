package com.kingodogo.buildscape.event;

import com.kingodogo.buildscape.TestBootstrap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("PMD.AvoidAccessibilityAlteration")
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
    void logoutClearsBothCrawlModesOnBothSidesWithoutChangingOtherPlayers() throws Exception {
        UUID departing = UUID.randomUUID();
        UUID remaining = UUID.randomUUID();
        for (String mode : new String[]{"FORCED_PLAYERS", "CUSTOM_CRAWLING_PLAYERS"}) {
            for (boolean clientSide : new boolean[]{false, true}) {
                players(mode).add(new HollowLogCrawlHandler.PlayerSide(departing, clientSide));
                players(mode).add(new HollowLogCrawlHandler.PlayerSide(remaining, clientSide));
            }
        }

        HollowLogCrawlHandler.onPlayerLoggedOut(departing);

        for (String mode : new String[]{"FORCED_PLAYERS", "CUSTOM_CRAWLING_PLAYERS"}) {
            for (boolean clientSide : new boolean[]{false, true}) {
                assertFalse(players(mode).contains(new HollowLogCrawlHandler.PlayerSide(departing, clientSide)));
                assertTrue(players(mode).contains(new HollowLogCrawlHandler.PlayerSide(remaining, clientSide)));
            }
        }
    }

    @Test
    void theClientAndServerCopiesOfOnePlayerAreTrackedSeparately() throws Exception {
        UUID id = UUID.randomUUID();
        Set<HollowLogCrawlHandler.PlayerSide> crawling = players("CUSTOM_CRAWLING_PLAYERS");
        crawling.add(new HollowLogCrawlHandler.PlayerSide(id, true));
        assertTrue(crawling.contains(new HollowLogCrawlHandler.PlayerSide(id, true)));
        assertFalse(crawling.contains(new HollowLogCrawlHandler.PlayerSide(id, false)));
    }

    @Test
    void serverStopClearsBothCrawlModesBeforeTheNextSession() throws Exception {
        players("FORCED_PLAYERS").add(new HollowLogCrawlHandler.PlayerSide(UUID.randomUUID(), false));
        players("CUSTOM_CRAWLING_PLAYERS").add(new HollowLogCrawlHandler.PlayerSide(UUID.randomUUID(), true));

        HollowLogCrawlHandler.onServerStopping();

        assertTrue(players("FORCED_PLAYERS").isEmpty());
        assertTrue(players("CUSTOM_CRAWLING_PLAYERS").isEmpty());
    }

    @SuppressWarnings("unchecked")
    private static Set<HollowLogCrawlHandler.PlayerSide> players(String mode) throws Exception {
        var field = HollowLogCrawlHandler.class.getDeclaredField(mode);
        field.setAccessible(true);
        return (Set<HollowLogCrawlHandler.PlayerSide>) field.get(null);
    }
}
