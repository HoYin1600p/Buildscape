package com.kingodogo.buildscape.client.screen;

import com.kingodogo.buildscape.config.ConfigSetting;
import com.kingodogo.buildscape.config.PillarParticleConfig;
import com.kingodogo.buildscape.recipe.framework.cache.RecipeCacheRegressionTest;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class UploadBatchingTest {
    @BeforeClass
    public static void bootstrap() {
        RecipeCacheRegressionTest.bootstrap();
    }

    private static PillarParticleConfig copyOf(PillarParticleConfig source) {
        PillarParticleConfig copy = new PillarParticleConfig();
        copy.particle_speed = source.particle_speed;
        copy.particle_lifetime = source.particle_lifetime;
        copy.pattern = source.pattern;
        copy.particle_color = new ArrayList<>(source.particle_color);
        copy.max_particle_color = source.max_particle_color;
        copy.edit_permission_mode = source.edit_permission_mode;
        copy.replaceItems(source.items);
        copy.revision = source.revision;
        return copy;
    }

    /** What the server does with an upload: merges the uploaded groups using conflict handling so simultaneous uploads don't overwrite unrelated fields or items. */
    private static void upload(PillarParticleConfig server, PillarParticleConfig client, Set<ConfigSetting> groups) {
        server.applyUpload(client, groups);
    }

    @Test
    public void twoClientsEditingDifferentSettingsDuringTheWaitKeepBothEdits() {
        PillarParticleConfig server = new PillarParticleConfig();
        PillarParticleConfig clientA = copyOf(server);
        PillarParticleConfig clientB = copyOf(server);
        ConfigUploadBatch batchA = new ConfigUploadBatch();
        batchA.rebase(clientA);

        // Player A raises the particle speed and waits for the upload.
        clientA.particle_speed = 0.4D;
        batchA.edited(ConfigSetting.PARTICLES, clientA, 1000L);

        // Player B saves colours; the server takes them and broadcasts its state to A.
        clientB.particle_color = new ArrayList<>(List.of("#112233", "#445566"));
        clientB.max_particle_color = 2;
        upload(server, clientB, Set.of(ConfigSetting.COLORS));
        PillarParticleConfig incomingForA = copyOf(server);
        batchA.reapply(incomingForA);

        // A's screen shows the merge: the broadcast colours and A's own pending speed.
        assertEquals(0.4D, incomingForA.particle_speed, 0.0D);
        assertEquals(List.of("#112233", "#445566"), incomingForA.particle_color);
        assertEquals(2, incomingForA.max_particle_color);

        // When A's wait runs out, only the particle group goes out, and it carries A's speed.
        assertFalse(batchA.isDue(1000L + ConfigUploadBatch.DELAY_MS - 1));
        assertTrue(batchA.isDue(1000L + ConfigUploadBatch.DELAY_MS));
        Set<ConfigSetting> groups = batchA.take();
        assertEquals(Set.of(ConfigSetting.PARTICLES), groups);
        upload(server, incomingForA, groups);

        // The server now holds both edits.
        assertEquals(0.4D, server.particle_speed, 0.0D);
        assertEquals(List.of("#112233", "#445566"), server.particle_color);
    }

    @Test
    public void differentFieldsOfTheSameGroupBothSurviveOnTheClientAndInTheUpload() {
        PillarParticleConfig server = new PillarParticleConfig();
        PillarParticleConfig clientA = copyOf(server);
        PillarParticleConfig clientB = copyOf(server);
        ConfigUploadBatch batchA = new ConfigUploadBatch();
        batchA.rebase(clientA);

        clientA.particle_speed = 0.4D;
        batchA.edited(ConfigSetting.PARTICLES, clientA, 1000L);
        // B changes the lifetime (the same PARTICLES group) and its save reaches the server first.
        clientB.particle_lifetime = 90;
        upload(server, clientB, Set.of(ConfigSetting.PARTICLES));
        PillarParticleConfig incomingForA = copyOf(server);
        batchA.reapply(incomingForA);

        assertEquals(0.4D, incomingForA.particle_speed, 0.0D);
        assertEquals(90, incomingForA.particle_lifetime);
        upload(server, incomingForA, batchA.take());
        assertEquals(0.4D, server.particle_speed, 0.0D);
        assertEquals(90, server.particle_lifetime);
    }

    @Test
    public void bothPlayersAddingDifferentItemsKeepsBothAndTheLocalEditWinsOnTheSameField() {
        PillarParticleConfig server = new PillarParticleConfig();
        server.replaceItems(List.of("minecraft:stone", "minecraft:dirt"));
        PillarParticleConfig clientA = copyOf(server);
        ConfigUploadBatch batchA = new ConfigUploadBatch();
        batchA.rebase(clientA);

        clientA.items.add("minecraft:diamond");
        batchA.edited(ConfigSetting.ITEMS, clientA, 1000L);
        // B adds emerald and the server broadcasts it.
        server.replaceItems(List.of("minecraft:stone", "minecraft:dirt", "minecraft:emerald"));
        PillarParticleConfig incomingForA = copyOf(server);
        batchA.reapply(incomingForA);

        assertEquals(Set.of("minecraft:stone", "minecraft:dirt", "minecraft:emerald", "minecraft:diamond"),
                incomingForA.items);
        upload(server, incomingForA, batchA.take());
        assertEquals(incomingForA.items, server.items);

        // Same field: A's pending lifetime beats the broadcast one.
        PillarParticleConfig other = new PillarParticleConfig();
        ConfigUploadBatch batch = new ConfigUploadBatch();
        batch.rebase(other);
        other.particle_lifetime = 30;
        batch.edited(ConfigSetting.PARTICLES, other, 1L);
        PillarParticleConfig incoming = new PillarParticleConfig();
        incoming.particle_lifetime = 60;
        batch.reapply(incoming);
        assertEquals(30, incoming.particle_lifetime);
    }

    @Test
    public void simultaneousUploadsToDifferentFieldsInSameGroupDoNotOverwrite() {
        PillarParticleConfig server = new PillarParticleConfig();
        PillarParticleConfig clientA = copyOf(server);
        PillarParticleConfig clientB = copyOf(server);
        ConfigUploadBatch batchA = new ConfigUploadBatch();
        batchA.rebase(clientA);
        ConfigUploadBatch batchB = new ConfigUploadBatch();
        batchB.rebase(clientB);

        // A submits particle speed 0.40; B submits lifetime 90.
        clientA.particle_speed = 0.4D;
        batchA.edited(ConfigSetting.PARTICLES, clientA, 1000L);

        clientB.particle_lifetime = 90;
        batchB.edited(ConfigSetting.PARTICLES, clientB, 1000L);

        // Both uploads constructed before either client receives the other's update
        Set<ConfigSetting> groupsA = batchA.take();
        Set<ConfigSetting> groupsB = batchB.take();

        upload(server, clientA, groupsA);
        upload(server, clientB, groupsB);

        assertEquals(0.4D, server.particle_speed, 0.0D);
        assertEquals(90, server.particle_lifetime);
    }

    @Test
    public void simultaneousUploadsAddingDifferentItemsDoNotOverwrite() {
        PillarParticleConfig server = new PillarParticleConfig();
        server.replaceItems(List.of("minecraft:stone"));
        PillarParticleConfig clientA = copyOf(server);
        PillarParticleConfig clientB = copyOf(server);
        ConfigUploadBatch batchA = new ConfigUploadBatch();
        batchA.rebase(clientA);
        ConfigUploadBatch batchB = new ConfigUploadBatch();
        batchB.rebase(clientB);

        // A adds diamond; B adds dirt.
        clientA.items.add("minecraft:diamond");
        batchA.edited(ConfigSetting.ITEMS, clientA, 1000L);

        clientB.items.add("minecraft:dirt");
        batchB.edited(ConfigSetting.ITEMS, clientB, 1000L);

        // Both uploads constructed before either client receives the other's update
        Set<ConfigSetting> groupsA = batchA.take();
        Set<ConfigSetting> groupsB = batchB.take();

        upload(server, clientA, groupsA);
        upload(server, clientB, groupsB);

        assertEquals(Set.of("minecraft:stone", "minecraft:diamond", "minecraft:dirt"), server.items);
    }

    @Test
    public void anItemRemovedLocallyStaysRemovedAndAnItemTheOtherPlayerAddedStays() {
        PillarParticleConfig server = new PillarParticleConfig();
        server.replaceItems(List.of("minecraft:stone", "minecraft:dirt", "minecraft:sand"));
        PillarParticleConfig clientA = copyOf(server);
        ConfigUploadBatch batchA = new ConfigUploadBatch();
        batchA.rebase(clientA);

        clientA.items.remove("minecraft:dirt");
        batchA.edited(ConfigSetting.ITEMS, clientA, 1000L);
        // B touched other items only: removed sand, added emerald.
        server.replaceItems(List.of("minecraft:stone", "minecraft:dirt", "minecraft:emerald"));
        PillarParticleConfig incomingForA = copyOf(server);
        batchA.reapply(incomingForA);

        assertEquals(Set.of("minecraft:stone", "minecraft:emerald"), incomingForA.items);
    }

    @Test
    public void colourSlotsAreMergedPerSlot() {
        PillarParticleConfig server = new PillarParticleConfig();
        server.particle_color = new ArrayList<>(List.of("#000001", "#000002", "#000003"));
        PillarParticleConfig clientA = copyOf(server);
        ConfigUploadBatch batchA = new ConfigUploadBatch();
        batchA.rebase(clientA);

        clientA.particle_color.set(0, "#AAAAAA");
        batchA.edited(ConfigSetting.COLORS, clientA, 1000L);
        server.particle_color = new ArrayList<>(List.of("#000001", "#BBBBBB", "#000003"));
        PillarParticleConfig incomingForA = copyOf(server);
        batchA.reapply(incomingForA);

        assertEquals(List.of("#AAAAAA", "#BBBBBB", "#000003"), incomingForA.particle_color);
    }

    @Test
    public void everyGroupSurvivesAServerSnapshot() {
        PillarParticleConfig local = new PillarParticleConfig();
        ConfigUploadBatch batch = new ConfigUploadBatch();
        batch.rebase(local);
        local.items.add("minecraft:stone");
        batch.edited(ConfigSetting.ITEMS, local, 1L);
        local.particle_color = new ArrayList<>(List.of("#ABCDEF"));
        local.max_particle_color = 1;
        batch.edited(ConfigSetting.COLORS, local, 2L);
        local.edit_permission_mode = PillarParticleConfig.EditPermissionMode.values()[
                (local.edit_permission_mode.ordinal() + 1) % PillarParticleConfig.EditPermissionMode.values().length];
        batch.edited(ConfigSetting.POLICY, local, 3L);
        // A stored value outside the screen's range is carried as it is.
        local.particle_speed = 4.0D;
        local.particle_lifetime = 500;
        local.use_pattern = !local.use_pattern;
        local.pattern = "spiral";
        batch.edited(ConfigSetting.PARTICLES, local, 4L);

        PillarParticleConfig incoming = new PillarParticleConfig();
        batch.reapply(incoming);
        assertEquals(Set.of("minecraft:stone"), incoming.items);
        assertEquals(List.of("#ABCDEF"), incoming.particle_color);
        assertEquals(1, incoming.max_particle_color);
        assertEquals(local.edit_permission_mode, incoming.edit_permission_mode);
        assertEquals(4.0D, incoming.particle_speed, 0.0D);
        assertEquals(500, incoming.particle_lifetime);
        assertEquals(local.use_pattern, incoming.use_pattern);
        assertEquals("spiral", incoming.pattern);
    }

    @Test
    public void editsThatCanNoLongerBeUploadedAreRevertedToTheServerValues() {
        PillarParticleConfig local = new PillarParticleConfig();
        local.particle_speed = 0.06D;
        ConfigUploadBatch batch = new ConfigUploadBatch();
        batch.rebase(local);
        local.particle_speed = 0.07D;
        local.particle_lifetime = local.particle_lifetime + 5;
        batch.edited(ConfigSetting.PARTICLES, local, 1L);
        local.max_particle_color = local.max_particle_color + 1;
        batch.edited(ConfigSetting.COLORS, local, 2L);
        int maxColors = local.max_particle_color;

        // Access to particles was lost during the wait: they go back to what the server has, colours stay pending.
        batch.revert(ConfigSetting.PARTICLES, local);
        assertEquals(0.06D, local.particle_speed, 0.0D);
        assertEquals(new PillarParticleConfig().particle_lifetime, local.particle_lifetime);
        assertEquals(maxColors, local.max_particle_color);
        assertEquals(Set.of(ConfigSetting.COLORS), batch.pendingSettings());
    }

    @Test
    public void aRefusedGroupIsRestoredSoTheServerStateShowsAndOthersStay() {
        PillarParticleConfig local = new PillarParticleConfig();
        ConfigUploadBatch batch = new ConfigUploadBatch();
        batch.rebase(local);
        local.particle_speed = 0.4D;
        batch.edited(ConfigSetting.PARTICLES, local, 1L);
        sendNow(batch, local);
        local.max_particle_color = 5;
        batch.edited(ConfigSetting.COLORS, local, 3L);

        batch.refuse(ConfigSetting.PARTICLES, local);
        assertEquals(new PillarParticleConfig().particle_speed, local.particle_speed, 0.0D);
        PillarParticleConfig incoming = new PillarParticleConfig();
        batch.reapply(incoming);
        assertEquals(5, incoming.max_particle_color);
        assertEquals(Set.of(ConfigSetting.COLORS), batch.take());
    }

    /** The upload goes out: the groups leave the batch and later edits are measured against the sent values. */
    private static Set<ConfigSetting> sendNow(ConfigUploadBatch batch, PillarParticleConfig local) {
        Set<ConfigSetting> groups = batch.take();
        batch.measureFrom(local);
        return groups;
    }

    @Test
    public void aRefusalWithNoNewerEditsRestoresEveryRefusedFieldAndLeavesNothingPending() {
        PillarParticleConfig local = new PillarParticleConfig();
        ConfigUploadBatch batch = new ConfigUploadBatch();
        batch.rebase(local);
        double serverSpeed = local.particle_speed;
        int serverMax = local.max_particle_color;
        local.particle_speed = 0.4D;
        batch.edited(ConfigSetting.PARTICLES, local, 1L);
        local.max_particle_color = serverMax + 1;
        batch.edited(ConfigSetting.COLORS, local, 2L);
        assertEquals(Set.of(ConfigSetting.PARTICLES, ConfigSetting.COLORS), sendNow(batch, local));

        // Refusal first, no snapshot: the displayed values go back to the server's.
        batch.refuse(null, local);
        assertEquals(serverSpeed, local.particle_speed, 0.0D);
        assertEquals(serverMax, local.max_particle_color);
        assertFalse(batch.hasPending());
        assertTrue(batch.inFlightSettings().isEmpty());
    }

    @Test
    public void aRefusalNamingOneGroupLeavesTheOtherGroupsUploadAlone() {
        PillarParticleConfig local = new PillarParticleConfig();
        ConfigUploadBatch batch = new ConfigUploadBatch();
        batch.rebase(local);
        double serverSpeed = local.particle_speed;
        local.particle_speed = 0.4D;
        batch.edited(ConfigSetting.PARTICLES, local, 1L);
        local.max_particle_color = local.max_particle_color + 1;
        batch.edited(ConfigSetting.COLORS, local, 2L);
        int sentMax = local.max_particle_color;
        sendNow(batch, local);

        batch.refuse(ConfigSetting.PARTICLES, local);
        assertEquals(serverSpeed, local.particle_speed, 0.0D);
        assertEquals(sentMax, local.max_particle_color);
        assertEquals(Set.of(ConfigSetting.COLORS), batch.inFlightSettings());
    }

    @Test
    public void aNewerEditToTheRefusedFieldSurvivesTheSnapshotAndThenTheRefusal() {
        PillarParticleConfig local = new PillarParticleConfig();
        ConfigUploadBatch batch = new ConfigUploadBatch();
        batch.rebase(local);
        PillarParticleConfig server = copyOf(local);
        local.particle_speed = 0.40D;
        batch.edited(ConfigSetting.PARTICLES, local, 1L);
        sendNow(batch, local);
        // Another edit of the same field before the answer arrives.
        local.particle_speed = 0.45D;
        batch.edited(ConfigSetting.PARTICLES, local, 2L);

        // The server sends its state, then the refusal for PARTICLES.
        PillarParticleConfig snapshot = copyOf(server);
        batch.reapply(snapshot);
        batch.refuse(ConfigSetting.PARTICLES, snapshot);

        assertEquals(0.45D, snapshot.particle_speed, 0.0D);
        assertEquals(Set.of(ConfigSetting.PARTICLES), batch.pendingSettings());
        assertTrue(batch.isDue(2L + ConfigUploadBatch.DELAY_MS));
        assertEquals(Set.of(ConfigSetting.PARTICLES), batch.take());
    }

    @Test
    public void aNewerEditToAnotherGroupKeepsItWhileAGeneralRefusalRestoresTheRefusedField() {
        PillarParticleConfig local = new PillarParticleConfig();
        ConfigUploadBatch batch = new ConfigUploadBatch();
        batch.rebase(local);
        PillarParticleConfig server = copyOf(local);
        local.particle_speed = 0.40D;
        batch.edited(ConfigSetting.PARTICLES, local, 1L);
        sendNow(batch, local);
        int newMax = local.max_particle_color + 2;
        local.max_particle_color = newMax;
        batch.edited(ConfigSetting.COLORS, local, 2L);

        // The refusal names no setting.
        PillarParticleConfig snapshot = copyOf(server);
        batch.reapply(snapshot);
        batch.refuse(null, snapshot);

        assertEquals(server.particle_speed, snapshot.particle_speed, 0.0D);
        assertEquals(newMax, snapshot.max_particle_color);
        assertEquals(Set.of(ConfigSetting.COLORS), batch.pendingSettings());
    }

    @Test
    public void aRefusalBeforeTheSnapshotGivesTheSameFinalScreen() {
        PillarParticleConfig local = new PillarParticleConfig();
        ConfigUploadBatch batch = new ConfigUploadBatch();
        batch.rebase(local);
        PillarParticleConfig server = copyOf(local);
        local.particle_speed = 0.40D;
        local.particle_lifetime = local.particle_lifetime + 7;
        batch.edited(ConfigSetting.PARTICLES, local, 1L);
        sendNow(batch, local);
        local.particle_speed = 0.45D;
        batch.edited(ConfigSetting.PARTICLES, local, 2L);
        int newMax = local.max_particle_color + 2;
        local.max_particle_color = newMax;
        batch.edited(ConfigSetting.COLORS, local, 3L);

        // Refusal first: the refused field without a newer edit is back, the newer ones stay.
        batch.refuse(null, local);
        assertEquals(0.45D, local.particle_speed, 0.0D);
        assertEquals(server.particle_lifetime, local.particle_lifetime);
        assertEquals(newMax, local.max_particle_color);
        // Then the snapshot.
        PillarParticleConfig snapshot = copyOf(server);
        batch.reapply(snapshot);
        assertEquals(0.45D, snapshot.particle_speed, 0.0D);
        assertEquals(server.particle_lifetime, snapshot.particle_lifetime);
        assertEquals(newMax, snapshot.max_particle_color);
        assertEquals(Set.of(ConfigSetting.PARTICLES, ConfigSetting.COLORS), batch.pendingSettings());
    }

    @Test
    public void anAcceptedUploadIsConfirmedBySnapshotAndNothingIsRestored() {
        PillarParticleConfig local = new PillarParticleConfig();
        ConfigUploadBatch batch = new ConfigUploadBatch();
        batch.rebase(local);
        PillarParticleConfig server = copyOf(local);
        local.particle_speed = 0.40D;
        batch.edited(ConfigSetting.PARTICLES, local, 1L);
        sendNow(batch, local);
        assertEquals(Set.of(ConfigSetting.PARTICLES), batch.inFlightSettings());
        upload(server, local, Set.of(ConfigSetting.PARTICLES));

        PillarParticleConfig snapshot = copyOf(server);
        batch.reapply(snapshot);
        assertEquals(0.40D, snapshot.particle_speed, 0.0D);
        assertTrue(batch.inFlightSettings().isEmpty());
        assertFalse(batch.hasPending());
        // A refusal of something else later does not undo the accepted value.
        batch.refuse(null, snapshot);
        assertEquals(0.40D, snapshot.particle_speed, 0.0D);
    }

    @Test
    public void refusedColourSlotsAndItemsReturnToTheServerStateExceptNewerEdits() {
        PillarParticleConfig local = new PillarParticleConfig();
        local.particle_color = new ArrayList<>(List.of("#000001", "#000002", "#000003"));
        local.replaceItems(List.of("minecraft:stone", "minecraft:dirt"));
        ConfigUploadBatch batch = new ConfigUploadBatch();
        batch.rebase(local);
        local.particle_color.set(0, "#AAAAAA");
        local.particle_color.set(1, "#BBBBBB");
        batch.edited(ConfigSetting.COLORS, local, 1L);
        local.items.add("minecraft:diamond");
        local.items.remove("minecraft:dirt");
        batch.edited(ConfigSetting.ITEMS, local, 1L);
        sendNow(batch, local);
        // Slot 1 is edited again before the answer.
        local.particle_color.set(1, "#CCCCCC");
        batch.edited(ConfigSetting.COLORS, local, 2L);

        batch.refuse(null, local);
        assertEquals(List.of("#000001", "#CCCCCC", "#000003"), local.particle_color);
        assertEquals(Set.of("minecraft:stone", "minecraft:dirt"), local.items);
        assertEquals(Set.of(ConfigSetting.COLORS), batch.pendingSettings());
    }

    @Test
    public void manyPillarCounterClicksBecomeOneSendWithTheLastValues() {
        PillarUploadBatch<String> batch = new PillarUploadBatch<>();
        batch.edited("S-P1", "speed 0.1", 1000L);
        batch.edited("S-P1", "speed 0.2", 1500L);
        batch.edited("S-P1", "speed 0.3", 2000L);
        // Each click restarts the wait.
        assertTrue(batch.takeDue(2000L + PillarUploadBatch.DELAY_MS - 1).isEmpty());
        assertEquals(List.of("speed 0.3"), batch.takeDue(2000L + PillarUploadBatch.DELAY_MS));
        assertFalse(batch.hasPending());
        assertTrue(batch.takeDue(99_000L).isEmpty());
        assertEquals(3000L, PillarUploadBatch.DELAY_MS);
    }

    @Test
    public void pillarsAreCoalescedSeparatelyAndOnePillarsWaitDoesNotHoldBackAnother() {
        PillarUploadBatch<String> batch = new PillarUploadBatch<>();
        batch.edited("S-P1", "p1 a", 1000L);
        batch.edited("S-P2", "p2 a", 3000L);
        batch.edited("S-P2", "p2 b", 3500L);
        assertEquals(List.of("p1 a"), batch.takeDue(1000L + PillarUploadBatch.DELAY_MS));
        assertTrue(batch.hasPending());
        assertEquals(List.of("p2 b"), batch.takeDue(3500L + PillarUploadBatch.DELAY_MS));
    }

    @Test
    public void closingSendsEverythingPendingAtOnce() {
        PillarUploadBatch<String> batch = new PillarUploadBatch<>();
        batch.edited("S-P1", "p1 last", 10L);
        batch.edited("S-P2", "p2 last", 20L);
        assertEquals(List.of("p1 last", "p2 last"), batch.takeAll());
        assertFalse(batch.hasPending());
        assertTrue(batch.takeAll().isEmpty());
    }

    @Test
    public void switchingToAnotherPillarFlushesThePreviousOneOnly() {
        PillarUploadBatch<String> batch = new PillarUploadBatch<>();
        batch.edited("S-P1", "p1 last", 10L);
        batch.edited("S-P1", "p1 newest", 20L);
        // Another pillar is selected: the old one goes out now, nothing is sent for the selected one.
        assertEquals(List.of("p1 newest"), batch.takeAllExcept("S-P2"));
        assertFalse(batch.hasPending());

        batch.edited("S-P2", "p2 a", 30L);
        assertTrue(batch.takeAllExcept("S-P2").isEmpty());
        assertTrue(batch.hasPending());
        // The Save Pillar button sends that pillar's entry itself.
        assertEquals("p2 a", batch.take("S-P2"));
        assertNull(batch.take("S-P2"));
    }
}
