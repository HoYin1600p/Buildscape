package com.kingodogo.buildscape.registry;

import com.kingodogo.buildscape.adapter.v118x.RegistrationOrder118x;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class RegistrationOrder118xTest {
    @Test
    void orderMatchesReferenceModItemsDeclarations() throws IOException {
        Path relative = Path.of("ref/forge/1.18.2/src/main/java/com/kingodogo/buildscape/item/ModItems.java");
        Path root = Path.of("").toAbsolutePath();
        while (root != null && !Files.isRegularFile(root.resolve(relative))) root = root.getParent();
        assertNotNull(root, "Cannot locate reference ModItems.java from the test working directory");
        String source = Files.readString(root.resolve(relative));
        // Ignore commented declarations; whitespace includes multiline register calls.
        source = source.replaceAll("(?s)/\\*.*?\\*/", "").replaceAll("(?m)//[^\\r\\n]*", "");
        var matcher = Pattern.compile("\\bITEMS\\.register\\s*\\(\\s*\"([a-z0-9_]+)\"").matcher(source);
        List<String> reference = new ArrayList<>();
        while (matcher.find()) reference.add(matcher.group(1));
        assertFalse(reference.isEmpty(), "No reference item registrations found");
        assertEquals(reference, RegistrationOrder118x.itemIds());
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
