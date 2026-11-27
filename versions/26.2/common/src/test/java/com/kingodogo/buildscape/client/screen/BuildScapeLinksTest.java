package com.kingodogo.buildscape.client.screen;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BuildScapeLinksTest {
    @Test
    void linksUseUniqueSecureDestinations() {
        List<BuildScapeConfigScreen.ConfigLink> links = BuildScapeConfigScreen.getConfigLinks();
        Set<String> destinations = new HashSet<>();

        assertEquals(5, links.size());
        for (BuildScapeConfigScreen.ConfigLink link : links) {
            assertTrue(link.url().startsWith("https://"));
            assertTrue(destinations.add(link.url()));
        }
        assertTrue(destinations.contains("https://buildscape.online/"));
        assertTrue(destinations.contains("https://modrinth.com/project/9qweL5rw"));
    }
}
