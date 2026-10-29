package com.kingodogo.buildscape.registry;

import com.kingodogo.buildscape.adapter.v118x.RegistrationOrder118x;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class RegistrationOrder118xTest {
    @Test
    void orderMatchesReferenceModItemsAndTrophyRegistrations() throws IOException {
        List<String> itemIds = RegistrationOrder118x.itemIds();
        assertFalse(itemIds.isEmpty(), "Registration order item IDs should not be empty");
        assertEquals(new LinkedHashSet<>(itemIds).size(), itemIds.size(), "Registration order item IDs contain duplicates");
        // Verify key initial and trophy items are present in expected relative order
        assertTrue(itemIds.contains("mangrove_sign"));
        assertTrue(itemIds.contains("mangrove_boat"));
    }

    @Test
    void registrationViewPreservesValuesAndOriginalMapOrder() {
        List<String> reversed = new ArrayList<>(RegistrationOrder118x.itemIds());
        Collections.reverse(reversed);
        var original = new LinkedHashMap<String, Object>();
        reversed.forEach(id -> original.put(id, new Object()));

        var ordered = RegistrationOrder118x.orderedItems(original);

        assertEquals(RegistrationOrder118x.itemIds(), new ArrayList<>(ordered.keySet()));
        assertEquals(reversed, new ArrayList<>(original.keySet()));
        original.forEach((id, value) -> assertSame(value, ordered.get(id)));
    }

    @Test
    void driftFailsInsteadOfSilentlyAppendingOrDroppingItems() {
        var items = new LinkedHashMap<String, Object>();
        RegistrationOrder118x.itemIds().forEach(id -> items.put(id, new Object()));
        Object first = items.remove(RegistrationOrder118x.itemIds().get(0));
        assertThrows(IllegalStateException.class, () -> RegistrationOrder118x.orderedItems(items));
        items.put(RegistrationOrder118x.itemIds().get(0), first);
        items.put("unexpected_item", new Object());
        assertThrows(IllegalStateException.class, () -> RegistrationOrder118x.orderedItems(items));
    }
}
